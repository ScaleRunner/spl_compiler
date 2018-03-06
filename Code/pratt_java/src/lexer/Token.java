package lexer;

public abstract class Token<T> {
    private final TokenType tokenType;
    private final T value;

    public Token(TokenType tokenType, T value) {
        this.tokenType = tokenType;
        this.value = value;
    }

    public TokenType getType() {
        return tokenType;
    }

    public T getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }
}
