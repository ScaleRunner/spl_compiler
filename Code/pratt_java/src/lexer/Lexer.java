package lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private String input;
    private int currentPosition = 0;
    private final Map<String, Token> keywordMap;

    public Lexer(String inp) {
        this.input = inp;
        this.keywordMap = setupKeywordMap();
        removeComments();
    }

    private Map<String, Token> setupKeywordMap(){
        Map<String, Token> keywordMap = new HashMap<>();

        // Statement Keywords
        keywordMap.put("if", new TokenOther(TokenType.TOK_KW_IF));
        keywordMap.put("else", new TokenOther(TokenType.TOK_KW_ELSE));
        keywordMap.put("while", new TokenOther(TokenType.TOK_KW_WHILE));
        keywordMap.put("return", new TokenOther(TokenType.TOK_KW_RETURN));

        // Type Keywords
        keywordMap.put("Int", new TokenOther(TokenType.TOK_KW_INT));
        keywordMap.put("Bool", new TokenOther(TokenType.TOK_KW_BOOL));
        keywordMap.put("Char", new TokenOther(TokenType.TOK_KW_CHAR));
        keywordMap.put("var", new TokenOther(TokenType.TOK_KW_VAR));
        keywordMap.put("Void", new TokenOther(TokenType.TOK_KW_VOID));

        keywordMap.put("True", new TokenBool(true));
        keywordMap.put("False", new TokenBool(false));

        // Expression Keywords
        keywordMap.put("print", new TokenOther(TokenType.TOK_KW_PRINT));
        keywordMap.put("read", new TokenOther(TokenType.TOK_KW_READ));
        keywordMap.put("isEmpty", new TokenOther(TokenType.TOK_KW_IS_EMPTY));

        // Field Keywords
        keywordMap.put(".hd", new TokenOther(TokenType.TOK_HD));
        keywordMap.put(".tl", new TokenOther(TokenType.TOK_TL));
        keywordMap.put(".fst", new TokenOther(TokenType.TOK_FST));
        keywordMap.put(".snd", new TokenOther(TokenType.TOK_SND));

        return keywordMap;
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

    /**
     * Removes comments from the input using regex
     */
    private void removeComments(){
        Matcher m;

        Pattern inlineComment = Pattern.compile("//[^\\n]*\\n");
        Pattern blockComment = Pattern.compile("/\\*[\\S\\s]*?\\*/");

        //Remove Inline Comments
        m = inlineComment.matcher(input);
        this.input = m.replaceAll("");

        //Remove Block Comments
        m = blockComment.matcher(input);
        this.input = m.replaceAll("");

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

        if (Character.isDigit(input.charAt(currentPosition))) {
            return lexInteger();
        }

        if (match('\'')) {
            currentPosition++;
            if(Character.isLetterOrDigit(input.charAt(currentPosition))){
                char c = input.charAt(currentPosition);
                currentPosition++;
                if(match('\'')){
                    currentPosition++;
                    return new TokenChar(c);
                }
            }
            throw new TokenException("Unfinished Char expression, you probably forgot an apostrophe.");
        }

        if (match('+')) {
            currentPosition++;
            return new TokenOther(TokenType.TOK_PLUS);
        }

        if (match('-')) {
            currentPosition++;
            if (match('>')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_KW_ARROW);
            }
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
            if (match('=')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_EQ);
            }
            return new TokenOther(TokenType.TOK_ASSIGN);
        }

        if (match('>')) {
            currentPosition++;
            if (match('=')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_GEQ);
            }
            return new TokenOther(TokenType.TOK_GT);
        }

        if (match('<')) {
            currentPosition++;
            if (match('=')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_LEQ);
            }
            return new TokenOther(TokenType.TOK_LT);
        }

        if (match('!')) {
            currentPosition++;
            if (match('=')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_NEQ);
            }
            return new TokenOther(TokenType.TOK_NOT);
        }

        if (match('&')) {
            currentPosition++;
            if (match('&')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_AND);
            }
            return new TokenOther(TokenType.TOK_ERR);
        }

        if (match(':')) {
            currentPosition++;
            if (match(':')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_FUNC_TYPE_DEF);
            }
            return new TokenOther(TokenType.TOK_CONS);
        }

        if (match('|')) {
            currentPosition++;
            if (match('|')) {
                currentPosition++;
                return new TokenOther(TokenType.TOK_OR);
            }
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

            return new TokenOther(TokenType.TOK_OPEN_PARENTHESIS);
        }

        if (match(')')) {
            currentPosition++;

            return new TokenOther(TokenType.TOK_CLOSE_PARENTHESIS);
        }

        if (match(',')) {
            currentPosition++;

            return new TokenOther(TokenType.TOK_COMMA);
        }

        if (match(';')) {
            currentPosition++;

            return new TokenOther(TokenType.TOK_EOL);
        }

        if (Character.isAlphabetic(input.charAt(currentPosition)) || match('.')) {
            return lexIdentifier();
        }
        throw new TokenException(String.format("Found unknown character in input: '%s'", input.charAt(currentPosition)));
//        return new TokenError(String.format("Found unknown character in input: '%s'", input.charAt(currentPosition)));
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
        if(match('.')){
            resultBuilder.append('.');
            currentPosition++;
        }
        while (currentPosition < input.length()
                && (Character.isAlphabetic(input.charAt(currentPosition)) || Character
                .isDigit(input.charAt(currentPosition)) || match('_'))) {
            resultBuilder.append(input.charAt(currentPosition));
            currentPosition++;
        }

        String result = resultBuilder.toString();

        Token tok = keywordMap.get(result);
        if(tok != null) { // The string is a keyword
            return tok;
        }

        if (result.contains(".")){
            throw new TokenException(String.format(
                    "Invalid field keyword in '%s'.\n\t Did you put a space between field keywords?"
                    , result));
        }

        // Identifier is not a keyword, so we treat it as an identifier
        return new TokenIdentifier(result);
    }
}
