package lexer;

public class TokenBool extends Token<Boolean> {

    public TokenBool(boolean value) {
        super(TokenType.TOK_BOOL, value);
    }
}
