package statements.parselets;


import expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.exceptions.ParseException;
import statements.LoopStatement;
import statements.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * Parselet for the While Parselt: while(a==b) {a = b;}
 */
public class WhileParselet implements PrefixParseletStatement {

    public Statement parse(Parser parser, Token token) {
        Expression condition = parser.parseExpression();
        List<Statement> body = new ArrayList<>();
        if(parser.match(TokenType.TOK_OPEN_CURLY)){
            body = parser.parseBlock();
            parser.match(TokenType.TOK_CLOSE_CURLY);
        } else {
            throw new ParseException(parser, "While statements should have a condition, like while(a==b){}");
        }

        return new LoopStatement(condition, body);

    }

}