package alg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PairBestMulti implements Comparable<PairBestMulti> {
	private static int serial = 0;
	private final int id = serial++;

	private Solution[] sol;
	private Inputs[] imp;
	private double totcostSol = 0.0;

	public PairBestMulti(Solution[] key, Inputs[] value, double totCost, int periods) {
		this.sol = new Solution[periods];
		this.imp = new Inputs[periods];
		this.totcostSol = totCost;
		
		for(int i = 0; i<periods;i++){
			this.sol[i] = new Solution(key[i]);
			this.imp[i] = (Inputs) deepClone(value[i]);
		}
	}

	public Solution[] getkey() {
		return sol;
	}

	public Inputs[] getvalue() {
		return imp;
	}
	
	
	public double getTotcostSol() {
		return totcostSol;
	}

	public void setTotcostSol(double totcostSol) {
		this.totcostSol = totcostSol;
	}

	
	

	public static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PairBestMulti other = (PairBestMulti) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(PairBestMulti o) {
		if (o.getTotcostSol() == this.getTotcostSol()) {
			return 0;
		} else if (o.getTotcostSol() < this.getTotcostSol()) {
			return 1;
		} else {
			return -1;
		}
	}


}
