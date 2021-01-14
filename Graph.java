/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myPackage;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hp
 */
public class Graph 
{
   private String NS = " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                        " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                        " PREFIX sd: <http://isimm.org/resource/sd#> " +
                        " PREFIX cmm: <http://vocab.deri.ie/cmm#> " + 
                        " PREFIX schema:  <http://schema.org#> " +
                        " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                        " PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
                        " PREFIX gn: <http://www.geonames.org/ontology#> " +
                        " PREFIX gr: <http://purl.org/goodrelations/v1#> ";   
   
   public List<Capability> Capabilities = new ArrayList<Capability>();
   
   public List<Capability> getCapabilities (Model objectModel)
   {
       String aux = null ;
       Capability C = null ;
       String s = " DESCRIBE ?C " + 
                  " WHERE { ?C rdf:type cmm:Capability .  } " ;
       
       Query q = QueryFactory.create(NS+s);
       Model m = QueryExecutionFactory.create(q,objectModel).execDescribe();
       StmtIterator iter = m.listStatements();
       // print out the predicate, subject and object of each statement
       while (iter.hasNext()) 
       {
            Statement stmt = iter.nextStatement(); // get next statement
            Resource subject = stmt.getSubject(); // get the subject
            Property predicate = stmt.getPredicate(); // get the predicate
            RDFNode object = stmt.getObject(); // get the object   
            if (! subject.toString().equals(aux))
               {   
                   C = new Capability();
                   this.Capabilities.add(C);  
               }
            
            PropertyValue V = new PropertyValue();            
            if (object instanceof Resource) 
                {  V.URI = "<"+object.toString()+">"; }
            else // object is a literal
                {  V.URI = " \"" + "<"+object.toString()+">" + " \"" ;   }
            
            C.URI = "<"+subject.toString()+">" ;
            C.PE.put("<"+predicate.toString()+">", V);
            
            aux = subject.toString() ;
        }
       
       return this.Capabilities ;
   }
    
    
}
