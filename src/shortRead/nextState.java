package shortRead;

import java.io.Serializable;

public class nextState implements Comparable<nextState> , Serializable {
	public int acceptedValue;
	//- ORIGINAL LINE: vector<unsigned short> nextRowDp;
	public java.util.ArrayList<Short> nextRowDp = new java.util.ArrayList<Short>();
	public boolean valid;
	//- ORIGINAL LINE: std::pair<unsigned int, unsigned int> nxInterval;
	public pair<Integer, Integer> nxInterval = new pair<Integer, Integer>();

	public final int compareTo(nextState otherInstance) {
		if (lessThan(otherInstance)) {
			return -1;
		} else if (otherInstance.lessThan(this)) {
			return 1;
		}

		return 0;
	}

	public nextState(int edit) {
		int resize = 2 * edit + 1 - nextRowDp.size();
		short initVal = (short) (edit + 1);
		for (int i = 0; i < resize; i++) {
			nextRowDp.add(initVal);
		}
		valid = false;
		acceptedValue = -1;
	}

	public nextState() {
	}

	//- ORIGINAL LINE: boolean operator <(const nextState &m) const
	public boolean lessThan(nextState m) {
		if (acceptedValue == -1)
			return false;
		if (m.acceptedValue == -1)
			return true;
		return acceptedValue < m.acceptedValue;
	}
}