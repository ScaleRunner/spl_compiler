package expressions;

import util.Visitor;

import java.util.Objects;

/**
 * Conditional Expression: if a then b else c
 */
public class LoopExpression implements Expression {
    public final Expression condition;
    public final Expression body;

    public LoopExpression(
            Expression condition, Expression body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoopExpression that = (LoopExpression) o;
        return Objects.equals(condition, that.condition) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {

        return Objects.hash(condition, body);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
