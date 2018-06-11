package lexer;

public class TokenIdentifier extends Token<String> {
    public TokenIdentifier(String val) {
        super(TokenType.TOK_IDENTIFIER, val);
    }
}
