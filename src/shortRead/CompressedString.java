package shortRead;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

///#define SA_SAMPLERATE 64
///#define BT_SAMPLERATE 4


public class CompressedString implements Serializable
{
	public char[] dna = new char[4];
	public BitArray invalidPos;
	public int sz;
	public byte[] encodedStr;
	public int zeroPos;

	public CompressedString()
	{
		sz = 0;
		dna[0]='a';
		dna[1]='c';
		dna[2]='g';
		dna[3]='t';
		encodedStr = null;
		invalidPos = new BitArray();
	}

	public final void save(FileChannel out) throws IOException
	{	
		System.out.println("Size= "+sz);
		ByteBuffer bbuf = ByteBuffer.allocate(4);
		bbuf.order(ByteOrder.LITTLE_ENDIAN);
		bbuf.putInt(sz);
		bbuf.flip();
		out.write(bbuf);
		bbuf.clear();
//		out.print(Repository.intToBytes(sz));
		if (sz > 0)
		{
			invalidPos.save(out);
			for (int i = 0; i < (sz + 3) / 4; i++){
//				out.print(encodedStr[i]);
				bbuf.put(encodedStr[i]);
				bbuf.flip();
				out.write(bbuf);
				bbuf.clear();
			}
//			out.print(Repository.intToBytes(zeroPos));
			bbuf.putInt(zeroPos);
			bbuf.flip();
			out.write(bbuf);
			bbuf.clear();
		}
	}

	public final void load(MyBufferedScanner fin) throws IOException
	{
		sz = fin.readInt();
		if (sz > 0)
		{
			invalidPos.load(fin);
			encodedStr = new byte[(sz + 3) / 4];
			for(int i = 0 ; i < encodedStr.length ; i++){
				encodedStr[i]=fin.readByte();
			}
			zeroPos = fin.readInt();
		}
	}

	public final void set(char [] T, int n)	{
//		zeroPos = -1;
		sz = n;
		invalidPos.reset(n);
		encodedStr = new byte[(n + 3) / 4];
		for (int i = 0; i < (n + 3) / 4; i++)
			encodedStr[i] = 0;
//-ORIGINAL LINE: for( unsigned int i = 0; i < n; i++ )
		for (int i = 0; i < n; i++)
			if (T[i] == 'n')
				invalidPos.setBit(i);
			else if (T[i] == 0)
				zeroPos = i;
			else
			{
				int idx = 0;
				while (dna[idx] != T[i])
					idx++;
//The right shift operator was replaced by Java's logical right shift operator since the left operand was originally of an unsigned type, but you should confirm this replacement:
				encodedStr[i >>> 2] |= (idx << ((i & 3) << 1));
			}
	}

	public final char charAt(int pos)
	{
		if (pos == zeroPos)
			return 0;
		if (invalidPos.getPos(pos) > 0)
			return 'n';
//The right shift operator was replaced by Java's logical right shift operator since the left operand was originally of an unsigned type, but you should confirm this replacement:
		return dna[( encodedStr[pos>>2] >> ( ( pos & 3 ) << 1 ) ) & 3];
	}

//	public final int getBytes()
//	{
//		long ret = 0;
//		ret += sz / 4;
//		ret += invalidPos.getBytes();
//		return ret;
//	}
}