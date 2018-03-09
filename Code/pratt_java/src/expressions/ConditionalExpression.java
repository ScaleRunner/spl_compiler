package expressions;

import java.util.Objects;

/**
 * Conditional Expression: if a then b else c
 */
public class ConditionalExpression implements Expression {
    private final Expression condition;
    private final Expression then_expression;
    private final Expression else_expression;

    public ConditionalExpression(
            Expression condition, Expression then_expression, Expression else_expression) {
        this.condition = condition;
        this.then_expression = then_expression;
        this.else_expression = else_expression;
    }

    /**
     * Prints a conditional expression as:
     *         (if (CONDITIONAL) {EXPRESSION} else {EXPRESSION})
     * @param builder visitor
     */
    public void print(StringBuilder builder) {
        builder.append("(if").append(" (");
        condition.print(builder);
        builder.append(") ");
        builder.append("{");
        then_expression.print(builder);
        builder.append("}");
        if (else_expression != null){
            builder.append(" else ").append("{");
            else_expression.print(builder);
            builder.append("}");
        }

        builder.append(")");
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
