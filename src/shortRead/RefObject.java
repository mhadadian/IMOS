package shortRead;

import java.io.Serializable;

//----------------------------------------------------------------------------------------
//	This class is used to simulate the ability to pass arguments by reference in Java.
//----------------------------------------------------------------------------------------
public final class RefObject<T> implements Serializable
{
	public T argvalue;
	public RefObject(T refarg)
	{
		argvalue = refarg;
	}
}