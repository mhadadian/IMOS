package shortRead;

import java.io.Serializable;

public class nextState3 implements Comparable<nextState3> , Serializable
{
	public final int compareTo(nextState3 otherInstance)
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

  public int acceptedValue;
  public compressedArray nextRowDp = new compressedArray();
  public boolean valid;

//- ORIGINAL LINE: std::pair<unsigned int, unsigned int> nxInterval;
  public pair<Integer, Integer> nxInterval = new pair<Integer, Integer>();
  public nextState3(int edit)
  {
	valid = false;
	acceptedValue = -1;
  }
  public nextState3() {}

//-ORIGINAL LINE: boolean operator <(const nextState3 &m) const
  public boolean lessThan(nextState3 m)
  {
	if (acceptedValue == -1)
	  return false;
	if (m.acceptedValue == -1)
	  return true;
	return acceptedValue < m.acceptedValue;
  }
}