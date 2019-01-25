package shortRead;

import java.io.Serializable;

public class intervalNode implements Serializable
{
//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: unsigned int left, right;
  public int left;
  public int right;
  public byte editDistance;
  public int leftToRight;
  public int mask;
  public intervalNode()
  {
  }
//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: intervalNode(unsigned int _l, unsigned int _r, int _ed, int _lr, int _m)
  public intervalNode(int _l, int _r, byte _ed, int _lr, int _m)
  {
	left = _l;
	right = _r;
	editDistance = _ed;
	leftToRight = _lr;
	mask = _m;
  }
  
  public intervalNode(intervalNode O){
	  left = O.left;
	  right = O.right;
	  editDistance = O.editDistance;
	  leftToRight = O.leftToRight;
	  mask = O.mask;
  }
}