package cz.cvut.kbss.model.change.custom;

import cz.cvut.kbss.repository.OntologyRepository;

/**
 * A custom change (Java implementation) to apply during data migration.
 */
public interface CustomChange {

    /**
     * Applies this change.
     *
     * @param repository the repository being migrated
     */
    void apply(OntologyRepository repository);
}
