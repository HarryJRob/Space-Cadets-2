import java.util.*;

public class Lexer {
	
	public LinkedList<Token> strToTokens(String file, int lineNum) {
		LinkedList<Token> tokenList = new LinkedList<Token>();
		
		//String management stuff
		file = file.replaceAll(";", " ;");
		file = file.replaceAll("end sub", "end_sub");
		String[] splitFile = file.split(" ");
		
		for (int i = 0; i < splitFile.length; i++) {
			Token t = new Token(identifyToken(splitFile[i]),splitFile[i],lineNum);
			if (t.getType() != "TAB")
				tokenList.add(t);
		}
		
		return tokenList;
	}
	
	private String identifyToken(String curToken) {
		String returnStr = "ERROR";
	    switch (curToken) {
	      case "incr": returnStr = "INCREMENT";  break;
	      case "decr": returnStr = "DECREMENT";  break;
	      case "clear": returnStr = "CLEAR";  break;
	      case "while": returnStr = "WHILE"; break;
	      case "not": returnStr = "NOT"; break;
	      case "do": returnStr = "DO"; break;
	      case "end": returnStr = "END"; break;
	      case "if": returnStr = "IF"; break;
	      case "else": returnStr = "ELSE"; break;
	      case "sub": returnStr = "SUBROUTINE"; break;
	      case "end_sub": returnStr = "SUBROUTINE_END"; break;
	      case ";" : returnStr = "LINE_TERM"; break;
	      case "" : returnStr = "TAB"; break;
	      default:
	    	  if (curToken.matches("[+|/|*|-]")) {
	    		  returnStr = "OPERATOR";
	    	  } else if (curToken.matches("[==|!=|<=|>=|>|<]")) {
	    		  returnStr = "COMPARATOR";
	    	  } else if (curToken.matches("[a-zA-Z]+")) {
	    		  returnStr = "IDENTIFIER";
	    	  } else if (curToken.matches("[0-9]+")) {
	    		  returnStr = "NUMBER";
	    	  }
	    	  break;
	    }
		return returnStr;
	}
	
}
