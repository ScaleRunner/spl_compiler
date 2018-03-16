package parser;

import expressions.AssignExpression;
import expressions.CallExpression;
import expressions.Expression;
import expressions.PostfixExpression;
import lexer.Token;
import lexer.TokenType;
import parselets.*;
import statements.IfParselet;
import statements.StatementParselet;
import statements.WhileParselet;

import java.util.*;

public class Parser {

    private final Iterator<Token> mTokens;
    private final List<Token> mRead = new ArrayList<>();
    private final Map<TokenType, PrefixParselet> mPrefixParselets = new HashMap<>();
    private final Map<TokenType, InfixParselet> mInfixParselets = new HashMap<>();

    private final Map<TokenType, PrefixParselet> mIdParselets = new HashMap<>();
    private final Map<TokenType, InfixParselet> mFieldAssignParselets = new HashMap<>();
    //private final Map<TokenType, StatementParselet> mStatementParselets = new HashMap<>();

    public Parser(List<Token> tokens) {
        this.mTokens = tokens.iterator();
        setup_parser();
    }
    private void registerPrefix(TokenType token, PrefixParselet parselet){
        mPrefixParselets.put(token, parselet);
    }

    private void registerInfix(TokenType type, InfixParselet parselet){
        mInfixParselets.put(type, parselet);
    }

    private void registerId(TokenType type, PrefixParselet parselet){

        mIdParselets.put(type, parselet);
    }

    private void registerFieldAssign(TokenType type, InfixParselet parselet){

        mFieldAssignParselets.put(type, parselet);
    }

//    private void registerStatement(TokenType type, StatementParselet parselet){
//        mStatementParselets.put(type, parselet);
//    }

    /**
     * This function registers all the rules in the grammer.
     * The precedences are taken from the official Java Documentation.
     * @see <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html">Java Operators</a>
     */
    private void setup_parser(){
        // Register Prefixes
//        registerPrefix(TokenType.TOK_PLUS, new PrefixOperatorParselet(prefix_precedence));
        registerPrefix(TokenType.TOK_MINUS, new PrefixOperatorParselet(Precedence.UNARY));
        registerPrefix(TokenType.TOK_NOT, new PrefixOperatorParselet(Precedence.UNARY));

        // Register Operations
        registerInfix(TokenType.TOK_PLUS, new BinaryOperatorParselet(Precedence.ADDITIVE, false));
        registerInfix(TokenType.TOK_MINUS, new BinaryOperatorParselet(Precedence.ADDITIVE, false));
        registerInfix(TokenType.TOK_MULT, new BinaryOperatorParselet(Precedence.MULTIPLICATIVE, false));
        registerInfix(TokenType.TOK_DIV, new BinaryOperatorParselet(Precedence.MULTIPLICATIVE, false));
        registerInfix(TokenType.TOK_MOD, new BinaryOperatorParselet(Precedence.MULTIPLICATIVE, false));

        // Register Comparisons
        registerInfix(TokenType.TOK_EQ, new BinaryOperatorParselet(Precedence.EQUALITY, false));
        registerInfix(TokenType.TOK_NEQ, new BinaryOperatorParselet(Precedence.EQUALITY, false));

        registerInfix(TokenType.TOK_GEQ, new BinaryOperatorParselet(Precedence.COMPARISON, false));
        registerInfix(TokenType.TOK_LEQ, new BinaryOperatorParselet(Precedence.COMPARISON, false));
        registerInfix(TokenType.TOK_GT, new BinaryOperatorParselet(Precedence.COMPARISON, false));
        registerInfix(TokenType.TOK_LT, new BinaryOperatorParselet(Precedence.COMPARISON, false));

        // Register Logical AND and OR
        registerInfix(TokenType.TOK_AND, new BinaryOperatorParselet(Precedence.AND, false));
        registerInfix(TokenType.TOK_OR, new BinaryOperatorParselet(Precedence.OR, false));

        // Register Other Rules
        //registerInfix(TokenType.TOK_KW_IF, new ConditionalParselet());
        registerPrefix(TokenType.TOK_INT, new IntegerParselet());
        registerPrefix(TokenType.TOK_IDENTIFIER, new IdentifierParselet());
        registerPrefix(TokenType.TOK_BOOL, new BooleanParselet());
        //registerInfix(TokenType.TOK_ASSIGN, new AssignParselet());
        registerPrefix(TokenType.TOK_OPEN_PARENTESIS, new GroupParselet());
        //registerPrefix(TokenType.TOK_OPEN_CURLY, new BlockParselet());
        registerInfix(TokenType.TOK_OPEN_PARENTESIS, new CallParselet());

        // Register Fields
        registerInfix(TokenType.TOK_HD, new PostfixOperatorParselet(Precedence.POSTFIX));
        registerInfix(TokenType.TOK_TL, new PostfixOperatorParselet(Precedence.POSTFIX));
        registerInfix(TokenType.TOK_FST, new PostfixOperatorParselet(Precedence.POSTFIX));
        registerInfix(TokenType.TOK_SND, new PostfixOperatorParselet(Precedence.POSTFIX));

        //registerStatement(TokenType.TOK_KW_IF, new IfParselet());

        registerId(TokenType.TOK_IDENTIFIER, new IdentifierParselet());
        // Register Fields
        registerFieldAssign(TokenType.TOK_HD, new PostfixOperatorParselet(Precedence.POSTFIX));
        registerFieldAssign(TokenType.TOK_TL, new PostfixOperatorParselet(Precedence.POSTFIX));
        registerFieldAssign(TokenType.TOK_FST, new PostfixOperatorParselet(Precedence.POSTFIX));
        registerFieldAssign(TokenType.TOK_SND, new PostfixOperatorParselet(Precedence.POSTFIX));
        registerFieldAssign(TokenType.TOK_ASSIGN, new AssignParselet());
    }

    public ArrayList<Expression> parseBlock(){
        ArrayList<Expression> statements = new ArrayList<>();
        while(!lookAhead(0).getType().equals(TokenType.TOK_EOF)){
            Expression expr = parseStatement();
            statements.add(expr);
            if ((lookAhead(0).getType().equals(TokenType.TOK_EOF)))
                //consume();
                break;
        }

        return statements;
    }

    /**
     * Parses expressions
     * @param precedence precedence value
     * @return ABST
     */
    public Expression parseExpression(int precedence) {
        Token token = consume();
        PrefixParselet prefix = mPrefixParselets.get(token.getType());

        if (prefix == null) throw new ParseException(
                String.format("There was an error parsing '%s'.", token.toString()));

        Expression left = prefix.parse(this, token);

        while (precedence < getPrecedence()) {
            token = consume();

            InfixParselet infix = mInfixParselets.get(token.getType());
            left = infix.parse(this, left, token);
        }
        return left;
    }

    public Expression parseStatement() {
        int lookahead = 0;
        if (match(TokenType.TOK_KW_IF)) return (new IfParselet().parse(this, lookAhead(0)));
        if (match(TokenType.TOK_KW_WHILE)) return (new WhileParselet().parse(this, lookAhead(0)));
        if (lookAhead(lookahead).getType() == TokenType.TOK_IDENTIFIER) {
            Token id = consume();
            Expression left = new IdentifierParselet().parse(this, id);
            //consume();

            if (lookAhead(lookahead).getType().compareTo(TokenType.TOK_OPEN_PARENTESIS) == 0){


                Expression funcall = new CallParselet().parse(this, left,lookAhead(lookahead++));
                while (lookahead>0){
                    consume();
                    lookahead--;

                }
                if (match(TokenType.TOK_SEMI_COLON))
                    return funcall;
            }
            else{
                while ((lookAhead(0).getType().compareTo(TokenType.TOK_HD) == 0) ||
                       (lookAhead(0).getType().compareTo(TokenType.TOK_TL) == 0) ||
                       (lookAhead(0).getType().compareTo(TokenType.TOK_FST) == 0) ||
                       (lookAhead(0).getType().compareTo(TokenType.TOK_SND) == 0)  ){
                        //Expression right =  (parseExpression());
                    Token field = consume();
                    left = new PostfixOperatorParselet(Precedence.POSTFIX).parse(this, left, field);

                    }
                    //else throw new ParseException(String.format("There was an error parsing '%s'.", lookAhead(0).toString()));
                }

                if (lookAhead(lookahead).getType().compareTo(TokenType.TOK_ASSIGN)== 0) {
                    lookahead++;
                    while (lookahead>0){
                        consume();
                        lookahead--;

                    }
                    Expression right =  (parseExpression());
                    if (!match(TokenType.TOK_SEMI_COLON))
                        throw new ParseException(String.format("Expected ';' and found '%s'.", lookAhead(0).toString()));
                    return new AssignExpression(left, right);
                }



            }

        throw new ParseException("No statement could be parsed " + lookAhead(0) );
        //return parseExpression(0);
    }


    public Expression parseExpression() {
        return parseExpression(0);
    }

    public boolean match(TokenType expected) {
        Token token = lookAhead(0);
        if (token.getType() != expected) {
            return false;
        }

        consume();
        return true;
    }

    public Token consume(TokenType expected) {
        Token token = lookAhead(0);
        if (token.getType() != expected) {
            throw new RuntimeException(
                    String.format("Expected token: \t %s \n Found token: \t %s",
                            expected, token.getType())
            );
        }

        return consume();
    }

    private Token consume() {
        // Make sure we've read the token.
        lookAhead(0);

        return mRead.remove(0);
    }

    public Token lookAhead(int distance) {
        // Read in as many as needed.
        while (distance >= mRead.size()) {
            mRead.add(mTokens.next());
        }

        // Get the queued token.
        return mRead.get(distance);
    }

    private int getPrecedence() {
        InfixParselet parser = mInfixParselets.get(lookAhead(0).getType());
        if (parser != null){
            return parser.getPrecedence();
        }

        return 0;
    }
}
