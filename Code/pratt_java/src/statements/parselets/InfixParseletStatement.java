package statements.parselets;

import expressions.Expression;
import lexer.Token;
import parser.Parser;
import statements.Statement;

/**
 * The InfixParseletExpression handles expressions which are in between arguments
 */
public interface InfixParseletStatement {
    Statement parse(Parser parser, Expression left, Token token);
}
