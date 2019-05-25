
//Creator: Mehul Joshi
import java.util.*;

public abstract class Piece {
	public String name;
	public int points, row, col, counter, color;

	public abstract boolean isLegalMove(int row, int col, ChessBoard c);

	public abstract String toString();

	public abstract boolean isCheckingKing(ChessBoard B);

	public boolean isOtherKingOnPathWay(ChessBoard B, int rowFactor, int colFactor, int row, int col) {

		if (B.isOnBoard(row, col)) {
			if (B.matrix[row][col].piece != null && B.matrix[row][col].piece instanceof King
					&& B.matrix[row][col].piece.color != this.color) {
				return true;
			} else if (B.matrix[row][col].piece != null) {
				return false;
			} else {
				return isOtherKingOnPathWay(B, rowFactor, colFactor, row + rowFactor, col + colFactor);
			}
		}
		return false;
	}

	public boolean simulate(int row, int col, ChessBoard B, ArrayList<Piece> other) {
		if (!B.isOnBoard(row, col) || !isLegalMove(row, col, B)) {
			return false;
		} else {
			// Do a temporary swapping of pieces
			boolean canMove = false;
			Piece temp = B.matrix[row][col].piece;
			B.matrix[row][col].piece = B.matrix[this.row][this.col].piece;
			B.matrix[this.row][this.col].piece = null;
			if (temp != null) {
				other.remove(temp);
			}
			if (!isInCheck(other, B)) {
				canMove = true;
			}
			if (temp != null)
				other.add(temp);
			B.matrix[this.row][this.col].piece = B.matrix[row][col].piece;
			B.matrix[row][col].piece = temp;
			return canMove;
			
		}
	}

	public boolean isInCheck(ArrayList<Piece> other, ChessBoard B) {
		for (Piece p : other) {
			if (p.isCheckingKing(B)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkPath(ChessBoard B, int row, int col, int rowFactor, int colFactor, ArrayList<Piece> other) {
		if (!B.isOnBoard(row, col) || !isLegalMove(row, col, B)) {
			return false;
		}

		if (simulate(row, col, B, other)) {
			return true;
		} else {
			return checkPath(B, row + rowFactor, col + colFactor, rowFactor, colFactor, other);
		}
	}
	
	public abstract boolean hasLegalMoves(ChessBoard B, ArrayList<Piece> other);

	public abstract ArrayList<String> getMoves(ChessBoard B, ArrayList<Piece> other);
	
	
	
	
}
