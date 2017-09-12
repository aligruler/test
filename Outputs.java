package alg;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130112
 */
public class Outputs
{
    /* INSTANCE FIELDS & CONSTRUCTOR */
    private Solution bestInitSol;
    private Solution bestSol;
    private String instanceName;
    private float lambda;
    private float variance;
    private float runningTime;
    private double alpha; 
    private double beta;
    private int numPeriods;
    private int approach;
    private Solution[] bestPerSol;
    private ArrayList<Outputs> list = null;
    
    private ArrayList<ArrayList<Solution>> refinedList = null;
    	
    
    
    public void addArray (ArrayList<Solution> solList){
    	refinedList.add(solList);
    }
    
    public ArrayList<ArrayList<Solution>> getRefinedList (){
    	return refinedList;
    }
    
    public void setList(){
    	list.add(this);
    }
    
   
    public Outputs(int period, int app)
    {   bestInitSol = new Solution();
        bestPerSol = new Solution[period];
        bestSol = null;
        instanceName = null;
        lambda = 0;
        variance = 0;
        runningTime = 0;
        numPeriods = 0;
        approach = app;
    }

    /* SET METHODS */
    
    
    public void setBestInitSol(Solution aSol){bestInitSol = aSol;}
    
    public int getNumPeriods() {
		return numPeriods;
	}

	public void setNumPeriods(int numSols) {
		this.numPeriods = numSols;
	}

	public void setBestPerSol(Solution[] aSol){bestPerSol = aSol;}
    public Solution[] getBestPerSol(){return bestPerSol;}
    
    public void setOBSol(Solution obSol){bestSol = obSol;}
    public void setInstanceName(String name){instanceName = name;}
    public void setLambda(float lam){lambda = lam;}
    public void setK(float k){variance = k;}
    public void setRunningT(float t){runningTime = t;}
    
    
    public int getApproach() {
		return approach;
	}

	public void setApproach(int approach) {
		this.approach = approach;
	}

	public void printRefinedList(ArrayList<ArrayList<Solution>> list){
    	try 
        {   
        PrintWriter out = new PrintWriter("refinedOutput.txt");
    	out.printf(" OriginalStock \t OriginalRouting \t OrignalTotal \t refinedRouting \t refinedTotal \t percentageDiffRout \t percentageDiffTot \n");
    	for (int i = 0; i < list.size(); i++){
    		ArrayList<Solution> singleList = refinedList.get(i);
    		for (int j = 0; j < singleList.size(); j++){
    			Solution currentSol = singleList.get(j);
    			double averageDiffTot = 100*( (currentSol.getRefinedTotCosts() - currentSol.getTotalCosts()) / currentSol.getTotalCosts());
    			double averageDiffRout = 100*( (currentSol.getRefinedRoutingCosts() - currentSol.getRoutingCosts()) / currentSol.getRoutingCosts());
    			out.printf("%.2f \t %.2f \t%.2f \t%.2f \t%.2f \t%.2f \t%.2f \n",
    					currentSol.getStockCosts(), currentSol.getRoutingCosts(), currentSol.getTotalCosts(), 
    					currentSol.getRefinedRoutingCosts(), currentSol.getRefinedTotCosts(), averageDiffRout,  averageDiffTot);
    		}
    		
    		
        	}//end for
        	out.close();
        } 
        catch (IOException exception) 
        {   System.out.println("Error processing output file: " + exception);
        }
    }//end method
    
    
    /* GET METHODS */
    public Solution getBestInitSol(){return bestInitSol;}
    public Solution getOBSol(){return bestSol;}
    public String getInstanceName(){return instanceName;}
    public float getLambda(){return lambda;}
    public float getK(){return variance;}
    public float getRunningT(){return runningTime;}

    /* AUXILIARY METHODS */
    public void makeGlobalOutput(Outputs output, int j, int tests, ArrayList<Outputs> list)
    {
    	list.add(j, output);
    	
    	if (j == tests-1){
        try 
        {   
        PrintWriter out = new PrintWriter("Globaloutput.txt");
    	out.println("Global Solution");
    	out.println("***************************************************");
    	for (int i = 0; i < list.size(); i++){
    		out.println("Result " + i + " of " + list.size());
    		out.println("Name: " + list.get(i).getInstanceName());
    		out.println("Lambda " + list.get(i).getLambda());
    		out.println("Variance " + list.get(i).getK());
    		out.println("Running Time " + list.get(i).getRunningT());
        	out.println("--------------------------------------------");
        	out.println("INTISOL");
        	out.println(list.get(i).getBestInitSol().toString());
        	out.println("**********************************************");
        	out.println("BESTSOL");
        	out.println(list.get(i).getOBSol().toString());
        	out.println();
        	}//end for
        	out.close();
        	
        } 
        catch (IOException exception) 
        {   System.out.println("Error processing output file: " + exception);
        }
    	}
    }
    
    
    public void sendToFile(String outFile)
    {
        try 
        {   PrintWriter out = new PrintWriter(outFile);
            out.println("***************************************************");
            out.println("*                      OUTPUTS                    *");
            out.println("***************************************************");
            out.println("\r\n");
            out.println(outFile);
            out.println("\r\n");
            out.println("--------------------------------------------");
            out.println("BEST INITIAL SOLUTION");
            out.println("--------------------------------------------");
            out.println(bestInitSol.toString() + "\r\n");
            out.println("--------------------------------------------");
            out.println("\r\n OUR BEST SOLUTION:\r\n");
            out.println("--------------------------------------------");
            out.println(bestSol.toString() + "\r\n");
            out.close();
        } catch (IOException exception) 
        {   System.out.println("Error processing output file: " + exception);
        }
    }

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}



	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}
	

	 public void debug(String outFile, Solution sol, Test test, Inputs input)
	    {
	        try 
	        {   PrintWriter out = new PrintWriter(outFile);
	        	
	           out.printf("Vehicle Capacity: \t%.2f \n",input.getVehCap());
	           out.printf("Sol Routing Costs: \t%.2f \n",sol.getRoutingCosts());
	           out.printf("Sol Stock Costs: \t%.2f \n",sol.getStockCosts());
	           out.printf("Sol Total Costs: \t%.2f \n\n",sol.getTotalCosts());
	           float servedIS =0;
	           float servedShould = 0;


	        
	            for(int i = 0; i < sol.getRoutes().size();i++){
	            	out.printf("Route %d \n",i);
	            	out.printf("origin \tEnd \tEdgeCost \tInitialStock \tMaxStock  \t policy  \tservedDemandIS \tservedDemandSHOULD\n");
	 	            float  totservedIS = 0;
		            float  totservedShould = 0;
		            
	            	for(int j =0; j < sol.getRoutes().get(i).getEdges().size(); j++ ){
	            		Edge tE = sol.getRoutes().get(i).getEdges().get(j);

	            		
	            		if(tE.getEnd().getId() != 0 ){
	            			float demandShould = tE.getEnd().getMaxCap() * tE.getEnd().getActivePolicy().getRefillUpToPercent() - tE.getEnd().getCurrentLevel();
		            		totservedIS += tE.getEnd().getUnitsToServe();
		    	            totservedShould += demandShould;
	            			
	            			servedIS += tE.getEnd().getUnitsToServe();
	            			servedShould += demandShould;
	            			
	            		 out.printf("%d \t%d \t%.2f  \t%.2f   \t%.2f \t%.2f  \t %.2f \t %.2f\n",
	            			tE.getOrigin().getId(),
	            			tE.getEnd().getId(), 
	            			tE.getCosts(),
	            			tE.getEnd().getCurrentLevel(),
	            			tE.getEnd().getMaxCap(),
	            			tE.getEnd().getActivePolicy().getRefillUpToPercent(),
         				tE.getEnd().getUnitsToServe(), 
         				demandShould);	
	            		}else{
	            			out.printf("%d \t%d \t%.2f  \t   \t \t  \t%.2f \t%.2f\n",
	            		      tE.getOrigin().getId(),
	 	            		  tE.getEnd().getId(), 
	 	            		  tE.getCosts(),
	 	            		  totservedIS,
	 	            		 totservedShould); 
	            		}	

	            	}
	            	if(totservedIS > 100){
	            		System.err.println("Errrorrrrr en instancia");
	            		System.exit(0);
	            		//System.exit(-1);
	            	}
	            	out.printf("\n\n");
	            		            	
	            	
	            }
	            
	            out.printf("Total servedIS \tTotal servedShould \n");
	            out.printf("%.2f  \t%.2f \n",servedIS, servedShould);
	            if (servedIS != servedShould){
	            	System.err.println("ERRORRRRR EN INSTANCIA ");
	            	//System.exit(-1);
	            }
	            
	            servedIS = 0;
		        servedShould = 0;
	            out.printf("\n\n");
	            
         	out.printf("Non Served nodes\n");
         	out.printf("ID \tInitialStock \tMaxStock  \tpolicy \tservedDemandIS  \tservedDemandSHOULD\n");

         	for(int j = 0; j <sol.getNonServedNodes().size(); j++){
         		float demandShould = sol.getNonServedNodes().get(j).getMaxCap() * 
         							 sol.getNonServedNodes().get(j).getActivePolicy().getRefillUpToPercent() - 
         							 sol.getNonServedNodes().get(j).getCurrentLevel();
         		
          		 out.printf("%d \t%.2f  \t%.2f   \t%.2f \t%.2f	\t%.2f \n",
          				sol.getNonServedNodes().get(j).getId(),
	            			sol.getNonServedNodes().get(j).getCurrentLevel(),
	            			sol.getNonServedNodes().get(j).getMaxCap(),
	            			sol.getNonServedNodes().get(j).getActivePolicy().getRefillUpToPercent(),
	            			sol.getNonServedNodes().get(j).getUnitsToServe(), 
	            			demandShould );	

         	}
	            
	            
	            out.close();
	        } catch (IOException exception) 
	        {   System.out.println("Error processing output file: " + exception);
	        }
	    }
	
	
	

	 
	 public void printGMLOutput(String outFile, Inputs inputs) {
			try {
				double zoomFactor = 0.1;
				// colours per route
				// It can support until 20 distinct colours per route
				LinkedList<String> my_colors = new LinkedList<>();
				my_colors.add("#FF0000");
				my_colors.add("#00FF00");
				my_colors.add("#0000FF");
				my_colors.add("#FFFF00");
				my_colors.add("#00FFFF");
				my_colors.add("#FF00FF");
				my_colors.add("#5E5E5E");
				my_colors.add("#000080");
				my_colors.add("#000000");
				my_colors.add("#008000");
				my_colors.add("#008080");
				my_colors.add("#800080");
				my_colors.add("#800000");
				my_colors.add("#643200");
				my_colors.add("#C86400");
				my_colors.add("#964B00");
				my_colors.add("#40E0D0");
				my_colors.add("#D2B48C");
				my_colors.add("#E6E6FA");
				my_colors.add("#FFDAB9");
				// System.out.println("Ruta en el GRAFICAR");
				PrintWriter out = new PrintWriter(outFile);
				out.println("graph [ hierarchic 1 directed 1");
				int route_colour = 0;
				Node[] nodes = inputs.getNodes();
				
				// print depot
				out.print("node [ id " + nodes[0].getId() + " graphics [ x "
						+ nodes[0].getX() / zoomFactor + " y "
						+ nodes[0].getY() / zoomFactor);
				out.println(" w 11  h 11 type \"rectangle\" fill  \"#FF6600\"] LabelGraphics [text \""
						+ nodes[0].getId() + "\" fontSize 7 ] ]");
				

				
				for (int i = 1; i < nodes.length; i++) {
					// print nodes
						out.print("node [ id " + nodes[i].getId()
								+ " graphics [ x " + nodes[i].getX()
								/ zoomFactor + " y " + nodes[i].getY()
								/ zoomFactor);
						out.println(" w 11  h 11 type \"roundrectangle\"] LabelGraphics [text  \""
								+ nodes[i].getId() + "\" fontSize 7 ] ]");

				}
				
				
				// print routes
				for (Route r : bestSol.getRoutes()) {
					List<Edge> e = r.getEdges();
					for (int i = 0; i < e.size(); i++) {
						out.print("edge [ source " + e.get(i).getOrigin().getId()
								+ " target " + e.get(i).getEnd().getId());
						out.println(" graphics [ fill \""
								+ my_colors.get(route_colour % 20)
								+ "\" targetArrow \"standard\" ] ]");
					}
					route_colour++;
				}
				out.println("]");
				out.close();
			} catch (IOException exception) {
				System.out.println("Error processing output file: " + exception);
			}
		}	 
	 
	
	
	
}