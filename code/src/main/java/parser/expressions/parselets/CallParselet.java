package parser.expressions.parselets;

import parser.exceptions.ParseException;
import parser.expressions.CallExpression;
import parser.expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.Precedence;

import java.util.ArrayList;
import java.util.List;

/**
 * Parselet to parse a function call like "a(b, c, d)".
 */
public class CallParselet implements InfixParseletExpression {
    public Expression parse(Parser parser, Expression left, Token token) {
        // Parse the comma-separated arguments until we hit, ")".
        List<Expression> args = new ArrayList<>();

        // There may be no arguments at all.
        if (!parser.match(TokenType.TOK_CLOSE_PARENTHESIS)) {
            do {
                args.add(parser.parseExpression());
            } while (parser.match(TokenType.TOK_COMMA));
            if(!parser.match(TokenType.TOK_CLOSE_PARENTHESIS)){
                throw new ParseException(parser,"Expected ')' after function arguments");
            }
        }

        return new CallExpression(left, args);
    }

    public int getPrecedence() {
        return Precedence.CALL;
    }
}