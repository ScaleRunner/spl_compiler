package expressions;

import java.util.Objects;

/**
 * Identifier Expression: abc
 */
public class IntegerExpression implements Expression {

    private final int name;

    public IntegerExpression(int name) {
        this.name = name;
    }

    public int getName() {
        return name;
    }

    public void print(StringBuilder builder) {
        builder.append(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerExpression that = (IntegerExpression) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
