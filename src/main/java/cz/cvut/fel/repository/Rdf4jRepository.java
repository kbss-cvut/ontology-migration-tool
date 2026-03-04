package cz.cvut.fel.repository;

import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;


public class Rdf4jRepository implements OntologyRepository {
    private final Repository repo;
    private RepositoryConnection conn;

    public Rdf4jRepository(String endpoint, String user, String password) {
        HTTPRepository r = new HTTPRepository(endpoint);
        r.setUsernameAndPassword(user, password);
        r.init();
        this.repo = r;
    }

    @Override
    public void begin(){
        conn = repo.getConnection();
        conn.begin();
    }

    @Override
    public void update(String sparql) {
        Update upd = conn.prepareUpdate(sparql);
        upd.execute();
    }

    @Override
    public void commit() {
        conn.commit();
        conn.close();
    }

    @Override
    public boolean ask(String sparql) {
        try(RepositoryConnection c = repo.getConnection()){
            BooleanQuery bq = c.prepareBooleanQuery(QueryLanguage.SPARQL, sparql);
            return bq.evaluate();
        }
    }
}
