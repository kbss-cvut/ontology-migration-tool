package cz.cvut.kbss.repository;

public interface OntologyRepository {
    void begin();
    void update(String sparql);
    void commit();
    boolean ask(String sparql);
    void close();
}
