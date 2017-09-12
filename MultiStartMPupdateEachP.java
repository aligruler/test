package alg;

import shake.Suffle;
import shake.changePol;
import shake.spliting;
import shake.shake;
import shake.splitGeo;
import LS.BestPolicy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.rng.RandomStreamBase;
import umontreal.ssj.probdist.Distribution;
import umontreal.ssj.probdist.LognormalDist;
import umontreal.ssj.randvar.LognormalGen;
import umontreal.ssj.randvar.RandomVariateGen;/**
 * Iteratively calls the RandCWS and saves the best solution.
 * 
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 13810
 */
public class MultiStartMPupdateEachP {
	/* 0. Instance fields and class constructor */
	public static Test aTest;
	public static Inputs inputs; // contains savingsList too
	public static Random rng;
	
	private Solution[] initialSolsPerPolicy = new Solution[6]; // (0):0% (1):25% ...// (5):decent.
	private Solution initialSolAllPeriods = null;
	private Solution bestSolAllPeriods = null;
	private Solution baseSolAllPeriods = null;
	private Solution newSolCurrentPeriod = null;
	private Solution baseSolCurrentPeriod = null;
	private Solution[] initSolPerPeriod;
	private Solution[] vnsPeriodSols;
	private Solution[] bestSolPerPeriod;
	
	private Outputs outputs;
	RandomStreamBase stream = new LFSR113(); // L'Ecuyer stream
	Node[] auxTempNodes;
	List<Double> inventoryCost = new ArrayList<>();
	List<Double> routingCost = new ArrayList<>();
	ArrayList<Solution> outList = new ArrayList<Solution>();
	ArrayList<stockInfo[]> stockInfo = new ArrayList<stockInfo[]>();
	Inputs InputsInitVector[] = null;
	final static String outputFolder = "outputs";


	MultiStartMPupdateEachP(Test myTest, Inputs myInputs, Random myRng) {
		aTest = myTest;
		inputs = myInputs;
		rng = myRng;
	}

	public Outputs solve() {

		outputs = new Outputs(aTest.getNumPeriods(),aTest.getApproach());
		//BestSolutionsDiff listBestSols = new BestSolutionsDiff();
		RouteCache cache = new RouteCache();
		Inputs auxInitInput = null;
		
		auxInitInput = (Inputs) deepClone(inputs); // Inputs original
		
		InputsInitVector = new Inputs[aTest.getNumPeriods()];
		
		initSolPerPeriod = new Solution[aTest.getNumPeriods()];
		vnsPeriodSols = new Solution[aTest.getNumPeriods()];
		bestSolPerPeriod = new Solution[aTest.getNumPeriods()];

		/* 1. COMPUTE THE BEST INITIAL STRATEGY for N periods. */
				
		initialSolAllPeriods = initSolMultiPeriod(aTest.getNumPeriods(), initSolPerPeriod, InputsInitVector);
		outputs.setBestInitSol(initialSolAllPeriods);
		System.out.println("INITIAL SOL: " + initialSolAllPeriods.getAccTotCosts());

		/* 2. VNS algorithm. */
		
		auxTempNodes = new Node[inputs.getNodes().length];
		Inputs auxInput = null;
				
		bestSolAllPeriods = new Solution(initialSolAllPeriods);		
		baseSolAllPeriods = new Solution(initialSolAllPeriods);
		
		
		for(int k = 0; k < aTest.getNumPeriods();k++){
			bestSolPerPeriod[k] = new Solution(initSolPerPeriod[k]);
		} 
		
		Boolean firstime = true;

		List<shake> nlist = loadNeigh();
		
		inputs = (Inputs) deepClone(InputsInitVector[0]);
		auxInitInput = (Inputs) deepClone(InputsInitVector[0]); 
		 
		int shakingNum = 0;
		long start = ElapsedTime.systemTime();
		double elapsed = 0.0;
		
		baseSolCurrentPeriod = new Solution(initSolPerPeriod[0]);
		Solution baseSolaux = new Solution(initSolPerPeriod[0]);		
		
		int iteration = 0;
		
		
		while (iteration < aTest.getSimRuns()*aTest.getNumPeriods()) {			
		//while (elapsed < (aTest.getMaxTime()*aTest.getNumPeriods())) {
			Inputs InputsVector[] = null;
			InputsVector = new Inputs[aTest.getNumPeriods()];

			double routingCostsTotal = 0.0;
			double stockCostsTotal = 0.0;
			
			for (int p = 0; p < aTest.getNumPeriods(); p++) { // Periods
				
				if (p > 0) {
					Simulation Sim = new Simulation(aTest, inputs);
					Sim.SimulateDemandInit();
					InitialSol initSol = new InitialSol(aTest, inputs, rng);
					int indexBestInitSol = initSol.makeInitSol();
					baseSolCurrentPeriod = new Solution(initSol.constructInitialSols(indexBestInitSol));
				} else {
					baseSolaux = new Solution(baseSolCurrentPeriod);
				}

				/* shaking and LS*/
				shake Shaking = nlist.get(shakingNum);
				newSolCurrentPeriod = new Solution(Shaking.shakeIt(baseSolCurrentPeriod, inputs));

				newSolCurrentPeriod = localSearch(inputs, newSolCurrentPeriod, aTest, rng, cache);
				newSolCurrentPeriod = cache.improve(newSolCurrentPeriod, inputs);
 				
				routingCostsTotal += newSolCurrentPeriod.getRoutingCosts();
				stockCostsTotal += newSolCurrentPeriod.getStockCosts();				

				vnsPeriodSols[p] = new Solution(newSolCurrentPeriod);
				InputsVector[p] = (Inputs) deepClone(inputs);
								
    		    String outputsFilePath2 = outputFolder + File.separator + aTest.getInstanceName() + "_" + p + "_" + "debug.txt";
    		    outputs.debug(outputsFilePath2, vnsPeriodSols[p], aTest, inputs);

				updateCurrentLevel();
			}// end for periods
			
			baseSolAllPeriods.setAccRoutingCosts(routingCostsTotal);
			baseSolAllPeriods.setAccStockCosts(stockCostsTotal);
			baseSolAllPeriods.setAccTotCosts(routingCostsTotal + stockCostsTotal);

			if (baseSolAllPeriods.getAccTotCosts() < bestSolAllPeriods.getAccTotCosts()) {
				shakingNum = 0;
				baseSolCurrentPeriod = new Solution(vnsPeriodSols[0]);
				auxInput = (Inputs) deepClone(InputsVector[0]);
				inputs = (Inputs) deepClone(InputsVector[0]);

				bestSolAllPeriods = new Solution(baseSolAllPeriods);
				
				for (int j = 0; j < aTest.getNumPeriods(); j++) {
					bestSolPerPeriod[j] = new Solution(vnsPeriodSols[j]);
				}
				
				System.err.println(bestSolAllPeriods.getAccTotCosts());
				bestSolAllPeriods.setTime(elapsed);
				firstime = false;
			} 
			else {
				shakingNum = (shakingNum < 3) ? (shakingNum + 1) : 0;
				if (shakingNum == nlist.size()){
					Collections.shuffle(nlist, rng);
				}
				baseSolCurrentPeriod = new Solution(baseSolaux);
				if (firstime == false) {
					inputs = (Inputs) deepClone(auxInput);
				} else {
					inputs = (Inputs) deepClone(auxInitInput);
				}
			}
			elapsed = ElapsedTime.calcElapsed(start, ElapsedTime.systemTime());
			
			iteration++;
		} // end while VNS
				
		System.out.println("BEST SOLUTION IS FOUND: " + bestSolAllPeriods.getAccTotCosts());
		
		outList.add(bestSolAllPeriods);
		outputs.setBestPerSol(bestSolPerPeriod);
		outputs.setOBSol(bestSolAllPeriods);
		
		return outputs;
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
		
	
	public void printStockSimCosts(ArrayList<simPairsList> list, Test aTest) {
		try {
			PrintWriter out = new PrintWriter(
					aTest.getInstanceName() + "_" + aTest.getK() + "_" + aTest.getLambda() + "_simStockResults.txt");
			out.printf("BestSolNr \t SimRun \t expStockCosts \t expStockOutCosts \t totStockCost \n");
			for (int i = 0; i < list.size(); i++) {
				simPairsList current = list.get(i);
				out.printf("%d \t %d \t %.2f \t %.2f \t %.2f \n", current.getBestSolNr(), current.getSimRun(),
						current.getsStockCosts(), current.getStockOut(),
						(current.getsStockCosts() + current.getStockOut()));
			}
			out.close();
		}

		catch (IOException exception) {
			System.out.println("Error processing output file: " + exception);
		}
	}

	/////
	public void printStockCosts(ArrayList<stockInfo[]> list, Test aTest) {
		try {
			PrintWriter out = new PrintWriter(
					aTest.getInstanceName() + "_" + aTest.getK() + "_" + aTest.getLambda() + "_StockResults.txt");
			out.printf(
					"Period\t Initial Stock\t RefillUpToPercent\t MaxCap\t UnitToServeIS\t UnitsToServeShould\t Random Demand\t StockNPerior \n");
			for (int i = 0; i < list.size(); i++) {
				stockInfo[] current = list.get(i);
				out.printf("Node: %d\n", i);
				for (int j = 0; j < current.length; j++) {
					out.printf("%d\t %.2f\t %.2f\t %.2f\t %.2f\t %.2f \t \t \t \t\n", current[j].getPeriod(),
							current[j].getCurrentLevel(), current[j].getRefillUpToPercent(), current[j].getMaxcap(),
							current[j].getUnitsToserve(), current[j].getExpDemand());
				}
				out.printf("\n");

			}
			out.close();
		}

		catch (IOException exception) {
			System.out.println("Error processing output file: " + exception);
		}
	}

	/////
	public static Solution initSolMultiPeriod(int nPeriods, Solution[] initSolPerPeriod, Inputs[] InputsInitVector) {
		Solution initialSolSP = null;
		double routingCostsInitSol = 0;
		double stockCostsInitSol = 0;

		for (int i = 0; i < nPeriods; i++) { // Periods

			// 1. USE SIMULATION TO ESTIMATE EXP SURPLUS AND EXP STOCK COSTS. */
			Simulation Sim = new Simulation(aTest, inputs);
			Sim.SimulateDemandInit();

			/* 2. COMPUTE THE BEST INITIAL STRATEGY. */
			InitialSol initSol = new InitialSol(aTest, inputs, rng);
			int indexBestInitSol = initSol.makeInitSol();

			// Initial solution
			initialSolSP = initSol.constructInitialSols(indexBestInitSol);

			routingCostsInitSol += initialSolSP.getRoutingCosts();
			stockCostsInitSol += initialSolSP.getStockCosts();
			

			InputsInitVector[i] = (Inputs) deepClone(inputs); // Inputs original
			initSolPerPeriod[i] = new Solution(initialSolSP);
			// initialPeriodSols[i] = initialSol;
			// Actualizo current level antes de pasar al siguiente periodo
			updateCurrentLevel();
		}
		
		initialSolSP.setAccRoutingCosts(routingCostsInitSol);
		initialSolSP.setAccStockCosts(stockCostsInitSol);
		initialSolSP.setAccTotCosts(routingCostsInitSol + stockCostsInitSol);
		
		System.out.println("InitSol. Routing Cost :" + routingCostsInitSol);
		System.out.println("InitSol. Stock Cost  :" + stockCostsInitSol);
		return initialSolSP;
	}

	public static void updateCurrentLevel() {

		for (int jj = 1; jj < inputs.getNodes().length; jj++) {
			Node currentN = inputs.getNodes()[jj];
			float expSurplus = currentN.getActivePolicy().getExpSurplus();
			// System.out.println("Current Level After: " + expSurplus);
			if (expSurplus < 0) {
				currentN.setCurrentLevelNewPeriod(0);
			} else {
				currentN.setCurrentLevelNewPeriod(expSurplus);
			}
			currentN.updateNodes();
		}

	}

	public static void updateCurrentLevelInitPeriod(float[] obtainInitialCurrentLevel) {

		for (int jj = 1; jj < inputs.getNodes().length; jj++) {
			Node currentN = inputs.getNodes()[jj];
			currentN.setCurrentLevelNewPeriod(obtainInitialCurrentLevel[jj - 1]);
			currentN.updateNodes();
		}

	}

	public void printRefinedSols(BestSolutions bestSols, Test aTest) {
		try {
			PrintWriter out = new PrintWriter(
					aTest.getInstanceName() + "_" + aTest.getK() + "_" + aTest.getLambda() + "_Refined.txt");
			bestSols.getSolutions().size();
			Iterator iterator = bestSols.getSolutions().iterator();
			// PairBest pairIter = (PairBest) iterator.next();
			while (iterator.hasNext()) {
				PairBest pairIter = (PairBest) iterator.next();
				Solution sol = pairIter.getkey();
				// System.out.println(sol.getRoutingCosts());
				out.printf(
						"Stock \t %.2f \t Routing \t %.2f \t Total \t %.2f \t RoutingRefined \t %.2f \t TotalRefined \t %.2f \n",
						sol.getStockCosts(), sol.getRoutingCosts(), sol.getTotalCosts(), sol.getRefinedRoutingCosts(),
						sol.getRefinedTotCosts());
			}
			out.close();
		}

		catch (IOException exception) {
			System.out.println("Error processing output file: " + exception);
		}
	}

	/* AUXILIARY METHODS */
	private Solution constructInitialSols(int index) {
		double stockCosts = 0.0;
		float accDemand = 0.0f;
		for (int i = 1; i < inputs.getNodes().length; i++) // node 0 is depot
		{
			Node aNode = inputs.getNodes()[i];
			if (index == 5) // decentralized, use policiesByCosts
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

	public static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<shake> loadNeigh() {
		List<shake> list = new LinkedList<shake>();
		list.add(new Suffle(aTest, rng));
		list.add(new spliting(aTest, rng));
		list.add(new changePol(aTest, rng));
		list.add(new splitGeo(aTest, rng));
		return list;
	}

	public ArrayList<Solution> getOutList() {
		return outList;
	}

	public static int getRandomIndex(int a, int b) {
		int newIndex = ThreadLocalRandom.current().nextInt(a, b);
		return newIndex;
	}

	public Solution localSearch(Inputs inputs, Solution baseSol, Test aTest, Random rng, RouteCache cache) {
		boolean improved = true;

		improved = true;
		while (improved) {
			double beforeCost = baseSol.getTotalCosts();
			// baseSol = LS.RandomizationPolicy.RandoPolicy(inputs, baseSol,
			// aTest, rng);
			baseSol = LS.BestPolicy.ChangePolicy(inputs, baseSol, aTest, rng);
			double AfterCost = baseSol.getTotalCosts();
			improved = beforeCost - AfterCost > 0.001;
		}

		improved = true;
		while (improved) {
			double beforeCost = baseSol.getTotalCosts();
			// baseSol = LS.BestPolicy.ChangePolicy(inputs, baseSol, aTest,
			// rng);
			baseSol = LS.RandomizationPolicy.RandoPolicy(inputs, baseSol, aTest, rng);
			double AfterCost = baseSol.getTotalCosts();
			improved = beforeCost - AfterCost > 0.001;
		}

		/*
		 * improved = true; while (improved) { double beforeCost =
		 * baseSol.getTotalCosts(); //baseSol =
		 * LS.RandomizationPolicy.RandoPolicy(inputs, baseSol, aTest, rng);
		 * baseSol = LS.RandomDemand.RandomDem(inputs, baseSol, aTest, rng);
		 * double AfterCost = baseSol.getTotalCosts(); improved = beforeCost -
		 * AfterCost > 0.001; }
		 */

		// }

		return baseSol;
	}

	public void printAllSol() {
		try {

			PrintWriter out = new PrintWriter("Pareto.txt");

			out.println("***************************************************");
			out.println("Inventory" + "\t" + "Routing");

			Iterator<Double> itInvent = inventoryCost.iterator();
			Iterator<Double> itRout = routingCost.iterator();

			while (itInvent.hasNext() && itRout.hasNext()) {
				out.println(itInvent.next() + "\t" + itRout.next());
			}
			out.close();

		} catch (IOException exception) {
			System.out.println("Error processing output file: " + exception);
		}
	}
}
