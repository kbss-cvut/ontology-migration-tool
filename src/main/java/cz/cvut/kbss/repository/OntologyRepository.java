package cz.cvut.kbss.repository;

public interface OntologyRepository {

    void begin();

    void commit();

    void close();

    void update(String sparql);

    boolean ask(String sparql);

    void importFromUrl(String url, String context);
}
