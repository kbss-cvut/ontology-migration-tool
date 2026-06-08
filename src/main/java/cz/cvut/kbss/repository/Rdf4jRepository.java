package cz.cvut.kbss.repository;

import cz.cvut.kbss.exception.MigrationExecutionException;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class Rdf4jRepository implements OntologyRepository {

    private static final Logger LOG = LoggerFactory.getLogger(Rdf4jRepository.class);

    private final Repository repo;
    private RepositoryConnection conn;

    public Rdf4jRepository(String endpoint, String username, String password) {
        HTTPRepository r = new HTTPRepository(endpoint);
        r.setUsernameAndPassword(username, password);
        r.init();
        this.repo = r;
    }

    @Override
    public void close() {
        if (conn != null && conn.isActive()) {
            conn.rollback();
            conn.close();
        }
        repo.shutDown();
    }

    @Override
    public void begin() {
        this.conn = repo.getConnection();
        conn.begin();
    }

    @Override
    public void commit() {
        conn.commit();
        conn.close();
    }

    @Override
    public void update(String sparql) {
        LOG.trace("Executing SPARQL Update: {}", sparql);
        Update upd = conn.prepareUpdate(sparql);
        upd.execute();
    }

    @Override
    public boolean ask(String sparql) {
        try (RepositoryConnection c = repo.getConnection()) {
            BooleanQuery bq = c.prepareBooleanQuery(QueryLanguage.SPARQL, sparql);
            return bq.evaluate();
        }
    }

    @Override
    public void importFromUrl(String url, String context) {
        try (RepositoryConnection c = repo.getConnection()) {
            if (context != null) {
                c.add(new URL(url), Values.iri(context));
            } else {
                c.add(new URL(url));
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        } catch (IOException e) {
            throw new MigrationExecutionException("Unable to import RDF data.", e);
        }
    }
}
