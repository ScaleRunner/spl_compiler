package parser;

import lexer.Token;
import lexer.TokenOther;
import lexer.TokenType;
import parser.declarations.Declaration;
import parser.declarations.parselets.FunctionDeclarationParselet;
import parser.declarations.parselets.VariableDeclarationParselet;
import parser.exceptions.ParseException;
import parser.exceptions.SemicolonError;
import parser.expressions.CallExpression;
import parser.expressions.Expression;
import parser.expressions.IdentifierExpression;
import parser.expressions.parselets.*;
import parser.statements.CallStatement;
import parser.statements.Statement;
import parser.statements.parselets.*;
import util.PrettyPrinter;

import java.util.*;

public class Parser {

    private final List<Token> input;
    private final Iterator<Token> mTokens;
    private int currentToken = 0;
    private final List<Token> mRead = new ArrayList<>();
    private final Map<TokenType, PrefixParseletExpression> mPrefixParseletsExpression = new HashMap<>();
    private final Map<TokenType, InfixParseletExpression> mInfixParseletsExpression = new HashMap<>();

    private final Map<TokenType, InfixParseletStatement> mInfixParseletsStatement = new HashMap<>();
    private final Map<TokenType, PrefixParseletStatement> mPrefixParseletsStatement = new HashMap<>();


    public Parser(List<Token> tokens) {
        this.input = tokens;
        this.mTokens = tokens.iterator();
        setup_parser();
    }

    private void registerPrefixExpression(TokenType token, PrefixParseletExpression parselet) {
        mPrefixParseletsExpression.put(token, parselet);
    }

    private void registerInfixExpression(TokenType type, InfixParseletExpression parselet) {
        mInfixParseletsExpression.put(type, parselet);
    }

    private void registerInfixStatement(TokenType type, InfixParseletStatement parselet){
        mInfixParseletsStatement.put(type, parselet);
    }

    private void registerPrefixStatement(TokenType type, PrefixParseletStatement parselet){
        mPrefixParseletsStatement.put(type, parselet);
    }

    /**
     * This function registers all the rules in the grammar.
     * The precedences are taken from the official Java Documentation.
     * @see <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html">Java Operators</a>
     */
    private void setup_parser(){
        // Register Prefixes
//        registerPrefixExpression(TokenType.TOK_PLUS, new PrefixOperatorParselet(prefix_precedence));
        registerPrefixExpression(TokenType.TOK_MINUS, new PrefixOperatorParselet(Precedence.UNARY));
        registerPrefixExpression(TokenType.TOK_NOT, new PrefixOperatorParselet(Precedence.UNARY));

        // Register Operations
        registerInfixExpression(TokenType.TOK_PLUS, new BinaryOperatorParselet(Precedence.ADDITIVE, false));
        registerInfixExpression(TokenType.TOK_MINUS, new BinaryOperatorParselet(Precedence.ADDITIVE, false));
        registerInfixExpression(TokenType.TOK_CONS, new BinaryOperatorParselet(Precedence.CONS, true));
        registerInfixExpression(TokenType.TOK_MULT, new BinaryOperatorParselet(Precedence.MULTIPLICATIVE, false));
        registerInfixExpression(TokenType.TOK_DIV, new BinaryOperatorParselet(Precedence.MULTIPLICATIVE, false));
        registerInfixExpression(TokenType.TOK_MOD, new BinaryOperatorParselet(Precedence.MULTIPLICATIVE, false));

        // Register Comparisons
        registerInfixExpression(TokenType.TOK_EQ, new BinaryOperatorParselet(Precedence.EQUALITY, false));
        registerInfixExpression(TokenType.TOK_NEQ, new BinaryOperatorParselet(Precedence.EQUALITY, false));

        registerInfixExpression(TokenType.TOK_GEQ, new BinaryOperatorParselet(Precedence.COMPARISON, false));
        registerInfixExpression(TokenType.TOK_LEQ, new BinaryOperatorParselet(Precedence.COMPARISON, false));
        registerInfixExpression(TokenType.TOK_GT, new BinaryOperatorParselet(Precedence.COMPARISON, false));
        registerInfixExpression(TokenType.TOK_LT, new BinaryOperatorParselet(Precedence.COMPARISON, false));

        // Register Logical AND and OR
        registerInfixExpression(TokenType.TOK_AND, new BinaryOperatorParselet(Precedence.AND, false));
        registerInfixExpression(TokenType.TOK_OR, new BinaryOperatorParselet(Precedence.OR, false));

        // Register Types
        registerPrefixExpression(TokenType.TOK_INT, new IntegerParselet());
        registerPrefixExpression(TokenType.TOK_IDENTIFIER, new IdentifierParselet());
        registerPrefixExpression(TokenType.TOK_KW_IS_EMPTY, new isEmptyParselet());
        registerPrefixExpression(TokenType.TOK_KW_READ, new ReadParselet());
        registerPrefixExpression(TokenType.TOK_BOOL, new BooleanParselet());
        registerPrefixExpression(TokenType.TOK_CHAR, new CharacterParselet());
        registerPrefixExpression(TokenType.TOK_OPEN_BRACKETS, new ListParselet());

        // Register Other
        registerPrefixExpression(TokenType.TOK_OPEN_PARENTHESIS, new ParenthesisParselet());
        registerInfixExpression(TokenType.TOK_OPEN_PARENTHESIS, new CallParselet());

        // Register Fields
        registerInfixExpression(TokenType.TOK_HD, new PostfixOperatorParselet(Precedence.POSTFIX));
        registerInfixExpression(TokenType.TOK_TL, new PostfixOperatorParselet(Precedence.POSTFIX));
        registerInfixExpression(TokenType.TOK_FST, new PostfixOperatorParselet(Precedence.POSTFIX));
        registerInfixExpression(TokenType.TOK_SND, new PostfixOperatorParselet(Precedence.POSTFIX));

        // Register Statements
        registerInfixStatement(TokenType.TOK_ASSIGN, new AssignParselet());
        registerPrefixStatement(TokenType.TOK_KW_IF, new ConditionalParselet());
        registerPrefixStatement(TokenType.TOK_KW_WHILE, new WhileParselet());
        registerPrefixStatement(TokenType.TOK_KW_RETURN, new ReturnParselet());
        registerPrefixStatement(TokenType.TOK_KW_PRINT, new PrintParselet());
    }


    /* ********************************************
     *        Beginning of Parsing the SPL        *
     *********************************************/

    public ArrayList<Declaration> parseSPL(){
        ArrayList<Declaration> declarations = new ArrayList<>();
        while (lookAhead(0).getType() != TokenType.TOK_EOF) {
            Declaration decl = parseDeclaration();
            declarations.add(decl);
        }
        if(declarations.size()== 0){
            throw new ParseException(this, "An SPL program needs at least one declaration.");
        }

        return declarations;
    }

    private Declaration parseDeclaration() {
        Token token = consume();

        // Variable declaration
        if (token.getType() == TokenType.TOK_KW_VAR ||
                token.getType() == TokenType.TOK_KW_CHAR ||
                token.getType() == TokenType.TOK_KW_INT ||
                token.getType() == TokenType.TOK_KW_BOOL ||
                token.getType() == TokenType.TOK_OPEN_BRACKETS ||
                token.getType() == TokenType.TOK_OPEN_PARENTHESIS) {

            //Assuming this only works for global, isGlobal should be true;
            return new VariableDeclarationParselet().parse(this, token, true);
        }

        // Function declaration
        if (token.getType() == TokenType.TOK_IDENTIFIER) {
            return new FunctionDeclarationParselet(new IdentifierExpression(token.getStringValue())).parse(this, token);
        }

        throw new ParseException(this, "No valid declaration is found!.");
    }

    public ArrayList<Statement> parseBlock(){
        ArrayList<Statement> statements = new ArrayList<>();
        while (lookAhead(0).getType() != TokenType.TOK_EOF && lookAhead(0).getType() != TokenType.TOK_CLOSE_CURLY) {
            Statement expr = parseStatement();
            statements.add(expr);
            if (lookAhead(0).getType() == TokenType.TOK_EOF)
                break;
        }
        return statements;
    }

    public Statement parseStatement() {
        Token token = consume();

        // WHILE-IF-RETURN-PRINT
        if (token.getType() == TokenType.TOK_KW_WHILE ||
                token.getType() == TokenType.TOK_KW_IF ||
                token.getType() == TokenType.TOK_KW_RETURN ||
                token.getType() == TokenType.TOK_KW_PRINT) {
            PrefixParseletStatement prefix = mPrefixParseletsStatement.get(token.getType());

            if (prefix == null)
                throw new ParseException(this, token);

            return prefix.parse(this, token);

        } else if (token.getType() == TokenType.TOK_IDENTIFIER) {
            Expression id = mPrefixParseletsExpression.get(token.getType()).parse(this, token);

            // FUNCALL
            if (match(TokenType.TOK_OPEN_PARENTHESIS)) {
                CallExpression funcall = (CallExpression) new CallParselet().parse(this, id, lookAhead(0));

                if (match(TokenType.TOK_EOL))
                    return new CallStatement(funcall);
                else
                    throw new SemicolonError(this);
            }

            // ASSIGNMENT
            else{
                // Consume all fields if any
                if (fieldAhead()) {
                    id = IdentifierExpression.parseFields(this, id);
                }

                token = consume(TokenType.TOK_ASSIGN);
                InfixParseletStatement parselet = mInfixParseletsStatement.get(token.getType());

                return parselet.parse(this, id, token);
            }
        }
        throw new ParseException(this, String.format("No statement could be parsed in line %s", getLine()));
    }

    /**
     * Parses the topmost Expression
     * This function should be called when you want to parse a full expression.
     * @return ABST belonging to the topmost expression.
     */
    public Expression parseExpression() {
        return parseExpression(0);
    }

    /**
     * Parses one Expression
     * @param precedence precedence value
     * @return ABST belonging to the Expression
     */
    public Expression parseExpression(int precedence) {
        Token token = consume();
        PrefixParseletExpression prefix = mPrefixParseletsExpression.get(token.getType());

        if (prefix == null) throw new ParseException(this, token);

        Expression left = prefix.parse(this, token);

        while (precedence < getPrecedence()) {
            token = consume();

            InfixParseletExpression infix = mInfixParseletsExpression.get(token.getType());
            left = infix.parse(this, left, token);
        }
        return left;
    }

    /* ********************************************
     *        End of the Parsing functions        *
     *********************************************/

    /**
     * Tries to match a token of the given listType. If it matches it gets consumed.
     * @param expected TokenType of the to-be-matched token
     * @return Bool if the Token was there.
     */
    public boolean match(TokenType expected) {
        Token token = lookAhead(0);
        if (token.getType() != expected) {
            return false;
        }

        consume();
        return true;
    }

    /**
     * Checks if the next token is a field.
     * @return Bool whether the next token is a field.
     */
    public boolean fieldAhead() {
        return (lookAhead(0).getType() == TokenType.TOK_HD ||
                lookAhead(0).getType() == TokenType.TOK_TL ||
                lookAhead(0).getType() == TokenType.TOK_FST ||
                lookAhead(0).getType() == TokenType.TOK_SND);
    }

    /**
     * Consumes the token of given listType, if it cannot be found it throws an error.
     * @param expected TokenType of the to-be-consumed token.
     * @return Token
     */
    public Token consume(TokenType expected) {
        Token token = lookAhead(0);
        if (token.getType() != expected) {
            throw new ParseException(this,
                    String.format("Expected token: \t %s\n\tFound token: \t %s",
                            expected, token.getType())
            );
        }

        return consume();
    }

    /**
     * Load in one token into the buffer
     * @return First Token in the buffer.
     */
    public Token consume() {
        // Make sure we've read the token.
        lookAhead(0);
        currentToken++;

        return mRead.remove(0);
    }

    /**
     * LookAhead to get the specified Token
     * @param distance size of LookAhead
     * @return Token that is 'distance' tokens ahead
     */
    public Token lookAhead(int distance) {
        // Read in as many as needed.
        while (distance >= mRead.size()) {
            mRead.add(mTokens.next());
        }

        // Get the queued token.
        return mRead.get(distance);
    }


    /**
     * Gets the precendence of the next expression if it exists.
     * @return Precedence of next expression, else 0.
     */
    private int getPrecedence() {
        InfixParseletExpression parser = mInfixParseletsExpression.get(lookAhead(0).getType());
        if (parser != null){
            return parser.getPrecedence();
        }

        return 0;
    }

    /**
     * Helper function to identify what the line is of the current token.
     * @return current line.
     */
    public String getLine() {
        List<Token> currentList = new ArrayList<>(input);
        int lastChar = currentList.indexOf(new TokenOther(TokenType.TOK_EOL));
        while (currentToken > lastChar) {
            if (lastChar == -1) {
                lastChar = currentList.size();
                break;
            }
            currentList = currentList.subList(lastChar + 1, currentList.size());
            lastChar = currentList.indexOf(new TokenOther(TokenType.TOK_EOL));
        }
        return PrettyPrinter.printLine(currentList.subList(0, lastChar));
    }
}
