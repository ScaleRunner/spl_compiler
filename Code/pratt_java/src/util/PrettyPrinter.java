package util;

import expressions.*;
import lexer.TokenType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
        builder.append(" ");
        switch (t) {
            case TOK_NOT:
                builder.append("!");
                break;
            case TOK_MINUS:
                builder.append("-");
                break;
            case TOK_PLUS:
                builder.append("+");
                break;
            case TOK_MULT:
                builder.append("*");
                break;
            case TOK_DIV:
                builder.append("/");
                break;
            case TOK_MOD:
                builder.append("%");
                break;
            case TOK_EQ:
                builder.append("==");
                break;
            case TOK_LT:
                builder.append("<");
                break;
            case TOK_GT:
                builder.append(">");
                break;
            case TOK_LEQ:
                builder.append("<=");
                break;
            case TOK_GEQ:
                builder.append(">=");
                break;
            case TOK_NEQ:
                builder.append("!=");
                break;
            case TOK_AND:
                builder.append("&&");
                break;
            case TOK_OR:
                builder.append("||");
                break;
            default:
                throw new Error("PrettyPrinter: cannot accept token " + t);
        }
        builder.append(" ");
    }

    @Override
    public void visit(Expression e){
        if(e.getClass() == AssignExpression.class){
            this.visit((AssignExpression) e);
        }

        else if(e.getClass() == BooleanExpression.class){
            this.visit((BooleanExpression) e);
        }

        else if(e.getClass() == CallExpression.class){
            this.visit((CallExpression) e);
        }

        else if(e.getClass() == ConditionalExpression.class){
            this.visit((ConditionalExpression) e);
        }

        else if(e.getClass() == IdentifierExpression.class){
            this.visit((IdentifierExpression) e);
        }

        else if(e.getClass() == IntegerExpression.class){
            this.visit((IntegerExpression) e);
        }

        else if(e.getClass() == LoopExpression.class){
            this.visit((LoopExpression) e);
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
    public void visit(List<Expression> es){
        for(Expression e : es){
            this.visit(e);
        }
    }

    @Override
    public void visit(AssignExpression e){
        builder.append(e.name).append(" = ");
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
    public void visit(ConditionalExpression e) {
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
    public void visit(LoopExpression e) {
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

}
