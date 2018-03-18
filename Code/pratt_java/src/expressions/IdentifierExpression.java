package expressions;

import lexer.Token;
import parselets.PostfixOperatorParselet;
import parser.Parser;
import parser.Precedence;
import util.Visitor;

import java.util.Objects;

/**
 * Identifier Expression: abc
 */
public class IdentifierExpression implements Expression {

    public final String name;

    public IdentifierExpression(String name) {
        this.name = name;
    }

    public static Expression parseFields(Parser p, Expression expr) {
        do {
            Token field = p.consume();
            expr = new PostfixOperatorParselet(Precedence.POSTFIX).parse(p, expr, field);
        } while (p.fieldAhead());
        return expr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifierExpression that = (IdentifierExpression) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
