package shortRead;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class BWT implements Serializable {
	public char[] dna = new char[4];
	public int mode;
	public int zeroPos;
	public char[] remap = new char[DefineConstants.size_uchar];
	public int sigma;
	public char[] remap_reverse;
	public int n;
	public int[] C = new int[DefineConstants.size_uchar + 1];
	public int[] suffixes;
	public BitArray[] bit = new BitArray[DefineConstants.MAXALPH];
	public CompressedString bwt = new CompressedString();

	// new vars
	public char X[];

	public BWT() {
		zeroPos = sigma = n = 0;
		dna[0] = 'a';
		dna[1] = 'c';
		dna[2] = 'g';
		dna[3] = 't';
		remap_reverse = null;
		suffixes = null;
	}

	public BWT(char[] T, int _n, int _m) throws IOException {
		mode = _m;
		n = _n;
		build(T, n);
		dna[0] = 'a';
		dna[1] = 'c';
		dna[2] = 'g';
		dna[3] = 't';
	}

	// -ORIGINAL LINE: byte *remap0(byte *T, uint32_t n)
	public void remap0(char[] T, int n) {
		X = new char[n];
		int i;
		int size = 0;

		int[] freqs = new int[DefineConstants.size_uchar];
		System.out.println("remap 0");
		// for (i = 0; i < freqs.length; i++){
		// freqs[i] = 0;
		// }
		for (i = 0; i < n; i++) {
			if (T[i] == 0) {
				int ax = 0;
			}
			if (freqs[T[i]]++ == 0) {
				size++;
			}
		}

		sigma = size;
		System.out.println("remap 1");
		// remap alphabet
		// need revision
		// if (freqs[0] > 1)
		// {
		// i = 1;
		// sigma++;
		// } // test if some character of T is zero, we already know that
		// // text[n-1]='\0'
		// else
		i = 0;
		System.out.println("remap 2");
		remap_reverse = new char[size];
		for (char j = 0; j < freqs.length; j++) {
			if (freqs[j] != 0) {
				remap[j] = (char) i;
				remap_reverse[i++] = j;
			}
		}
		System.out.println("remap 3");
		// remap text
		// X = new byte[n];
		for (i = 0; i < n - 1; i++) // the last character must be zero
			X[i] = remap[T[i]];
		X[n - 1] = 0;
		// return X;
	}

	public final void build(char[] T, int n) throws IOException {

		// -ORIGINAL LINE: unsigned int *SA;
		int SA[];
		int prev;
		int tmp;
		System.out.println("Build -1");
		remap0(T, n);
		for (int i = 0; i < sigma; i++) {
			bit[i] = new BitArray();
			char ch = remap_reverse[i];
			if (ch == 'a' || ch == 'c' || ch == 'g' || ch == 't' || ch == 'n') {
				bit[i].reset(n);
			}
		}
		System.out.println("Build 0");
		// -ORIGINAL LINE: unsigned int *xx = new unsigned int[n + 100];
		// int[] xx = new int[n + 100];
		//// -ORIGINAL LINE: for (unsigned int i = 0; i < n; i++)
		// for (int i = 0; i < n; i++)
		// xx[i] = X[i];
		// xx[n] = xx[n + 1] = xx[n + 2] = 0;
		// -ORIGINAL LINE: for (unsigned int i = 0; i <
		// DefineConstants.size_uchar + 1; i++)
		for (int i = 0; i < DefineConstants.size_uchar + 1; i++)
			C[i] = 0;
		// -ORIGINAL LINE: for (unsigned int i = 0; i < n; ++i)
		for (int i = 0; i < n; ++i)
			C[X[i]]++;
		prev = C[0];
		C[0] = 0;
		System.out.println("Build 1");
		// -ORIGINAL LINE: for (unsigned int i = 1; i <
		// DefineConstants.size_uchar + 1; i++)
		for (int i = 1; i < DefineConstants.size_uchar + 1; i++) {
			tmp = C[i];
			C[i] = C[i - 1] + prev;
			prev = tmp;
		}
		// -ORIGINAL LINE: SA = new unsigned int[n + 100];
		String adr = "temp.ref";
		String pAdr = "sa_"+ adr + ".sdsl";
		PrintWriter pw = new PrintWriter(new File(adr));
		for(int i = 0 ; i < n ; i++){
			pw.print((int)X[i]);
		}
		pw.flush(); pw.close();
		
		long ram_usage = 3072L << 20;
		String text_fname = adr;
		String out_fname = text_fname + ".sa5";
		String gap_fname = out_fname;
		
		
		
		
		SA = new int[n + 100];
		String saAdr = (mode == 0) ? "HgSuffixArray/hgSuff.bin" : "HgSuffixArray/hgInvSuff.bin";
		File inputFile = new File(saAdr);
		MyBufferedScanner fin = new MyBufferedScanner(new FileInputStream(inputFile));
		final int maxR = 100000;
		System.out.println("Build 2");
		// -ORIGINAL LINE: unsigned int buffer[maxR];
		// - unsigned int idx = 0;
		int[] buffer = new int[maxR];
		int idx = 0;
		while (true) {
			int rd = 0;
			for (int i = 0; i < maxR; i++) {
				try {
					buffer[i] = fin.readInt();
					rd++;
				} catch (ArithmeticException E) {
					rd = i;
					break;
				}
			}
			for (int i = 0; i < rd; i++) {
				SA[idx++] = buffer[i];
			}
			if (rd == 0)
				break;
		}
		System.out.println("Build 3");
		// -ORIGINAL LINE: for (unsigned int i = 0; i < n; i++)
		for (int i = 0; i < n; i++) {
			if (SA[i] == 0)
				T[i] = 0;
			else
				T[i] = remap_reverse[X[SA[i] - 1]];
			if (T[i] == 0)
				zeroPos = i;
		}
		for (int i = 0; i < n; i++) {
			char ch = T[i];
			if (ch == 'a' || ch == 'c' || ch == 'g' || ch == 't') {
				bit[remap[T[i]]].setBit(i);
			}
		}
		for (int i = 0; i < sigma; i++) {
			char ch = remap_reverse[i];
			if (ch == 'a' || ch == 'c' || ch == 'g' || ch == 't') {
				bit[i].setSum();
			}
		}
		System.out.println("Build 4");
		bwt.set(T, n);
		T = null;
		suffixes = new int[((n + DefineConstants.SA_SAMPLERATE - 1) / DefineConstants.SA_SAMPLERATE)];
		// -ORIGINAL LINE: for (unsigned int i = 0; i < n; i++)
		for (int i = 0; i < n; i++) {
			if (i % DefineConstants.SA_SAMPLERATE == 0)
				suffixes[i / DefineConstants.SA_SAMPLERATE] = SA[i];
		}
		fin.close();
		System.out.println("Build 5");
	}

	public final void load(MyBufferedScanner fin) throws IOException {
		for (int i = 0; i < DefineConstants.size_uchar; i++) {
			remap[i] = (char) fin.read();
		}
		sigma = fin.readInt();
		remap_reverse = new char[sigma];
		for (int i = 0; i < sigma; i++) {
			remap_reverse[i] = (char) fin.read();
		}
		n = fin.readInt();
		for (int i = 0; i < DefineConstants.size_uchar; i++) {
			C[i] = fin.readInt();
		}
		suffixes = new int[(n + DefineConstants.SA_SAMPLERATE - 1) / DefineConstants.SA_SAMPLERATE];
		for (int i = 0; i < suffixes.length; i++) {
			suffixes[i] = fin.readInt();
		}
		zeroPos = fin.readInt();

		for (int i = 0; i < DefineConstants.MAXALPH; i++) {
			bit[i] = new BitArray();
			bit[i].load(fin);
		}
		bwt.load(fin);
	}

	public final void save(FileChannel out) throws IOException {
		ByteBuffer bbuf = ByteBuffer.allocate(8);
		bbuf.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < DefineConstants.size_uchar; i++) {
			// out.print((char) remap[i]);
			bbuf.putChar(remap[i]);
			bbuf.flip();
			out.write(bbuf);
			bbuf.clear();
		}
		// out.print(Repository.intToBytes(sigma));
		bbuf.putInt(sigma);
		bbuf.flip();
		out.write(bbuf);
		bbuf.clear();
		for (int i = 0; i < sigma; i++) {
			// out.print((char)remap_reverse[i]);
			bbuf.putChar(remap_reverse[i]);
			bbuf.flip();
			out.write(bbuf);
			bbuf.clear();
		}
		// out.print(Repository.intToBytes(n));
		bbuf.putInt(n);
		bbuf.flip();
		out.write(bbuf);
		bbuf.clear();

		for (int i = 0; i < DefineConstants.size_uchar; i++) {
			// out.print(Repository.intToBytes(C[i]));
			bbuf.putInt(C[i]);
			bbuf.flip();
			out.write(bbuf);
			bbuf.clear();
		}
		for (int i = 0; i < (n + DefineConstants.SA_SAMPLERATE - 1) / DefineConstants.SA_SAMPLERATE; i++) {
			// out.print(Repository.intToBytes(suffixes[i]));
			bbuf.putInt(suffixes[i]);
			bbuf.flip();
			out.write(bbuf);
			bbuf.clear();
		}
		// out.print(Repository.intToBytes(zeroPos));
		bbuf.putInt(zeroPos);
		bbuf.flip();
		out.write(bbuf);
		bbuf.clear();

		for (int i = 0; i < DefineConstants.MAXALPH; i++)
			bit[i].save(out);
		bwt.save(out);
		// out.close();
	}

	public final char charAt(int pos) {
		if (pos == zeroPos)
			return 0;
		if (bwt.sz != 0)
			return bwt.charAt(pos);
		for (int i = 0; i < 4; i++) {
			if (bit[remap[dna[i]]].getPos(pos) > 0)
				return dna[i];
		}
		return 'n';
	}

	public final int locateRow(int idx) {
		// need check
		int temp = idx;
		int dist = 0;
		// int cont = 0;
		while (idx % DefineConstants.SA_SAMPLERATE != 0) {
			char ch = charAt(idx);
			// cont++;
			// need check
			if (ch == 110) {
				ch = 97;
			}
			if (ch == 0) {
				return dist;
			}
			int id = remap[ch];
			int bitTemp = bit[id].getRank(idx);
			// System.out.println("idx = "+idx);
			idx = C[id] + bit[id].getRank(idx) - 1;
			dist++;
			if (dist > 2000) {
				dist = Integer.MIN_VALUE;
				break;
			}
		}
		// System.out.println("IDX Cont:\t"+cont);
		return (suffixes[idx / DefineConstants.SA_SAMPLERATE] + dist);// % n;
	}

	public final void updateInterval(RefObject<Integer> l, RefObject<Integer> r, int c) {
		if (l.argvalue == 0)
			l.argvalue = C[c];
		else {
			l.argvalue = C[c] + bit[c].getRank(l.argvalue - 1);
		}
		r.argvalue = C[c] + bit[c].getRank(r.argvalue) - 1;
	}
}