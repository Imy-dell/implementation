/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myPackage;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author hp
 */
public class Capability 
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
   
   private PropertyValue PV = new PropertyValue();
   public String URI ;
   public Map<String, PropertyValue>   PE;
     
   Capability()
   {
        this.URI = null ;
        this.PE = new LinkedHashMap<String, PropertyValue>();
   }
    
 /*  public boolean isSourceNode (PropertyValue value, Model objectModel)
   {
       boolean result = false ;
       String s = " DESCRIBE  " + value.URI ;
       
       Query q = QueryFactory.create(NS+s);
       Model m = QueryExecutionFactory.create(q,objectModel).execDescribe();
       StmtIterator iter = m.listStatements();
       // get the predicate, subject and object of each statement
       while (iter.hasNext()) 
       {
            Statement stmt = iter.nextStatement(); // get next statement
            Property predicate = stmt.getPredicate(); // get the predicate
            RDFNode object = stmt.getObject(); // get the object   
            if ( predicate.toString().contains("dependsOn"))
            { 
                result = true ;
                PropertyValue V = new PropertyValue();
                
                if (object instanceof Resource) 
                    {  V.URI = "<"+object.toString()+">" ; }
                else // object is a literal
                    {  V.URI = " \"" + "<"+object.toString()+">" + " \"" ;   }
                
                value.Dependencies.add(V);
            }
        }
       
       return result ;
   }*/
   
   public boolean Specifies(Capability C)
   {
       PV.readRDFfile();        
       int countS = 0 , countE = 0;
       if(this.PE.keySet().containsAll(C.PE.keySet()))
       {
         for(Iterator<String> i = C.PE.keySet().iterator() ; i.hasNext() ; )
           {
               String pd = i.next();
               String v1 = this.PE.get(pd).URI ;
               String v2 = C.PE.get(pd).URI ;
               
               if (! v1.equals(v2)) 
               { if (PV.specifies(v1,v2,this,C))
                   {  
                       countS++;
                   }
               }
               else 
               {   countE ++ ;  }
           } 
       }
       
       return ( countS + countE == C.PE.size() ) ;
   }
   
}
