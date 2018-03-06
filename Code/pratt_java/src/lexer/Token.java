package lexer;

import java.util.Objects;

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
        return String.format("(%s, %s)", tokenType.toString(), String.valueOf(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token<?> token = (Token<?>) o;
        return tokenType == token.tokenType &&
                Objects.equals(value, token.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(tokenType, value);
    }
}
