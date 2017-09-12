package alg;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.rng.RandomStreamBase;
import umontreal.ssj.probdist.Distribution;
import umontreal.ssj.probdist.LognormalDist;
import umontreal.ssj.randvar.LognormalGen;
import umontreal.ssj.randvar.RandomVariateGen;
/***********************************************************************************
 * Project SimORouting - AlgorithmSplit.java
 * 
 * This class encapsulates the splitting methodology. 
 * 
 * Date of last revision (YYMMDD): 090228
 * (C) Angel A. Juan - ajuanp(@)gmail.com
 **********************************************************************************/

public class AlgorithmSplit 
{
	/******************************************************************************* 
     * INSTANCE FIELDS 
     * ****************************************************************************/
	
	private int firstPolicy = 57; // First splitting policy
	private int lastPolicy = 60; // Last splitting policy
	private ArrayList <Edge> savingsList; // Original savingsList
	
	private float[] vrpCenter; // (x-bar, y-bar) is a geometric center for the VRP 
	private float vrpCenterX; // x-bar = mean of x[i]
	private float vrpCenterY; // y-bar = mean of y[i]

	private LinkedList<Route> frontRoutes; // list of routes satisfying policy	
	private LinkedList<Route> backRoutes; // routes not satisfying split policy
	private LinkedList<Node> frontNodes; // nodes in frontRoutes
	private Solution frontSubSol; // sub-solution derived from the frontRoutes
	private Solution backSubSol; // vrpSol = union(frontSubSol, backSubSol)
	

    /******************************************************************************* 
     * CLASS CONSTRUCTOR 
     * ****************************************************************************/
	
	public AlgorithmSplit(Solution Sol, Inputs input)
	{	
		// Set some instance fields
		Inputs inp = input;
		LinkedList<Route> routesSol = new LinkedList<Route>(Sol.getRoutes());

		LinkedList<Node> nodesSol = getNodes(routesSol);
		nodesSol.add(0, inp.getNodes()[0]);

		List<Edge> edges = InputsManager.generateSavingsList(nodesSol);	
		savingsList = new ArrayList<Edge>(edges);

		/***************************************************************************
	     * GET THE (X-BAR, Y-BAR) COORDINATES FOR A CENTRAL VRP POINT
	     **************************************************************************/
		vrpCenter = calcGeometricCenter(nodesSol);  
    	vrpCenterX = vrpCenter[0]; // x-bar coordinate 
		vrpCenterY = vrpCenter[1]; // y-bar coordinate
	}

	
	 /******************************************************************************
     * PUBLIC METHOD improveSolUsingSplitting()
     ******************************************************************************/
    
    public Solution improveSolUsingSplitting(Solution vrpSol, int nIterPerSplit,Test aTest, Inputs inp, Random rng)
    {    		
    	
    	// 1. Initialize dynamic currentVRPSol variable
    	Solution currentVRPSol = vrpSol;
    	
    	// 2. For each possible splitting policy, split and solve
    	for ( int policy = firstPolicy; policy <= lastPolicy; policy++ )
    	{	
    		// 2.1. Construct a list with all routes in the currentVRPSol;
    		LinkedList<Route> allRoutes = currentVRPSol.getRoutes();
    		
    		// 2.2. Set a geometric center for each route in the list
    		setRoutesGeometricCenters(allRoutes);
    		
    		// 2.3. Split allRoutes in two subsets, 
    		// (frontRoutes will be the ones we will try to improve next, but
    		//  we also need to know which are the complementary backRoutes)
    		frontRoutes = calcFrontRoutes(allRoutes, policy);
    		backRoutes = calcBackRoutes(frontRoutes, allRoutes);
    		
    		// 2.4. Continue this process only if there are enough routes
    		if ( frontRoutes.size() > 1 && backRoutes.size() > 0 )
    		{
    			// 2.5. Construct the initial complementary sub-solutions
    			frontSubSol = calcSubSol(frontRoutes);
    			backSubSol = calcSubSol(backRoutes);
    			
    			// 2.6. Get lists of nodes associated with the selected routes
    			frontNodes = getNodes(frontRoutes);
    			frontNodes.add(0, inp.getNodes()[0]);
    		
    			// 2.7. Calculate splitSavingsList from original savingsList
    			//  (notice that the resulting sublist will be already sorted)
    			ArrayList<Edge> splitSavingsListF = new ArrayList<Edge>();
    			for ( int i = 0; i < savingsList.size(); i++ )
    			{
    				Edge iEdge = savingsList.get(i);
    				Node iEdgeOrigin = iEdge.getOrigin();
    				Node iEdgeEnd = iEdge.getEnd();
    				// Construct frontNodes list
    				if ( iEdgeOrigin.getId() < iEdgeEnd.getId() &&
    						frontNodes.contains(iEdgeOrigin) && 
    						frontNodes.contains(iEdgeEnd) )
    					splitSavingsListF.add(iEdge);	
    			}
    			
    			// 2.8. Start a random search process to improve frontSubSol
    			int nIterF = nIterPerSplit; // * frontRoutes.size();
    			for ( int i = 1; i <= nIterF; i++ )
    			{
    				// 2.8.1. Get a random solution based on the Random CWS	
 
    			Solution newSubSolF = RandCWS.solve(aTest, splitSavingsListF, frontNodes, rng, true, inp.getVehCap());   				
    				
    				// 2.8.3. If appropriate, update frontSubSol
    				if ( newSubSolF.getRoutingCosts()   < frontSubSol.getRoutingCosts())
    					frontSubSol = newSubSolF;
    			}
    		
    			// 2.9. Try a quick improvement of backSubSol
    			//  (notice that improveRoutesUsingHashTable has been already 
    			//   applied to this set of routes in the SRGCWSCS.java)
    			// backSubSol = rc.improveRoutesUsingHashTable(backSubSol);
    			//backSubSol = rc.improveAreaUsingHashTable(backSubSol); //No la tengo lo comento
    			
    			// 2.10. Re-construct the full VRP solution by using frontSubSol
    			Solution newSol = unifySubSols(frontSubSol, backSubSol);
    		
    			// 2.11. If appropriate, update currentVRPSol
    			
    			
    			LinkedList<Route> newRoutes = new LinkedList<Route>(newSol.getRoutes());
    			LinkedList<Route> currentRoutes = new LinkedList<Route>(currentVRPSol.getRoutes());
    			
    			LinkedList<Node> newNodes = null;
    			newNodes = getNodes(newRoutes);
    			newNodes.add(0, inp.getNodes()[0]);
    			
    			LinkedList<Node> currentNodes = null;
    			currentNodes = getNodes(currentRoutes);
    			currentNodes.add(0, inp.getNodes()[0]);
    			
    			
    			
    			if ( (newSol.getRoutingCosts() < currentVRPSol.getRoutingCosts()) && (newNodes.size() == currentNodes.size()) )
    			{	

    				//System.out.println("Entraaaaaaaaaaaaaa");
    				double stockCosts = 0.0;
    				float accDemand = 0;
    				for (int i = 1; i < inp.getNodes().length; i++) // node 0 is depot
    				{
    					Node aNode = inp.getNodes()[i];		
    					accDemand = accDemand + aNode.getActivePolicy().getUnitsToServe();
    					stockCosts = stockCosts + aNode.getActivePolicy().getExpStockCosts();
    				}

    				currentVRPSol = newSol;
    				currentVRPSol.setDemandToServe(accDemand);
    				currentVRPSol.setStockCosts(stockCosts);
    				currentVRPSol.setTotalCosts(stockCosts + newSol.getRoutingCosts());
    				return currentVRPSol;

    			}
    			
    			
    		}
    	}
    	
    	// 3. Return the best full VRP solution found so far
    	return currentVRPSol;
    }

    
	/******************************************************************************* 
	 * PRIVATE METHOD setRoutesGeometricCenters()
	 * Given a list of routes, it assigns to each route a geometric center
	 ******************************************************************************/
    
	private void setRoutesGeometricCenters(LinkedList<Route> routes)
	{
		for ( int i = 0; i < routes.size(); i++ )
    	{
    		Route iRoute = routes.get(i); 		
    		LinkedList<Node> iRouteNodes = getAllNodes(iRoute); 		
    		float[] iRouteCenter = calcGeometricCenter(iRouteNodes);
    		iRoute.setXRouteCenter(iRouteCenter[0]);
    		iRoute.setYRouteCenter(iRouteCenter[1]);
    	}
	}

	
	/******************************************************************************* 
	 * PRIVATE METHOD calcGeometricCenter()
	 * Returns a geometric center for a set of nodes
	 ******************************************************************************/

	private float[] calcGeometricCenter(List<Node> nodes)
	{
		// 1. Declare and initialize variables
		float sumX = (float) 0.0; // sum of x[i]
		float sumY = (float) 0.0; // sum of y[i]
		float[] center = new float[2]; // center as (x, y) coordinates
		
		// 2. Calculate sums of x[i] and y[i] for all iNodes in nodes
		Node iNode; // iNode = ( x[i], y[i] )
		for (int i = 0; i < nodes.size(); i++)
		{
			iNode = nodes.get(i);
			sumX = sumX + iNode.getX();
			sumY = sumY + iNode.getY();
		}
		
		// 3. Calculate means for x[i] and y[i]  
		center[0] = sumX / nodes.size(); // mean for x[i]
		center[1] = sumY / nodes.size(); // mean for y[i]
		
		// 4. Return center as (x-bar, y-bar)
		return center;
	}	
    
	
	/******************************************************************************* 
	 * PRIVATE METHOD calcFrontRoutes()
	 * Returns those routes from allRoutes that satisfy a given policy
	 ******************************************************************************/
	
	 private LinkedList<Route> calcFrontRoutes(LinkedList<Route> allRoutes, int policy)
	 {
		 LinkedList<Route> fRoutes = new LinkedList<Route>();
		 double mPId8 = Math.tan(Math.PI / 8);
		 double m3PId8 = Math.tan(3 * Math.PI / 8);
		 
		 switch (policy) 
		 {
		 	// POLICIES 1 TO 16: COVERING 270�
		 	case 1: // From 180� to 90� (South & East)
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterY >= allRoutes.get(i).getYRouteCenter() ||
		 				vrpCenterX <= allRoutes.get(i).getXRouteCenter() )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 2: // From 157.5� to 67.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY + m3PId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY - mPId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 3: // From 135� to 45�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY + 1 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY - 1 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 4: // From 112.5� to 22.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY + mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY - m3PId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 5: // From 90� to 0� (West & South)
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterY >= allRoutes.get(i).getYRouteCenter() ||
			 			vrpCenterX >= allRoutes.get(i).getXRouteCenter() )
			 			fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 6: // From 67.5� to 337.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY - mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() >= vrpCenterY + m3PId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 7: // From 45� to 315�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY + 1 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY - 1 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 8: // From 22.5� to 292.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY + mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY - m3PId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 9: // From 0� to 270� (North & West)
		 		for (int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterY <= allRoutes.get(i).getYRouteCenter() ||
		 				vrpCenterX >= allRoutes.get(i).getXRouteCenter() )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 10: // From 337.5� to 247.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY - mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() >= vrpCenterY + m3PId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 11: // From 315� to 225�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY - 1 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() >= vrpCenterY + 1 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 12: // From 292.5� to 202.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY - m3PId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() >= vrpCenterY + mPId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 13: // From 270� to 180� (North & East)
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterY <= allRoutes.get(i).getYRouteCenter() ||
		 				vrpCenterX <= allRoutes.get(i).getXRouteCenter() )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 14: // From 247.5� to 157.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY - mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY + m3PId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 15: // From 225� to 135�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY - 1 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY + 1 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 16: // From 202.5� to 112.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY - m3PId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY + mPId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;	
		 	// POLICIES 17 TO 32: COVERING 225�
		 	case 17: // From 180� to 45�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if(  vrpCenterY >= allRoutes.get(i).getYRouteCenter() ||
		 				 allRoutes.get(i).getYRouteCenter() >= vrpCenterY + 1 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 18: // From 157.5� to 22.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY + mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY - mPId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 19: // From 135� to 0�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterY >= allRoutes.get(i).getYRouteCenter() ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY - 1 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 20: // From 112.5� to 337.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY - mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY - m3PId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 21: // From 90� to 315�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterX >= allRoutes.get(i).getXRouteCenter() ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY - 1 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 22: // From 67.5� to 292.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY + m3PId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY - m3PId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 23: // From 45� to 270�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterX >= allRoutes.get(i).getXRouteCenter() ||
				 		allRoutes.get(i).getYRouteCenter() >= vrpCenterY + 1 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 24: // From 22.5� to 247.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY + mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() >= vrpCenterY + m3PId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 25: // From 0� to 225�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterY <= allRoutes.get(i).getYRouteCenter() ||
				 		allRoutes.get(i).getYRouteCenter() >= vrpCenterY + 1 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 26: // From 337.5� to 202.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY + mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() >= vrpCenterY - mPId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 27: // From 315� to 180�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterY <= allRoutes.get(i).getYRouteCenter() ||
				 		allRoutes.get(i).getYRouteCenter() >= vrpCenterY - 1 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 28: // From 292.5� to 157.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY - mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() >= vrpCenterY - m3PId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 29: // From 270� to 135�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterX <= allRoutes.get(i).getXRouteCenter() ||
				 		allRoutes.get(i).getYRouteCenter() >= vrpCenterY - 1 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 30: // From 247.5� to 112.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY - m3PId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY - m3PId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 31: // From 225� to 90�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterX <= allRoutes.get(i).getXRouteCenter() ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY + 1 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 32: // From 202.5� to 67.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY + mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
				 		allRoutes.get(i).getYRouteCenter() <= vrpCenterY + m3PId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	// POLICIES 33 TO 48: COVERING 180�
		 	case 33: // From 0� to 180� (North)
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterY <= allRoutes.get(i).getYRouteCenter() )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 34: // From 337.5� to 157.5� 
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY - mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 35: // From 315� to 135�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY - 1 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 36: // From 292.5� to 112.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY - m3PId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 37: // From 270� to 90� (East)
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterX <= allRoutes.get(i).getXRouteCenter() )
		 				fRoutes.add(allRoutes.get(i));		
		 		break;
		 	case 38: // From 247.5� to 67.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY + m3PId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 39: // From 225� to 45�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY + 1 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 40: // From 202.5� to 22.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY + mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 41: // From 180� to 0� (South)
		 		for (int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterY >= allRoutes.get(i).getYRouteCenter() )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 42: // From 157.5� to 337.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY - mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 43: // From 135� to 315�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY - 1 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 44: // From 112.5� to 292.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY - m3PId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 45: // 90� to 270� (West)
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( vrpCenterX >= allRoutes.get(i).getXRouteCenter() )
		 				fRoutes.add(allRoutes.get(i));
		 		break;		
		 	case 46: // From 67.5� to 247.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY + m3PId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;	
		 	case 47: // From 45� to 225�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY + 1 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	case 48: // From 22.5� to 202.5�
		 		for ( int i = 0; i < allRoutes.size(); i++ )
		 			if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY + mPId8 *
			 		    (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
		 				fRoutes.add(allRoutes.get(i));
		 		break;
		 	// POLICIES 49 TO 52: COVERING 2*90�
		  	case 49: // From 0� to 90� and 180� to 270� (1st and 3rd quadrants)
			 	for ( int i = 0; i < allRoutes.size(); i++ )
			 		if( (vrpCenterY <= allRoutes.get(i).getYRouteCenter() &&
  	 					 vrpCenterX <= allRoutes.get(i).getXRouteCenter()) ||
			 			(vrpCenterY >= allRoutes.get(i).getYRouteCenter() &&
					 	 vrpCenterX >= allRoutes.get(i).getXRouteCenter()) )
			 			fRoutes.add(allRoutes.get(i));
			 	break;
		  	case 50: // From 337.5� to 67.5� and 157.5� to 247.5�
			 	for ( int i = 0; i < allRoutes.size(); i++ )
			 		if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY + m3PId8 *
				 		 (allRoutes.get(i).getXRouteCenter() - vrpCenterX) &&
				 		allRoutes.get(i).getYRouteCenter() >= vrpCenterY - mPId8 *
						 (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
						allRoutes.get(i).getYRouteCenter() >= vrpCenterY + m3PId8 *
						 (allRoutes.get(i).getXRouteCenter() - vrpCenterX) &&
						allRoutes.get(i).getYRouteCenter() <= vrpCenterY - mPId8 *
						 (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
			 			fRoutes.add(allRoutes.get(i));
			 	break;
		  	case 51: // From 315� to 45� and 135� to 225�
			 	for ( int i = 0; i < allRoutes.size(); i++ )
			 		if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY + 1 *
				 		 (allRoutes.get(i).getXRouteCenter() - vrpCenterX) &&
				 		allRoutes.get(i).getYRouteCenter() >= vrpCenterY - 1 *
						 (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
						allRoutes.get(i).getYRouteCenter() >= vrpCenterY + 1 *
						 (allRoutes.get(i).getXRouteCenter() - vrpCenterX) &&
						allRoutes.get(i).getYRouteCenter() <= vrpCenterY - 1 *
						 (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
			 			fRoutes.add(allRoutes.get(i));
			 	break;
		  	case 52: // From 292.5� to 22.5� and 112.5� to 202.5�
			 	for ( int i = 0; i < allRoutes.size(); i++ )
			 		if( allRoutes.get(i).getYRouteCenter() <= vrpCenterY + mPId8 *
				 		 (allRoutes.get(i).getXRouteCenter() - vrpCenterX) &&
				 		allRoutes.get(i).getYRouteCenter() >= vrpCenterY - m3PId8 *
						 (allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
						allRoutes.get(i).getYRouteCenter() >= vrpCenterY + mPId8 *
						 (allRoutes.get(i).getXRouteCenter() - vrpCenterX) &&
						allRoutes.get(i).getYRouteCenter() <= vrpCenterY - m3PId8 *
						 (allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
			 			fRoutes.add(allRoutes.get(i));
			 	break;	
		  	case 53: // From 270� to 0� and 90� to 180� (2nd and 4rd quadrants)
			 	for ( int i = 0; i < allRoutes.size(); i++ )
			 		if( (vrpCenterY <= allRoutes.get(i).getYRouteCenter() &&
	  	 				 vrpCenterX >= allRoutes.get(i).getXRouteCenter()) ||
				 		(vrpCenterY >= allRoutes.get(i).getYRouteCenter() &&
						 vrpCenterX <= allRoutes.get(i).getXRouteCenter()) )
				 		fRoutes.add(allRoutes.get(i));
			 	break;
		  	case 54: // From 247.5� to 337.5� and 67.5� to 157.5�
			 	for ( int i = 0; i < allRoutes.size(); i++ )
			 		if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY + m3PId8 *
			 			(allRoutes.get(i).getXRouteCenter() - vrpCenterX) &&
				 		 allRoutes.get(i).getYRouteCenter() >= vrpCenterY - mPId8 *
						(allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
						 allRoutes.get(i).getYRouteCenter() <= vrpCenterY + m3PId8 *
						(allRoutes.get(i).getXRouteCenter() - vrpCenterX) &&
						 allRoutes.get(i).getYRouteCenter() <= vrpCenterY - mPId8 *
						(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
			 			fRoutes.add(allRoutes.get(i));
			 	break;
		  	case 55: // From 225� to 315� and 45� to 135�
			 	for ( int i = 0; i < allRoutes.size(); i++ )
			 		if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY + 1 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) &&
				 		 allRoutes.get(i).getYRouteCenter() >= vrpCenterY - 1 *
						(allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
						 allRoutes.get(i).getYRouteCenter() <= vrpCenterY + 1 *
						(allRoutes.get(i).getXRouteCenter() - vrpCenterX) &&
						 allRoutes.get(i).getYRouteCenter() <= vrpCenterY - 1 *
						(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
			 			fRoutes.add(allRoutes.get(i));
			 	break;
		  	case 56: // From 202.5� to 292.5� and 22.5� to 112.5�
			 	for ( int i = 0; i < allRoutes.size(); i++ )
			 		if( allRoutes.get(i).getYRouteCenter() >= vrpCenterY + mPId8 *
				 		(allRoutes.get(i).getXRouteCenter() - vrpCenterX) &&
				 		 allRoutes.get(i).getYRouteCenter() >= vrpCenterY - m3PId8 *
						(allRoutes.get(i).getXRouteCenter() - vrpCenterX) ||
						 allRoutes.get(i).getYRouteCenter() <= vrpCenterY + mPId8 *
						(allRoutes.get(i).getXRouteCenter() - vrpCenterX) &&
						 allRoutes.get(i).getYRouteCenter() <= vrpCenterY - m3PId8 *
						(allRoutes.get(i).getXRouteCenter() - vrpCenterX) )
			 			fRoutes.add(allRoutes.get(i));
			 	break;
		  	case 57: // (2nd quadrant)
			 	for ( int i = 0; i < allRoutes.size(); i++ )
			 		if( (vrpCenterY <= allRoutes.get(i).getYRouteCenter() &&
	  	 				 vrpCenterX >= allRoutes.get(i).getXRouteCenter()) )
				 		fRoutes.add(allRoutes.get(i));
		  	case 58: // (4rd quadrant)
			 	for ( int i = 0; i < allRoutes.size(); i++ )
			 		if( (vrpCenterY >= allRoutes.get(i).getYRouteCenter() &&
						 vrpCenterX <= allRoutes.get(i).getXRouteCenter()) )
				 		fRoutes.add(allRoutes.get(i));
			 	break;
		  	case 59: // (1st quadrant)
			 	for ( int i = 0; i < allRoutes.size(); i++ )
			 		if( (vrpCenterY <= allRoutes.get(i).getYRouteCenter() &&
  	 					 vrpCenterX <= allRoutes.get(i).getXRouteCenter()) )
			 			fRoutes.add(allRoutes.get(i));
			 	break;
		  	case 60: // (3rd quadrant)
			 	for ( int i = 0; i < allRoutes.size(); i++ )
			 		if( (vrpCenterY >= allRoutes.get(i).getYRouteCenter() &&
					 	 vrpCenterX >= allRoutes.get(i).getXRouteCenter()) )
			 			fRoutes.add(allRoutes.get(i));
			 	break;
		 	default:
		 		System.out.println("UNDEFINED POLICY");
		 		break;
		 }
		 
		 return fRoutes;
	 }
	 
	
	/******************************************************************************* 
	 * PRIVATE METHOD calcSubSol()
	 * Returns the sub-solution containing all given routes
	 ******************************************************************************/
	
	private Solution calcSubSol(LinkedList<Route> routes)
	{
		Solution sol = new Solution();
		double routingCots = 0.0F;
		
		for (int i = 0; i < routes.size(); i++ )
		{
			Route iRoute = routes.get(i);
			sol.addRoute(iRoute);
			routingCots += iRoute.getCosts();
		}
		sol.setRoutingCosts(routingCots);
		return sol;
	}
	
	
	/******************************************************************************* 
	 * PRIVATE METHOD calcBackRoutes()
	 * Returns those routes from allRoutes that are not frontRoutes
	 ******************************************************************************/
	
	private LinkedList<Route> calcBackRoutes(LinkedList<Route> frontRoutes, 
			LinkedList<Route> allRoutes)
	{
		LinkedList<Route> bRoutes = new LinkedList<Route>();
		
		for ( int i = 0; i < allRoutes.size(); i++ )
		{
			Route iRoute = allRoutes.get(i);
			if ( frontRoutes.contains(iRoute) == false )
				bRoutes.add(iRoute);
		}
		
		return bRoutes;
	}
	
	
	/******************************************************************************* 
	 * PRIVATE METHOD unifySubSols()
	 * Returns the solution that results from the union between two half-solutions
	 ******************************************************************************/
	
	private Solution unifySubSols(Solution frontSubSol, Solution backSubSol)
	{
		Solution vrpSol = new Solution();
		double routingCots = 0.0F;
		
		for ( int i = 0; i < frontSubSol.getRoutes().size(); i++ )
		{
			Route iRoute = frontSubSol.getRoutes().get(i);
			vrpSol.addRoute(iRoute);
			routingCots += iRoute.getCosts();
		}
		
		for ( int j = 0; j < backSubSol.getRoutes().size(); j++ )
		{
			Route jRoute = backSubSol.getRoutes().get(j);
			vrpSol.addRoute(jRoute);
			routingCots += jRoute.getCosts();
		}
		
		vrpSol.setRoutingCosts(routingCots);
		return vrpSol;
	}
	
	
	
	public static LinkedList<Node> getNodes(LinkedList<Route> routes) {
		LinkedList<Node> nodes = new LinkedList<Node>();
		for (Route route : routes) {
			for (Edge e : route.getEdges()) {
				// System.out.printf(e.getOrigin().getId()+" ->
				// "+e.getEnd().getId()+" ");
				if (e.getEnd().getId() != 0) {
					Node end = e.getEnd();
					nodes.add(e.getEnd());
				}
			}
		}
		return nodes;
	}
	
	
	public static LinkedList<Node> getAllNodes(Route route) {
		LinkedList<Node> nodes = new LinkedList<Node>();
		for (Edge e : route.getEdges()) {
			nodes.add(e.getEnd());
		}
		return nodes;
	}
	
	
}