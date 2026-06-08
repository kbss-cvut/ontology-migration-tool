package integration;

import cz.cvut.kbss.repository.TransactionalRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRepository implements TransactionalRepository {
    private boolean transactionActive = false;
    private final List<String> updates = new ArrayList<>();

    @Override
    public void begin() {
        transactionActive = true;
    }

    @Override
    public void commit() {
        transactionActive = false;
    }

    @Override
    public void close() {
        // Do nothing
    }

    @Override
    public void update(String sparql) {
        assertTrue(transactionActive);
        updates.add(sparql);
    }

    @Override
    public boolean ask(String sparql) {
        return updates.contains(sparql);
    }

    @Override
    public void clearGraph(String graph) {
        assertTrue(transactionActive);
    }

    @Override
    public void importFromUrl(String url, String graph) {
        assertTrue(transactionActive);
    }

    public List<String> getUpdates() {
        return updates;
    }
}
