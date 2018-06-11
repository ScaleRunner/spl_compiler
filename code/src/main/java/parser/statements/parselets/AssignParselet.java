package parser.statements.parselets;

import parser.expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.Precedence;
import parser.exceptions.ParseException;
import parser.exceptions.SemicolonError;
import parser.statements.AssignStatement;
import parser.statements.Statement;


/**
 * Parses assignment parser.expressions like "a = b". The left side of an assignment
 * expression must be a simple name like "a", and parser.expressions are
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