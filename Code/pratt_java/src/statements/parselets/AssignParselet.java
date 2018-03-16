package statements.parselets;

import statements.AssignStatement;
import expressions.Expression;
import expressions.IdentifierExpression;
import lexer.Token;
import lexer.TokenType;
import parser.ParseException;
import parser.Parser;
import parser.Precedence;
import statements.Statement;


/**
 * Parses assignment expressions like "a = b". The left side of an assignment
 * expression must be a simple name like "a", and expressions are
 * right-associative. (In other words, "a = b = c" is parsed as "a = (b = c)").
 */
public class AssignParselet implements InfixParseletStatement {

    public Statement parse(Parser parser, Expression left, Token token) {
        Expression right = parser.parseExpression(Precedence.ASSIGNMENT - 1);

        if (!parser.match(TokenType.TOK_SEMI_COLON))
            throw new ParseException("There mush be a ';' in the end of an assignemnt");

        if (!(left instanceof IdentifierExpression))
            throw new ParseException("The left-hand side of an assignment must be a name.");


        return new AssignStatement(left, right);
    }
}