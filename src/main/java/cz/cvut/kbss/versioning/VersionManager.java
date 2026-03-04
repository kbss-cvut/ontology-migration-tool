package cz.cvut.kbss.versioning;

import cz.cvut.kbss.repository.OntologyRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class VersionManager {
    private final OntologyRepository repository;
    private static final String CONTEXT = "http://onto.fel.cvut.cz/ontologies/ontology-migration-tool/versions";

    public VersionManager(OntologyRepository repository) {
        this.repository = repository;
    }

    public boolean isApplied(String id) {
        String query = String.format("""
                                             ASK {
                                                GRAPH <%s> {
                                                    ?cs <http://onto.fel.cvut.cz/ontologies/ontology-migration-tool/version-id> "%s" .
                                                }
                                             }
                                             """, CONTEXT, id);
        return repository.ask(query);
    }

    public void markApplied(String id) {
        String now = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();
        String query = String.format("""
                                             INSERT DATA {
                                                 GRAPH <%s> {
                                                     _:cs <http://onto.fel.cvut.cz/ontologies/ontology-migration-tool/version-id> "%s" ;
                                                          <http://onto.fel.cvut.cz/ontologies/ontology-migration-tool/applied-at> "%s"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
                                                 }
                                             }
                                             """, CONTEXT, id, now);
        repository.update(query);
    }
}
