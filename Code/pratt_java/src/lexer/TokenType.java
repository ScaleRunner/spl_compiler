package lexer;

public enum TokenType {
    TOK_EOF,
    TOK_ERR,
    TOK_INT,
    TOK_PLUS,
    TOK_MINUS,
    TOK_MULT,
    TOK_DIV,
    TOK_MOD,
    TOK_PLUS_EQUALS,
    TOK_IDENTIFIER,
    TOK_KW_IF,
    TOK_BOOL,
    TOK_EQ,
    TOK_LT,
    TOK_GT,
    TOK_LEQ,
    TOK_GEQ,
    TOK_NEQ,
    TOK_AND,
    TOK_OR,
    TOK_ASSIGN,
    TOK_NOT,
    TOK_CONS,
    TOK_FUNC_TYPE_DEF,
    TOK_KW_ELSE,
    TOK_KW_WHILE,
    TOK_KW_INT,
    TOK_KW_BOOL,
    TOK_KW_CHAR,
    TOK_KW_RETURN,
    TOK_KW_VAR,
    TOK_OPEN_PARENTESIS,
    TOK_CLOSE_PARENTESIS,
    TOK_OPEN_CURLY,
    TOK_CLOSE_CURLY,
    TOK_OPEN_BRACKETS,
    TOK_CLOSE_BRACKETS,
    TOK_SEMI_COLON,
    TOK_COMMA,
    TOK_KW_VOID,
    TOK_KW_ARROW,
    TOK_HD,
    TOK_TL,
    TOK_FST,
    TOK_SND;

    public String getValue() {
        switch (this) {
            case TOK_PLUS:
                return "+";
            case TOK_EQ:
                return "==";
            case TOK_DIV:
                return "/";
            case TOK_MINUS:
                return "-";
            case TOK_ASSIGN:
                return "=";
            default:
                return null;
        }
    }
}
