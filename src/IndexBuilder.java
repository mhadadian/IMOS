import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class IndexBuilder {
	
	public static void main(String [] args) throws IOException{
		if(args.length==0){
			printUsage();
		}
		
		File check = new File("SureMap-IndexBuilder");
		if(!check.exists()){
			System.err.println("SureMap-IndexBuilder not exist!");
			System.exit(0);
		}
		Scanner in = null;
		String firstName="";
		String secondName="";
		try{
		File inf = new File(args[0]);
		in = new Scanner(inf);
		int lindex = args[0].lastIndexOf(".");
		firstName = args[0].substring(0, lindex)+"0";
		secondName = args[0].substring(0, lindex)+"1";
		}catch (Exception e) {
			printUsage();
			System.exit(0);
		}
		
		firstName += ".fa"; 
		secondName+= ".fa";	
		File f = new File(firstName);
		PrintWriter pw = new PrintWriter(f);
		int x = 0;
		String chromosome = in.nextLine();
		StringBuffer sb = null;
		Boolean tooLarge = false;
		while(in.hasNextLine()){
			sb = new StringBuffer(chromosome+"\n");
			while(in.hasNextLine()){
				String s = in.nextLine();
				if(s.charAt(0)=='>'){
					chromosome = s;
					break;
				}
				sb.append(s+"\n");
			}				 
			if(sb.length()+x<2000000000){
				x+=sb.length();
				pw.print(sb.toString());
			}
			else{
				tooLarge = true;
				pw.close();
				f = new File(secondName);
				pw = new PrintWriter(f);
				pw.print(sb.toString());
				pw.println(chromosome);
				while(in.hasNextLine()){
					pw.println(in.nextLine());
				}
				pw.close();
				
			}
		}
		in.close();
		Runtime.getRuntime().exec("./SureMap-IndexBuilder "+firstName+" ./");
		if(tooLarge)
		Runtime.getRuntime().exec("./SureMap-IndexBuilder "+secondName+" ./");
	}

	private static void printUsage() {
		System.out.println("java -cp IMOS.jar IndexBuilder [FA file]");
		System.out.println("\tExample:");
		System.out.println("\t\tjava -cp IMOS.jar IndexBuilder chr19.fa");
		
	}
}
