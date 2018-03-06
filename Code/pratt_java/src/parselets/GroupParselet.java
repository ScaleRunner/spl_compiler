package parselets;

import expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;

/**
 * Parses parentheses used to group an expression, like "a * (b + c)".
 */
public class GroupParselet implements PrefixParselet {
    public Expression parse(Parser parser, Token token) {
        Expression expression = parser.parseExpression();
        parser.consume(TokenType.TOK_CLOSE_PARENTESIS);
        return expression;
    }
}
