package parser.statements.parselets;

import parser.expressions.Expression;
import lexer.Token;
import parser.Parser;
import parser.statements.Statement;

/**
 * The InfixParseletExpression handles parser.expressions which are in between arguments
 */
public interface InfixParseletStatement {
    Statement parse(Parser parser, Expression left, Token token);
}
