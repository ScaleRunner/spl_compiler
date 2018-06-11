package codeGeneration;

import util.Node;

@SuppressWarnings("serial")
public class CompileException extends RuntimeException {
    public CompileException(String message, Node n) {
        super(String.format("%s\n\tError found in line %s", message, n));
    }

    public CompileException(String message) {
        super(message);
    }
}