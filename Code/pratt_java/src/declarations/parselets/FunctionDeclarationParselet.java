package declarations.parselets;

import declarations.Declaration;
import declarations.FunctionDeclaration;
import expressions.Expression;
import expressions.IdentifierExpression;
import expressions.parselets.PrefixParseletExpression;
import jdk.nashorn.internal.runtime.ParserException;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.exceptions.ParseException;
import statements.Statement;

import java.util.ArrayList;
import java.util.List;

public class FunctionDeclarationParselet {
    public final IdentifierExpression name;
    public final List<IdentifierExpression> args;
    public final List<Declaration> funVarDecl;
    public List<Statement> stats;
    public final List<TokenType> fargsTypes;
    public TokenType returnType;

    public FunctionDeclarationParselet(IdentifierExpression name){
        this.name = name;
        this.args = new ArrayList<>();
        this.funVarDecl = new ArrayList<>();
        this.stats = new ArrayList<>();
        this.fargsTypes = new ArrayList<>();
        this.returnType = null;
    }

    public FunctionDeclaration parse(Parser parser, Token token) {
        if(token.getType() == TokenType.TOK_IDENTIFIER){
//            name = new IdentifierExpression(token.getStringValue());
            if(parser.match(TokenType.TOK_OPEN_PARENTHESIS)){


                // There may be no arguments at all.
                //TODO: Fix no args
                if (!parser.match(TokenType.TOK_CLOSE_PARENTHESIS)) {
                    do {
                        Expression arg = parser.parseExpression();
                        if(arg.getClass() == IdentifierExpression.class){
                            args.add((IdentifierExpression) arg);
                        }
                        else{
                            throw new ParseException(parser, "Arguments have to be identifiers not "+arg.getClass() );
                        }
                    } while (parser.match(TokenType.TOK_COMMA));
                    if(parser.match(TokenType.TOK_CLOSE_PARENTHESIS)){

                        if(parser.match(TokenType.TOK_FUNC_TYPE_DEF)){
                            //TODO: TUPLES, LISTS, IDs
                            while(parser.lookAhead(0).getType() == (TokenType.TOK_KW_BOOL)||
                                    parser.lookAhead(0).getType() ==(TokenType.TOK_KW_INT)||
                                    parser.lookAhead(0).getType() ==(TokenType.TOK_KW_CHAR)){
                                fargsTypes.add(parser.consume().getType());

                            }

                            if(parser.match(TokenType.TOK_KW_ARROW)){
                                if(parser.lookAhead(0).getType() == (TokenType.TOK_KW_BOOL)||
                                        parser.lookAhead(0).getType() ==(TokenType.TOK_KW_INT)||
                                        parser.lookAhead(0).getType() ==(TokenType.TOK_KW_CHAR)||
                                        parser.lookAhead(0).getType() ==(TokenType.TOK_KW_VOID))
                                {
                                    returnType = parser.consume().getType();
                                }
                                else{
                                    throw new ParseException(parser, "Invalid Return type " + parser.getLine());
                                }
                            }
                            else{
                                throw new ParseException(parser, "Missing '->' after argument types. " + parser.getLine());
                            }


                        }

                        if(parser.match(TokenType.TOK_OPEN_CURLY)){
                            while (parser.lookAhead(0).getType() == TokenType.TOK_KW_VAR ||
                                    parser.lookAhead(0).getType() == TokenType.TOK_KW_CHAR ||
                                    parser.lookAhead(0).getType() == TokenType.TOK_KW_INT ||
                                    parser.lookAhead(0).getType() == TokenType.TOK_KW_BOOL) {
                                //PrefixParseletStatement prefix = mPrefixParseletsStatement.get(token.getType());

                                //if (prefix == null) throw new ParseException(this, token);

                                funVarDecl.add(new VariableDeclarationParselet().parse(parser, parser.consume()));
                            }
                            stats = parser.parseBlock();
                            if(stats.size() > 0){
                                if(parser.match(TokenType.TOK_CLOSE_CURLY)){
                                    return new FunctionDeclaration(name, args, funVarDecl, stats, fargsTypes, returnType);
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

            }
            else{
                throw new ParseException(parser, "Missing '(' after function identifier.");
            }
        }
        else {
            throw new ParseException(parser, "Identifier in variable declaration is missing");
        }

        return null;
    }

}
