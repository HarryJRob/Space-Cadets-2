import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BareBones {
	
	private static Lexer myLex = new Lexer();
	
	public static void main(String[] args) {
		interpret("");
	//	if (args.length != 1 ) {
	//		System.out.println("Usage: BareBones <path>");
	//	} else if (args.length == 1) {
	//		interpret(loadFile(args[0]));
	//	}
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
	
	public static void interpret(String fileStr) {
		LinkedList<Variable> varList = new LinkedList<Variable>();
		LinkedList<Token> curTokenList = new LinkedList<Token>();
		Stack<Integer> loopStack = new Stack<Integer>(); // This is a bad way to do this! This could be done using expected stack 
		fileStr = "incr X; clear X; incr X; incr X; decr X;";
		fileStr = fileStr.replace(";",";:");
		String[] lines = fileStr.split(":");
		
		for (int i = 0; i < lines.length; i++) {
			System.out.println("\nCurrent line: " + (i+1) + "\nStatement: "+lines[i]);
			curTokenList = myLex.strToTokens(lines[i], i);
			String tokenSyntax = getTokenSyntax(curTokenList).trim();
			
			if(tokenSyntax.matches("INCREMENT IDENTIFIER LINE_TERM")) {
				posAddVariable(varList,curTokenList.get(1).getAdditional());
				varList.get(findVarName(varList,curTokenList.get(1).getAdditional())).increment();
				
			} else if(tokenSyntax.matches("DECREMENT IDENTIFIER LINE_TERM")) {
				posAddVariable(varList,curTokenList.get(1).getAdditional());
				varList.get(findVarName(varList,curTokenList.get(1).getAdditional())).decrement();
				
			} else if(tokenSyntax.matches("CLEAR IDENTIFIER LINE_TERM")) {
				posAddVariable(varList,curTokenList.get(1).getAdditional());
				varList.get(findVarName(varList,curTokenList.get(1).getAdditional())).clear();
				
			} else if(tokenSyntax.matches("WHILE IDENTIFIER NOT NUMBER DO LINE_TERM")) {
				
			} else if(tokenSyntax.matches("END LINE_TERM")) {
				
			} else { System.out.println("Invalid Syntax: \nLine: "+lines[i]+"\nLine Number: "+i);}
			
			curTokenList.clear();
			printVarList(varList);
		}
		
		if (loopStack.size() != 0) {
			for (int i = 0; i < loopStack.size(); i++) {
				System.out.println("\nError loop did not terminate: \n"+"Line: " + loopStack.get(i) + "\nStatement: " + lines[loopStack.get(i)]);
			}
		}
	}
	
	private static String getTokenSyntax(LinkedList<Token> tokenList) {
		String returnStr = "";
		for(int i = 0; i < tokenList.size();i++) {
			returnStr += tokenList.get(i).getType()+" ";
		}
		return returnStr;
	}
	
	private static LinkedList<Variable> posAddVariable(LinkedList<Variable> list,String name) {
		boolean toAdd = true;
		
		if (findVarName(list,name) != -1) {
			toAdd = false;
		}
		
		if (toAdd) {
			list.add(new Variable(name));
		}
		
		return list;
	}
	
	private static int findVarName(LinkedList<Variable> list, String name) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getIdentifier().hashCode() == name.hashCode()) {
				return i;
			}
		}
		return -1;
	}
	
	private static void printVarList(LinkedList<Variable> list) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).getIdentifier() + " = " + list.get(i).getValue());
		}
	}
}
