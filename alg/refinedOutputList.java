package alg;

import java.util.ArrayList;

public class refinedOutputList {
    String instanceName;
    float variance;
    float lambda;
    public float getVariance() {
		return variance;
	}

	public void setVariance(float variance) {
		this.variance = variance;
	}

	public float getLambda() {
		return lambda;
	}

	public void setLambda(float lambda) {
		this.lambda = lambda;
	}

	ArrayList<Solution> list;
	int bestSolNr;    
	int simRun;
    double stockCosts;
    double stockOutCosts;
        
    public refinedOutputList(String instanceName, ArrayList<Solution> list, float variance, float lambda)
    {
        this.instanceName = instanceName;
        this.list = list;
        this.variance = variance;
        this.lambda = lambda;
    }

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public ArrayList<Solution> getList() {
		return list;
	}

	public void setList(ArrayList<Solution> list) {
		this.list = list;
	}  
}
