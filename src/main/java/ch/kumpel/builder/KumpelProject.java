package ch.kumpel.builder;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KumpelProject {

    private Long endRevision;
    private SVNURL initialDiffPath;
    private String password;
    private String projectName;
    private String relativeBasePath;
    private SVNURL repositoryRoot;
    private SVNNodeKind rootNodeKind;
    private Long startRevision;
    private String subprojectName;
    private String url;
    private String userName;
    private String targetDirectory;
    private final List<Long> versions = new ArrayList<Long>();
    private final Map<Long, List<String>> additionalPaths = new HashMap<Long, List<String>>();
    private final List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
    private final List<SVNLogEntryPath> listEntries = new ArrayList<SVNLogEntryPath>();
    private boolean buildDiffs = false;

    public Map<Long, List<String>> getAdditionalPaths() {
        return additionalPaths;
    }

    public File createPath(String fileName) {
        File dir = new File(targetDirectory + "/" + projectName + "/" + subprojectName + "/");
        dir.mkdirs();
        File file = new File(targetDirectory + "/" + projectName + "/" + subprojectName + "/"
                + subprojectName + "." + fileName);

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public Long getEndRevision() {
        return endRevision;
    }

    public SVNURL getInitialDiffPath() {
        return initialDiffPath;
    }

    public String getPassword() {
        return password;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getRelativeBasePath() {
        return relativeBasePath;
    }

    public SVNURL getRepositoryRoot() {
        return repositoryRoot;
    }

    public SVNNodeKind getRootNodeKind() {
        return rootNodeKind;
    }

    public Long getStartRevision() {
        return startRevision;
    }

    public String getSubprojectName() {
        return subprojectName;
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public List<Long> getVersions() {
        return versions;
    }

    public void setEndRevision(Long endRevision) {
        this.endRevision = endRevision;
    }

    public void setInitialDiffPath(SVNURL initialDiffPath) {
        this.initialDiffPath = initialDiffPath;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setRelativeBasePath(String relativeBasePath) {
        this.relativeBasePath = relativeBasePath;
    }

    public void setRepositoryRoot(SVNURL repositoryRoot) {
        this.repositoryRoot = repositoryRoot;
    }

    public void setRootNodeKind(SVNNodeKind rootNodeKind) {
        this.rootNodeKind = rootNodeKind;
    }

    public void setStartRevision(Long startRevision) {
        this.startRevision = startRevision;
    }

    public void setSubprojectName(String subprojectName) {
        this.subprojectName = subprojectName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<SVNLogEntry> getLogEntries() {
        return logEntries;
    }

    public List<SVNLogEntryPath> getListEntries() {
        return listEntries;
    }

    public void setBuildDiffs(boolean buildDiffs) {
        this.buildDiffs = buildDiffs;

    }

    public boolean isBuildDiffs() {
        return buildDiffs;
    }

    public boolean hasLogin() {
        return getUserName() != null;
    }

    public ISVNAuthenticationManager createAuthenticationManager() {
        if (hasLogin()) {
            return SVNWCUtil
                    .createDefaultAuthenticationManager(getUserName(), getPassword());
        } else {
            return null;
        }

    }
}
