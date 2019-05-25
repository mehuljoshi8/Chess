
/* Creator: Mehul Joshi
 * The purpose of this class is to create the chess board of the program.
 * The Board contains all the pieces and a board
 * of alternating black and white squares
 */
import java.util.*;

public class ChessBoard {

	public Cell[][] matrix;
	public ChessPlayer white, black;
	public int moveCount = 0;
	private ArrayList<String> gamelog;

	public ChessBoard(ChessPlayer player1, ChessPlayer player2) {
		white = player1;
		black = player2;
		gamelog = new ArrayList<String>();
		int squareColor = 0;
		matrix = new Cell[8][8];
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				if ((row + col) % 2 == 0) {
					squareColor = 0;
				} else {
					squareColor = 1;
				}

				initializeSquare(row, col, squareColor);
			}
		}
	}

	private void initializeSquare(int row, int col, int squareColor) {
		if (row > 1 && row < 6) {
			matrix[row][col] = new Cell(squareColor);
		} else {
			ChessPlayer temp = null;
			Piece piece = null;
			if (row < 2)
				temp = black;
			else
				temp = white;
			if (row == 0 || row == 7) {
				piece = temp.pieces.get(col);
			} else {
				piece = temp.pieces.get(col + 8);
			}
			piece.counter = 0;
			matrix[row][col] = new Cell(squareColor, piece);
			matrix[row][col].piece.row = row;
			matrix[row][col].piece.col = col;

		}

	}

	public String toString() {

		String result = lineDivider();
		for (int row = 0; row < matrix.length; row++) {
			result += (8 - row) + " |";
			for (int col = 0; col < matrix.length; col++) {
				if (matrix[row][col].piece != null)
					result += String.format("%2s |", matrix[row][col].piece.toString());
				else
					result += "   |";
			}

			result += "\n" + lineDivider();
		}

		result += " ";

		for (int i = 0; i < 8; i++) {
			result += "   " + (char) (97 + i);
		}

		return result;

	}

	public String lineDivider() {
		String result = "  ";
		for (int i = 0; i < 33; i++) {
			result += "_";
		}
		return result + "\n";
	}

	public String flipBoard() {
		String result = " ";
		for (int i = 0; i < 8; i++) {
			result += "   " + (char) (104 - i);
		}

		result += "\n" + lineDivider();
		for (int row = matrix.length - 1; row >= 0; row--) {
			result += (8 - row) + " |";
			for (int col = matrix.length - 1; col >= 0; col--) {
				if (matrix[row][col].piece != null) {
					result += String.format("%2s |", matrix[row][col].piece.toString());
				} else {
					result += "   |";
				}
			}

			result += "\n" + lineDivider();
		}

		return result;
	}

	public void capturePiece(int row, int col, int termrow, int termcol, ArrayList<Piece> other) {
		other.remove(matrix[termrow][termcol].piece);
		movePiece(row, col, termrow, termcol);
	}

	public void nullifyMove(int row, int col, int termrow, int termcol, Piece originalPiece) {
		matrix[row][col].piece = matrix[termrow][termcol].piece;
		matrix[termrow][termcol].piece = originalPiece;
		matrix[row][col].piece.row = row;
		matrix[row][col].piece.col = col;
		matrix[row][col].piece.counter--;
		moveCount--;
	}

	public void movePiece(int row, int col, int termrow, int termcol) {
		moveCount++;
		matrix[row][col].piece.counter++;
		matrix[termrow][termcol].piece = matrix[row][col].piece;
		matrix[row][col].piece = null;
		matrix[termrow][termcol].piece.row = termrow;
		matrix[termrow][termcol].piece.col = termcol;
	}

	public boolean isOnBoard(int row, int col) {
		return row > -1 && row < 8 && col > -1 && col < 8;
	}

	public Object[] getTypes() {
		Object[] o = new Object[3];
		if (moveCount % 2 == 0) {
			o[0] = 0;
			o[1] = white;
			o[2] = black;
		} else {
			o[1] = black;
			o[0] = 1;
			o[2] = white;
		}
		return o;
	}

	public void performKingSideCastle(King king, Rook rook) {
		movePiece(king.row, king.col, king.row, king.col + 2);
		movePiece(rook.row, rook.col, rook.row, rook.col - 2);
		moveCount--;
	}

	public void performQueenSideCastle(King king, Rook rook) {
		movePiece(king.row, king.col, king.row, king.col - 2);
		movePiece(rook.row, rook.col, rook.row, rook.col + 3);
		moveCount--;
	}

	public int getMoveCount() {
		return moveCount;
	}

	public ArrayList<String> getGameLog() {
		return gamelog;
	}

	public void pushIntoGameLog(String move) {
		gamelog.add(move);
	}

	public Cell[][] copy() {
		Cell[][] copy = new Cell[8][8];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				copy[i][j] = new Cell(matrix[i][j].color, matrix[i][j].piece);
			}
		}

		return copy;
	}

	// returns [r,c,tr,tc]
	public static int[] parseMove(String[] tokens) {
		// for a regular move !enpassant
		if (tokens.length == 3) {
			int[] arr = new int[4];
			arr[0] = 8 - Integer.parseInt(tokens[0].substring(1));
			arr[1] = tokens[0].charAt(0) - 97;
			arr[2] = 8 - Integer.parseInt(tokens[2].substring(1));
			arr[3] = tokens[2].charAt(0) - 97;
			return arr;
		}
		return null;

	}

}
