package parser.expressions.parselets;

import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.Precedence;
import parser.exceptions.ParseException;
import parser.expressions.Expression;
import parser.expressions.ReadExpression;
import parser.expressions.isEmptyExpression;

/**
 * Parselet to parse a read function "read()".
 */

public class ReadParselet implements PrefixParseletExpression{
    public Expression parse(Parser parser, Token token) {
        //TODO: What does this function take? Right now it just has 1 argument.
        Expression arg;

        if(!parser.match(TokenType.TOK_OPEN_PARENTHESIS)){
            throw new ParseException(parser,"Expected '(' after read keyword");
        }
        arg = parser.parseExpression();

        if(!parser.match(TokenType.TOK_CLOSE_PARENTHESIS)){
            throw new ParseException(parser,"Expected ')' after read argument");
        }

        return new ReadExpression(arg);
    }

    public int getPrecedence() {
        return Precedence.CALL;
    }

}