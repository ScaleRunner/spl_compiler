package parser.expressions.parselets;

import lexer.Token;
import parser.Parser;
import parser.expressions.Expression;

/**
 * The InfixParseletExpression handles expressions which are in between arguments.
 * This parselet is also used to parse CallExpressions and PostfixOperatorExpressions
 */
public interface InfixParseletExpression {
    Expression parse(Parser parser, Expression left, Token token);

    int getPrecedence();
}
