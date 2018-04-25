package codeGeneration;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

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

    }

    @Override
    public void visit(BooleanExpression e) {

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

    }

    @Override
    public void visit(isEmptyExpression e) {

    }

    @Override
    public void visit(ListExpression e) {

    }

    @Override
    public void visit(OperatorExpression e) {

    }

    @Override
    public void visit(PostfixExpression e) {

    }

    @Override
    public void visit(PrefixExpression e) {

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

//    @Override
//    public void visit(AstExprInteger i) {
//        output.add("ldc " + i.getValue());
//    }
//
//    @Override
//    public void visit(AstExprBinOp e) {
//        e.getLeft().accept(this);
//        e.getRight().accept(this);
//        switch (e.getOperator()) {
//            case TOK_PLUS:
//                output.add("add");
//                break;
//            case TOK_MULT:
//                output.add("mul");
//                break;
//            case TOK_MINUS:
//                output.add("sub");
//                break;
//            default:
//                throw new Error("Code generator: AstExprBinOp: unknown operator "
//                        + e.getOperator().toString());
//        }
//    }
}