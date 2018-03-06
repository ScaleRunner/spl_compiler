package parselets;


import expressions.ConditionalExpression;
import expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.Precedence;

/**
 * Parselet for the condition or "ternary" operator, like "a ? b : c".
 */
public class ConditionalParselet implements InfixParselet {
    public Expression parse(Parser parser, Expression left, Token token) {
        Expression thenArm = parser.parseExpression();
        parser.consume(TokenType.TOK_CONS);
        Expression elseArm = parser.parseExpression(Precedence.CONDITIONAL - 1);

        return new ConditionalExpression(left, thenArm, elseArm);
    }

    public int getPrecedence() {
        return Precedence.CONDITIONAL;
    }
}