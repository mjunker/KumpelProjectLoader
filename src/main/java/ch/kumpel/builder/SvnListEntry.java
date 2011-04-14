package ch.kumpel.builder;

import org.tmatesoft.svn.core.SVNNodeKind;

public class SvnListEntry {

    private String path;

    private String copyFromPath;
    private Long copyFromRevision;
    private SVNNodeKind nodeKind;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCopyFromPath() {
        return copyFromPath;
    }

    public void setCopyFromPath(String copyFromPath) {
        this.copyFromPath = copyFromPath;
    }

    public Long getCopyFromRevision() {
        return copyFromRevision;
    }

    public void setCopyFromRevision(Long copyFromRevision) {
        this.copyFromRevision = copyFromRevision;
    }

    public void setNodeKind(SVNNodeKind nodeKind) {
        this.nodeKind = nodeKind;
    }

    public SVNNodeKind getNodeKind() {
        return nodeKind;
    }

}
