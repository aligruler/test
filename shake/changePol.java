package shake;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import alg.Edge;
import alg.Inputs;
import alg.Node;
import alg.Policy;
import alg.Test;
import alg.Solution;
import alg.RandCWS;
import alg.Outputs;


public class changePol extends shake
{
	
    public changePol(Test test,Random rng) {
		super(test,rng);
		// TODO Auto-generated constructor stub
	}


    @Override
    public Solution shakeIt(Solution solInp, Inputs inp) {
		float accDemand = 0;
		double stockCosts = 0;
		
		for (int i = 1; i < inp.getNodes().length; i++) // node 0 is depot							
		{
			Node aNode = inp.getNodes()[i];
			//Policy index = aNode.getBestSolPolicy();
			int newPolicy = rng.nextInt(inp.getNodes()[i].getPoliciesByRefill().length-1);
			Policy index = aNode.getRandomPolicy(newPolicy);
			
			
			
			Policy auxActivePolicy = aNode.getActivePolicy(); // auxiliar copy
			aNode.setActivePolicy(index);
			
			
		    Solution sol = new Solution(RandCWS.solve(aTest, inp, rng, true)); ///AQUIIIIII
		    int factible = checkCap(sol , inp.getVehCap());
			if(factible == 1){
				aNode.setActivePolicy(auxActivePolicy);
				
			}
			else{
				accDemand = solInp.getDemandToServe() + index.getUnitsToServe() - auxActivePolicy.getUnitsToServe();
				stockCosts = solInp.getStockCosts() + index.getExpStockCosts()- auxActivePolicy.getExpStockCosts();
				
				
				if(solInp.getTotalCosts()  > (sol.getRoutingCosts() + stockCosts) )
				{
					sol.setDemandToServe(accDemand);
					sol.setStockCosts(stockCosts);
					sol.setTotalCosts(sol.getStockCosts() + sol.getRoutingCosts());	
					//System.out.println("nueva sol " + sol.getTotalCosts());
					solInp = new Solution(sol); 
				}else{
					aNode.setActivePolicy(auxActivePolicy);
				}

			}
		}

		return solInp;
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
