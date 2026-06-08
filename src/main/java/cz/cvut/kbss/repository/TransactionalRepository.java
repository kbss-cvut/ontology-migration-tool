package cz.cvut.kbss.repository;

public interface TransactionalRepository extends OntologyRepository {

    void begin();

    void commit();

    void close();
}
