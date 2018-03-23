package parser;

public class Precedence {
    // Ordered in increasing precedence.
    public static final int ASSIGNMENT = 1;
    public static final int CONDITIONAL = 2;
    public static final int OR = 3;
    public static final int AND = 4;
    public static final int EQUALITY = 5;
    public static final int COMPARISON = 6;
    public static final int CONS = 7;
    public static final int ADDITIVE = 8;
    public static final int MULTIPLICATIVE = 9;
    public static final int UNARY = 10;
    public static final int POSTFIX = 11;
    public static final int CALL = 12;
}
