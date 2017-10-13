
public class Variable {

	private String identifier;
	private int Value;
	
	public Variable(String identifier) {
		this.identifier = identifier;
		Value = 0;
	}
	
	public void increment() {
		Value++;
	}
	
	public void decrement() {
		Value--;
	}
	
	public void reset() {
		Value = 0;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public int getValue() {
		return Value;
	}
}
