/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import shortRead.SureMapObjDyn;
import shortRead.mappingInfo;

/**
 *
 * @author MOSJAVA
 */
public class ALRecord implements Serializable, Comparable<ALRecord> {

    public String ID;
    public String Read;
    public String Score;
    private String Flag;
    private String Reference;
    private int Position;
    private String Cigar;
    private int AS;
    private String MD;
    private int NM;
    private int ReadSize;
    public String FragRead[];
    public String FragScore[];
    private byte Depth;
    public int FragPos[];
    public boolean FragIsReverse[];
    private int FragSize;
    private boolean Reverse;
    private boolean Maped;
    private byte CorrDist;

    public ALRecord(String ID, String Read, String Score, int FragSize, byte CorrDist) {
        this.ID = ID;
        this.Read = Read.toLowerCase();
//        System.out.println("Read "+this.Read);
        this.Score = Score;
        this.ReadSize = Read.length();
        this.FragSize = FragSize;
        this.CorrDist = CorrDist;
        int fragNum = ReadSize / FragSize;
        FragRead = new String[fragNum];
        FragScore = new String[fragNum];
        //----------------Fragmentation
        char[] CRead = Read.toCharArray();
        char[] CScore = Read.toCharArray();
        for (int i = 0; i < fragNum; i++) {
            int k = i * FragSize;
            FragRead[i] = "";
            FragScore[i] = "";
            for (int j = 0; j < FragSize; j++) {
                FragRead[i] += CRead[k + j];
                FragScore[i] += CScore[k + j];
            }
        }
        FragPos = new int[fragNum];
        FragIsReverse = new boolean[fragNum];
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
     * @param ID the ID to set
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
     * @param Read the Read to set
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
     * @param Score the Score to set
     */
    public void setScore(String Score) {
        this.Score = Score;
    }

    /**
     * @return the Flag
     */
    public String getFlag() {
        return Flag;
    }

    /**
     * @param Flag the Flag to set
     */
    public void setFlag(String Flag) {
        this.Flag = Flag;
    }

    /**
     * @return the Reference
     */
    public String getReference() {
        return Reference;
    }

    /**
     * @param Reference the Reference to set
     */
    public void setReference(String Reference) {
        this.Reference = Reference;
    }

    /**
     * @return the Position
     */
    public int getPosition() {
        return Position;
    }

    /**
     * @param Position the Position to set
     */
    public void setPosition(int Position) {
        this.Position = Position;
    }

    /**
     * @return the Cigar
     */
    public String getCigar() {
        return Cigar;
    }

    /**
     * @param Cigar the Cigar to set
     */
    public void setCigar(String Cigar) {
        this.Cigar = Cigar;
    }

    /**
     * @return the AS
     */
    public int getAS() {
        return AS;
    }

    /**
     * @param AS the AS to set
     */
    public void setAS(int AS) {
        this.AS = AS;
    }

    /**
     * @return the MD
     */
    public String getMD() {
        return MD;
    }

    /**
     * @param MD the MD to set
     */
    public void setMD(String MD) {
        this.MD = MD;
    }

    /**
     * @return the NM
     */
    public int getNM() {
        return NM;
    }

    /**
     * @param NM the NM to set
     */
    public void setNM(int NM) {
        this.NM = NM;
    }

    /**
     * @return the ReadSize
     */
    public int getReadSize() {
        return ReadSize;
    }

    /**
     * @param ReadSize the ReadSize to set
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
     * @param FragRead the FragRead to set
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
     * @param FragScore the FragScore to set
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
     * @param Depth the Depth to set
     */
    public void setDepth(byte Depth) {
        this.Depth = Depth;
    }

    /**
     * @return the FragPos
     */
    public int[]getFragPos() {
        return FragPos;
    }

    /**
     * @param FragPos the FragPos to set
     */
    public void setFragPos(int[]FragPos) {
        this.FragPos = FragPos;
    }

//    public void Align(SureMapObjDyn smo, int i) throws InterruptedException {
//        String Seq = FragRead[i];
//        ArrayList<mappingInfo> results = smo.threadAlign("@A", Seq, FragScore[i],false);
//        mappingInfo mi = results.get(results.size()-1);
//        if(mi.flag!=4){
//        	FragPos[i]=mi.refPos;
//        	FragIsReverse[i]=(mi.flag==16) ? true : false;
//        }
//        else{
//        	FragPos[i]=-1;
//        }
//    }

    public boolean Anchor(int step, List<Integer> MapList) {
//        if (step == 0) {
//            //isAligned
//            if (FragPos[0] != -1 && FragPos[1] != -1) {
//                //isSameDirection
//                if (FragIsReverse[0] == FragIsReverse[1]) {
//                    if (Math.abs(FragPos[1] - FragPos[0] - FragSize * (FragIsReverse[0] ? -1 : 1)) <= CorrDist) {
//                        Position = FragPos[0] - (ReadSize - FragSize) * (FragIsReverse[0] ? -1 : 0) + 1;
//                        Reverse = FragIsReverse[0];
//                        Maped = true;
//                        return true;
//                    }
//                }
//            }
//        }
//        if (step > 0) {
        {
            int j = step;
            for (int i : MapList) {
            	if(i==j) continue;
                //isAligned
                if (FragPos[i] != -1 && FragPos[j] != -1) {
                    //isSameDirection
                    if (FragIsReverse[i] == FragIsReverse[j]) {
                        int dist = Math.abs(i-j);
                        if ( Math.abs(Math.abs(FragPos[j] - FragPos[i]) - FragSize*dist) <= CorrDist*dist*4 ) {
//                        if ( Math.abs(Math.abs(FragPos[j] - FragPos[i]) - FragSize*dist) <= 3*dist ) {

                            //Need Test
                            Position = FragPos[i] - FragSize*(i)*(FragIsReverse[i]?-1:1)-(ReadSize-FragSize)*(FragIsReverse[i]?1:0) + 1;
                            Reverse = FragIsReverse[i];
                            Maped = true;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public void clear(){
    	int fragNum = ReadSize / FragSize;
        FragPos = new int[fragNum];
        FragIsReverse = new boolean[fragNum];
    }
    /**
     * @return the Reverse
     */
    public boolean isReverse() {
        return Reverse;
    }

    /**
     * @param Reverse the Reverse to set
     */
    public void setReverse(boolean Reverse) {
        this.Reverse = Reverse;
    }

    /**
     * @return the Maped
     */
    public boolean isMaped() {
        return Maped;
    }

    /**
     * @param Maped the Maped to set
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
     * @param CorrDist the CorrDist to set
     */
    public void setCorrDist(byte CorrDist) {
        this.CorrDist = CorrDist;
    }

	/**
	 * @return the fragSize
	 */
	public int getFragSize() {
		return FragSize;
	}

	/**
	 * @param fragSize the fragSize to set
	 */
	public void setFragSize(int fragSize) {
		FragSize = fragSize;
	}

	@Override
	public int compareTo(ALRecord arg0) {
		return arg0.ReadSize-ReadSize;
	}
}
