package expressions;

import java.util.Objects;

public class FieldExpression implements Expression {
    private final String name;

    public FieldExpression(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void print(StringBuilder builder) {
        builder.append(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldExpression that = (FieldExpression) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
