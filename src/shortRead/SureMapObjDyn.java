package shortRead;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import bio.ALRecord;
import bio.ASRecord;
import bio.Arguments;
//import bio.LocalAligner;
import bio.Path;

public class SureMapObjDyn implements Serializable
{

	public BWT fmIdx = new BWT();
	public BWT revIdx = new BWT();
	public CompressedString Ref = new CompressedString();

	/* runing options */
	//int globalMode = 0;
//	public static int mc = 10;
	public double AcceptableErr = 0.3;
	public int core = 1;
	public int globalMaxReport = 1;
	public int[] maxReport;
	public boolean globalBestOne = false;
	public String globalMode = "normal";
	public boolean[] bestOne;
	public double globalNoisePercent = -0.1;
	public double[] noisePercent;
	public int globalMaxDiffMismatch = 1;
	public int[] maxDiffMismatch;
	public int globalMaxDiffEdit = 1;
	public int[] maxDiffEdit;
	public int globalGap = 0;
	public int[] gap;
	public int globalUniqeOption = 0;
	public int globalLongReadNoice = 30;
	public int[] uniqeOption;
	public String outputAdr = "report.sam";
	public boolean longRead = false;
	public int[] mxFailed ;
	/*      */



	/********** GLOBAL VARIABLES ********************/
	public bwtNode[][] indexes;
	public ArrayList< bwtNode >[] threadResults;

	public HASHNode[] HASH = new HASHNode[DefineConstants.seed2];
	public boolean[] isSetHASH = new boolean[DefineConstants.seed2];
	
	public String[] readsPerThread ;
	//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	//ORIGINAL LINE: unsigned char nonComplete[DefineConstants.MAXTHREADS][DefineConstants.MAXINPLEN * 4];
	public char[][] nonComplete ;
	public int[] leftIndex;
	public int[] rightIndex;
	public char[] dna = {'a','c','g','t'};//"acgt";
	public String tst = new String(new char[100]);
	public TreeMap< pair< Integer, Integer >, Integer >[][] failedTry;
	public ArrayList< mappingInfo > toWrite = new ArrayList< mappingInfo >();

	public ArrayList< String > refNames = new ArrayList< String >();
	public ArrayList< String >[] findAllStrings ;

	public ArrayList< Integer > refOffSets = new ArrayList< Integer >();
	public ArrayList< Long > ms = new ArrayList< Long >();

	public ArrayList<mappingInfo>[] results;


	public int cntReads;
	public long sptr = 0;
	public long vis = 0;
	public long[] mch = {0};
	public long[] lp ;
	public long totReads = 0;
	//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	//ORIGINAL LINE: unsigned long long needMask[64];
	public long[] needMask = new long[64];

	public long[][] zeroGen = new long[64][64];
	public long[][][][] windowMask ;
	public int[] Flag ;
	public int[][][] dbound ;
	public int gg = 1;
	public int sampleRate = 11;
	public int MAXD = 0;
	public int debg = 0;
	public int cntDFS = 0;
	public int cntBut = 0;
	public int hit = 0;
	public int unhit = 0;
	public int rc = 0;
	/*short wdp[MAXTHREADS][MAXINPLEN][MAXINPLEN];
	char Prev[MAXTHREADS][MAXINPLEN][MAXINPLEN];
	*/
	public int[] drX = {0, -1, -1};
	public int[] drY = {-1, -1, 0};
	public int totChar;
	public int[] indexesPtr;
	public int dbg = 0;
	public int loopTh = 100;
	public int[] resPtr;
	public int[] direction;

	//map< int, int > dbound[MAXTHREADS][MAXMASK];
	public int readPtr = 0;

	//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	//ORIGINAL LINE: unsigned int refLen;
	public int refLen;
	//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	//ORIGINAL LINE: unsigned int preSearched[DefineConstants.MAXTHREADS][DefineConstants.MAXMASK * 4 + 1];
	public int[][] preSearched;
	public boolean[] forceStop;
	public boolean onlyMis = true;
	public boolean[] threadJobDone = new boolean[100];
	
	
	public Semaphore mutex;
	public Semaphore mutexTokens;
	public Queue<Integer> tokens;
	public Semaphore listSemaphore; 
	public Arguments Args;
	
	public boolean fast = false; 
	/**
	 * @throws IOException ***************************************************************/
	
	
	public void init(Arguments ARG, String args[]) throws IOException{
		Args = ARG;
		core = ARG.getCore();
		fast = ARG.isFast();
		int MAXTHREADS = core;
		mutex = new Semaphore(core);
		mutexTokens = new Semaphore(1);
		listSemaphore = new Semaphore(1);
		tokens = new LinkedList<Integer>();
		for(int i =0 ; i < core ; i++){
			tokens.add(i);
		}
		maxReport = new int[MAXTHREADS];
		globalBestOne = false;
		globalMode = "normal";
		bestOne = new boolean[MAXTHREADS];
		globalNoisePercent = -0.1;
		noisePercent = new double[MAXTHREADS];
		globalMaxDiffMismatch = 1;
		maxDiffMismatch = new int[MAXTHREADS];
		globalMaxDiffEdit = 1;
		maxDiffEdit = new int[MAXTHREADS];
		globalGap = 0;
		gap = new int[MAXTHREADS];
		globalUniqeOption = 0;
		globalLongReadNoice = 30;
		uniqeOption = new int[MAXTHREADS];
		outputAdr = "report.sam";
		longRead = false;

		mxFailed = new int[MAXTHREADS];
		/*      */

		/********** GLOBAL VARIABLES ********************/
		indexes = new bwtNode[MAXTHREADS][DefineConstants.MAXINDXES];
		threadResults = new ArrayList[DefineConstants.MAXTHREADS];

		HASH = new HASHNode[DefineConstants.seed2];
		isSetHASH = new boolean[DefineConstants.seed2];

//		reads = new String[DefineConstants.MAXREADSIZE + 1000];
//		qc = new String[DefineConstants.MAXREADSIZE + 1000];
//		readsName = new String[DefineConstants.MAXREADSIZE + 1000];
		readsPerThread = new String[MAXTHREADS];
		//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
		//ORIGINAL LINE: unsigned char nonComplete[DefineConstants.MAXTHREADS][DefineConstants.MAXINPLEN * 4];
		nonComplete = new char[MAXTHREADS][DefineConstants.MAXINPLEN * 4];
		leftIndex = new int[MAXTHREADS];
		rightIndex = new int[MAXTHREADS];
		tst = new String(new char[100]);
		failedTry = new TreeMap[MAXTHREADS][DefineConstants.MAXMASK];
		toWrite = new ArrayList< mappingInfo >();

		refNames = new ArrayList< String >();
		findAllStrings = new ArrayList[MAXTHREADS];

		refOffSets = new ArrayList< Integer >();
		ms = new ArrayList< Long >();

		results = new ArrayList[MAXTHREADS];
		lp = new long[MAXTHREADS];
		
		//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
		//ORIGINAL LINE: unsigned long long needMask[64];
		needMask = new long[64];

		zeroGen = new long[64][64];
		windowMask = new long[MAXTHREADS][6][DefineConstants.MAXMASK][DefineConstants.MAXMASK];
		Flag = new int[MAXTHREADS];
		dbound = new int[MAXTHREADS][DefineConstants.MAXMASK][DefineConstants.MAXMASK];
		gg = 1;
		sampleRate = 11;
		MAXD = 0;
		debg = 0;
		cntDFS = 0;
		cntBut = 0;
		hit = 0;
		unhit = 0;
		rc = 0;
		indexesPtr = new int[MAXTHREADS];
		dbg = 0;
		loopTh = 100;
		resPtr = new int[MAXTHREADS];
		direction = new int[MAXTHREADS];

		readPtr = 0;
		
		preSearched = new int[MAXTHREADS][DefineConstants.MAXMASK * 4 + 1];

		forceStop = new boolean[MAXTHREADS];
		onlyMis = true;
		threadJobDone = new boolean[100];
		
		
		String idxname;
		String qryname;
		
//		String argTemp [] = {"-g","-a", "-k","10", "-t", "1", "-e", "0.05", "-m","fast", "-o", "output.sam", "chr19.fa", "query.fastq"};
//		String argTemp [] = {"-g","-a", "-t", "1", "-e", "0.05", "-m","fast", "-o", "output.sam", "chr19.fa", "query.fastq"};
//		String argTemp [] = {"-b", "-g", "-c", "1", "-o", "output.sam", "chr19.fa", "query.fastq"};
//		args = argTemp;
		int argc = args.length;
		/* parse command line parameter */
		if (argc < 3) {
			print_usage("SureMap");
			System.exit(1);
		}
		for (int i = 0; i < argc; i++)
			args[i] = args[i];
		int argPtr = 0;
		boolean isSetD = false;
		
		while (argPtr < argc && args[argPtr].charAt(0) == '-') {
			if (args[argPtr].length() != 2) {
				print_usage(args[0]);
				System.exit(1);
			}
			char option = args[argPtr].charAt(1);
			argPtr++;
			if (argPtr >= argc) {
				print_usage("sureMap.java");
				System.exit(1);
			}
			if (option == 'b') {
				globalBestOne = true;
				globalMaxReport = DefineConstants.MAXREPORTSIZE;
				continue;
			} else if (option == 'g') {
				globalGap = 1;
				continue;
			} else if (option == '1') {
				globalGap = 0;
				continue;
			} else if (option == 'a') {
				globalMaxReport = DefineConstants.MAXREPORTSIZE;
				continue;
			} else if (option == 'u') {
				globalUniqeOption = 1;
				globalMaxReport = 2;
				continue;
			}
			String operand = args[argPtr++];
			if (option == 'k') {
				globalMaxReport = Integer.parseInt(operand);
			} else if (option == 'e') {
				globalNoisePercent = Double.parseDouble(operand);
//				AcceptableErr = 3*globalNoisePercent;
			} else if (option == 'v') {
				globalMaxDiffEdit = Byte.parseByte(operand);
				isSetD = true;
			} else if (option == 'o') {
				outputAdr = operand;
			}
			else if (option == 'm')
			{
				globalMode = (operand);
				if (!globalMode.equals("normal") && !globalMode.equals("fast") && !globalMode.equals("sensitive") && !globalMode.equals("very-sensitive"))
				{
					print_usage("SureMap.java");
					System.exit(1);
				}
			}
			else
			{
				print_usage("SureMap.java");
				System.exit(1);
			}
		}
		if (globalNoisePercent < 0 && !isSetD)
			globalNoisePercent = 0.05;
		if (core < 0)
			core = 1;
		if (core > DefineConstants.MAXTHREADS)
			core = DefineConstants.MAXTHREADS;
		idxname = qryname = null;
		/* read ref name */
		if (argPtr < argc)
			idxname = args[argPtr++];

		/* read filenames */
		if (argPtr < argc)
		{
			qryname = args[argPtr++];
		}

		if (qryname == null || idxname == null)
		{
			print_usage("SureMap.java");
			System.exit(1);
		}
		String refPrefix = idxname;
		String fastqAdr = qryname;
		String fwAdr = refPrefix + ".fm";
		String rvAdr = refPrefix + ".rev.fm";
		String rfInf = refPrefix;
		loadRef(rfInf);
		/* load index */
		File inputFile = new File(fwAdr);
		MyBufferedScanner fmIdxIn = new MyBufferedScanner(new FileInputStream(inputFile));
		fmIdx.load(fmIdxIn);
		File inputFile2 = new File(rvAdr);
		MyBufferedScanner revIdxIn = new MyBufferedScanner(new FileInputStream(inputFile2));
		revIdx.load(revIdxIn);

		System.out.println("Reference Loaded Succesfully\n");
		totChar = fmIdx.sigma;
		
	}
	
	public void destroy() throws IOException{
		Args = null;
		mutex = null;
		mutexTokens = null;
		listSemaphore = null;
		tokens = null;
		maxReport = null;
		bestOne = null;
		noisePercent = null;
		maxDiffMismatch = null;
		maxDiffEdit = null;
		globalGap = 0;
		gap = null;
		globalUniqeOption = 0;
		globalLongReadNoice = 30;
		uniqeOption = null;
		outputAdr = null;
		longRead = false;

		mxFailed = null;
		/*      */

		/********** GLOBAL VARIABLES ********************/
		indexes = null;
		threadResults = null;

		HASH = null;
		isSetHASH = null;

//		reads = new String[DefineConstants.MAXREADSIZE + 1000];
//		qc = new String[DefineConstants.MAXREADSIZE + 1000];
//		readsName = new String[DefineConstants.MAXREADSIZE + 1000];
		readsPerThread = null;
		//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
		//ORIGINAL LINE: unsigned char nonComplete[DefineConstants.MAXTHREADS][DefineConstants.MAXINPLEN * 4];
		nonComplete = null;
		leftIndex = null;
		rightIndex = null;
		tst = null;
		failedTry = null;
		toWrite = null;

		refNames = null;
		findAllStrings = null;

		refOffSets = null;
		ms = null;

		results = null;
		lp = null;
		
		//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
		//ORIGINAL LINE: unsigned long long needMask[64];
		needMask = null;

		zeroGen = null;
		windowMask = null;
		Flag = null;
		dbound = null;
		indexesPtr = null;
		resPtr = null;
		direction = null;

		readPtr = 0;
		
		preSearched = null;

		forceStop = null;
		threadJobDone = null;
		/* load index */
		fmIdx=null;
		revIdx=null;
		Ref = null;
		System.gc();
		System.out.println("Refrence Destroyed from driver");
	}
	
	public  long depth;
	
	//ORIGINAL LINE: bool dfsButtumUpWithGap(bwtNode curMap, int leftIdx, int rightIdx, int curRow, int tIdx, vector<unsigned short>& curRowDp, int maxEditDistance, int leftToRight, BWT& fm, vector<pair< intervalNode, intervalNode >>& path, int idxPath, int DArray[], int lastAc, int lower, bool isChecked );

	public boolean dfsButtumUpWithGap(bwtNode curMap, int leftIdx, int rightIdx, int curRow, int tIdx, ArrayList<Short> curRowDp, int maxEditDistance, int leftToRight, BWT fm, ArrayList<pair<intervalNode, intervalNode>> path, int idxPath, int[] DArray, int lastAc, int lower, boolean isChecked) {
		depth++;
		rc++;
		int mask = path.get(idxPath).first.mask;
		int cm = curRowDp.get(maxEditDistance); //dpRow.getCell( maxEditDistance, maxEditDistance );
		//need check
		pair<Integer,Integer> tempPair = new pair< Integer, Integer >(curRow,cm);
		if(failedTry[tIdx][mask].containsKey(tempPair))
		if (failedTry[tIdx][mask].get(tempPair) >= mxFailed[tIdx] && (dbound[tIdx][mask][curRow] < cm))
		{
			return false;
		}
		else
			return false;
		ArrayList<Integer> ups = new java.util.ArrayList<Integer>();
		if (resPtr[tIdx] >= maxReport[tIdx])
			return true;
		int nextRowNum = curRow + 1;
		int len = rightIdx - leftIdx + 1;
		nextState[] nxState = new nextState[6];
		for (int i = 0; i < totChar; i++)
			nxState[i] = new nextState(maxEditDistance);
		char on = (curMap.first == curMap.second) ? fm.remap[fm.bwt.charAt(curMap.first)] : (char) -1;
		if (on == 0)
			return false;
		int lch = 1;
		int rch = totChar - 1;
		if (on != (char) -1)
			lch = rch = on;
		for (int nextChar = lch; nextChar <= rch; nextChar++) {
			if (fm.remap_reverse[nextChar] == 'n')
				continue;
			// C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no
			// direct equivalent in Java:
			// ORIGINAL LINE: std::pair<unsigned int, unsigned int> nxInterval =
			// getNextInterval(curMap, fm, nextChar);
			pair<Integer, Integer> nxInterval = getNextInterval(curMap, fm, nextChar);
			if (nxInterval.first > nxInterval.second) {
				continue;
			}
			nxState[nextChar].nxInterval = nxInterval;
			int minAcVal = maxEditDistance + 1;
			int chk = maxEditDistance + 1;
			for (int k = 0; k < 2 * maxEditDistance + 1; k++) {
				int col = nextRowNum + (k - maxEditDistance);
				// - ORIGINAL LINE: unsigned short &ref =
				// nxState[nextChar].nextRowDp[k];
				// short ref = nxState[nextChar].nextRowDp.get(k);
				int ref = (maxEditDistance + 1);
				if (col >= 0 && col <= len) {
					if (k > 0) {
						ref = Math.min((1 + nxState[nextChar].nextRowDp.get(k - 1)), ref);
					}
					if (k < curRowDp.size() && col > 0) {
						if (leftToRight == 1) {
							//
							ref = Math.min(
									(((nextChar == fm.remap[readsPerThread[tIdx].charAt(leftIdx + col - 1)]) ? 0 : 1)
											+ curRowDp.get(k)),
									ref);
						} else {
							ref = Math.min(
									(((nextChar == fm.remap[readsPerThread[tIdx].charAt(leftIdx - col + 1)]) ? 0 : 1)
											+ curRowDp.get(k)),
									ref);
						}
					}
					if (k + 1 < curRowDp.size()) {
						ref = Math.min((1 + curRowDp.get(k + 1)), ref);
					}
					if (col < len) {
						minAcVal = Math.min(minAcVal, ref + DArray[len - col - 1]);
					} else {
						minAcVal = Math.min(minAcVal, (int) ref);
					}
					if (col == len && ref <= maxEditDistance) {
						nxState[nextChar].acceptedValue = ref;
					}
				}
				nxState[nextChar].nextRowDp.add((short) ref);
			}
			nxState[nextChar].valid = minAcVal <= maxEditDistance;
		}
		if (on == -1) {
			Arrays.sort(nxState, 0, totChar);
			lch = 0;
			rch = totChar - 1;
		}
		int preVal = resPtr[tIdx];
		boolean useLess = true;
		boolean cn = false;
		boolean shTry = false;
		for (int nextChar = lch; nextChar <= rch; nextChar++) {
			if (nxState[nextChar].valid == false) {
				continue;
			}
			{
				cn = true;
				bwtNode nextMap = new bwtNode(curMap);
				nextMap.first = nxState[nextChar].nxInterval.first;
				nextMap.second = nxState[nextChar].nxInterval.second;
				nextMap.acceptedValue = nxState[nextChar].acceptedValue;
				nextMap.len++;
				boolean cc = true;
				if (nxState[nextChar].acceptedValue != -1 && nxState[nextChar].acceptedValue >= lastAc) {
					shTry = true;
				}
				if (nxState[nextChar].acceptedValue != -1 && nxState[nextChar].acceptedValue > lastAc) {
					cc = false;
				}
				if (leftToRight != 0)
					nonComplete[tIdx][rightIndex[tIdx]++] = fm.remap_reverse[nextChar];
				else
					nonComplete[tIdx][--leftIndex[tIdx]] = fm.remap_reverse[nextChar];
				boolean nx = dfsButtumUpWithGap(new bwtNode(nextMap), leftIdx, rightIdx, nextRowNum, tIdx,
						nxState[nextChar].nextRowDp, maxEditDistance, leftToRight, fm, path, idxPath, DArray,
						(nxState[nextChar].acceptedValue == -1) ? lastAc : nxState[nextChar].acceptedValue, lower, cc);
				if (nx)
					return true;
				
				if (leftToRight != 0)
					rightIndex[tIdx]--;
				else
					leftIndex[tIdx]++;
				
			}
		}
		if (isChecked && (shTry || cn == false) && curMap.acceptedValue != -1 && curMap.acceptedValue <= maxEditDistance
				&& curMap.acceptedValue > lower) {
			bwtNode nextMap = new bwtNode(curMap);
			boolean nx = buttumUpMapping(new bwtNode(nextMap), path, idxPath - 1, leftToRight, tIdx,0);
			if (nx)
				return true;
		}
		if (resPtr[tIdx] > preVal)
			useLess = false;
		if (useLess)
		{

			dbound[tIdx][mask][curRow] = Math.min(dbound[tIdx][mask][curRow], cm);
			/////// NEED CHECK
			pair<Integer, Integer> TP = new pair<Integer, Integer>(curRow,cm);
			int base = 0;
			if(failedTry[tIdx][mask].containsKey(TP)){
				base = failedTry[tIdx][mask].get(TP);
			}
			failedTry[tIdx][mask].put(TP, base+1);
//			failedTry[tIdx][mask].put(TP, failedTry[tIdx][mask].get(TP)+1);
		}
		return false;
	}

	public boolean CompressedDfsButtumUpWithGap(bwtNode curMap, int leftIdx, int rightIdx, int curRow, int tIdx, compressedArray dpRow, short maxEditDistance, int leftToRight, BWT fm, java.util.ArrayList<pair< intervalNode, intervalNode >> path, int idxPath, int[] DArray, int lastAc, int lower, boolean isChecked)
	{
		depth++;
		rc++;
		int mask = path.get(idxPath).first.mask;
		int cm = dpRow.getCell(maxEditDistance, maxEditDistance);
		if(failedTry[tIdx][mask].containsKey(new pair< Integer, Integer >(curRow,cm))){
			if (failedTry[tIdx][mask].get(new pair< Integer, Integer >(curRow,cm)) >= mxFailed[tIdx] && (dbound[tIdx][mask][curRow] < cm)){
				return false;
			}
		}
		int tmask = mask;
		if (resPtr[tIdx] >= maxReport[tIdx])
			return true;
		int nextRowNum = curRow + 1;
		int len = rightIdx - leftIdx + 1;
		nextState3[] nxState = new nextState3[6];
		for (int i = 0; i < totChar; i++)
		{
			nxState[i]=new nextState3();
			nxState[i].valid = false;
			nxState[i].acceptedValue = -1;
		}
		char on = (curMap.first == curMap.second) ? fm.remap[fm.bwt.charAt(curMap.first)] : (char) -1;
		if (on == 0)
			return false;
		int lch = 1;
		int rch = totChar - 1;
		if (on != (char) -1)
			lch = rch = on;
		int mid = (leftToRight != 0) ? (leftIdx + nextRowNum - 1) : (rightIdx - nextRowNum + 1);
		for (int nextChar = lch; nextChar <= rch; nextChar++)
		{
			if (fm.remap_reverse[nextChar] == 'n' || nextChar == 0)
			{
				nxState[nextChar].valid = false;
				continue;
			}
			//ORIGINAL LINE: std::pair< unsigned int, unsigned int > nxInterval = getNextInterval(curMap, fm, nextChar);
			pair<Integer, Integer > nxInterval = getNextInterval(curMap, fm, nextChar);
			if (nxInterval.first > nxInterval.second){
				continue;
			}
			nxState[nextChar].nxInterval = nxInterval;
			long realMask = 0;
			try{
				realMask = windowMask[tIdx][nextChar][mask][mid + maxEditDistance];
			}catch (ArrayIndexOutOfBoundsException e) {
//					int a = 1/0;
				//need check Critical Bug
//				System.out.println("Critical");
				return true;
			}
			// ORIGINAL LINE: unsigned int index = (((dpRow.HASHCode * DefineConstants.seed1) + realMask)) % DefineConstants.seed2;
			long HC = dpRow.HASHCode;
			if(dpRow.HASHCode<0){
				int mod = (int) dpRow.HASHCode & 1;
				long HashTemp = dpRow.HASHCode >>> 1;
				BigInteger INT = BigInteger.valueOf(HashTemp).multiply(BigInteger.valueOf(2)).add(BigInteger.valueOf(mod));
				HC = INT.mod(BigInteger.valueOf(DefineConstants.seed2)).longValue();
			}
			//// error prone point
			int index = (int) (((((HC % DefineConstants.seed2) * (DefineConstants.seed1% DefineConstants.seed2)) + realMask% DefineConstants.seed2)) % DefineConstants.seed2);
//			try {
//				mutexHash.acquire();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				System.out.println("ACQUIRE PROBLEM");
//				e.printStackTrace();
//			}
//			System.out.println(tIdx+"CAME");
//			while(HASH[index]== null || HASH[index].to == null || HASH[index].to.rowSize==0 || !isSetHASH[index]){
//			while(!isSetHASH[index]){System.out.println(tIdx+"-WAITING on "+index);
			if (!isSetHASH[index] || HASH[index].mask != dpRow.HASHCode || maxEditDistance != HASH[index].base || realMask != HASH[index].m2) {
				unhit++;
				// -ORIGINAL LINE: vector<unsigned short> curRowDp = dpRow.getRow(maxEditDistance);
				// -ORIGINAL LINE: vector<unsigned short> nextRowDp(2 * maxEditDistance + 1, maxEditDistance + 1);
				ArrayList<Short> curRowDp = dpRow.getRow(maxEditDistance);
				ArrayList<Short> nextRowDp = new ArrayList<Short>();
				//////NEED CHECK
//				 for(int i =0 ; i < 2*maxEditDistance+1 ; i++){
//				 			nextRowDp.add((short)(maxEditDistance + 1));
//				 }
				int minAcVal = maxEditDistance + 1;
				int chk = maxEditDistance + 1;
				for (int k = 0; k < 2 * maxEditDistance + 1; k++) {
					int col = nextRowNum + (k - maxEditDistance);
					int ref = 1 + maxEditDistance;
					ref = Math.min((maxEditDistance + 1), (short) (1 + curRowDp.get(k)));
					if (k > 0) {
						ref = Math.min((1 + nextRowDp.get(k - 1)), ref);
					}
					int rIdx = (leftToRight != 0) ? (leftIdx + col - 1) : (rightIdx - col + 1);
					if (rIdx >= leftIdx && rIdx <= rightIdx) {
						ref = Math.min((((nextChar == fm.remap[readsPerThread[tIdx].charAt(rIdx)]) ? 0 : 1) + curRowDp.get(k)),ref);
					}
					if (k + 1 < curRowDp.size()) {
						ref = Math.min((1 + curRowDp.get(k + 1)), ref);
					}
					minAcVal = Math.min(minAcVal, (int) ref);
					if (col == len && ref <= maxEditDistance) {
						nxState[nextChar].acceptedValue = ref;
					}
					nextRowDp.add((short) ref);
				}
				
//				NEED CHECK
				HASH[index]=new HASHNode();
				HASH[index].mask = dpRow.HASHCode;
				HASH[index].to = new compressedArray(nextRowDp, (short) maxEditDistance);
				HASH[index].base = maxEditDistance;
				HASH[index].isValid = minAcVal <= maxEditDistance;
				HASH[index].m2 = realMask;
				isSetHASH[index] = true;
			}
//			}
			if (HASH[index].isValid == false)
				continue;
//			mutexHash.release();
			nxState[nextChar].valid = true;
//			while(HASH[index].to == null || HASH[index].to.rowSize==0);
			
//			if(HASH[index].to.rowSize==0){
//				System.out.println("YA FATEMEH");
//			}
			nxState[nextChar].nextRowDp = new compressedArray(HASH[index].to);
			
//			if(nxState[nextChar].nextRowDp.row.length<1){
//				System.out.println(HASH[index].to.rowSize+" "+nxState[nextChar].nextRowDp.rowSize);
//			}
			if (Math.abs(len - nextRowNum) <= maxEditDistance)
			{
				int val = HASH[index].to.getCell(len - nextRowNum + maxEditDistance, maxEditDistance);
				if (val <= maxEditDistance)
					nxState[nextChar].acceptedValue = val;
			}
		}
		if (on == (char) -1)
		{
			//sort( nxState + 1, nxState + totChar );
			lch = 1;
			rch = totChar - 1;
		}
		boolean cn = false;
		boolean shTry = false;
		boolean lq = false;
		int preVal = resPtr[tIdx];
		boolean useLess = true;
		for (int nextChar = lch; nextChar <= rch; nextChar++){
			if (nxState[nextChar].valid == false)
				continue;
			{
				cn = true;
				bwtNode nextMap = new bwtNode(curMap);
				nextMap.first = nxState[nextChar].nxInterval.first;
				nextMap.second = nxState[nextChar].nxInterval.second;
				nextMap.acceptedValue = nxState[nextChar].acceptedValue;
				nextMap.len++;
				boolean cc = true;
				if (nxState[nextChar].acceptedValue != -1 && nxState[nextChar].acceptedValue >= lastAc)
				{
						shTry = true;
				}
				if (nxState[nextChar].acceptedValue != -1 && nxState[nextChar].acceptedValue > lastAc)
				{
						cc = false;
				}
				if (nxState[nextChar].acceptedValue != -1 && nxState[nextChar].acceptedValue < lastAc)
				{
						lq = true;
				}
				if (leftToRight != 0)
					nonComplete[tIdx][rightIndex[tIdx]++] = fm.remap_reverse[nextChar];
				else
					nonComplete[tIdx][--leftIndex[tIdx]] = fm.remap_reverse[nextChar];

				//need check
//ORIGINAL LINE: boolean nx = CompressedDfsButtumUpWithGap(nextMap, leftIdx, rightIdx, nextRowNum, tIdx, nxState[nextChar].nextRowDp, maxEditDistance, leftToRight, fm, path, idxPath, DArray, (nxState[nextChar].acceptedValue == -1) ? lastAc : nxState[nextChar].acceptedValue, lower, cc);
				boolean nx = CompressedDfsButtumUpWithGap(nextMap, leftIdx, rightIdx, nextRowNum, tIdx, nxState[nextChar].nextRowDp, maxEditDistance, leftToRight, fm, path, idxPath, DArray, (nxState[nextChar].acceptedValue == -1) ? lastAc : nxState[nextChar].acceptedValue, lower, cc);
				if (leftToRight != 0)
					rightIndex[tIdx]--;
				else
					leftIndex[tIdx]++;
				if (nx){
					return true;
				}
			}
		}
		if (isChecked && (shTry || cn == false) && curMap.acceptedValue != -1 && curMap.acceptedValue <= maxEditDistance && curMap.acceptedValue > lower)
		{
			bwtNode nextMap = new bwtNode(curMap);
			boolean nx = buttumUpMapping(nextMap, path, idxPath - 1, leftToRight, tIdx,1);
			if (nx)
			{
				//failedTry[tIdx][mask][curRow][cm] = 0;
				return true;
			}
		}
		if (resPtr[tIdx] > preVal)
			useLess = false;
		if (useLess)
		{
				dbound[tIdx][mask][curRow] = Math.min(dbound[tIdx][mask][curRow], cm);
				//need check
				pair<Integer, Integer> TP = new pair<Integer, Integer>(curRow,cm);
				int base = 0;
				if(failedTry[tIdx][mask].containsKey(TP)){
					base = failedTry[tIdx][mask].get(TP);
				}
				failedTry[tIdx][mask].put(TP, base+1);
		}
		else
			failedTry[tIdx][mask].put(new pair< Integer, Integer >(curRow,cm), 0);
		return false;
	}

	public boolean dfsButtumUp(bwtNode curMap, int leftIdx, int rightIdx, int curRow, int tIdx, int curError, int maxEditDistance, int leftToRight, BWT fm, java.util.ArrayList<pair< intervalNode, intervalNode >> path, int idxPath, int[] DArray, int lastAc, int lower, boolean isChecked)
	{
		int mask = path.get(idxPath).first.mask;
		int cm = curError; //dpRow.getCell( maxEditDistance, maxEditDistance );
		if(failedTry[tIdx][mask].containsKey(new pair<Integer, Integer>(curRow,cm))){
			if (failedTry[tIdx][mask].get(new pair<Integer, Integer>(curRow,cm)) >= mxFailed[tIdx] && (dbound[tIdx][mask][curRow] < cm)){
				return false;
			}
		}
		if (resPtr[tIdx] >= maxReport[tIdx])
			return false;
		//cerr << curMap.len << ' ' << rightIndex[tIdx] - leftIndex[tIdx] << endl;
		int len = (rightIdx - leftIdx + 1);
		int nextRowNum = curRow + 1;
		nextState2[] nxState = new nextState2[10];
		for (int i = 0; i < totChar; i++)
			nxState[i] = new nextState2(maxEditDistance);
		char on = (curMap.first == curMap.second) ? fm.remap[fm.bwt.charAt(curMap.first)] : (char) -1;
		int lch = 1;
		int rch = totChar - 1;
		if (on != (char) -1)
			lch = rch = on;
		int preVal = resPtr[tIdx];
		boolean useLess = true;
		for (int nextChar = lch; nextChar <= rch; nextChar++)
		{
			if (fm.remap_reverse[nextChar] == 'n')
					continue;
	//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
	//ORIGINAL LINE: std::pair< unsigned int, unsigned int > nxInterval = getNextInterval(curMap, fm, nextChar);
			pair<Integer, Integer > nxInterval = getNextInterval(curMap, fm, nextChar);
			if (nxInterval.first > nxInterval.second)
			{
				continue;
			}
			int col = nextRowNum;
			nxState[nextChar].nxInterval = nxInterval;
			int minAcVal = maxEditDistance + 1;
			int chk = maxEditDistance + 1;
			int ref = curError + 1;
			if (leftToRight == 1)
				ref = Math.min((((nextChar == fm.remap[readsPerThread[tIdx].charAt(leftIdx + col - 1)]) ? 0 : 1) + curError), ref);
			else
				ref = Math.min((((nextChar == fm.remap[readsPerThread[tIdx].charAt(rightIdx - col + 1)]) ? 0 : 1) + curError), ref);
			if (ref <= maxEditDistance)
			{
				nxState[nextChar].valid = true;
				nxState[nextChar].error = ref;
				if (col == len)
						nxState[nextChar].acceptedValue = ref;
			}
		}
		if (on == (char) -1)
		{
				/*for( int i = 0; i < totChar; i++ )
				    if( nxState[i].error == curError )
				        swap( nxState[0], nxState[i] );
				*/
				lch = 1;
				rch = totChar - 1;
		}
		for (int nextChar = lch; nextChar <= rch; nextChar++)
		{
			if (nxState[nextChar].valid == false)
				continue;
			if (nxState[nextChar].acceptedValue != -1 && nxState[nextChar].acceptedValue > lower)
			{
				if (leftToRight != 0)
					nonComplete[tIdx][rightIndex[tIdx]++] = fm.remap_reverse[nextChar];
				else
					nonComplete[tIdx][--leftIndex[tIdx]] = fm.remap_reverse[nextChar];

//C++ TO JAVA CONVERTER WARNING: The following line was determined to be a copy constructor call - this should be verified and a copy constructor should be created if it does not yet exist:
//ORIGINAL LINE: bwtNode nextMap = curMap;
				bwtNode nextMap = new bwtNode(curMap);
				nextMap.first = nxState[nextChar].nxInterval.first;
				nextMap.second = nxState[nextChar].nxInterval.second;
				nextMap.len++;
				nextMap.acceptedValue = nxState[nextChar].acceptedValue;
//C++ TO JAVA CONVERTER WARNING: The following line was determined to be a copy constructor call - this should be verified and a copy constructor should be created if it does not yet exist:
//ORIGINAL LINE: boolean nx = buttumUpMapping(nextMap, path, idxPath - 1, leftToRight, tIdx);
				boolean nx = buttumUpMapping(new bwtNode(nextMap), path, idxPath - 1, leftToRight, tIdx,2);
				if (leftToRight != 0)
					rightIndex[tIdx]--;
				else
					leftIndex[tIdx]++;
				if (nx)
					return true;
			}
			{
				if (nxState[nextChar].acceptedValue != -1)
					continue;
				if (leftToRight != 0)
					nonComplete[tIdx][rightIndex[tIdx]++] = fm.remap_reverse[nextChar];
				else
					nonComplete[tIdx][--leftIndex[tIdx]] = fm.remap_reverse[nextChar];
				//cout << (char)fm.remap_reverse[nextChar] << endl;

//C++ TO JAVA CONVERTER WARNING: The following line was determined to be a copy constructor call - this should be verified and a copy constructor should be created if it does not yet exist:
//ORIGINAL LINE: bwtNode nextMap = curMap;
				bwtNode nextMap = new bwtNode(curMap);
				nextMap.first = nxState[nextChar].nxInterval.first;
				nextMap.second = nxState[nextChar].nxInterval.second;
				nextMap.len++;
//C++ TO JAVA CONVERTER WARNING: The following line was determined to be a copy constructor call - this should be verified and a copy constructor should be created if it does not yet exist:
//ORIGINAL LINE: boolean nx = dfsButtumUp(nextMap, leftIdx, rightIdx, nextRowNum, tIdx, nxState[nextChar].error, maxEditDistance, leftToRight, fm, path, idxPath, DArray, (nxState[nextChar].acceptedValue == -1) ? lastAc : nxState[nextChar].acceptedValue, lower, isChecked);
				boolean nx = dfsButtumUp(new bwtNode(nextMap), leftIdx, rightIdx, nextRowNum, tIdx, nxState[nextChar].error, maxEditDistance, leftToRight, fm, path, idxPath, DArray, (nxState[nextChar].acceptedValue == -1) ? lastAc : nxState[nextChar].acceptedValue, lower, isChecked);
				if (leftToRight != 0)
					rightIndex[tIdx]--;
				else
					leftIndex[tIdx]++;
				if (nx)
					return true;
			}

		}
		if (resPtr[tIdx] > preVal)
			useLess = false;
		if (useLess)
		{
			//if( dbound[tIdx][mask].count( curRow ) == 0 )
			//	dbound[tIdx][mask][curRow] = cm;
			//else
				dbound[tIdx][mask][curRow] = Math.min(dbound[tIdx][mask][curRow], cm);
				pair<Integer, Integer> TP = new pair<Integer, Integer>(curRow,cm);
				int base = 0;
				if(failedTry[tIdx][mask].containsKey(TP)){
					base = failedTry[tIdx][mask].get(TP);
				}
				failedTry[tIdx][mask].put(TP, base+1);
		}
		return false;
	}


//C++ TO JAVA CONVERTER TODO TASK: The original C++ template specifier was replaced with a Java generic specifier, which may not produce the same behavior:
//ORIGINAL LINE: template<typename T>
	public <T> void print(java.util.ArrayList<T> v)
	{
		for (int i = 0; i < v.size(); i++)
		{
			System.out.print(v.get(i));
			System.out.print(' ');
		}
		System.out.print("\n");
	}


	public  boolean isValidDnaCharacter(char ch)
	{
		return (ch == 'a' || ch == 'c' || ch == 'g' || ch == 't');
	}

	public void toLower(RefObject<Character> ch)
	{
		if (ch.argvalue >= 'A' && ch.argvalue <= 'Z')
				ch.argvalue = (char) ('a' + ch.argvalue - 'A');
	}

	// ORIGINAL LINE: inline unsigned int locateBwt(unsigned int idx)
		public int locateBwt(int idx) {
			return fmIdx.locateRow(idx);
		}

		// ORIGINAL LINE: inline unsigned int locateInvBwt(unsigned int idx)
		public int locateInvBwt(int idx) {
			return revIdx.locateRow(idx);
		}

		public boolean newCmp(bwtNode a, bwtNode b) {
			return a.acceptedValue < b.acceptedValue;
		}

		public  pair<String, Integer> getPosition(int offSet) {
			int idx = 0;
			for (idx = 0; idx < refNames.size() - 1; idx++)
				if (offSet >= refOffSets.get(idx) && offSet < refOffSets.get(idx + 1))
					break;
			return new pair<String, Integer>(refNames.get(idx), offSet - refOffSets.get(idx));
		}

//	public static void resetControllers()
//	{
//		for (int i = 0; i < core; i++)
//		{
//			readVector[i] = writeVector[i] = false;
//			readAccess[i] = -1;
//		}
//		for (int i = 0; i < DefineConstants.seed2; i++)
//			reading[i] = writing[i] = false;
//		finished = false;
//	}

		public String reverseComplement(String s) {
			char[] schars = new char[s.length()];
			for (int i = schars.length - 1; i > -1; i--) {
				schars[schars.length - 1 - i] = s.charAt(i);
			}

			for (int i = 0; i < schars.length; i++) {
				if (schars[i] == 'n')
					continue;
				if (schars[i] == 'a')
					schars[i] = 't';
				else if (schars[i] == 't')
					schars[i] = 'a';
				else if (schars[i] == 'c')
					schars[i] = 'g';
				else if (schars[i] == 'g')
					schars[i] = 'c';
			}
			return new String(schars);
		}

		public int getMinEdit(int leftIdx, int rightIdx, int tIdx) {
			// ORIGINAL LINE: unsigned int lb = 0, rb = fmIdx.n - 1;
//			int lb = 0;
//			int rb = fmIdx.n - 1;
			RefObject<Integer> lb =	new RefObject<Integer>(0);
			RefObject<Integer> rb = new RefObject<Integer>(fmIdx.n - 1);
			int z = 0;
			int j = 0;
			for (int i = leftIdx; i <= rightIdx; i++) {
				int id = fmIdx.remap[readsPerThread[tIdx].charAt(i)];
				revIdx.updateInterval(lb, rb, id);
				if (lb.argvalue > rb.argvalue) {
					z++;
					lb.argvalue = 0;
					rb.argvalue = fmIdx.n - 1;
				}
			}
			return z;
		}


	//ORIGINAL LINE: std::pair< unsigned int, unsigned int > getInvToBwt( int tIdx )
	public pair<Integer, Integer> getInvToBwt(int tIdx){
	//ORIGINAL LINE: unsigned int tl = 0, tr = fmIdx.n - 1, iter = 0;
		RefObject<Integer> tl = new RefObject<Integer>(0);
		RefObject<Integer> tr = new RefObject<Integer>(fmIdx.n -1);
		int iter = 0;
		for (int i = rightIndex[tIdx] - 1; i >= leftIndex[tIdx]; i--)
		{
			int id = fmIdx.remap[nonComplete[tIdx][i]];
			fmIdx.updateInterval(tl, tr, id);
		}
	//ORIGINAL LINE: return std::pair< unsigned int, unsigned int >( tl, tr );
		return new pair<Integer, Integer>(tl.argvalue, tr.argvalue);
	}

	//ORIGINAL LINE: std::pair< unsigned int, unsigned int > getBwtToInv( int tIdx )
	public pair<Integer, Integer> getBwtToInv(int tIdx){
	//ORIGINAL LINE: unsigned int tl = 0, tr = fmIdx.n - 1;
		RefObject<Integer> tl = new RefObject<Integer>(0);
		RefObject<Integer> tr = new RefObject<Integer>(fmIdx.n-1);
		for (int i = leftIndex[tIdx]; i < rightIndex[tIdx]; i++)
		{
			int id = fmIdx.remap[nonComplete[tIdx][i]];
			revIdx.updateInterval(tl, tr, id);
		}
	//ORIGINAL LINE: return std::pair< unsigned int, unsigned int >( tl, tr );
		return new pair<Integer, Integer>(tl.argvalue, tr.argvalue);
	}

	//ORIGINAL LINE: std::pair< unsigned int, unsigned int > getInterVal( string& q, BWT& fm )
	public pair<Integer, Integer> getInterVal(String q, BWT fm)
	{
	//ORIGINAL LINE: unsigned int tl = 0, tr = fm.n - 1;
		RefObject<Integer> tl = new RefObject<Integer>(0);
		RefObject<Integer> tr = new RefObject<Integer>(fm.n -1);
		for (int i = 0; i < q.length(); i++)
		{
			int id = fmIdx .remap[q.charAt(i)];
			fm .updateInterval(tl, tr, id);
		}
	//ORIGINAL LINE: return std::pair< unsigned int, unsigned int >( tl, tr );
		return new pair(tl.argvalue, tr.argvalue);
	}

	//ORIGINAL LINE: std::pair< unsigned int, unsigned int > getNextInterval( bwtNode& cur, BWT& fm, int id )
	public pair<Integer, Integer> getNextInterval(bwtNode cur, BWT fm, int id) {
//		int tl = cur.first;
//		int tr = cur.second;
		RefObject<Integer> tl =	new RefObject<Integer>(cur.first);
		RefObject<Integer> tr = new RefObject<Integer>(cur.second);
		fm.updateInterval(tl, tr, id);
		return new pair<Integer, Integer>(tl.argvalue, tr.argvalue);
	}

	public void buildLevstein(String q, boolean REV, int tId, BWT fm) {
		if (REV) {
			char[] schars = new char[q.length()];
			for (int i = schars.length - 1; i > -1; i--) {
				schars[schars.length - 1 - i] = q.charAt(i);
			}
			q = new String(schars);
		}
		// ORIGINAL LINE: unsigned int lb = 0, rb = fm.n - 1;
//		int lb = 0;
//		int rb = fm.n - 1;
		RefObject<Integer> lb =	new RefObject<Integer>(0);
		RefObject<Integer> rb = new RefObject<Integer>(fm.n - 1);
		for (int i = 0; i < q.length(); i++) {
			fm.updateInterval(lb, rb, fm.remap[q.charAt(i)]);
			if (lb.argvalue > rb.argvalue)
				break;
		}
		if (lb.argvalue <= rb.argvalue)
			indexes[tId][indexesPtr[tId]++] = (new bwtNode(lb.argvalue, rb.argvalue, q.length(), 0));
	}

	public void newBuildDArray(int leftIdx, int rightIdx, int st, int dir, int tIdx, int[] DArr, BWT fm, int D) {
		// ORIGINAL LINE: unsigned int lb = 0, rb = fm.n - 1;
//		int lb = 0;
//		int rb = fm.n - 1;
		RefObject<Integer> lb =	new RefObject<Integer>(0);
		RefObject<Integer> rb = new RefObject<Integer>(fm.n - 1);
		int len = rightIdx - leftIdx + 1;
		for (int i = 0; i < len; i++)
			DArr[i] = D + 1;
		int z = 0;
		int j = 0;
		int idx = 0;
		for (int I = st; (I >= leftIdx && I <= rightIdx); I += dir, idx++) {
			int i = fmIdx.remap[readsPerThread[tIdx].charAt(I)];
			fm.updateInterval(lb, rb, i);
			if (lb.argvalue > rb.argvalue) {
				z++;
				j = i + 1;
				lb.argvalue = 0;
				rb.argvalue = fm.n - 1;
			}
			DArr[idx] = z;
		}
	}
	
public ArrayList<Integer> getLastRow(String s1, String s2, int maxDiff, RefObject<Integer> bestEnd)
{
	return getLastRow(s1, s2, maxDiff, bestEnd, 0);
}


//need check : use array instead of ArrayList
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: java.util.ArrayList< int > getLastRow(String s1, String s2, int maxDiff, int& bestEnd, int mode = 0)
	public ArrayList<Integer> getLastRow(String s1, String s2, int maxDiff, RefObject<Integer> bestEnd, int mode){
		ArrayList<Integer>[] lastRows = new ArrayList[2];
//		lastRows[0] = new ArrayList<Integer>(s2.length() + 1, 10 * maxDiff + 1);
		lastRows[0] = new ArrayList<Integer>();
//		lastRows[1] = new ArrayList<Integer>(s2.length() + 1, 10 * maxDiff + 1);
		lastRows[1] = new ArrayList<Integer>();
		for(int i = 0 ; i < s2.length() + 1 ; i++){
			lastRows[1].add(10 * maxDiff + 1);
		}
		int turn = 1;
		int curBest = 10000000;
		for (int i = 0; i < s2.length() + 1; i++)
			lastRows[1 - turn].add(i);
		for (int i = 1; i <= s1.length(); i++){
			int preRow = 1 - turn;
			for (int j = 0; j <= s2.length(); j++){
				if (j < 0 || j > s2.length())
					continue;
				if (j == 0){
					try{
						lastRows[turn].set(j, (mode == 0) ? 0 : i);
					}
					catch (Exception e) {
						System.out.println("Critical: Arraylist Turn Error");
						throw e;
					}
				}
				else
				{
					lastRows[turn].set(j, 10 * maxDiff + 1);
					lastRows[turn].set(j, Math.min(lastRows[preRow].get(j) + 1, lastRows[turn].get(j - 1) + 1));
					lastRows[turn].set(j, Math.min(lastRows[turn].get(j), lastRows[preRow].get(j - 1) + 1));
					if (s1.charAt(i - 1) == s2.charAt(j - 1))
						lastRows[turn].set(j, Math.min(lastRows[turn].get(j), lastRows[preRow].get(j - 1)));
				}
			}
			if (lastRows[turn].get(s2.length()) < curBest){
				bestEnd.argvalue = i;
				curBest = lastRows[turn].get(s2.length());
			}
			turn = 1 - turn;
		}
		return lastRows[1 - turn];
	}

	//need rewriting
	public  int getBestStartingPos(RefObject<String> s1, RefObject<String> s2, int maxDiff)
	{
		//need check
		RefObject<Integer> tempRef_bestEnd = new RefObject<Integer>(0);
		getLastRow(s1.argvalue, s2.argvalue, maxDiff, tempRef_bestEnd, 0);
		int bestEnd = tempRef_bestEnd.argvalue;
		s1.argvalue = s1.argvalue.substring(0, bestEnd);
		s1.argvalue = Repository.reverseString(s1.argvalue);
		s2.argvalue = Repository.reverseString(s2.argvalue);

		RefObject<Integer> tempRef_bestEnd2 = new RefObject<Integer>(bestEnd);
		getLastRow(s1.argvalue, s2.argvalue, maxDiff, tempRef_bestEnd2, 0);
		bestEnd = tempRef_bestEnd2.argvalue;

		int ret = s1.argvalue.length() - bestEnd;
		s1.argvalue = s1.argvalue.substring(0, bestEnd);
		s1.argvalue = Repository.reverseString(s1.argvalue);
		s2.argvalue = Repository.reverseString(s2.argvalue);

		return ret;
	}

	public static String Rev(String s1)
	{
		return Repository.reverseString(s1);
	}

	public static ArrayList<Integer> Rev(ArrayList<Integer> v)
	{
		Collections.reverse(v);
		return v;
	}
public  pair< String, String > Hirschberg(String s1, String s2, int maxDiff)
{
	return Hirschberg(s1, s2, maxDiff, 0);
}


//Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: pair< String, String > Hirschberg(String s1, String s2, int maxDiff, int depth = 0)
	public  pair< String, String > Hirschberg(String s1, String s2, int maxDiff, int depth){
		pair< String, String > ret = new pair< String, String >();
		
		int tmp=0;
		if (s1.length() == 0){
//			String Z = "";
			String W = "";
			for (int i = 0; i < s2.length(); i++)
			{
//				Z += s2.charAt(i);
				W += '-';
			}
			return new pair< String, String >(s1, W);
		}
		else if (s2.length() == 0){
			String Z = "";
//			String W = "";
			for (int i = 0; i < s1.length(); i++)
			{
				Z += '-';
//				W += s1.charAt(i);
			}
			return new pair< String, String >(Z, s1);
		}
		else if (s1.length() == 1){
			char [] w = new char[s2.length()];
			for (int i = 0; i < s2.length(); i++){
//				Z += s2.charAt(i);
//				W += '-';
				w[i]= '-';
			}
			char s1Char = s1.charAt(0);
			int s2Index = s2.indexOf(s1Char);
			if (s2Index != -1)
				w[s2Index] = s1Char;
			else
				w[0] = s1Char;
			return new pair< String, String >(s2, new String(w));
		}
		else if (s2.length() == 1){
			//W = s1;
			char z[] = new char[s1.length()];
			for (int i = 0; i < s1.length(); i++){
//				Z += '-';
				z[i]='-';
			}
			char s2Char = s2.charAt(0);
			int s1Index = s1.indexOf(s2Char);
			if (s1Index != -1)
				z[s1Index] = s2Char;
			else
				z[0] = s2Char;
			return new pair< String, String >(new String(z), s1);
		}
		int xlen = s1.length();
		int xmid = xlen / 2;
		int ylen = s2.length();
		//need check
		RefObject<Integer> tempRef_tmp = new RefObject<Integer>(tmp);
		ArrayList<Integer> ScoreL = getLastRow(s1.substring(0, xmid), s2, maxDiff, tempRef_tmp, 1);
		tmp = tempRef_tmp.argvalue;
		RefObject<Integer> tempRef_tmp2 = new RefObject<Integer>(tmp);
		ArrayList<Integer> ScoreR = Rev(getLastRow(Rev(s1.substring(xmid)), Rev(s2), maxDiff, tempRef_tmp2, 1));
		tmp = tempRef_tmp2.argvalue;

		int ymid = 0;
		int best = 10000000;
		for (int i = 0; i <= s2.length(); i++)
			if (ScoreL.get(i) + ScoreR.get(i) < best)
			{
				ymid = i;
				best = ScoreL.get(i) + ScoreR.get(i);
			}
		pair< String, String > h1 = Hirschberg(s1.substring(0, xmid), s2.substring(0, ymid), maxDiff, depth + 1);
		pair< String, String > h2 = Hirschberg(s1.substring(xmid), s2.substring(ymid), maxDiff, depth + 1);
		return new pair< String, String >(h1.first + h2.first, h1.second + h2.second);
	}

	public void testHeirch(String s1, String s2, int maxDiff)
	{
		System.out.print(s1);
		System.out.print(' ');
		System.out.print(s2);
		System.out.print("\n");
		RefObject<String> S1 = new RefObject<String>(s1);
		RefObject<String> S2 = new RefObject<String>(s2);
		getBestStartingPos(S1, S2, maxDiff);
		s1= S1.argvalue;
		s2= S2.argvalue;
		System.out.print(s1);
		System.out.print(' ');
		System.out.print(s2);
		System.out.print("\n");
		System.out.print(Hirschberg(s1, s2, maxDiff).first);
		System.out.print("\n");
		System.out.print(Hirschberg(s1, s2, maxDiff).second);
		System.out.print("\n");
	}

	public boolean processResult(bwtNode curMap, int tIdx, int preLeftToRight) {
//		System.out.println("Start Proccess Result");
		if (preLeftToRight == 1) {
			pair<Integer, Integer> nx = getInvToBwt(tIdx);
			curMap.first = nx.first;
			curMap.second = nx.second;
		}
//		System.out.println("p 1");
		for (int BwtIdx = curMap.first; BwtIdx <= curMap.second; BwtIdx++) {
			bwtNode newCurMap = new bwtNode(curMap);
			newCurMap.first = newCurMap.second = BwtIdx;
//			System.out.println("Locate Row Started");
			newCurMap.refRow = fmIdx.locateRow(newCurMap.first);
//			System.out.println("Locate Row Ended and For Started");
			boolean valid = true;
			for (int i = 0; i < resPtr[tIdx]; i++) {
				if (Math.abs((long) threadResults[tIdx].get(i).refRow - (long) newCurMap.refRow) <= readsPerThread[tIdx]
						.length()) {
					// max( threadResults[tIdx][i].acceptedValue,
					// newCurMap.acceptedValue ) ){
					if (newCurMap.acceptedValue >= threadResults[tIdx].get(i).acceptedValue) {
						valid = false;
						break;
					} else {
						bwtNode temp = threadResults[tIdx].get(resPtr[tIdx] - 1);
						threadResults[tIdx].set(resPtr[tIdx] - 1, threadResults[tIdx].get(i));
						threadResults[tIdx].set(i, temp);
						threadResults[tIdx].remove(threadResults[tIdx].size() - 1);
						resPtr[tIdx]--;
						i--;
					}
				}
			}
//			System.out.println("FOR ENDED");
			if (valid == false)
				continue;
			newCurMap.flag = (byte) ((direction[tIdx] == 0) ? 0 : 16);
			threadResults[tIdx].add(newCurMap);
			resPtr[tIdx]++;
			if (uniqeOption[tIdx] != 0 && resPtr[tIdx] > 1) {
				forceStop[tIdx] = true;
				resPtr[tIdx] = 0;
//				System.out.println("Proccess Result END 0");
				return true;
			}
			if (resPtr[tIdx] >= maxReport[tIdx]){
//				System.out.println("Proccess Result END 1");
				return true;
			}
		}
//		System.out.println("Proccess Result END 2");
		return false;
	}



	public boolean buttumUpMapping(bwtNode curMap, ArrayList<pair<intervalNode, intervalNode>> path, int idxPath, int preLeftToRight, int tIdx,int Stage) {
//		System.out.println("buttumUpMapping From "+ Stage);
		depth++;
		if (lp[tIdx] > loopTh){
//			System.out.println("buttumUpMapping ENDS 0");
			return false;
		}
		if (resPtr[tIdx] >= maxReport[tIdx]){
//			System.out.println("buttumUpMapping ENDS 1");
			return false;
		}
		cntBut++;
		if (idxPath == -1){
//			System.out.println("buttumUpMapping ENDS 2");
			return processResult(curMap, tIdx, preLeftToRight);
		}
		int curLeftToRight = (path.get(idxPath).first.left < path.get(idxPath).second.left) ? 1 : 0;
		if (curLeftToRight == 1 && preLeftToRight == 0) {
			// ORIGINAL LINE: std::pair<unsigned int, unsigned int> nx =
			// getBwtToInv(curMap.first, curMap.len);
			pair<Integer, Integer> nx = getBwtToInv(tIdx);
			curMap.first = nx.first;
			curMap.second = nx.second;
		}
		if (curLeftToRight == 0 && preLeftToRight == 1) {
			// ORIGINAL LINE: std::pair<unsigned int, unsigned int> nx =
			// getInvToBwt(curMap.first, curMap.len);
			pair<Integer, Integer> nx = getInvToBwt(tIdx);
			curMap.first = nx.first;
			curMap.second = nx.second;
		}
//		System.out.println("BU 1/3");
		int curMask = path.get(idxPath).first.mask;
		byte edit = path.get(idxPath).second.editDistance;
		int len = path.get(idxPath).second.right - path.get(idxPath).second.left + 1;
		int[] DArray = new int[len];
		if (curLeftToRight == 1) {
			newBuildDArray(path.get(idxPath).second.left, path.get(idxPath).second.right, path.get(idxPath).second.left,
					1, tIdx, DArray, revIdx, path.get(idxPath).second.editDistance);
		} else {
			newBuildDArray(path.get(idxPath).second.left, path.get(idxPath).second.right,
					path.get(idxPath).second.right, -1, tIdx, DArray, fmIdx, path.get(idxPath).second.editDistance);
		}
//		System.out.println("BU 2/3");
		if (DArray[path.get(idxPath).second.right - path.get(idxPath).second.left] > edit){
//			System.out.println("buttumUpMapping ENDS 3");
			return false;
		}
		boolean nx = false;
		int prePtVal = resPtr[tIdx];
		if (gap[tIdx] != 0) {
			// ORIGINAL LINE: vector<unsigned short> root(2 * edit + 1, edit +
			// 1);
//			System.out.println("BU 3/3");
			ArrayList<Short> root = new ArrayList<Short>(2 * edit + 1);
			for (int i = 0; i < 2 * edit + 1; i++) {
				root.add((short) (edit + 1));
			}
			for (int i = edit; i < root.size(); i++)
				root.set(i, (short) Math.min(edit + 1, Math.abs(i - edit) + curMap.acceptedValue));
			int lower = (curLeftToRight == 0) ? (curMap.acceptedValue + path.get(idxPath).second.editDistance / 2) : -1;
			curMap.acceptedValue = -1;
//			System.out.println("BU 4/3");
			if (edit <= 30) {
				compressedArray tmp = new compressedArray(root, edit);
				nx = CompressedDfsButtumUpWithGap(new bwtNode(curMap), path.get(idxPath).second.left,
						path.get(idxPath).second.right, 0, tIdx, tmp, edit, curLeftToRight,
						(curLeftToRight == 1) ? revIdx : fmIdx, path, idxPath, DArray, edit + 10, lower, false);
			} else 
			{
				nx = dfsButtumUpWithGap(new bwtNode(curMap), path.get(idxPath).second.left,
						path.get(idxPath).second.right, 0, tIdx, root, edit, curLeftToRight,
						(curLeftToRight == 1) ? revIdx : fmIdx, path, idxPath, DArray, edit + 10, lower, false);
			}
		} else {
			edit = path.get(idxPath).second.editDistance;
			nx = dfsButtumUp(curMap, path.get(idxPath).second.left, path.get(idxPath).second.right, 0, tIdx,
					curMap.acceptedValue, edit, curLeftToRight, (curLeftToRight == 1) ? revIdx : fmIdx, path, idxPath,
					DArray, edit + 10,
					(curLeftToRight == 0) ? (curMap.acceptedValue + path.get(idxPath).second.editDistance / 2) : -1,
					false);
		}
		DArray = null;
		if (resPtr[tIdx] == prePtVal)
			preSearched[tIdx][curMask]++;
//		System.out.println("buttumUpMapping ENDS 5/3");
		if (nx)
			return true;
		return false;
	}

	public boolean topDownMapping(int leftIdx, int rightIdx, byte editDistance, int leftToRight, int tIdx,
			ArrayList<pair<intervalNode, intervalNode>> path, int curMask) {
		depth++;
		if (lp[tIdx] > loopTh)
			return false;
		int mid = (leftIdx + rightIdx) / 2;
		int minEditLeft = getMinEdit(leftIdx, mid, tIdx);
		int minEditRight = getMinEdit(mid + 1, rightIdx, tIdx);
		int minEditAll = getMinEdit(leftIdx, rightIdx, tIdx);
		if (editDistance < minEditAll)
			return false;
		if (editDistance > MAXD) {
			{
				intervalNode left = new intervalNode(leftIdx, mid, (byte) (editDistance / 2), 1, curMask * 2);
				intervalNode right = new intervalNode(mid + 1, rightIdx, editDistance, 0, 0);
				path.add(new pair<intervalNode, intervalNode>(left, right));
				if (editDistance / 2 >= minEditLeft) {
					// Duplicate ArrayList Path to Path2
					ArrayList<pair<intervalNode, intervalNode>> path2 = new ArrayList<pair<intervalNode, intervalNode>>(
							path.size());
					for (pair<intervalNode, intervalNode> p : path) {
						path2.add(new pair<intervalNode, intervalNode>(new intervalNode(p.first),
								new intervalNode(p.second)));
					}
					boolean leftRes = topDownMapping(left.left, left.right, left.editDistance, left.leftToRight, tIdx,
							path2, curMask * 2);
					if (leftRes)
						return true;
				}
			}
			path.remove(path.size() - 1);
			{
				intervalNode left = new intervalNode(leftIdx, mid, editDistance, 1, 0);
				intervalNode right = new intervalNode(mid + 1, rightIdx, (byte) (editDistance / 2), 0, curMask * 2 + 1);
				path.add(new pair<intervalNode, intervalNode>(right, left));
				if (editDistance / 2 >= minEditRight) {
					ArrayList<pair<intervalNode, intervalNode>> path2 = new ArrayList<pair<intervalNode, intervalNode>>(
							path.size());
					for (pair<intervalNode, intervalNode> p : path) {
						path2.add(new pair<intervalNode, intervalNode>(new intervalNode(p.first),
								new intervalNode(p.second)));
					}
					boolean rightRes = topDownMapping(right.left, right.right, right.editDistance, right.leftToRight,
							tIdx, path2, curMask * 2 + 1);
					if (rightRes)
						return true;
				}
			}
			return false;
		}
		indexesPtr[tIdx] = 0;
		// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit
		// typing in Java:
		// auto t1 = std.chrono.high_resolution_clock.now();
		int len = rightIdx - leftIdx + 1;
		String q = readsPerThread[tIdx].substring(leftIdx, rightIdx + 1);
		if (leftToRight == 1) {
			buildLevstein(q, false, tIdx, revIdx);
		} else {
			buildLevstein(q, true, tIdx, fmIdx);
		}
		
		for (int i = 0; i < indexesPtr[tIdx]; i++){
			for (int j = leftIdx; j <= rightIdx; j++){
				nonComplete[tIdx][rightIndex[tIdx]++] = readsPerThread[tIdx].charAt(j);
				//cout << readsPerThread[tIdx][j];
			}
//The following line was determined to be a copy constructor call - this should be verified and a copy constructor should be created if it does not yet exist:
//ORIGINAL LINE: boolean nx = buttumUpMapping(indexes[tIdx][i], path, path.size() - 1, leftToRight, tIdx);
			boolean nx = buttumUpMapping(new bwtNode(indexes[tIdx][i]), path, path.size() - 1, leftToRight, tIdx,3);
			if (nx)
			{
				return true;
			}
			leftIndex[tIdx] = rightIndex[tIdx] = 2 * DefineConstants.MAXINPLEN;
		}
		return false;
	}

	public pair< String, Integer > getCigar(long refPos, String read, int diff, int tIdx){
		if (gap[tIdx] == 0){
			String sout = read.length() + "M";
			return new pair< String, Integer >(sout, 0);
		}
		String refSub = "";
		//C++ TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
		//ORIGINAL LINE: for( long long i = max( 0LL, refPos - diff ); i < min( (unsigned long long)Ref.sz, refPos + read.length() + diff ); i++ )
		for (long i = Math.max(0L, refPos - diff); i < Math.min((long)Ref.sz, refPos + read.length() + diff); i++){
			//need LONG
			refSub += Ref.charAt((int) i);
		}
		int len = read.length();
		String ret = "";
		RefObject<String> REFSUB = new RefObject<String>(refSub);
		RefObject<String> READ = new RefObject<String>(read);
		int curRow = getBestStartingPos(REFSUB, READ, diff);
		refSub = REFSUB.argvalue;
		read = READ.argvalue;
		pair< String, String > hs = Hirschberg(refSub, read, diff);
		for (int i = 0; i < hs.first.length(); i++){
			char c1 = hs.first.charAt(i);
			char c2 = hs.second.charAt(i);
			if (c1 == '-')
				ret += 'D';
			else if (c2 == '-')
				ret += 'I';
			else
				ret += 'M';
		}
		String fret = "";
		for (int i = 0; i < ret.length(); i++){
			int j = i;
			while (j < ret.length() && ret.charAt(j) == ret.charAt(i))
				j++;
			fret += (j - i);
			fret += ret.charAt(i);
			i = j - 1;
		}
		return new pair< String, Integer >(fret, curRow);
	}
	
public String samFormatString(String qName, String read, int refPos, int len, int acceptedValue, int flag, boolean Cigar, String QC)
{
	return samFormatString(qName, read, refPos, len, acceptedValue, flag, Cigar, QC, new pair< String, Integer >("", 0));
}

//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: String samFormatString(String& qName, String& read, uint refPos, int len, int acceptedValue, int flag, boolean Cigar, String& QC, pair< String, uint > cigar = pair< String, uint >("", 0))
	public String samFormatString(String qName, String read, int refPos, int len, int acceptedValue, int flag, boolean Cigar, String QC, pair< String, Integer > cigar)
	{
		String ret = "";
		ret += ((qName.length() > 0) ? qName : "*") + "\t"; // QNAME field
		ret += flag + "\t"; // FLAG field
		if (flag == 4)
		{
			ret += ("*\t0\t255\t*\t*\t0\t0\t" + read + "\t*");
			return ret;
		}
		int dif = (acceptedValue == -1) ? (((globalLongReadNoice * read.length()) / 100)) : (acceptedValue);
		String tp;
		pair< String, Integer > ps = getPosition(refPos - dif + cigar.second);
		ret += ps.first + "\t"; // RNAME field
//		ret += ps.second + "\t"; // POS field
		ret += refPos + "\t"; // POS field
		ret += "255\t"; // MAPQ field
		ret += cigar.first + '\t'; // CIGAR field
		ret += "*\t"; // RNEXT field
		ret += "0\t"; // PNEXT field
		ret += "0\t"; // TLEN field
		ret += read + '\t'; // SEQ field
		if (QC.equals(""))
				ret += "*"; // QUAL field
		else
			ret += QC;
		return ret;
	}

	public void buildMaskArray(int idx, int allowedEdit, String curRead) {
		java.util.ArrayList<pair<Integer, Integer>>[] qu = new ArrayList[2];
		java.util.ArrayList<Integer>[] mm = new ArrayList[2];
		qu[0]=new ArrayList<pair<Integer, Integer>>(); qu[1]=new ArrayList<pair<Integer, Integer>>();
		mm[0]=new ArrayList<Integer>(); mm[1]=new ArrayList<Integer>();
		
		qu[0].add(new pair<Integer, Integer>(0, curRead.length() - 1));
		mm[0].add(1);
		int turn = 0;
		int mxEr = 2 * allowedEdit;
		while (mxEr != 0) {
			for (int ci = 0; ci < qu[turn].size(); ci++) {
				int cL = qu[turn].get(ci).first;
				int cR = qu[turn].get(ci).second;
				int cM = mm[turn].get(ci);
				int mid = (cL + cR) / 2;
				qu[1 - turn].add(new pair<Integer, Integer>(cL, mid));
				mm[1 - turn].add(2 * cM);
				qu[1 - turn].add(new pair<Integer, Integer>(mid + 1, cR));
				mm[1 - turn].add(2 * cM + 1);
				if (mxEr == 2 * allowedEdit)
					continue;
				cL = (ci % 2 == 0) ? qu[turn].get(ci + 1).first : qu[turn].get(ci - 1).first;
				cR = (ci % 2 == 0) ? qu[turn].get(ci + 1).second : qu[turn].get(ci - 1).second;
				int len = curRead.length();
				if (ci % 2 == 0) {
					for (int ii = cL - mxEr; ii <= cR + mxEr; ii++) {
						for (int j = 0; j < 6; j++) {
							windowMask[idx][j][cM][ii + mxEr] = 0;
							for (int k = ii - mxEr; k <= ii + mxEr; k++) {
								windowMask[idx][j][cM][ii + mxEr] <<= 1;
								if (k >= cL && k <= cR && fmIdx.remap[curRead.charAt(k)] == j)
									windowMask[idx][j][cM][ii + mxEr]++;
							}
						}
					}
				} else {
					for (int ii = cL - mxEr; ii <= cR + mxEr; ii++) {
						for (int j = 0; j < 6; j++) {
							windowMask[idx][j][cM][ii + mxEr] = 0;
							for (int k = ii + mxEr; k >= ii - mxEr; k--) {
								windowMask[idx][j][cM][ii + mxEr] <<= 1;
								if (k >= cL && k <= cR && fmIdx.remap[curRead.charAt(k)] == j)
									windowMask[idx][j][cM][ii + mxEr]++;
							}
						}
					}
				}
			}
			qu[turn].clear();
			mm[turn].clear();
			turn = 1 - turn;
			mxEr /= 2;
		}
		qu[turn].clear();
		mm[turn].clear();
	}

	public void initDbound(int idx)
	{
		//std::fill( dbound[idx], dbound[idx] + MAXMASK * MAXMASK, 100000 );
		for (int i = 0; i < DefineConstants.MAXMASK; i++)
			for (int j = 0; j < DefineConstants.MAXMASK; j++)
				dbound[idx][i][j] = 1000000; //.clear();
		for (int i = 0; i < DefineConstants.MAXMASK; i++)
			failedTry[idx][i]= new TreeMap< pair< Integer, Integer >, Integer >();
		leftIndex[idx] = 2 * DefineConstants.MAXINPLEN;
		rightIndex[idx] = 2 * DefineConstants.MAXINPLEN;
	}

	public ArrayList<mappingInfo> getAllMapping(int idx, String read, String _qual, String readName)
	{
		ArrayList< mappingInfo > ret = new ArrayList< mappingInfo >();
		cntDFS = 0;
		hit = 0;
		unhit = 0;
		forceStop[idx] = false;
		String qual = _qual;
		//need check ---- critical bug
		char readChar [] = read.toCharArray();
		for (int j = 0; j < read.length(); j++){
			boolean v = false;
			for (int k = 0; k < 4; k++)
				if (readChar[j] == dna[k])
					v = true;
			if (v == false)
				readChar[j] = 'a';
		}
		read = new String(readChar);
		String curRead = read;
		String curReadRev = reverseComplement(read);
		String curName = readName;
		//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		//		auto t1 = std.chrono.high_resolution_clock.now();
		readsPerThread[idx] = read;
		ArrayList<pair< intervalNode, intervalNode >> path = new ArrayList<pair< intervalNode, intervalNode >>();
		lp[idx] = 0;
		resPtr[idx] = 0;
		threadResults[idx]= new ArrayList<bwtNode>();
		/*if( longRead ){
		    noisePercent[idx] = 0.2;
		    uniqeOption[idx] = false;
		    maxReport[idx] = 1;
		}*/
		//need to be byte
		int allowedEdit = 0;
		for (int j = 0; j < read.length() * 4 + 1; j++)
			preSearched[idx][j] = 0;
		if (noisePercent[idx] >= 0)
		{
			noisePercent[idx] = Math.min(noisePercent[idx], 0.2);
			allowedEdit = (int)((double)read.length() * noisePercent[idx]);
		}
		else if (maxDiffEdit[idx] >= 0)
			allowedEdit = maxDiffEdit[idx];
		else
			allowedEdit = 1;

		buildMaskArray(idx, allowedEdit, readsPerThread[idx]);
		initDbound(idx);
		direction[idx] = 0;
		//The following line was determined to be a copy constructor call - this should be verified and a copy constructor should be created if it does not yet exist:
		//ORIGINAL LINE: topDownMapping(0, read.length() - 1, allowedEdit, 1, idx, path, 1);
		//topDownMapping(0, read.length() - 1, allowedEdit, 1, idx, new java.util.ArrayList(path), 1);
		topDownMapping(0, read.length() - 1, (byte)allowedEdit, 1, idx, path, 1);
		if (resPtr[idx] < maxReport[idx] && forceStop[idx] == false){
			debg = 0;
			direction[idx] = 1;
			for (int j = 0; j < read.length() * 4 + 1; j++)
				preSearched[idx][j] = 0;
			path.clear();
			readsPerThread[idx] = reverseComplement(readsPerThread[idx]);
			buildMaskArray(idx, allowedEdit, readsPerThread[idx]);
			initDbound(idx);
			//C++ TO JAVA CONVERTER WARNING: The following line was determined to be a copy constructor call - this should be verified and a copy constructor should be created if it does not yet exist:
			//ORIGINAL LINE: topDownMapping(0, read.length() - 1, allowedEdit, 1, idx, path, 1);
			topDownMapping(0, read.length() - 1, (byte) allowedEdit, 1, idx, path, 1);
			readsPerThread[idx] = reverseComplement(readsPerThread[idx]);
		}
		//time
		if (forceStop[idx])
			resPtr[idx] = 0;

		if (resPtr[idx] == 0)
		{
			ret.add(new mappingInfo(curName, curRead, 0, 0, 0, 4, qual));
		}
		// function call need check
		else if (bestOne[idx])
		{
			Collections.sort(threadResults[idx]);
			ret.add(new mappingInfo(curName, (threadResults[idx].get(0).flag == 0) ? curRead : curReadRev, fmIdx.locateRow(threadResults[idx].get(0).first), threadResults[idx].get(0).len, threadResults[idx].get(0).acceptedValue, threadResults[idx].get(0).flag, qual));
		}
		else
		{
			for (int i = 0; i < resPtr[idx]; i++)
				for (int j = threadResults[idx].get(i).first; j <= Math.min(threadResults[idx].get(i).first + maxReport[idx] - 1, threadResults[idx].get(i).second); j++)
					ret.add(new mappingInfo(curName, (threadResults[idx].get(i).flag == 0) ? curRead : curReadRev, fmIdx.locateRow(j), threadResults[idx].get(i).len, threadResults[idx].get(i).acceptedValue, threadResults[idx].get(i).flag, qual));
		}
		return ret;
	}	
	
	public ArrayList<mappingInfo> threadAlign(String readName, String read, String qc, boolean reportAll) throws InterruptedException{
		mutex.acquire();
		mutexTokens.acquire();
		int idx = tokens.poll();
		mutexTokens.release();
//		String mos = read;
//		String rev = reverseComplement(read);
		if (read.length() <= DefineConstants.MAXSHORTREADLEN){
			maxReport[idx] = globalMaxReport;
			noisePercent[idx] = globalNoisePercent;
			maxDiffEdit[idx] = globalMaxDiffEdit;
			uniqeOption[idx] = globalUniqeOption;
			maxDiffMismatch[idx] = globalMaxDiffMismatch;
			bestOne[idx] = globalBestOne;
			if(reportAll){
				maxReport[idx] = 11;
				uniqeOption[idx] = 0;
				noisePercent[idx] = globalNoisePercent/2;
			}
			if (globalMode.equals("fast"))
				mxFailed[idx] = 1;
			else if (globalMode.equals("normal"))
				mxFailed[idx] = 7;
			else if (globalMode.equals("sensitive"))
				mxFailed[idx] = 30;
			else
				mxFailed[idx] = 10000;
			gap[idx] = globalGap;
			ArrayList<mappingInfo> mps = getAllMapping(idx, read, qc, readName);
			mutexTokens.acquire();
			tokens.add(idx);
			mutexTokens.release();
			mutex.release();
			return mps;
//			 if (resPtr[idx] != 0)
//			 {
//				mch[idx]++;
//			 }
//			for (int j = 0; j < mps.size(); j++)
//			{
//				if (mps.get(j).flag != 4)
//				{
////					mps.get(j).cigar = GlobalMembersFmcount.getCigar(mps.get(j).refPos, (mps.get(j).flag == 0) ? mos : rev, mps.get(j).acceptedValue, idx);
//				}
//				mps.get(j).order = ii;
//				results[idx].add(mps.get(j));
//			}
		}
		else{
			mutexTokens.acquire();
			tokens.add(idx);
			mutexTokens.release();
			mutex.release();
			System.out.println("LONG READ :|");
			return null;
		}
	}	
	public ArrayList<ALRecord> threadDistribute(final Iterator<ALRecord> list) throws InterruptedException, FileNotFoundException{
		final ArrayList<ALRecord> out = new ArrayList<ALRecord>();
		if(!list.hasNext()) return out;

		while(list.hasNext()){
			ALRecord alr = list.next();
			out.add(alr);
		}
		//need to be consern about Core number that is not divisable by 4
		final int microBatch = core;
		Collections.sort(out);
		final DoubleEndList<ALRecord> recList [] = new DoubleEndList[microBatch];
		for(int i = 0 ; i < microBatch ; i++){
			recList[i]=new DoubleEndList<ALRecord>(out.size()/microBatch+2);
		}
		{
			int i = 0;
			boolean A = false;
			for(ALRecord al : out){
				int I = i%microBatch;
				if(I==0){
					A = !A;
				}
				if(A){
					I=microBatch-I-1;
				}
				recList[I].add(al);
				i++;
			}
		}

    	ExecutorService executor = Executors.newFixedThreadPool(core);

    	Callable<Boolean> call[] = new Callable[core];
    	final Semaphore mutex = new Semaphore(core);
    	final ReentrantLock changeList = new ReentrantLock(true);
    	for(int i = 0 ; i < core ; i++){
    		final int I = i;
    		final int X = I%microBatch;
    		call[i]= () -> {
    			while(recList[X].hasNext()){	
    				ALRecord alr = recList[X].getLeft();
    				if(alr==null)
    					break;
    				try{
    					mapFunction12(alr, I);
    				}
    				catch (Exception e) {
//    					System.out.println(alr.getID());
//						e.printStackTrace(System.out);
						
					}
    			}
    			recList[X].setFull();
    			int min = Integer.MAX_VALUE/2;
    			int imin = -1;
    			changeList.lock();
    			for(int j =0 ; j < core ; j++){
    				if(!recList[j].isFull()){
    					if(recList[j].left<min){
    						min = recList[j].left;
    						imin = j;
    					}
    				}
    			}
    			if(imin!=-1){
    				recList[imin].setFull();
    			}
    			changeList.unlock();
    			if(imin!=-1)
    			while(recList[imin].hasNext()){	
    				ALRecord alr = recList[imin].getRight();
    				if(alr==null)
    					break;
    				try{
    					mapFunction12(alr, I);
    				}
    				catch (Exception e) {
//    					System.out.println(alr.getID());
//						e.printStackTrace(System.out);
//						
					}
    			}
				mutex.release();
				return true;
    		};
    	}
    	
    	listSemaphore.acquire();
    	for(int i = 0 ; i < core ; i++){
    		mutex.acquire();
    		executor.submit(call[i]);
    		Thread.sleep(75);
    	}
    	mutex.acquire(core);
    	executor.shutdown();
    	listSemaphore.release();
    	return out;
	}
	
	
	
	public ALRecord mapFunction12(ALRecord alr, int coreNum) {
        List<Integer> mapedFrag = new LinkedList<Integer>();
        int iterations = alr.getReadSize() / Args.getFragmentSize();
//        List<Integer> order = new LinkedList<Integer>();
        Stack<Integer> order = new Stack<Integer>();
        int falseCount = 0;
        for (int i = 2; i < iterations; i++) {
            order.add(i);
        }
        Collections.shuffle(order);
        order.push(1);
        order.push(0);
        for (int i : order) {
            try {
            	String Seq = alr.FragRead[i];
                ArrayList<mappingInfo> results = threadAlignI("@A", Seq, alr.FragScore[i],false,coreNum);
                mappingInfo mi = results.get(results.size()-1);
                if(mi.flag!=4){
                	alr.FragPos[i]=mi.refPos;
                	alr.FragIsReverse[i]=(mi.flag==16) ? true : false;
                }
                else{
                	alr.FragPos[i]=-1;
                }
			} catch (InterruptedException e) {
//				e.printStackTrace();
			}
            if (alr.getFragPos()[i] > 0) {
            	mapedFrag.add(i);
            	if(mapedFrag.size()<2) continue;
                boolean isMaped = alr.Anchor(i, mapedFrag);
//                if (isMaped) break;
                if (isMaped) {
                	
//                	break;
                    String A = alr.getRead();
                    int len = A.length()/100;
                    len = Math.max(len, 500);
                    String Complement = A;
                    if (alr.isReverse()) {
                        A = complement(A);
//                        alr.setRead(A);
                        Complement = A;
                    }
//                    String Asmal = A.substring(0,len);
                    int pos = alr.getPosition();//-(int)(A.length()*0.05);
                    ////NEED Revise TODAY
//                    alr.setPosition(pos);
                    String B = "";
//                    for(int I =0 ; I <= alr.getReadSize()*(1.1) ; I++){
//                    for(int I =0 ; I <= len*5 ; I++){
//                    	B+=Ref.charAt(I+pos-1);
//                    }                  
//                    LocalAligner la = new LocalAligner(Asmal, B, -100, -100, 3, (int) (0.1 * B.length()), 2 * B.length());
//                    int diff = la.run();
//                    pos = pos+diff;
//                    
                    B = "";
                    for(int I =0 ; I <= alr.getReadSize()*(1.1) ; I++){
                    	B+=Ref.charAt(I+pos-1);
                    }  
                    LocalAligner la = new LocalAligner(A, B, -8, -6, 3, (int) (0.1 * B.length()), 2 * B.length());
                    int diff = la.DQ(Args.getMinL());
                    pos = pos+diff;
                    //new need
//                    if(fast || A.length()>150)
//                    	alr.setPosition(la.DQ(Args.getMinL())+pos);
                    
                    alr.setPosition(pos);
//                    la.DQ(Args.getMinL());
//                    alr.setPosition(pos+la.posDist);
//                    System.out.print(alr.getPosition()+"\t");
//                    else
//                    	alr.setPosition(pos+la.Full());
//                    System.out.println(alr.getPosition()+"\t"+alr.ID+"\t"+la.totalMismatch+"\t"+la.totalGap);
//                    if(la.totalMismatch+ la.totalGap <0.3*alr.getReadSize() ){//&& la.mScore > 0){
                    if(true){
                    	alr.setRead(Complement);
	                    alr.setAS(la.mScore);
	                    alr.setCigar(la.realCigar);
//	                    //TODAY need to be deleted
//	                    alr.setCigar(la.posDist+"");
	                    alr.setFlag(alr.isReverse() ? "16" : "0");
	                    alr.setMD(la.cigar);
	                    alr.setNM(la.totalMismatch);
	                    pair<String, Integer> p = getPosition(alr.getPosition());
	                    alr.setReference(p.first);
	                    alr.setPosition(p.second);
//	                    alr.setReference("chr19");
	                    break;
                    }
//                    else{
////                    	System.out.println("ALIGN:\t"+alr.getReadSize()+"\t"+la.totalMismatch+"\t"+la.totalGap+"\t"+la.mScore);
//                    	alr.setMaped(false);
//                    	falseCount++;
//                    	if(falseCount>0)
//                    		break;
//                    }
//                    break;
                    
                }
            }
        }
        return alr;
    }
	
	public ArrayList<ASRecord> threadDistributeAssign(final Iterator list) throws InterruptedException{
		final ArrayList<ASRecord> out = new ArrayList<ASRecord>();
		if(!list.hasNext()) return out;
		while(list.hasNext()){
			Object o = list.next();
			ASRecord asr = null;
			if(o instanceof ALRecord)
				asr = new ASRecord((ALRecord) o,Args);
			else{
				asr = (ASRecord) o;
				asr = new ASRecord(asr.ID, asr.Read, asr.Score, Args.getFragmentSize(), Args.getCorrDist(), Args.getDepth(), Args.getMinL());
			}
			out.add(asr);
		}

		final int microBatch = core;
		Collections.sort(out);
		final DoubleEndList<ASRecord> recList [] = new DoubleEndList[microBatch];
		for(int i = 0 ; i < microBatch ; i++){
			recList[i]=new DoubleEndList<ASRecord>(out.size()/microBatch+2);
		}
		{
			int i = 0;
			boolean A = false;
			for(ASRecord al : out){
				int I = i%microBatch;
				if(I==0){
					A = !A;
				}
				if(A){
					I=microBatch-I-1;
				}
				recList[I].add(al);
				i++;
			}
		}

    	ExecutorService executor = Executors.newFixedThreadPool(core);

    	Callable<Boolean> call[] = new Callable[core];
    	final Semaphore mutex = new Semaphore(core);
    	final ReentrantLock changeList = new ReentrantLock(true);
    	for(int i = 0 ; i < core ; i++){
    		final int I = i;
    		final int X = I%microBatch;
    		call[i]= () -> {
    			while(recList[X].hasNext()){	
    				ASRecord alr = recList[X].getLeft();
    				if(alr==null)
    					break;
    				try{
    					mapFunction3(alr, I,true);
    				}
    				catch (Exception e) {
//    					System.out.println(alr.getID());
//						e.printStackTrace(System.out);
						
					}
    			}
    			recList[X].setFull();
    			int min = Integer.MAX_VALUE/2;
    			int imin = -1;
    			changeList.lock();
    			for(int j =0 ; j < core ; j++){
    				if(!recList[j].isFull()){
    					if(recList[j].left<min){
    						min = recList[j].left;
    						imin = j;
    					}
    				}
    			}
    			if(imin!=-1){
    				recList[imin].setFull();
    			}
    			changeList.unlock();
    			if(imin!=-1)
    			while(recList[imin].hasNext()){	
    				ASRecord alr = recList[imin].getRight();
    				if(alr==null)
    					break;
    				try{
    					mapFunction3(alr, I,true);
    				}
    				catch (Exception e) {
//    					System.out.println(alr.getID());
//						e.printStackTrace(System.out);
						
					}
    			}
				mutex.release();
				return true;
    		};
    	}
    	listSemaphore.acquire();
    	for(int i = 0 ; i < core ; i++){
    		mutex.acquire();
    		executor.submit(call[i]);
    		Thread.sleep(75);
    	}
    	mutex.acquire(core);
    	executor.shutdown();
    	listSemaphore.release();
    	return out;
	}
	
//	public ASRecord mapFunction3(ASRecord asr, int I) {
	public ASRecord mapFunction3(ASRecord asr, int I, boolean FirstIter) {
//		System.out.println("MAP3");
//    	ASRecord asr = new ASRecord(alr,args)
    	//need reforming
//		for (int F = 0; F < asr.FragPos.length; F++) {
//		System.out.println(asr.ID);
//		System.out.println("Start Assigning");
		for (int F = 1; F < asr.FragPos.length; F+=2) {
			ArrayList<mappingInfo> results = null;
			try {
//				System.out.println("Getting Result "+F+"\t"+asr.FragRead[F]);
				results = threadAlignI("@A", asr.FragRead[F], asr.FragScore[F],true,I);
//				System.out.println("Getting Result "+F+" Done ");
			} catch (InterruptedException e) {
//				e.printStackTrace();
			}
			if(results == null) continue;
			if(results.size() > 10) continue;
			if(results.get(0).flag==4) continue;
			asr.FragPos[F] = new int[results.size()];
			asr.FragIsReverse[F] = new boolean[results.size()];
			asr.FragAlignerScore[F] = new int[results.size()];
			int i = 0 ;
//			System.out.println("Checking Result");
			for(mappingInfo mi : results){
				if(mi.flag==4) continue;
				asr.FragPos[F][i] = mi.refPos;
				asr.FragIsReverse[F][i] = (mi.flag==16) ? true : false;
				asr.FragAlignerScore[F][i] = 150;
				i++;
			}
//			System.out.println("Checking Result Done");
		}
//		System.out.println("ALIGNING FRAGMENTS DONE");
		// ------- compute Scores
		for (int i = 0; i < asr.FragPos.length; i++) {
			if (asr.FragPos[i] != null) {
				double exp = Math.pow(Math.E, -0.5 * asr.FragPos[i].length);
				exp*=10;
				for (int j = 0; j < asr.FragPos[i].length; j++) {
					asr.FragAlignerScore[i][j] *= exp;
				}
//				for (int j = 0; j < FragAlignerScore[i].length; j++) {
//					FragAlignerScore[i][j] *= exp;
//				}
			}
		}
//		System.out.println("COMPUTE SCORES DONE");
		// -----
		Path paths [] = asr.PathFinderRelax();
//		System.out.println("Searching For Pathes Done");
		if(paths==null) return asr;
		int validPaths = asr.Filter(paths);
//		System.out.println("FILTERING DONE");
		// ----- compute real position
		asr.Position = new int[validPaths];
		asr.Reverse = new boolean[validPaths];
		asr.AS = new int[validPaths];
		asr.Cigar = new String[validPaths];
		//need re-comment
		asr.Flag = new String[validPaths];
		asr.MD = new String[validPaths];
		asr.NM = new int[validPaths];
		asr.Reference = new String[validPaths];
		asr.validPath = new boolean[validPaths];
		for(int i =0 ; i < validPaths ; i++){
			int tempPos = paths[i].pos - asr.Overlap*(paths[i].fragNum)*(paths[i].isReverse?-1:1)-(asr.ReadSize-asr.FragSize)*(paths[i].isReverse?1:0);
			///NEED Revise Recomment
//			asr.Position[i]=tempPos+1;
			asr.Position[i]=tempPos-10;
			asr.Reverse[i]=paths[i].isReverse;
//			localAlignment(Ref, i);
			{
				String A = asr.Read;
				String Complement = A;
		        if (asr.Reverse[i]) {
		            A = complement(asr.Read);
		            Complement = A;
		        }
		        String B = "";
//		        System.out.println("Getting Refrence Started");
		        for(int j =0 ; j <= asr.ReadSize*1.2 ; j++){
		        	try{
		        	B+=Ref.charAt(j+asr.Position[i]-1);
		        	}catch (Exception e) {
		        		e.printStackTrace(System.out);
		        		System.out.println("ERR: "+(j+asr.Position[i]-1)+"\t"+Ref.sz);
		        		break;
					}
		        }
//		        System.out.println("Getting Refrence Done and Local ALignment Started");
		        LocalAligner la = new LocalAligner(A, B, -8, -6, 2, (int) (0.1 * B.length()), 2 * B.length());
//		        System.out.println("Local Alignment Done");
		        //need revision
//		        if(fast)
		        	asr.Position[i]+=la.DQ(Args.getMinL());
//		        else{
//		        	asr.Position[i]+=la.Full();
//		        }
//		        int scoreTh = asr.ReadSize;
//		        int misPen = (int)(0.1*(double)asr.ReadSize);
//		        if(la.totalMismatch<misPen && la.totalGap <misPen && la.mScore>scoreTh){
//		        if(la.totalMismatch<0.2*asr.getReadSize() && la.totalGap <0.32*asr.getReadSize() && la.mScore > 0){
//		        if(la.totalMismatch<AcceptableErr*asr.getReadSize() && la.totalGap <AcceptableErr*asr.getReadSize() ){
//		        if(la.totalMismatch + la.totalGap <0.5*asr.getReadSize() ){
		        if(true) {
		        	asr.setRead(Complement);
			        asr.AS[i]=la.mScore;
			        asr.Cigar[i]=la.realCigar;
			      //need re-comment
			        asr.Flag[i]=asr.Reverse[i] ? "16" : "0";
			        asr.MD[i]=la.cigar;
			        asr.NM[i]=la.totalMismatch;
//			        asr.Reference[i]="chr19";
			        pair<String, Integer> p = getPosition(asr.Position[i]);
                    asr.Reference[i]= p.first;
                    asr.Position[i] = p.second;
			        asr.validPath[i]=true;
			        asr.Maped=true;
		        }
//		        else{
////		        	System.out.println("ASSIGN:\t"+asr.ReadSize+"\t"+la.totalMismatch+"\t"+la.totalGap+"\t"+la.mScore);
//		        }
			}
		}
//		System.out.println("ASSIGNMENT DONE");
		//need re-comment
//		if(!asr.Maped && FirstIter){
//			asr.reform();
//			mapFunction3(asr, I , false);
//		}
    	return asr;
    }
	
	
	
	
	public static String complement(String In) {
        String out = "";
        char c[] = In.toCharArray();
        for (int i = c.length - 1; i > -1; i--) {
            switch (c[i]) {
                case 'a':
                    out += 't';
                    break;
                case 't':
                    out += 'a';
                    break;
                case 'c':
                    out += 'g';
                    break;
                case 'g':
                    out += 'c';
                    break;
                default:
                    out += 'n';
                    break;
            }
        }
        return out;
    }
	public ArrayList<mappingInfo> threadAlignI(String readName, String read, String qc, boolean reportAll,int idx) throws InterruptedException{
		
		if (read.length() <= DefineConstants.MAXSHORTREADLEN){
			maxReport[idx] = globalMaxReport;
			noisePercent[idx] = globalNoisePercent;
			maxDiffEdit[idx] = globalMaxDiffEdit;
			uniqeOption[idx] = globalUniqeOption;
			maxDiffMismatch[idx] = globalMaxDiffMismatch;
			bestOne[idx] = globalBestOne;
			if(reportAll){
				maxReport[idx] = 11;
				uniqeOption[idx] = 0;
			}
			if (globalMode.equals("fast"))
				mxFailed[idx] = 1;
			else if (globalMode.equals("normal"))
				mxFailed[idx] = 7;
			else if (globalMode.equals("sensitive"))
				mxFailed[idx] = 30;
			else
				mxFailed[idx] = 10000;
			gap[idx] = globalGap;
			ArrayList<mappingInfo> mps = getAllMapping(idx, read, qc, readName);
			return mps;
		}
		else{
			System.out.println("LONG READ :|");
			return null;
		}
	}
	
	public void loadRef(String pref) throws IOException {
		String filename = pref + ".rinfo";
		File inputfile2 = new File(filename);
		MyBufferedScanner fin2 = new MyBufferedScanner(new FileInputStream(inputfile2));
		Ref.load(fin2);
//		fin2.close();
		
		FastScanner fin = new FastScanner(new File(pref + ".cinfo"));
		refLen = Ref.sz;
		System.out.println("ref length = " + Ref.sz);

		while (fin.hasMoreTokens()) {
			String tmp = fin.next();
			// -ORIGINAL LINE: unsigned int pos;
			int pos = fin.nextInt();
			refNames.add(tmp);
			refOffSets.add(pos);
		}
		// - ORIGINAL LINE: refOffSets.push_back(4000000000ULL);
		refOffSets.add(2000000000);
	}

	public static boolean has_suffix(String str, String suffix)
	{
		boolean ret = str.length() >= suffix.length();
		if(!ret) return ret;
		int start = str.length() - suffix.length();
		for(int i = 0 ; i < suffix.length() ; i++){
			if(str.charAt(start+i)!=suffix.charAt(i)){
				return false;
			}
		}
		return ret;
//		return str.length() >= suffix.length() && str.compare(str.length() - suffix.length(), suffix.length(), suffix) == 0;
	}

	public static boolean has_prefix(String str, String suffix)
	{
		boolean ret = str.length() >= suffix.length();
		if(!ret) return ret;
		int start = 0;
		for(int i = 0 ; i < suffix.length() ; i++){
			if(str.charAt(start+i)!=suffix.charAt(i)){
				return false;
			}
		}
		return ret;
//		return str.length() >= suffix.length() && suffix.equals(str.substring(0, suffix.length()));

	}

	public static void print_usage(String program)
	{
		System.out.println();
		System.out.printf("USAGE: %s [ OPTIONS ] <ref.fa> <query file>\n", program);
		System.out.println("  ref.fa: prefix path to the directory containing the reference genome and its index files");
		System.out.println("  query file : file containing queries in fastq format");
		System.out.println("  OPTIONS: ");
		System.out.println("\t -a : report all locations [default : disabled]");
		System.out.println("\t -b : report bestOne  [default : disabled]");
		System.out.println("\t -g : map with  indels [default : disabled]");
		System.out.println("\t -u : report only unique alignments [default : disabled]");
		System.out.println("\t -t val[int]  : number of threads [default : 1]");
		System.out.println("\t -k val[int]  : maximum number of alignments to be reported for each read [default : 1]");
		System.out.println("\t -e val[double]  : estimated error rate of reads [default : 0.05]");
		System.out.println("\t -v val[int]  : maximum number of allowed mismatch (included gap if any) for read mapping");
		System.out.println("\t -o val[string]  : output sam file [defaul : report.sam]");
		System.out.println("\t -m choose sensitivity threshold {fast, normal, sensitive, very-sensitive}[default: normal]");
		System.out.printf("EXAMPLE: %s -t 2 -u -e 0.1 -o output.sam /path/to/index/directory/hg19.fa query.fastq\n",program);
		System.out.println();
		return;
	}
	
}