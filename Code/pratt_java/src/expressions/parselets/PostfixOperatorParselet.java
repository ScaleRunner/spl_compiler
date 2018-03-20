package expressions.parselets;

import expressions.Expression;
import expressions.PostfixExpression;
import lexer.Token;
import parser.Parser;

/**
 * Generic postfix parselet for an unary operator like: a.hd
 */
public class PostfixOperatorParselet implements InfixParseletExpression {
    public PostfixOperatorParselet(int precedence) {
        mPrecedence = precedence;
    }

    public Expression parse(Parser parser, Expression left, Token token) {
        return new PostfixExpression(left, token.getType());
    }

    public int getPrecedence() {
        return mPrecedence;
    }

    private final int mPrecedence;
}