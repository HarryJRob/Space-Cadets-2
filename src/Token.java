
public class Token {

	final String type;
	final String additional;
	final int tokenNum;
	
	public Token(String type, String additional, int tokenNum) {
		this.type = type;
		this.additional = additional;
		this.tokenNum = tokenNum;
	}
	
	public String toString() {
		return type + " " + additional + " " + tokenNum;
	}
}
