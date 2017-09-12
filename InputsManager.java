package alg;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130112
 */
public class InputsManager
{
    public static Inputs readInputs(String nodesFilePath, String vehiclesFilePath)
    {
        Inputs inputs = null;
        try
        {   // 1. COUNT THE # OF NODES (# OF LINES IN nodesFilePath)
            BufferedReader br = new BufferedReader(new FileReader(nodesFilePath));
            String f = null;
            int nNodes = 0;
            while( (f = br.readLine()) != null )
            {   if( f.charAt(0) != '#' )
                    nNodes++;
            }
            // 2. CREATE THE INPUTS OBJECT WITH nNodes
            inputs = new Inputs(nNodes);
            // 3. CREATE ALL NODES AND FILL THE NODES LIST
            FileReader reader = new FileReader(nodesFilePath);
            Scanner in = new Scanner(reader);
            String s = null;
            int k = 0;
            while( in.hasNextLine() )
            {   s = in.next();
                if( s.charAt(0) == '#' ) // this is a comment line
                    in.nextLine(); // skip comment lines
                else
                {   float x = Float.parseFloat(s); 
                    float y = in.nextFloat();
                    float demand = in.nextFloat();
                    Node node = new Node(k, x, y, demand);
                    inputs.getNodes()[k] = node;
                    k++;
                }
            }
            in.close();
             // 4. READ VEHICLE CAPACITY (HOMOGENEOUS FLEET)
            reader = new FileReader(vehiclesFilePath);
            in = new Scanner(reader);
            while( in.hasNextLine() )
            {   s = in.next();
                if( s.charAt(0) == '#' ) // this is a comment line
                    in.nextLine(); // skip comment lines
                else
                {   float vCap = Float.parseFloat(s);
                    inputs.setVehCap(vCap);
                }
            }
            in.close();
        }
        catch (IOException exception)
        {   System.out.println("Error processing inputs files: " + exception);
        }
        return inputs;
    }
    
    
    
    
    /**
     * Creates the (edges) savingsList according to the CWS heuristic from nodes.
     */
    public static LinkedList<Edge> generateSavingsList(List<Node> nodes)
    {
        int nNodes = nodes.size();      

        Edge[] savingsArray = new Edge[(nNodes - 1) * (nNodes - 2) / 2];
        Node depot = nodes.get(0);
        int k = 0;
        for( int i = 1; i < nNodes - 1; i++ ) // node 0 is the depot
        {   for( int j = i + 1; j < nNodes; j++ )
            {   Node iNode = nodes.get(i);
                Node jNode = nodes.get(j);
                // Create ijEdge and jiEdge, and assign costs and savings
                Edge ijEdge = new Edge(iNode, jNode);
                ijEdge.setCosts(ijEdge.calcCosts(iNode, jNode));
                ijEdge.setSavings(ijEdge.calcSavings(iNode, jNode, depot));
                Edge jiEdge = new Edge(jNode, iNode);
                jiEdge.setCosts(jiEdge.calcCosts(jNode, iNode));
                jiEdge.setSavings(jiEdge.calcSavings(jNode, iNode, depot));
                // Set inverse edges
                ijEdge.setInverse(jiEdge);
                jiEdge.setInverse(ijEdge);
                // Add a single new edge to the savingsList
                savingsArray[k] = ijEdge;
                k++;
            }
        }
        // Construct the savingsList by sorting the edgesList. Uses the compareTo()
        //  method of the Edge class (TIE ISSUE #1).
        

 
        Arrays.sort(savingsArray);
        List sList = Arrays.asList(savingsArray);
        LinkedList savingsList = new LinkedList(sList);
        return savingsList;
    }   
     
    

    /**
     * Creates the (edges) savingsList according to the CWS heuristic.
     */
    public static void generateSavingsList(Inputs inputs)
    {
        int nNodes = inputs.getNodes().length;
        Edge[] savingsArray = new Edge[(nNodes - 1) * (nNodes - 2) / 2];
        Node depot = inputs.getNodes()[0];
        int k = 0;
        for( int i = 1; i < nNodes - 1; i++ ) // node 0 is the depot
        {   for( int j = i + 1; j < nNodes; j++ )
            {   Node iNode = inputs.getNodes()[i];
                Node jNode = inputs.getNodes()[j];
                // Create ijEdge and jiEdge, and assign costs and savings
                Edge ijEdge = new Edge(iNode, jNode);
                ijEdge.setCosts(ijEdge.calcCosts(iNode, jNode));
                ijEdge.setSavings(ijEdge.calcSavings(iNode, jNode, depot));
                Edge jiEdge = new Edge(jNode, iNode);
                jiEdge.setCosts(jiEdge.calcCosts(jNode, iNode));
                jiEdge.setSavings(jiEdge.calcSavings(jNode, iNode, depot));
                // Set inverse edges
                ijEdge.setInverse(jiEdge);
                jiEdge.setInverse(ijEdge);
                // Add a single new edge to the savingsList
                savingsArray[k] = ijEdge;
                k++;
            }
        }
        // Construct the savingsList by sorting the edgesList. Uses the compareTo()
        //  method of the Edge class (TIE ISSUE #1).
        Arrays.sort(savingsArray);
        List sList = Arrays.asList(savingsArray);
        LinkedList savingsList = new LinkedList(sList);
        inputs.setList(savingsList);
    }

    /*
     * Creates the list of paired edges connecting node i with the depot,
     *  i.e., it creates the edges (0,i) and (i,0) for all i > 0.
     */
    public static void generateDepotEdges(Inputs inputs)
    {   Node[] nodes = inputs.getNodes();
        Node depot = nodes[0]; // depot is always node 0
        // Create diEdge and idEdge, and set the corresponding costs
        for( int i = 1; i < nodes.length; i++ ) // node 0 is depot
        {   Node iNode = nodes[i];
            Edge diEdge = new Edge(depot, iNode);
            iNode.setDiEdge(diEdge);
            diEdge.setCosts(diEdge.calcCosts(depot, iNode));
            Edge idEdge = new Edge(iNode, depot);
            iNode.setIdEdge(idEdge);
            idEdge.setCosts(idEdge.calcCosts(depot, iNode));
            iNode.setRoundtripToDepotCosts(diEdge.getCosts() + idEdge.getCosts());
            // Set inverse edges
            idEdge.setInverse(diEdge);
            diEdge.setInverse(idEdge);
        }
    }

    /**
    * @return geometric center for a set of nodes
    */
    public static float[] calcGeometricCenter(List<Node> nodesList)
    {
        Node[] nodesArray = new Node[nodesList.size()];
        nodesArray = nodesList.toArray(nodesArray);
        return calcGeometricCenter(nodesArray);
    }

    public static float[] calcGeometricCenter(Node[] nodes)
    {
        // 1. Declare and initialize variables
	float sumX = 0.0F; // sum of x[i]
	float sumY = 0.0F; // sum of y[i]
	float[] center = new float[2]; // center as (x, y) coordinates
	// 2. Calculate sums of x[i] and y[i] for all iNodes in nodes
	Node iNode; // iNode = ( x[i], y[i] )
	for( int i = 0; i < nodes.length; i++ )
	{   iNode = nodes[i];
            sumX = sumX + iNode.getX();
            sumY = sumY + iNode.getY();
	}
	// 3. Calculate means for x[i] and y[i]
	center[0] = sumX / nodes.length; // mean for x[i]
	center[1] = sumY / nodes.length; // mean for y[i]
	// 4. Return center as (x-bar, y-bar)
	return center;
    }
}