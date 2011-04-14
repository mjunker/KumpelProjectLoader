package ch.kumpel.builder;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class KumpelProjectPrinter {

    private static final String UTF_8 = "UTF-8";

    public void print(KumpelProject config) throws FileNotFoundException,
            UnsupportedEncodingException {
        PrintStream printStream = new PrintStream(config.createPath("list"),
                UTF_8);
        for (SVNLogEntryPath entry : config.getListEntries()) {
            printHistory(printStream, entry);
        }
        printProjectInfo(config, new PrintStream(config.createPath("info"),
                UTF_8));

        printLog(new PrintStream(config.createPath("log"), UTF_8), config
                .getLogEntries());
    }

    public static void printHistory(PrintStream printStream,
                                    SVNLogEntryPath listEntry) {

        printStream
                .printf(
                        "pathString=\"%s\" nodeKind=%s copyFromPath=\"%s\" copyFromRevision=%s\n",
                        listEntry.getPath(), listEntry.getKind(), listEntry
                        .getCopyPath(), listEntry.getCopyRevision());
    }

    public void printLog(PrintStream stream, List<SVNLogEntry> logEntries) {
        for (SVNLogEntry logEntry : logEntries) {
            stream.println("=========================================");
            stream.printf("author=%s\nrevision=%s\ndate=%s\nmessage=%s\n",
                    logEntry.getAuthor(), logEntry.getRevision(),
                    new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(logEntry
                            .getDate()), logEntry.getMessage());
            stream.println("-----------------------------------------");
            Map<String, SVNLogEntryPath> map = logEntry.getChangedPaths();
            for (Map.Entry<String, SVNLogEntryPath> entry : map.entrySet()) {
                SVNLogEntryPath path = entry.getValue();
                stream.printf("action=%c ", path.getType());

                printHistory(stream, path);
            }
        }

    }

    public void printProjectInfo(KumpelProject config, PrintStream stream) {
        stream.printf("url=%s\n" + //
                "startRev=%s\n" + //
                "endRev=%s\nlogin=%s\n" + //
                "password=%s\n" + //
                "reposRoot=%s\n" + //
                "projectName=%s\n" + //
                "relativePath=%s\n" + //
                "nodeKind=%s\n" + //
                "initialDiffPathPrefix=%s\n", //
                config.getUrl(), config.getStartRevision(), config
                .getEndRevision(), config.getUserName(), config
                .getPassword(), config.getRepositoryRoot(), config
                .getProjectName(), config.getRelativeBasePath(), config
                .getRootNodeKind(), config.getInitialDiffPath());
    }

}
