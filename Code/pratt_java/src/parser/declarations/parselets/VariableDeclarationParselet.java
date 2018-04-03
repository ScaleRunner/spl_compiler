package parser.declarations.parselets;

import parser.expressions.Expression;
import parser.expressions.IdentifierExpression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.exceptions.ParseException;
import parser.declarations.VariableDeclaration;

public class VariableDeclarationParselet {

    public VariableDeclaration parse(Parser parser,  Token token) {
        TokenType varType = token.getType();

        if(parser.lookAhead(0).getType() != TokenType.TOK_IDENTIFIER)
            throw new ParseException(parser, "Identifier in variable declaration is missing");

        IdentifierExpression left = new IdentifierExpression(parser.consume().getStringValue());

        if(!parser.match(TokenType.TOK_ASSIGN))
            throw new ParseException(parser, "Assignment symbol '=' is missing after identifier");

        Expression right = parser.parseExpression();

        if(parser.match(TokenType.TOK_EOL))
            return new VariableDeclaration(varType,left, right);

        throw new ParseException(parser, "Missing ';' at the end of the line.");
    }
}
