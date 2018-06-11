package parser.expressions.parselets;

import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.Precedence;
import parser.exceptions.ParseException;
import parser.expressions.*;


import java.util.ArrayList;
import java.util.List;

import parser.expressions.CallExpression;
import parser.expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.Precedence;

import java.util.ArrayList;
import java.util.List;

/**
 * Parselet to parse a function call like "a(b, c, d)".
 */

public class isEmptyParselet implements PrefixParseletExpression{
    public Expression parse(Parser parser, Token token) {
        // Parse the comma-separated arguments until we hit, ")".
        Expression arg;

        // There may be no arguments at all.
        if(!parser.match(TokenType.TOK_OPEN_PARENTHESIS)){
            throw new ParseException(parser,"Expected '(' after isEmpty keyword");
        }
        arg = parser.parseExpression();
        if(!parser.match(TokenType.TOK_CLOSE_PARENTHESIS)){
            throw new ParseException(parser,"Expected ')' after isEmpty argument");
        }

        return new isEmptyExpression(arg);
    }

    public int getPrecedence() {
        return Precedence.CALL;
    }

}