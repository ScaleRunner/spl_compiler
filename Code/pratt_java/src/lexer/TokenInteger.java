package lexer;

public class TokenInteger extends Token<Integer> {
    public TokenInteger(int val) {
        super(TokenType.TOK_INT, val);
    }
}
