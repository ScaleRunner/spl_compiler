package codeGeneration;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import compiler.CompileException;
import lexer.TokenType;
import parser.declarations.Declaration;
import parser.declarations.FunctionDeclaration;
import parser.declarations.VariableDeclaration;
import parser.expressions.*;
import parser.statements.*;
import parser.types.Type;
import parser.types.Types;
import util.Node;
import util.Visitor;

public class CodeGenerator implements Visitor {

    private final ProgramWriter programWriter;
    //Controls what to do if identifier is in lhs of Assignment (stl)
    //Or rhs (load from MP + offset);
    private boolean lsideAssign = false;
    private int localVariableDeclarationOffset;
    HashMap<String, Integer> localVariablesPlusOffset = new HashMap<>();
    //need to work on it later
    HashMap<String, Integer> argsPlusOffset = new HashMap<>();

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
        programWriter.addToOutput("ldc", val);
    }

    @Override
    public void visit(CallExpression e) {
        for(Expression arg : e.args){
            this.visit(arg);
        }
        programWriter.addToOutput("bsr", e.function_name.name);
    }

    @Override
    public void visit(CharacterExpression e) {
        programWriter.addToOutput("ldc", Integer.toString( (int) e.name));
    }

    @Override
    public void visit(IdentifierExpression e) {
        //If identifier is in the rhs of Assignment, we need to use an offset to load it to the stack;

        if(!lsideAssign){

            programWriter.addToOutput("ldr MP"); //Loads value from MP (assuming MP is in origin)
            programWriter.addToOutput("ldc "+localVariablesPlusOffset.get(e.name)); // Loads offset FromOrigin
            programWriter.addToOutput("add");// With this address we can load variable
            programWriter.addToOutput("lda 0"); //Loads value from address
        }


    }

    @Override
    public void visit(IntegerExpression e) {
        programWriter.addToOutput("ldc", Integer.toString(e.name));
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
            programWriter.addToOutput("not");
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
        for(Expression arg : s.args){
            this.visit(arg);
        }
        programWriter.addToOutput("bsr", s.function_name.name);
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
        Declaration.visitDeclaration(this, d);
    }

    @Override
    public void visit(FunctionDeclaration d) {

        int i = 0;
        programWriter.createBranch(d.funName.name);
        programWriter.addToOutput("link "+ (d.decls.size()-1));

        for(Declaration varDec : d.decls){
            localVariableDeclarationOffset = i;
            this.visit(varDec);
            i++;
        }
        for(Statement funStmt : d.stats){
            this.visit(funStmt);
        }


    }

    @Override
    public void visit(VariableDeclaration d) {
        lsideAssign = true;
        this.visit(d.left);
        lsideAssign = false;
        this.visit(d.right);
        programWriter.addToOutput("stl "+localVariableDeclarationOffset);
        localVariablesPlusOffset.put(d.left.name, localVariableDeclarationOffset);

    }

}