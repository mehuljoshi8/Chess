
//Creator: Mehul Joshi
import java.util.*;

public class Queen extends Piece {
	private final int[][] offSetFactors = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 }, { -1, 1 }, { 1, -1 }, { -1, -1 },
			{ 1, 1 } };

	public Queen(int color) {
		this.points = 9;
		this.color = color;
		if (color == 0) {
			this.name = "Q";
		} else
			this.name = "q";
	}

	public String toString() {
		return name;
	}

	public boolean isLegalMove(int row, int col, ChessBoard c) {
		return isLegalRowMove(row, col, c) || isLegalColMove(row, col, c) || isLegalDiagonalMove(row, col, c);
	}

	private boolean isLegalRowMove(int row, int col, ChessBoard c) {
		if (this.row == row) {
			if (this.col < col) {
				for (int i = this.col + 1; i < col; i++) {
					if (c.matrix[row][i].piece != null) {
						return false;
					}
				}
				return (c.matrix[row][col].piece == null || !(c.matrix[row][col].piece.color == color));
			} else if (this.col > col) {
				for (int i = this.col - 1; i > col; i--) {
					if (c.matrix[row][i].piece != null) {
						return false;
					}
				}
				return (c.matrix[row][col].piece == null || !(c.matrix[row][col].piece.color == color));
			}

			return false;
		}
		return false;
	}

	private boolean isLegalColMove(int row, int col, ChessBoard c) {
		if (this.col == col) {
			if (this.row > row) {
				for (int i = this.row - 1; i > row; i--) {
					if (c.matrix[i][col].piece != null) {
						return false;
					}
				}
				return (c.matrix[row][col].piece == null || !(c.matrix[row][col].piece.color == color));
			} else if (this.row < row) {
				for (int i = this.row + 1; i < row; i++) {
					if (c.matrix[i][col].piece != null) {
						return false;
					}
				}
				return (c.matrix[row][col].piece == null || !(c.matrix[row][col].piece.color == color));
			} else {
				return false;
			}
		}
		return false;
	}

	private boolean isLegalDiagonalMove(int row, int col, ChessBoard c) {
		return Math.abs(this.row - row) == Math.abs(this.col - col)
				&& c.matrix[this.row][this.col].color == c.matrix[row][col].color && getDiagonalFactors(row, col, c);
	}

	private boolean getDiagonalFactors(int row, int col, ChessBoard c) {
		// + upwards
		if (this.row > row && this.col < col) {
			return isDiagonalPathEmpty(row, col, c, -1, 1, this.row - 1, this.col + 1);
		}
		// + downwards
		else if (this.row < row && this.col > col) {
			return isDiagonalPathEmpty(row, col, c, 1, -1, this.row + 1, this.col - 1);
		}
		// -upwards
		else if (this.row > row && this.col > col) {
			return isDiagonalPathEmpty(row, col, c, -1, -1, this.row - 1, this.col - 1);
		}
		// - downwards
		else if (this.row < row && this.col < col) {
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
			if (checkPath(B, offSetFactors[i][0] + row, col + offSetFactors[i][1], offSetFactors[i][0],
					offSetFactors[i][1], other)) {
				return true;
			}
		}

		return false;
	}

	public ArrayList<String> getMoves(ChessBoard B, ArrayList<Piece> other) {
		//System.out.println("The queen on: " + (char) (this.col + 97) + (8 - this.row) + " can move to ");
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
