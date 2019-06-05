import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Dofleini extends ChessPlayer {
	private ChessBoard mind;
	private MoveNode overallRoot;
	private MoveNode currentNode;
	private ChessPlayer opponent;
	private int depth;
	private static final double RESIZE_FACTOR = 0.75;

	public Dofleini(int color, int depth, ChessPlayer opponent) {
		super("Dofleini", color);
		if (color == 0)
			mind = new ChessBoard(this, opponent);
		else
			mind = new ChessBoard(opponent, this);
		this.depth = depth;
		this.opponent = opponent;

		constructTree();
	}

	private void constructTree() {
		overallRoot = constructTree(new MoveNode("s", null), 0);
		System.out.println(overallRoot.code);

	}

	private MoveNode constructTree(MoveNode root, int depth) {
		System.out.println("The move before constructing on node:\n" + mind);
		System.out.println("Looking to expand on: " + root.code);
		int[] nums = ChessBoard.parseMove(root.code.split(" "));

		if (depth == this.depth) {
			System.out.println("Reached the end........");
			return root;
		}
		setParameters(root, nums);
		Piece redacted = getRedacted(root, nums);
		System.out.println("Piece killed: " + redacted);
		System.out.println("Depth: " + depth);

		root.nodes = new ArrayList<MoveNode>();

		simulate(root, depth);

		System.out.println(mind);
		System.out.println("For the root: " + root.code + " the nodes are:\n" + root.nodes);

//		//nullify the roots move;
//		if (!root.code.equals("s")) {
//			// nullify the root's code before proceeding
//			mind.nullifyMove(nums[0], nums[1], nums[2], nums[3], redacted);
//
//		}
		if (depth != 0)
			nullifyMove(root, depth, redacted, nums);

		return root;
	}

	private Piece getRedacted(MoveNode root, int[] nums) {
		if (nums != null && root.code.contains("x")) {
			// Get the peice killed by enpassant
			if (root.isEnpassantMove) {
				return mind.matrix[nums[0]][nums[3]].piece;
			} else {
				return mind.matrix[nums[2]][nums[3]].piece;
			}
		}

		return null;
	}

	private void setParameters(MoveNode root, int[] nums) {
		if (nums != null && root.code.contains("x")) {
			if (mind.matrix[nums[2]][nums[3]].piece == null && mind.matrix[nums[0]][nums[1]].piece instanceof Pawn) {
				root.isEnpassantMove = true;
			}
		} else if (root.code.equals("O-O")) {
			root.isKCastleMove = true;
		} else if (root.code.equals("O-O-O")) {
			root.isQCastleMove = true;
		}
	}

	private void simulate(MoveNode root, int depth) {
		if (depth % 2 == 0) {
			if (depth != 0) {
				mind.black.processCommand(root.code, mind, mind.white);
			}
			// the nodes are all white player nodes
			for (String s : mind.white.getMoves(mind, mind.black)) {
				MoveNode n = constructTree(new MoveNode(s, root), depth + 1);
				root.nodes.add(n);
			}
		} else {
			// the nodes are all black player nodes
			mind.white.processCommand(root.code, mind, mind.black);
			for (String s : mind.black.getMoves(mind, mind.white)) {
				MoveNode n = constructTree(new MoveNode(s, root), depth + 1);
				root.nodes.add(n);
			}
		}
	}

	private void nullifyMove(MoveNode root, int depth, Piece redacted, int[] nums) {
		if (depth % 2 == 0) {
			// then you have to nullify a white move
			if (nums != null) {
				if (root.isEnpassantMove) {
					// then it is enpassant
					redactEnpassant(nums, redacted);
					mind.white.pieces.remove(redacted);
					mind.black.pieces.add(redacted);
				} else {
					mind.nullifyMove(nums[0], nums[1], nums[2], nums[3], redacted);
					mind.white.pieces.remove(redacted);
					mind.black.pieces.add(redacted);
				}
			} else if (root.isKCastleMove) {
				redactKingSideCastle(depth % 2);

			} else if (root.isQCastleMove) {
				redactQueenSideCastle(depth % 2);
			}
		} else {
			// nullify a black move
			if (nums != null) {
				if (root.isEnpassantMove) {
					// then it is enpassant
					redactEnpassant(nums, redacted);
					mind.black.pieces.remove(redacted);
					mind.white.pieces.add(redacted);
				} else {
					mind.nullifyMove(nums[0], nums[1], nums[2], nums[3], redacted);
					mind.black.pieces.remove(redacted);
					mind.white.pieces.add(redacted);
				}
			} else if (root.isKCastleMove) {
				redactKingSideCastle(depth % 2);

			} else if (root.isQCastleMove) {
				redactQueenSideCastle(depth % 2);
			}

		}
	}

	// For the regular moves the framework to revert moves is already implemented in
	// the chessboard class.
	// So I just need to implement for enpassants, and castling situation's
	// revertion.
	private void redactEnpassant(int[] arr, Piece redacted) {
		mind.matrix[arr[0]][arr[1]].piece = mind.matrix[arr[2]][arr[3]].piece;
		mind.matrix[arr[2]][arr[3]].piece = null;
		mind.matrix[arr[0]][arr[3]].piece = redacted;
		mind.matrix[arr[0]][arr[1]].piece.row = arr[0];
		mind.matrix[arr[0]][arr[1]].piece.col = arr[1];
		mind.matrix[arr[0]][arr[1]].piece.counter--;
		mind.moveCount--;
	}

	private void redactKingSideCastle(int color) {
		King k;
		Rook r;
		int row;
		if (color == 0) {
			// get the white pieces
			row = 7;
			k = mind.white.getKing();
			r = mind.white.getKingSideRook();
		} else {
			row = 0;
			k = mind.black.getKing();
			r = mind.black.getKingSideRook();
		}
		mind.matrix[row][4].piece = k;
		mind.matrix[k.row][k.col].piece = null;
		k.row = row;
		k.col = 4;
		mind.matrix[row][7].piece = r;
		mind.matrix[r.row][r.col].piece = null;
		r.row = row;
		r.col = 7;
	}

	private void redactQueenSideCastle(int color) {
		King k;
		Rook r;
		int row;
		if (color == 0) {
			row = 7;
			k = mind.white.getKing();
			r = mind.white.getQueenSideRook();
		} else {
			row = 0;
			k = mind.black.getKing();
			r = mind.black.getQueenSideRook();
		}
		mind.matrix[row][4].piece = k;
		mind.matrix[k.row][k.col] = null;
		k.row = row;
		k.col = 4;
		mind.matrix[row][0].piece = r;
		mind.matrix[r.row][r.col].piece = null;
		r.row = row;
		r.col = 0;
	}

//	private MoveNode constructTree(MoveNode root, int depth) {
//		System.out.println("Looking to expand on: " + root.code);
//		System.out.println("Depth: " + depth);
//		int nums[] = ChessBoard.parseMove(root.code.split(" "));
//		setParameters(root, nums);
//		if (depth == this.depth) {
//			System.out.println("Reached the end...");
//			return null;
//		}
//		//System.out.println("Looking to expand on: " + root.code);
//
//		Piece redacted = getRedacted(root, nums);
//		System.out.println("Piece killed: " + (redacted != null ? redacted : ""));
//		System.out.println("Depth: " + depth);
//
//		if (depth % 2 == 0) {
//			if (depth != 0)
//				mind.black.processCommand(root.code, mind, mind.white);
//			// the nodes are all white player nodes
//			for (String s : mind.white.getMoves(mind, mind.black)) {
//				MoveNode n = constructTree(new MoveNode(s, root), depth + 1);
//				root.nodes.add(n);
//			}
//		} else {
//			// the nodes are all black player nodes
//			mind.white.processCommand(root.code, mind, mind.black);
//			for (String s : mind.black.getMoves(mind, mind.white)) {
//				MoveNode n = new MoveNode(s, root);
//				constructTree(new MoveNode(s, root), depth + 1);
//				root.nodes.add(n);
//			}
//		}
//		if (depth != 0)
//			nullifyMove(root, depth % 2, redacted);
//
//		System.out.println("For the root: " + root.code + "\nThe nodes are as follows: " + root.nodes);
//		return root;
//	}
//
//	// enpassant parameter is already set
//	// set the isKCastle
//	// set the isQCastle
//	private void setParameters(MoveNode root, int[] nums) {
//		if (nums != null && root.code.contains("x")) {
//			if (mind.matrix[nums[2]][nums[3]].piece == null && mind.matrix[nums[0]][nums[1]].piece instanceof Pawn) {
//				root.isEnpassantMove = true;
//			}
//		} else if (root.code.equals("O-O")) {
//			root.isKCastleMove = true;
//		} else if (root.code.equals("O-O-O")) {
//			root.isQCastleMove = true;
//		}
//	}
//
//	private Piece getRedacted(MoveNode root, int[] nums) {
//		if (nums != null && root.code.contains("x")) {
//			// Get the peice killed by enpassant
//			if (root.isEnpassantMove) {
//				return mind.matrix[nums[0]][nums[3]].piece;
//			} else {
//				return mind.matrix[nums[2]][nums[3]].piece;
//			}
//		}
//
//		return null;
//	}
//
//	private void simulate(MoveNode root, int depth) {
//		if (depth % 2 == 0) {
//			if (depth != 0)
//				mind.black.processCommand(root.code, mind, mind.white);
//			// the nodes are all white player nodes
//			for (String s : mind.white.getMoves(mind, mind.black)) {
//				MoveNode n = constructTree(new MoveNode(s, root), depth + 1);
//				root.nodes.add(n);
//			}
//		} else {
//			// the nodes are all black player nodes
//			mind.white.processCommand(root.code, mind, mind.black);
//			for (String s : mind.black.getMoves(mind, mind.white)) {
//				MoveNode n = constructTree(new MoveNode(s, root), depth + 1);
//				root.nodes.add(n);
//			}
//		}
//	}
//
//	private void nullifyMove(MoveNode root, int color, Piece redacted) {
//		if (color == 0) {
//			// nullify black node
//		} else {
//			// nullify white node
//		}
//	}
//
////	private MoveNode constructTree(MoveNode root, int depth) {
////		if (depth == this.depth) {
////			return null;
////		}
////		System.out.println("Looking to expand on : " + root.code);
////		int nums[] = ChessBoard.parseMove(root.code.split(" "));
////		Piece redacted = getRedacted(root, nums);
////		System.out.println("Piece killed: " + (redacted != null ? redacted : ""));
////		System.out.println("Depth: " + depth);
////		root.nodes = new ArrayList<MoveNode>();
////		if (!root.code.equals("s")) {
////			simulate(root, nums, redacted, depth % 2);
////		}
////
////		if (depth % 2 == 0) {
////			// get white nodes
////			for (String s : mind.white.getMoves(mind, mind.black)) {
////				MoveNode n = new MoveNode(s, root);
////				constructTree(n, depth + 1);
////				root.nodes.add(n);
////			}
////		} else {
////			// get black nodes
////			for (String s : mind.black.getMoves(mind, mind.white)) {
////				MoveNode n = new MoveNode(s, root);
////				constructTree(n, depth + 1);
////				root.nodes.add(n);
////			}
////		}
////
////		if (!root.code.equals("s")) {
////			nullifyMove(root, redacted, nums, depth % 2);
////		}
////
////		return root;
////	}
////
////	private Piece getRedacted(MoveNode root, int[] nums) {
////		if (nums != null && root.code.contains("x")) {
////			// Get the peice killed by enpassant
////			if (mind.matrix[nums[2]][nums[3]].piece == null && mind.matrix[nums[0]][nums[1]].piece instanceof Pawn) {
////				root.isEnpassantMove = true;
////				return mind.matrix[nums[0]][nums[3]].piece;
////			} else {
////				return mind.matrix[nums[2]][nums[3]].piece;
////			}
////		}
////
////		return null;
////	}
////
	// For the regular moves the framework to revert moves is already implemented in
	// the chessboard class.
	// So I just need to implement for enpassants, and castling situation's
	// revertion.
//	private void nullifyMove(MoveNode root, Piece redacted, int[] arr, int color) {
//		if (arr != null) {
//			if (root.isEnpassantMove) {
//				// then it is enpassant
//				redactEnpassant(arr, redacted, color);
//			} else {
//				mind.nullifyMove(arr[0], arr[1], arr[2], arr[3], redacted);
//			}
//		} else if (root.isKCastleMove) {
//			
//		} else if (root.isQCastleMove) {
//
//		}
//
//	}

//	private void redactEnpassant(int[] arr, Piece redacted, int color) {
//		mind.matrix[arr[0]][arr[1]].piece = mind.matrix[arr[2]][arr[3]].piece;
//		mind.matrix[arr[2]][arr[3]].piece = null;
//		mind.matrix[arr[0]][arr[3]].piece = redacted;
//		mind.matrix[arr[0]][arr[1]].piece.row = arr[0];
//		mind.matrix[arr[0]][arr[1]].piece.col = arr[1];
//		mind.matrix[arr[0]][arr[1]].piece.counter--;
//		mind.moveCount--;
//		
//	}
////
////	// This method would have to handle enpassants, castles, and regular moves.
////	private void simulate(MoveNode root, int[] nums, Piece redacted, int color) {
////		// enpassant
////		if (root.isEnpassantMove) {
////			// root.isEnpassantMove = true;
////			simulateEnpassant(nums, color);
////		} else if (root.code.equals("O-O")) {
////			// castle king side
////			root.isKCastleMove = true;
////			simulateKingSideCastle(depth % 2);
////		} else if (root.code.equals("O-O-O")) {
////			// castle queen side
////			root.isQCastleMove = true;
//			simulateQueenSideCastle(depth % 2);
//		} else {
//			// regular move
//			mind.movePiece(nums[0], nums[1], nums[2], nums[3]);
//		}
//	}
//
//	private void simulateEnpassant(int[] nums, int color) {
//		Piece s = mind.matrix[nums[0]][nums[3]].piece;
//		graveyard.add(s);
//		mind.white.pieces.remove(s);
//		mind.matrix[nums[0]][nums[3]].piece = null;
//		mind.movePiece(nums[0], nums[1], nums[2], nums[3]);
//	}
//
//	private void simulateKingSideCastle(int color) {
//		if (color == 0) {
//			// black move
//			mind.performKingSideCastle(mind.black.getKing(), mind.black.getKingSideRook());
//		} else {
//			// white move
//			mind.performKingSideCastle(mind.white.getKing(), mind.white.getKingSideRook());
//		}
//	}
//
//	private void simulateQueenSideCastle(int color) {
//		if (color == 0) {
//			// black move
//			mind.performQueenSideCastle(mind.black.getKing(), mind.black.getQueenSideRook());
//		} else {
//			// white move
//			mind.performQueenSideCastle(mind.white.getKing(), mind.white.getQueenSideRook());
//		}
//	}

}
