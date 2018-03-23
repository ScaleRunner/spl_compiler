package parser.statements.parselets;


import parser.expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.exceptions.ParseException;
import parser.exceptions.SemicolonError;
import parser.statements.ReturnStatement;
import parser.statements.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * Parselet for the condition or "ternary" operator, like "a ? b : c".
 */
public class ReturnParselet implements PrefixParseletStatement {

    public Statement parse(Parser parser, Token token) {
        List<Expression> args = new ArrayList<>();
        //TODO
        //Return parser.statements do not necessarily need parenthesis.
        //Do we want to keep it like this or make like the original grammar?
        if (parser.match(TokenType.TOK_OPEN_PARENTHESIS)) {
            while (parser.lookAhead(0).getType() != TokenType.TOK_CLOSE_PARENTHESIS) {
                args.add(parser.parseExpression());
                parser.match(TokenType.TOK_COMMA);
            }
            parser.consume(TokenType.TOK_CLOSE_PARENTHESIS);
        } else {
            throw new ParseException(parser, "Return parser.statements should have parentheses around the expression.");
        }

        if (!parser.match(TokenType.TOK_EOL)) {
            throw new SemicolonError(parser);
        }

        return new ReturnStatement(args);
    }


}