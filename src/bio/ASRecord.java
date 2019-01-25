/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import shortRead.SureMapObjDyn;
import shortRead.mappingInfo;
import shortRead.LocalAligner;
/**
 *
 * @author MOSJAVA
 */
public class ASRecord implements Serializable, Comparable<ASRecord> {

	public String ID;
	public String Read;
	public String Score;
	public boolean validPath[];
	public String Flag[];
	public String Reference[];
	public int Position[];
	public String Cigar[];
	public int AS[];
	public String MD[];
	public int NM[];
	public int ReadSize;
	public String FragRead[];
	public String FragScore[];
	public byte Depth;
	public int FragPos[][];
	public int FragAlignerScore[][];
	public boolean FragIsReverse[][];
	public int FragSize;
	public boolean Reverse[];
	public boolean Maped;
	public byte CorrDist;
	public int Overlap;
	public int minL;
	private float consecutiveThreshold;
	public ASRecord(String ID, String Read, String Score, int FragSize, byte CorrDist, byte Depth, int minL) {
		this.ID = ID;
		this.Read = Read;
		this.Score = Score;
		this.ReadSize = Read.length();
		this.FragSize = FragSize;
		this.CorrDist = CorrDist;
		this.Depth = Depth;
		this.consecutiveThreshold = 0.3f;
		this.minL = minL;
		Overlap = FragSize / 2;
		int fragNum = (ReadSize - FragSize) / (FragSize - Overlap) + 1;
		if (fragNum < 1)
			fragNum = 1;
		FragRead = new String[fragNum];
		FragScore = new String[fragNum];

		// ----------------Fragmentation
		char[] CRead = Read.toCharArray();
		char[] CScore = Read.toCharArray();
		int step = (FragSize - Overlap);
		for (int i = 0; i < fragNum; i++) {
			int k = i * step;
			FragRead[i] = "";
			FragScore[i] = "";
			for (int j = 0; j < FragSize; j++) {
				FragRead[i] += CRead[k + j];
				FragScore[i] += CScore[k + j];
			}
		}
		FragPos = new int[fragNum][];
		FragIsReverse = new boolean[fragNum][];
		FragAlignerScore = new int[fragNum][];
	}
	
	public ASRecord(ALRecord alr , Arguments args) {
		this.ID = alr.getID();
		this.Read = alr.getRead();
//		System.out.println("Read Ass"+this.Read);
		this.Score = alr.getScore();
		this.ReadSize = Read.length();
		this.FragSize = alr.getFragSize();
		this.CorrDist = alr.getCorrDist();
		this.Depth = args.getDepth();
		this.consecutiveThreshold = 0.3f;
		this.minL = args.getMinL();
		Overlap = FragSize / 2;
		int fragNum = (ReadSize - FragSize) / (FragSize - Overlap) + 1;
		if (fragNum < 1)
			fragNum = 1;
		FragRead = new String[fragNum];
		FragScore = new String[fragNum];

		// ----------------Fragmentation
		char[] CRead = Read.toCharArray();
		char[] CScore = Read.toCharArray();
		int step = (FragSize - Overlap);
		for (int i = 0; i < fragNum; i++) {
			int k = i * step;
			FragRead[i] = "";
			FragScore[i] = "";
			for (int j = 0; j < FragSize; j++) {
				FragRead[i] += CRead[k + j];
				FragScore[i] += CScore[k + j];
			}
		}
		FragPos = new int[fragNum][];
		FragIsReverse = new boolean[fragNum][];
		FragAlignerScore = new int[fragNum][];
	}
	public void clear(){
		
	}
	
	public void reform(){
		FragSize = 150;
		Overlap = FragSize / 2;
		int fragNum = (ReadSize - FragSize) / (FragSize - Overlap) + 1;
		if (fragNum < 1)
			fragNum = 1;
		FragRead = new String[fragNum];
		FragScore = new String[fragNum];

		// ----------------Fragmentation
		char[] CRead = Read.toCharArray();
		char[] CScore = Read.toCharArray();
		int step = (FragSize - Overlap);
		for (int i = 0; i < fragNum; i++) {
			int k = i * step;
			FragRead[i] = "";
			FragScore[i] = "";
			for (int j = 0; j < FragSize; j++) {
				FragRead[i] += CRead[k + j];
				FragScore[i] += CScore[k + j];
			}
		}
		FragPos = new int[fragNum][];
		FragIsReverse = new boolean[fragNum][];
		FragAlignerScore = new int[fragNum][];
	}
	
	@Override
	public String toString() {
		return ID + " " + Read + " " + Score;
	}

	/**
	 * @return the ID
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @param ID
	 *            the ID to set
	 */
	public void setID(String ID) {
		this.ID = ID;
	}

	/**
	 * @return the Read
	 */
	public String getRead() {
		return Read;
	}

	/**
	 * @param Read
	 *            the Read to set
	 */
	public void setRead(String Read) {
		this.Read = Read;
	}

	/**
	 * @return the Score
	 */
	public String getScore() {
		return Score;
	}

	/**
	 * @param Score
	 *            the Score to set
	 */
	public void setScore(String Score) {
		this.Score = Score;
	}

	/**
	 * @return the Flag
	 */
	public String[] getFlag() {
		return Flag;
	}

	/**
	 * @param Flag
	 *            the Flag to set
	 */
	public void setFlag(String[] Flag) {
		this.Flag = Flag;
	}

	/**
	 * @return the Reference
	 */
	public String[] getReference() {
		return Reference;
	}

	/**
	 * @param Reference
	 *            the Reference to set
	 */
	public void setReference(String[] Reference) {
		this.Reference = Reference;
	}

	/**
	 * @return the Position
	 */
	public int[] getPosition() {
		return Position;
	}

	/**
	 * @param Position
	 *            the Position to set
	 */
	public void setPosition(int[] Position) {
		this.Position = Position;
	}

	/**
	 * @return the Cigar
	 */
	public String[] getCigar() {
		return Cigar;
	}

	/**
	 * @param Cigar
	 *            the Cigar to set
	 */
	public void setCigar(String [] Cigar) {
		this.Cigar = Cigar;
	}

	/**
	 * @return the AS
	 */
	public int[] getAS() {
		return AS;
	}

	/**
	 * @param AS
	 *            the AS to set
	 */
	public void setAS(int [] AS) {
		this.AS = AS;
	}

	/**
	 * @return the MD
	 */
	public String[] getMD() {
		return MD;
	}

	/**
	 * @param MD
	 *            the MD to set
	 */
	public void setMD(String [] MD) {
		this.MD = MD;
	}

	/**
	 * @return the NM
	 */
	public int[] getNM() {
		return NM;
	}

	/**
	 * @param NM
	 *            the NM to set
	 */
	public void setNM(int [] NM) {
		this.NM = NM;
	}

	/**
	 * @return the ReadSize
	 */
	public int getReadSize() {
		return ReadSize;
	}

	/**
	 * @param ReadSize
	 *            the ReadSize to set
	 */
	public void setReadSize(int ReadSize) {
		this.ReadSize = ReadSize;
	}

	/**
	 * @return the FragRead
	 */
	public String[] getFragRead() {
		return FragRead;
	}

	/**
	 * @param FragRead
	 *            the FragRead to set
	 */
	public void setFragRead(String[] FragRead) {
		this.FragRead = FragRead;
	}

	/**
	 * @return the FragScore
	 */
	public String[] getFragScore() {
		return FragScore;
	}

	/**
	 * @param FragScore
	 *            the FragScore to set
	 */
	public void setFragScore(String[] FragScore) {
		this.FragScore = FragScore;
	}

	/**
	 * @return the Depth
	 */
	public byte getDepth() {
		return Depth;
	}

	/**
	 * @param Depth
	 *            the Depth to set
	 */
	public void setDepth(byte Depth) {
		this.Depth = Depth;
	}

	/**
	 * @return the FragPos
	 */
	public int[][] getFragPos() {
		return FragPos;
	}

	/**
	 * @param FragPos
	 *            the FragPos to set
	 */
	public void setFragPos(int[][] FragPos) {
		this.FragPos = FragPos;
	}
	
	public void Align(SureMapObjDyn smo, int F) {
		ArrayList<mappingInfo> results = null;
		try {
			results = smo.threadAlign("@A", FragRead[F], FragScore[F],true);
		} catch (InterruptedException e) {e.printStackTrace();}
		if(results == null) return;
		if(results.size() > 10) return;
		if(results.get(0).flag==4) return;
		FragPos[F] = new int[results.size()];
		FragIsReverse[F] = new boolean[results.size()];
		FragAlignerScore[F] = new int[results.size()];
		int i = 0 ;
		for(mappingInfo mi : results){
			if(mi.flag==4) continue;
			FragPos[F][i] = mi.refPos;
			FragIsReverse[F][i] = (mi.flag==16) ? true : false;
			FragAlignerScore[F][i] = 150;
			i++;
		}
	}
	
	
	
	
//	public void Align(char[] refArray, int F) {
//		char[] Seq = FragRead[F].toCharArray();
//		int FS = Seq.length;
//		int Thresh = (int) (FS * 0.1);
//		// char [] refArray = Ref.toCharArray();
//		int Pos[] = new int[Depth];
//		int Sco[] = new int[Depth];
//		boolean Dir[] = new boolean[Depth];
//		int iPos = 0;
//		for (int i = 0; i < refArray.length - FS; i++) {
//			int mismatch = 0;
//			boolean isMaped = true;
//			for (int j = 0; j < FS; j++) {
//				if (refArray[i + j] != Seq[j]) {
//					mismatch++;
//					if (mismatch > Thresh) {
//						isMaped = false;
//						break;
//					}
//				}
//			}
//			if (isMaped) {
//				Pos[iPos] = i;
//				Dir[iPos] = false;
//				Sco[iPos] = FS - mismatch * 2;
//				iPos++;
//				if (iPos >= Depth) {
//					Pos = null;
//					iPos = -1;
//					break;
//				}
//			}
//		}
//		if (iPos > 0) {
//			FragPos[F] = new int[iPos];
//			FragIsReverse[F] = new boolean[iPos];
//			FragAlignerScore[F] = new int[iPos];
//			for (int i = 0; i < FragPos[F].length; i++) {
//				FragPos[F][i] = Pos[i];
//				FragIsReverse[F][i] = Dir[i];
//				FragAlignerScore[F][i] = Sco[i];
//			}
//		}
//	}

	public boolean Assign(SureMapObjDyn Ref , boolean FirstIter) {
//		char[] refChar = Ref.toCharArray();
		//need reforming
//		for (int i = 0; i < FragPos.length; i++) {
		for (int i = 1; i < FragPos.length; i+=2) {
			Align(Ref, i);
		}
		// ------- compute Scores
		for (int i = 0; i < FragPos.length; i++) {
			if (FragPos[i] != null) {
				double exp = Math.pow(Math.E, -0.5 * FragPos[i].length);
				exp*=10;
				for (int j = 0; j < FragPos[i].length; j++) {
					FragAlignerScore[i][j] *= exp;
				}
//				for (int j = 0; j < FragAlignerScore[i].length; j++) {
//					FragAlignerScore[i][j] *= exp;
//				}
			}
		}
		// -----
		Path paths [] = PathFinderRelax();
		if(paths==null) return false;
		int validPaths = Filter(paths);
		// ----- compute real position
		Position = new int[validPaths];
		Reverse = new boolean[validPaths];
		AS = new int[validPaths];
		Cigar = new String[validPaths];
		//need re-comment
		Flag = new String[validPaths];
		MD = new String[validPaths];
		NM = new int[validPaths];
		Reference = new String[validPaths];
		validPath = new boolean[validPaths];
		for(int i =0 ; i < validPaths ; i++){
			int tempPos = paths[i].pos - Overlap*(paths[i].fragNum)*(paths[i].isReverse?-1:1)-(ReadSize-FragSize)*(paths[i].isReverse?1:0);
			Position[i]=tempPos+1;
			Reverse[i]=paths[i].isReverse;
			localAlignment(Ref, i);
		}
		//need re-comment
//		if(!Maped && FirstIter){
//			reform();
//			Assign(Ref, false);
//		}
		return true;
	}
	
	
	public void localAlignment(SureMapObjDyn Ref, int i){
    	String A = Read;
        if (Reverse[i]) {
            A = complement(Read);
            Read = A;
        }
        String B = "";
        for(int j =0 ; j <= ReadSize*1.1 ; j++){
        	B+=Ref.Ref.charAt(j+Position[i]-1);
        }
        LocalAligner la = new LocalAligner(A, B, -8, -6, 2, (int) (0.1 * B.length()), 2 * B.length());
        //need revision
        Position[i]+=la.DQ(minL);
        int scoreTh = ReadSize;
        int misPen = (int)(0.1*(double)ReadSize);
        if(la.totalMismatch<misPen && la.totalGap <misPen && la.mScore>scoreTh){
	        AS[i]=la.mScore;
	        Cigar[i]=la.realCigar;
	      //need re-comment
	        Flag[i]=Reverse[i] ? "16" : "0";
	        MD[i]=la.cigar;
	        NM[i]=la.totalMismatch;
	        Reference[i]="chr19";
	        validPath[i]=true;
	        Maped=true;
        }
//        Maped=true;
        //need DELETE
//        Reference[i]=System.nanoTime()+"";
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

	
	public int Filter(Path paths[]){
		paths[0].isTrue=true;
		int validPaths=1;
		int maxScore = paths[0].score;
		if(maxScore>0){
			int ThreshScore = maxScore/2;
			for(int i = 1 ; i < paths.length ; i++){
				if(paths[i].score<ThreshScore){
					break;
				}
				double P = ((double)(paths[i-1].score - paths[i].score)/((double)(paths[i].score)));
				if(P>consecutiveThreshold){
					break;
				}
				paths[i].isTrue=true;
				validPaths++;
			}
		}
		else{
			int ThreshScore = maxScore*2;
			for(int i = 1 ; i < paths.length ; i++){
				if(paths[i].score<ThreshScore){
					break;
				}
				//numerator and denominator are negative so P is always positive
				double P = ((double)(paths[i].score - paths[i-1].score)/((double)(paths[i].score)));
				if(P>consecutiveThreshold){
					break;
				}
				paths[i].isTrue=true;;
				validPaths++;
			}
		}
		return validPaths;
	}
	public Path[] PathFinderRelax() {
		int min = Depth + 2;
		int imin = -1;
		for (int i = 0; i < FragPos.length; i++) {
			if (FragPos[i] != null){
				if (FragPos[i].length < min) {
					min = FragPos[i].length;
					imin = i;
				}
			}
		}
		if(min==0) return null;
		if(imin==-1) return null;
		int ppos[] = new int[min];
		int pscore[] = new int[min];
		boolean pdir[] = new boolean[min];
//		ArrayList[] path = new ArrayList[min];
		for (int i = 0; i < min; i++) {
//			path[i] = new ArrayList<int[]>();
			ppos[i] = FragPos[imin][i];
			pdir[i] = FragIsReverse[imin][i];
		}
		int error = (int) (0.1 * Overlap);

		for (int i = 0; i < FragPos.length; i++) {
			if (FragPos[i] != null){
				for (int k = 0; k < ppos.length; k++) {
					for (int j = 0; j < FragPos[i].length; j++) {
						if (FragIsReverse[i][j] == pdir[k]) {
							int dist = Math.abs(i - imin);
							if (Math.abs(Math.abs(FragPos[i][j] - ppos[k]) - Overlap * dist) <= error * dist) {
								pscore[k] += FragAlignerScore[i][j];
//								path[k].add(new int[] { i, j });
								break;
							}
						}
					}
				}
			}
		}
		Path paths [] = new Path[min];
		for(int i = 0 ; i < min ; i++){
			paths[i]= new Path(imin, ppos[i], pscore[i], pdir[i]);
		}
		Arrays.sort(paths);
		return paths;
	}
	
	//
	// public boolean Anchor(int step, List<Integer> MapList) {
	// if (step == 0) {
	// //isAligned
	// if (FragPos[0]!= -1 && FragPos[1] != -1) {
	// //isSameDirection
	// if (FragIsReverse[0] == FragIsReverse[1]) {
	// if (Math.abs(FragPos[1] - FragPos[0] - FragSize * (FragIsReverse[0] ? -1
	// : 1)) <= CorrDist) {
	// Position = FragPos[0] - (ReadSize - FragSize) * (FragIsReverse[0] ? -1 :
	// 0);
	// Reverse = FragIsReverse[0];
	// Maped = true;
	// return true;
	// }
	// }
	// }
	// }
	// if (step > 0) {
	// int j = step;
	// for (int i : MapList) {
	// //isAligned
	// if (FragPos[i] != -1 && FragPos[j] != -1) {
	// //isSameDirection
	// if (FragIsReverse[i] == FragIsReverse[j]) {
	// int dist = Math.abs(i-j);
	// if ( Math.abs(Math.abs(FragPos[j] - FragPos[i]) - FragSize*dist) <=
	// CorrDist*dist ) {
	// //Need Test
	// Position = FragPos[i] -
	// FragSize*(i)*(FragIsReverse[i]?-1:1)-(ReadSize-FragSize)*(FragIsReverse[i]?1:0);
	// Reverse = FragIsReverse[i];
	// Maped = true;
	// return true;
	// }
	// }
	// }
	// }
	// }
	// return false;
	// }

	/**
	 * @return the Reverse
	 */
	public boolean[] isReverse() {
		return Reverse;
	}

	/**
	 * @param Reverse
	 *            the Reverse to set
	 */
	public void setReverse(boolean[] Reverse) {
		this.Reverse = Reverse;
	}

	/**
	 * @return the Maped
	 */
	public boolean isMaped() {
		return Maped;
	}

	/**
	 * @param Maped
	 *            the Maped to set
	 */
	public void setMaped(boolean Maped) {
		this.Maped = Maped;
	}

	/**
	 * @return the CorrDist
	 */
	public byte getCorrDist() {
		return CorrDist;
	}

	/**
	 * @param CorrDist
	 *            the CorrDist to set
	 */
	public void setCorrDist(byte CorrDist) {
		this.CorrDist = CorrDist;
	}

	/**
	 * @return the validPath
	 */
	public boolean[] getValidPath() {
		return validPath;
	}

	@Override
	public int compareTo(ASRecord arg0) {
		return arg0.ReadSize-ReadSize;
	}

}
