package lexer;

public class TokenError extends Token {
	String error;

	public String getError() {
		return error;
	}

	public TokenError(String string) {
		super(TokenType.TOK_ERR);
		error = string;
	}

}
