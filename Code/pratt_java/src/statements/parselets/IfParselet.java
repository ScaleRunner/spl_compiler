package statements.parselets;


import expressions.ConditionalExpression;
import expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parselets.BlockParselet;
import parselets.PrefixParselet;
import parser.Parser;

import java.util.List;

/**
 * Parselet for the condition or "ternary" operator, like "a ? b : c".
 */
public class IfParselet implements PrefixParselet {

    public Expression parse(Parser parser, Token token) {
        Expression condition = parser.parseExpression();
        List<Expression> thenArm = null;
        if(parser.match(TokenType.TOK_OPEN_CURLY)){
            thenArm = new BlockParselet().parse(parser, parser.lookAhead(0));
        }

        List<Expression> elseArm = null;

        if(parser.lookAhead(0).getType() == TokenType.TOK_KW_ELSE){
            parser.consume(TokenType.TOK_KW_ELSE);
            if(parser.match(TokenType.TOK_OPEN_CURLY)){
                elseArm = new BlockParselet().parse(parser, parser.lookAhead(0));
            }
        }

        if(elseArm != null)
            return new ConditionalExpression(condition, thenArm, elseArm);
        else
            return new ConditionalExpression(condition, thenArm);
    }



}