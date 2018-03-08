package expressions;

import lexer.TokenType;

import java.util.Objects;

/**
 * A postfix unary expression like: a.hd
 */
public class PostfixExpression implements Expression {
    private final Expression left;
    private final TokenType operator;

    public PostfixExpression(Expression left, TokenType operator) {
        this.left = left;
        this.operator = operator;
    }

    public void print(StringBuilder builder) {
        builder.append("(");
        left.print(builder);
        builder.append(operator.toString()).append(")");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostfixExpression that = (PostfixExpression) o;
        return Objects.equals(left, that.left) &&
                operator == that.operator;
    }

    @Override
    public int hashCode() {

        return Objects.hash(left, operator);
    }
}
