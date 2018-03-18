package statements.parselets;


import expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.ParseException;
import parser.Parser;
import statements.ReturnStatement;
import statements.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * Parselet for the condition or "ternary" operator, like "a ? b : c".
 */
public class ReturnParselet implements PrefixParseletStatement {

    public Statement parse(Parser parser, Token token) {
        List<Expression> args = new ArrayList<>();
        if (parser.match(TokenType.TOK_OPEN_PARENTHESIS)) {
            if (parser.lookAhead(0).getType() != TokenType.TOK_CLOSE_PARENTHESIS) {
                //TODO: RECOGNIZE MULTIPLE EXPRESSIONS
                args.add(parser.parseExpression());
//                parser.match(TokenType.TOK_COMMA);
            }
            parser.consume(TokenType.TOK_CLOSE_PARENTHESIS);
        } else {
            throw new ParseException("Return statements should have parentheses around the expression.");
        }

        if (!parser.match(TokenType.TOK_EOL)) {
            throw new ParseException("Return statements should be ended with a ';'");
        }

        return new ReturnStatement(args);
    }


}