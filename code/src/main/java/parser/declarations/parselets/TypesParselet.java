package parser.declarations.parselets;

import lexer.Token;
import lexer.TokenType;
import parser.types.Type;
import parser.Parser;

import java.util.ArrayList;
import java.util.List;

public class TypesParselet {
    List<Type> types;

    public TypesParselet(){
        this.types = new ArrayList<>();

    }

    public List<Type> parse(Parser parser, Token token){
        TypeParselet typeparser = new TypeParselet();
        while( (token.getType() == TokenType.TOK_KW_BOOL||
                token.getType() == TokenType.TOK_KW_INT||
                token.getType() == TokenType.TOK_KW_CHAR||
                token.getType() == TokenType.TOK_OPEN_PARENTHESIS||
                token.getType() == TokenType.TOK_OPEN_BRACKETS

                ) && (token.getType() != TokenType.TOK_EOF) ){

            types.add(typeparser.parse(parser,token));
            if(parser.lookAhead(0).getType() == TokenType.TOK_KW_ARROW)
                break;
            token = parser.consume();
        }

        return types;

    }
}
