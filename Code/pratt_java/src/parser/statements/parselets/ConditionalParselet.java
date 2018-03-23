package parser.statements.parselets;


import parser.statements.ConditionalStatement;
import parser.expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.statements.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * Parselet for the condition or "ternary" operator, like "a ? b : c".
 */
public class ConditionalParselet implements PrefixParseletStatement {

    public Statement parse(Parser parser, Token token) {
        Expression condition = parser.parseExpression();
        List<Statement> thenArm = new ArrayList<>();
        if(parser.match(TokenType.TOK_OPEN_CURLY)){
            thenArm = new BlockParselet().parse(parser, parser.lookAhead(0));
        }

        List<Statement> elseArm = new ArrayList<>();

        if(parser.lookAhead(0).getType() == TokenType.TOK_KW_ELSE){
            parser.consume(TokenType.TOK_KW_ELSE);
            if(parser.match(TokenType.TOK_OPEN_CURLY)){
                elseArm = new BlockParselet().parse(parser, parser.lookAhead(0));
            }
        }

        return new ConditionalStatement(condition, thenArm, elseArm);
    }



}