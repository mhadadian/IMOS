package shortRead;

import java.io.Serializable;

public class compressedArray implements Serializable{
	// ORIGINAL LINE: unsigned long long* row = NULL;
	// ORIGINAL LINE: unsigned long long HASHCode;
	// ORIGINAL LINE: unsigned short rowSize;
	public long[] row;
	public long HASHCode;
	public int rowSize;

	public compressedArray() {
	}

	// ORIGINAL LINE: compressedArray( vector<unsigned short> _row, unsigned
	// short _base )
	public compressedArray(java.util.ArrayList<Short> _row, short _base) {

		// - ORIGINAL LINE: unsigned short base = _base;
		// - ORIGINAL LINE: unsigned char cellWidth = 0;
		// - ORIGINAL LINE: unsigned char cntInRow = 0;
		short base = _base;
		byte cellWidth = 0;
		byte cntInRow = 0;
		while ((1 << cellWidth) - 1 < base + 1)
			cellWidth++;
		HASHCode = 0;
		cntInRow = (byte) (64 / cellWidth);

		// - ORIGINAL LINE: unsigned int rowSize = (_row.size() + cntInRow - 1)
		// / (cntInRow);
		rowSize = (_row.size() + cntInRow - 1) / (cntInRow);
		// cout << (int)cellWidth << ' ' << (int)cntInRow << endl;

		// - ORIGINAL LINE: row = new unsigned long long[rowSize];
		row = new long[rowSize];

		for (int i = 0; i < rowSize; i++)
			row[i] = 0;
		for (int i = 0; i < _row.size(); i += cntInRow) {
			for (int j = Math.min((int) _row.size() - 1, (int) (i + cntInRow - 1)); j >= i; j--) {
				// HASHCode = HASHCode * seed3 + _row[j];
				int idx = i / (cntInRow);
				row[idx] <<= (cellWidth);
				row[idx] += _row.get(j);
			}
		}
		for (int i = 0; i < rowSize; i++)
			HASHCode = HASHCode * DefineConstants.seed3 + row[i];
	}

	public compressedArray(compressedArray m) {
		HASHCode = m.HASHCode;
		rowSize = m.rowSize;
		// ORIGINAL LINE: row = new unsigned long long[rowSize];
		row = new long[rowSize];
		for (int i = 0; i < rowSize; i++)
			row[i] = m.row[i];
	}

	// -ORIGINAL LINE: unsigned short getCell(int pos, unsigned short base)
	public final short getCell(int pos, short base) {
		// -ORIGINAL LINE: unsigned char cellWidth = 0;
		byte cellWidth = 0;
		// -ORIGINAL LINE: unsigned char cntInRow = 0;
		byte cntInRow = 0;
		while ((1 << cellWidth) - 1 < base + 1)
			cellWidth++;
		cntInRow = (byte) (64 / cellWidth);
		int idx = pos / (cntInRow);
		// - The right shift operator was replaced by Java's logical right shift
		// operator since the left operand was originally of an unsigned type,
		// but you should confirm this replacement:
		return (short) ((row[idx] >>> (cellWidth * (pos % cntInRow))) & ((1 << cellWidth) - 1));
	}

	// -ORIGINAL LINE: vector<unsigned short> getRow(unsigned short base)
	public final java.util.ArrayList<Short> getRow(short base) {
		// -ORIGINAL LINE: vector<unsigned short> ret(2 * base + 1);
		int retSize = 2 * base + 1;
		java.util.ArrayList<Short> ret = new java.util.ArrayList<Short>(retSize);
		for (int i = 0; i < retSize; i++)
			ret.add(getCell(i, base));
		return ret;
	}

	public boolean equalsTo(compressedArray m) {
		return HASHCode == m.HASHCode;
	}

	// copy/move constructor is called to construct arg
	public final compressedArray copyFrom(compressedArray arg) {
		HASHCode = arg.HASHCode;
		rowSize = arg.rowSize;
		// ORIGINAL LINE: row = new unsigned long long[rowSize];
		row = new long[rowSize];
		for (int i = 0; i < rowSize; i++)
			row[i] = arg.row[i];
		return this;
	}

	public int getHachCode() {
		return (int) HASHCode;

	}

	public void dispose() {
		row = null;
	}
}