package util;

import lexer.Token;
import lexer.TokenType;
import parser.types.*;
import parser.declarations.Declaration;
import parser.declarations.FunctionDeclaration;
import parser.declarations.VariableDeclaration;
import parser.expressions.*;
import parser.statements.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrettyPrinter implements Visitor {
    private StringBuilder builder;
    private String prefix;

    public String getResultString() {
        return builder.toString();
    }

    public PrettyPrinter() {
        builder = new StringBuilder();
        prefix = "";
    }

    private void printToken(TokenType t) {
        switch (t) {
            case TOK_NOT:
                builder.append(" ! ");
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
            case TOK_CONS:
                builder.append(" : ");
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
                builder.append(" > ");
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
            case TOK_HD:
            case TOK_TL:
            case TOK_FST:
            case TOK_SND:
                builder.append(t.getValue());
                break;
            case TOK_EOF:
                break;
            default:
                throw new Error("PrettyPrinter: cannot print token " + t);
        }
    }

    @Override
    public void visit(Statement s) {
        Statement.visitStatement(this, s);
    }

    @Override
    public void visit(Expression e){
        Expression.visitExpression(this, e);
    }

    public void visit(List<Statement> es){
        for(int i = 0; i < es.size(); i++) {
            builder.append(prefix);
            this.visit(es.get(i));
            if(i < es.size() - 1) {
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
    public void visit(CallStatement s) {
        this.visit(s.function_name);
        builder.append("(");
        for (int i = 0; i < s.args.size(); i++) {
            this.visit(s.args.get(i));
            if (i < s.args.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(");");
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

    /**
     * Prints a conditional expression as:
     *         if(CONDITION) {
     *              ...
     *         } else {
     *              ...
     *         }
     * @param e expression
     */
    @Override
    public void visit(ConditionalStatement e) {
        builder.append("if").append("(");
        this.visit(e.condition);
        builder.append(") ");
        builder.append("{\n");
        prefix = prefix + '\t';
        this.visit(e.then_expression);
        prefix = prefix.replaceFirst("\t", "");
        if(e.else_expression.size() > 0) {
            builder.append('\n').append(prefix).append("} else {\n");
            prefix = prefix + '\t';
            this.visit(e.else_expression);
            prefix = prefix.replaceFirst("\t", "");
        }
        builder.append('\n').append(prefix).append("}");
    }

    @Override
    public void visit(IdentifierExpression e) {
        builder.append(e.name);
    }

    @Override
    public void visit(CharacterExpression e) {
        builder.append("'").append(e.name).append("'");
    }

    @Override
    public void visit(IntegerExpression e) {
        builder.append(e.name);
    }

    @Override
    public void visit(isEmptyExpression e) {
        builder.append("isEmpty(");
        this.visit(e.arg);
        builder.append(")");
    }

    @Override
    public void visit(ListExpression e) {
        builder.append("[]");
    }

    /**
     * Prints a conditional expression as:
     *         while(CONDITION) {
     *              ...
     *         }
     * @param e expression
     */
    @Override
    public void visit(LoopStatement e) {
        builder.append("while").append("(");
        this.visit(e.condition);
        builder.append(") {\n");
        prefix = prefix + '\t';
        this.visit(e.body);
        prefix = prefix.replaceFirst("\t", "");
        builder.append("\n").append(prefix).append("}");
    }

    @Override
    public void visit(PrintStatement s) {
        builder.append("print(");
        if (s.arg != null) {
            this.visit(s.arg);
        }
        builder.append(");");
    }

    @Override
    public void visit(ReturnStatement s) {
        builder.append("return ");
        this.visit(s.arg);
        builder.append(";");
    }

    @Override
    public void visit(Declaration d) {
        Declaration.visitDeclaration(this, d);
    }

    public void visit(ArrayList<Declaration> ds) {
        for(int i = 0; i < ds.size(); i++) {
            Declaration.visitDeclaration(this, ds.get(i));
            if(i < ds.size() - 1){
                builder.append("\n");
            }
        }
    }

    /**
     * Print a declaration as: fib(i) :: Int -> Int { ... }
     * @param d
     */
    @Override
    public void visit(FunctionDeclaration d) {
        this.visit(d.funName);
        builder.append("(");
        for(int i = 0; i < d.args.size(); i++){
            this.visit(d.args.get(i));
            if (i < d.args.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(")");

        if(d.funType != null){
            builder.append(" :: ");
            this.visit(d.funType);
        }

        builder.append(" {\n");
        prefix = prefix + "\t";
        for(VariableDeclaration varDecl : d.decls){
            builder.append(prefix);
            this.visit(varDecl);
        }
        builder.append("\n");
        this.visit(d.stats);
        prefix = prefix.replaceFirst("\t", "");
        builder.append("\n}\n\n");
    }

    /**
     * Print VariableDeclaration as ('var' | Type) id = Expression;
     * @param d decl
     */
    @Override
    public void visit(VariableDeclaration d) {
        if(d.varType != null){
            Type.visitType(this, d.varType);
        } else {
            builder.append("var");
        }
        builder.append(" ");
        this.visit(d.left);

        builder.append(" = ");

        this.visit(d.right);

        builder.append(";\n");
    }

    public void visit(BoolType t) {
        builder.append(TokenType.TOK_KW_BOOL.getValue());
    }

    public void visit(CharType t) {
        builder.append(TokenType.TOK_KW_CHAR.getValue());
    }

    public void visit(FunType fType) {
        for(int i = 0; i < fType.argsTypes.size(); i++){
            Type.visitType(this, fType.argsTypes.get(i));
            if (i < fType.argsTypes.size() - 1) {
                builder.append(" ");
            }
        }

        builder.append(" -> ");

        Type.visitType(this, fType.returnType);
    }

    public void visit(IntType t) {
        builder.append(TokenType.TOK_KW_INT.getValue());
    }

    public void visit(ListType t) {
        builder.append('[');
        Type.visitType(this, t.listType);
        builder.append(']');
    }

    public void visit(TupleType t) {
        builder.append('(');
        Type.visitType(this, t.left);
        builder.append(", ");
        Type.visitType(this, t.right);
        builder.append(')');
    }

    public void visit(VarType t) {
        builder.append(TokenType.TOK_KW_VAR.getValue());
    }

    public void visit(VoidType t) {
        builder.append(TokenType.TOK_KW_VOID.getValue());
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

    @Override
    public void visit(ReadExpression e) {
        builder.append("read(");
        this.visit(e.arg);
        builder.append(")");
    }

    @Override
    public void visit(TupleExpression e) {
        builder.append('(');
        this.visit(e.left);
        builder.append(", ");
        this.visit(e.right);
        builder.append(')');
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

    public static void writeToFile(String outfile, List<? extends Node> nodes) throws IOException {
        FileWriter fileWriter = new FileWriter(outfile, false);
        PrettyPrinter pp = new PrettyPrinter();
        for(Node n: nodes){
            Node.visitNode(pp, n);
        }
        fileWriter.write(pp.getResultString());
        fileWriter.close();
    }

}
