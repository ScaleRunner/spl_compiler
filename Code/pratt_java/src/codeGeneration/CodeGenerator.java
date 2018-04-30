package codeGeneration;

import java.io.FileNotFoundException;
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

    private final ProgramWriter programWriter;

    public CodeGenerator(String filepath) {
        this.programWriter = new ProgramWriter(filepath);
    }

    public void generateCode(List<Declaration> nodes, String postamble)
            throws FileNotFoundException {
        for(Node n : nodes){
            n.accept(this);
        }

        if (postamble != null) {
            programWriter.addToOutput(postamble);
        }
        programWriter.writeToFile();
    }

    public void generateCode(Node n, String postamble) throws FileNotFoundException {
        n.accept(this);
        if (postamble != null) {
            programWriter.addToOutput(postamble);
        }
        programWriter.writeToFile();
    }

    @Override
    public void visit(Expression e) {
        Expression.visitExpression(this, e);
    }

    @Override
    public void visit(BooleanExpression e) {
        // If e.name than "-1" else "0"
        String val = e.name ? "-1" : "0";
        programWriter.addToOutput("ldc " + val);
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
        programWriter.addToOutput("ldc " + e.name);
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
                programWriter.addToOutput("add");
                break;
            case TOK_MULT:
                programWriter.addToOutput("mul");
                break;
            case TOK_MINUS:
                programWriter.addToOutput("sub");
                break;
            case TOK_MOD:
                programWriter.addToOutput("mod");
                break;
            case TOK_DIV:
                programWriter.addToOutput("div");
                break;

            // Boolean
            case TOK_AND:
                programWriter.addToOutput("and");
                break;
            case TOK_OR:
                programWriter.addToOutput("or");
                break;

            // Comparison
            case TOK_EQ:
                programWriter.addToOutput("eq");
                break;
            case TOK_NEQ:
                programWriter.addToOutput("ne");
                break;
            case TOK_LT:
                programWriter.addToOutput("lt");
                break;
            case TOK_GT:
                programWriter.addToOutput("gt");
                break;
            case TOK_LEQ:
                programWriter.addToOutput("le");
                break;
            case TOK_GEQ:
                programWriter.addToOutput("ge");
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
            programWriter.addToOutput("neg");
        else if(e.operator == TokenType.TOK_NOT){
            programWriter.addToOutput("ldc 1");
            programWriter.addToOutput("xor");
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