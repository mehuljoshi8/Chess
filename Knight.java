
//Creator: Mehul Joshi
import java.util.*;

public class Knight extends Piece {
	public final int[][] offSetFactors = { { 2, 1 }, { -2, -1 }, { -2, 1 }, { 2, -1 }, { 1, 2 }, { -1, -2 }, { 1, -2 },
			{ -1, 2 } };

	public Knight(int color) {

		this.points = 3;
		this.color = color;

		if (color == 0) {
			this.name = "N";
		} else
			this.name = "n";
	}

	public String toString() {
		return name;
	}

	public boolean isLegalMove(int row, int col, ChessBoard c) {
		return (isLegalRowMove(row, col) || isLegalColMove(row, col))
				&& c.matrix[this.row][this.col].color != c.matrix[row][col].color
				&& (c.matrix[row][col].piece == null || c.matrix[row][col].piece.color != this.color);
	}

	private boolean isLegalRowMove(int row, int col) {
		return Math.abs(this.row - row) == 1 && Math.abs(this.col - col) == 2;
	}

	private boolean isLegalColMove(int row, int col) {
		return Math.abs(this.col - col) == 1 && Math.abs(this.row - row) == 2;
	}

	public boolean isCheckingKing(ChessBoard B) {
		for (int i = 0; i < offSetFactors.length; i++) {
			if (isKingOn(row + offSetFactors[i][0], col + offSetFactors[i][1], B)) {
				return true;
			}
		}

		return false;
	}

	private boolean isKingOn(int row, int col, ChessBoard B) {
		return B.isOnBoard(row, col) && B.matrix[row][col].piece != null && B.matrix[row][col].piece instanceof King
				&& B.matrix[row][col].piece.color != color;
	}

	public boolean hasLegalMoves(ChessBoard B, ArrayList<Piece> other) {
		for (int i = 0; i < offSetFactors.length; i++) {
			if (simulate(row + offSetFactors[i][0], col + offSetFactors[i][1], B, other)) {
				return true;
			}
		}

		return false;
	}

	public ArrayList<String> getMoves(ChessBoard B, ArrayList<Piece> other) {

		String location = "" + (char) (this.col + 97) + (8 - this.row);

		ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < offSetFactors.length; i++) {
			if (simulate(row + offSetFactors[i][0], col + offSetFactors[i][1], B, other)) {
				list.add(location + getOperator(B, row + offSetFactors[i][0], col + offSetFactors[i][1])
						+ (char) (this.col + offSetFactors[i][1] + 97) + "" + (8 - (this.row + offSetFactors[i][0])));

			}
		}
		return list;
	}

	private String getOperator(ChessBoard B, int row, int col) {
		if (B.matrix[row][col].piece != null) {
			return " x ";
		} else {
			return " - ";
		}
	}
}
