package cz.cvut.fel.model.changes;

import cz.cvut.fel.repository.OntologyRepository;


public class RenameResourceChange extends Change{
    private String oldName;
    private String newName;


    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    @Override
    public String apply(OntologyRepository repository) {
        String sparqlPropertyWog = String.format("""
                DELETE { ?s <%s> ?o }
                INSERT { ?s <%s> ?o }
                WHERE  { ?s <%s> ?o }
                """, oldName, newName, oldName);
        String sparqlPropertyWg = String.format("""
                DELETE { GRAPH ?g { ?s <%s> ?o } }
                INSERT { GRAPH ?g { ?s <%s> ?o } }
                WHERE  { GRAPH ?g { ?s <%s> ?o } }
                """, oldName, newName, oldName);
        String sparqlSubjectWog = String.format("""
                DELETE { <%s> ?p ?o}
                INSERT { <%s> ?p ?o }
                WHERE { <%s> ?p ?o }
                """, oldName, newName, oldName);
        String sparqlSubjectWg = String.format("""
                DELETE { GRAPH ?g { <%s> ?p ?o} }
                INSERT { GRAPH ?g { <%s> ?p ?o } }
                WHERE { GRAPH ?g { <%s> ?p ?o } }
                """, oldName, newName, oldName);
        String sparqlTypeWog =String.format("""
                DELETE { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s>}
                INSERT { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s>}
                WHERE { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> }
                """, oldName, newName, oldName);
        String sparqlTypeWg =String.format("""
                DELETE { GRAPH ?g { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s>} }
                INSERT { GRAPH ?g { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s>} }
                WHERE { GRAPH ?g { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <%s> } }
                """, oldName, newName, oldName);
        String sparqlObjectWg = String.format("""
                DELETE { GRAPH ?g { ?s ?p <%s> } }
                INSERT { GRAPH ?g { ?s ?p <%s> } }
                WHERE  { GRAPH ?g { ?s ?p <%s> } }
                """, oldName, newName, oldName);
        String sparqlObjectWog = String.format("""
                DELETE { ?s ?p <%s> }
                INSERT { ?s ?p <%s> }
                WHERE  { ?s ?p <%s> }
                """, oldName, newName, oldName);
        String transaction = sparqlPropertyWg + ";\n" +
                sparqlSubjectWg + ";\n" +
                sparqlTypeWg + ";\n" +
                sparqlObjectWg + ";\n" +
                sparqlPropertyWog + ";\n" +
                sparqlSubjectWog + ";\n" +
                sparqlTypeWog + ";\n" +
                sparqlObjectWog;
        return transaction;
    }

    @Override
    public String getLogMessage() {
        return String.format("Resource renamed: <%s> → <%s>", oldName, newName);
    }
}
