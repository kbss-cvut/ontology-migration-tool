package cz.cvut.fel.repository;


import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.http.HTTPRepository;

import java.io.IOException;

public class RepositoryFactory {

    public static OntologyRepository createRepository(String type, String endpoint,
                                                      String user, String password) throws IOException {
        switch (type.toLowerCase()) {
            case "rdf4j":
                return new Rdf4jRepository(endpoint, user, password);
            default:
                throw new IllegalArgumentException("Unknown repository type: " + type);
        }

    }

}
