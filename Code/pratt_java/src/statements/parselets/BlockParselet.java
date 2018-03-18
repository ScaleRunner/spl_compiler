package statements.parselets;

import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import statements.Statement;

import java.util.ArrayList;

/**
 * Parses parentheses used to group an expression, like "a * (b + c)".
 */
public class BlockParselet {
    public ArrayList<Statement> parse(Parser parser, Token token) {
        ArrayList<Statement> expressions = new ArrayList<>();
        while (true) {

            // Stop when we hit the closing }.
            if (parser.match(TokenType.TOK_CLOSE_CURLY)) break;
            expressions.add(parser.parseStatement());


            //if (parser.match(TokenType.TOK_EOL)) break;

            // Each expression is separated by a line.
            //parser.consume(TokenType.TOK_EOL);
        }

        return expressions;
    }
}
