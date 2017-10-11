import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class programMain {
	
	private static Lexer myLex = new Lexer();
	
	public static void main(String[] args) {
		if (args.length > 1) {
			System.out.println("Usage: Space-Cadets-2 <path>");
		} else if (args.length == 1) {
			String file = loadFile(args[0]);
			LinkedList<Token> tokenList = myLex.strToTokens(file);
		}
	}
	
	public static String loadFile(String path) {
		String str = "";
		try {
			File f = new File(path);
			if(f.exists() && !f.isDirectory()) { 
				BufferedReader in = new BufferedReader(new FileReader(path));
				String curLine = "";
				while (curLine != null) {
					str += curLine;
					curLine = in.readLine();
				}
			
				in.close();
			} else { throw new IOException(); }

		} catch (IOException e) { System.out.println("Invalid path entered"); System.exit(0); }
		
		return str;
	}
	
}
