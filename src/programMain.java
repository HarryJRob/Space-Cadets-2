import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class programMain {
	
	private static Lexer myLex = new Lexer();
	
	public static void main(String[] args) {
		String file = loadFile("/Users/HJR/Desktop/test.txt");
		interpret(file);
		
		//if (args.length > 1) {
		//	System.out.println("Usage: Space-Cadets-2 <path>");
		//} else if (args.length == 1) {
		//	String file = loadFile(args[0]);
		//	interpret(file);
		//}
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
	
	/*
	 * Any number of tabs may come after a line terminator and before a command
	 * Valid token strings:
	 *  INCREMENT IDENTIFIER LINE_TERM
	 *  DECREMENT IDENTIFIER LINE_TERM
	 *  CLEAR IDENTIFIER LINE_TERM
	 *  WHILE IDENTIFIER NOT NUMBER DO LINE_TERM ... END LINE_TERM
	 */
	
	/*
	 * 	      case "incr": returnStr = "INCREMENT";  break;
	      case "decr": returnStr = "DECREMENT";  break;
	      case "clear": returnStr = "CLEAR";  break;
	      case "while": returnStr = "WHILE"; break;
	      case "not": returnStr = "NOT"; break;
	      case "do": returnStr = "DO"; break;
	      case "end": returnStr = "END"; break;
	      case ";" : returnStr = "LINE_TERM"; break;
	      case "" : returnStr = "TAB"; break;
	 */
	
	public static void interpret(String fileStr) {
		LinkedList<Variable> varList = new LinkedList<Variable>();
		Stack<Variable> expectedStack = new Stack<Variable>();
		LinkedList<Token> curTokenList = new LinkedList<Token>();
		
		fileStr = fileStr.replace(";",";:");
		String[] lines = fileStr.split(":");
		
		for (int i = 0; i < lines.length; i++) {
			System.out.println("Cur line: "+lines[i]);
			curTokenList = myLex.strToTokens(fileStr, i);
			
			for (int a = 0; a< curTokenList.size(); a++) {
				switch (curTokenList.get(a).getType()){
					case "INCREMENT": break;
				}
			}
			
			curTokenList.clear();
		}
		
	}
	
}
