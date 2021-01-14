/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myPackage;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import java.io.*;
import java.util.*;
import javax.script.*;

/**
 *
 * @author hp
 */
public class PropertyValue 
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
   
    public Model objectModel = ModelFactory.createMemModelMaker().createModel();
    public String URI ;
    public List<PropertyValue> Dependencies ;
    public int nbRelations = 0;
    
    PropertyValue()
    {
        this.URI = null ;
        this.Dependencies = new ArrayList<PropertyValue>() ;
    }
    
    public boolean specifies(String uri1, String uri2, Capability C1, Capability C2)
   {
       String typeV2 = getType(uri2) ;
       if(!getMGV(uri1).equals(getMGV(uri2)))
            {  //System.out.print("because the given values aren't parametred by the same Most General Value, the result is : ");  
               return false ; }
       
       else if (typeV2.equals("FunctionalValue"))
               {  return specifies_FN (uri1, uri2, C1, C2) ;  }
             
            else if (typeV2.equals("ConstrainedValue"))
                    {  return  specifies_Const (uri1, uri2, C1, C2) ; }
             
                 else if (typeV2.equals("ConditionalValue"))
                         {   return specifies_CND(uri1,uri2,C1,C2) ;  }
                  
                      else if (typeV2.equals("EnumerationValue"))
                              { return specifies_Enum(uri1,uri2,C1,C2);  }
                       
                           else if (typeV2.startsWith("_"))
                                   { return specifies_2MGV (uri1, uri2, C1, C2); }
                            
                                else
                                    { return specifies_MGV (uri1, uri2, C1, C2); }
         
       
   }
  
   /*******************************************************************************************/
   /*******************************************************************************************/
   
   private boolean specifies_MGV (String uri1, String uri2, Capability C1, Capability C2)
   {
       String typeV1 = getType(uri1);
       
       if (typeV1.equals("EnumerationValue"))
           {   return Enum_specifies_Value (uri1, uri2, C1, C2) ; }
       
       else if (typeV1.equals("ConditionalValue"))
            {  return CND_specifies_Value (uri1, uri2, C1, C2)  ; }
             
            else  if (typeV1.equals("ConstrainedValue"))
                  { return Const_specifies_MGV (uri1, uri2) ;  }
                  
                  else if (typeV1.equals("FunctionalValue"))
                        {   return R3_12(uri1, uri2, C1);  }
                       
                       else if (typeV1.startsWith("_"))
                            {   return _2MGV_specifies_MGV (uri1, uri2);  }
                            
                            else
                                {  return MGV_specifies_MGV (uri1, uri2); }
       
   }
   
   private boolean MGV_specifies_MGV (String uri1, String uri2)
   {
       String s = " ASK   { " 
                + uri1 + " ?specificationRelation  " + uri2 + " . "  
                + "  ?specificationRelation   rdfs:domain " + getMGV(uri1) + " . " 
                + "  ?specificationRelation   rdfs:range  " + getMGV(uri1) + "  . }" ;
       
       Query askQuery = QueryFactory.create(NS+s);
       return( QueryExecutionFactory.create(askQuery,objectModel).execAsk() );
   }
    
   private boolean _2MGV_specifies_MGV (String uri1, String uri2) 
   {
       boolean result = true ;         
       String s =  " SELECT  ?v  "
                +  " WHERE { ?v  rdf:type " + uri1 + " . } ";
       
       Query q = QueryFactory.create(NS+s);
       ResultSet rs = QueryExecutionFactory.create(q,objectModel).execSelect();
       while ( rs.hasNext() )
       {
            QuerySolution row = rs.nextSolution();
            String v = "<"+row.getResource("v").toString()+">" ;
            if (!MGV_specifies_MGV(v,uri2))
            { result = false ; } 
       }   
       
       return result ;       
   }
   
   private boolean Const_specifies_MGV (String uri1, String uri2) 
   {
       boolean result = true ;
       String s =  " SELECT  ?v "
                +  " WHERE { ?v   rdf:type  " + getMGV(uri1) + "  .  } ";
       
       Query q = QueryFactory.create(NS+s);
       ResultSet rs = QueryExecutionFactory.create(q,objectModel).execSelect();
       while ( rs.hasNext() )
           {
                QuerySolution row = rs.nextSolution();
                String v = "<"+row.getResource("v").toString()+">" ;
                if ( MGV_specifies_Const(v,uri1) )
                  { if ( !MGV_specifies_MGV(v,uri2) ) 
                        { result = false ; } }
           } 
       
       return result ;
   }
   
   /*******************************************************************************************/
   /*******************************************************************************************/
   
   private boolean specifies_2MGV (String uri1, String uri2, Capability C1, Capability C2)
   {
       String typeV1 = getType(uri1);
       
      if (typeV1.equals("EnumerationValue"))
           {   return Enum_specifies_Value (uri1, uri2, C1, C2) ; }
       
       else if (typeV1.equals("ConditionalValue"))
            {  return CND_specifies_Value (uri1, uri2, C1, C2)  ; }
             
            else  if (typeV1.equals("ConstrainedValue"))
                  { return Const_specifies_2MGV (uri1, uri2) ; }
                  
                  else if (typeV1.equals("FunctionalValue"))
                        {  return R3_12(uri1, uri2, C1);   }
                       
                       else if (typeV1.startsWith("_"))
                            {  return _2MGV_specifies_2MGV (uri1, uri2);  }
                            
                            else
                                {  return MGV_specifies_2MGV (uri1, uri2); }
       
   }
   
   private boolean MGV_specifies_2MGV (String uri1, String uri2)
   {
       String s = "  ASK {  " + uri1 + " rdf:type " + uri2 + " . } " ;       
       Query q = QueryFactory.create(NS+s);
       if (!QueryExecutionFactory.create(q,objectModel).execAsk())
       {
           s = " ASK  {  " + uri1 + " ?SR  ?v . "  
                           + "  ?SR   rdfs:domain " + getMGV(uri1) + " . " 
                           + "  ?SR   rdfs:range  " + getMGV(uri1) + " . " 
                           + "  ?v    rdf:type " + uri2 + "  . } " ;
           q = QueryFactory.create(NS+s);
       }
       return( QueryExecutionFactory.create(q,objectModel).execAsk() );
   }
   
   private boolean _2MGV_specifies_2MGV (String uri1, String uri2) 
   {
       String s = "  ASK { " + uri1 + " rdfs:subClassOf " + uri2 + " .  } " ; 
       Query q = QueryFactory.create(NS+s);
       boolean result = QueryExecutionFactory.create(q,objectModel).execAsk();
       if (result == false)
        { return (specificationRelation(uri1,uri2)) ; }
       else
        { return result ;}
   }
   
   private boolean Const_specifies_2MGV (String uri1, String uri2) 
   {
       boolean result = true ;
       String s =  " SELECT  ?v  "
                +  " WHERE { ?v  rdf:type " + getMGV(uri1) + " . } ";
       
       Query q = QueryFactory.create(NS+s);
       ResultSet rs = QueryExecutionFactory.create(q,objectModel).execSelect();
       while( rs.hasNext() )
           {
                QuerySolution row = rs.nextSolution();
                String v = "<"+row.getResource("v").toString()+">" ;
                if ( MGV_specifies_Const(v,uri1) )
                  { if ( !MGV_specifies_2MGV(v,uri2) ) 
                        { result = false ; } }
           } 
       
       return result ;
   }
   
   private boolean specificationRelation(String A, String B)
   {
       boolean result = true;
       String s = " SELECT  ?alpha  " 
                + " WHERE { ?alpha   rdf:type  "+ A + " . } " ; 
       
       Query q = QueryFactory.create(NS+s);
       ResultSet rs = QueryExecutionFactory.create(q,objectModel).execSelect();
       while(rs.hasNext())
       {
            QuerySolution row = rs.nextSolution();
            String alpha = "<"+row.getResource("alpha").toString()+">" ;
            if ( !MGV_specifies_2MGV(alpha,B) ) 
                     { result = false ; }
       } 
       
       if (result == false)
       { return result ; }
       else 
       {  s  = "  ASK {  ?alpha1   rdf:type " + A  + " . "
             + "         ?beta1    rdf:type " + B +  " . "
             + "         ?alpha1   ?SR   ?beta1  . "  
             + "         ?SR   rdfs:domain " + getMGV(A) + " . " 
             + "         ?SR   rdfs:range  " + getMGV(B) + "  . }" ;      
       
          q = QueryFactory.create(NS+s);
          return QueryExecutionFactory.create(q,objectModel).execAsk() ; 
       }
   }
   
   /*******************************************************************************************/
   /*******************************************************************************************/
   
   private boolean specifies_Const (String uri1, String uri2, Capability C1, Capability C2)
   {
       String typeV1 = getType(uri1);
       
      if (typeV1.equals("EnumerationValue"))
           {   return Enum_specifies_Value (uri1, uri2, C1, C2) ; }
       
       else if (typeV1.equals("ConditionalValue"))
            {  return CND_specifies_Value (uri1, uri2, C1, C2)  ; }
      
            else  if (typeV1.equals("ConstrainedValue"))
                 { return Const_specifies_Const (uri1, uri2) ; }
                  
                  else if (typeV1.equals("FunctionalValue"))
                        {  return R3_12(uri1, uri2, C1);  }
                       
                       else if (typeV1.startsWith("_"))
                            { return _2MGV_specifies_Const (uri1, uri2);  }
                            
                            else
                                { return MGV_specifies_Const (uri1, uri2); }
       
   }
   
   private boolean MGV_specifies_Const (String uri1, String uri2)
   {    
       String assertion = getExprValue(uri2);
       return satisfy(uri1,assertion) ;
   }
   
   private boolean _2MGV_specifies_Const (String uri1, String uri2)
   {
       boolean result = true ;         
       String s = " SELECT  ?v  "
                + " WHERE { ?v  rdf:type " + uri1 + " . } ";
       
       Query q = QueryFactory.create(NS+s);
       ResultSet rs = QueryExecutionFactory.create(q,objectModel).execSelect();
       while( rs.hasNext() )
       {
            QuerySolution row = rs.nextSolution();
            String v = "<"+row.getResource("v").toString()+">" ;
            if ( !MGV_specifies_Const(v,uri2) ) 
                     { result = false ; } 
       } 
       
       return result ;
   }
   
   private boolean Const_specifies_Const (String uri1, String uri2)
   {
       boolean result = true ;
       String s = " SELECT  ?v  "
                + " WHERE { ?v  rdf:type " + getMGV(uri1) + " . } ";
       
       Query q = QueryFactory.create(NS+s);
       ResultSet rs = QueryExecutionFactory.create(q,objectModel).execSelect();
       while( rs.hasNext() )
           {
                QuerySolution row = rs.nextSolution();
                String v = "<"+row.getResource("v").toString()+">" ;
                if ( MGV_specifies_Const(v,uri1) )
                  {   if ( !MGV_specifies_Const(v,uri2) ) 
                        {   result = false ; } }
           }         
       return result ;
   }
  
   /*******************************************************************************************/
   /*******************************************************************************************/
   
   private boolean specifies_FN (String uri1, String uri2, Capability C1, Capability C2)
   {
       String typeV1 = getType(uri1);
       
       if (typeV1.equals("EnumerationValue"))
       {   return Enum_specifies_Value (uri1, uri2, C1, C2) ;  }
       
       else if (typeV1.equals("ConditionalValue"))
            {  return CND_specifies_Value (uri1, uri2, C1, C2)  ; }
             
            else if (typeV1.equals("ConstrainedValue"))
                 {  return  false ; }
                  
                 else if (typeV1.equals("FunctionalValue"))
                      {   return false ;  }
                                    
                      else 
                        {  return SingleValue_specifies_FN (uri1, uri2, C1, C2); }
       
   }
      
   private boolean SingleValue_specifies_FN (String y, String FN, Capability Ci, Capability Cj) 
   {   
       boolean result = false;
       //FN est une valeur fonctionnelle calculée par une expression f et qui dépend d'un propriété pf
       String pf = getDependencie(FN); //get pf 
       String f = getFunction(FN); //get f 
       
       String v = Ci.PE.get(pf).URI; // v est la valeur de pf dans Ci
       String vf = Cj.PE.get(pf).URI; // v est la valeur de pf dans Cj
       
       if (specifies(v, vf, Ci, Cj))
       { if (image(v,f).equals(y))
            { result = true ; } }
       
       return result ;
   }
   
   private boolean R3_12(String FN, String y, Capability Ci)
   {boolean result=false;
       /*String f = getFunction(FN);
       String pf = getDependencie(FN);
       String vf = Ci.PE.get(pf).URI; //vf la valeur de la propriété pf
       
       boolean result = true ;
       String s =  " SELECT  ?v  "
                +  " WHERE { ?v  rdf:type " + getMGV(vf) + " . } ";
       
       Query q = QueryFactory.create(NS+s);
       ResultSet rs = QueryExecutionFactory.create(q,objectModel).execSelect();
       while( rs.hasNext() )
           {
                QuerySolution row = rs.nextSolution();
                String v = "<"+row.getResource("v").toString()+">" ;
                if ( specifies(v,vf,Ci,null) )
                  {   if (! specifies(image(v,f),y,Ci,null) ) 
                        { result = false ; } }
           } */
       
       return result ;    
   }
   
   private boolean FN_specifies_FN (String FN1, String FN2, Capability C1, Capability C2) 
   {
       String f1 = getFunction(FN1) , 
              pf1 = getDependencie(FN1) ,  
              vf1 = C1.PE.get(pf1).URI ; 
       
       String f2 = getFunction(FN2) , 
              pf2 = getDependencie(FN2 ),  
              vf2 = C2.PE.get(pf2).URI ; 
       
       if(f1.equals(f2))
         {  return specifies(vf1, vf2, C1, C2); }
       
       return false; 
   }
   
   private String getDependencie(String value)
   {
       String pd = null ;
       String s = " SELECT  ?pd " 
                + " WHERE { " + value + " cmm:dependsOn  ?pd . } " ; 
       Query q = QueryFactory.create(NS+s);
       ResultSet rs = QueryExecutionFactory.create(q,objectModel).execSelect();
       while (rs.hasNext()) 
        {
            QuerySolution row = rs.nextSolution();
            pd = "<"+row.getResource("pd").toString()+">" ;
        }
       return pd ;
   }
   
   private String image(String instance, String expr)
   {       
       Object eval = null;     
       String[] tab = instance.split("#");
       instance = tab[1].substring(0, tab[1].length()-1) ;
       expr = expr.replaceAll("[?][a-z]*", instance).substring(0, expr.indexOf("^"));
    
       ScriptEngineManager mgr = new ScriptEngineManager();
       ScriptEngine eng = mgr.getEngineByName("JavaScript");
       try {
                eval = eng.eval(expr);
           } 
       catch (ScriptException e) 
           {  System.out.println("Error evaluating input string.");   }
       
       return tab[0]+"#"+eval.toString()+">";
   }
  
   /*******************************************************************************************/
   /*******************************************************************************************/
   
   private boolean specifies_CND(String uri1, String uri2, Capability C1, Capability C2)
   {
       String typeV1 = getType(uri1);
       
       if ( !typeV1.equals("ConditionalValue") ) 
          {   return value_specifies_CND (uri1, uri2, C1, C2) ; } 
       
       else 
          {   return CND_specifies_CND (uri1, uri2, C1, C2) ; } 
           
    }

   private boolean value_specifies_CND (String y, String CND, Capability C1, Capability C2)
   {
       String c = getCondition(CND) ;
       String v = getValue(CND);       
       String pd = getDependencie(CND);
       try{
            if ( satisfy(C2.PE.get(pd).URI,c) )
                {  return specifies(y,v,C1,C2); }
            
          }catch(java.lang.NullPointerException e){return false;}
       
       return false ;
   }
    
   private boolean CND_specifies_Value (String CND, String y, Capability C1, Capability C2)
   {
       String v = getValue(CND);
       return specifies(v,y,C1,C2) ;
   }
   
   private String findSymbol(String expr)
   {
       String[] tab = expr.split(" ");
       for(int i = 0; i<=tab.length ; i++)
       {
           if(tab[i].startsWith("?"))
           { return tab[i] ; }
       }
       return null;
   }
   
   private boolean CND_specifies_CND (String CND1, String CND2, Capability C1, Capability C2) 
   {
       boolean result = false;
       String c1 = getCondition(CND1) ,  v1 = getValue(CND1)  ,  symbol1 = findSymbol(c1);
       String c2 = getCondition(CND2) ,  v2 = getValue(CND2) ;
       
       if (specifies(v1,v2,C1,C2))
       {
           result = true;
           String s = " SELECT "    + symbol1 + 
                      " WHERE  {  " + c1.substring(0, c1.indexOf("^")) + " . } " ;
       
           Query q = QueryFactory.create(NS+s);
           ResultSet rs = QueryExecutionFactory.create(q,objectModel).execSelect();
           while ( rs.hasNext() )
           {
                QuerySolution row = rs.nextSolution();
                String v = "<"+row.getResource(symbol1.substring(1)).toString()+">" ;
                String a = "ASK  { " + c2.substring(0, c2.indexOf("^")).replaceAll("[?][a-z]*", v) + " . } ";
                Query q1 = QueryFactory.create(NS+a);
                if ( !QueryExecutionFactory.create(q1,objectModel).execAsk() )
                {
                    result = false ;
                }
           } 
       }
       return result ;
   }
    
   /*****************************************************************************************/
   /*******************************************************************************************/
   
   private boolean specifies_Enum(String uri1, String uri2, Capability C1, Capability C2)
   {
       String typeV1 = getType(uri1);
       
       if ( (!typeV1.equals("EnumerationValue")) && (!typeV1.equals("ConditionalValue")) ) 
          {   return Value_specifies_Enum (uri1, uri2, C1, C2) ; } 
       
       else if (typeV1.equals("ConditionalValue")) 
               {  return CND_specifies_Value (uri1, uri2, C1, C2) ; } 
                                       
            else 
                {  return Enum_specifies_Enum (uri1,uri2,C1,C2) ; } 
       
    }
    
   private boolean Value_specifies_Enum (String v, String E, Capability C1, Capability C2)
   {           
       List<String> listOfelements = getElements(E);
       
       if (listOfelements.contains(v))
       { return true ;  }
       
       else
       {  for (Iterator<String> i = listOfelements.iterator(); i.hasNext();) 
              {
                 String item = i.next();
                 if (specifies(v,item,C1,C2))
                   {   return true ;  }
              }
       }
       
       return false ;
   }
   
   private boolean Enum_specifies_Enum (String E, String E1, Capability C1, Capability C2)
   {
       int countE=0, countS=0;
       List<String> elementsOfE = getElements(E);
       List<String> elementsOfE1 = getElements(E1);
       
       for (Iterator<String> i = elementsOfE.iterator(); i.hasNext();) //parcourir E
       {
         String alpha = i.next(); //pour tout alpha dans E
         if (elementsOfE1.contains(alpha)) //si alpha appartient à E'  
         { countE ++ ; }
         
         else 
         {  for (Iterator<String> j = elementsOfE1.iterator(); j.hasNext();) //on parcourt E'
            {
               String beta = j.next(); 
               if ( specifies(alpha,beta,C1,C2) ) // vérifier s'il existe une valeur beta spécifiée par alpha
                { countS++;
                  break;  }  
            }
         }
       }
       
       return (countS > 0) && (countE + countS == elementsOfE.size());
   } 
   
   private boolean Enum_specifies_Value (String E, String v, Capability C1, Capability C2)
   {
       boolean result = false;
       List<String> elementsOfE = getElements(E);
       for (Iterator<String> i = elementsOfE.iterator(); i.hasNext(); ) //parcourir E
       {
         String e = i.next(); //pour tout e dans E
         if (specifies(e,v,C1,C2) ) 
         { result = true ; }
         else
         { break; }
       }
       
       return result;
   }
   
   private List<String> getElements(String enumValue)
   {
       List<String> listOfelements = new ArrayList<String>();
       String s = NS 
              + " SELECT  ?v " 
              + " WHERE { " + enumValue + " rdf:type  cmm:EnumerationValue . " 
                            + enumValue + " cmm:hasElement  ?v . } " ; 
       Query q = QueryFactory.create(s);
       ResultSet rs = QueryExecutionFactory.create(q,objectModel).execSelect();
       while (rs.hasNext()) 
        {
            QuerySolution row = rs.nextSolution();
            String v = "<"+row.getResource("v").toString()+">" ;
            listOfelements.add(v) ;   
        }
       
       return listOfelements ;
   }
   
   
   /*********************************************************************************************/
   /*********************************************************************************************/
   
   
   private String getType (String URI)
   {
       //System.out.println(URI);
       String type = null ;
       String s = " SELECT ?type  "
                + " WHERE {  " + URI + "  rdf:type  ?type  .  ?type rdf:type owl:Class .  } " ;       
       //System.out.println(s);
       Query selectQuery = QueryFactory.create(NS+s);
       ResultSet rs = QueryExecutionFactory.create(selectQuery,objectModel).execSelect();       
       while(rs.hasNext())
       {   
           QuerySolution row = rs.nextSolution();
           type = "<"+row.getResource("type").toString()+">" ; 
       }
       //System.out.println(type);
       return subString(type) ;
   }
  
   private String getMGV(String URI)
   {
      String t = getType(URI) ;
      String s , mgv  = null;
      
      if(!t.isEmpty())
      {
          if ( t.equals("ConstrainedValue")||t.equals("FunctionalValue")||t.equals("ConditionalValue")||t.equals("EnumerationValue") )
          { s = "SELECT ?mgv  WHERE { " + URI + " cmm:hasParameterMGV  ?mgv . } " ;  }
      
          else if (t.startsWith("_"))
                { s = "SELECT ?mgv  WHERE { " + URI + " rdfs:subClassOf  ?mgv . ?mgv a owl:Class . } " ; }
                  
               else
                { s = "SELECT ?mgv  WHERE { " + URI + " rdf:type  ?mgv . ?mgv a owl:Class . } " ;  }
       
          Query selectQuery = QueryFactory.create(NS+s);
          ResultSet rs = QueryExecutionFactory.create(selectQuery,objectModel).execSelect();
          while(rs.hasNext())
          {
            QuerySolution row = rs.nextSolution();
            mgv = "<"+row.getResource("mgv").toString()+">" ;    
          }
      }
      return mgv ;
   }
   
   private String subString (String URI)
   {
       if (URI.contains("#"))
        { return URI.substring(URI.indexOf("#")+1, URI.length()-1); }
       
       else 
           if (URI.contains(":"))
                { return URI.substring(URI.indexOf(":")+1, URI.length()); }
       
           else 
                { return URI ; }
   }
   
   private boolean satisfy(String MGVuri, String constraint)
   {  
      boolean result  ;
      String c ;
      Object eval = null;
      if ( subString(MGVuri).matches("[0-9]+[.]*[0-9]+") )
            {   
                MGVuri = subString(MGVuri) ;                                
                c = constraint.replaceAll("[?]([a-z]*[A-Z]*)*", MGVuri);
                c = c.substring(0, c.indexOf("^"));
                ScriptEngineManager mgr = new ScriptEngineManager();
                ScriptEngine engine = mgr.getEngineByName("JavaScript");
                try   
                    {  eval = engine.eval(c);   }
                catch (ScriptException e) 
                    {  System.out.println("Error evaluating input string.");   }
                
                result =  (((Boolean) eval).booleanValue());
            }      
      else 
            {
                //System.out.println("helloooooo");
                constraint = constraint.replaceAll("[?]([a-z]*[A-Z]*)*", MGVuri);
                constraint = constraint.substring(0, constraint.indexOf("^"));
                //System.out.println(constraint);
                String s = NS +  "ASK { " + constraint + " } "  ; 
                //System.out.println(s);
                Query q = QueryFactory.create(s);
                result = QueryExecutionFactory.create(q,objectModel).execAsk();
            }
      return result ;
   } 
   
   private String getExprValue(String constrainedValue)
   {
       String exprValue = null ;
       String s = NS + 
                " SELECT   ?e " +
                " WHERE {  " + constrainedValue + " rdf:type cmm:ConstrainedValue . " +
                               constrainedValue + " cmm:hasParameterMGV ?mgv . " +
                               constrainedValue + " cmm:hasConstraint ?alpha . " +
                           "   ?alpha  cmm:exprValue ?e .  } " ;        
       Query q = QueryFactory.create(s);
       ResultSet rs = QueryExecutionFactory.create(q,objectModel).execSelect();
       while (rs.hasNext()) 
        {
            QuerySolution row = rs.nextSolution();
            exprValue = row.getLiteral("e").toString() ;            
        }       
       return exprValue ;
   }

   private String getFunction(String FNvalue)
   {
       String f = null ;
       String s = NS + 
                 " SELECT   ?expr " +
                 " WHERE {   " +  FNvalue + "  rdf:type cmm:FunctionalValue . " +
                                  FNvalue + "  cmm:hasFunction  ?f . " +
                                  " ?f   cmm:exprValue   ?expr   .   } " ;        
       Query q = QueryFactory.create(s);
       ResultSet rs = QueryExecutionFactory.create(q,objectModel).execSelect();
       while (rs.hasNext()) 
        {                 
            QuerySolution row = rs.nextSolution();
            f = row.getLiteral("expr").toString() ; 
        }
       
       return f ;
   }
           
   private String getValue(String CNDvalue)
   {
       String v = null ;
       String s = NS+
                  "SELECT  ?v " +
                  "WHERE { " + CNDvalue + " rdf:type  cmm:ConditionalValue . " 
                             + CNDvalue + " cmm:hasValue  ?v .  } " ;
       Query q = QueryFactory.create(s);
       ResultSet rs = QueryExecutionFactory.create(q,objectModel).execSelect() ;
       while (rs.hasNext())
       {
                QuerySolution row = rs.nextSolution();
                v = "<"+row.getResource("v").toString()+">" ;               
       } 
       return v ;
   }
   
   private String getCondition(String conditionalValue)
   {   
       String exprValue = null ;
       String s = NS + 
                " SELECT   ?expr " +
                " WHERE {  " + conditionalValue + " rdf:type cmm:ConditionalValue . " +
                               conditionalValue + " cmm:hasParameterMGV ?mgv . " +
                               conditionalValue + " cmm:hasCondition ?c . " +
                           "   ?c  cmm:exprValue ?expr .  } " ;        
       Query q = QueryFactory.create(s);
       ResultSet rs = QueryExecutionFactory.create(q,objectModel).execSelect();
       while (rs.hasNext()) 
        {
            QuerySolution row = rs.nextSolution();
            exprValue = row.getLiteral("expr").toString() ;            
        }       
       
       return exprValue ;
   }
   
   /***************************************************************************************/
   /***************************************************************************************/
   
   public void readRDFfile() 
   {
        String inputFile="D:/memory/mine/new.ttl";
        try 
        {
            InputStream in = new  FileInputStream(inputFile);
            if (in == null) 
                { System.out.println("File not found"); } 
            objectModel.read(new FileInputStream("D:/memory/mine/new.ttl"),"","TURTLE");
        } 
        
        catch (FileNotFoundException ex) 
            {   System.out.println("MainClass.read catched error: " + ex);  }
           
        catch(Exception e) 
            {    System.out.println("MainClass.read catched error: " + e);  }
    }

   public void WriteRDFfile(Model objectModel)
   {  
        StringWriter sw = new StringWriter();
        objectModel.setNsPrefix("","");
        objectModel.write(sw, "TURTLE");
        String owlCode = sw.toString();
        File file = new File("D:/memory/mine/new.ttl");    
        try 
        {
            FileWriter fw = new FileWriter(file);
            fw.write(owlCode);
            fw.close();
        } 
        catch (IOException ex) 
        {
            System.out.println("MainClass.write catched error: " + ex) ;
        }
              
    }  
   
   public void addRelation(Capability C1, Capability C2)
   {
       Property specifies = objectModel.getProperty("http://vocab.deri.ie/cmm#specifies");
       Resource c1 = objectModel.createResource(C1.URI.substring(1, C1.URI.length()-1));
       Resource c2 = objectModel.createResource(C2.URI.substring(1, C2.URI.length()-1));
       
       Statement stmnt = objectModel.createStatement(c1, specifies, c2);
       objectModel.add(stmnt);
       
       WriteRDFfile(objectModel.add(stmnt));
       nbRelations++;
   }
   
}
