package parser.expressions;

import lexer.TokenType;
import util.Visitor;

import java.util.Objects;

/**
 * A prefix unary expression like: !a
 *                                 -b
 */
public class PrefixExpression extends Expression {
    public final TokenType operator;
    public final Expression right;

    public PrefixExpression(TokenType operator, Expression right) {
        this.operator = operator;
        this.right = right;
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

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
