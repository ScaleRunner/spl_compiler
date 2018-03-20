package lexer;

public enum TokenType {
    TOK_EOF("EOF"),
    TOK_EOL(";"),
    TOK_ERR("ERROR"),
    TOK_INT("Int"),
    TOK_PLUS("+"),
    TOK_MINUS("-"),
    TOK_MULT("*"),
    TOK_DIV("/"),
    TOK_MOD("%"),
    TOK_PLUS_EQUALS("+="),
    TOK_IDENTIFIER("Char[]"),
    TOK_KW_IF("if"),
    TOK_BOOL("Bool"),
    TOK_EQ("=="),
    TOK_LT("<"),
    TOK_GT(">"),
    TOK_LEQ("<="),
    TOK_GEQ(">="),
    TOK_NEQ("!="),
    TOK_AND("&&"),
    TOK_OR("||"),
    TOK_ASSIGN("="),
    TOK_NOT("!"),
    TOK_CONS(":"),
    TOK_FUNC_TYPE_DEF("????????????????????"),
    TOK_KW_ELSE("else"),
    TOK_KW_WHILE("while"),
    TOK_KW_INT("Int"),
    TOK_KW_BOOL("Bool"),
    TOK_KW_CHAR("Char"),
    TOK_KW_RETURN("return"),
    TOK_KW_PRINT("print"),
    TOK_KW_VAR("var"),
    TOK_OPEN_PARENTHESIS("("),
    TOK_CLOSE_PARENTHESIS(")"),
    TOK_OPEN_CURLY("{"),
    TOK_CLOSE_CURLY("}"),
    TOK_OPEN_BRACKETS("["),
    TOK_CLOSE_BRACKETS("]"),
    TOK_COMMA(","),
    TOK_KW_VOID("Void"),
    TOK_KW_ARROW("->"),
    TOK_HD(".hd"),
    TOK_TL(".tl"),
    TOK_FST(".fst"),
    TOK_SND(".snd");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
