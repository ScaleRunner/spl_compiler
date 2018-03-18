package statements.parselets;

import expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.ParseException;
import parser.Parser;
import parser.Precedence;
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

        if (!parser.match(TokenType.TOK_EOL))
            throw new ParseException("There mush be a ';' at the end of an assignment.");

        return new AssignStatement(left, right);
    }
}