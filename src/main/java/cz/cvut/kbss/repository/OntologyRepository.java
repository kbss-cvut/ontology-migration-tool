package cz.cvut.kbss.repository;

public interface OntologyRepository {

    void begin();

    void commit();

    void close();

    void update(String sparql);

    boolean ask(String sparql);

    void clearGraph(String graph);

    void importFromUrl(String url, String graph);
}
