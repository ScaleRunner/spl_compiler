package parser.declarations.parselets;

import lexer.Token;
import lexer.TokenType;
import parser.FunType.*;
import parser.Parser;
import parser.exceptions.ParseException;

public class TupleTypeParselet {
    public TupleType parse(Parser parser, Token token) {

        Type left = new TypeParselet().parse(parser, token);

        if(!parser.match(TokenType.TOK_COMMA)) {
            throw new ParseException(parser, "Tuple listType with missing comma");
        }

        Type right = new TypeParselet().parse(parser, parser.consume());

        if(!parser.match(TokenType.TOK_CLOSE_PARENTHESIS)){
            throw new ParseException(parser, "Tuple listType with missing closing parenthesis.");
        }

        return new TupleType(left, right);
    }

}
