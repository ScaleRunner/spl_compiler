package parser.statements;

import parser.expressions.Expression;
import util.Visitor;

import java.util.Objects;

public class ReturnStatement extends Statement {

    public final Expression arg;

    public ReturnStatement(Expression arg) {
        this.arg = arg;
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
        return Objects.equals(arg, that.arg);
    }

    @Override
    public int hashCode() {

        return Objects.hash(arg);
    }
}
