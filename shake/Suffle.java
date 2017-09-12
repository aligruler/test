package shake;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import alg.Edge;
import alg.Inputs;
import alg.Policy;
import alg.Test;
import alg.Solution;
import alg.RandCWS;
import alg.Outputs;


public class Suffle extends shake
{
    public static double percentage = 0.20;
	
    public Suffle(Test test,Random rng) {
		super(test,rng);
		// TODO Auto-generated constructor stub
	}



     
    @Override
    public Solution shakeIt(Solution sol, Inputs inputs) {
    	
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

    
    	int numberRC = inputs.getNodes().length;
    	long policiesToChange = Math.round(percentage * numberRC);

    	rng.setSeed(aTest.getSeed());
    	for( int i = 0; i < policiesToChange; i++ )
        {
            int index =  rng.nextInt(numberRC-1) + 1; //getRandomIndex(1, numberRC-1); //0 is the depot, start in 1
            int newPolicy = rng.nextInt(inputs.getNodes()[index].getPoliciesByRefill().length-1); //getRandomIndex(0, inputs.getNodes()[index].getPoliciesByRefill().length-1);

            //System.out.println("index: " + index + " policy "+ newPolicy);
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
        newSol.setTotalCosts(newSol.getStockCosts() + newSol.getRoutingCosts());   
         
       
        
    return newSol;
    } // end shakeOne 
   
}
