package parser.exceptions;

import lexer.Token;
import parser.Parser;

@SuppressWarnings("serial")
public class ParseException extends RuntimeException {
    public ParseException(Parser p, String message) {
        super(String.format("%s\n\tError found in line %s", message, p.getLine()));
    }

    public ParseException(Parser p, Token t) {
        super(String.format("There was an error parsing '%s'.\n\tError found in line \t '%s'", t, p.getLine()));
    }
}