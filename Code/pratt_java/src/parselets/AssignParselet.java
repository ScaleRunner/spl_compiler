package parselets;

import expressions.AssignExpression;
import expressions.Expression;
import expressions.IdentifierExpression;
import lexer.Token;
import parser.ParseException;
import parser.Parser;
import parser.Precedence;


/**
 * Parses assignment expressions like "a = b". The left side of an assignment
 * expression must be a simple name like "a", and expressions are
 * right-associative. (In other words, "a = b = c" is parsed as "a = (b = c)").
 */
public class AssignParselet implements InfixParselet {
    public Expression parse(Parser parser, Expression left, Token token) {
        Expression right = parser.parseExpression(Precedence.ASSIGNMENT - 1);

        if (!(left instanceof IdentifierExpression))
            throw new ParseException("The left-hand side of an assignment must be a name.");

        String name = ((IdentifierExpression) left).getName();
        return new AssignExpression(name, right);
    }

    public int getPrecedence() {
        return Precedence.ASSIGNMENT;
    }
}