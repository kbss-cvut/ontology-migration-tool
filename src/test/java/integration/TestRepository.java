package integration;

import cz.cvut.kbss.repository.OntologyRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRepository implements OntologyRepository {
    private boolean transactionActive = false;
    private final List<String> updates = new ArrayList<>();

    @Override
    public void begin() {
        transactionActive = true;
    }

    @Override
    public void update(String sparql) {
        assertTrue(transactionActive);
        updates.add(sparql);
    }

    @Override
    public void commit() {
        transactionActive = false;
    }

    @Override
    public boolean ask(String sparql) {
        return updates.contains(sparql);
    }

    public List<String> getUpdates() {
        return updates;
    }

    @Override
    public void close() {
        // Do nothing
    }
}
