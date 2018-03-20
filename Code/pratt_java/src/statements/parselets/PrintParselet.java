package statements.parselets;


import expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.exceptions.ParseException;
import parser.exceptions.SemicolonError;
import statements.PrintStatement;
import statements.Statement;

/**
 * Parselet for the condition or "ternary" operator, like "a ? b : c".
 */
public class PrintParselet implements PrefixParseletStatement {

    public Statement parse(Parser parser, Token token) {
        Expression arg = null;
        if (parser.match(TokenType.TOK_OPEN_PARENTHESIS)) {
            if (parser.lookAhead(0).getType() != TokenType.TOK_CLOSE_PARENTHESIS) {
                arg = parser.parseExpression();
            }
            parser.consume(TokenType.TOK_CLOSE_PARENTHESIS);
        } else {
            throw new ParseException(parser, "Print statements should have parentheses around the expression.");
        }

        if (!parser.match(TokenType.TOK_EOL)) {
            throw new SemicolonError(parser);
        }

        return new PrintStatement(arg);
    }


}