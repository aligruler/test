package shake;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import alg.AlgorithmSplit;
import alg.Edge;
import alg.Inputs;
import alg.Node;
import alg.Policy;
import alg.Test;
import alg.Solution;
import alg.Split;
import alg.RandCWS;
import alg.Outputs;


public class splitGeo extends shake
{
	
    public splitGeo(Test test,Random rng) {
		super(test,rng);
		// TODO Auto-generated constructor stub
	}


    @Override
    public Solution shakeIt(Solution solInp, Inputs inp) {
    	Solution newSol = new Solution();
    	
      	AlgorithmSplit splitGeo = new AlgorithmSplit(solInp,inp);
        newSol = splitGeo.improveSolUsingSplitting(solInp, 50, aTest, inp, rng);
    	return newSol;
	
}





         

}
