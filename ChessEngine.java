
// Creator: Mehul Joshi
// This is the only class that interacts with the user
import java.util.*;
import java.io.*;

public class ChessEngine {

	private static final Scanner in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
	public int mode;
	// 0 = h v. h
	// 1 = h v. c
	// 2 = c v. h

	public int flipBoard;
	// At the start prompt will appear and the user will be able to
	// choose to flip the board after everymove

	// but after that the flip board command only flips the board that one time
	// and displays the board the regular way afterwards

	// 0 = flip
	// 1 = no flip

	public static void main(String[] args) {
		ChessEngine engine = new ChessEngine();
		engine.runInputLoop();
	}

	private String processCommand(String input, ChessBoard B, Object[] o) {
		ChessPlayer user = (ChessPlayer) o[1], other = (ChessPlayer) o[2];
		return (user.processCommand(input, B, other));

	}

	private ChessBoard assignMode(String mode) {
		String whiteName, blackName, flipBoard;
		if (mode.equalsIgnoreCase("human vs human")) {
			this.mode = 0;
			System.out.print("white's player name: ");
			whiteName = in.nextLine().strip();
			System.out.print("black's player name: ");
			blackName = in.nextLine().strip();

			System.out.print("Would you like to flip the board after every move?(y|n): ");
			flipBoard = in.nextLine();
			if (flipBoard.equals("y")) {
				this.flipBoard = 1;
			} else {
				this.flipBoard = 0;
			}
			return new ChessBoard(new ChessPlayer(whiteName, 0), new ChessPlayer(blackName, 1));
		} else if (mode.equalsIgnoreCase("Computer vs Human")) {
			this.mode = 1;
			System.out.print("black's player name: ");
			blackName = in.nextLine().strip();
			ChessPlayer black = new ChessPlayer(blackName, 1);

			return new ChessBoard(new Dofleini(0, 1, black), black);
		} else if (mode.equalsIgnoreCase("Human vs Computer")) {
			this.mode = 2;

		}

		return null;

	}

	private void runInputLoop() {
		String input = "";
		System.out.print("Welcome to the most elegant chess platform in the world!\nReady to start a new game?(y|n): ");
		input = in.nextLine();

		if (input.equals("y")) {
			// implement the playing chess for different modes.
			ChessBoard board = promptForMode();
			System.out.println("Starting new game for: " + board.white + " vs " + board.black);
			if (this.mode == 0) {
				runHumanVHuman(board);
			} else if (mode == 1) {
				runComputerVHuman(board, (Dofleini) board.white);
			} else if (mode == 2) {
				runHumanVComputer(board);
			}

		} else {
			System.out.print("Would you like to open a game from a file?(y|n): ");
			input = in.nextLine();
			if (input.equals("y")) {
				System.out.print("Please type a file name: ");
				String filename = in.nextLine();
				// The implementation of the load game functionality
				// comes after you are able to save the game
			}

		}
	}

	private void runHumanVHuman(ChessBoard b) {
		String input;
		while (true) {
			if (b.getMoveCount() % 2 == 0) {
				System.out.print(displayTheBoard(b) + "\nIt is " + b.white + "'s turn\nEnter: ");
			} else {
				System.out.print(displayTheBoard(b) + "\nIt is " + b.black + "'s turn\nEnter: ");
			}

			input = in.nextLine().strip();
			System.out.println(processCommand(input, b, b.getTypes()));
			System.out.println(b.white.getMoves(b, b.black));
		}
	}

	private void runComputerVHuman(ChessBoard b, Dofleini d) {
		String input;
		while (true) {
			System.out.println(displayTheBoard(b));
			if (b.getMoveCount() % 2 == 0) {
				System.out.print("It is " + b.white + "'s turn\nEnter: ");
				input = "";
			} else {
				System.out.print("It is " + b.black + "'s turn\nEnter: ");
				input = in.nextLine().strip();
				System.out.println(input);
			}

			System.out.println(processCommand(input, b, b.getTypes()));
		}
	}

	private void runHumanVComputer(ChessBoard b) {
		
	}

	private String displayTheBoard(ChessBoard b) {
		// mode 0 is Human vs human
		if (mode == 0) {
			if (flipBoard == 1) {
				if (b.getMoveCount() % 2 == 0) {
					return b.toString();
				} else {
					return b.flipBoard();
				}
			}

		}
		// mode 1 is computer vs human
		else if (mode == 1) {
			return b.flipBoard();
		}
		// mode 2 is human vs computer
		else if (mode == 2) {
			return b.toString();
		}

		return b.toString();
	}

	private ChessBoard promptForMode() {
		System.out.print("What mode are you interested in playing in?\nEnter: ");
		ChessBoard c = null;
		for (String input = in.nextLine(); c == null; input = in.nextLine()) {
			c = assignMode(input);
			if (c == null) {
				System.out.println(listAllAvailableModes());
				System.out.print("Enter: ");
			}
		}

		return c;
	}

	private String listAllAvailableModes() {
		return "That is not a legal mode\nLegal Modes include:\n -human vs human\nnew modes are soon to come";
	}
}
