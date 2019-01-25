package shortRead;

import java.io.Serializable;

public class bwtNode implements Comparable<bwtNode> , Serializable
{
	//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	//ORIGINAL LINE: unsigned int first, second, len;
	  public int first;
	  public int second;
	  public int len;
	  public int acceptedValue;
	  public int refRow;
	  public byte flag;
	

//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: bwtNode(unsigned int _f, unsigned int _s, unsigned int _l, int _ac)
  public bwtNode(int _f, int _s, int _l, int _ac)
  {
	first = _f;
	second = _s;
	len = _l;
	acceptedValue = _ac;
  }
  
  public bwtNode(bwtNode O){
	  this.first = O.first;
	  this.second = O.second;
	  this.len = O.len;
	  this.acceptedValue = O.acceptedValue;
	  this.refRow = O.refRow;
	  this.flag = O.flag;
  }
  
  public bwtNode()
  {
  }
  
  public final int compareTo(bwtNode otherInstance)
	{
		if (lessThan(otherInstance))
		{
			return -1;
		}
		else if (otherInstance.lessThan(this))
		{
			return 1;
		}

		return 0;
	}
  
//'const' methods are not available in Java:
//ORIGINAL LINE: boolean operator <(const bwtNode &m) const
  public boolean lessThan(bwtNode m)
  {
	if (first != m.first)
	  return first < m.first;
	if (second != m.second)
	  return second < m.second;
	return len < m.len;
  }
//'const' methods are not available in Java:
//ORIGINAL LINE: boolean operator ==(const bwtNode &m) const
  public boolean equalsTo(bwtNode m)
  {
	return first == m.first && second == m.second && len == m.len;
  }
  public final void print()
  {
	System.err.println(first +" "+ second +" "+ len +" "+ acceptedValue);
  }
}