package expressions;

import lexer.TokenType;

import java.util.Objects;

/**
 * A prefix unary expression like: !a
 *                                 -b
 */
public class PrefixExpression implements Expression {
    private final TokenType operator;
    private final Expression right;

    public PrefixExpression(TokenType operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    public void print(StringBuilder builder) {
        builder.append("(").append(operator.getValue());
        right.print(builder);
        builder.append(")");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrefixExpression that = (PrefixExpression) o;
        return operator == that.operator &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(operator, right);
    }
}
