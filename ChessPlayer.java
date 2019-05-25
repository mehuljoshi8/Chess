
//Creator: Mehul Joshi
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class ChessPlayer {

	private String name;
	public int color;

	private static final Scanner in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
	public ArrayList<Piece> pieces = new ArrayList<Piece>(), graveyard = new ArrayList<Piece>();

	public ChessPlayer(String name, int color) {
		this.name = name;
		this.color = color;
		initializePieces();
	}

	private void initializePieces() {
		pieces.add(new Rook(color));
		pieces.add(new Knight(color));
		pieces.add(new Bishop(color));
		pieces.add(new Queen(color));
		pieces.add(new King(color));
		pieces.add(new Bishop(color));
		pieces.add(new Knight(color));
		pieces.add(new Rook(color));

		for (int i = 8; i < 16; i++) {
			pieces.add(new Pawn(color));
		}

	}

	public String toString() {
		return name;
	}

	/*
	 * User Specific commands include: help & resign The command for resigning is:
	 * resign
	 */
	private String handleUserCommands(String input, ChessPlayer other, ChessBoard B) {
		if (input.equalsIgnoreCase("help")) {
			return "you get no help";
		} else if (input.equalsIgnoreCase("resign")) {
			System.out.println(other + " wins by resignation");
			System.exit(0);
		} else if (input.equalsIgnoreCase("draw")) {
			System.out.println("Draw?");
			if (other.acceptDraw(ChessPlayer.in.nextLine())) {
				System.out.println("Draw by mutual acceptance %%");
				System.exit(0);
			}
		}
		return "false";
	}

	/*
	 * In the ChessPlayer's processCommand: It takes an input and delineates where
	 * to take it from there Legal inputs include: a2 - a4 O-O O-O-O help/ resign/
	 * a2 x a4 a7 x b8 = Q/R/N/B a7 - a8 = Q/R/N/B
	 */
	public String processCommand(String input, ChessBoard B, ChessPlayer other) {
		String s = "false";
		String[] tokens = input.split(" ");
		if (tokens.length == 1) {
			s = isLegalCastle(other, B, tokens[0]) ? "true" : handleUserCommands(input, other, B);

		} else if (tokens.length == 3 && input.matches("^[a-h][1-8]\\s(-|x)\\s[a-h][1-8]$")
				&& isLegalMove(tokens, other, B)) {

			B.pushIntoGameLog(input);
			s = "true";

		} else if (tokens.length == 5 && input.matches("^[a-h][1-8]\\s(-|x)\\s[a-h][1-8]\\s(=)\\s[QRNB]$")
				&& B.matrix[8 - Integer.parseInt(tokens[0].substring(1))][getCol(tokens[0])].piece instanceof Pawn
				&& isLegalMove(tokens, other, B) && pawnReachedOtherSide(
						(Pawn) B.matrix[8 - Integer.parseInt(tokens[2].substring(1))][getCol(tokens[2])].piece)) {

			pieces.remove(B.matrix[8 - Integer.parseInt(tokens[2].substring(1))][getCol(tokens[2])].piece);
			addPiece(tokens[4], 8 - Integer.parseInt(tokens[2].substring(1)), getCol(tokens[2]), B);

			B.pushIntoGameLog(input);
			s = "true";

		}

		if (other.isInCheckMate(this.pieces, B)) {
			System.out.println(B + "\nThe Winner is " + this.name + " by checkmate ##");
			System.exit(0);
		} else if (other.isInStaleMate(this.pieces, B)) {
			System.out.println(B + "\nDraw by stalemate %%");
			System.exit(0);

		} else if (drawByInsufficinetMaterial(other.pieces)) {
			System.out.println(B + "\nDraw by insufficient Material %%");
			System.exit(0);
		}
		return s;

	}

	public King getKing() {
		for (Piece p : pieces) {
			if (p instanceof King) {
				return (King) p;
			}
		}

		return null;
	}

	public Rook getKingSideRook() {
		for (Piece p : pieces) {
			if (p instanceof Rook && p.col == 7) {
				return (Rook) p;
			}
		}
		return null;
	}

	public Rook getQueenSideRook() {
		for (Piece p : pieces) {
			if (p instanceof Rook && p.col == 0) {
				return (Rook) p;
			}
		}
		return null;
	}

	public boolean isInCheck(ArrayList<Piece> other, ChessBoard B) {
		for (Piece p : other) {
			if (p.isCheckingKing(B)) {
				System.out.println("+");
				return true;
			}
		}
		return false;
	}

	private boolean isInCheckMate(ArrayList<Piece> other, ChessBoard B) {
		if (isInCheck(other, B)) {
			for (Piece p : pieces) {
				if (p.hasLegalMoves(B, other)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean isInStaleMate(ArrayList<Piece> other, ChessBoard B) {
		for (Piece p : pieces) {
			if (p.hasLegalMoves(B, other)) {
				return false;
			}
		}
		return true;
	}

	private boolean isLegalCastle(ChessPlayer other, ChessBoard B, String castleType) {
		System.out.println(castleType);

		King king = getKing();
		Rook rook = null;

		if (castleType.equalsIgnoreCase("O-O")) {
			rook = getKingSideRook();
			if (isLegalKingSideCastle(king, rook, other, king.col + 1, king.col + 2, B)
					&& B.matrix[rook.row][rook.col - 1].piece == null) {

				B.performKingSideCastle(king, rook);

				B.pushIntoGameLog(castleType);
				return true;
			}

		} else if (castleType.equalsIgnoreCase("O-O-O")) {
			rook = getQueenSideRook();

			if (isLegalQueenSideCastle(king, rook, other, king.col - 1, king.col - 2, B)
					&& B.matrix[rook.row][rook.col + 1].piece == null) {
				B.performQueenSideCastle(king, rook);
				B.pushIntoGameLog(castleType);
			}

		}
		return false;
	}

	private boolean isLegalKingSideCastle(King k, Rook r, ChessPlayer other, int startCol, int endCol, ChessBoard B) {

		if (!isInCheck(other.pieces, B) && r != null && k.counter == 0 && r.counter == 0) {
			ArrayList<String> list = getOtherMoves(other, B);
			for (int i = startCol; i <= endCol; i++) {

				if (B.matrix[k.row][i].piece != null) {

					return false;
				}

				if (!isLegalCastle(list, k.row, i)) {
					return false;
				}
			}

			return true;
		}
		return false;
	}

	private ArrayList<String> getOtherMoves(ChessPlayer other, ChessBoard b) {
		ArrayList<String> list = new ArrayList<String>();
		for (Piece p : other.pieces) {
			list.addAll(p.getMoves(b, pieces));
		}
		return list;
	}

	private boolean isLegalCastle(ArrayList<String> list, int row, int col) {
		String position = (char) (97 + col) + "" + (8 - row);
		for (String s : list) {
			if (s.split(" ")[2].equals(position)) {
				return false;
			}
		}

		return true;
	}

	private boolean isLegalMove(String[] tokens, ChessPlayer other, ChessBoard B) {
		boolean isValid = false;
		Piece j = null;
		Piece s = null;
		int row = 8 - Integer.parseInt(tokens[0].substring(1)), col = getCol(tokens[0]),
				termrow = 8 - Integer.parseInt(tokens[2].substring(1)), termcol = getCol(tokens[2]);

		if (B.isOnBoard(row, col) && B.isOnBoard(termrow, termcol)
				&& isLegalMove(termrow, termcol, B.matrix[row][col].piece, B)) {

			j = B.matrix[termrow][termcol].piece;
			if (tokens[1].equals("x") && j != null) {
				isValid = true;
				graveyard.add(B.matrix[termrow][termcol].piece);
				B.capturePiece(row, col, termrow, termcol, other.pieces);

			} else if (tokens[1].equals("-") && j == null) {
				isValid = true;
				B.movePiece(row, col, termrow, termcol);
			}
			//enpassant
			else if (tokens[1].equals("x") && B.matrix[row][col].piece instanceof Pawn) {
				isValid = true;
				s = B.matrix[row][termcol].piece;
				graveyard.add(s);
				other.pieces.remove(s);
				B.matrix[row][termcol].piece = null;
				B.movePiece(row, col, termrow, termcol);

			}

			if (isInCheck(other.pieces, B) && isValid) {
				isValid = false;
				B.nullifyMove(row, col, termrow, termcol, j);
				if (tokens[1].equals("x") && j != null) {
					graveyard.remove(j);
					other.pieces.add(j);

				} else if (s != null) {
					graveyard.remove(s);
					other.pieces.add(s);
				}
			}
		}
		return isValid;
	}

	private int getCol(String location) {
		return location.charAt(0) - 97;
	}

	private boolean isLegalMove(int termrow, int termcol, Piece p, ChessBoard B) {
		return p != null && p.color == this.color && p.isLegalMove(termrow, termcol, B);
	}

	private void addPiece(String newPiece, int row, int col, ChessBoard B) {
		Piece p = null;
		if (newPiece.equals("Q")) {
			p = new Queen(color);
		} else if (newPiece.equals("R")) {
			p = new Rook(color);
		} else if (newPiece.equals("N")) {
			p = new Knight(color);
		} else if (newPiece.equals("B")) {
			p = new Bishop(color);
		}

		p.row = row;
		p.col = col;

		B.matrix[row][col].piece = p;
		pieces.add(p);

	}

	private boolean pawnReachedOtherSide(Pawn p) {
		return color == 0 ? p.row == 0 : p.row == 7;
	}

	public boolean acceptDraw(String input) {
		return input.equalsIgnoreCase("yes");
	}

	private boolean drawByInsufficinetMaterial(ArrayList<Piece> other) {
		return other.size() == 1 && this.pieces.size() == 1;
	}

	public ArrayList<String> getMoves(ChessBoard b, ChessPlayer other) {
		ArrayList<String> moves = new ArrayList<String>();

		for (Piece p : this.pieces) {
			moves.addAll(p.getMoves(b, other.pieces));
		}

		King king = getKing();
		Rook rook = getKingSideRook();
		if (isLegalKingSideCastle(king, rook, other, king.col + 1, king.col + 2, b)
				&& b.matrix[rook.row][rook.col - 1].piece == null) {
			moves.add("O-O");
		}

		rook = getQueenSideRook();

		if (isLegalQueenSideCastle(king, rook, other, king.col - 1, king.col - 2, b)
				&& b.matrix[rook.row][rook.col + 1].piece == null) {
			moves.add("O-O-O");
		}

		return moves;
	}

	private boolean isLegalQueenSideCastle(King k, Rook r, ChessPlayer other, int sc, int ec, ChessBoard b) {
		if (!isInCheck(other.pieces, b) && r != null && k.counter == 0 && r.counter == 0) {
			ArrayList<String> list = getOtherMoves(other, b);
			for (int i = sc; i >= ec; i--) {
				if (b.matrix[k.row][i].piece != null) {
					
					return false;
				}

				if (!isLegalCastle(list, k.row, i)) {
					return false;
				}
			}
			return true;

		}

		return false;
	}

}
