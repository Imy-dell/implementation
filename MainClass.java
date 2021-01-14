/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myPackage;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;


    
/**
 *
 * @author hp
 */
public class MainClass 
{
   static PropertyValue PV = new PropertyValue() ;
   static Graph G = new Graph();
    
   public static void main(String... args) throws FileNotFoundException 
   {
        PV.readRDFfile(); 
        List<Capability> L = G.getCapabilities(PV.objectModel); 
        
        for (Iterator<Capability> i = L.iterator(); i.hasNext();) 
        {  
            Capability C1 = i.next();
            for (Iterator<Capability> j = L.iterator(); j.hasNext(); )  
            {
                Capability C2 = j.next();
                if (!C1.equals(C2) )
                { if (C1.Specifies(C2))
                    {
                        System.out.println(C1.URI + "\t cmm:variantOf \t " + C2.URI);
                        PV.addRelation(C1, C2);
                    }
                }
            }
        }
        System.out.println("We have inferred "+ PV.nbRelations + " specification order relation");
        
   }
}














        
        /*
         // MGV_vs_MGV
          System.out.println(PV.specifies("sd:Sousse","sd:RepublicOfTunisia",null,null)); //exemple
          System.out.println(PV.specifies("sd:Sousse","sd:RepublicOfAlgeria",null,null)); //contre exemple
         
         // 2MGV_vs_MGV
          System.out.println(PV.specifies("sd:CenterOfTunisia","sd:RepublicOfTunisia",null,null));
          System.out.println(PV.specifies("sd:CenterOfTunisia","sd:RepublicOfAlgeria",null,null));
         
        
        
        
          //MGV_vs_2MGV
          System.out.println(PV.specifies("sd:Sousse","sd:CenterOfTunisia",null,null)); //exemple
          System.out.println(PV.specifies("sd:Sousse","sd:SouthOfTunisia",null,null)); //contre exemple
         
          //2MGV_vs_2MGV
          System.out.println(PV.specifies("sd:Sahel","sd:CenterOfTunisia",null,null)); //exemple
          System.out.println(PV.specifies("sd:NorthOfTunisia","sd:CenterOfTunisia",null,null)); //contre exemple
         
          
        
         
         // MGV_vs_Const
          System.out.println(PV.specifies("<http://isimm.org/resource/sd#15>","sd:weightValue10_100",null,null)); //exemple
          System.out.println(PV.specifies("<http://isimm.org/resource/sd#15>","sd:weightValue25_70",null,null)); //contre exemple
         
         // Const_vs_Const
          System.out.println(PV.specifies("sd:weightValue25_70","sd:weightValue10_100",null,null));//exemple
          System.out.println(PV.specifies("sd:weightValue10_100","sd:weightValue25_70",null,null));//contre exemple
         
     
     
     
        
         //MGV_vs_FN
           System.out.println(PV.specifies("<http://isimm.org/resource/sd#105.0>","sd:priceValueT",L.get(0),L.get(1))); //exemple
           System.out.println(PV.specifies("<http://isimm.org/resource/sd#105.0>","sd:priceValueC",L.get(0),L.get(1))); //contre exp
         
            
            
            
         
         // Enum_vs_Enum
          System.out.println(PV.specifies("sd:to1","sd:to",null,null)); //exemple
          System.out.println(PV.specifies("sd:to","sd:to1",null,null)); //contre exemple
         
         //value_vs_Enum
          System.out.println(PV.specifies("<http://isimm.org/resource/sd#Sousse>","sd:to",null,null)); //exemple
         
         
         //CND_vs_CND
          System.out.println(PV.specifies("sd:priceValueCNDcenter", "sd:priceValueCNDtunisia", L.get(0), L.get(1))); //exemple
          System.out.println(PV.specifies("sd:priceValueCNDtunisia", "sd:priceValueCNDcenter", L.get(0), L.get(1))); //contre exp
        
         * /
        
         
        
   }    
        
}      
        
        












        
      /*  System.out.println(R.specifies("sd:to1","sd:to",null));
        System.out.println(R.specifies("sd:NorthOfTunisia","sd:RepublicOfAlgeria",null));
        
        
        
        
     /*   System.out.println(R.specifies("sd:Tunis","sd:NorthOfTunisia"));
        System.out.println(R.specifies("sd:weightValue25_70","sd:weightValue10_100"));
        System.out.println(R.specifies("sd:Tunis","sd:to"));
        System.out.println(R.specifies("sd:priceValueCNDcenter","sd:priceValueCNDtunisia"));
        System.out.println(R.specifies("<http://isimm.org/resource/sd#NorthOfTunisia>","sd:to1"));
        System.out.println(R.specifies("sd:to1","sd:to"));
        System.out.println(R.specifies("<http://isimm.org/resource/sd#RepublicOfAlgeria>","sd:to"));
        
         
       
        
        
        System.out.println(R.specifies("sd:Sousse","sd:RepublicOfTunisia"));    
       // System.out.println(R.specifies("sd:ContinentAfricain","sd:RepublicOfTunisia"));        
        
        System.out.println(R.specifies("sd:CenterOfTunisia","sd:RepublicOfTunisia"));  
        
        System.out.println(R.specifies("sd:conditionTunisia","sd:RepublicOfTunisia")); 
       // System.out.println(R.specifies("sd:weightValue10_100","sd:RepublicOfTunisia"));
        
          
        System.out.println(R.specifies("sd:Sousse","sd:CenterOfTunisia"));  
       // System.out.println(R.specifies("sd:Sousse","sd:SouthOfTunisia"));  
        
        System.out.println(R.specifies("sd:Sahel","sd:CenterOfTunisia"));
      //  System.out.println(R.specifies("sd:NorthOfTunisia","sd:CenterOfTunisia"));
        
        R.specifies("<http://isimm.org/resource/sd#105.0>","sd:priceValueT");
        
        System.out.println(R.specifies("sd:priceValueC","sd:priceValueT"));
     //   System.out.println(R.specifies("sd:priceValueT","sd:priceValueC"));
        
        System.out.println(R.specifies("sd:weightValue10_100","sd:priceValueT"));
        
        System.out.println(R.specifies("<http://isimm.org/resource/sd#15>","sd:weightValue10_100")); 
       // System.out.println(R.specifies("<http://isimm.org/resource/sd#15>","sd:weightValue25_70")); 
        
        System.out.println(R.specifies("sd:weightValue25_70","sd:weightValue10_100")); 
       
        System.out.println(R.specifies("sd:priceValueCNDcenter","sd:priceValueCNDtunisia")); */
        
        
        //   System.out.println(L.size());
      /*  for (Iterator<Capability> i = L.iterator(); i.hasNext();) 
            {
                Capability C = i.next();
                System.out.println("--------------------------------  "+ C.URI+ "  ------------------------------------" );
                for ( Iterator<PropertyValue> j = C.Values.iterator(); j.hasNext() ; ) 
                {
                    PropertyValue value = j.next();
                    if(C.isSourceNode(value,R.objectModel))
                    {for (Iterator<PropertyValue> k = value.Dependencies.iterator(); k.hasNext();) 
                        {   PropertyValue v = k.next();
                            System.out.println("--------> "+v.URI);
                        }}
                    else 
                    { System.out.println(value.URI);   }
                }
            } */
        
        
        //UM.WriteRDFfile(objectModel);