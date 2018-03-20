package expressions;

import util.Visitor;

import java.util.Objects;

/**
 * Identifier Expression: abc
 */
public class TupleExpression implements Expression {

    public final Expression left;
    public final Expression right;

    public TupleExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TupleExpression that = (TupleExpression) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(left, right);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
