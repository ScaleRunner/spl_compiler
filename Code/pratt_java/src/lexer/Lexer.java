package lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private String input;
    private int currentPosition = 0;

    public Lexer(String inp) {
        input = inp;
    }

    public List<Token> tokenize(){
        List<Token> tokenizedInput = new ArrayList<>();
        Token tok;
        do{
            tok = nextToken();
            tokenizedInput.add(tok);
        } while(tok.getType() != TokenType.TOK_EOF);
        return tokenizedInput;
    }

    private void skipWhitespace() {
        while (currentPosition < input.length()
                && Character.isWhitespace(input.charAt(currentPosition))) {
            currentPosition++;
        }
    }

    private boolean match(char c) {
        return currentPosition < input.length()
                && input.charAt(currentPosition) == c;
    }

    public Token nextToken() {
        skipWhitespace();
        if (currentPosition >= input.length()) {
            return new TokenOther(TokenType.TOK_EOF);
        }

        // From here on, we have at least one character in the input

        if (Character.isDigit(input.charAt(currentPosition))) {
            return lexInteger();
        }

        if (match('+')) {
            currentPosition++;
            return new TokenOther(TokenType.TOK_PLUS);
        }

        if (match('-')) {
            currentPosition++;
            return new TokenOther(TokenType.TOK_MINUS);
        }

        if (match('*')) {
            currentPosition++;
            return new TokenOther(TokenType.TOK_MULT);
        }

        if (match('/')) {
            currentPosition++;
            return new TokenOther(TokenType.TOK_DIV);
        }

        if (match('%')) {
            currentPosition++;
            return new TokenOther(TokenType.TOK_MOD);
        }

        if (match('=')) {
            currentPosition++;
            // Just an example how to lex operators that consist of two
            // characters.
            if (match('=')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_EQ);
            }

            // Otherwise, we've seen nothing.
            return new TokenOther(TokenType.TOK_ASSIGN);
        }

        if (match('>')) {
            currentPosition++;
            // Just an example how to lex operators that consist of two
            // characters.
            if (match('=')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_GEQ);
            }

            // Otherwise, we've seen nothing.
            return new TokenOther(TokenType.TOK_GT);
        }

        if (match('<')) {
            currentPosition++;
            // Just an example how to lex operators that consist of two
            // characters.
            if (match('=')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_LEQ);
            }

            // Otherwise, we've seen nothing.
            return new TokenOther(TokenType.TOK_LT);
        }

        if (match('!')) {
            currentPosition++;
            // Just an example how to lex operators that consist of two
            // characters.
            if (match('=')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_NEQ);
            }

            // Otherwise, we've seen nothing.
            return new TokenOther(TokenType.TOK_NOT);
        }

        if (match('&')) {
            currentPosition++;
            // Just an example how to lex operators that consist of two
            // characters.
            if (match('&')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_AND);
            }

            // Otherwise, we've seen nothing.
            return new TokenOther(TokenType.TOK_ERR);
        }

        if (match(':')) {
            currentPosition++;
            // Just an example how to lex operators that consist of two
            // characters.
            if (match(':')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_FUNC_TYPE_DEF);
            }

            // Otherwise, we've seen nothing.
            return new TokenOther(TokenType.TOK_CONS);
        }

        if (match('|')) {
            currentPosition++;
            // Just an example how to lex operators that consist of two
            // characters.
            if (match('|')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_OR);
            }

            // Otherwise, we've seen nothing.
            return new TokenOther(TokenType.TOK_ERR);
        }

        if (match('{')) {
            currentPosition++;

            return new TokenOther(TokenType.TOK_OPEN_CURLY);
        }

        if (match('}')) {
            currentPosition++;

            return new TokenOther(TokenType.TOK_CLOSE_CURLY);
        }

        if (match('[')) {
            currentPosition++;

            return new TokenOther(TokenType.TOK_OPEN_BRACKETS);
        }

        if (match(']')) {
            currentPosition++;

            return new TokenOther(TokenType.TOK_CLOSE_BRACKETS);
        }

        if (match('(')) {
            currentPosition++;

            return new TokenOther(TokenType.TOK_OPEN_PARENTESIS);
        }

        if (match(')')) {
            currentPosition++;

            return new TokenOther(TokenType.TOK_CLOSE_PARENTESIS);
        }

        if (match(',')) {
            currentPosition++;

            return new TokenOther(TokenType.TOK_COMMA);
        }

        if (match(';')) {
            currentPosition++;

            return new TokenOther(TokenType.TOK_SEMI_COLON);
        }

        if (match('.')) {
            currentPosition++;

            return new TokenOther(TokenType.TOK_DOT);
        }

        if (Character.isAlphabetic(input.charAt(currentPosition))) {
            return lexIdentifier();
        }

        return new TokenError(String.format("Found unknown character in input: '%s'", input.charAt(currentPosition)));
    }

    private Token lexInteger() {
        boolean negative = false;
        int currentValue = 0;
        if (match('-')) {
            negative = true;
            currentPosition++;
        }
        while (currentPosition < input.length()
                && Character.isDigit(input.charAt(currentPosition))) {

            currentValue *= 10;
            currentValue += Character.getNumericValue(input
                    .charAt(currentPosition));
            currentPosition++;
        }

        if (negative) {
            currentValue *= -1;
        }

        return new TokenInteger(currentValue);
    }

    private Token lexIdentifier() {
        StringBuilder resultBuilder = new StringBuilder();
        while (currentPosition < input.length()
                && (Character.isAlphabetic(input.charAt(currentPosition)) || Character
                .isDigit(input.charAt(currentPosition)) || match('_'))) {
            resultBuilder.append(input.charAt(currentPosition));
            currentPosition++;
        }

        String result = resultBuilder.toString();

        // Check if the identifier is one of the reserved keywords
        if (result.equals("if")) {
            return new TokenOther(TokenType.TOK_KW_IF);
        }

        if (result.equals("else")) {
            return new TokenOther(TokenType.TOK_KW_ELSE);
        }

        if (result.equals("while")) {
            return new TokenOther(TokenType.TOK_KW_WHILE);
        }

        if (result.equals("Int")) {
            return new TokenOther(TokenType.TOK_KW_INT);
        }

        if (result.equals("Bool")) {
            return new TokenOther(TokenType.TOK_KW_BOOL);
        }

        if (result.equals("Char")) {
            return new TokenOther(TokenType.TOK_KW_CHAR);
        }

        if (result.equals("var")) {
            return new TokenOther(TokenType.TOK_KW_VAR);
        }

        if (result.equals("return")) {
            return new TokenOther(TokenType.TOK_KW_RETURN);
        }

        if (result.equals("hd")) {
            return new TokenField("hd");
        }

        if (result.equals("tl")) {
            return new TokenField("tl");
        }

        if (result.equals("fst")) {
            return new TokenField("fst");
        }

        if (result.equals("snd")) {
            return new TokenField("snd");
        }


        if (result.equals("True")) {
            return new TokenBool(true);
        }

        if (result.equals("False")) {
            return new TokenBool(false);
        }

        if (result.equals("Void")) {
            return new TokenOther(TokenType.TOK_KW_VOID);
        }

        if (result.equals("->")) {
            return new TokenOther(TokenType.TOK_KW_ARROW);
        }


        // Identifier is not a keyword, so we treat it as identifier
        return new TokenIdentifier(result);
    }
}
