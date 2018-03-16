package expressions;

import lexer.TokenType;
import util.Visitor;

import java.util.Objects;

/**
 * A postfix unary expression like: a.hd
 */
public class PostfixExpression implements Expression {
    public final Expression left;
    public final TokenType operator;

    public PostfixExpression(Expression left, TokenType operator) {
        this.left = left;
        this.operator = operator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostfixExpression that = (PostfixExpression) o;
        return Objects.equals(left, that.left) &&
                operator == that.operator;
    }

    @Override
    public int hashCode() {

        return Objects.hash(left, operator);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}