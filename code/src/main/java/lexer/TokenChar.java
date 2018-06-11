package lexer;

public class TokenChar extends Token<Character> {
    public TokenChar(char val) {
        super(TokenType.TOK_CHAR, val);
    }
}
