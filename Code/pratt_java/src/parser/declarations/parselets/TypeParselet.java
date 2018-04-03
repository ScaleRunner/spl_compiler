package parser.declarations.parselets;

import lexer.Token;
import lexer.TokenType;
import parser.FunType.*;
import parser.Parser;
import parser.exceptions.ParseException;

public class TypeParselet {

    public Type parse(Parser parser, Token token) {
        Token next;
        switch(token.getType()){
            case TOK_KW_BOOL:
                return Types.boolType;
            case TOK_KW_INT:
                return Types.intType;
            case TOK_KW_CHAR:
                return Types.charType;
            case TOK_OPEN_PARENTHESIS:
                next = parser.consume();
                return new TupleTypeParselet().parse(parser,next);

            case TOK_OPEN_BRACKETS:
                next = parser.consume();
                Type type = new TypeParselet().parse(parser, next);
                if(parser.match(TokenType.TOK_CLOSE_BRACKETS)){
                    return Types.listType(type);
                }
                throw new ParseException(parser, "Invalid argument listType");

            default:
                throw new ParseException(parser, "Invalid argument listType");

        }
    }
}
