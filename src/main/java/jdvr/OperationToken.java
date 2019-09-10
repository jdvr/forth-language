package jdvr;

class OperationToken implements Token{

	TokenType type;
	public OperationToken(TokenType type) {
		this.type = type;
	}

	@Override
	public TokenType type() {
		return type;
	}
}
