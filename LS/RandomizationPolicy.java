package LS;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import alg.Node;
import alg.Policy;
import alg.RandCWS;
import alg.Edge;
import alg.Inputs;
import alg.Solution;
import alg.Test;

public class RandomizationPolicy {
	
					
		public static Solution RandoPolicy(Inputs inputs, Solution baseSol,Test aTest, Random rng){
			
			 Solution newSol = null;
			 int index = 0;
			 Map<Integer,Double> PolicyCost = new LinkedHashMap<Integer, Double>();
			 
		     for( int i = 1; i < inputs.getNodes().length; i++ ) //node 0 is depot
	         {
		    	 index = 0;
	             Node aNode = inputs.getNodes()[i];
	             Policy initActivePolicy = aNode.getActivePolicy();
	             float unitsToServe = 0;
	             double stockCosts = 0;
	             PolicyCost.clear();
	             
	             PolicyCost.put(aNode.getIndexPolicy(initActivePolicy.getRefillUpToPercent()), baseSol.getTotalCosts());
	             
	             for( Policy p : aNode.getPoliciesByRefill() )
	             {
	            	 //guardo el coste de la politica actual 
	            	 
	                 if( p != initActivePolicy ) // try only policies other than the initial one
	                 {   
	                	 index++;
	                	 unitsToServe = baseSol.getDemandToServe() - 
	                             aNode.getActivePolicy().getUnitsToServe() + p.getUnitsToServe();
	                     stockCosts = baseSol.getStockCosts() - 
	                             aNode.getActivePolicy().getExpStockCosts() + p.getExpStockCosts();
	                     Policy auxActivePolicy = aNode.getActivePolicy(); // auxiliar copy
	                     aNode.setActivePolicy(p); // change policy to test for improvement
	                     
	                     newSol = RandCWS.solve(aTest, inputs, rng, false);
	                     newSol.setDemandToServe(unitsToServe);
	                     newSol.setStockCosts(stockCosts);
	                     newSol.setTotalCosts(stockCosts + newSol.getRoutingCosts());
	                     
	                     // check that finalServedDemand = demandToServe at the beginning
	                     if( Math.abs(newSol.getDemandToServe() - newSol.getServedDemand()) > 0.001 ){
	                         System.out.println("PROBLEM WITH DEMANDS, UNRELIABLE SOL!!! " + newSol.getDemandToServe() + " " + newSol.getServedDemand());
	                     	  System.exit(1);
	                     }

	                     aNode.setActivePolicy(auxActivePolicy); 
	                     PolicyCost.put(aNode.getIndexPolicy(p.getRefillUpToPercent()), newSol.getTotalCosts());
	                 }
	             }
	             
	            Map<Integer, Double> sortedMapAsc = new LinkedHashMap<Integer, Double>();
	            sortedMapAsc  = sortByComparator(PolicyCost, true);
	            
	            
	            Policy currentPolicy = inputs.getNodes()[i].getActivePolicy();
	            double currentUnitsToServe = currentPolicy.getUnitsToServe();
	            Boolean exDemand = true;
	            int times = 0;
	            
	           // while(exDemand && times < 50){
	            	times++;
	               int policyIndex = getRandomPosition(aTest.getIrpBias(),aTest.getIrpBias(),rng,aNode.getPoliciesByCosts().length-1);
	    	            
	               List<Entry<Integer, Double> > indexedList = new ArrayList<Map.Entry<Integer, Double>>(sortedMapAsc.entrySet());
	               Map.Entry<Integer, Double> entry = indexedList.get(policyIndex);
	               int selectPolice = entry.getKey();
	            
	               unitsToServe = baseSol.getDemandToServe() - aNode.getActivePolicy().getUnitsToServe() + aNode.getRandomPolicy(selectPolice).getUnitsToServe();
	           	  
	               double unitsToServePolicy = inputs.getNodes()[i].getRandomPolicy(selectPolice).getUnitsToServe();
            	
	               //double newDemand = inputs.getNodes()[i].getInRoute().getDemand() - currentUnitsToServe + unitsToServePolicy;
	              // exDemand = false;
            	  
	               //if(newDemand <= inputs.getVehCap()){
	            	   stockCosts = baseSol.getStockCosts() -  aNode.getActivePolicy().getExpStockCosts() + aNode.getRandomPolicy(selectPolice).getExpStockCosts();
	            
	            	   Policy auxActivePolicy2 = aNode.getActivePolicy(); // auxiliar copy
	            
	            	   aNode.setActivePolicy(aNode.getRandomPolicy(selectPolice)); // change policy to test for improvement
	            
	            	   newSol = RandCWS.solve(aTest, inputs, rng, false);
	            	   newSol.setDemandToServe(unitsToServe);
	            	   newSol.setStockCosts(stockCosts);
	            	   newSol.setTotalCosts(stockCosts + newSol.getRoutingCosts());
	            	   
	                          
	                  // check that finalServedDemand = demandToServe at the beginning
	            	   if( Math.abs(newSol.getDemandToServe() - newSol.getServedDemand()) > 0.001 ){
	                         System.out.println("PROBLEM WITH DEMANDS, UNRELIABLE SOL!!! " + newSol.getDemandToServe() + " " + newSol.getServedDemand());
	                     	  System.exit(1);
	                     }
	             
	                  // System.err.println("New SOL: " + newSol.getTotalCosts());
	                  
	                  /*int factible = checkCap(newSol,inputs.getVehCap() );
	                  
	                  if (factible == 1){   
	                	  aNode.setActivePolicy(auxActivePolicy2);  
	                	  exDemand = true;
	                	  continue;
	                  }*/
	              
	                  if( newSol.getTotalCosts() < baseSol.getTotalCosts() ) // If improvement
	                  {   
                         baseSol = newSol;
	                  }
	                  else // If not an improvement, reset active policy to its value
                        aNode.setActivePolicy(auxActivePolicy2);  
	               //}else{
	            	   //exDemand = true;
	              // }   
	            //}
	    
	       } // end for, try another policy for this node
	            
		      
		     return baseSol;	
	      } // try changing active policy in another node
			
		
		
		
		private static int getRandomPosition(double alpha, double beta, Random r,int size) {
			 double randomValue = alpha + (beta - alpha) * r.nextDouble();
			 int index = (int) (Math.log(r.nextDouble()) / Math.log(1 - randomValue));
			 // int index = (int) (Math.log(r.nextDouble()) / Math.log(1 - beta));
			 index = index % size;
			 return index;
		}
	
		
		
		
	    private static Map<Integer, Double> sortByComparator(Map<Integer, Double> unsortMap, final boolean order)
	    {
	        List<Entry<Integer, Double>> list = new LinkedList<Entry<Integer, Double>>(unsortMap.entrySet());

	        // Sorting the list based on values
	        Collections.sort(list, new Comparator<Entry<Integer, Double>>()
	        {
	            public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2)
	            {
	                if (order)
	                {
	                    return o1.getValue().compareTo(o2.getValue());
	                }
	                else
	                {
	                    return o2.getValue().compareTo(o1.getValue());

	                }
	            }
	        }); 
	    
	     // Maintaining insertion order with the help of LinkedList
	        Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
	        for (Entry<Integer, Double> entry : list)
	        {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }

	        return sortedMap;
	    }		 	
		
	    
	    private static int checkCap(Solution auxSolDet , float capacity){   
	     for(int i = 0; i < auxSolDet.getRoutes().size();i++){
			 double  totservedIS = 0;
			 for(int jj =0; jj < auxSolDet.getRoutes().get(i).getEdges().size(); jj++ ){
				 Edge tE = auxSolDet.getRoutes().get(i).getEdges().get(jj);
				 
				 if(tE.getEnd().getId() != 0 ){
					 totservedIS += tE.getEnd().getUnitsToServe();
				 }
				 if(totservedIS > capacity){
       		       return 1;
				 }
			 
			 }
	     }
	    return 0;
	}
	    
	    
	    
	    
}