package shortRead;

public class DoubleEndList<U> {
	public U list[];
	int size;
	int left;
	int right;
	private int I = 0;
	private boolean full;
	@SuppressWarnings("unchecked")
	public DoubleEndList(int size) {
		this.size = size;
		left = 0;
		right = size-1;
		I = 0;
		full = false;
		list = (U[]) new Object[size];
	}
	public boolean isFull() {
		return full;
	}
	public void setFull() {
		this.full = true;
	}
	public boolean add(U a) {
		try {
			list[I++] = a;
			return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			return false;
		}
	}

	public U getLeft() {
		if (left <= right) {
			return list[left++];
		} else {
			return null;
		}
	}

	public U getRight() {
		if (right > left) {
			U u = list[right--];
			if (u == null) {
				return getRight();
			} else {
				return u;
			}
		} else {
			return null;
		}
	}
	public boolean hasNext(){
		return (left<right);
	}
}
