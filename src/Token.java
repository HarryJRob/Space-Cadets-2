
public class Token {

	private final String type;
	private final String additional;
	private final int lineNum;
	
	public Token(String type, String additional, int lineNum) {
		this.type = type;
		this.additional = additional;
		this.lineNum = lineNum;
	}
	
	public String getType() {
		return type;
	}
	
	public String getAdditional() {
		return additional;
	}
	
	public int getLineNum() {
		return lineNum;
	}
}
