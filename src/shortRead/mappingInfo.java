package shortRead;

import java.io.Serializable;

public class mappingInfo implements Comparable<mappingInfo> , Serializable
{


	public String qName;
	public String read;
	public String QC;
	public int refPos;
	public pair< String, Integer > cigar = new pair< String, Integer >();
	public int len;
	public int acceptedValue;
	public int flag;
	public int startPos;
	public int order;
	public mappingInfo()
	{
	}
	public mappingInfo(String _q, String _r, int _rf, int _len, int _ac, int _flag, String _QC)
	{
		this(_q, _r, _rf, _len, _ac, _flag, _QC, new pair< String, Integer>("", 0));
	}
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: mappingInfo(String _q, String _r, uint _rf, int _len, int _ac, int _flag, String _QC, pair< String, uint > _c = pair< String, uint >("", 0))
	public mappingInfo(String _q, String _r, int _rf, int _len, int _ac, int _flag, String _QC, pair< String, Integer> _c)
	{
		qName = _q;
		read = _r;
		refPos = _rf;
		len = _len;
		acceptedValue = _ac;
		flag = _flag;
		QC = _QC;
		cigar = _c;
	}
	public final void print()
	{
		System.out.println(qName);
		System.out.println(read+" "+QC);
		System.out.println(refPos+" "+len+" "+acceptedValue+" "+flag);
	}

	
	public final int compareTo(mappingInfo otherInstance)
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
	
	//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
	//ORIGINAL LINE: boolean operator < (const mappingInfo& m) const
		public boolean lessThan (mappingInfo m)
		{
			return order < m.order;
		}
}