package cz.cvut.kbss.model.changes;

import cz.cvut.kbss.repository.OntologyRepository;


public class RenameResourceChange extends Change{
    private String oldIri;
    private String newIri;

    public String getOldIri() {
        return oldIri;
    }

    public void setOldIri(String oldIri) {
        this.oldIri = oldIri;
    }

    public String getNewIri() {
        return newIri;
    }

    public void setNewIri(String newIri) {
        this.newIri = newIri;
    }

    @Override
    public String apply(OntologyRepository repository) {
        String sparqlPropertyWog = String.format("""
                DELETE { ?s <%s> ?o }
                INSERT { ?s <%s> ?o }
                WHERE  { ?s <%s> ?o }
                """, oldIri, newIri, oldIri);
        String sparqlPropertyWg = String.format("""
                DELETE { GRAPH ?g { ?s <%s> ?o } }
                INSERT { GRAPH ?g { ?s <%s> ?o } }
                WHERE  { GRAPH ?g { ?s <%s> ?o } }
                """, oldIri, newIri, oldIri);
        String sparqlSubjectWog = String.format("""
                DELETE { <%s> ?p ?o}
                INSERT { <%s> ?p ?o }
                WHERE { <%s> ?p ?o }
                """, oldIri, newIri, oldIri);
        String sparqlSubjectWg = String.format("""
                DELETE { GRAPH ?g { <%s> ?p ?o} }
                INSERT { GRAPH ?g { <%s> ?p ?o } }
                WHERE { GRAPH ?g { <%s> ?p ?o } }
                """, oldIri, newIri, oldIri);
        String sparqlTypeWog =String.format("""
                DELETE { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s>}
                INSERT { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s>}
                WHERE { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> }
                """, oldIri, newIri, oldIri);
        String sparqlTypeWg =String.format("""
                DELETE { GRAPH ?g { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s>} }
                INSERT { GRAPH ?g { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s>} }
                WHERE { GRAPH ?g { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> } }
                """, oldIri, newIri, oldIri);
        String sparqlObjectWg = String.format("""
                DELETE { GRAPH ?g { ?s ?p <%s> } }
                INSERT { GRAPH ?g { ?s ?p <%s> } }
                WHERE  { GRAPH ?g { ?s ?p <%s> } }
                """, oldIri, newIri, oldIri);
        String sparqlObjectWog = String.format("""
                DELETE { ?s ?p <%s> }
                INSERT { ?s ?p <%s> }
                WHERE  { ?s ?p <%s> }
                """, oldIri, newIri, oldIri);
        return sparqlPropertyWg + ";\n" +
                sparqlSubjectWg + ";\n" +
                sparqlTypeWg + ";\n" +
                sparqlObjectWg + ";\n" +
                sparqlPropertyWog + ";\n" +
                sparqlSubjectWog + ";\n" +
                sparqlTypeWog + ";\n" +
                sparqlObjectWog;
    }

    @Override
    public String getLogMessage() {
        return String.format("Resource renamed: <%s> → <%s>", oldIri, newIri);
    }
}
