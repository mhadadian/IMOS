package shortRead;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;




public class IndexB {
	public static void main(String [] args) throws IOException{
		int opt = -1;
		int samplerate;
//		String inname = args[0];
		String inname = "chr19.fa";
		String refFile = inname.substring(inname.lastIndexOf("\\")+1);
//		int n;
		String idxname = "";
		long offSet = 0;
		StringBuilder Ref = new StringBuilder();
		ArrayList<String> refNames = new ArrayList<String>();
		ArrayList<Long>	refOffset = new ArrayList<Long>();
		FastScanner inp = new FastScanner(new File(inname));
		
		while(inp.hasMoreTokens()){
			String line = inp.nextLine();
			if(line.charAt(0)=='>'){
				String refName = line.substring(1).trim();
				refNames.add(refName);
				refOffset.add(offSet);
			}
			else{
				line = line.toLowerCase();
//				if(line.matches("^[atcgn]+$")){
					Ref.append(line);
					offSet+=line.length();
//				}
			}
//			System.out.println(offSet/50);
			
		}
		/** Building rinfo and cinfo files **/
		char[] T = new char[Ref.length()];
//		Ref.getChars(0, T.length, T, 0);
//		CompressedString ref = new CompressedString();
//		File rinfofile = new File(refFile+".rinfoJ");
//		FileChannel rinfo = new FileOutputStream(rinfofile, false).getChannel();
//		ref.set(T, Ref.length());
//		ref.zeroPos = Ref.length();
//		ref.save(rinfo);
//		rinfo.close();
//		PrintWriter cinfo = new PrintWriter(refFile+".cinfoJ");
//		for(int i = 0 ; i < refNames.size() ; i++){
//			cinfo.println(refNames.get(i)+" "+refOffset.get(i));
//		}
//		cinfo.close();
//		System.out.println("Reference Length=\t"+ T.length);
		
		/** Building Forward BWT **/
		int n = T.length;
		T = new char[Ref.length()+100];
		Ref.getChars(0, n, T, 0);
		T[n] = 0;
		System.out.println("Check Point 0");
		BWT fmBwt = new BWT(T, T.length, 0);
		File fmfile = new File(refFile+".fmJ");
		FileChannel fm = new FileOutputStream(fmfile, false).getChannel();
		System.out.println("Check Point 1");
		fmBwt.save(fm);
		System.out.println("Check Point 2");
	}
}
