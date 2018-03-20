package expressions.parselets;

import expressions.Expression;
import expressions.TupleExpression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;

/**
 * Parses parentheses used to group an expression, like "a * (b + c)".
 */
public class ParenthesisParselet implements PrefixParseletExpression {
    public Expression parse(Parser parser, Token token) {
        Expression expression = parser.parseExpression();
        Expression right = null;
        if (parser.match(TokenType.TOK_CLOSE_PARENTHESIS)) {
            return expression;
        }
        if (parser.match(TokenType.TOK_COMMA)) {
            right = parser.parseExpression();
        }
        parser.consume(TokenType.TOK_CLOSE_PARENTHESIS);
        return new TupleExpression(expression, right);
    }
}
