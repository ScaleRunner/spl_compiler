package parser.exceptions;

import parser.Parser;

public class SemicolonError extends ClassCastException {

    public SemicolonError(Parser p) {
        super(String.format("There is probably an ';' missing.\n\tError found in line '%s'. \n\t", p.getLine()));
    }
}