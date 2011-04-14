package ch.kumpel.builder;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogBuilder extends AbstractBuilder {

    public void buildLog(SVNRepository repository, KumpelProject config)
            throws SVNException, FileNotFoundException,
            UnsupportedEncodingException {

        repository.log(null, config.getStartRevision(),
                config.getEndRevision(), true, true, 0,
                createLogHandler(config));

    }

    private void checkAdditionHistoryStubRequired(
            SVNLogEntryPath svnLogEntryPath, KumpelProject config) {
        List<String> copyPath = PathHelper.splitPath(svnLogEntryPath
                .getCopyPath());
        List<String> nodePath = PathHelper.splitPath(svnLogEntryPath.getPath());
        List<String> basePath = PathHelper.splitPath(config
                .getRelativeBasePath());
        while (!copyPath.isEmpty()) {
            if (basePath.isEmpty()) {
                break;
            }
            if (copyPath.get(0).equals(basePath.get(0))) {
                basePath.remove(0);
            }
            if (!copyPath.get(0).equals(nodePath.get(0)) && !basePath.isEmpty()) {
                if (!config.getAdditionalPaths().containsKey(
                        svnLogEntryPath.getCopyRevision())) {
                    config.getAdditionalPaths().put(
                            svnLogEntryPath.getCopyRevision(),
                            new ArrayList<String>());
                }
                config.getAdditionalPaths().get(
                        svnLogEntryPath.getCopyRevision()).add(
                        svnLogEntryPath.getCopyPath());
                break;
            }
            nodePath.remove(0);
            copyPath.remove(0);

        }

    }

    private ISVNLogEntryHandler createLogHandler(final KumpelProject config) {
        return new ISVNLogEntryHandler() {

            @Override
            public void handleLogEntry(SVNLogEntry logEntry)
                    throws SVNException {

                config.getVersions().add(logEntry.getRevision());
                config.getLogEntries().add(logEntry);
                Map<String, SVNLogEntryPath> map = logEntry.getChangedPaths();
                for (Map.Entry<String, SVNLogEntryPath> entry : map.entrySet()) {
                    SVNLogEntryPath path = entry.getValue();

                    if (path.getCopyPath() != null) {
                        checkAdditionHistoryStubRequired(path, config);
                    }

                }
            }

        };

    }

}
