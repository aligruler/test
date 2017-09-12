package alg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PairBestDiff implements Comparable<PairBestDiff> {
	private static int serial = 0;
	private final int id = serial++;

	private int node;
	private double totalCost;

	public PairBestDiff(int nodeAct, double TotalCost) {
		this.node = nodeAct;
		this.totalCost = TotalCost;
	}

	public int getkey() {
		return node;
	}
	
	public double getValue() {
		return totalCost;
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
		long temp;
		temp = Double.doubleToLongBits(totalCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + id;
		result = prime * result + node;
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
		PairBestDiff other = (PairBestDiff) obj;
		if (Double.doubleToLongBits(totalCost) != Double.doubleToLongBits(other.totalCost))
			return false;
		if (id != other.id)
			return false;
		if (node != other.node)
			return false;
		return true;
	}

	@Override
	public int compareTo(PairBestDiff o) {
		if (o.getValue() == this.getValue()) {
			return 0;
		} else if (o.getValue() < this.getValue()) {
			return 1;
		} else {
			return -1;
		}
	}

}
