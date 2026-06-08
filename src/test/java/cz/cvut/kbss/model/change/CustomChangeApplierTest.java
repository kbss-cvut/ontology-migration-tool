package cz.cvut.kbss.model.change;

import cz.cvut.kbss.exception.InvalidCustomChangeException;
import cz.cvut.kbss.model.change.custom.CustomChange;
import cz.cvut.kbss.repository.OntologyRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomChangeApplierTest {

    @Test
    void applyInstantiatesCustomChangeAndCallsApply() {
        TestCustomChange.applied = false;
        CustomChangeApplier applier = new CustomChangeApplier("cz.cvut.kbss.model.change.CustomChangeApplierTest$TestCustomChange");
        applier.apply(null);
        assertTrue(TestCustomChange.applied);
    }

    public static class TestCustomChange implements CustomChange {

        private static boolean applied;

        @Override
        public void apply(OntologyRepository repository) {
            applied = true;
        }
    }

    @Test
    void applyThrowsInvalidCustomChangeExceptionWhenCustomChangeClassIsNotFound() {
        CustomChangeApplier applier = new CustomChangeApplier("non.existing.class");
        assertThrows(InvalidCustomChangeException.class, () -> applier.apply(null));
    }

    @Test
    void applyThrowsInvalidCustomChangeExceptionWhenCustomChangeCannotBeInstantiated() {
        CustomChangeApplier applier = new CustomChangeApplier("cz.cvut.kbss.model.change.CustomChangeApplierTest$InvalidTestCustomChange");
        assertThrows(InvalidCustomChangeException.class, () -> applier.apply(null));
    }

    @SuppressWarnings("unused")
    public static class InvalidTestCustomChange implements CustomChange {

        // Missing public no-arg constructor

        public InvalidTestCustomChange(String param) {
        }

        @Override
        public void apply(OntologyRepository repository) {

        }
    }
}