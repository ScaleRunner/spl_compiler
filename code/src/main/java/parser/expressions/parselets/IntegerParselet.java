package parser.expressions.parselets;

import parser.expressions.Expression;
import parser.expressions.IntegerExpression;
import lexer.Token;
import lexer.TokenInteger;
import parser.Parser;

/**
 * Simple parselet for a an integer variable: 1, 2, 3 ... 4.
 */
public class IntegerParselet implements PrefixParseletExpression {
    public Expression parse(Parser parser, Token token) {
        TokenInteger tok = (TokenInteger) token;
        return new IntegerExpression(tok.getValue());
    }
}
