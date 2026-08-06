package cz.cvut.kbss.loader;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import cz.cvut.kbss.model.ChangeLog;
import cz.cvut.kbss.model.ChangeSet;
import cz.cvut.kbss.model.change.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChangeLogLoaderTest {
    static final long CHANGE_TYPE_COUNT = getChangeSubTypeCount();

    static Collection<Class<? extends Change>> getChangeClasses() {
        return List.of(
                AddClassChange.class,
                AddPropertyChange.class,
                AddResourceChange.class,
                CustomChangeApplier.class,
                DeleteClassChange.class,
                DeleteResourceChange.class,
                ImportFromUrlChange.class,
                RenameResourceChange.class,
                SparqlUpdateChange.class
        );
    }

    static long getChangeSubTypeCount() {
        return Arrays.stream(Change.class.getAnnotation(JsonSubTypes.class)
                .value())
                .map(JsonSubTypes.Type::value)
                .distinct()
                .count();
    }

    @Test
    void loadValidChangelogLoadsChangelog() {
        ChangeLogLoader loader = new ChangeLogLoader("valid-changelog.yaml");
        final ChangeLog changeLog = assertDoesNotThrow(loader::loadChangelog);

        assertNotNull(changeLog);
        assertEquals(1, changeLog.getChangeSets().size());
        final ChangeSet changeSet = changeLog.getChangeSets().get(0);
        assertEquals("cs-001", changeSet.getId());
        assertEquals(CHANGE_TYPE_COUNT, changeSet.getChanges().size());
    }

    @Test
    void loadValidChangelogLoadsEveryChangeType() {
        ChangeLogLoader loader = new ChangeLogLoader("valid-changelog.yaml");
        final ChangeLog changeLog = loader.loadChangelog();
        final ChangeSet changeSet = changeLog.getChangeSets().get(0);

        for (Class<? extends Change> type : getChangeClasses()) {
            changeSet.getChanges().stream()
                    .filter(c -> c.getClass().equals(type))
                    .findAny()
                    .orElseGet(() -> fail("Change of type " + type.getSimpleName() + " not found"));
        }
    }
}