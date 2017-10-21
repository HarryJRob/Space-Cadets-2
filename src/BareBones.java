import java.io.*;
import java.util.*;

public class BareBones {
	
	private static Lexer myLex = new Lexer();
	
	public static void main(String[] args) {
		//interpret("");
		if (args.length != 1 ) {
			System.out.println("Usage: BareBones <path>");
		} else if (args.length == 1) {
			interpret(loadFile(args[0]));
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
					if (curLine != "")
					str += curLine + "\n";
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
		//fileStr = "incr X;\n incr X;\n incr X;\n incr X;\n incr X;\n while X not 0 do;\n incr Y;\n decr X;\n end;\n incr Y;\n #Some comment\n decr Y;\n sub Test;\n incr X;\n incr X;\n end sub;\n Test;\n sub text;\n incr Y;\n Test;\n end sub;\n text;\n if X / X == 1;\n incr Z;\n end if;\n";
		fileStr = fileStr.replaceAll("#[ a-zA-Z0-9_-]*\n", "");
		String[] lines = fileStr.split("\n");
		
		System.out.println("-------------------------------------------\n              Program Start\n-------------------------------------------");
		
		for (int i = 0; i < lines.length; i++) {
			System.out.println("\nCurrent line: " + (i+1) + "\nStatement: "+lines[i].trim());
			curTokenList = myLex.strToTokens(lines[i], i);
			String tokenSyntax = myLex.getTokenSyntax(curTokenList).trim();

			if (tokenSyntax.matches("IF (IDENTIFIER|NUMBER) (OPERATOR (IDENTIFIER|NUMBER) )?COMPARATOR (IDENTIFIER|NUMBER) (OPERATOR (IDENTIFIER|NUMBER) )?LINE_TERM")) {
				boolean ifResult = false;
				{
					String statement1 = "", statement2 = "", operator = "";
				
					if (tokenSyntax.matches("IF (IDENTIFIER|NUMBER) COMPARATOR (IDENTIFIER|NUMBER) LINE_TERM")) {
						statement1 += curTokenList.get(1).getAdditional();
						operator += curTokenList.get(2).getAdditional();
						statement2 += curTokenList.get(3).getAdditional();
					
					} else if (tokenSyntax.matches("IF (IDENTIFIER|NUMBER) (OPERATOR (IDENTIFIER|NUMBER) )COMPARATOR (IDENTIFIER|NUMBER) LINE_TERM")) {
						statement1 += curTokenList.get(1).getAdditional() + " " + curTokenList.get(2).getAdditional() + " " + curTokenList.get(3).getAdditional(); 
						operator += curTokenList.get(4).getAdditional();
						statement2 += curTokenList.get(5).getAdditional();
					
					} else if (tokenSyntax.matches("IF (IDENTIFIER|NUMBER) COMPARATOR (IDENTIFIER|NUMBER) (OPERATOR (IDENTIFIER|NUMBER) )LINE_TERM")) {
						statement1 += curTokenList.get(1).getAdditional();
						operator += curTokenList.get(2).getAdditional();
						statement2 += curTokenList.get(3).getAdditional() + " " + curTokenList.get(4).getAdditional() + " " + curTokenList.get(5).getAdditional();
					
					} else if (tokenSyntax.matches("IF (IDENTIFIER|NUMBER) (OPERATOR (IDENTIFIER|NUMBER) )COMPARATOR (IDENTIFIER|NUMBER) (OPERATOR (IDENTIFIER|NUMBER) )LINE_TERM")) {
						statement1 += curTokenList.get(1).getAdditional() + " " + curTokenList.get(2).getAdditional() + " " + curTokenList.get(3).getAdditional(); 
						operator += curTokenList.get(4).getAdditional();
						statement2 += curTokenList.get(5).getAdditional() + " " + curTokenList.get(6).getAdditional() + " " + curTokenList.get(7).getAdditional();
					
					} else { System.out.println("If statement error: This should never be printed!!"); }

					int valueOne = evaluateStatement(varList, statement1), valueTwo = evaluateStatement(varList,statement2);
					
					switch(operator) {
						case "==": 
							if (valueOne == valueTwo)
								ifResult = true;
							break;
						case "!=": 
							if (valueOne != valueTwo)
								ifResult = true;
							break;
						case "<=": 
							if (valueOne <= valueTwo)
								ifResult = true;
							break;
						case ">=": 
							if (valueOne >= valueTwo)
								ifResult = true;
							break;
						case ">": 
							if (valueOne > valueTwo)
								ifResult = true;
							break;
						case "<": 
							if (valueOne < valueTwo)
								ifResult = true;
							break;
					}
					
					if (ifResult == false) {
						int nextElse = getNextString("else;",i,lines);
						int nextEndIf = getNextString("end if;",i,lines);
						if (nextEndIf != -1) {
							if (nextElse < nextEndIf && nextElse != -1) {
								i = nextElse;
							} else if (nextEndIf < nextElse || nextElse == -1) {
								i = nextEndIf;
							}
						} else { System.out.println("If statement does not have terminator: \nLine: "+lines[i]+"\nLine Number: "+(i+1)); }
					} 
				}
			} else if (tokenSyntax.matches("ELSE LINE_TERM")) {
				if (getNextString("end if;",i,lines) != -1) {
					i = getNextString("end if;",i,lines);
				} else { System.out.println("Else statement does not have terminator: \nLine: "+lines[i]+"\nLine Number: "+(i+1)); }
				
			} else if (tokenSyntax.matches("ENDIF LINE_TERM")) {
				
			} else if (tokenSyntax.matches("SUBROUTINE IDENTIFIER LINE_TERM")) {
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
				} else { System.out.println("Unidentified subroutine call: \nLine: "+lines[i]+"\nLine Number: "+(i+1)); }
				
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
			
			} else if(tokenSyntax.matches("")) {
			} else { System.out.println("Invalid Syntax: \nLine: "+lines[i]+"\nLine Number: "+(i+1));}
			
			curTokenList.clear();
			printVarList(varList);
		}
		
		if (loopStack.size() != 0) {
			for (int i = 0; i < loopStack.size(); i++) {
				System.out.println("Error loop did not terminate: \n"+"Line: " + loopStack.get(i) + "\nStatement: " + lines[loopStack.get(i)]);
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
	
	//Var Utils
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
	
	private static int evaluateStatement(LinkedList<Variable> varList, String statement) {
		String[] parts = statement.split(" ");
		if (parts.length == 1) {
			if (parts[0].matches("[a-zA-Z]+")) {
				return varList.get(findVarName(varList,parts[0])).getValue();
			} else if (parts[0].matches("[0-9.]+")) {
				return Integer.parseInt(parts[0]);
			}

		} else if (parts.length == 3) {
			int v1 = 0, v2 = 0;
			
			if (parts[0].matches("[a-zA-Z]+")) {
				v1 = varList.get(findVarName(varList,parts[0])).getValue();
			} else if (parts[0].matches("[0-9.]+")) {
				v1 = Integer.parseInt(parts[0]);
			}
			
			if (parts[2].matches("[a-zA-Z]+")) {
				v2 = varList.get(findVarName(varList,parts[2])).getValue();
			} else if (parts[2].matches("[0-9.]+")) {
				v2 = Integer.parseInt(parts[2]);
			}
			
			switch(parts[1]) {
				case "+": return v1 + v2;
				case "-": return v1 - v2;
				case "*": return v1 * v2;
				case "/": if (v2 != 0) { return v1 / v2; } else { return -1; }
			}
		}
		
		return -1;
	}
}
