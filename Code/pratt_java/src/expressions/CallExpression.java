package expressions;

import java.util.List;
import java.util.Objects;

/**
 * Function Call: foo(bar)
 */
public class CallExpression implements Expression {
    private final Expression function_name;
    private final List<Expression> args;

    public CallExpression(Expression function, List<Expression> args) {
        function_name = function;
        this.args = args;
    }

    public void print(StringBuilder builder) {
        function_name.print(builder);
        builder.append("(");
        for (int i = 0; i < args.size(); i++) {
            args.get(i).print(builder);
            if (i < args.size() - 1) builder.append(", ");
        }
        builder.append(")");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallExpression that = (CallExpression) o;
        return Objects.equals(function_name, that.function_name) &&
                Objects.equals(args, that.args);
    }

    @Override
    public int hashCode() {

        return Objects.hash(function_name, args);
    }
}
