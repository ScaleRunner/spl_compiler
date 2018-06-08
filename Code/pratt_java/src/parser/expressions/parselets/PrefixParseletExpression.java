package parser.expressions.parselets;

import lexer.Token;
import parser.Parser;
import parser.expressions.Expression;

/**
 * One of the two interfaces used by the Pratt parser. A PrefixParseletExpression is
 * associated with a token that appears at the beginning of an expression. Its
 * parse() method will be called with the consumed leading token, and the
 * parselet is responsible for parsing anything that comes after that token.
 */
public interface PrefixParseletExpression {
    Expression parse(Parser parser, Token token);
}
