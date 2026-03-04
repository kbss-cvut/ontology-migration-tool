package cz.cvut.kbss.repository;


public class RepositoryFactory {

    public static OntologyRepository createRepository(String type, String endpoint,
                                                      String user, String password) {
        if ("rdf4j".equalsIgnoreCase(type)) {
            return new Rdf4jRepository(endpoint, user, password);
        }
        throw new IllegalArgumentException("Unknown repository type: " + type);
    }
}
