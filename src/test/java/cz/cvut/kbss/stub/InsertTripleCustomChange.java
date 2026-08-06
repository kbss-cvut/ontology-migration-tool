package cz.cvut.kbss.stub;

import cz.cvut.kbss.model.change.custom.CustomChange;
import cz.cvut.kbss.repository.OntologyRepository;

/**
 * A {@link CustomChange} implementation that inserts a fixed triple into the repository.
 * <p>
 * Used to verify that {@link cz.cvut.kbss.model.change.CustomChangeApplier} correctly instantiates a custom
 * change and invokes it against a real repository.
 */
public class InsertTripleCustomChange implements CustomChange {

    public static final String SUBJECT = "http://ex/customChangeSubject";
    public static final String PREDICATE = "http://ex/customChangePredicate";
    public static final String OBJECT = "http://ex/customChangeObject";

    @Override
    public void apply(OntologyRepository repository) {
        repository.update(String.format("INSERT DATA { <%s> <%s> <%s> . }", SUBJECT, PREDICATE, OBJECT));
    }
}
