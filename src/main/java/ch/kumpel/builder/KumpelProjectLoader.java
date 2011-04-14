package ch.kumpel.builder;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class KumpelProjectLoader extends AbstractBuilder {

    private SVNRepository repository;

    public static void main(String[] args) throws SVNException, IOException, InterruptedException {

        if (!(args.length == 6 || args.length == 7 || args.length == 8)) {
            System.out
                    .println("Usage: KumpelProjectLoader <projectName> <target-directory> <url> <startRev> <endRev> <buildDiffs> <username> <password>");
            return;
        }
        String projectName = null, url = null, userName = null, password = null, targetDirecty = null;
        Long startRev = null, endRev = null;
        Boolean buildDiffs = false;

        projectName = args[0];
        targetDirecty = args[1];
        url = args[2];
        startRev = Long.parseLong(args[3]);
        endRev = Long.parseLong(args[4]);
        buildDiffs = Boolean.parseBoolean(args[5]);

        if (args.length == 7) {
            userName = args[6];
            password = "";
        }

        if (args.length == 8) {
            userName = args[6];
            password = args[7];
        }

        new KumpelProjectLoader().build(projectName, targetDirecty, url, startRev, endRev,
                userName, password, buildDiffs);
    }

    public void build(String name, String targetDirectory, String url, Long startRev, Long endRev,
                      String user, String password, boolean buildDiffs)
            throws SVNException, IOException, InterruptedException {

        KumpelProject config = new KumpelProject();
        config.setProjectName(name);
        config.setUrl(url);
        config.setStartRevision(startRev);
        config.setEndRevision(endRev);
        config.setUserName(user);
        config.setPassword(password);
        config.setSubprojectName("main");
        config.setTargetDirectory(targetDirectory);
        config.setBuildDiffs(buildDiffs);
        build(config);
    }

    public void build(KumpelProject config) throws SVNException, IOException {

        this.repository = RepositoryBuilder.createRepository(config);

        printf("Building Project %s %s\n", config.getProjectName(), config
                .getSubprojectName());
        printf("Step 1: Loading Project Info...\n");
        new ProjectInfoBuilder().buildProjectInfo(repository, config);
        if (config.getRootNodeKind() == SVNNodeKind.DIR) {
            printf("Step 2: Loading Initial List of Directory Tree...\n");
            new InitialTreeBuilder().buildInitialTree(repository, config);
            if (!config.getStartRevision().equals(config.getEndRevision())) {

                printf("Step 3/4: Loading Logs...\n");
                new LogBuilder().buildLog(repository, config);
            }

        }
        buildAdditionalHistoryStubs(config);
        if (config.isBuildDiffs()) {
            new DiffBuilder().buildDiffs(config);
        }
        cleanupNodeKind(config);
        new KumpelProjectPrinter().print(config);

    }

    private void cleanupNodeKind(KumpelProject config) throws SVNException {

        for (SVNLogEntry svnLogEntry : config.getLogEntries()) {
            Map<String, SVNLogEntryPath> paths = (Map<String, SVNLogEntryPath>) svnLogEntry.getChangedPaths();
            for (Entry<String, SVNLogEntryPath> svnLogEntryPath : paths.entrySet()) {
                if (SVNNodeKind.UNKNOWN.equals(svnLogEntryPath.getValue().getKind())) {
                    SVNDirEntry info = repository.info(svnLogEntryPath.getValue().getPath(), svnLogEntry.getRevision());

                    if (info != null && info.getKind() != null) {
                        try {
                            Field fieldMyNodeKind = svnLogEntryPath.getValue().getClass().getDeclaredField("myNodeKind");
                            fieldMyNodeKind.setAccessible(true);
                            fieldMyNodeKind.set(svnLogEntryPath.getValue(), info.getKind());
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        }

    }

    private void loadAdditionalHistoryStub(String url, Long revision,
                                           KumpelProject config) throws SVNException, IOException {

        try {
            KumpelProjectLoader loader = new KumpelProjectLoader();
            loader.setVerbose(false);
            KumpelProject additionalHistoryConfig = new KumpelProject();
            additionalHistoryConfig.setProjectName(config.getProjectName());
            additionalHistoryConfig.setTargetDirectory(config.getTargetDirectory());
            additionalHistoryConfig.setSubprojectName(creatSubProjectName(url,
                    revision));
            additionalHistoryConfig.setUrl(config.getRepositoryRoot()
                    .toString()
                    + url);
            additionalHistoryConfig.setStartRevision(revision);
            additionalHistoryConfig.setEndRevision(revision);
            additionalHistoryConfig.setUserName(config.getUserName());
            additionalHistoryConfig.setPassword(config.getPassword());
            loader.build(additionalHistoryConfig);
        } catch (InvalidPathException e) {
            System.out.printf(
                    "Unable to load additional Project: %s  Revision: %s\n",
                    url, revision);
        }
    }

    private String creatSubProjectName(String url, Long revision) {

        return url.replaceAll("[^a-zA-Z0-9]", "") + revision;
    }

    private void buildAdditionalHistoryStubs(KumpelProject config)
            throws SVNException, IOException {

        int processed = 0;
        for (Entry<Long, List<String>> entry : config.getAdditionalPaths()
                .entrySet()) {
            for (String path : entry.getValue()) {
                loadAdditionalHistoryStub(path, entry.getKey(), config);
                processed++;
                printf("%d of %d additional histories loaded.\n", processed,
                        totalNumberOfAdditionalHistories(config
                                .getAdditionalPaths()));
            }
        }
    }

    private int totalNumberOfAdditionalHistories(
            Map<Long, List<String>> additionalPaths) {

        int total = 0;
        for (List<String> paths : additionalPaths.values()) {
            total += paths.size();
        }
        return total;
    }

}
