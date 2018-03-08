package expressions;

import java.util.Objects;

/**
 * Assignment: a = b
 */
public class AssignExpression implements Expression {
    private final String name;
    private final Expression right;

    public AssignExpression(String name, Expression right) {
        this.name = name;
        this.right = right;
    }

    public void print(StringBuilder builder) {
        builder.append("(").append(name).append(" = ");
        right.print(builder);
        builder.append(")");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignExpression that = (AssignExpression) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, right);
    }
}
