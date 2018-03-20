package expressions.parselets;

import expressions.Expression;
import expressions.IdentifierExpression;
import lexer.Token;
import lexer.TokenIdentifier;
import parser.Parser;

/**
 * Simple parselet for a named variable like "abc".
 */
public class IdentifierParselet implements PrefixParseletExpression {
    public Expression parse(Parser parser, Token token) {
        TokenIdentifier tok = (TokenIdentifier) token;
        return new IdentifierExpression(tok.getValue());
    }
}
