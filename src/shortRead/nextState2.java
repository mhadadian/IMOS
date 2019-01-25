package shortRead;

import java.io.Serializable;

public class nextState2 implements Comparable<nextState2>, Serializable
{
	public final int compareTo(nextState2 otherInstance)
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
  public int error;
  public boolean valid;

//-ORIGINAL LINE: std::pair<unsigned int, unsigned int> nxInterval;
  public pair<Integer, Integer> nxInterval = new pair<Integer, Integer>();
  public nextState2(int edit)
  {
	valid = false;
	acceptedValue = -1;
  }
  public nextState2()
  {
  }
//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: boolean operator <(const nextState2 &m) const
  public boolean lessThan(nextState2 m)
  {
	  return error < m.error;
  }
}