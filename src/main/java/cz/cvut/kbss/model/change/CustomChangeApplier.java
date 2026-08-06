package cz.cvut.kbss.model.change;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.kbss.exception.InvalidCustomChangeException;
import cz.cvut.kbss.model.change.custom.CustomChange;
import cz.cvut.kbss.repository.OntologyRepository;

import java.lang.reflect.InvocationTargetException;

/**
 * Executes a user-provided Java change implementation.
 * <p>
 * Loads the class specified by {@link #className}, instantiates it with a no-arg constructor,
 * and applies it as a {@link CustomChange}.
 */
public class CustomChangeApplier extends Change {

    @JsonProperty("class")
    private String className;

    public CustomChangeApplier() {
    }

    public CustomChangeApplier(String className) {
        this.className = className;
    }

    @Override
    public void apply(OntologyRepository repository) {
        try {
            final Class<? extends CustomChange> changeClass = Class.forName(className).asSubclass(CustomChange.class);
            final CustomChange change = changeClass.getDeclaredConstructor().newInstance();
            change.apply(repository);
        } catch (ClassNotFoundException e) {
            throw new InvalidCustomChangeException("Custom change class " + className + " not found.", e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new InvalidCustomChangeException("Unable to instantiate custom change.", e);
        }
    }

    @Override
    public String getLogMessage() {
        return "Executing custom change class: " + className;
    }
}
