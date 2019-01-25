package shortRead;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class BitArray implements Serializable{
	public static final int blockLog2 = 6;
	public static final int block = (1 << blockLog2);
	public int smRate;
	public long[] arr;
	public byte[] pc;
	public int[] sum;
	public int sz;
	public long[] needMask = new long[64];

	public BitArray()
	{
		smRate = 0;
		needMask[0] = 1;
		arr = null;
		sum = null;
		pc = null;
		for (int i = 1; i < 64; i++)
			needMask[i] = needMask[i - 1] + (1l << i);
		sz = 0;
	}


	public static byte myPopcount(long x)
	{
		x = (x & 0x5555555555555555l) + ((x >>> 1) & 0x5555555555555555l);
		x = (x & 0x3333333333333333l) + ((x >>> 2) & 0x3333333333333333l);
		x = (x & 0x0F0F0F0F0F0F0F0Fl) + ((x >>> 4) & 0x0F0F0F0F0F0F0F0Fl);
		return (byte) ((x * 0x0101010101010101l) >>> 56);
	}

	public final void reset(long aS)
	{
		sz = (int) ((aS + block - 1) / block);
		arr = new long[sz];
		for (int i = 0; i < sz; i++)
			arr[i] = 0;
	}

	public final void setBit(int pos)
	{
//-The right shift operator was replaced by Java's logical right shift operator since the left operand was originally of an unsigned type, but you should confirm this replacement:
		int idx = pos >>> blockLog2;
		arr[idx] |= (1 << (pos & (block - 1)));
	}

	public final void setSum()
	{
		smRate = DefineConstants.BT_SAMPLERATE;
//-ORIGINAL LINE: unsigned int smSize = ( sz + smRate - 1 ) / smRate;
		int smSize = (sz + smRate - 1) / smRate;
//-ORIGINAL LINE: sum = new unsigned int[smSize];
		sum = new int[smSize];
		pc = new byte[sz];
		for (int i = 0; i < sz; i++)
			pc[i] = myPopcount(arr[i]);
		for (int i = 0; i < smSize; i++)
			sum[i] = 0;
		sum[0] = myPopcount(arr[0]);
		for (int i = smRate; i < sz; i += smRate)
		{
			int idx = i / smRate;
			sum[idx] = sum[idx - 1];
//-ORIGINAL LINE: for( unsigned int j = ( idx - 1 ) * smRate + 1; j <= i; j++ )
			for (int j = (idx - 1) * smRate + 1; j <= i; j++)
				sum[idx] += myPopcount(arr[j]);
		}
	}

	public final int getPos(int pos)
	{
//-The right shift operator was replaced by Java's logical right shift operator since the left operand was originally of an unsigned type, but you should confirm this replacement:
		int idx = pos >>> blockLog2;
		return ( (arr[idx] & (1 << (pos & (block - 1)))) > 0 ) ? 1 : 0;
	}

	public final int getRank(int pos)
	{
//-The right shift operator was replaced by Java's logical right shift operator since the left operand was originally of an unsigned type, but you should confirm this replacement:
		int idx = (pos >>> blockLog2);
		if (sz == 0)
			return 0;
		int ret = 0;
		try
		{
			ret = myPopcount(arr[idx] & needMask[(pos & (block - 1))]);
		}
		catch (final RuntimeException e)
		{
			 e.printStackTrace();
		}
		if (idx == 0)
			return ret;
		int pre = idx - 1;
		while (pre >= 0 && (pre % smRate) > 0)
		{
			ret += pc[pre];
			pre--;
		}
		if (pre >= 0 && (pre & (smRate - 1)) == 0)
		{
			ret += sum[pre / smRate];
		}
		return ret;
	}

	public final void save(FileChannel out) throws IOException
	{	
//		System.out.println("Invalid Pos= "+sz);
//		out.print(intToBytes(sz));
		ByteBuffer bbuf = ByteBuffer.allocate(8);
		bbuf.order(ByteOrder.LITTLE_ENDIAN);
		bbuf.putInt(sz);
		bbuf.flip();
		out.write(bbuf);
		bbuf.clear();
		if (sz > 0)
		{
			for (int i = 0; i < sz; i++){
//				out.print(longToBytes(arr[i]));
				bbuf.putLong(arr[i]);
				bbuf.flip();
				out.write(bbuf);
				bbuf.clear();
			}
//			out.print(intToBytes(smRate));
			bbuf.putInt(smRate);
			bbuf.flip();
			out.write(bbuf);
			bbuf.clear();
			if (smRate > 0)
			{
//-ORIGINAL LINE: unsigned int smSize = ( sz + smRate - 1 ) / smRate;
				int smSize = (sz + smRate - 1) / smRate;
				for (int i = 0; i < smSize; i++){
//					out.print(intToBytes(sum[i]));
					bbuf.putInt(sum[i]);
					bbuf.flip();
					out.write(bbuf);
					bbuf.clear();
				}
			}
		}		
//		out.close();
	}

	public final void load(MyBufferedScanner in) throws IOException
	{
		pc = null;
		sz = in.readInt();
//		System.out.println(sz);
		if(sz>0){
			arr = new long[sz];
			for(int i = 0 ; i < sz ; i++){
				arr[i] = in.readLong();
			}
			smRate = in.readInt();
			if(smRate>0){
				int smSize = (sz + smRate - 1) / smRate;
				sum = new int[smSize];
				for(int i = 0 ; i < smSize ; i++){
					sum[i]= in.readInt();
				}
				pc = new byte[sz];
				for (int i = 0; i < sz; i++)
					pc[i] = myPopcount(arr[i]);
			}
		}
//		System.out.println("b=\t"+ arr[0] +" "+ arr[13]+" "+ arr[23]);
//		in.close();
	}

	public final long getBytes()
	{
		long ret = sz / 8;
		if (sum != null)
			ret += (sz / smRate) * 4;
		return ret;
	}
	public String intToBytes(int a){
		char a0 =(char)(a & 255);
		a=a>>8;
		char a1 =(char)(a & 255);
		a=a>>8;
		char a2 =(char)(a & 255);
		a=a>>8;
		char a3 =(char)(a & 255);
		String out = a3+""+a2+""+a1+""+a0+"";
		return out;
	}
	public String longToBytes(long a){
		char a0 =(char)(a & 255);
		a=a>>8;
		char a1 =(char)(a & 255);
		a=a>>8;
		char a2 =(char)(a & 255);
		a=a>>8;
		char a3 =(char)(a & 255);
		a=a>>8;
		char a4 =(char)(a & 255);
		a=a>>8;
		char a5 =(char)(a & 255);
		a=a>>8;
		char a6 =(char)(a & 255);
		a=a>>8;
		char a7 =(char)(a & 255);
		String out = a7+""+a6+""+a5+""+a4+""+a3+""+a2+""+a1+""+a0+"";
		return out;
	}
}