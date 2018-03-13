package statements;


import expressions.ConditionalExpression;
import expressions.Expression;
import expressions.LoopExpression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;

/**
 * Parselet for the condition or "ternary" operator, like "a ? b : c".
 */
public class WhileParselet implements StatementParselet{

    public Expression parse(Parser parser, Token token) {
        Expression condition = parser.parseExpression();
        Expression body = null;
        if(parser.match(TokenType.TOK_OPEN_CURLY)){
            body = parser.parseStatement();
            parser.match(TokenType.TOK_CLOSE_CURLY);
        }

        return new LoopExpression(condition, body);

    }

}