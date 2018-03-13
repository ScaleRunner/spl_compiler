package expressions;

import java.util.Objects;

/**
 * Conditional Expression: if a then b else c
 */
public class LoopExpression implements Expression {
    private final Expression condition;
    private final Expression body;

    public LoopExpression(
            Expression condition, Expression body) {
        this.condition = condition;
        this.body = body;
    }

    /**
     * Prints a conditional expression as:
     *         (if (CONDITIONAL) {EXPRESSION} else {EXPRESSION})
     * @param builder visitor
     */
    public void print(StringBuilder builder) {
        builder.append("(while").append(" (");
        condition.print(builder);
        builder.append(") ");
        builder.append("{");
        body.print(builder);
        builder.append("}");

        builder.append(")");
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
}
