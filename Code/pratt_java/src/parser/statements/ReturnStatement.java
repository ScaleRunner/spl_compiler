package parser.statements;

import parser.expressions.Expression;
import util.Visitor;

import java.util.List;
import java.util.Objects;

public class ReturnStatement extends Statement {

    public final List<Expression> args;

    public ReturnStatement(List<Expression> args) {
        this.args = args;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnStatement that = (ReturnStatement) o;
        return Objects.equals(args, that.args);
    }

    @Override
    public int hashCode() {

        return Objects.hash(args);
    }
}
