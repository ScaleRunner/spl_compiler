package parselets;

import expressions.Expression;
import expressions.PostfixExpression;
import lexer.Token;
import parser.Parser;

/**
 * Generic infix parselet for an unary arithmetic operator. Parses postfix
 * unary "?" expressions.
 */
public class PostfixOperatorParselet implements InfixParselet {
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