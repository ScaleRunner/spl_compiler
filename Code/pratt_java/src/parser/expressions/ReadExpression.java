package parser.expressions;

import util.Visitor;

import java.util.List;
import java.util.Objects;

public class ReadExpression extends Expression {

    public final Expression arg;

    public ReadExpression(Expression arg) {
        this.arg = arg;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReadExpression r = (ReadExpression) o;
        return Objects.equals(arg, r.arg);
    }

    @Override
    public int hashCode() {

        return Objects.hash(arg);
    }
}
