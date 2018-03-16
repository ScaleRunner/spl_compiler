package parselets;

import expressions.Expression;
import expressions.PrefixExpression;
import lexer.Token;
import parser.Parser;

/**
 * Generic prefix parselet for an unary arithmetic operator. Parses prefix
 * unary "-", "+", "~", and "!" expressions.
 */
public class PrefixOperatorParselet implements PrefixParseletExpression {
    public PrefixOperatorParselet(int precedence) {
        mPrecedence = precedence;
    }

    public Expression parse(Parser parser, Token token) {
        // To handle right-associative operators like "^", we allow a slightly
        // lower precedence when parsing the right-hand side. This will let a
        // parselet with the same precedence appear on the right, which will then
        // take *this* parselet's result as its left-hand argument.
        Expression right = parser.parseExpression(mPrecedence);

        return new PrefixExpression(token.getType(), right);
    }

    public int getPrecedence() {
        return mPrecedence;
    }

    private final int mPrecedence;
}