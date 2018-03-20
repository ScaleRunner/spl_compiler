package expressions.parselets;

import expressions.Expression;
import lexer.Token;
import parser.Parser;

/**
 * The InfixParseletExpression handles expressions which are in between arguments
 */
public interface InfixParseletExpression {
    Expression parse(Parser parser, Expression left, Token token);

    int getPrecedence();
}
