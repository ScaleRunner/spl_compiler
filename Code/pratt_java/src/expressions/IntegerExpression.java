package expressions;

import util.Visitor;

import java.util.Objects;

/**
 * Identifier Expression: abc
 */
public class IntegerExpression implements Expression {

    public final int name;

    public IntegerExpression(int name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerExpression that = (IntegerExpression) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
