
public class Token {

	private final String type;
	private final String additional;
	private final int tokenNum;
	
	public Token(String type, String additional, int tokenNum) {
		this.type = type;
		this.additional = additional;
		this.tokenNum = tokenNum;
	}
	
	public String getType() {
		return type;
	}
	
	public String getAdditional() {
		return additional;
	}
}
