package integration;

import cz.cvut.fel.repository.OntologyRepository;

import java.util.ArrayList;
import java.util.List;

public class TestRepository implements OntologyRepository {
    private boolean transactionActive = false;
    private final List<String> updates = new ArrayList<>();

    @Override
    public void begin() {
        transactionActive = true;
    }

    @Override
    public void update(String sparql) {
        updates.add(sparql);
    }

    @Override
    public void commit() {
        transactionActive = false;
    }

    @Override
    public boolean ask(String sparql) {
        return false;
    }

    public List<String> getUpdates() {
        return updates;
    }
}
