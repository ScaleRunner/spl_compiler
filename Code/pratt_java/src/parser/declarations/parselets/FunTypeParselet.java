package parser.declarations.parselets;

import lexer.Token;
import lexer.TokenType;
import parser.types.FunType;
import parser.types.Type;
import parser.Parser;
import parser.exceptions.ParseException;

import java.util.List;

public class FunTypeParselet {

    public FunType parse(Parser parser, Token token) {
        List<Type> argsTypes;
        Type returnType;
        Token next;
        //if(token.getType() == TokenType.TOK_KW_ARROW)
            //argsTypes = new ArrayList<>();

        //else
            argsTypes = new TypesParselet().parse(parser, token);

        if(token.getType() != TokenType.TOK_KW_ARROW) {
            if (parser.match(TokenType.TOK_KW_ARROW)) {

            } else
                throw new ParseException(parser, "Missing '->' after argument types");
        }
        next = parser.consume();
        returnType = new ReturnTypeParselet().parse(parser, next);


        return new FunType(argsTypes, returnType);
    }
}
