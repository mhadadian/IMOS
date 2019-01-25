package shortRead;

public class Repository {
	public static String intToBytes(int a){
		char a0 =(char)(a & 255);
		a=a>>8;
		char a1 =(char)(a & 255);
		a=a>>8;
		char a2 =(char)(a & 255);
		a=a>>8;
		char a3 =(char)(a & 255);
//		String out = a3+""+a2+""+a1+""+a0+"";
		String out = a0+""+a1+""+a2+""+a3;
		return out;
		
//		byte a0 = (byte) (a & 255);
//		a=a>>8;
//		byte a1 = (byte) (a & 255);
//		a=a>>8;
//		byte a2 = (byte) (a & 255);
//		a=a>>8;
//		byte a3 = (byte) (a & 255);
//		String out = a3+""+a2+""+a1+""+a0+"";
////		String out = a0+""+a1+""+a2+""+a3;
//		return out;
	}
	public static String longToBytes(long a){
		char a0 =(char)(a & 255);
		a=a>>8;
		char a1 =(char)(a & 255);
		a=a>>8;
		char a2 =(char)(a & 255);
		a=a>>8;
		char a3 =(char)(a & 255);
		a=a>>8;
		char a4 =(char)(a & 255);
		a=a>>8;
		char a5 =(char)(a & 255);
		a=a>>8;
		char a6 =(char)(a & 255);
		a=a>>8;
		char a7 =(char)(a & 255);
		String out = a7+""+a6+""+a5+""+a4+""+a3+""+a2+""+a1+""+a0+"";
		return out;
	}
	public static String reverseString(String s){
		char[] schars = new char[s.length()];
		for (int i = schars.length - 1; i > -1; i--) {
			schars[schars.length - 1 - i] = s.charAt(i);
		}
		return new String(schars);
	}
}
