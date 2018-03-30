package parser.declarations.parselets;

import lexer.Token;
import parser.FunType.*;
import parser.Parser;
import parser.exceptions.ParseException;

import java.util.List;

public class TypeParselet {

        public Type parse(Parser parser, Token token) {
            Token next;
            switch(token.getType()){
                case TOK_KW_BOOL:
                    return new BoolType(Basic.BOOL);
                case TOK_KW_INT:
                    return (new IntType(Basic.INT));
                case TOK_KW_CHAR:
                    return(new CharType(Basic.CHAR));
                case TOK_OPEN_PARENTHESIS:
                    next = parser.consume();
                    return(new TupleTypeParselet().parse(parser,next));
                case TOK_OPEN_BRACKETS:
                    next = parser.consume();
                    return (new TypeParselet().parse(parser, next));
                default:
                    throw new ParseException(parser, "Invalid argument type");

            }
        }

    public Type parse(Token token) {
        Token next;
        switch(token.getType()){
            case TOK_KW_BOOL:
                return new BoolType(Basic.BOOL);
            case TOK_KW_INT:
                return (new IntType(Basic.INT));
            case TOK_KW_CHAR:
                return(new CharType(Basic.CHAR));


        }
        return null;
    }
}
