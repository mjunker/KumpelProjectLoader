package ch.kumpel.builder;

import org.apache.commons.io.IOUtils;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.*;
import java.util.*;

public class DiffBuilder extends AbstractBuilder {

    private static final int NUMBER_OF_THREADS = 40;
    private Map<Integer, OutputStream> printStreams = new HashMap<Integer, OutputStream>();

    private KumpelProject kumpelProject;
    private Stack<DiffJob> stack;

    public void buildDiffs(KumpelProject project) throws SVNException,
            IOException {
        this.kumpelProject = project;
        final SVNURL url = SVNURL.parseURIDecoded(kumpelProject.getUrl());
        printf("Step 4/4: Loading File Diffs...\n");

        buildInitialDiff(url, kumpelProject);


        initializeStack();
        List<Thread> threads = new ArrayList<Thread>();

        long start = System.currentTimeMillis();


        for (int i = 1; i < NUMBER_OF_THREADS; i++) {
            final PrintStream printStreamForThread = new PrintStream(kumpelProject.createPath("diff" + i), "UTF-8");
            printStreams.put(Integer.valueOf(i), printStreamForThread);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    SVNClientManager manager = SVNClientManager.newInstance();
                    manager.setAuthenticationManager(kumpelProject.createAuthenticationManager());

                    final SVNDiffClient diff = manager.getDiffClient();

                    DiffJob diffJob;
                    while ((diffJob = getNextDiffJob()) != null)

                        try {
                            printf("          Thread %s : Loading Diff %s\n", Thread.currentThread().getName(), diffJob.endRev);
                            diff.doDiff(url, SVNRevision.create(diffJob.startRev), url, SVNRevision
                                    .create(diffJob.endRev), SVNDepth.INFINITY, true,
                                    printStreamForThread);
                        } catch (SVNException e) {
                            e.printStackTrace();
                        }
                }

            }, String.valueOf(i));
            threads.add(thread);
            thread.start();


        }

        waitUntilAllThreadsFinished(threads);

        closeAllStreams();

        mergeFiles();

        System.out.println("took miliseconds to load diffs: " + (System.currentTimeMillis() - start));

    }

    private void mergeFiles() throws IOException {
        PrintStream mainDiff = new PrintStream(kumpelProject.createPath("diff"), "UTF-8");

        try {
            for (Integer fileNumber : printStreams.keySet()) {
                IOUtils.copy(new FileReader(kumpelProject.createPath("diff" + fileNumber)), mainDiff);
            }
        } finally {
            mainDiff.close();
        }

    }

    private void closeAllStreams() throws IOException {
        for (OutputStream outputStream : printStreams.values()) {
            outputStream.close();
        }
    }

    private void waitUntilAllThreadsFinished(List<Thread> threads) {
        boolean finished = false;

        while (!finished) {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // NOOP
            }
            boolean currentlyFinished = true;
            for (Thread thread : threads) {
                currentlyFinished = currentlyFinished && !thread.isAlive();
            }
            finished = currentlyFinished;

        }
    }


    private DiffJob getNextDiffJob() {
        synchronized (this) {

            if (stack.isEmpty()) {
                return null;
            } else {
                return stack.pop();
            }

        }
    }

    private void initializeStack() {
        stack = new Stack<DiffJob>();
        for (int i = 1; i < kumpelProject.getVersions().size(); i++) {
            Long fromVersion = kumpelProject.getVersions().get(i) - 1;
            Long toVersion = kumpelProject.getVersions().get(i);
            stack.push(new DiffJob(fromVersion, toVersion));

        }
    }

    private class DiffJob {
        private Long startRev;
        private Long endRev;

        private DiffJob(Long startRev, Long endRev) {
            this.startRev = startRev;
            this.endRev = endRev;
        }
    }

    private void buildInitialDiff(SVNURL url,
                                  KumpelProject config) throws SVNException, FileNotFoundException, UnsupportedEncodingException {

        SVNClientManager manager = SVNClientManager.newInstance();
        manager.setAuthenticationManager(kumpelProject.createAuthenticationManager());

        SVNDiffClient diff = manager.getDiffClient();

        SVNURL currentUrl = url;
        boolean done = false;
        while (!done) {
            try {
                diff.doDiff(currentUrl, SVNRevision.create(config
                        .getStartRevision() - 1), currentUrl, SVNRevision
                        .create(config.getStartRevision()), SVNDepth.INFINITY,
                        true, new PrintStream(kumpelProject.createPath("initial_diff"),
                                "UTF-8"));
                config.setInitialDiffPath(currentUrl);
                done = true;
                printf("Initial Diff Path=%s", currentUrl);

            } catch (SVNException e) {
                // if the path didn't exist at this revision, we try to load again from the parent dir
                if (e.getMessage().contains("was not found in the repository at revision")) {
                    currentUrl = currentUrl.removePathTail();
                } else {
                    throw e;
                }

            }
        }

    }


}
