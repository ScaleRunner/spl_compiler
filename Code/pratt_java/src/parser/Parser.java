package parser;

import expressions.Expression;
import lexer.Token;
import lexer.TokenType;
import parselets.InfixParselet;
import parselets.PrefixParselet;

import java.util.*;

public class Parser {
    public Parser(Iterator<Token> tokens) {
        mTokens = tokens;
    }

    private final Iterator<Token> mTokens;
    private final List<Token> mRead = new ArrayList<Token>();
    private final Map<TokenType, PrefixParselet> mPrefixParselets =
            new HashMap<TokenType, PrefixParselet>();
    private final Map<TokenType, InfixParselet> mInfixParselets =
            new HashMap<TokenType, InfixParselet>();

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

    public Token consume() {
        // Make sure we've read the token.
        lookAhead(0);

        return mRead.remove(0);
    }

    private Token lookAhead(int distance) {
        // Read in as many as needed.
        while (distance >= mRead.size()) {
            mRead.add(mTokens.next());
        }

        // Get the queued token.
        return mRead.get(distance);
    }

    private int getPrecedence() {
        InfixParselet parser = mInfixParselets.get(lookAhead(0).getType());
        if (parser != null) return parser.getPrecedence();

        return 0;
    }
}
