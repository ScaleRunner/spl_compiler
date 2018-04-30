package codeGeneration;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import lexer.TokenType;
import parser.declarations.Declaration;
import parser.declarations.FunctionDeclaration;
import parser.declarations.VariableDeclaration;
import parser.expressions.*;
import parser.statements.*;
import util.Node;
import util.Visitor;

public class CodeGenerator implements Visitor {
    private List<String> output;

    public List<String> getOutput() {
        return output;
    }

    public CodeGenerator() {
        output = new LinkedList<>();
    }

    public void generateCode(List<Declaration> nodes, String outputFilename, String postamble)
            throws FileNotFoundException {
        for(Node n : nodes){
            n.accept(this);
        }

        if (postamble != null) {
            output.add(postamble);
        }
        writeToFile(outputFilename);
    }

    private void writeToFile(String filename) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filename);
        for (String line : output) {
            out.println(line);
        }
        out.close();
    }

    public void generateCode(Node n, String outputFilename)
            throws FileNotFoundException {
        generateCode(n, outputFilename, null);
    }

    public void generateCode(Node n, String outputFilename,
                             String postamble) throws FileNotFoundException {
        n.accept(this);
        if (postamble != null) {
            output.add(postamble);
        }
        writeToFile(outputFilename);
    }

    @Override
    public void visit(Expression e) {
        Expression.visitExpression(this, e);
    }

    @Override
    public void visit(BooleanExpression e) {
        // If e.name than "-1" else "0"
        String val = e.name ? "-1" : "0";
        output.add("ldc " + val);
    }

    @Override
    public void visit(CallExpression e) {

    }

    @Override
    public void visit(CharacterExpression e) {

    }

    @Override
    public void visit(IdentifierExpression e) {

    }

    @Override
    public void visit(IntegerExpression e) {
        output.add("ldc " + e.name);
    }

    @Override
    public void visit(isEmptyExpression e) {

    }

    @Override
    public void visit(ListExpression e) {

    }

    @Override
    public void visit(OperatorExpression e) {
        this.visit(e.left);
        this.visit(e.right);
        switch (e.operator) {
            // arithmetic binary functions
            case TOK_PLUS:
                output.add("add");
                break;
            case TOK_MULT:
                output.add("mul");
                break;
            case TOK_MINUS:
                output.add("sub");
                break;
            case TOK_MOD:
                output.add("mod");
                break;
            case TOK_DIV:
                output.add("div");
                break;

            // Boolean
            case TOK_AND:
                output.add("and");
                break;
            case TOK_OR:
                output.add("or");
                break;

            // Comparison
            case TOK_EQ:
                output.add("eq");
                break;
            case TOK_NEQ:
                output.add("ne");
                break;
            case TOK_LT:
                output.add("lt");
                break;
            case TOK_GT:
                output.add("gt");
                break;
            case TOK_LEQ:
                output.add("le");
                break;
            case TOK_GEQ:
                output.add("ge");
                break;

            default:
                throw new CodeGenerationException(String.format("Invalid operator '%s'.", e.operator), e);
        }
    }

    @Override
    public void visit(PostfixExpression e) {

    }

    @Override
    public void visit(PrefixExpression e) {
        this.visit(e.right);

        if(e.operator == TokenType.TOK_MINUS)
            output.add("neg");
        else if(e.operator == TokenType.TOK_NOT){
            output.add("not");
        }
        else
            throw new CodeGenerationException("Invalid operator", e);
    }

    @Override
    public void visit(TupleExpression e) {

    }

    @Override
    public void visit(Statement s) {

    }

    @Override
    public void visit(AssignStatement s) {

    }

    @Override
    public void visit(CallStatement s) {

    }

    @Override
    public void visit(ConditionalStatement s) {

    }

    @Override
    public void visit(LoopStatement s) {

    }

    @Override
    public void visit(PrintStatement s) {

    }

    @Override
    public void visit(ReturnStatement s) {

    }

    @Override
    public void visit(Declaration d) {

    }

    @Override
    public void visit(FunctionDeclaration d) {

    }

    @Override
    public void visit(VariableDeclaration d) {

    }
}