package lexer;

public class TokenError extends Token<String> {

    public TokenError(String string) {
        super(TokenType.TOK_ERR, string);
    }
}
