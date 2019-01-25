

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;

import bio.ALRecord;
import bio.ASRecord;
import bio.Arguments;
import shortRead.FastScanner;
import shortRead.SureMapObjDyn;

/**
 *
 * @author MOSJAVA
 */
public class IM {
	final static int minL = 31;
	static Log LOG;
	static BufferedWriter BWAligned;

	/// Aligner Variable
	static String dashG = "-g";
	static String inputName;
	static String ref0;
	static String ref1;
	static String errorRate = null;
	static SureMapObjDyn smo0;
	static SureMapObjDyn smo1;
	static int batchSize = -1;
	// static ALRecord alrs [];
	static ASRecord[] asrs;
	
	// readCount
	static int rC = 0;

	public static void main(String[] args) throws InterruptedException, IOException {
		if(args.length==0){
			printUsage();
			System.exit(0);
		}
		System.out.println("IMOS Client Mode Started V2.0: ");
		// ------------------ Initialize Contex ------------------//

		Arguments arg = new Arguments();
		initArgs(arg, args);
		// ------------- Read Ref -----------//

		arg.setFast(true);
		smo0 = new SureMapObjDyn();
		smo0.init(arg, new String[] { dashG, "-u", "-e", errorRate, "-m", arg.getMode(), "-o", "output.sam", ref0,
				"query.fastq" });
		if (ref1 != null) {
			smo1 = new SureMapObjDyn();
			smo1.init(arg, new String[] { dashG, "-u", "-e", errorRate, "-m", arg.getMode(), "-o", "output.sam", ref1,
					"query.fastq" });
		}
		FastScanner in = new FastScanner(new File(inputName));

		File Ferr = new File("err.txt");
		PrintWriter err = new PrintWriter(Ferr);
		int Iteration = 1;
		long prevTime = System.currentTimeMillis();
		long startTime = prevTime;
		int alignment = 0;
		int assignment= 0;
		int totalReads= 0;
		try {
			while (in.hasMoreTokens()) {
				int alc = 0;
				int asc = 0;
				rC = 0;
				ArrayList<ALRecord> al = new ArrayList<>();
				while (in.hasMoreTokens()) {
					String lines[] = { in.nextLine(), in.nextLine(), in.nextLine(), in.nextLine() };
					lines[0]= new StringTokenizer(lines[0]).nextToken();
					al.add(new ALRecord(lines[0].substring(1), lines[1].toLowerCase(), lines[3], arg.getFragmentSize(),
							arg.getCorrDist()));
					rC++;
					if (rC >= batchSize) {
						break;
					}
				}
				Iterator<ALRecord> it = smo0.threadDistribute(al.iterator()).iterator();
				al.clear();
				while (it.hasNext()) {
					ALRecord r = it.next();
					if (r.isMaped()) {
						printRecordAlign(r, 1);
						alc++;
					} else {
						r.clear();
						al.add(r);
					}
				}
				BWAligned.flush();

				if (ref1 != null) {
					//// ------------ REF1
					it = smo1.threadDistribute(al.iterator()).iterator();
					al.clear();
					while (it.hasNext()) {
						ALRecord r = it.next();
						if (r.isMaped()) {
							printRecordAlign(r, 1);
							alc++;
						} else {
							al.add(r);
						}
					}
				}
				//ASSIGNMENT
				Iterator<ASRecord> its = smo0.threadDistributeAssign(al.iterator()).iterator();
				ArrayList<ASRecord> as = new ArrayList<ASRecord>();
				while (its.hasNext()) {
					ASRecord r = its.next();
					if (r.isMaped()) {
						printRecordAssign(r);
						asc++;
					} else {
						as.add(r);
					}
				}
				if (ref1 != null) {
					its = smo1.threadDistributeAssign(as.iterator()).iterator();
					while (its.hasNext()) {
						ASRecord r = its.next();
						if (r.isMaped()) {
							asc++;
						}
						printRecordAssign(r);
					}
				}
				else {
					for(ASRecord r : as) {
						printRecordAssign(r);
					}
				}
				long ct = System.currentTimeMillis();
				alignment+=alc;
				assignment+=asc;
				totalReads+=rC;
				System.out.println("Step =" + Iteration++ + " time=" + (ct - prevTime) / 1000 + "s" + "\tAligned: Tot="+(alc+asc)+"\t aln="+alc+"\tass="+asc);
				BWAligned.flush();
				prevTime = ct;
			}
		} catch (Exception e) {
			e.printStackTrace(err);
		}
		double mapRate = (double)(alignment+assignment)/(double)totalReads;
		System.out.println("Total Reads =\t"+totalReads);
		System.out.println("Total Map =\t"+(alignment+assignment));
		System.out.println("Map Rate =\t"+mapRate);
		System.out.println("Alignment =\t"+alignment);
		System.out.println("Assignment =\t"+assignment);
		System.out.println("TOTAL TIME IS "+(prevTime-startTime)/1000+"s REAL TIME");
		err.close();

	}
	private static void printUsage(){
		System.out.println("usage:\tjava -cp IMOS.jar IM [OPTIONS] -I [inputFQ] -REF [index]");
		System.out.println("Warning: use -Xmx18G for human genome");
		System.out.println("");
		System.out.println("inputFQ:\tInput reads in FastQ format");
		System.out.println("index:\t\tIndex files name built with index builder");
		System.out.println();
		System.out.println("OPTIONS:");
		System.out.println("\t-C [int]:\tNumber of cores");
		System.out.println("\t-ER [float]:\tTolerable error rate, 0<rate<1");
		System.out.println("\t-O [String]:\toutput file path");
		System.out.println("\t-RF [int]:\tRefine Factor 1<factor<10 [default=4]");
		System.out.println("\t-X [String]:\tSequencer Machine : {\"Pacbio\",\"Illumina\"}");
		System.out.println();
		System.out.println("EXAMPLE: java -cp IMOS.jar IM -c 4 -X Pacbio -O out.sam -I Read.fq -REF chr19.fa");
	}

	private static void initArgs(Arguments arg, String[] args) throws IOException {
		arg.setFragmentSize(40);
		arg.setDepth((byte) 10);
		arg.setCorrDist((byte) 5);
		arg.setCore(1);
		arg.setMinL(31);
		arg.setMode("fast");
		arg.setFast(true);
		String outFile = "";
		String Sequencer = "";

		boolean minLSet = false;
		// core=1;
		for (int i = 0; i < args.length; i++) {
			switch (args[i].toUpperCase()) {
			case "-G":
				dashG = "-1";
				break;
			case "-B":
				batchSize = Integer.parseInt(args[++i]);
				break;
			case "-L1":
				arg.setFragmentSize(Integer.parseInt(args[++i]));
				break;
			case "-DP":
				arg.setDepth(Byte.parseByte(args[++i]));
				break;
			case "-CORR":
				arg.setCorrDist(Byte.parseByte(args[++i]));
				break;
			case "-C":
				arg.setCore(Integer.parseInt(args[++i]));
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
			case "-REF":
				ref0 = args[++i];
				break;
			// case "-REF1":
			// ref1 = args[++i];
			// break;
			case "-O":
				outFile = args[++i];
				break;
			case "-X":
				Sequencer = args[++i];
				break;
			}
		}

		if (inputName == null) {
			System.out.println("Missing Input");
			printUsage();
			System.exit(0);
		}
		if (ref0 == null) {
			System.out.println("Missing Index");
			printUsage();
			System.exit(0);
		}
		String Refer = ref0;
		int lindex = Refer.lastIndexOf(".");
		ref0 = Refer.substring(0, lindex) + "0" + ".fa";
		ref1 = Refer.substring(0, lindex) + "1" + ".fa";
		if (!new File(ref1+".fm").exists()) {
			ref1 = null;
		}
		if (Sequencer.equalsIgnoreCase("pacbio")) {
			if(errorRate==null)
				errorRate = "0.08";
			if(!minLSet)
				arg.setMinL(400);
			if(batchSize==-1)
				batchSize = 1000;
		} else if (Sequencer.equalsIgnoreCase("illumina")) {
			if(errorRate==null)
				errorRate = "0.01";
			if(!minLSet)
				arg.setMinL(31);
			if(batchSize==-1)
				batchSize = 10000;
		} else {
			Sequencer = "No Sequencer Defined";
		}
		if(errorRate==null) {
			errorRate = "0.05";
		}
		if (!minLSet && Double.parseDouble(errorRate) > 0.015) {
			arg.setMinL(400);
		}
		
		if (outFile.equals("")) {
			outFile = inputName + "-out";
		}
		File f0 = new File(outFile);
		if (f0.exists()) {
			f0.delete();
		}
		BWAligned = new BufferedWriter(new FileWriter(f0, true));
		
		System.out.println("============ Running Options ============");
		System.out.println("\t# of Cores:\t" + arg.getCore());
		System.out.println("\tBatch Size:\t" + batchSize);
		System.out.println("\tGap 4 frag:\t" + dashG);
		System.out.println("\tL1 Size:\t" + arg.getFragmentSize());
		System.out.println("\tSequencer:\t" + Sequencer);
		System.out.println("\tError Rate:\t" + errorRate);
		System.out.println("\tInput:\t\t" + inputName);
		System.out.println("\tOutput:\t\t" + outFile);
		System.out.println("\tReference:\t" + Refer);
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

	private static void printRecordAlign(ALRecord x, int P) throws IOException {
		BWAligned.write(x.getID() + "\t" + x.getFlag() + "\t" + x.getReference() + "\t" + x.getPosition() + "\t255\t"
				+ x.getCigar() + "\t*\t0\t0\t" + x.getRead() + "\t" + x.getScore() + "\tAS:i:" + x.getAS() + "\tMD:Z:"
				+ x.getMD() + "\tNM:i:" + x.getNM() + "\n");
	}

	private static void printRecordAssign(ASRecord x) throws IOException {
		if (x.isMaped()) {
			for (int i = 0; i < x.getPosition().length; i++) {
				if (x.getValidPath()[i]) {
					BWAligned.write(x.getID() + "\t" + x.getFlag()[i] + "\t" + x.getReference()[i] + "\t"
							+ x.getPosition()[i] + "\t255\t" + x.getCigar()[i] + "\t*\t0\t0\t" + x.getRead() + "\t"
							+ x.getScore() + "\tAS:i:" + x.getAS()[i] + "\tMD:Z:" + x.getMD()[i] + "\tNM:i:"
							+ x.getNM()[i] + "\n");
				}
			}
		} else {
			BWAligned.write(x.getID() + "\t" + 4 + "\t" + "*" + "\t" + "*" + "\t0\t" + "*" + "\t*\t0\t0\t" + x.getRead()
					+ "\t" + x.getScore()+"\n");
		}
	}

}
