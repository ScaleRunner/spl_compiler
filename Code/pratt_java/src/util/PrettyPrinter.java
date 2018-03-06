package util;

import lexer.TokenType;

public class PrettyPrinter implements Visitor {
    StringBuilder result;

    public String getResultString() {
        return result.toString();
    }

    public PrettyPrinter() {
        result = new StringBuilder();
    }

    private void printToken(TokenType t) {
        switch (t) {
            case TOK_PLUS:
                result.append("+");
                break;
            case TOK_MULT:
                result.append("*");
                break;
            case TOK_KW_IF:
                result.append("if");
                break;
            default:
                throw new Error("PrettyPrinter: cannot print token " + t);
        }
    }
}
