package parser.expressions;

import util.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * List Expression: []
 */
public class ListExpression extends Expression {

    public ListExpression() {

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
