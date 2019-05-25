//Creator: Mehul Joshi

public class Cell {
	public int color; 
	//The colors that a cell can be are totally dependent on the color scale and style the user chooses
	public Piece piece;

	public Cell(int color) {
		this(color, null);
	}
	
	public Cell(int color, Piece piece) {
		this.piece = piece;
		this.color = color;
	}
	
	public String toString() {
		return piece != null? piece.toString() : "";
	}
	
}
