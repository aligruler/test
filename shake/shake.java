package shake;
//import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.rng.RandomStreamBase;
import umontreal.ssj.probdist.Distribution;
import umontreal.ssj.probdist.LognormalDist;
import umontreal.ssj.randvar.LognormalGen;
import umontreal.ssj.randvar.RandomVariateGen;


import java.util.Random;
import  alg.Test;
import  alg.Inputs;
import  alg.Solution;





public abstract class shake {
	
	protected  Test aTest;
	protected  Random rng;

	
	public shake (Test test, Random myrng){
		this.aTest = test;
		rng = myrng;
		
	}
	

	public abstract Solution shakeIt(Solution sol, Inputs inputs);
	
}
