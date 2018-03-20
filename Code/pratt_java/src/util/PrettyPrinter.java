package util;

import expressions.*;
import lexer.Token;
import lexer.TokenType;
import statements.AssignStatement;
import statements.ConditionalStatement;
import statements.LoopStatement;
import statements.Statement;

import java.util.List;

public class PrettyPrinter implements Visitor {
    private StringBuilder builder;

    public String getResultString() {
        return builder.toString();
    }

    public PrettyPrinter() {
        builder = new StringBuilder();
    }

    private void printToken(TokenType t) {
        switch (t) {
            case TOK_NOT:
                builder.append(" !");
                break;
            case TOK_MINUS:
                builder.append(" - ");
                break;
            case TOK_PLUS:
                builder.append(" + ");
                break;
            case TOK_MULT:
                builder.append(" * ");
                break;
            case TOK_DIV:
                builder.append(" / ");
                break;
            case TOK_MOD:
                builder.append(" % ");
                break;
            case TOK_EQ:
                builder.append(" == ");
                break;
            case TOK_LT:
                builder.append(" < ");
                break;
            case TOK_GT:
                builder.append(" >");
                break;
            case TOK_LEQ:
                builder.append(" <= ");
                break;
            case TOK_GEQ:
                builder.append(" >= ");
                break;
            case TOK_NEQ:
                builder.append(" != ");
                break;
            case TOK_AND:
                builder.append(" && ");
                break;
            case TOK_OR:
                builder.append(" || ");
                break;
            case TOK_EOF:
                break;
            default:
                throw new Error("PrettyPrinter: cannot accept token " + t);
        }
    }

    @Override
    public void visit(Statement s) {
        if (s.getClass() == LoopStatement.class) {
            this.visit((LoopStatement) s);
        } else if (s.getClass() == ConditionalStatement.class) {
            this.visit((ConditionalStatement) s);
        } else if (s.getClass() == AssignStatement.class) {
            this.visit((AssignStatement) s);
        }
    }

    @Override
    public void visit(Expression e){
        if (e.getClass() == BooleanExpression.class) {
            this.visit((BooleanExpression) e);
        }

        else if(e.getClass() == CallExpression.class){
            this.visit((CallExpression) e);
        }


        else if(e.getClass() == IdentifierExpression.class){
            this.visit((IdentifierExpression) e);
        }

        else if(e.getClass() == IntegerExpression.class){
            this.visit((IntegerExpression) e);
        }


        else if(e.getClass() == OperatorExpression.class){
            this.visit((OperatorExpression) e);
        }

        else if(e.getClass() == PostfixExpression.class){
            this.visit((PostfixExpression) e);
        }

        else if(e.getClass() == PrefixExpression.class){
            this.visit((PrefixExpression) e);
        }
    }

    @Override
    public void visit(List<Statement> es){
        for (int i = 0; i < es.size(); i++) {
            this.visit(es.get(i));
            if (i < es.size() - 1) {
                builder.append("\n");
            }
        }
    }

    @Override
    public void visit(AssignStatement e){
        this.visit(e.name);
        builder.append(" = ");
        this.visit(e.right);
        builder.append(";");
    }

    @Override
    public void visit(BooleanExpression e) {
        if(e.name){
            builder.append("True");
        } else {
            builder.append("False");
        }
    }

    @Override
    public void visit(CallExpression e) {
        this.visit(e.function_name);
        builder.append("(");
        for (int i = 0; i < e.args.size(); i++) {
            this.visit(e.args.get(i));
            if (i < e.args.size() - 1){
                builder.append(", ");
            }
        }
        builder.append(")");
    }

    @Override
    public void visit(ConditionalStatement e) {
        builder.append("(if").append(" (");
        this.visit(e.condition);
        builder.append(") ");
        builder.append("{");
        this.visit(e.then_expression);
        builder.append("}");
        if (e.else_expression != null){
            builder.append(" else ").append("{");
            this.visit(e.else_expression);
            builder.append("}");
        }
        builder.append(";");
    }

    @Override
    public void visit(IdentifierExpression e) {
        builder.append(e.name);
    }

    @Override
    public void visit(IntegerExpression e) {
        builder.append(e.name);
    }

    /**
     * Prints a conditional expression as:
     *         (if (CONDITIONAL) {EXPRESSION} else {EXPRESSION})
     * @param e expression
     */
    @Override
    public void visit(LoopStatement e) {
        builder.append("while").append(" (");
        this.visit(e.condition);
        builder.append(") \n");
        builder.append("{");
        this.visit(e.body);
        builder.append("};");
    }

    @Override
    public void visit(OperatorExpression e) {
        this.visit(e.left);
        printToken(e.operator);
        this.visit(e.right);
    }

    @Override
    public void visit(PostfixExpression e) {
        this.visit(e.left);
        printToken(e.operator);
    }

    @Override
    public void visit(PrefixExpression e) {
        printToken(e.operator);
        // However, we have to remove the space between the operator and the expression
        builder.setLength(builder.length() - 1);
        this.visit(e.right);
    }

    public static String printLine(List<Token> tokens) {
        PrettyPrinter p = new PrettyPrinter();
        for (Token t : tokens) {
            try {
                p.printToken(t.getType());
            } catch (Error e) {
                p.builder.append(t.getStringValue());
            }
        }
        return p.getResultString();
    }
}
