package ch.kumpel.builder;

import org.apache.commons.lang.util.Validate;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.io.IOException;
import java.util.List;

public class ProjectInfoBuilder extends AbstractBuilder {

    public void buildProjectInfo(SVNRepository repository, KumpelProject config)
            throws SVNException, IOException {

        SVNDirEntry dirEntry = repository.info("", config.getStartRevision());
        if (dirEntry == null) {
            throw new InvalidPathException();
        } else {
            config.setRelativeBasePath(getRelativeBasePath(config.getUrl(),
                    dirEntry.getRepositoryRoot().toString()));
            config.setRepositoryRoot(dirEntry.getRepositoryRoot());
            config.setRootNodeKind(dirEntry.getKind());
        }

    }

    private String getRelativeBasePath(String url, String reposRoot) {

        List<String> otherPath = PathHelper.splitPath(reposRoot);
        List<String> relativePath = PathHelper.splitPath(url);

        Validate.isTrue(otherPath.size() <= relativePath.size());
        int relativePathSize = relativePath.size();
        for (int i = 0; i < relativePathSize; i++) {
            if (otherPath.size() > 0) {
                Validate.isTrue(otherPath.get(0).equals(relativePath.get(0)));
                otherPath.remove(0);
                relativePath.remove(0);
            }

        }
        return PathHelper.createPath(relativePath);

    }
}
