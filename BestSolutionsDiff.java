package alg;

import java.util.Comparator;
import java.util.TreeSet;

public class BestSolutionsDiff {
	
	private TreeSet<PairBestMulti> arbol; 
	private static final int MAX_SOLS = 10;
	
    public BestSolutionsDiff(){ 
    	arbol = new TreeSet<>(); 
    }
	
	public void addSolution(PairBestMulti sol){
		arbol.add(sol); 
		if(arbol.size() > MAX_SOLS) 
			arbol.remove(arbol.last()); 
	}
	
	
	public TreeSet<PairBestMulti> getSolutions(){
		return arbol;	
	}
	
	
	public int getSize(){
		return arbol.size();	
	}
	


}