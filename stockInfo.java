package alg;

public class stockInfo {
	
	private static int serial = 0;
	private final int id = serial++;

	private float currentLevel;
	private float refillUpToPercent;
	private float maxcap;
	private float unitsToserve;
	private int period;
	private int Numnode;
	private float expDemand;

	public int getNumnode() {
		return Numnode;
	}

	public void setNumnode(int numnode) {
		Numnode = numnode;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public stockInfo(int node, int period, float currentLevel, float refillUpToPercent, float maxcap, float unitsToserve,float expDemand ) {
		this.currentLevel = currentLevel;
		this.refillUpToPercent = refillUpToPercent;
		this.maxcap = maxcap;
		this.unitsToserve = unitsToserve;
		this.period = period;
		this.Numnode = node;
		this.expDemand = expDemand;	
	}

	public float getExpDemand() {
		return expDemand;
	}

	public void setExpDemand(float expDemand) {
		this.expDemand = expDemand;
	}

	public static int getSerial() {
		return serial;
	}

	public static void setSerial(int serial) {
		stockInfo.serial = serial;
	}

	public float getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(float currentLevel) {
		this.currentLevel = currentLevel;
	}

	public float getRefillUpToPercent() {
		return refillUpToPercent;
	}

	public void setRefillUpToPercent(float refillUpToPercent) {
		this.refillUpToPercent = refillUpToPercent;
	}

	public float getMaxcap() {
		return maxcap;
	}

	public void setMaxcap(float maxcap) {
		this.maxcap = maxcap;
	}

	public float getUnitsToserve() {
		return unitsToserve;
	}

	public void setUnitsToserve(float unitsToserve) {
		this.unitsToserve = unitsToserve;
	}

	public int getId() {
		return id;
	}
	
}
