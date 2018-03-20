package statements.parselets;

import expressions.Expression;
import lexer.Token;
import parser.Parser;
import statements.Statement;

/**
 * One of the two interfaces used by the Pratt parser. A PrefixParseletExpression is
 * associated with a token that appears at the beginning of an expression. Its
 * parse() method will be called with the consumed leading token, and the
 * parselet is responsible for parsing anything that comes after that token.
 * This interface is also used for single-token expressions like declarations, in
 * which case parse() simply doesn't consume any more tokens.
 *
 * @author rnystrom
 */
public interface PrefixParseletStatement {
    Statement parse(Parser parser, Token token);
}
