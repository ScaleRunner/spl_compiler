package parser;

import expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parselets.*;
import statements.Statement;
import statements.parselets.*;

import java.util.*;

public class Parser {

    private final Iterator<Token> mTokens;
    private final List<Token> mRead = new ArrayList<>();
    private final Map<TokenType, PrefixParseletExpression> mPrefixParselets = new HashMap<>();
    private final Map<TokenType, InfixParseletExpression> mInfixParselets = new HashMap<>();

    private final Map<TokenType, InfixParseletStatement> mInfixParseletsStatement = new HashMap<>();
    private final Map<TokenType, PrefixParseletStatement> mPrefixParseletsStatement = new HashMap<>();


    public Parser(List<Token> tokens) {
        this.mTokens = tokens.iterator();
        setup_parser();
    }
    private void registerPrefix(TokenType token, PrefixParseletExpression parselet){
        mPrefixParselets.put(token, parselet);
    }

    private void registerInfix(TokenType type, InfixParseletExpression parselet){
        mInfixParselets.put(type, parselet);
    }

    private void registerInfixStatement(TokenType type, InfixParseletStatement parselet){
        mInfixParseletsStatement.put(type, parselet);
    }

    private void registerPrefixStatement(TokenType type, PrefixParseletStatement parselet){
        mPrefixParseletsStatement.put(type, parselet);
    }

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
        registerPrefix(TokenType.TOK_INT, new IntegerParselet());
        registerPrefix(TokenType.TOK_IDENTIFIER, new IdentifierParselet());
        registerPrefix(TokenType.TOK_BOOL, new BooleanParselet());
        registerPrefix(TokenType.TOK_OPEN_PARENTESIS, new GroupParselet());
        //registerPrefix(TokenType.TOK_OPEN_CURLY, new BlockParselet());
        registerInfix(TokenType.TOK_OPEN_PARENTESIS, new CallParselet());

        // Register Fields
        registerInfix(TokenType.TOK_HD, new PostfixOperatorParselet(Precedence.POSTFIX));
        registerInfix(TokenType.TOK_TL, new PostfixOperatorParselet(Precedence.POSTFIX));
        registerInfix(TokenType.TOK_FST, new PostfixOperatorParselet(Precedence.POSTFIX));
        registerInfix(TokenType.TOK_SND, new PostfixOperatorParselet(Precedence.POSTFIX));

        // Register Statements
        registerInfixStatement(TokenType.TOK_ASSIGN, new AssignParselet());
        registerPrefixStatement(TokenType.TOK_KW_IF, new ConditionalParselet());
        //TODO: WHILE
    }

    public ArrayList<Statement> parseBlock(){
        ArrayList<Statement> statements = new ArrayList<>();
        while(!lookAhead(0).getType().equals(TokenType.TOK_EOF)){
            Statement expr = parseStatement();
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
        PrefixParseletExpression prefix = mPrefixParselets.get(token.getType());

        if (prefix == null) throw new ParseException(
                String.format("There was an error parsing '%s'.", token.toString()));

        Expression left = prefix.parse(this, token);

        while (precedence < getPrecedence()) {
            token = consume();

            InfixParseletExpression infix = mInfixParselets.get(token.getType());
            left = infix.parse(this, left, token);
        }
        return left;
    }

    public Statement parseStatement() {
        int lookahead = 0;
        if (lookAhead(0).getType() == TokenType.TOK_KW_IF) {
            Token token = consume();
            PrefixParseletStatement prefix = mPrefixParseletsStatement.get(token.getType());
            return prefix.parse(this, token);

//            return (new ConditionalParselet().parse(this, lookAhead(0)));
        }
        if (match(TokenType.TOK_KW_WHILE)) return (new WhileParselet().parse(this, lookAhead(0)));
        if (lookAhead(lookahead).getType() == TokenType.TOK_IDENTIFIER) {
            Token id = consume();
            Expression left = new IdentifierParselet().parse(this, id);
            //consume();

            if (lookAhead(lookahead).getType() == TokenType.TOK_OPEN_PARENTESIS){

                consume();
                throw new UnsupportedOperationException("Function call not working right now...");
//                Statement funcall = new CallParselet().parse(this, left,lookAhead(0));
//
//                if (match(TokenType.TOK_SEMI_COLON))
//                    return funcall;
//                else throw new ParseException(String.format("Expected ';' and found '%s'.", lookAhead(0).toString()));

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

                if (lookAhead(lookahead).getType() == TokenType.TOK_ASSIGN) {
                    Token tok = consume();
                    InfixParseletStatement parselet =  mInfixParseletsStatement.get(tok.getType());
                    return parselet.parse(this, left, tok);
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
        InfixParseletExpression parser = mInfixParselets.get(lookAhead(0).getType());
        if (parser != null){
            return parser.getPrecedence();
        }

        return 0;
    }
}
