package parser.declarations.parselets;

import lexer.Token;
import lexer.TokenType;
import parser.FunType.*;
import parser.Parser;
import parser.exceptions.ParseException;

public class TupleTypeParselet {
    public TupleType parse(Parser parser, Token token) {
        Token next;
        Type left;
        Type right;

        switch(token.getType()){
            case TOK_KW_BOOL:
                left = new BoolType(Basic.BOOL);
                break;
            case TOK_KW_INT:
                left = (new IntType(Basic.INT));
                break;
            case TOK_KW_CHAR:
                left = (new CharType(Basic.CHAR));
                break;
            case TOK_OPEN_PARENTHESIS:
                next = parser.consume();
                left = (new TupleTypeParselet().parse(parser,next));
                break;
            case TOK_OPEN_BRACKETS:
                next = parser.consume();
                left = (new TypeParselet().parse(parser, next));
                break;
            default:
                throw new ParseException(parser, "Invalid argument type");

        }

        if(parser.match(TokenType.TOK_COMMA)){
            Token rightToken = parser.consume();
            switch(rightToken.getType()){
                case TOK_KW_BOOL:
                    right = new BoolType(Basic.BOOL);
                    break;
                case TOK_KW_INT:
                    right = (new IntType(Basic.INT));
                    break;
                case TOK_KW_CHAR:
                    right = (new CharType(Basic.CHAR));
                    break;
                case TOK_OPEN_PARENTHESIS:
                    next = parser.consume();
                    right = (new TupleTypeParselet().parse(parser,next));
                    break;
                case TOK_OPEN_BRACKETS:
                    next = parser.consume();
                    right = (new TypeParselet().parse(parser, next));
                    break;
                default:
                    throw new ParseException(parser, "Invalid argument type");

            }

            return new TupleType(left, right);


        }
        else{
            throw new ParseException(parser, "Error in parsing, Tuple type with missing comma ',' \n"+parser.getLine());
        }
    }

}
