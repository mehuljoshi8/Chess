
//Creator: Mehul Joshi
import java.util.*;

public class Bishop extends Piece {
	public final int[][] offSetFactors = { { -1, 1 }, { 1, 1 }, { -1, -1 }, { 1, -1 } };

	public Bishop(int color) {
		this.points = 3;
		this.color = color;
		if (color == 0) {
			this.name = "B";
		} else
			this.name = "b";
	}

	public String toString() {
		return name;
	}

	public boolean isLegalMove(int row, int col, ChessBoard c) {
		return Math.abs(this.row - row) == Math.abs(this.col - col) && getDiagonalFactors(row, col, c);
	}

	private boolean getDiagonalFactors(int row, int col, ChessBoard c) {
		if (this.row > row && this.col < col) {
			return isDiagonalPathEmpty(row, col, c, -1, 1, this.row - 1, this.col + 1);
		} else if (this.row < row && this.col > col) {
			return isDiagonalPathEmpty(row, col, c, 1, -1, this.row + 1, this.col - 1);
		} else if (this.row > row && this.col > col) {
			return isDiagonalPathEmpty(row, col, c, -1, -1, this.row - 1, this.col - 1);
		} else if (this.row < row && this.col < col) {
			return isDiagonalPathEmpty(row, col, c, 1, 1, this.row + 1, this.col + 1);
		}

		return false;

	}

	private boolean isDiagonalPathEmpty(int targetRow, int targetCol, ChessBoard B, int rowFactor, int colFactor,
			int row, int col) {
		if (row == targetRow && col == targetCol) {
			return (B.matrix[targetRow][targetCol].piece != null
					&& B.matrix[targetRow][targetCol].piece.color != this.color)
					|| B.matrix[targetRow][targetCol].piece == null;
		} else if (B.matrix[row][col].piece != null) {
			return false;
		} else {
			return isDiagonalPathEmpty(targetRow, targetCol, B, rowFactor, colFactor, row + rowFactor, col + colFactor);
		}

	}

	public boolean isCheckingKing(ChessBoard B) {
		for (int i = 0; i < offSetFactors.length; i++) {
			if (isOtherKingOnPathWay(B, offSetFactors[i][0], offSetFactors[i][1], row + offSetFactors[i][0],
					col + offSetFactors[i][1])) {
				return true;
			}
		}
		return false;
	}

	public boolean hasLegalMoves(ChessBoard B, ArrayList<Piece> other) {
		for (int i = 0; i < offSetFactors.length; i++) {
			if (checkPath(B, row + offSetFactors[i][0], col + offSetFactors[i][1], offSetFactors[i][0],
					offSetFactors[i][1], other)) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<String> getMoves(ChessBoard B, ArrayList<Piece> other) {
		//System.out.println("The bishop on: " + (char) (this.col + 97) + (8 - this.row) + " can move to ");
		ArrayList<String> list = new ArrayList<String>();
		String location = "" + (char) (this.col + 97) + (8 - this.row);

		for (int[] arr : offSetFactors) {
			list.addAll(findMoves(B, other, row + arr[0], col + arr[1], arr[0], arr[1], location));

		}

		return list;
	}

	private ArrayList<String> findMoves(ChessBoard B, ArrayList<Piece> other, int row, int col, int rf, int cf,
			String location) {
		String operator = "";
		ArrayList<String> list = new ArrayList<String>();
		while (B.isOnBoard(row, col) && isLegalMove(row, col, B)) {
			if (simulate(row, col, B, other)) {
				if(B.matrix[row][col].piece != null) {
					operator = " x ";
				} else {
					operator = " - ";
				}
				
				list.add(location + operator + (char) (col + 97) + "" + (8 - (row)));
			
			}
			row += rf;
			col += cf;
		}
		return list;
	}
	
	

}
