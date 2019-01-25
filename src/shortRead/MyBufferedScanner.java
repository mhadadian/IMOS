package shortRead;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class MyBufferedScanner extends BufferedInputStream implements Serializable{

	public MyBufferedScanner(InputStream arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	public byte readByte() throws IOException{
		return (byte) read();
	}
	public short readByteToShort() throws IOException{
		return (short) read();
	}
	public int readInt() throws IOException{
		int out = 0;
		int a = read();
		int b = read();
		int c = read();
		int d = read();
		out+=d<<8;
		out+=c;
		out=out<<8;
		out+=b;
		out=out<<8;
		out+=a;
		if(out==-16843009) throw new ArithmeticException();
		return out;
	}
	public long readLong() throws IOException{
		long out = 0;
		int a = read();
		int b = read();
		int c = read();
		int d = read();
		int a1 = read();
		int b1 = read();
		int c1 = read();
		int d1 = read();
		out+=d1<<8;
		out+=c1;
		out=out<<8;
		out+=b1;
		out=out<<8;
		out+=a1;
		out=out<<8;
		out+=d;
		out=out<<8;
		out+=c;
		out=out<<8;
		out+=b;
		out=out<<8;
		out+=a;
		if(out==-72340172838076673l) throw new ArithmeticException();
		return out;
	}

}
