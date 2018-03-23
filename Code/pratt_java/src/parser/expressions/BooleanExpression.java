package parser.expressions;

import util.Visitor;

import java.util.Objects;

/**
 * Boolean Expression: True, False
 */
public class BooleanExpression implements Expression {

    public final boolean name;

    public BooleanExpression(boolean name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanExpression that = (BooleanExpression) o;
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
