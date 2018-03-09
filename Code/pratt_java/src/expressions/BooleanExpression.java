package expressions;

import java.util.Objects;

/**
 * Identifier Expression: abc
 */
public class BooleanExpression implements Expression {

    private final boolean name;

    public BooleanExpression(boolean name) {
        this.name = name;
    }

    public boolean getName() {
        return name;
    }

    public void print(StringBuilder builder) {
        builder.append(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanExpression that = (BooleanExpression) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
