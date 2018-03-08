package expressions;

import lexer.TokenType;

import java.util.Objects;

/**
 * Expression with Operator like: a + b
 *                                a - b
 *                                a * b
 *                                etc.
 */
public class OperatorExpression implements Expression {
    private final Expression left;
    private final TokenType operator;
    private final Expression right;

    public OperatorExpression(Expression left, TokenType operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public void print(StringBuilder builder) {
        builder.append("(");
        left.print(builder);
        builder.append(" ").append(operator.toString()).append(" ");
        right.print(builder);
        builder.append(")");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperatorExpression that = (OperatorExpression) o;
        return Objects.equals(left, that.left) &&
                operator == that.operator &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(left, operator, right);
    }
}
