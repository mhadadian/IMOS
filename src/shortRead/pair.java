package shortRead;

import java.io.Serializable;

public class pair <U,V> implements Comparable<pair>, Serializable{
	public U first;
	public V second;
	public pair(U _1 , V _2) {
		first = _1;
		second = _2;
	}
	public pair() {
		first = null;
		second = null;
	}
	@Override
	public int compareTo(pair arg0) {
		if(arg0.first instanceof Integer && first instanceof Integer){
			if(arg0.first != first) return (int) arg0.first - (int) first;
			if(arg0.second != second) return (int) arg0.second - (int) second;
		}
		else{
			int a = 1/0;
		}
		return 0;
	}
	
	@Override
	public boolean equals(Object arg0) {
		if(arg0 instanceof pair){
			pair p = (pair) arg0;
			if(p.first instanceof Integer && first instanceof Integer && p.second instanceof Integer && second instanceof Integer){
				int pf = (int) p.first;
				int ps = (int) p.second;
				int f = (int) first;
				int s = (int) second;
				if(pf == f && ps == s){
					return true;
				}
				else{
					return false;
				}
			}
			else{
				return false;
			}
		}
		else
			return false;
//		return super.equals(arg0);
	}
	
	
}
