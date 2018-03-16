package expressions;

import java.util.Objects;

/**
 * Assignment: a = b
 */
public class AssignExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public AssignExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public void print(StringBuilder builder) {
        String name = ((IdentifierExpression) left).getName();
        builder.append("(").append(name).append(" = ");
        right.print(builder);
        builder.append(")");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignExpression that = (AssignExpression) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(left, right);
    }
}
