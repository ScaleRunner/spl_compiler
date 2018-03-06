package parselets;

import expressions.Expression;
import expressions.NameExpression;
import lexer.Token;
import lexer.TokenIdentifier;
import parser.Parser;

/**
 * Simple parselet for a named variable like "abc".
 */
public class NameParselet implements PrefixParselet {
    public Expression parse(Parser parser, Token token) {
        TokenIdentifier tok = (TokenIdentifier) token;
        return new NameExpression(tok.getValue());
    }
}
