package lexer;

public class TokenField extends Token {
	final String value;

	public String getValue() {
		return value;
	}

	public TokenField(String val) {
		super(TokenType.TOK_FIELD);
		value = val;
	}

}
