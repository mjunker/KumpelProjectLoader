package ch.kumpel.builder;

import org.apache.commons.lang.util.Validate;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNReporter;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class InitialTreeBuilder extends AbstractBuilder {

    public void buildInitialTree(SVNRepository repository,
                                 final KumpelProject config) throws SVNException,
            FileNotFoundException, UnsupportedEncodingException {
        ISVNReporterBaton reporter = new ISVNReporterBaton() {
            public void report(ISVNReporter reporter) throws SVNException {
                reporter.setPath("", null, config.getStartRevision(),
                        SVNDepth.INFINITY, true);

                reporter.finishReport();

            }
        };
        try {
            // run an update-like request which never receives any real file
            // deltas
            repository.status(config.getStartRevision(), "", SVNDepth.INFINITY,
                    reporter, getListEditor(config));
        } catch (SVNException e) {
            Validate.isTrue(e.getMessage().startsWith(
                    "svn: Cannot replace a directory from within"));
        }

    }

    private ISVNEditor getListEditor(final KumpelProject config) {
        return new ISVNEditor() {

            @Override
            public void deleteEntry(String path, long revision)
                    throws SVNException {
                // TODO Auto-generated method stub

            }

            @Override
            public void addDir(String path, String copyFromPath,
                               long copyFromRevision) throws SVNException {

                SVNLogEntryPath listEntry = new SVNLogEntryPath(path, 'A',
                        copyFromPath, copyFromRevision, SVNNodeKind.DIR);

                config.getListEntries().add(listEntry);

            }

            @Override
            public void addFile(String path, String copyFromPath,
                                long copyFromRevision) throws SVNException {
                SVNLogEntryPath listEntry = new SVNLogEntryPath(path, 'A',
                        copyFromPath, copyFromRevision, SVNNodeKind.FILE);
                config.getListEntries().add(listEntry);
            }

            @Override
            public void changeDirProperty(String name, SVNPropertyValue value)
                    throws SVNException {
                // TODO Auto-generated method stub

            }

            @Override
            public void changeFileProperty(String path, String propertyName,
                                           SVNPropertyValue propertyValue) throws SVNException {
                // TODO Auto-generated method stub

            }

            @Override
            public void closeDir() throws SVNException {
                // TODO Auto-generated method stub

            }

            @Override
            public SVNCommitInfo closeEdit() throws SVNException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void closeFile(String path, String textChecksum)
                    throws SVNException {
                // TODO Auto-generated method stub

            }

            @Override
            public void openDir(String path, long revision) throws SVNException {
                // TODO Auto-generated method stub

            }

            @Override
            public void openFile(String path, long revision)
                    throws SVNException {
                // TODO Auto-generated method stub

            }

            @Override
            public void openRoot(long revision) throws SVNException {
                // TODO Auto-generated method stub

            }

            @Override
            public void targetRevision(long revision) throws SVNException {

            }

            @Override
            public OutputStream textDeltaChunk(String path,
                                               SVNDiffWindow diffWindow) throws SVNException {
                return null;
            }

            @Override
            public void textDeltaEnd(String path) throws SVNException {
            }

            @Override
            public void abortEdit() throws SVNException {

            }

            @Override
            public void absentDir(String path) throws SVNException {
                // TODO Auto-generated method stub

            }

            @Override
            public void absentFile(String path) throws SVNException {
                // TODO Auto-generated method stub

            }

            @Override
            public void applyTextDelta(String path, String baseChecksum)
                    throws SVNException {
            }

        };
    }
}
