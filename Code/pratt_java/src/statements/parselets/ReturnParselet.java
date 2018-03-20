package statements.parselets;


import expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.exceptions.ParseException;
import parser.exceptions.SemicolonError;
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
            throw new ParseException(parser, "Return statements should have parentheses around the expression.");
        }

        if (!parser.match(TokenType.TOK_EOL)) {
            throw new SemicolonError(parser);
        }

        return new ReturnStatement(args);
    }


}