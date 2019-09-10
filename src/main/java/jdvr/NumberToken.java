package jdvr;

class NumberToken implements Token {
	Integer value;

	public NumberToken(Integer value) {
		this.value = value;
	}

	@Override
	public TokenType type() {
		return TokenType.NUMBER;
	}
}
