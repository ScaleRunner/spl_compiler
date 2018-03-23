package parser.expressions.parselets;

import parser.expressions.Expression;
import lexer.Token;
import parser.Parser;

/**
 * The InfixParseletExpression handles parser.expressions which are in between arguments
 */
public interface InfixParseletExpression {
    Expression parse(Parser parser, Expression left, Token token);

    int getPrecedence();
}
