package parser.declarations.parselets;

import lexer.Token;
import lexer.TokenType;
import parser.FunType.*;
import parser.Parser;
import parser.exceptions.ParseException;

public class ReturnTypeParselet {
    public Type parse(Parser parser, Token token) {
        Token next;
        switch(token.getType()){
            case TOK_KW_BOOL:
                return new BoolType(Basic.BOOL);
            case TOK_KW_INT:
                return  (new IntType(Basic.INT));
            case TOK_KW_CHAR:
                return (new CharType(Basic.CHAR));
            case TOK_OPEN_PARENTHESIS:
                next = parser.consume();
                return (new TupleTypeParselet().parse(parser,next));
            case TOK_OPEN_BRACKETS:
                next = parser.consume();
                return (new TypeParselet().parse(parser, next));
            case TOK_KW_VOID:
                return new VoidType(Return.VOID);
            default:
                throw new ParseException(parser, "Invalid argument type");

        }
    }
}
