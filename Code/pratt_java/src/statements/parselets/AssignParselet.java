package statements.parselets;

import expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.Precedence;
import parser.exceptions.ParseException;
import parser.exceptions.SemicolonError;
import statements.AssignStatement;
import statements.Statement;


/**
 * Parses assignment expressions like "a = b". The left side of an assignment
 * expression must be a simple name like "a", and expressions are
 * right-associative. (In other words, "a = b = c" is parsed as "a = (b = c)").
 */
public class AssignParselet implements InfixParseletStatement {

    public Statement parse(Parser parser, Expression left, Token token) {
        Expression right = parser.parseExpression(Precedence.ASSIGNMENT - 1);

        if (parser.match(TokenType.TOK_CLOSE_PARENTHESIS))
            throw new ParseException(parser, "Unbalanced Parentheses! There might be too many ')' tokens.");
        if (!parser.match(TokenType.TOK_EOL)) throw new SemicolonError(parser);

        return new AssignStatement(left, right);
    }
}