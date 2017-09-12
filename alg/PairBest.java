package alg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PairBest implements Comparable<PairBest> {
	private static int serial = 0;
	private final int id = serial++;

	private Solution sol;
	private Inputs imp;

	public PairBest(Solution key, Inputs value) {
		this.sol = new Solution(key);
		this.imp = (Inputs) deepClone(value);
	}

	public Solution getkey() {
		return sol;
	}

	public Inputs getvalue() {
		return imp;
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
		PairBest other = (PairBest) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(PairBest o) {
		if (o.getkey().getTotalCosts() == this.getkey().getTotalCosts()) {
			return 0;
		} else if (o.getkey().getTotalCosts() < this.getkey().getTotalCosts()) {
			return 1;
		} else {
			return -1;
		}
	}

}
