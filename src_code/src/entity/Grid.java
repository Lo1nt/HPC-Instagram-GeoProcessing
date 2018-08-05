package entity;

public class Grid implements Comparable<Grid> {
	
	
	public Grid() {
		
	}
	
	public Grid(String id, int count) {
		this.id = id;
		this.count = count;
	}
	
	//A1, A2, A3, A4; B1, B2, B3, B4; C1, C2, C3, C4, C5; D3, D4, D5;
	private String id;
	
	//count the num of Instagran posts
	private int count;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int compareTo(Grid arg0) {
		if (this.count > arg0.count) {
			return -1;
		}
		else if (this.count < arg0.count) {
			return 1;
		}
		return 0;
	}
	
	
}
