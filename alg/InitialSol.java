package alg;

import java.util.Random;

public class InitialSol {
	
	 	public Test aTest;
	 	public Inputs inputs; // contains savingsList too
	 	public Random rng;
	 	

	    InitialSol(Test myTest, Inputs myInputs, Random myRng)
	    {
	        aTest = myTest;
	        inputs = myInputs;
	        rng = myRng;
	    }
		
		
	    public int makeInitSol(){
	    	
	    	Solution[] initialSols = new Solution[6]; // (0):0% (1):25% ... (5):decent.
	    	int indexBestInitSol = 0;
	    	double bestSolTotalCosts = Double.MAX_VALUE;
	    	
	    	// 2.1. Compute the total costs (stock and routing) of each srategy.
	    	for( int index = 0; index <= 5; index++ )
	    	{
	    	    initialSols[index] = constructInitialSols(index); // key construction process
	    	    double solTotalCosts = initialSols[index].getTotalCosts();
	    	    if( solTotalCosts < bestSolTotalCosts )
	    	    {   indexBestInitSol = index;
	    	        bestSolTotalCosts = solTotalCosts;
	    	    }  
	    	}


	    	// 2.3. For each node, set its best policy so far.
	    	for( Node aNode : inputs.getNodes() ){
	    	    if( aNode.getId() != 0 ){ // depot is node 0
	    	        aNode.setBestSolPolicy(aNode.getActivePolicy().getACopy());
	    	    }
	    	}
	    	
	    return indexBestInitSol;
	    }
	    
	    
	    /* AUXILIARY METHODS */
	    public Solution constructInitialSols(int index)
	    {
	        double stockCosts = 0.0;
	        float accDemand = 0.0f;
	        for( int i = 1; i < inputs.getNodes().length; i++ ) // node 0 is depot
	        {    
	            Node aNode = inputs.getNodes()[i];
	            if( index == 5 ) // decentralized, use policiesByCosts
	                aNode.setActivePolicy(aNode.getPoliciesByCosts()[0]);
	            else // use policiesByRefill
	                aNode.setActivePolicy(aNode.getPoliciesByRefill()[index]);
	            
	            accDemand = accDemand + aNode.getActivePolicy().getUnitsToServe();
	            stockCosts = stockCosts + aNode.getActivePolicy().getExpStockCosts();
	        }
	        Solution aSol = RandCWS.solve(aTest, inputs, rng, false);
	        aSol.setDemandToServe(accDemand);
	        aSol.setStockCosts(stockCosts);
	        
	        
	        aSol.setTotalCosts(stockCosts + aSol.getRoutingCosts());
	        return aSol;
	    }
	    
}

