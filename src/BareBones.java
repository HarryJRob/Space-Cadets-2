import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BareBones {
	
	private static Lexer myLex = new Lexer();
	
	public static void main(String[] args) {
		//String file = loadFile("/Users/HJR/Desktop/test.txt"); //MAC
		//String file = loadFile("C:\\Users\\Harry\\Desktop\\Tests\\Bare_Bones.txt"); //PC
		//interpret(file);
		
		if (args.length > 1) {
			System.out.println("Usage: Space-Cadets-2 <path>");
		} else if (args.length == 1) {
			String file = loadFile(args[0]);
			interpret(file);
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
	
	public static void interpret(String fileStr) {
		LinkedList<Variable> varList = new LinkedList<Variable>();
		Stack<Token> expectedStack = new Stack<Token>();
		LinkedList<Token> curTokenList = new LinkedList<Token>();
		Stack<Integer> loopStack = new Stack<Integer>(); // This is a bad way to do this! This could be done using expected stack 

		fileStr = fileStr.replace(";",";:");
		String[] lines = fileStr.split(":");
		
		for (int i = 0; i < lines.length; i++) {
			System.out.println("\nCurrent line: " + (i+1) + "\nStatement: "+lines[i]);
			
			if (lines[i].matches("[ ]*((incr|decr|clear) [a-zA-Z]+|while [a-zA-Z]+ not [0-9]+ do|end);")) {
				curTokenList = myLex.strToTokens(lines[i], i);

				for (int a = 0; a < curTokenList.size(); a++) {
					//System.out.println("Current Token: " +curTokenList.get(a).getType());
					if (expectedStack.size() == 0 || curTokenList.get(a).getType() == expectedStack.pop().getType()) {
						switch (curTokenList.get(a).getType()){
							case "INCREMENT": expectedStack.add(new Token("LINE_TERM","",i));
								expectedStack.add(new Token("IDENTIFIER","",i));
								break;
							case "DECREMENT": expectedStack.add(new Token("LINE_TERM","",i));
								expectedStack.add(new Token("IDENTIFIER","",i)); 
								break;
							case "CLEAR": expectedStack.add(new Token("LINE_TERM","",i));
								expectedStack.add(new Token("IDENTIFIER","",i));
								break;
							case "WHILE": 							
								expectedStack.add(new Token("LINE_TERM","",i)); 
								expectedStack.add(new Token("DO","",i)); 
								expectedStack.add(new Token("NUMBER","",i));
								expectedStack.add(new Token("NOT","",i));
								expectedStack.add(new Token("IDENTIFIER","",i)); 
								
								loopStack.add(i);
							

								break;
							case "NOT": break;
							case "DO": 
								
								break;
							case "END": 
								Integer lineNum = loopStack.pop(); 
								//Not pretty
								
								LinkedList<Token> tempList = myLex.strToTokens(lines[lineNum], i);
								if ( varList.get(findVarName(varList,tempList.get(1).getAdditional())).getValue() != Integer.valueOf(tempList.get(3).getAdditional()))  {
									//System.out.println("Do repeat");
									i = lineNum-1;
								}
								
								expectedStack.add(new Token("LINE_TERM","",i)); break;
							case "LINE_TERM": expectedStack.clear(); break;
							case "IDENTIFIER": 
								varList = posAddVariable(varList,curTokenList.get(a).getAdditional());
								if (curTokenList.get(a-1).getType() == "INCREMENT") 
									varList.get(findVarName(varList,curTokenList.get(a).getAdditional())).increment();
								else if (curTokenList.get(a-1).getType() == "DECREMENT") 
									varList.get(findVarName(varList,curTokenList.get(a).getAdditional())).decrement();
								else if (curTokenList.get(a-1).getType() == "CLEAR") 
									varList.get(findVarName(varList,curTokenList.get(a).getAdditional())).reset();
								break;
							case "NUMBER": break;
							default: System.out.println("\nError processing - Line No: "+i+" Statement: " + curTokenList.get(a).getAdditional()); break;
						}
						
					} else { System.out.println("\nSyntax Error - " + i + "\n" + lines[i]); }
					
				}
				printVarList(varList);
				curTokenList.clear();
			} else { System.out.println("\nError interpreting line: " + i + "\nStatement: " + lines[i] + "\n"); }

		}
	}
	
	private static LinkedList<Variable> posAddVariable(LinkedList<Variable> list,String name) {
		boolean toAdd = true;
		
		if (findVarName(list,name) != -1) {
			toAdd = false;
		}
		
		if (toAdd) {
			//System.out.println("Adding variable: " + name);
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
