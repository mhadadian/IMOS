
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.storage.StorageLevel;

import bio.Arguments;
import scala.Tuple2;


public class IMOS {
	static String errorRate = "0.05";
	static String inputName;
	static String outputName;
	static PrintWriter pw;
	static private boolean isMeta = false;
	static boolean isFastM = false;
	public static void main(String[] args) throws IOException {
		if(args.length==0){
			printUsage();
			System.exit(0);
		}
		System.out.println("IMOS SPARK MODE STARTED V2.7.2");
		Arguments arg = new Arguments();
		initArgs(arg, args);
		String appName = "IMOS7 -Options:";
		for(int i = 0 ; i < args.length ; i++) {
			appName+=" "+args[i];
		}
		SparkConf conf = new SparkConf().setAppName(appName);
		conf.set("spark.hadoop.validateOutputSpecs", "false");
		JavaSparkContext spark = new JavaSparkContext(conf);
		Broadcast<Arguments> Args = spark.broadcast(arg);
		JavaRDD<String> wholeFile = spark.textFile(inputName);
//		wholeFile.persist(StorageLevel.MEMORY_AND_DISK_SER());
		
		JavaRDD<String> records = null;
		if(!isFastM) {
			records = wholeFile.zipWithIndex().mapToPair(x -> {
				Tuple2<Long, Tuple2<Integer, String>> tp = new Tuple2<Long, Tuple2<Integer, String>>(x._2/4, new Tuple2<Integer, String>((int)(x._2%4), x._1));
				return tp;
			})
			.groupByKey().map(x -> {
				String rec="";
				String [] lines = new String[4];
				for(Tuple2<Integer, String> tp : x._2) {
					lines[tp._1]=tp._2;
				}
				if(lines[0]==null || lines[1]==null || lines[3]==null) return null;
				if(lines[0].charAt(0)=='@') {
					int end = lines[0].indexOf(" ");
					if(end<0) end = lines[0].length();
					rec = lines[0].substring(0,end) + " " + lines[1] + " " + lines[3];
				}
				return rec;
			});
		} else {
			records = wholeFile;
		}
		

		records.persist(StorageLevel.MEMORY_ONLY());
		JavaRDD<String> results = null;
		if(args[0].equalsIgnoreCase("mini")) {
			results = miniMap(records);
		}
		else if(args[0].equalsIgnoreCase("im")) {
//			records = records.repartition(records.getNumPartitions()*4);
			results = metaMap(records, Args);
		}
		else {
			System.out.println("Please select the aligner among [mini,meta]");
			spark.close();
			return;
		}
		
//		results.coalesce(1);
		results.saveAsTextFile(outputName);
		spark.close();
	}
	private static String myReadUTF(DataInputStream dins) throws IOException {
		byte b = dins.readByte();
		String s = "";
		for(int i = 0 ; i <= b ; i++) {
			s+=dins.readUTF();
		}
		return s;
	}
	

	private static void myWriteUTF(String line, DataOutputStream oouts) throws IOException {
		int part = line.length()/50000;
		oouts.writeByte(part);
		for(int i = 0 ; i <= part ; i++) {
			int st = i*50000;
			int en = Math.min(st+50000, line.length());
			oouts.writeUTF(line.substring(st, en));
		}
	}

	
	private static JavaRDD<String> miniMap(JavaRDD<String> records){
		return records.mapPartitions(x -> {
			ArrayList<String> out = new ArrayList<String>();
			long ThreadID = Thread.currentThread().getId();
			System.out.println("[IMOS:"+ThreadID+"] Writing to file . . .");
			String fileName = "ImosData"+ThreadID+".fastq";
			File tempFile = new File(fileName);
			FileWriter tempFileWriter = new FileWriter(tempFile);
			BufferedWriter bw = new BufferedWriter(tempFileWriter);
			final int buf = 1000;
			String seq[] = new String[buf];
			String qual[] = new String[buf];
			String nameID[] = new String[buf];
			int index = 0;
			int writeToFile = 0;
			while(x.hasNext()){
				String alr = x.next();
				String sp[] = alr.split(" ");
				if(sp.length != 3) {
					System.out.println(sp[0]);
					continue;
				}
				nameID[index] = sp[0];
				seq[index] = sp[1];
				qual[index] = sp[2];
				index++;
				if(index==buf) {
					for(int i = 0 ; i< buf; i++) {
						bw.write(nameID[i]+"\n");
						bw.write(seq[i]+"\n");
						bw.write("+\n");
						bw.write(qual[i]+"\n");
						writeToFile++;
					}
					index=0;
				}
			}
			for(int i = 0 ; i< index; i++) {
				bw.write(nameID[i]+"\n");
				bw.write(seq[i]+"\n");
				bw.write("+\n");
				bw.write(qual[i]+"\n");
				writeToFile++;
			}
			bw.flush();
			bw.close();
			System.out.println("[IMOS:"+ThreadID+"] write "+writeToFile+" lines to files");

			System.out.println("[IMOS:"+ThreadID+"] Checking Lock . . .");
			Socket socket;
			File lock = new File("../LOCK22");
			int cnt = 0;
			try {
				while (true) {
					boolean isFileCreated = false;
					try {
						System.out.println("[IMOS:"+ThreadID+"] Checking exist . . .");
						while (lock.exists()) {
							int sleep = (int) (Math.random() * 1000);
							Thread.sleep(sleep);
						}
						lock.createNewFile();
						isFileCreated = true;
						socket = new Socket("localhost", 7777);
						break;
					} catch (Exception e) {
						Thread.sleep(1000);
						System.out.println("[IMOS:"+ThreadID+"] retry connecting . . .");
						if(isFileCreated) {
							lock.delete();
						}
						e.printStackTrace(System.out);
					}
				}
				System.out.println("[IMOS:"+ThreadID+"] connected");
				DataInputStream dins = new DataInputStream(socket.getInputStream());
				DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
				
				System.out.println("[IMOS:"+ThreadID+"] Sending");
				
				dout.writeUTF(tempFile.getAbsolutePath());
				
				dout.flush();
				System.out.println("[IMOS:"+ThreadID+"] "+cnt + "sent");
				dout.writeUTF("END");
				String s = myReadUTF(dins);
				while (!s.equalsIgnoreCase("END")) {
					out.add(s);
					s = myReadUTF(dins);
				}
				dout.writeUTF("END");
				dout.flush();
				Thread.sleep(100);
				tempFile.delete();
				socket.close();
				System.out.println("[IMOS:"+ThreadID+"] FINISHED");
			}
			catch (Exception e) {
				System.out.println("error occured after reading "+cnt+" reads");
				e.printStackTrace(System.err);
				// TODO: handle exception
			}
			lock.delete();
			if(!lock.exists()) {
				System.out.println("[IMOS:"+ThreadID+"] lock released");
			}
			if(tempFile.exists())
				tempFile.delete();
			return out.iterator();
		});
	}
	
	private static JavaRDD<String> metaMap(JavaRDD<String> records, Broadcast<Arguments> Args ){
		return records.mapPartitions(x -> {
			ArrayList<String> out = new ArrayList<String>();
			long ThreadID = Thread.currentThread().getId();
			System.out.println("[IMOS:"+ThreadID+"] Checking Lock . . .");
			Socket socket;
			File lock = new File("../LOCK22");
			int cnt = 0;
			try {
				while (true) {
					boolean isFileCreated = false;
					try {
						System.out.println("[IMOS:"+ThreadID+"] Checking exist . . .");
						while (lock.exists()) {
							int sleep = (int) (Math.random() * 1000);
							Thread.sleep(sleep);
						}
						lock.createNewFile();
						isFileCreated = true;
						socket = new Socket("localhost", 7777);
						break;
					} catch (Exception e) {
						Thread.sleep(1000);
						System.out.println("retry connecting . . .");
						if(isFileCreated) {
							lock.delete();
						}
						e.printStackTrace(System.out);
					}
				}
				System.out.println("[IMOS:"+ThreadID+"] connected");
				DataInputStream dins = new DataInputStream(socket.getInputStream());
				DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
				Arguments myarg = Args.getValue();
				dout.writeUTF(myarg.getMinL() + "");
				dout.writeUTF(myarg.getErrorRate()+"");
	
				System.out.println("[IMOS:"+ThreadID+"] Sending");
//				cnt = 0;
				
				while(x.hasNext()){
					String alr = x.next();
					String sp[] = alr.split(" ");
					try {
						if(sp.length==3) {
							myWriteUTF(sp[0], dout);
							myWriteUTF(sp[1], dout);
							myWriteUTF(sp[2], dout);
							cnt++;
						}
					}
					catch (ArrayIndexOutOfBoundsException e) {
						// TODO: handle exception
					}

				}
				dout.flush();
				System.out.println("[IMOS:"+ThreadID+"] "+cnt + "sent");
				myWriteUTF("END", dout);
				String s = myReadUTF(dins);
				while (!s.equalsIgnoreCase("END")) {
					if(s.charAt(0)!='@')
						out.add(s);
					s = myReadUTF(dins);
				}
				dout.writeUTF("END");
				dout.flush();
				Thread.sleep(100);
	
				socket.close();
				System.out.println("[IMOS:"+ThreadID+"] FINISHED");
			}
			catch (Exception e) {
				System.out.println("error occured after reading "+cnt+" reads");
				e.printStackTrace(System.err);
				// TODO: handle exception
			}
			lock.delete();
			if(!lock.exists()) {
				System.out.println("[IMOS:"+ThreadID+"] lock released");
			}
			return out.iterator();
		});
	}
	
	
	private static void printUsage(){
		System.out.println("usage:\tspark-submit --class IMOS --master [MASTER] --executor-memory 10G --dirver-memory 2G IMOS.jar [ALIGNER] [OPTIONS] -I [inputFQ]");
		System.out.println("");
		System.out.println("MASTER:\t\tIdentify Spark Master local, yarn or ip of spark standalone master");
		System.out.println("inputFQ:\tInput reads in FastQ format");
		System.out.println();
		System.out.println("ALIGNER:\tIM for Improved Meta-aligner and ThirdParty, Mini for Minimap2");
		System.out.println();
		System.out.println("OPTIONS:");
		System.out.println("\t-FM :\tif load balancer is used and the file in the hdfs is a fastm format\n");
		System.out.println("    Mini:");
		System.out.println("\tNo Option is required. The options must be set at the worker nodes.");
		System.out.println("    IM:");
		System.out.println("\t-ER [float]:\tTolerable error rate, 0<rate<1");
		System.out.println("\t-O [String]:\toutput file path");
		System.out.println("\t-X [String]:\tSequencer Machine : {\"Pacbio\",\"Illumina\"}");
		System.out.println();
		System.out.println("EXAMPLE: spark-submit --class IMOS --master local --executor-memory 10G --dirver-memory 2G IMOS.jar IM -X Pacbio -I Read.fq -O out.sam");
	}

	private static void initArgs(Arguments arg, String[] args){
		arg.setFragmentSize(40);
		arg.setDepth((byte) 10);
		arg.setCorrDist((byte) 5);
		arg.setMinL(31);
		arg.setMode("fast");
		arg.setFast(true);
		String Sequencer = "";
		outputName = "";
		
		boolean minLSet = false;
		// core=1;
		for (int i = 1; i < args.length; i++) {
			switch (args[i].toUpperCase()) {
			case "-L1":
				arg.setFragmentSize(Integer.parseInt(args[++i]));
				break;
			case "-DP":
				arg.setDepth(Byte.parseByte(args[++i]));
				break;
			case "-CORR":
				arg.setCorrDist(Byte.parseByte(args[++i]));
				break;
			case "-RF":
				arg.setMinL(Integer.parseInt(args[++i])*100);
				minLSet = true;
				break;
			case "-ER":
				errorRate = args[++i];
				break;
			case "-I":
				inputName = args[++i];
				break;
			case "-O":
				outputName = args[++i];
				break;
			case "-X":
				Sequencer = args[++i];
				break;
			case "-FM":
				isFastM = true;
				break;
			}
		}
		if(args[0].equalsIgnoreCase("meta")) {
			isMeta = true;
		}
		if (inputName == null) {
			System.out.println("Missing Input");
			printUsage();
			System.exit(0);
		}
		if(isMeta) {
			if (Sequencer.equalsIgnoreCase("pacbio")) {
				if(errorRate==null)
					errorRate = "0.08";
				if(!minLSet)
					arg.setMinL(400);
			} else if (Sequencer.equalsIgnoreCase("illumina")) {
				if(errorRate==null)
					errorRate = "0.01";
				if(!minLSet)
					arg.setMinL(31);
			} else {
				Sequencer = "No Sequencer Defined";
			}
			if(errorRate==null) {
				errorRate = "0.05";
			}
			if (!minLSet && Double.parseDouble(errorRate) > 0.015) {
				arg.setMinL(400);
			}
		}
		if (outputName.equals("")) {
			outputName = inputName + "-out";
		}
		arg.setErrorRate(Double.parseDouble(errorRate));
		System.out.println("============ Running Options ============");
		if(isMeta) {
			System.out.println("\tSequencer:\t" + Sequencer);
			System.out.println("\tError Rate:\t" + errorRate);
		} else {
			
		}
		System.out.println("\tInput:\t\t" + inputName);
		System.out.println("\tOutput:\t\t" + outputName);
		
	}
	
}