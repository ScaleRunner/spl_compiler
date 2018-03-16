package expressions;

import util.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Conditional Expression: if a then b else c
 */
public class ConditionalExpression implements Expression {
    public final Expression condition;
    public final List<Expression> then_expression;
    public final List<Expression> else_expression;

    public ConditionalExpression(
            Expression condition, List<Expression> then_expression, List<Expression> else_expression) {
        this.condition = condition;
        this.then_expression = then_expression;
        this.else_expression = else_expression;
    }

    public ConditionalExpression(
            Expression condition, List<Expression> then_expression) {
        this.condition = condition;
        this.then_expression = then_expression;
        this.else_expression = new ArrayList<>();
    }

    /**
     * Prints a conditional expression as:
     *         (if (CONDITIONAL) {EXPRESSION} else {EXPRESSION})
     * @param v visitor
     */
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConditionalExpression that = (ConditionalExpression) o;
        return Objects.equals(condition, that.condition) &&
                Objects.equals(then_expression, that.then_expression) &&
                Objects.equals(else_expression, that.else_expression);
    }

    @Override
    public int hashCode() {

        return Objects.hash(condition, then_expression, else_expression);
    }
}
