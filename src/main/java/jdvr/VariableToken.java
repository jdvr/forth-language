package jdvr;

import java.util.List;

public class VariableToken implements Token {

	private final List<Token> value;

	public VariableToken(List<Token> value) {
		this.value = value;
	}

	public List<Token> getValue() {
		return value;
	}

	@Override
	public TokenType type() {
		return TokenType.VARIABLE;
	}
}
