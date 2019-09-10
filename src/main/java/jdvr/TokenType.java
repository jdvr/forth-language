package jdvr;

enum TokenType {
	NUMBER("#", "#"),
	VARIABLE("#", "#"),
	ADD("+", "Addition"),
	SUBTRACT("-", "Subtraction"),
	DIVISION("/", "Division"),
	MULTIPLICATION("*", "Multiplication"),
	DUP("dup", "Duplicating"),
	SWAP("swap", "Swapping"),
	OVER("over", "Overing"),
	DROP("drop", "Dropping");

	private String symbol;
	private String humanReadableName;

	TokenType(String symbol, String humanReadableName) {
		this.symbol = symbol;
		this.humanReadableName = humanReadableName;
	}

	boolean match(String s) {
		return symbol.equals(s);
	}

	@Override
	public String toString() {
		return humanReadableName;
	}
}
