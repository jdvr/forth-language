package jdvr;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ForthEvaluator {

	public final static Map<TokenType, BiFunction<Integer, Integer, List<Integer>>> TwoValueOperationByToken = Map.of(
			TokenType.ADD, (left, right) -> List.of(left + right),
			TokenType.SUBTRACT, (left, right) -> List.of(left - right),
			TokenType.MULTIPLICATION, (left, right) -> List.of(left * right),
			TokenType.SWAP, (left, right) -> List.of(right, left),
			TokenType.OVER, (first, second) -> List.of(first, second, first),
			TokenType.DIVISION, (left, right) -> {
				if (right == 0) {
					throw new IllegalArgumentException("Division by 0 is not allowed");
				}
				return List.of(left / right);
			}
	);

	public final static Map<TokenType, IntFunction<List<Integer>>> SingleValueOperationByToken = Map.of(
			TokenType.DUP, value -> List.of(value, value),
			TokenType.DROP, value -> Collections.emptyList()
	);

	private final Map<String, VariableToken> variables;

	public ForthEvaluator() {
		variables = new HashMap<>();
	}


	public List<Integer> evaluateProgram(List<String> input) {
		var down = new Stack<Integer>();
		for (var line : input) {
			var tokens = parse(line);
			for (var token : tokens) {
				evaluate(down, token);
			}
		}
		return List.copyOf(down);
	}

	private void evaluate(Stack<Integer> down, Token token) {
		if (token.type() == TokenType.NUMBER) {
			down.push(((NumberToken) token).value);
		} else if (token.type() == TokenType.VARIABLE) {
			List<Token> variableValue = ((VariableToken) token).getValue();
			for (var t : variableValue) {
				evaluate(down, t);
			}
		} else if (TwoValueOperationByToken.containsKey(token.type())) {
			if (down.isEmpty() || down.size() < 2) {
				throw new IllegalArgumentException(token.type().toString() + " requires that the stack contain at least 2 values");
			}
			var right = down.pop();
			var left = down.pop();
			var res = TwoValueOperationByToken.get(token.type()).apply(left, right);
			down.addAll(res);
		} else {
			if (down.isEmpty()) {
				throw new IllegalArgumentException(token.type().toString() + " requires that the stack contain at least 1 value");
			}
			var value = down.pop();
			var res = SingleValueOperationByToken.get(token.type()).apply(value);
			down.addAll(res);
		}
	}

	private List<Token> parse(String line) {
		boolean isNotVariableDefinition = !line.startsWith(": ");
		if (isNotVariableDefinition) {
			return Stream.of(line.split("\\s"))
					.map(this::parseSingleWord)
					.collect(Collectors.toList());
		}
		String withoutColonAndSemiColon = removeColonAndSemicolon(line);
		String variableName = getVariableName(withoutColonAndSemiColon).toLowerCase();
		var parts = List.of(withoutColonAndSemiColon.split("\\s"));
		var rawValues = parts.subList(1, parts.size());
		var tokens = rawValues.stream().map(this::parseSingleWord).collect(Collectors.toList());
		variables.put(variableName, new VariableToken(tokens));
		return List.of();
	}

	private String removeColonAndSemicolon(String line) {
		return line
				.replace(":", "")
				.replace(";", "")
				.trim();
	}

	private String getVariableName(String withoutColonAndSemiColon) {
		var variableName = withoutColonAndSemiColon.substring(0, withoutColonAndSemiColon.indexOf(" "));
		if (variableName.matches("[\\d]+")) {
			throw new IllegalArgumentException("Cannot redefine numbers");
		}
		return variableName;
	}

	private Token parseSingleWord(String singleWord) {
		var lowerCaseInput = singleWord.toLowerCase();
		if (lowerCaseInput.matches("[\\d]+")) {
			return new NumberToken(Integer.valueOf(singleWord));
		} else if (variables.containsKey(lowerCaseInput)) {
			return variables.get(lowerCaseInput);
		} else {
			return Stream.of(TokenType.values())
					.filter(tokenType -> tokenType.match(lowerCaseInput))
					.findFirst()
					.map(tt -> (Token) new OperationToken(tt))
					.orElseThrow(() -> new IllegalArgumentException(String.format("No definition available for operator \"%s\"", singleWord)));
		}
	}

}
