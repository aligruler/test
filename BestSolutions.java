package alg;

import java.util.Comparator;
import java.util.TreeSet;

public class BestSolutions {
	
	private TreeSet<PairBest> arbol; 
	private static final int MAX_SOLS = 10;
	
    public BestSolutions(){ 
    	arbol = new TreeSet<>(); 
    }
	
	public void addSolution(PairBest sol){
		arbol.add(sol); 
		if(arbol.size() > MAX_SOLS) 
			arbol.remove(arbol.last()); 
	}
	
	
	public TreeSet<PairBest> getSolutions(){
		return arbol;	
	}
	
	
	public int getSize(){
		return arbol.size();	
	}
	


}