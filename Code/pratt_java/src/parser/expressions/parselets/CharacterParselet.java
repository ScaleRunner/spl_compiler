package parser.expressions.parselets;

import lexer.TokenChar;
import parser.expressions.BooleanExpression;
import parser.expressions.CharacterExpression;
import parser.expressions.Expression;
import lexer.Token;
import lexer.TokenBool;
import parser.Parser;

/**
 * Simple parselet for a character variable: 'a', '1'
 */
public class CharacterParselet implements PrefixParseletExpression {
    public Expression parse(Parser parser, Token token) {
        TokenChar tok = (TokenChar) token;
        return new CharacterExpression(tok.getValue());
    }
}
