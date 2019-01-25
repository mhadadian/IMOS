import java.util.ArrayList;


public class ThirdParty {
	//variables must be declared as static
	
	//initialize your aligner
	//args are the exact arguments gives to IMOS worker
	public static void init(String args[]) {
		
	}
	//the input is a list of reads each for line encapsulated in an string try split them with "\n"
	//out file should be list of lines in sam file
	//This method will be called periodically
	public static Iterable<String> proccess(ArrayList<String> inList, ArrayList<String> outFile){
		//place your code here and return the .sam data line by line
		return outFile;
	}
}
