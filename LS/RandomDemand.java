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
import alg.PairBest;
import alg.PairBestDiff;
import alg.BestSolutionsDiff2;
import alg.Policy;
import alg.RandCWS;
import alg.BestSolutions;
import alg.BestSolutionsDiff;
import alg.Edge;
import alg.Inputs;
import alg.Solution;
import alg.Test;

public class RandomDemand {
	
					
		public static Solution RandomDem(Inputs inputs, Solution baseSol,Test aTest, Random rng){
			
			float accDemand = 0;
			double stockCosts = 0;
			BestSolutionsDiff2 listBestSols = new BestSolutionsDiff2();
			
			for (int i = 1; i < inputs.getNodes().length; i++) // node 0 is depot							
			{
				Node aNode = inputs.getNodes()[i];
				Policy index = aNode.getBestSolPolicy(); //Indice del best policy
				
				Policy auxActivePolicy = aNode.getActivePolicy(); // auxiliar copy
				aNode.setActivePolicy(index); //pongo BestPolicy Como Active policy
				
				
			    Solution sol = new Solution(RandCWS.solve(aTest, inputs, rng, false)); //Calculo nueva solucion
			   
			    int factible = checkCap(sol , inputs.getVehCap());
				if(factible == 1){  //No factible 
					aNode.setActivePolicy(auxActivePolicy);
				} //Factible
				else{
					accDemand = baseSol.getDemandToServe() + index.getUnitsToServe() - auxActivePolicy.getUnitsToServe();
					stockCosts = baseSol.getStockCosts() + index.getExpStockCosts()- auxActivePolicy.getExpStockCosts();
					
					double diffCost =  (sol.getRoutingCosts() + stockCosts) - baseSol.getTotalCosts() ; 
					//System.out.println("nodo: " + aNode.getId() +" Entraaaaa " + diffCost);
					if (diffCost <0){
						PairBestDiff PairBest = new PairBestDiff(aNode.getId(),diffCost);
						listBestSols.addSolution(PairBest);
					}
					aNode.setActivePolicy(auxActivePolicy); //Pongo la politica como estaba
				}
			}
		
			//Cambio las politicas de los 5 nodos mas lejanos
			Iterator iterator;
	        iterator = listBestSols.getSolutions().iterator();
	        //System.out.println("Size: " + listBestSols.getSize());
	        
	        while(iterator.hasNext()){
	        	PairBestDiff PairBestDiff = (PairBestDiff) iterator.next();
	        	int node = PairBestDiff.getkey();
	        	double valor = PairBestDiff.getValue();
	        }
	        
	        
	        Map<Integer, Policy> BackOriginalSol = new HashMap<Integer, Policy>();
	        double OrgStock = 0;
	        float  OrgUnitsToServe = 0;
	        
	        double NewStock = 0;
	        float  NewUnitsToServe = 0;
			
	        //Hago los cambios de politicas
	        while(iterator.hasNext()){
	        	PairBestDiff PairBestDiff = (PairBestDiff) iterator.next();
	        	int node = PairBestDiff.getkey();
				Node aNode = inputs.getNodes()[node];
				BackOriginalSol.put(node, aNode.getActivePolicy()); //Guardo politica actual
				
			    OrgStock += aNode.getActivePolicy().getExpStockCosts();
			    OrgUnitsToServe += aNode.getActivePolicy().getUnitsToServe();

				Policy index = aNode.getBestSolPolicy(); //Indice del best policy
				aNode.setActivePolicy(index); //Activo mejor politica
				
		        NewStock += aNode.getActivePolicy().getExpStockCosts();
		        NewUnitsToServe += aNode.getActivePolicy().getUnitsToServe();
	        }
	        
			//Calculo la solucion
			Solution sol = new Solution(RandCWS.solve(aTest, inputs, rng, false)); //Calculo nueva solucion
				   
			int factible = checkCap(sol , inputs.getVehCap());
			if(factible == 1){  //No factible, lo pongo todo como estaba
					
				Iterator itHashMap;
				itHashMap = BackOriginalSol.keySet().iterator();
					
			    while(itHashMap.hasNext()){
			        	int node = (int) itHashMap.next();
						Policy OrinPol =  BackOriginalSol.get(node);
						Node aNode = inputs.getNodes()[node];
						aNode.setActivePolicy(OrinPol); //Activo politica original
			     }
			 }//Factible
			 else{
				accDemand = baseSol.getDemandToServe() + NewUnitsToServe - OrgUnitsToServe;
				stockCosts = baseSol.getStockCosts()  + NewStock - OrgStock;
				
				sol.setDemandToServe(accDemand);
				sol.setStockCosts(stockCosts);
				sol.setTotalCosts(sol.getStockCosts() + sol.getRoutingCosts());	
				baseSol = new Solution (sol);	
			 }
			
			return baseSol;
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