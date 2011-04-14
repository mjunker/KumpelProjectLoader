package ch.kumpel.builder;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

public class RepositoryBuilder {

    public static SVNRepository createRepository(KumpelProject config) {

        setupLibrary();

        SVNRepository repository = null;
        try {
            /*
                * Creates an instance of SVNRepository to work with the repository.
                * All user's requests to the repository are relative to the
                * repository location used to create this SVNRepository. SVNURL is
                * a wrapper for URL strings that refer to repository locations.
                */
            repository = SVNRepositoryFactory.create(SVNURL
                    .parseURIEncoded(config.getUrl()));
        } catch (SVNException svne) {
            /*
                * Perhaps a malformed URL is the cause of this exception
                */
            System.err
                    .println("error while creating an SVNRepository for location '"
                            + config.getUrl() + "': " + svne.getMessage());

        }
        repository.setAuthenticationManager(config.createAuthenticationManager());


        return repository;

    }

    private static void setupLibrary() {
        /*
           * For using over http:// and https://
           */
        DAVRepositoryFactory.setup();
        /*
           * For using over svn:// and svn+xxx://
           */
        SVNRepositoryFactoryImpl.setup();

        /*
           * For using over file:///
           */
        FSRepositoryFactory.setup();
    }

}
