package parser.statements;

import parser.expressions.Expression;
import util.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Conditional Expression: if a then b else c
 */
public class LoopStatement extends Statement {
    public final Expression condition;
    public final List<Statement> body;

    public LoopStatement(
            Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoopStatement that = (LoopStatement) o;
        return Objects.equals(condition, that.condition) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {

        return Objects.hash(condition, body);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
