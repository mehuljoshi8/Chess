
//Creator: Mehul Joshi
import java.util.*;

public class Pawn extends Piece {

	public Pawn(int color) {
		this.points = 1;
		this.color = color;
		if (color == 0) {
			this.name = "P";
		} else
			this.name = "p";

	}

	public String toString() {
		return name;
	}

	public boolean isLegalMove(int row, int col, ChessBoard c) {
		return legalTraversal(row, col, c) || legalCapture(row, col, c) || legalEnpassant(row, col, c);

	}

	private boolean legalEnpassant(int row, int col, ChessBoard c) {

		if (Math.abs(this.row - row) == 1 && Math.abs(this.col - col) == 1) {
			if (this.color == 0 && this.row == 3) {
				// white's enpassant
				Piece neighbor = c.matrix[this.row][col].piece;
				if (neighbor != null && neighbor instanceof Pawn && neighbor.color != this.color
						&& neighbor.counter == 1) {
					ArrayList<String> gamelog = c.getGameLog();
					String expected = (char) (97 + neighbor.col) + "" + (8 - neighbor.row + 2) + " - "
							+ (char) (97 + neighbor.col) + "" + (8 - neighbor.row);

					if (expected.equals(gamelog.get(gamelog.size() - 1))) {
						// finally it is a legal enpassant
						return true;
					}
				}
			} else if (this.color == 1 && this.row == 4) {
				Piece neighbor = c.matrix[this.row][col].piece;
				if (neighbor != null && neighbor instanceof Pawn && neighbor.color != this.color
						&& neighbor.counter == 1) {

					ArrayList<String> gamelog = c.getGameLog();
					String expected = (char) (97 + neighbor.col) + "" + (8 - neighbor.row - 2) + " - "
							+ (char) (97 + neighbor.col) + "" + (8 - neighbor.row);

					if (expected.equals(gamelog.get(gamelog.size() - 1))) {

						return true;
					}
				}
			}
		}

		return false;

	}

	private boolean legalTraversal(int row, int col, ChessBoard c) {
		boolean sameCol = this.col == col;

		boolean isEmptyInFront = false;
		int rowGap = 0;
		if (color == 0) {
			rowGap = this.row - row;
			isEmptyInFront = c.matrix[this.row - 1][this.col].piece == null;
		} else {
			rowGap = row - this.row;
			isEmptyInFront = c.matrix[this.row + 1][this.col].piece == null;
		}

		return (isEmptyInFront && counter == 0 && sameCol && rowGap > 0 && rowGap <= 2
				&& c.matrix[row][col].piece == null)
				|| (counter > 0 && sameCol && rowGap == 1 && c.matrix[row][col].piece == null);
	}

	private boolean legalCapture(int row, int col, ChessBoard c) {
		int rowGap = 0;
		if (color == 0)
			rowGap = this.row - row;
		else
			rowGap = row - this.row;
		return (this.col + 1 == col || this.col - 1 == col) && rowGap == 1 && c.matrix[row][col].piece != null
				&& !(c.matrix[row][col].piece.color == color);
	}

	public boolean isCheckingKing(ChessBoard B) {
		int rowFactor = getRowFactor();
		return B.isOnBoard(this.row + rowFactor, this.col - 1)
				&& B.matrix[this.row + rowFactor][this.col - 1].piece instanceof King
				&& B.matrix[this.row + rowFactor][this.col - 1].piece.color != this.color
				|| B.isOnBoard(this.row + rowFactor, this.col + 1)
						&& B.matrix[this.row + rowFactor][this.col + 1].piece instanceof King
						&& B.matrix[this.row + rowFactor][this.col + 1].piece.color != this.color;

	}

	private int getRowFactor() {
		return (color == 1) ? 1 : -1;
	}

	public boolean hasLegalMoves(ChessBoard B, ArrayList<Piece> other) {

		int rowFactor = getRowFactor();

		return simulate(this.row + rowFactor, this.col, B, other)
				|| simulate(this.row + 2 * rowFactor, this.col, B, other)
				|| simulate(this.row + rowFactor, this.col - 1, B, other)
				|| simulate(this.row + rowFactor, this.col + 1, B, other);
	}

	public ArrayList<String> getMoves(ChessBoard B, ArrayList<Piece> other) {
		ArrayList<String> list = new ArrayList<>();
		String location = "" + (char) (this.col + 97) + (8 - this.row);
		int rf = getRowFactor();

		if (simulate(row + rf, col, B, other)) {
			list.add(location + getOperator(B, row + rf, col) + (char) (this.col + 97) + "" + (8 - (this.row + rf)));
		}
		if (simulate(row + 2 * rf, col, B, other)) {

			list.add(location + getOperator(B, row + 2 * rf, col) + (char) (this.col + 97) + ""
					+ (8 - (this.row + 2 * rf)));

		}
		if (simulate(row + rf, col - 1, B, other)) {

			list.add(location + getOperator(B, row + rf, col - 1) + (char) (this.col - 1 + 97) + ""
					+ (8 - (this.row + rf)));
		}

		if (simulate(row + rf, col + 1, B, other)) {

			list.add(location + getOperator(B, row + rf, col + 1) + (char) (this.col + 1 + 97) + ""
					+ (8 - (this.row + rf)));
		}

		return list;
	}

	private String getOperator(ChessBoard B, int row, int col) {
		if (B.matrix[row][col].piece != null) {
			return " x ";
		} else if (B.matrix[this.row][col].piece != null && B.matrix[this.row][col].piece instanceof Pawn
				&& B.matrix[this.row][col].piece.color != this.color) {
			return " x ";
		} else {
			return " - ";
		}
	}

}
