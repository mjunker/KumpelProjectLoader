import ch.kumpel.builder.KumpelProjectLoader;
import org.tmatesoft.svn.core.SVNException;

import java.io.IOException;


public class ArgoUmlLoader {


    public static void main(String[] args) throws IOException, SVNException, InterruptedException {

        new KumpelProjectLoader().build(
                "verveinej",
                "./",
                "svn://scm.gforge.inria.fr/svn/verveinej",
                Long.valueOf(1), Long.valueOf(122),
                "", "", true);

    }
}
