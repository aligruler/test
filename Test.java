package alg;

//import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.rng.RandomStreamBase;
import umontreal.ssj.probdist.Distribution;
import umontreal.ssj.probdist.LognormalDist;
import umontreal.ssj.randvar.LognormalGen;
import umontreal.ssj.randvar.RandomVariateGen;
/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130112
 */
public class Test
{
    /* INSTANCE FIELDS AND CONSTRUCTOR */
    private String instanceName;
    private float maxRouteCosts; // Maximum costs allowed for any single route
    private float serviceCosts; // Costs of completing an individual service
    private float maxTime; // Maximum computing time allowed
    private String distrib; // Statistical distribution for the randomness
    private float beta1; // First parameter associated with the distribution
    private float beta2; // Second parameter associated with the distribution
    private int seed; // Seed value for the Random Number Generator (RNG)
    private float k; // Var[Di] = k * E[Di]
    private float lambda; // lambda constant for the stock-costs function
    private float irpbias; //Alpha bias random local functions
    private int period;//number of considered periods
    private RandomStream rng;
    private int approach;
    private int simRuns;
 
    public Test(String name, float rCosts, float sCosts, float t, 
            String d, float p1, float p2, int s, float kvalue, float l, float irp_bias, int p, int app, int sims)
    {
        instanceName = name;
        maxRouteCosts = rCosts;
        serviceCosts = sCosts;
        maxTime = t;
        distrib = d;
        beta1 = p1;
        beta2 = p2;
        seed = s;
        k = kvalue;
        lambda = l;
        irpbias = irp_bias;
        period = p;
        approach = app;
        simRuns = sims;
    }

    /* GET METHODS */
    public String getInstanceName(){return instanceName;}
    public float getMaxRouteCosts(){return maxRouteCosts;}
    public float getServiceCosts(){return serviceCosts;}
    public float getMaxTime(){return maxTime;}
    public String getDistribution(){return distrib;}
    public float getBeta1(){return beta1;}
    public float getBeta2(){return beta2;}
    public int getSeed(){return seed;}
    public float getK(){return k;}
    public float getLambda(){return lambda;}
    public float getIrpBias(){return irpbias;}
    public RandomStream getRandomStream() {return rng;}
    
    public void setBeta1(float beta){this.beta1 = beta;}
         
    
    public int getSimRuns() {
		return simRuns;
	}

	public void setSimRuns(int simRuns) {
		this.simRuns = simRuns;
	}

	public int getApproach() {
		return approach;
	}

	public void setApproach(int approach) {
		this.approach = approach;
	}

	public int getNumPeriods() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	//Set Methods
	public void setRandomStream(RandomStream stream) {
		rng = stream;
	}
    



}