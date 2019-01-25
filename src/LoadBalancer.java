import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;


import shortRead.FastScanner;

public class LoadBalancer {
	static int pwCounter = 0;
	static int filesLines[] ;
	public static void main(String[] args) throws IOException {
		long preTime = System.currentTimeMillis();
		int nodenum = 0;
		String fileName = "";
		String aligner = "";
		try {
			nodenum = Integer.parseInt(args[2]);
			fileName = args[1];
			aligner = args[0];
		}
		catch (Exception e) {
			System.out.println("usage: java -cp IMOS.jar LoadBalancer [aligner] <filename> <node> <isIllumina>");
			System.out.println("\taligner:\t [mini,meta]");
			System.out.println("\tfilename[String]: path to the input FastQ file.");
			System.out.println("\tnode [int]:\t indicates number of nodes in the cluster");
			System.out.println("\tisIllumina:\t yes, if it is illumina, No or leaving it blank for pacbio");
			return;
		}
		if(aligner.equalsIgnoreCase("mini")) {
			miniBalance(fileName, nodenum);
		}
		else if(aligner.equals("meta")) {
			boolean isIllumina = false;
			if(args.length==4 && args[3].equalsIgnoreCase("yes")) {
				isIllumina = true;
			}
			metaBalanceV2(fileName, nodenum,isIllumina);
		}
		else {
			System.out.println("Please select the aligner correctly.");
			System.out.println("usage: java -cp IMOS.jar LoadBalancer [aligner] <filename> <node> <isIllumina>");
			System.out.println("\taligner:\t [mini,meta]");
			System.out.println("\tfilename[String]: path to the input FastQ file.");
			System.out.println("\tnode [int]:\t indicates number of nodes in the cluster");
			System.out.println("\tisIllumina:\t yes, if it is illumina, No or leaving it blank for pacbio");
			return;
		}
		long curTime = System.currentTimeMillis();
		System.out.println((curTime-preTime)/1000+"s");
	}
	

	public static void miniBalance(String fileName, int nodenum) throws IOException {
		File inFile = new File(fileName);
		FastScanner in = new FastScanner(inFile);
		boolean isRecordOk = true;
		int lastIndex = fileName.lastIndexOf(".");
		if(lastIndex==-1) {
			lastIndex = fileName.length();
		}
		String newFileName = fileName.substring(0, lastIndex)+"-mini.fastm";
		BufferedWriter bw = new BufferedWriter(new FileWriter(newFileName));
		
		while (in.hasMoreTokens()) {
			String ID = in.nextLine();
			String Seq = in.nextLine();
			String p = in.nextLine();
			String Q = in.nextLine();
			if(ID.charAt(0)!='@') {
				isRecordOk = false;
			}
			if(p.charAt(0)!='+'){
				isRecordOk = false;
			}
			if(Seq.length()!=Q.length()) {
				isRecordOk = false;
			}
			if(isRecordOk) {
				int end = ID.indexOf(" ");
				if(end<0) end = ID.length();
				bw.write(ID.substring(0,end)+" "+Seq+" "+Q+"\n");
			}
		}
		bw.flush();
		bw.close();
		System.out.println("For uploading the file to HDFS use this command to get the best performance");
		System.out.println("replace <hadoop-distanation> with the willing path in hadoop");
		System.out.println("hadoop fs -Ddfs.replication="+nodenum+" -put "+newFileName+" <hadoop-distanation>");
	}


	public static void metaBalanceV2(String fileName,int NodeNum, boolean isIllumina) throws IOException {
		long inTime = 0;
		long prcTime = 0;
		long outTime = 0;
		long preTime = System.currentTimeMillis();
		File inFile = new File(fileName);
		String purefileName = fileName.substring(0, fileName.lastIndexOf('.'));
		//computing number of blocks
		long minBlockSize = 20971520;
//		long minBlockSize = 10360;
		if(isIllumina) {
			minBlockSize*=2;
		}
		long fileSize = inFile.length();
		if(fileSize < minBlockSize*2) {
			System.out.println("File Size is too small that do not need any balancing");
			return;
		}
		long numBlocks = fileSize/minBlockSize;
		for(int i = 0 ; i < NodeNum ; i++) {
			if(numBlocks%NodeNum==0) {
				break;
			}
			numBlocks--;
		}
		long blockSize = fileSize/numBlocks/512;
		blockSize = (blockSize+1)*512;
		//Num Block
		int NB = (int) numBlocks;
		
		FastScanner in = new FastScanner(inFile);

		int outbufLen = 1000;
		
		LBRead readBuf[][] = new LBRead[NB][outbufLen];
		LBRead roundr[] = new LBRead[NB]; // Round Robin
		String fileNames[] = new String[NB];
		filesLines = new int[NB];
		File[] files = new File[NB];
		BufferedWriter bws[] = new BufferedWriter[NB];
		long totalLen[] = new long[NB];
		int inbufLen = NB * outbufLen * 3;
		String[] inBuf = new String[inbufLen];
		
		int pwCounter = 0;
		for (int i = 0; i < NB; i++) {
			fileNames[i] = purefileName +"-m-"+ (pwCounter++) + ".fastm";
			files[i] = new File(fileNames[i]);
			bws[i] = new BufferedWriter(new FileWriter(files[i]));
		}
		int ipw = 0;
		int ir = 0;
		int irbuf = 0;
		int isbuf = 0;
		while (in.hasMoreTokens()) {
			long t0 = System.currentTimeMillis();
			while (in.hasMoreTokens() && isbuf < inbufLen) {
				inBuf[isbuf++] = in.nextLine();
				inBuf[isbuf++] = in.nextLine();
				in.nextLine();
				inBuf[isbuf++] = in.nextLine();
			}
			long t1 = System.currentTimeMillis();
			inTime+=t1-t0;
			for(int i = 0 ; i < isbuf-2 ; i+=3) {
				roundr[ir % NB] = new LBRead(inBuf[i], inBuf[i+1], inBuf[i+2]);
				ir++;
				if (ir % NB == 0) {
					Arrays.sort(roundr);
					for (int j = 0; j < NB; j++, ipw++) {
						readBuf[ipw % NB][irbuf] = roundr[j];
						totalLen[ipw % NB] += roundr[j].Seq.length();
						roundr[j]=null;
					}
					ipw++;
					ipw %= NB;
					irbuf++;
				}
			}
			for(int i = 0 ; i < roundr.length ; i++) {
				if(roundr[i]!=null) {
					readBuf[ipw % NB][irbuf] = roundr[i];
					totalLen[ipw % NB] += roundr[i].Seq.length();
					ipw++;
					ipw %= NB;
				}
			}
			long t2 = System.currentTimeMillis();
			prcTime += t2-t1;
			metaWrite(readBuf, bws, irbuf,ir,ipw);
			
			irbuf = 0;
			isbuf = 0;
			long t3 = System.currentTimeMillis();
			outTime += t3-t2;
		}
		

		long t2 = System.currentTimeMillis();
		for (int i = 0; i < NB; i++) {
			bws[i].flush();
			bws[i].close();
		}
		long t3 = System.currentTimeMillis();
		outTime += t3-t2;
		
		long catTime = System.currentTimeMillis();
		
		//cat command
		String OS = System.getProperty("os.name").toLowerCase();
		File combinedFile = new File(purefileName+"-meta.fastm");
		if(OS.contains("windows")) {
			BufferedWriter cmb = new BufferedWriter(new FileWriter(combinedFile));
			for(int i = 0 ; i < NB ; i++) {
				FastScanner sfs = new FastScanner(files[i]);
				String [] lines = new String[filesLines[i]+1];
				int j = 0;
				while(sfs.hasMoreTokens()) {
					lines[j++]= sfs.nextLine()+"\n";
				}
				for(int k = 0 ; k < j ; k++) {
					cmb.write(lines[k]);
				}
				sfs = null;
			}
			cmb.close();
			System.gc();
		}
		else {
			String [] catCommand = new String[NB+1];
			catCommand[0] = "cat";			
			for(int i = 0 ; i < NB ; i++) {
				catCommand[i+1]=fileNames[i];
			}
			ProcessBuilder builder = new ProcessBuilder(Arrays.asList(catCommand));
	        
	        builder.redirectOutput(combinedFile);
	        Process p = builder.start();
			try {
				p.waitFor();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		//deleting splited files
		for(int i = 0 ; i < NB ; i++) {
			files[i].delete();
		}
		
		long curTime = System.currentTimeMillis();
		System.out.println((curTime - preTime) + "ms total time - number of reads:" + ir);
		System.out.printf("In:\t%dms\n",inTime);
		System.out.printf("proc:\t%dms\n",prcTime);
		System.out.printf("out:\t%dms\n",outTime);
		System.out.printf("cat:\t%dms\n",(curTime-catTime));
		
		//A CHECK IS REQUIRED
		System.out.println("\n");
		System.out.println("For uploading the file to HDFS use this command to get the best performance");
		System.out.println("replace <hadoop-distanation> with the willing path in hadoop");
		System.out.println("hadoop fs -Ddfs.replication=1 -Ddfs.block.size="+blockSize+" -put "+purefileName+"-meta.fastm <hadoop-distanation>");
		
	}


	public static void metaWrite(LBRead readBuf[][], BufferedWriter pws[], int ibuf, int ir, int ipw) throws IOException {
		for (int j = 0; j < pws.length; j++) {
			for (int i = 0; i < ibuf; i++) {
				String s = readBuf[j][i].toString();
				readBuf[j][i]=null;
				pws[j].write(s);
				filesLines[j]++;
			}
			try {
				String s = readBuf[j][ibuf].toString();
				readBuf[j][ibuf+1]=null;
				pws[j].write(s);
				filesLines[j]++;
			}
			catch (Exception e) {}
		}
	}
	
}

class LBRead implements Comparable<LBRead> {
	String ID;
	String Seq;
	String Qual;

	public LBRead(String ID, String Seq, String Qual) {
		int end = ID.indexOf(" ");
		if(end<0) end = ID.length();
		this.ID = ID.substring(0,end);
		this.Seq = Seq;
		this.Qual = Qual;
	}

	@Override
	public int compareTo(LBRead arg0) {
		return this.Seq.length() - arg0.Seq.length();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ID + " " + Seq + " " + Qual+"\n";
	}
}