import cz.cvut.fel.repository.OntologyRepository;
import cz.cvut.fel.versioning.VersionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static javax.management.Query.times;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class VersionManagerTest {
    private OntologyRepository repository;
    private VersionManager versionManager;

    @BeforeEach
    void setUp() {
        repository = mock(OntologyRepository.class);
        versionManager = new VersionManager(repository);
    }

    @Test
    void returnTrueIfChangeSetIsAlreadyApplied() {
        when(repository.ask(anyString())).thenReturn(true);
        boolean applied = versionManager.isApplied("cs-001");
        assertTrue(applied);
    }

    @Test
    void markChangeSetAsApplied() {
        when(repository.ask(anyString())).thenReturn(false);
        versionManager.markApplied("cs-002");
        verify(repository).update(anyString());
    }
}
