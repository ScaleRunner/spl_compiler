package parser.expressions;

import util.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Identifier Expression: abc
 */
public class ListExpression implements Expression {

    public final List<Expression> items;

    public ListExpression() {
        this.items = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListExpression that = (ListExpression) o;
        return Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {

        return Objects.hash(items);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
