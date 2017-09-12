package shake;

import java.util.Random;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import alg.Inputs;
import alg.Policy;
import alg.Test;
import alg.Solution;
import alg.RandCWS;
import alg.Split;


public class spliting  extends shake{

	 public spliting(Test test,Random rng) {
			super(test,rng);
		}


	 
	 

    @Override
    public Solution shakeIt(Solution sol, Inputs inputs) {
    	
    	Solution newSol = new Solution();
    		
    	newSol = Split.splitSol(sol, aTest, inputs, rng);
    	return newSol;
    } 
   
 
}
