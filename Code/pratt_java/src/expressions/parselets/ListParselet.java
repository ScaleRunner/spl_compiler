package expressions.parselets;

import expressions.Expression;
import expressions.ListExpression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;

/**
 * Simple parselet for an empty list: []
 */
public class ListParselet implements PrefixParseletExpression {
    public Expression parse(Parser parser, Token token) {
        parser.consume(TokenType.TOK_CLOSE_BRACKETS);
        return new ListExpression();
    }
}
