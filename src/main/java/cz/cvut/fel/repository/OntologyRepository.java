package cz.cvut.fel.repository;

import org.eclipse.rdf4j.repository.RepositoryConnection;

public interface OntologyRepository {
    void begin();
    void update(String sparql);
    void commit();
    boolean ask(String sparql);
}
