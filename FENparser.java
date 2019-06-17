/* Mehul Joshi
 * FENparser.java
 * Given an FEN string this program will create a chessboard representation
 * 
 */
import java.util.*;
import java.io.*;

public class FENparser {
	

	public static void main(String[] args) {
		try {
			Scanner in= new Scanner(new File("/Users/mehuljoshi/Desktop/FEN.txt"));
			while(in.hasNextLine()) {
				
				String FEN = in.nextLine();
				System.out.println(FEN);
				String[][] board = new String[8][8];
				
				setUpBoard(FEN, board);
				System.out.println(toString(board));
				
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		
	
	}
	//NumberUtils.isNumber("") = false
	
	public static void setUpBoard(String FEN, String[][] board) {
		if(FEN.contains("w")) {
			FEN = FEN.substring(0, FEN.indexOf("w") - 1);
		} else {
			
			FEN = FEN.substring(0, FEN.lastIndexOf("b")-1);
		}
		
		
		String[] array = FEN.split("/");
		for(int i = 0; i < array.length; i++) {
			int a = 0;
			for(int j = 0; j < 8;) {
				String s = ""+ array[i].charAt(a);
				if(s.matches("\\d")) {
					j += Integer.parseInt(s);
					
				} else {
				
					board[i][j] = s;	
					j++;
					
				}
				

				
				a++;
			}
		}
		System.out.println(Arrays.toString(array));
	}
	
	
	
	public static String toString(String[][] board) {
		String result = lineDivider();
		for (int row = 0; row < board.length; row++) {
			result += (8 - row) + " |";
			for (int col = 0; col < board.length; col++) {
					if(board[row][col] == null) {
						result += "   |";
					} else {
						result += String.format("%2s |", board[row][col]);
					}
					
			}

			result += "\n" + lineDivider();
		}

		result += " ";

		for (int i = 0; i < 8; i++) {
			result += "   " + (char) (97 + i);
		}

		return result;

	}
	
	
	public static String lineDivider() {
		String result = "  ";
		for (int i = 0; i < 33; i++) {
			result += "_";
		}
		return result + "\n";
	}
	
	
	
	

	

}
