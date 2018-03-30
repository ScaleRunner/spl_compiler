package parser.declarations.parselets;

import lexer.Token;
import lexer.TokenType;
import parser.FunType.*;
import parser.Parser;
import parser.exceptions.ParseException;

public class ReturnTypeParselet {
    public Type parse(Parser parser, Token token) {
        Type type;
        try{
            type = new TypeParselet().parse(parser, token);
        } catch (ParseException e){
            if(token.getType() == TokenType.TOK_KW_VOID){
                type = Types.voidType;
            } else {
                throw e;
            }
        }

        return type;
    }
}
