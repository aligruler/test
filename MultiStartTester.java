package alg;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130112
 */

public class MultiStartTester
{
    final static String inputFolder = "inputs";
    final static String outputFolder = "outputs";
    final static String testFolder = "tests";
    final static String fileNameTest = "test2run.txt"; //"test2run.txt";
    final static String sufixFileNodes = "_input_nodes.txt";
    final static String sufixFileVehicules = "_input_vehicles.txt";
    final static String sufixFileOutput = "_outputs.txt";
    
    

    public static void main( String[] args )
    {
    	ArrayList<Outputs> outList = new ArrayList<Outputs>();
    	
    	//ArrayList<Outputs> outListPeriod = new ArrayList<Outputs>();
    	
        ArrayList<refinedOutputList> refinedList = new  ArrayList<refinedOutputList>();
        
    	
    	System.out.println("****  WELCOME TO THIS PROGRAM  ****");
        long programStart = ElapsedTime.systemTime();
        
        /* 1. GET THE LIST OF TESTS TO RUN FORM "test2run.txt"
              aTest = instanceName + testParameters */
        String testsFilePath = testFolder + File.separator + fileNameTest;
        ArrayList<Test> testsList = TestsManager.getTestsList(testsFilePath);
       
        /* 2. FOR EACH TEST (instanceName + testParameters) IN THE LIST... */
        int nTests = testsList.size();
        System.out.println("Number of tests to run: " + nTests);
        
        for( int k = 0; k < nTests; k++ )
        {   Test aTest = testsList.get(k);
            //System.out.println("\n# STARTING TEST " + (k + 1) + " OF " + nTests);
        	System.out.println();	
        	System.err.println("NEXT TEST IS STARTED");
        	System.err.println("***********************");
        	System.out.println();
            System.out.print(aTest.getInstanceName() + "; K=" + aTest.getK() + 
                    "; L=" + aTest.getLambda());
            System.out.println();

            // 2.1 GET THE INSTANCE INPUTS (DATA ON NODES AND VEHICLES)
            // "instanceName_input_nodes.txt" contains data on nodes
            String inputNodesPath = inputFolder + File.separator +
                    aTest.getInstanceName() + sufixFileNodes;
            String inputVehPath = inputFolder + File.separator +
                    aTest.getInstanceName() + sufixFileVehicules;

            // Read inputs files (nodes) and construct the inputs object
            Inputs inputs = InputsManager.readInputs(inputNodesPath, inputVehPath);
            InputsManager.generateDepotEdges(inputs);
            InputsManager.generateSavingsList(inputs);
            
            // 2.2. USE THE MULTI-START ALGORITHM TO SOLVE THE INSTANCE
            Random rng = new Random(aTest.getSeed());
            
            Outputs output = null;
            if (aTest.getApproach() == 0){
                MultiStartSPupdateEnd algorithmSP = new MultiStartSPupdateEnd(aTest, inputs, rng);
                output = algorithmSP.solve();
            }
            else if(aTest.getApproach() == 1)
            {//integrated
            	MultiStartSPupdateEachP algorithm = new MultiStartSPupdateEachP(aTest, inputs, rng);
                output = algorithm.solve();
            }          
            else if(aTest.getApproach() == 2)
            {//integrated
            	MultiStartMPupdateEnd algorithm = new MultiStartMPupdateEnd(aTest, inputs, rng);
                output = algorithm.solve();
            }            
            else if(aTest.getApproach() == 3)
            {//integrated
            	MultiStartMPupdateEachP algorithm = new MultiStartMPupdateEachP(aTest, inputs, rng);
                output = algorithm.solve();
            }

        
            
            //refinedList.add(new refinedOutputList(aTest.getInstanceName(), algorithm.getOutList(), aTest.getK(), aTest.getLambda()));
            
           
            String outputsFilePath2 = outputFolder + File.separator +
                    aTest.getInstanceName() + "_" + "debug.txt";
            //output.debug(outputsFilePath2, output.getOBSol(), aTest, inputs);
            
            
            String outputsFilePath3 = outputFolder + File.separator +
                    aTest.getInstanceName() + "_" + "debug_map.gml";
            //output.printGMLOutput(outputsFilePath3, inputs);
            
            
            output.setInstanceName(aTest.getInstanceName());
            output.setLambda(aTest.getLambda());
            output.setK(aTest.getK());
            output.setRunningT(aTest.getMaxTime());
            output.setNumPeriods(aTest.getNumPeriods());
            ///output.setList(output);
            // 2.3. PRINT OUT THE RESULTS TO FILE "instanceName_seed_outputs.txt"
            String outputsFilePath = outputFolder + File.separator +
                   aTest.getInstanceName() + "_" + aTest.getSeed() + sufixFileOutput;
            output.sendToFile(outputsFilePath);
           //Outputs auxOut = new Outputs();
           //auxOut = output;
           output.setAlpha(aTest.getIrpBias());
           output.setBeta(aTest.getIrpBias());
           outList.add(k, output);
           //output.makeGlobalOutput(auxOut, k , nTests, outList);
        }
        
        //printRefinedList(refinedList);        
        printGlobalPeriod(outList);
        printGlobalOutput(outList);
        

        
        /* 3. END OF PROGRAM */
        System.out.println("****  END OF PROGRAM, CHECK OUTPUTS FILES  ****");
            long programEnd = ElapsedTime.systemTime();
            System.out.println("Total elapsed time = "
                + ElapsedTime.calcElapsedHMS(programStart, programEnd));
    }   
    
    
    public static void printGlobalOutput(ArrayList<Outputs> list){       
        ///Print Global Results
        try 
        {   
        float previous_lamda = -1;
        float previous_k = -1;
        float previous_numP = -1;
        float previous_app = -1;

        PrintWriter out = new PrintWriter("Globaloutput.txt");
    	   
        out.println("Global Solution");
    	out.println("***************************************************");
    	for (int i = 0; i < list.size(); i++){
    		if (previous_lamda != list.get(i).getLambda() || previous_k != list.get(i).getK() 
    				|| previous_numP != list.get(i).getNumPeriods()){
    			previous_lamda = list.get(i).getLambda();
    			previous_k = list.get(i).getK();
    			previous_numP = list.get(i).getNumPeriods();
    			previous_app = list.get(i).getApproach();
    			
    			out.println();
    			out.println("Lambda: " + list.get(i).getLambda());
    			out.println("Variance: " + list.get(i).getK());
    			out.println("Periods: " + list.get(i).getNumPeriods());
    			//out.println("Approach: " + list.get(i).getApproach());
    	    	out.println("Instance" + "\t" + "InitSol" + "\t"+ "Stock" + "\t" + "Routing" + "\t" + "Total Costs" + "\t" + "Det Costs" + "\t" + "Approach");    	    	 
    		}
    		//double totcostInit = list.get(i).getBestInitSol()[list.get(i).getBestInitSol().length-1].getAccTotCosts();
    		previous_lamda = list.get(i).getLambda();
    		previous_k = list.get(i).getK();
    		previous_numP = list.get(i).getNumPeriods();
    		previous_app = list.get(i).getApproach();

    		out.println(list.get(i).getInstanceName() + "\t" + list.get(i).getBestInitSol().getAccTotCosts() + "\t" + list.get(i).getOBSol().getAccStockCosts() + "\t" +list.get(i).getOBSol().getAccRoutingCosts() + "\t" + 
    				list.get(i).getOBSol().getAccTotCosts() + "\t" + list.get(i).getOBSol().getDetCosts() + "\t" + list.get(i).getApproach());	
    		
    		}//end for
   	
        	out.close();
        	
        } 
        catch (IOException exception) 
        {   System.out.println("Error processing output file: " + exception);
        }
    }
    
    
    public static void printRefinedList(ArrayList<refinedOutputList> list){
    	try 
        {   
        PrintWriter out = new PrintWriter("refinedOutput.txt");
    	
        out.printf("OriginalStock \t OriginalRouting \t OrignalTotal \t refinedRouting \t refinedTotal \t percentageDiffRout \t percentageDiffTot \n");
    	
        for (int i = 0; i < list.size(); i++){
    		ArrayList<Solution> singleList = list.get(i).getList();
    		out.printf("%s \t %.2f \t %.2f \n", list.get(i).getInstanceName(), list.get(i).getVariance(), list.get(i).getLambda());
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
    
    
    
    public static void printGlobalPeriod(ArrayList<Outputs> outList){
    	try 
    	{   
    		float previous_lamda = -1;
    		float previous_k = -1;
    		float previous_numP = -1;
    		float previous_app = -1;

    		PrintWriter out = new PrintWriter("GlobaloutputPeriod.txt");

    		out.println("Solution per period");
    		out.println("***************************************************");
    		
    		for (int i = 0; i < outList.size(); i++){

    			if (previous_lamda != outList.get(i).getLambda() || previous_k != outList.get(i).getK() ||
    					previous_numP != outList.get(i).getNumPeriods() || previous_app != outList.get(i).getApproach()){
    				previous_lamda = outList.get(i).getLambda();
    				previous_k = outList.get(i).getK();
    				previous_numP = outList.get(i).getNumPeriods();
    				previous_app = outList.get(i).getApproach();
    				out.println("Lambda: " + outList.get(i).getLambda());
    				out.println("Variance: " + outList.get(i).getK());
    				out.println("Periods: " + outList.get(i).getNumPeriods());
    				out.println("Approach: " + outList.get(i).getApproach());
        	    	
    				}
    			out.printf("%s \n", outList.get(i).getInstanceName());
    			out.println("InitSol Costs: " + outList.get(i).getBestInitSol().getAccTotCosts());
    			previous_lamda = outList.get(i).getLambda();
    			previous_k = outList.get(i).getK();
    			previous_numP = outList.get(i).getNumPeriods();
    			previous_app = outList.get(i).getApproach();
    			out.printf("%s \t %s \t %s \t %s \n", "Period", "Routing",  "Stock", "Total");  				
				
    			for (int j = 0; j < outList.get(i).getBestPerSol().length;j++){
    				out.printf("%d \t %.2f \t %.2f \t %.2f  \n", 
    			    	j, 
    			    	outList.get(i).getBestPerSol()[j].getRoutingCosts(),
    			    	outList.get(i).getBestPerSol()[j].getStockCosts(),
    			    	outList.get(i).getBestPerSol()[j].getTotalCosts()); 
    			}
    			 out.printf("%S \t %.2f \t %.2f \t %.2f \n", "Total",
    					 outList.get(i).getOBSol().getAccRoutingCosts(),
    					 outList.get(i).getOBSol().getAccStockCosts(),
    					 outList.get(i).getOBSol().getAccTotCosts());
    			 out.println();
    		}//end for

    		out.close();
    	} 
    	catch (IOException exception) 
    	{   System.out.println("Error processing output file: " + exception);
    	}

    }
    
}