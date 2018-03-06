package parselets;

import expressions.CallExpression;
import expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.Precedence;

import java.util.ArrayList;
import java.util.List;

/**
 * Parselet to parse a function call like "a(b, c, d)".
 */
public class CallParselet implements InfixParselet {
    public Expression parse(Parser parser, Expression left, Token token) {
        // Parse the comma-separated arguments until we hit, ")".
        List<Expression> args = new ArrayList<Expression>();

        // There may be no arguments at all.
        if (!parser.match(TokenType.TOK_CLOSE_PARENTESIS)) {
            do {
                args.add(parser.parseExpression());
            } while (parser.match(TokenType.TOK_COMMA));
            parser.consume(TokenType.TOK_CLOSE_PARENTESIS);
        }

        return new CallExpression(left, args);
    }

    public int getPrecedence() {
        return Precedence.CALL;
    }
}