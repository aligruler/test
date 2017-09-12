package alg;
import java.util.Random;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An improved version (Durstenfeld) of the Fisher-Yates algorithm with O(n) time complexity
 * Permutes the given array
 * @author 
 * @param array array to be shuffled
 */
public class SuffleOld
{
/*    public static Random r = new Random();
   
    public static void fisherYates(int[] array)
    {
        for( int i = array.length - 1; i > 0; i-- )
        {
            int index = r.nextInt(i); // uniform random number, 0 <= index < i
            //swap element at i with element at index
            int tmp = array[index];
            array[index] = array[i];
            array[i] = tmp;
        }
    } // end fisherYates method
    

   
    public static Solution shakeOne(double percentage, Inputs inputs, Solution sol, Test aTest,Random rng, boolean useRandom)
    {
    	float unitsToServe = 0;
    	Policy newRandomPolicy;
    	Policy currentPolicy;
    	float currentUnitsToServe = 0;
    	float totCurrentUnitsToServe = 0;
    	float totNewUnitsToServe = 0;
    	double totCurrentStockCosts = 0;
    	double currentStockCosts = 0;
    	double totNewStockCosts = 0;
    	float newDemand = 0;
    	
    	//Aux sol
    	Solution newSol = new Solution();
    	//Solution newSol = new Solution();
    	//newSol = sol;
    
    	int numberRC = inputs.getNodes().length;
    	long policiesToChange = Math.round(percentage * numberRC);

    	
    	for( int i = 0; i < policiesToChange; i++ )
        {
            int index = getRandomIndex(1, numberRC-1); //0 is the depot, start in 1
            int newPolicy = getRandomIndex(0, inputs.getNodes()[index].getPoliciesByRefill().length-1);

            currentPolicy = inputs.getNodes()[index].getActivePolicy();
            currentUnitsToServe = currentPolicy.getUnitsToServe();
            currentStockCosts = currentPolicy.getExpStockCosts();
          
            
            if(inputs.getNodes()[index].getRandomPolicy(newPolicy).getRefillUpToPercent() != inputs.getNodes()[index].getActivePolicy().getRefillUpToPercent()){
            	if(newPolicy == 5){ // decentralized, use policiesByCosts
            		unitsToServe = inputs.getNodes()[index].getPoliciesByCosts()[0].getUnitsToServe();
            		newRandomPolicy = inputs.getNodes()[index].getPoliciesByCosts()[0];
            	}else{
            		unitsToServe = inputs.getNodes()[index].getRandomPolicy(newPolicy).getUnitsToServe();	
            		newRandomPolicy = inputs.getNodes()[index].getRandomPolicy(newPolicy);
            	}
           
            
            	newDemand = inputs.getNodes()[index].getInRoute().getDemand() - currentUnitsToServe + unitsToServe;
                    
            	if(newDemand <= inputs.getVehCap()){
            		totCurrentUnitsToServe += currentUnitsToServe;
            		totCurrentStockCosts += currentStockCosts;
            		totNewStockCosts += newRandomPolicy.getExpStockCosts();
            		totNewUnitsToServe += unitsToServe;
            		inputs.getNodes()[index].setActivePolicy(newRandomPolicy);	
            	}
            }else{
            	i--;
            }
        }
 
    	
        newSol = RandCWS.solve(aTest, inputs, rng, false);
        newSol.setDemandToServe(sol.getDemandToServe() - totCurrentUnitsToServe + totNewUnitsToServe);	
        newSol.setStockCosts(sol.getStockCosts() - totCurrentStockCosts + totNewStockCosts);
        newSol.setTotalCosts(sol.getStockCosts() - totCurrentStockCosts + totNewStockCosts + sol.getRoutingCosts());   
    return newSol;
    } // end shakeOne
    
    
	public static int getRandomIndex(int a, int b) {
		int newIndex = ThreadLocalRandom.current().nextInt(a, b);
		return newIndex;
	}
	*/
}
