import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

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
		HashMap<String, Integer> subroutineMap = new HashMap<String, Integer>();
		LinkedList<Variable> varList = new LinkedList<Variable>();
		LinkedList<Token> curTokenList = new LinkedList<Token>();
		Stack<Integer> loopStack = new Stack<Integer>();
		Stack<Integer> callStack = new Stack<Integer>();
		
		//String management stuff
		fileStr = "incr X; incr X; incr X; incr X; incr X; while X not 0 do; incr Y; decr X; end; incr Y; #Some comment; decr Y; sub test; incr X; incr X; end sub; test; sub text; incr Y; test; end sub; text;";
		
		fileStr = fileStr.replaceAll("#[ a-zA-Z0-9_-]*;", "");
		fileStr = fileStr.replace(";",";:");
		String[] lines = fileStr.split(":");
		
		System.out.println("-------------------------------------------\n              Program Start\n-------------------------------------------");
		
		for (int i = 0; i < lines.length; i++) {
			System.out.println("\nCurrent line: " + (i+1) + "\nStatement: "+lines[i].trim());
			curTokenList = myLex.strToTokens(lines[i], i);
			String tokenSyntax = getTokenSyntax(curTokenList).trim();
			
			if (tokenSyntax.matches("SUBROUTINE IDENTIFIER LINE_TERM")) {
				subroutineMap.put(curTokenList.get(1).getAdditional(), i);
				int endPos = getNextString("end sub;",i,lines);
				if (endPos != -1) {
					i = endPos;
				} else { System.out.println("Subroutine does not have terminator: \nLine: "+lines[i]+"\nLine Number: "+(i+1)); }
				
			} else if (tokenSyntax.matches("SUBROUTINE_END LINE_TERM")) {
				if (callStack.size() < 1 ) {
					System.out.println("Unexpected sub end statement: \nLine: "+lines[i]+"\nLine Number: "+(i+1));
				} else { i = callStack.pop(); }
			} else if (tokenSyntax.matches("IDENTIFIER LINE_TERM")) {
				if (subroutineMap.containsKey(curTokenList.get(0).getAdditional())) {
					callStack.add(i);
					i = subroutineMap.get(curTokenList.get(0).getAdditional());
				}
			} else if(tokenSyntax.matches("INCREMENT IDENTIFIER LINE_TERM")) {
				posAddVariable(varList,curTokenList.get(1).getAdditional());
				varList.get(findVarName(varList,curTokenList.get(1).getAdditional())).increment();
				
			} else if(tokenSyntax.matches("DECREMENT IDENTIFIER LINE_TERM")) {
				posAddVariable(varList,curTokenList.get(1).getAdditional());
				varList.get(findVarName(varList,curTokenList.get(1).getAdditional())).decrement();
				
			} else if(tokenSyntax.matches("CLEAR IDENTIFIER LINE_TERM")) {
				posAddVariable(varList,curTokenList.get(1).getAdditional());
				varList.get(findVarName(varList,curTokenList.get(1).getAdditional())).clear();
				
			} else if(tokenSyntax.matches("WHILE IDENTIFIER NOT NUMBER DO LINE_TERM")) {
				int endPos = getNextString("end;", i, lines);
				if (endPos != -1) {
					posAddVariable(varList,curTokenList.get(1).getAdditional());
					if (varList.get(findVarName(varList,curTokenList.get(1).getAdditional())).getValue() != 
							Integer.valueOf(curTokenList.get(3).getAdditional()))
					{ loopStack.add(i); }
					else { i = endPos; }
					
				} else { System.out.println("Loop does not have terminator: \nLine: "+lines[i]+"\nLine Number: "+(i+1));}
				
			} else if(tokenSyntax.matches("END LINE_TERM")) {
				if (loopStack.size() < 1 ) {
					System.out.println("Unexpected end statement: \nLine: "+lines[i]+"\nLine Number: "+(i+1));
				} else { i = loopStack.pop()-1; }
			} else { System.out.println("Invalid Syntax: \nLine: "+lines[i]+"\nLine Number: "+(i+1));}
			
			curTokenList.clear();
			printVarList(varList);
		}
		
		if (loopStack.size() != 0) {
			for (int i = 0; i < loopStack.size(); i++) {
				System.out.println("\nError loop did not terminate: \n"+"Line: " + loopStack.get(i) + "\nStatement: " + lines[loopStack.get(i)]);
			}
		}
		
		System.out.println("\n-------------------------------------------\n               Program end\n-------------------------------------------");
	}
	
	private static int getNextString(String toFind,int curLine, String[] lines) {
		for (int i = curLine; i < lines.length; i++) {
			if (lines[i].trim().matches(toFind.trim())) {
				return i;
			}
		}
		return -1;
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
