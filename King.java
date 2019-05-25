
// Creator: Mehul Joshi
import java.util.*;

public class King extends Piece {
	public final int[][] offSetFactors = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, -1 }, { 1, 1 }, { -1, -1 },
			{ -1, 1 } };

	public King(int color) {
		this.points = 100000000;
		this.color = color;
		if (color == 0) {
			this.name = "K";
		} else
			this.name = "k";
	}

	public String toString() {
		return name;
	}

	public boolean isLegalMove(int row, int col, ChessBoard c) {
		return ((c.matrix[row][col].piece == null || c.matrix[row][col].piece.color != color)
				&& (isLegalRowMove(row, col) || isLegalColMove(row, col) || isLegalDiagonalMove(row, col)));
	}

	private boolean isLegalRowMove(int row, int col) {
		return this.row == row && Math.abs(this.col - col) == 1;
	}

	private boolean isLegalColMove(int row, int col) {
		return this.col == col && Math.abs(this.row - row) == 1;
	}

	private boolean isLegalDiagonalMove(int row, int col) {
		return Math.abs(this.col - col) == 1 && Math.abs(this.row - row) == 1;
	}

	public boolean isCheckingKing(ChessBoard B) {
		for (int i = 0; i < offSetFactors.length; i++) {
			if (isCheckingKing(row + offSetFactors[i][0], col + offSetFactors[i][1], B)) {
				return true;
			}
		}

		return false;
	}

	private boolean isCheckingKing(int row, int col, ChessBoard B) {
		return B.isOnBoard(row, col) && B.matrix[row][col].piece != null && B.matrix[row][col].piece instanceof King
				&& B.matrix[row][col].piece.color != this.color;
	}

	public boolean hasLegalMoves(ChessBoard B, ArrayList<Piece> other) {
		for (int i = 0; i < offSetFactors.length; i++) {
			if (simulate(row + offSetFactors[i][0], this.col + offSetFactors[i][1], B, other)) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<String> getMoves(ChessBoard B, ArrayList<Piece> other) {
		
		//System.out.println("The king on " + "" + (char) (this.col + 97) + (8 - this.row) + " can move to");
		ArrayList<String> list = new ArrayList<String>();

		String result = "" + (char) (this.col + 97) + (8 - this.row);
		
		String operator = " - ";
		String finalLocation = "";
		for (int i = 0; i < offSetFactors.length; i++) {
			if (simulate(this.row + offSetFactors[i][0], this.col + offSetFactors[i][1], B, other)) {
				if (B.matrix[row + offSetFactors[i][0]][col + offSetFactors[i][1]].piece != null) {
					operator = " x ";
				}
				finalLocation = (char) (this.col + offSetFactors[i][1] + 97) + ""
						+ (8 - (this.row + offSetFactors[i][0]));
				
				list.add(result + operator + finalLocation);
				
			}
		}

		return list;
	}


}
