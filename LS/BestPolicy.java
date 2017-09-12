package LS;
import java.util.Random;
import alg.Node;
import alg.Policy;
import alg.RandCWS;
import alg.Inputs;
import alg.Solution;
import alg.Test;

public class BestPolicy {
		
	
	public static Solution ChangePolicy(Inputs inputs, Solution baseSol,Test aTest, Random rng){
		
		 Solution newSol = null;		 
		 
	     for( int i = 1; i < inputs.getNodes().length; i++ ) //node 0 is depot
         {
             Node aNode = inputs.getNodes()[i];
             Policy initActivePolicy = aNode.getActivePolicy();
             
             for( Policy p : aNode.getPoliciesByRefill() )
             {
                 if( p != initActivePolicy ) // try only policies other than the initial one
                 {   float unitsToServe = baseSol.getDemandToServe() - 
                             aNode.getActivePolicy().getUnitsToServe() + p.getUnitsToServe();
                     double stockCosts = baseSol.getStockCosts() - 
                             aNode.getActivePolicy().getExpStockCosts() + p.getExpStockCosts();
                     Policy auxActivePolicy = aNode.getActivePolicy(); // auxiliar copy
                     aNode.setActivePolicy(p); // change policy to test for improvement
                     
                     newSol = RandCWS.solve(aTest, inputs, rng, false);
                     newSol.setDemandToServe(unitsToServe);
                     
                     // check that finalServedDemand = demandToServe at the beginning
                     if( Math.abs(newSol.getDemandToServe() - newSol.getServedDemand()) > 0.001 ){
                    	 
                         System.out.println("PROBLEM WITH DEMANDS, UNRELIABLE SOL!!! " + newSol.getDemandToServe() + " " + newSol.getServedDemand());
                     	  System.exit(1);
                     }
                     newSol.setStockCosts(stockCosts);
                     newSol.setTotalCosts(stockCosts + newSol.getRoutingCosts());
                    // System.err.println("New SOL: " + newSol.getTotalCosts());
                     if( newSol.getTotalCosts() < baseSol.getTotalCosts() ) // If improvement
                     {   aNode.setActivePolicy(p);
                         baseSol = new Solution(newSol);             
                         
                     }
                     else // If not an improvement, reset active policy to its value
                         aNode.setActivePolicy(auxActivePolicy);
                 } // end if      
             } // end for, try another policy for this node
         } // try changing active policy in another node
		
	 return baseSol;	
		
	}
	

	
	

}
