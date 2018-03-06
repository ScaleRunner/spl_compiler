package lexer;

public class TokenField extends Token<String> {
    public TokenField(String val) {
        super(TokenType.TOK_FIELD, val);
    }
}
