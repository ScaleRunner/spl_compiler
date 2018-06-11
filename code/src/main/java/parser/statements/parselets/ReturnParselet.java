package parser.statements.parselets;


import parser.expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.exceptions.ParseException;
import parser.exceptions.SemicolonError;
import parser.statements.ReturnStatement;
import parser.statements.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * Parselet for the return statement, like return 1 + 1;.
 */
public class ReturnParselet implements PrefixParseletStatement {

    public Statement parse(Parser parser, Token token) {
        Expression arg = null;
        if(parser.lookAhead(0).getType() != TokenType.TOK_EOL){
            arg = parser.parseExpression();
        }

        if (!parser.match(TokenType.TOK_EOL)) {
            throw new SemicolonError(parser);
        }

        return new ReturnStatement(arg);
    }


}