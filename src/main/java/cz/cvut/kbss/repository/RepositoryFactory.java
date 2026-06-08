package cz.cvut.kbss.repository;


public class RepositoryFactory {

    public static TransactionalRepository createRepository(String endpoint,
                                                           String user, String password) {
        return new Rdf4jRepository(endpoint, user, password);
    }
}
