package statements;

import expressions.Expression;
import util.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Conditional Expression: if a then b else c
 */
public class ConditionalStatement implements Statement {
    public final Expression condition;
    public final List<Statement> then_expression;
    public final List<Statement> else_expression;

    public ConditionalStatement(
            Expression condition, List<Statement> then_expression, List<Statement> else_expression) {
        this.condition = condition;
        this.then_expression = then_expression;
        this.else_expression = else_expression;
    }

//    public ConditionalStatement(
//            Expression condition, List<Statement> then_expression) {
//        super(condition, then_expression, new ArrayList<Statement>());
//    }

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
        ConditionalStatement that = (ConditionalStatement) o;
        return Objects.equals(condition, that.condition) &&
                Objects.equals(then_expression, that.then_expression) &&
                Objects.equals(else_expression, that.else_expression);
    }

    @Override
    public int hashCode() {

        return Objects.hash(condition, then_expression, else_expression);
    }
}
