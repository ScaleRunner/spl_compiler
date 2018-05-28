package codeGeneration.python;

import codeGeneration.CompileException;
import parser.declarations.Declaration;
import parser.declarations.FunctionDeclaration;
import parser.declarations.VariableDeclaration;
import parser.expressions.*;
import parser.statements.*;
import util.Node;
import util.Visitor;

import java.io.FileNotFoundException;
import java.util.List;

public class CodeGenerator implements Visitor {

    private final ProgramWriter programWriter;

    public CodeGenerator(String filepath) {
        this.programWriter = new ProgramWriter(filepath, "\t");
    }

    public void generateCode(List<Declaration> nodes) throws FileNotFoundException {
        for(Node n : nodes){
            n.accept(this);
        }

        programWriter.writeToFile();
    }

    public void generateCode(Node n) throws FileNotFoundException {
        n.accept(this);

        programWriter.writeToFile();
    }

    @Override
    public void visit(Expression e) {
        Expression.visitExpression(this, e);
    }

    @Override
    public void visit(BooleanExpression e) {
        //TODO
    }

    @Override
    public void visit(CallExpression e) {
        //TODO
    }

    @Override
    public void visit(CharacterExpression e) {
        //TODO
    }

    @Override
    public void visit(IdentifierExpression e) {
        //TODO
    }

    @Override
    public void visit(IntegerExpression e) {
        //TODO
    }

    @Override
    public void visit(isEmptyExpression e) {
        this.visit(e.arg);
        // TODO: the rest
    }

    @Override
    public void visit(ListExpression e) {
        //TODO
    }

    @Override
    public void visit(OperatorExpression e) {
        this.visit(e.left);
        switch (e.operator) {
            // arithmetic binary functions
            case TOK_PLUS:
                programWriter.addToOutput("+");
                break;
            case TOK_MULT:
                programWriter.addToOutput("*");
                break;
            case TOK_MINUS:
                programWriter.addToOutput("-");
                break;
            case TOK_MOD:
                programWriter.addToOutput("%");
                break;
            case TOK_DIV:
                programWriter.addToOutput("/");
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
                programWriter.addToOutput("is");
                break;
            case TOK_NEQ:
                programWriter.addToOutput("not is");
                break;
            case TOK_LT:
                programWriter.addToOutput("<");
                break;
            case TOK_GT:
                programWriter.addToOutput(">");
                break;
            case TOK_LEQ:
                programWriter.addToOutput("<=");
                break;
            case TOK_GEQ:
                programWriter.addToOutput(">=");
                break;
            case TOK_CONS:
                // TODO: do this
                break;

            default:
                throw new CompileException(String.format("Invalid operator '%s'.", e.operator), e);
        }
        this.visit(e.right);
    }

    @Override
    public void visit(PostfixExpression e) {
        this.visit(e.left);
        switch (e.operator) {
            case TOK_FST:
                //TODO
                break;

            case TOK_SND:
                //TODO
                break;

            case TOK_HD:
                //TODO
                break;

            case TOK_TL:
                //TODO
                break;
        }
    }

    @Override
    public void visit(PrefixExpression e) {
        switch (e.operator) {
            case TOK_MINUS:
                programWriter.addToOutput("-");
                break;

            case TOK_NOT:
                programWriter.addToOutput("not");
                break;
        }
        this.visit(e.right);
    }

    @Override
    public void visit(ReadExpression e) {
        //TODO
    }

    @Override
    public void visit(TupleExpression e) {
        //TODO
    }

    @Override
    public void visit(Statement s) {
        Statement.visitStatement(this, s);
    }

    @Override
    public void visit(AssignStatement s) {
        //TODO
    }

    @Override
    public void visit(CallStatement s) {
        //TODO
    }

    @Override
    public void visit(ConditionalStatement conditionalStatement) {
        //TODO
    }

    @Override
    public void visit(LoopStatement loopStatement) {
        //TODO
    }

    @Override
    public void visit(PrintStatement s) {
        //TODO
    }

    @Override
    public void visit(ReturnStatement s) {
        //TODO
    }

    @Override
    public void visit(Declaration d) {
        Declaration.visitDeclaration(this, d);
    }

    @Override
    public void visit(FunctionDeclaration d) {
        //TODO
    }

    @Override
    public void visit(VariableDeclaration d) {
        //TODO
    }

}