package parser;

public class Precedence {
    // Ordered in increasing precedence.
    public static final int ASSIGNMENT = 1;
    public static final int CONDITIONAL = 2;
    public static final int OR = 3;
    public static final int AND = 4;
    public static final int EQUALITY = 5;
    public static final int COMPARISON = 6;
    public static final int ADDITIVE = 7;
    public static final int MULTIPLICATIVE = 8;
    public static final int UNARY = 9;
    public static final int POSTFIX = 10;
    public static final int CALL = 11;
}
