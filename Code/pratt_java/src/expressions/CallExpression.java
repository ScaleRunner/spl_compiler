package expressions;

import parser.CallException;
import util.Visitor;

import java.util.List;
import java.util.Objects;

/**
 * Function Call: foo(bar)
 */
public class CallExpression implements Expression {
    public final IdentifierExpression function_name;
    public final List<Expression> args;

    public CallExpression(Expression function, List<Expression> args) {
        try {
            function_name = (IdentifierExpression) function;
        } catch (ClassCastException e){
            throw new CallException(
                    String.format("Could not call %s as a function. Perhaps there is an operator missing?", function)
            );
        }
        this.args = args;
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

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
