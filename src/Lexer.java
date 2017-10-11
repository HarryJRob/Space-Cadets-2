import java.util.*;

public class Lexer {
	
	public LinkedList<Token> strToTokens(String file) {
		LinkedList<Token> tokenList = new LinkedList<Token>();
		file = file.replaceAll(";", " ; ");
		String[] splitFile = file.split(" ");
		
		for (int i = 0; i < splitFile.length; i++) {
			tokenList.add(new Token(identifyToken(splitFile[i]),splitFile[i],i));
			System.out.println(identifyToken(splitFile[i])+" , \'"+splitFile[i]+"\' , "+i);
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
	      case ";" : returnStr = "LINE_TERM"; break;
	      case "" : returnStr = "TAB"; break;
	      default: 
	    	  if (curToken.matches("[a-zA-Z]+")) {
	    		  returnStr = "IDENTIFIER";
	    	  } else if (curToken.matches("[0-9]+")) {
	    		  returnStr = "NUMBER";
	    	  }
	    	  break;
	    }
		return returnStr;
	}
	
}
