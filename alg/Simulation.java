package alg;

import java.util.Arrays;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.rng.RandomStreamBase;
import umontreal.ssj.probdist.Distribution;
import umontreal.ssj.probdist.LognormalDist;
import umontreal.ssj.randvar.LognormalGen;
import umontreal.ssj.randvar.RandomVariateGen;

public class Simulation {
	
    public Test aTest;
    public Inputs inputs; 
    final int simIterStoch = 500;
    final int simIterDet = 1;
	
	Simulation(Test myTest, Inputs myInputs)
    {
        aTest = myTest;
        inputs = myInputs;
    }
	
	
 
    public void SimulateDemandDet(){
    	
		 int sss = aTest.getSeed(); 
		 int seedArray[] = {sss, sss, sss, sss};
	     LFSR113.setPackageSeed(seedArray);
    	RandomStreamBase stream = new LFSR113(); // L'Ecuyer stream
    	
        for( int i = 1; i < inputs.getNodes().length; i++ )
        {   
        	Node aNode = inputs.getNodes()[i];   
        	//System.out.println("InitialStock before: " + aNode.getCurrentLevel());
            // 1.1. Set the random distribution for this node.
            double mean = aNode.getExpDemand();
            double var = aTest.getK() * mean;
            double factor = Math.log(1 + var / (mean*mean));
            double mu = Math.log(mean) - 0.5 * factor;
            double sigma = Math.sqrt(factor);
            Distribution dist = new LognormalDist( mu, sigma );
            RandomVariateGen rngLogN = new LognormalGen( stream, (LognormalDist) dist );   
            
            
            // 1.2. Perform simulation runs.
            long start = ElapsedTime.systemTime();
            double elapsed = 0.0;
            float meanRandon = 0;
            
            for( int j = 0; j < simIterDet; j++ )
            {   
                // Generate random demand.
             
                float randomDemand = (float) aNode.getExpDemand();
               // meanRandon += randomDemand;
                // Compute accum. surplus and stock costs.
                for( Policy policy : aNode.getPoliciesByRefill() )
                {
                    float surplus = policy.getUnitsToServe() + aNode.getCurrentLevel() - randomDemand;
                    policy.setExpSurplus(policy.getExpSurplus() + surplus); // accumulated surplus
                    double stockCosts;
                    if( surplus >= 0 ){
                        stockCosts = aTest.getLambda() * surplus;
                    }else{
                        stockCosts = aNode.getRoundtripToDepotCosts();
                    }
                    meanRandon += stockCosts;
                    policy.setExpStockCosts(policy.getExpStockCosts() + stockCosts); // accum. stock costs
                }
            }
            
            //System.out.println("DET: nodo: " + i + " demand: " + meanRandon/nSimIter);
            elapsed = ElapsedTime.calcElapsed(start, ElapsedTime.systemTime());
            // 1.3. Compute expected surplus and stock costs.
            for( Policy policy : aNode.getPoliciesByRefill() )
            {
            	policy.setExpSurplus(policy.getExpSurplus() / simIterDet);
            	policy.setExpStockCosts(policy.getExpStockCosts() / simIterDet);
            }
            // 1.4. Sort policies in aNode by Costs (policiesByRefill will not be modified).
            Arrays.sort(aNode.getPoliciesByCosts());
        }
        
   	
    } 
    
       
    
    public void SimulateDemandSto(){
    	
		 int sss = aTest.getSeed(); 
		 int seedArray[] = {sss, sss, sss, sss};
	     LFSR113.setPackageSeed(seedArray);
    	
    	RandomStreamBase stream = new LFSR113(); // L'Ecuyer stream
    	float totCost = 0;
    	int  totStocks = 0;
    	
        for( int i = 1; i < inputs.getNodes().length; i++ )
        {   
        	Node aNode = inputs.getNodes()[i];   
        	//System.out.println("InitialStock before: " + aNode.getCurrentLevel());
            // 1.1. Set the random distribution for this node.
            double mean = aNode.getExpDemand();
            double var = aTest.getK() * mean;
            double factor = Math.log(1 + var / (mean*mean));
            double mu = Math.log(mean) - 0.5 * factor;
            double sigma = Math.sqrt(factor);
            Distribution dist = new LognormalDist( mu, sigma );
            RandomVariateGen rngLogN = new LognormalGen( stream, (LognormalDist) dist );   
            Policy policy = null;
            
            // 1.2. Perform simulation runs.
            long start = ElapsedTime.systemTime();
            double elapsed = 0.0;
            float meanRandon = 0;
            
            
            policy = aNode.getActivePolicy();
            policy.setExpSurplus(0);
        	policy.setExpStockCosts(0);
            
            for( int j = 0; j < simIterStoch; j++ )
            {   

                float randomDemand = (float) rngLogN.nextDouble();
                //float randomDemand = (float) aNode.getExpDemand();
               // meanRandon += randomDemand;
              
                 float surplus = policy.getUnitsToServe() + aNode.getCurrentLevel() - randomDemand;
                 policy.setExpSurplus(policy.getExpSurplus() + surplus); // accumulated surplus
                 double stockCosts;
                 if( surplus >= -1 ){
                	 stockCosts = aTest.getLambda() * Math.abs(surplus);
                	// System.out.println("NO STOCKOUT -- node " + i + " " +stockCosts+ " " + surplus);
                	// System.out.println("NO Units: " + policy.getUnitsToServe() +" Clevel "+  aNode.getCurrentLevel() + " Random Demand: " + randomDemand);
                   
                 }else{
                	 stockCosts = aNode.getRoundtripToDepotCosts();
                	  //System.err.println("STOCKOUT -- node " + i + " " +stockCosts + " " + surplus);
                	  //System.err.println("Units: " + policy.getUnitsToServe() +" Clevel "+  aNode.getCurrentLevel() + " Random Demand: " + randomDemand);
                	  totStocks++;
                 }
                    meanRandon += stockCosts;
                   
                    policy.setExpStockCosts(policy.getExpStockCosts() + stockCosts); // accum. stock costs
               }
           
            
            	policy.setExpSurplus(policy.getExpSurplus() / simIterStoch);
            	policy.setExpStockCosts(policy.getExpStockCosts() / simIterStoch);

            	totCost += policy.getExpStockCosts();
            	
        	}
        
      //System.out.println("Coste total:" + totCost + " " + "Nodos con stockOut " + totStocks );

        }
    
	public void SimulateDemandInit() {
		 int sss = aTest.getSeed(); 
		 int seedArray[] = {sss, sss, sss, sss};
	     LFSR113.setPackageSeed(seedArray);
		
		RandomStreamBase stream = new LFSR113(); // L'Ecuyer stream
		stream.resetStartSubstream();
		
		for (int i = 1; i < inputs.getNodes().length; i++)
		{
			Node aNode = inputs.getNodes()[i];
			// System.out.println("InitialStock before: " +
			// aNode.getCurrentLevel());

			// 1.1. Set the random distribution for this node.
			double mean = aNode.getExpDemand();
			double var = aTest.getK() * mean;
			double factor = Math.log(1 + var / (mean * mean));
			double mu = Math.log(mean) - 0.5 * factor;

			double sigma = Math.sqrt(factor);

			Distribution dist = new LognormalDist(mu, sigma);

			RandomVariateGen rngLogN = new LognormalGen(stream, (LognormalDist) dist);

			// 1.2. Perform simulation runs.

			long start = ElapsedTime.systemTime();

			double elapsed = 0.0;
			float meanRandon = 0;
			for (int j = 0; j < simIterStoch; j++)

			{

				// Generate random demand.
				float randomDemand = (float) rngLogN.nextDouble();

				// float randomDemand = (float) aNode.getExpDemand();

				// meanRandon += randomDemand;

				// Compute accum. surplus and stock costs.

				for (Policy policy : aNode.getPoliciesByRefill())

				{
					float surplus = policy.getUnitsToServe() + aNode.getCurrentLevel() - randomDemand;
					policy.setExpSurplus(policy.getExpSurplus() + surplus); // accumulated
																			// surplus
					double stockCosts;
					if (surplus >= -1) {
						stockCosts = aTest.getLambda() * Math.abs(surplus);
					} else {
						stockCosts = aNode.getRoundtripToDepotCosts();
					}
					
					meanRandon += stockCosts;
					policy.setExpStockCosts(policy.getExpStockCosts() + stockCosts); // accum.
																						// stock
																						// costs

				}

			}

                elapsed = ElapsedTime.calcElapsed(start, ElapsedTime.systemTime());

                // 1.3. Compute expected surplus and stock costs.

                for( Policy policy : aNode.getPoliciesByRefill() )
                {

                policy.setExpSurplus(policy.getExpSurplus() / simIterStoch);
                policy.setExpStockCosts(policy.getExpStockCosts() / simIterStoch);

                }
                // 1.4. Sort policies in aNode by Costs (policiesByRefill will not be modified).
                Arrays.sort(aNode.getPoliciesByCosts());
            }
        }
        
   	
    }
