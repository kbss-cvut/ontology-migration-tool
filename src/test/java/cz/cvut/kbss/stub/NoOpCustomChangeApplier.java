package cz.cvut.kbss.stub;

import cz.cvut.kbss.model.change.custom.CustomChange;
import cz.cvut.kbss.repository.OntologyRepository;

/**
 * Empty implementation for {@link CustomChange}
 */
public class NoOpCustomChangeApplier implements CustomChange {
    @Override
    public void apply(OntologyRepository repository) {
        System.out.println("Applying no-op custom change.");
    }
}
