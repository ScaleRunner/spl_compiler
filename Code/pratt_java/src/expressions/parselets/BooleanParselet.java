package expressions.parselets;

import expressions.BooleanExpression;
import expressions.Expression;
import lexer.Token;
import lexer.TokenBool;
import parser.Parser;

/**
 * Simple parselet for a an integer variable: 1, 2, 3 ... 4.
 */
public class BooleanParselet implements PrefixParseletExpression {
    public Expression parse(Parser parser, Token token) {
        TokenBool tok = (TokenBool) token;
        return new BooleanExpression(tok.getValue());
    }
}
