package alg;

import java.util.Comparator;
import java.util.TreeSet;

public class BestSolutionsDiff2 {
	
	private TreeSet<PairBestDiff> arbol; 
	private static final int MAX_SOLS = 10;
	
    public BestSolutionsDiff2(){ 
    	arbol = new TreeSet<>(); 
    }
	
	public void addSolution(PairBestDiff sol){
		arbol.add(sol); 
		if(arbol.size() > MAX_SOLS) 
			arbol.remove(arbol.last()); 
	}
	
	
	public TreeSet<PairBestDiff> getSolutions(){
		return arbol;	
	}
	
	
	public int getSize(){
		return arbol.size();	
	}
	


}
