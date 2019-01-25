package bio;

public class Path implements Comparable<Path>{
	public int fragNum;
	public int pos;
	public int score;
	public boolean isReverse;
	public boolean isTrue;
	public Path(int fragNum , int pos , int score , boolean isReverse) {
		this.fragNum=fragNum;
		this.pos=pos;
		this.score=score;
		this.isReverse=isReverse;
		this.isTrue=false;
	}
	@Override
	public int compareTo(Path p) {
		return p.score-this.score;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "i="+fragNum+" p="+pos+" s="+score+" r="+isReverse;
	}
}
