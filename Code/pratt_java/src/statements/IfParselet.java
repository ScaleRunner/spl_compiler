package statements;


import expressions.ConditionalExpression;
import expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parselets.InfixParselet;
import parser.Parser;
import parser.Precedence;

/**
 * Parselet for the condition or "ternary" operator, like "a ? b : c".
 */
public class IfParselet implements StatementParselet{

    public Expression parse(Parser parser, Token token) {
        Expression condition = parser.parseExpression();
        Expression thenArm = null;
        if(parser.match(TokenType.TOK_OPEN_CURLY)){
            thenArm = parser.parseStatement();
            parser.match(TokenType.TOK_CLOSE_CURLY);
        }

        Expression elseArm = null;

        if(parser.lookAhead(0).getType() == TokenType.TOK_KW_ELSE){
            parser.consume(TokenType.TOK_KW_ELSE);
            elseArm = parser.parseStatement();
        }

        if(elseArm != null)
            return new ConditionalExpression(condition, thenArm, elseArm);
        else
            return new ConditionalExpression(condition, thenArm);
    }

}