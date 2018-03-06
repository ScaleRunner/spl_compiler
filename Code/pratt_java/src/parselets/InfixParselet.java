package parselets;

import expressions.Expression;
import lexer.Token;
import parser.Parser;

/**
 * The InfixParselet handles expressions which are in between arguments
 */
public interface InfixParselet {
    Expression parse(Parser parser, Expression left, Token token);

    int getPrecedence();
}
