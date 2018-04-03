package parser.declarations.parselets;

import parser.FunType.*;
import parser.declarations.Declaration;
import parser.declarations.FunctionDeclaration;
import parser.declarations.VariableDeclaration;
import parser.expressions.Expression;
import parser.expressions.IdentifierExpression;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.exceptions.ParseException;
import parser.statements.Statement;

import java.util.ArrayList;
import java.util.List;

public class FunctionDeclarationParselet {
    public final IdentifierExpression name;
    public final List<IdentifierExpression> args;
    public final List<VariableDeclaration> funVarDecl;
    public List<Statement> stats;
    public FunType funtype;

    public FunctionDeclarationParselet(IdentifierExpression name){
        this.name = name;
        this.args = new ArrayList<>();
        this.funVarDecl = new ArrayList<>();
        this.stats = new ArrayList<>();
        this.funtype = null;
    }

    public FunctionDeclaration parse(Parser parser, Token token) {
        if(token.getType() == TokenType.TOK_IDENTIFIER){
//            name = new IdentifierExpression(token.getStringValue());
            if(parser.match(TokenType.TOK_OPEN_PARENTHESIS)){


                // There may be no arguments at all.
                
                if (!(parser.lookAhead(0).getType() == TokenType.TOK_CLOSE_PARENTHESIS)) {
                    do {
                        Expression arg = parser.parseExpression();
                        if (arg.getClass() == IdentifierExpression.class) {
                            args.add((IdentifierExpression) arg);
                        } else {
                            throw new ParseException(parser, "Arguments have to be identifiers not " + arg.getClass());
                        }
                    } while (parser.match(TokenType.TOK_COMMA));

                }

                if(parser.match(TokenType.TOK_CLOSE_PARENTHESIS)){

                    if(parser.match(TokenType.TOK_FUNC_TYPE_DEF)){
                        Token tokentype = parser.consume();
                        if( (tokentype.getType() == TokenType.TOK_KW_BOOL)||
                                (tokentype.getType() == TokenType.TOK_KW_INT)||
                                (tokentype.getType() == TokenType.TOK_KW_CHAR)||
                                (tokentype.getType() == TokenType.TOK_OPEN_PARENTHESIS)||
                                (tokentype.getType() == TokenType.TOK_OPEN_BRACKETS)||
                                (tokentype.getType() ==  TokenType.TOK_KW_ARROW)){
                            funtype = new FunTypeParselet().parse(parser,tokentype);

                        }


//                        Token rettypetoken = parser.consume();
//                        if((rettypetoken.getType() ==TokenType.TOK_KW_BOOL)||
//                                (rettypetoken.getType() ==TokenType.TOK_KW_INT)||
//                                (rettypetoken.getType() ==TokenType.TOK_KW_CHAR)||
//                                (rettypetoken.getType() ==TokenType.TOK_KW_VOID))
//                        {
//                            returnType = new ReturnTypeParselet().parse(parser,rettypetoken);
//                        }
//                        else{
//                            throw new ParseException(parser, "Invalid Return listType " + parser.getLine());
//                        }



                    }
                    else{
                        throw new ParseException(parser, "Missing '::' after arguments." + parser.getLine());
                    }

                    if(parser.match(TokenType.TOK_OPEN_CURLY)){
                        while (parser.lookAhead(0).getType() == TokenType.TOK_KW_VAR ||
                                parser.lookAhead(0).getType() == TokenType.TOK_KW_CHAR ||
                                parser.lookAhead(0).getType() == TokenType.TOK_KW_INT ||
                                parser.lookAhead(0).getType() == TokenType.TOK_KW_BOOL ||
                                parser.lookAhead(0).getType() == TokenType.TOK_OPEN_BRACKETS) {
                            //PrefixParseletStatement prefix = mPrefixParseletsStatement.get(token.getType());

                            //if (prefix == null) throw new ParseException(this, token);

                            funVarDecl.add(new VariableDeclarationParselet().parse(parser, parser.consume()));
                        }
                        stats = parser.parseBlock();
                        if(stats.size() > 0){
                            if(parser.match(TokenType.TOK_CLOSE_CURLY)){
                                return new FunctionDeclaration(name, args, funVarDecl, stats, funtype);
                            }
                            else{
                                throw new ParseException(parser, "Missing '}' in function definition.");
                            }

                        }
                        else{
                            throw new ParseException(parser, "Function definition needs at least one statement.");
                        }

                    }
                    else{
                        throw new ParseException(parser, "Missing '{' after arguments definition ");
                    }
                }
                else{
                    throw new ParseException(parser, "Missing ')' at the end of arguments definition");
                }



            }
            else{
                throw new ParseException(parser, "Missing '(' after function identifier.");
            }
        }
        else {
            throw new ParseException(parser, "Identifier in variable declaration is missing");
        }


    }

}
