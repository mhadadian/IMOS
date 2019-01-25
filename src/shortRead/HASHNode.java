package shortRead;

import java.io.Serializable;

public class HASHNode implements Serializable
{
  public compressedArray to = new compressedArray();
//-ORIGINAL LINE: unsigned long long mask;
  public long mask;
//-ORIGINAL LINE: unsigned long long m2;
  public long m2;
  public boolean isValid;
  public int base;
  public HASHNode() {}
//ORIGINAL LINE: HASHNode(compressedArray _to, unsigned long long _mask, int _base, bool _isValid)
  public HASHNode(compressedArray _to, long _mask, int _base, boolean _isValid)
  {
	to = _to;
	mask = _mask;
	base = _base;
	isValid = _isValid;
  }
}