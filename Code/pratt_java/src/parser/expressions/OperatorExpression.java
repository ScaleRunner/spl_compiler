package parser.expressions;

import lexer.TokenType;
import util.Visitor;

import java.util.Objects;

/**
 * Expression with Operator like: a + b
 *                                a - b
 *                                a * b
 *                                etc.
 */
public class OperatorExpression implements Expression {
    public final Expression left;
    public final TokenType operator;
    public final Expression right;

    public OperatorExpression(Expression left, TokenType operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
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

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
