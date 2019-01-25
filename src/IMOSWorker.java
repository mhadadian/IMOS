
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import bio.ALRecord;
import bio.ASRecord;
import bio.Arguments;
import shortRead.SureMapObjDyn;

/**
 *
 * @author MOSJAVA
 */
public class IMOSWorker {
	final static int minL = 31;
	static boolean isReady = false;
	/// Meta-Aligner Aligner Variable
	static String dashG = "-g";
	static String ref0;
	static String ref1;
	static String errorRate = null;
	static SureMapObjDyn smo0;
	static SureMapObjDyn smo1;



	static int outCount = 0;

	public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {
		if (args.length < 2) {
			printUsage();
			System.exit(0);
		}
		System.out.println("IMOS Worker Mode Started V3.1.7: ");
		// V1 : Prototype
		// V2 : IMOS Meta-Aligner Final
		// V3 : MiniMap added
		// V3.1: Third Party Added
		// ------------------ Initialize Contex ------------------//
		if (args[0].equalsIgnoreCase("mini")) {
			miniMap(args);
		} else if (args[0].equalsIgnoreCase("im")) {
			metaMap(args);
		} else if (args[0].equalsIgnoreCase("third")) {
			mapThridParty(args);
		} else {
			System.out.println("Select the aligner used in worker nodes among [mini,meta,third]");
			printUsage();
			return;
		}

	}

	@SuppressWarnings("deprecation")
	private static void mapThridParty(String args[]) throws IOException {
		ThirdParty.init(args);
		
		// process
				while (true) {
					// Socket Work
					ServerSocket server = new ServerSocket(7777, 2);
					System.out.println("[IMOSWorker] WAITING FOR CONNECTION");
					Socket socket = server.accept();
					long startTime = System.currentTimeMillis() / 1000 % 1000000;
					System.out.println("[IMOSWorker:" + startTime + "] Connected");
					DataOutputStream oouts = new DataOutputStream(socket.getOutputStream());
					DataInputStream oins = new DataInputStream(socket.getInputStream());
					oins.readUTF();
					oins.readUTF();
					final ArrayList<String> outList = new ArrayList<String>();
					final ArrayList<String> inList = new ArrayList<String>();
					Thread align = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								System.out.println("[IMOSWorker] Start Reading");
								String o = myReadUTF(oins);
								int cnt = 0;
								while (o != null && !o.equalsIgnoreCase("END")) {
									cnt++;
									String temp = o + "\n" + myReadUTF(oins) + "\n+\n" + myReadUTF(oins);
									inList.add(temp);
									o = myReadUTF(oins);
								}
								System.out.println("[IMOSWorker] " + cnt + " reads");
								
								ThirdParty.proccess(inList,outList);
								
								for (String str : outList) {
									myWriteUTF(oouts, str);
								}
								
								myWriteUTF(oouts, "END");
								oouts.flush();
								while (!oins.readUTF().equalsIgnoreCase("END"))
									;
							} catch (IOException e) {
								// TODO: handle exception
							}
						}
					});
					align.start();
					while(align.isAlive()) {
						try {
							Thread.sleep(1000);
							myWriteUTF(oouts, "@");
						} catch (InterruptedException e) {e.printStackTrace();}
						catch (IOException e) {
							align.stop();
							startTime = System.currentTimeMillis() / 1000 % 1000000;
							System.out.println("[IMOSWorker:" + startTime + "] Connection is lost");
						}
						
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					socket.close();
					server.close();
					startTime = System.currentTimeMillis() / 1000 % 1000000;
					System.out.println("[IMOSWorker:" + startTime + "] Finished");
				}
		
		
	}

	@SuppressWarnings("deprecation")
	private static void metaMap(String args[]) throws IOException {
		// init the meta
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

		// process
		while (true) {
			// Socket Work
			ServerSocket server = new ServerSocket(7777, 2);
			System.out.println("[IMOSWorker] WAITING FOR CONNECTION");
			Socket socket = server.accept();
			long startTime = System.currentTimeMillis() / 1000 % 1000000;
			System.out.println("[IMOSWorker:" + startTime + "] Connected");
			DataOutputStream oouts = new DataOutputStream(socket.getOutputStream());
			DataInputStream oins = new DataInputStream(socket.getInputStream());

			int ml = Integer.parseInt(oins.readUTF());
			double er = Double.parseDouble(oins.readUTF());
			smo0.Args.setMinL(ml);
			smo0.globalNoisePercent = er;
			if (smo1 != null) {
				smo1.Args.setMinL(ml);
				smo1.globalNoisePercent = er;
			}
			final ArrayList<String> outList = new ArrayList<String>();
			Thread align = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						ArrayList<ALRecord> al = new ArrayList<ALRecord>();

						System.out.println("[IMOSWorker] Start Reading");
						String o = myReadUTF(oins);
						int cnt = 0;
						while (o != null && !o.equalsIgnoreCase("END")) {
							cnt++;
							al.add(new ALRecord(o, myReadUTF(oins), myReadUTF(oins), arg.getFragmentSize(),
									arg.getCorrDist()));
							o = myReadUTF(oins);
						}
						System.out.println("[IMOSWorker] " + cnt + " reads");

						File Ferr = new File("err.txt");
						PrintWriter err = new PrintWriter(Ferr);
						// int Iteration = 1;
						// long prevTime = System.currentTimeMillis();
						try {

							Iterator<ALRecord> it = smo0.threadDistribute(al.iterator()).iterator();
							al.clear();
							while (it.hasNext()) {
								ALRecord r = it.next();
								if (r.isMaped()) {
									outList.add(printRecordAlign(r));
								} else {
									r.clear();
									al.add(r);
								}
							}

							if (ref1 != null) {
								//// ------------ REF1
								it = smo1.threadDistribute(al.iterator()).iterator();
								al.clear();
								while (it.hasNext()) {
									ALRecord r = it.next();
									if (r.isMaped()) {
										outList.add(printRecordAlign(r));
									} else {
										al.add(r);
									}
								}
							}
							Iterator<ASRecord> its = smo0.threadDistributeAssign(al.iterator()).iterator();
							ArrayList<ASRecord> as = new ArrayList<ASRecord>();
							while (its.hasNext()) {
								ASRecord r = its.next();
								if (r.isMaped()) {
									outList.add(printRecordAssign(r));
								} else {
									as.add(r);
								}
							}
							if (ref1 != null) {
								its = smo1.threadDistributeAssign(as.iterator()).iterator();
								while (its.hasNext()) {
									ASRecord r = its.next();
									outList.add(printRecordAssign(r));
								}
							} else {
								for (ASRecord r : as) {
									outList.add(printRecordAssign(r));
								}
							}

						} catch (Exception e) {
							e.printStackTrace(err);
						}
						for (String str : outList) {
							myWriteUTF(oouts, str);
						}
						err.close();
						myWriteUTF(oouts, "END");
						oouts.flush();
						while (!oins.readUTF().equalsIgnoreCase("END"))
							;
					} catch (IOException e) {
						// TODO: handle exception
					}
				}
			});
			align.start();
			while(align.isAlive()) {
				try {
					Thread.sleep(1000);
					myWriteUTF(oouts, "@");
				} catch (InterruptedException e) {e.printStackTrace();}
				catch (IOException e) {
					align.stop();
					startTime = System.currentTimeMillis() / 1000 % 1000000;
					System.out.println("[IMOSWorker:" + startTime + "] Connection is lost");
				}
				
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			socket.close();
			server.close();
			startTime = System.currentTimeMillis() / 1000 % 1000000;
			System.out.println("[IMOSWorker:" + startTime + "] Finished");
		}
	}

	private static void miniMap(String args[]) throws IOException {
		System.out.println("MiniMap2 is selected");
		String options = "";
		for (int i = 1; i < args.length; i++) {
			options += args[i] + " ";
		}
		final Process p = Runtime.getRuntime().exec("./minimap2 " + options + "minimos");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				p.destroy();
			}
		});

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		final BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		Thread T = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String s = null;
				try {
					while ((s = stdError.readLine()) != null) {
						if (!isReady)
							if (s.equals("[MOSJAVA] Comunicating with IMOS Worker..."))
								isReady = true;
						System.out.println(s);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		T.start();
		while (true) {
			// Socket Work
			ServerSocket server = new ServerSocket(7777, 2);
			System.out.println("[IMOS Worker]: WAITING FOR CONNECTION");
			Socket socket = server.accept();

			System.out.println("[IMOS Worker]: Connected");
			// PrintWriter pw = new PrintWriter(tempFile);
			DataOutputStream oouts = new DataOutputStream(socket.getOutputStream());
			DataInputStream oins = new DataInputStream(socket.getInputStream());
			Socket socketMini = null;// = new Socket("localhost", 7778);
			DataOutputStream dos = null;
			String absPath = "";
			try {

				absPath = oins.readUTF();
				
				// contacting minimap
				while (!isReady) {
					Thread.sleep(1000);
				}
				socketMini = new Socket("localhost", 7778);
				dos = null;
				try {
					dos = new DataOutputStream(socketMini.getOutputStream());
					String s = absPath + (char) 0;
					dos.write((s).getBytes());
				} catch (IOException e) {
					System.out.println("Signal Terminate is received");
				}
				int startNum = outCount;
				// recieving data from minimap and sending to IMOS master
				String line = stdInput.readLine();
				while (line != null && !line.equalsIgnoreCase("finish@@@finish@@@finish")) {
					if (line.charAt(0) != '@') {
						myWriteUTF(oouts, line);
					}
					line = stdInput.readLine();
				}
				System.out.println((outCount - startNum) + " lines send through");
				myWriteUTF(oouts, "END");
				oouts.flush();
				while (!oins.readUTF().equalsIgnoreCase("END"))
					;
				oouts.close();
				oins.close();
				dos.close();
				socketMini.close();
				socket.close();
				server.close();
			} catch (Exception e) {
				System.out.println("----------------------HANDLED EXCEPTION 2------------------------");
//				e.printStackTrace();
				if (oouts != null)
					oouts.close();
				if (oins != null)
					oins.close();
				if (dos != null)
					dos.close();
				if (socketMini != null)
					socketMini.close();
				if (socket != null)
					socket.close();
				if (server != null)
					server.close();
				File delFile = new File(absPath);
				if(delFile.exists()) {
					delFile.delete();
				}
			}
		}

	}

	private static void myWriteUTF(DataOutputStream oouts, String line) throws IOException {
		int part = line.length() / 50000;
		oouts.writeByte(part);
		for (int i = 0; i <= part; i++) {
			int st = i * 50000;
			int en = Math.min(st + 50000, line.length());
			oouts.writeUTF(line.substring(st, en));
		}
		outCount++;
	}

	private static String myReadUTF(DataInputStream dins) throws IOException {
		byte b = dins.readByte();
		String s = "";
		for (int i = 0; i <= b; i++) {
			s += dins.readUTF();
		}
		return s;
	}

	private static void printUsage() {
		System.out.println("Usage:\tjava -cp IMOS.jar IMOSWorker [ALIGNER] [OPTIONS] -REF [index]");
		System.out.println("Warning: port 7777 and 7778 must be open");
		System.out.println("Warning: use -Xmx18G for human genome");
		System.out.println("");
		System.out.println("index:\tIndex files name built with index builder");
		System.out.println("ALIGNER:\tIM : Improved Meta-aligner\n\t\tMini : Minimap2\n\t\tThird : 3rd party aligner");
		System.out.println();
		System.out.println("OPTIONS:");
		System.out.println("    Minimap2:");
		System.out.println("\tThe arguments give directly to the Minimap2. See its help for more details.");
		System.out.println("    Third:");
		System.out.println("\tThe arguments give directly to the Third party aligner.");
		System.out.println("    IM:");
		System.out.println("\t-C [int]:\tNumber of cores");
		System.out.println("\t-ER [float]:\tTolerable error rate, 0<rate<1");
		System.out.println("\t-RF [int]:\tRefine Factor 1<factor<10 [default=4]");
		System.out.println("\t-X [String]:\tSequencer Machine : {\"Pacbio\",\"Illumina\"}");
		System.out.println();
		System.out.println("EXAMPLE: java -cp IMOS.jar IMOSWorker im -c 4 -x Pacbio -REF chr19.fa");
	}

	private static void initArgs(Arguments arg, String[] args) throws IOException {
		arg.setFragmentSize(40);
		arg.setDepth((byte) 10);
		arg.setCorrDist((byte) 5);
		arg.setCore(1);
		arg.setMinL(31);
		arg.setMode("fast");
		arg.setFast(true);
		String Sequencer = "";

		boolean minLSet = false;
		// core=1;
		for (int i = 1; i < args.length; i++) {
			switch (args[i].toUpperCase()) {
			case "-G":
				dashG = "-1";
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
				arg.setMinL(Integer.parseInt(args[++i]) * 100);
				minLSet = true;
				break;
			case "-ER":
				errorRate = args[++i];
				break;
			case "-REF":
				ref0 = args[++i];
				break;
			case "-X":
				Sequencer = args[++i];
				break;
			}
		}
		if (ref0 == null) {
			System.out.println("Missing Refrence");
			printUsage();
			System.exit(0);
		}
		String Refer = ref0;
		int lindex = Refer.lastIndexOf(".");
		ref0 = Refer.substring(0, lindex) + "0" + ".fa";
		ref1 = Refer.substring(0, lindex) + "1" + ".fa";
		if (!new File(ref1 + ".fm").exists()) {
			ref1 = null;
		}
		if (Sequencer.equalsIgnoreCase("pacbio")) {
			if (errorRate == null)
				errorRate = "0.08";
			if (!minLSet)
				arg.setMinL(400);
		} else if (Sequencer.equalsIgnoreCase("illumina")) {
			if (errorRate == null)
				errorRate = "0.01";
			if (!minLSet)
				arg.setMinL(31);
		} else {
			Sequencer = "No Sequencer Defined";
		}
		if (errorRate == null) {
			errorRate = "0.05";
		}
		if (!minLSet && Double.parseDouble(errorRate) > 0.015) {
			arg.setMinL(400);
		}

		System.out.println("============ Running Options ============");
		System.out.println("\t# of Cores:\t" + arg.getCore());
		System.out.println("\tSequencer:\t" + Sequencer);
		System.out.println("\tError Rate:\t" + errorRate);
		System.out.println("\tReference:\t" + Refer);
	}

	private static String printRecordAlign(ALRecord x) throws IOException {
		x.setID(x.getID().substring(0, x.getID().length() - 1));
		x.setRead(x.getRead().substring(0, x.getRead().length() - 1));
		x.setScore(x.getScore().substring(0, x.getScore().length() - 1));
		return (x.getID() + "\t" + x.getFlag() + "\t" + x.getReference() + "\t" + x.getPosition() + "\t255\t"
				+ x.getCigar() + "\t*\t0\t0\t" + x.getRead() + "\t" + x.getScore() + "\tAS:i:" + x.getAS() + "\tMD:Z:"
				+ x.getMD() + "\tNM:i:" + x.getNM());
	}

	private static String printRecordAssign(ASRecord x) throws IOException {
		x.setID(x.getID().substring(0, x.getID().length() - 1));
		x.setRead(x.getRead().substring(0, x.getRead().length() - 1));
		x.setScore(x.getScore().substring(0, x.getScore().length() - 1));
		String S = "";
		boolean first = true;
		if (x.isMaped()) {
			for (int i = 0; i < x.getPosition().length; i++) {
				if (x.getValidPath()[i]) {
					if (!first) {
						S += "\n";
					}
					first = false;
					S += (x.getID() + "\t" + x.getFlag()[i] + "\t" + x.getReference()[i] + "\t" + x.getPosition()[i]
							+ "\t255\t" + x.getCigar()[i] + "\t*\t0\t0\t" + x.getRead() + "\t" + x.getScore()
							+ "\tAS:i:" + x.getAS()[i] + "\tMD:Z:" + x.getMD()[i] + "\tNM:i:" + x.getNM()[i]);
				}
			}
		} else {
			S = (x.getID() + "\t" + 4 + "\t" + "*" + "\t" + "*" + "\t0\t" + "*" + "\t*\t0\t0\t" + x.getRead() + "\t"
					+ x.getScore());

		}
		return S;
	}

}