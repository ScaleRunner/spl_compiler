package compiler;

@SuppressWarnings("serial")
public class CompileException extends RuntimeException {
    public CompileException(String message) {
        super(message);
    }
}