package lexer;

public class TokenOther extends Token<String> {

    public TokenOther(TokenType tokenType) {
        super(tokenType, tokenType.getValue());
    }

}
