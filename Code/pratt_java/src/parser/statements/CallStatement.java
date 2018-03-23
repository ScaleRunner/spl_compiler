package parser.statements;

import parser.expressions.CallExpression;
import parser.expressions.Expression;
import parser.expressions.IdentifierExpression;
import util.Visitor;

import java.util.List;
import java.util.Objects;

public class CallStatement extends Statement {
    public final IdentifierExpression function_name;
    public final List<Expression> args;

    public CallStatement(CallExpression callExpression) {
        this.function_name = callExpression.function_name;
        this.args = callExpression.args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallStatement that = (CallStatement) o;
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
