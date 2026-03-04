package cz.cvut.kbss.versioning;

import cz.cvut.kbss.repository.OntologyRepository;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class VersionManager {
    private final OntologyRepository repository;
    private static final String VERSIONS =  "http://example.org/versions";

    public VersionManager(OntologyRepository repository) {
        this.repository = repository;
    }

    public boolean isApplied(String id){
        String query = String.format("""
                ASK {
                    GRAPH <%s> {
                        ?cs <http://example.org/id> "%s" .
                    }
                }
            """, VERSIONS, id);
        return repository.ask(query);
    }

    public void markApplied(String id){
        String now = OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        String query = String.format("""
                INSERT DATA {
                    GRAPH <%s> {
                        _:cs <http://example.org/id> "%s" ;
                             <http://example.org/appliedAt> "%s" .
                    }
                }
                """, VERSIONS, id, now);
        repository.update(query);
    }
}
