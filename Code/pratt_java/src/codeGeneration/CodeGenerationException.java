package codeGeneration;


import util.Node;

@SuppressWarnings("serial")
public class CodeGenerationException extends RuntimeException {
    public CodeGenerationException(String message, Node n) {
        super(String.format("%s\n\tError found in line %s", message, n));
    }
}