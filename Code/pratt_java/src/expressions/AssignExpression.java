package expressions;

import util.Visitor;

import java.util.Objects;

/**
 * Assignment: a = b
 */
public class AssignExpression implements Expression {
    public final String name;
    public final Expression right;

    public AssignExpression(String name, Expression right) {
        this.name = name;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignExpression that = (AssignExpression) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, right);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
