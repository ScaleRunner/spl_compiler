package codeGeneration;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import codeGeneration.writer.ProgramWriter;
import jdk.nashorn.internal.codegen.CompilerConstants;
import lexer.TokenType;
import parser.declarations.Declaration;
import parser.declarations.FunctionDeclaration;
import parser.declarations.VariableDeclaration;
import parser.expressions.*;
import parser.statements.*;
import parser.types.CharType;
import util.Node;
import util.Visitor;

public class CodeGenerator implements Visitor {

    private final ProgramWriter programWriter;
    //Controls what to do if identifier is in lhs of Assignment (stl)
    //Or rhs (load from MP + offset);
    private boolean lsideIdentifier = false;
    private int localVariableDeclarationOffset;


    HashMap<String, Integer> localVariablesPlusOffsettmp = new HashMap<>();

    HashMap<String, Integer> currentlocalVariablesPlusOffset = new HashMap<>();

    HashMap<String, HashMap<String, Integer>> functionsEnvironment = new HashMap<>();
    //need to work on it later
    HashMap<String, Integer> argsPlusOffset = new HashMap<>();


    private String currentBranch = "root";

    public CodeGenerator(String filepath) {
        this.programWriter = new ProgramWriter(filepath);
    }

    public void generateCode(List<Declaration> nodes, Command postamble) throws FileNotFoundException {
        for(Node n : nodes){
            n.accept(this);
        }

        if (postamble != null) {
            programWriter.addToOutput("root", postamble);
        }
        programWriter.writeToFile();
    }

    public void generateCode(Node n, Command postamble) throws FileNotFoundException {
        n.accept(this);

        if (postamble != null) {
            programWriter.addToOutput("root", postamble);
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
        programWriter.addToOutput(currentBranch, new Command("ldc", val));
    }

    @Override
    public void visit(CallExpression e) {

        for(Expression arg : e.args){
            this.visit(arg);
        }

        programWriter.addToOutput(currentBranch, new Command("bsr", e.function_name.name));

    }

    @Override
    public void visit(CharacterExpression e) {
        programWriter.addToOutput(currentBranch, new Command("ldc", Integer.toString( (int) e.name)));
    }

    @Override
    public void visit(IdentifierExpression e) {
        //If identifier is in the rhs of Assignment, we need to use an offset to load it to the stack;
        if(functionsEnvironment.get(currentBranch) != null)
            currentlocalVariablesPlusOffset = functionsEnvironment.get(currentBranch);
        if(currentlocalVariablesPlusOffset != null) {
            if (!lsideIdentifier && (currentlocalVariablesPlusOffset.get(e.name) != null)) {
                programWriter.addToOutput(currentBranch, new Command("ldl", Integer.toString(currentlocalVariablesPlusOffset.get(e.name)))); //Loads value from address
            }
            //we assume it's an argument
            else if (!lsideIdentifier) {
                programWriter.addToOutput(currentBranch, new Command("ldl",                 "-"+(Integer.toString(currentlocalVariablesPlusOffset.size() + 1)))); //Loads value from add
            }

        }
    }

    @Override
    public void visit(IntegerExpression e) {
        programWriter.addToOutput(currentBranch, new Command("ldc", Integer.toString(e.name)));
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
                programWriter.addToOutput(currentBranch, new Command("add"));
                break;
            case TOK_MULT:
                programWriter.addToOutput(currentBranch, new Command("mul"));
                break;
            case TOK_MINUS:
                programWriter.addToOutput(currentBranch, new Command("sub"));
                break;
            case TOK_MOD:
                programWriter.addToOutput(currentBranch, new Command("mod"));
                break;
            case TOK_DIV:
                programWriter.addToOutput(currentBranch, new Command("div"));
                break;

            // Boolean
            case TOK_AND:
                programWriter.addToOutput(currentBranch, new Command("and"));
                break;
            case TOK_OR:
                programWriter.addToOutput(currentBranch, new Command("or"));
                break;

            // Comparison
            case TOK_EQ:
                programWriter.addToOutput(currentBranch, new Command("eq"));
                break;
            case TOK_NEQ:
                programWriter.addToOutput(currentBranch, new Command("ne"));
                break;
            case TOK_LT:
                programWriter.addToOutput(currentBranch, new Command("lt"));
                break;
            case TOK_GT:
                programWriter.addToOutput(currentBranch, new Command("gt"));
                break;
            case TOK_LEQ:
                programWriter.addToOutput(currentBranch, new Command("le"));
                break;
            case TOK_GEQ:
                programWriter.addToOutput(currentBranch, new Command("ge"));
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
            programWriter.addToOutput(currentBranch, new Command("neg"));
        else if(e.operator == TokenType.TOK_NOT){
            programWriter.addToOutput(currentBranch, new Command("not"));
        }
        else
            throw new CodeGenerationException("Invalid operator", e);
    }

    @Override
    public void visit(TupleExpression e) {

    }

    @Override
    public void visit(Statement s) {
        Statement.visitStatement(this, s);
    }

    @Override
    public void visit(AssignStatement s) {

        //Check this later for 1 = 1;
        String name = ((IdentifierExpression) s.name).name;

        if(s.right instanceof CallExpression ){
            //SAVES previous MP
            //saves MP before putting arguments on stack
            programWriter.addToOutput(currentBranch, new Command("ldr", "MP"));

        }

        this.visit(s.right);
        if(s.right instanceof CallExpression ){
            //after funcall was visitted makes SP point to saved old MP
            CallExpression aux = (CallExpression) s.right;
            programWriter.addToOutput(currentBranch, new Command("ldr", "SP"));
            programWriter.addToOutput(currentBranch, new Command("ldc", Integer.toString(aux.args.size())));
            programWriter.addToOutput(currentBranch, new Command("sub"));
            programWriter.addToOutput(currentBranch, new Command("str", "SP"));

            //stores old MP in MP
            programWriter.addToOutput(currentBranch, new Command("str", "MP"));


            programWriter.addToOutput(currentBranch, new Command("ldr", "RR"));
            programWriter.addToOutput(currentBranch, new Command("stl", Integer.toString(currentlocalVariablesPlusOffset.get(name))));


        }

        //programWriter.addToOutput(currentBranch, new Command("stl", "0"));

    }

    @Override
    public void visit(CallStatement s) {


        for(Expression arg : s.args){
            this.visit(arg);
        }
        programWriter.addToOutput(currentBranch, new Command("bsr", s.function_name.name));


    }

    @Override
    public void visit(ConditionalStatement s) {

    }

    @Override
    public void visit(LoopStatement s) {

    }

    @Override
    public void visit(PrintStatement s) {

        this.visit(s.arg);
        if(s.arg.getType() instanceof CharType)
            programWriter.addToOutput(currentBranch, new Command("trap", "1"));
        else
            programWriter.addToOutput(currentBranch, new Command("trap", "0"));

    }

    @Override
    public void visit(ReturnStatement s) {
        if(s.arg != null)
            this.visit(s.arg);
    if(currentBranch != "main"){
        programWriter.addToOutput(currentBranch, new Command("str RR"));
        programWriter.addToOutput(currentBranch, new Command("unlink"));
        programWriter.addToOutput(currentBranch, new Command("ret"));

    }
    else{
        programWriter.addToOutput(currentBranch, new Command("halt"));
    }
    }

    @Override
    public void visit(Declaration d) {
        Declaration.visitDeclaration(this, d);
    }

    @Override
    public void visit(FunctionDeclaration d) {
        currentlocalVariablesPlusOffset = new HashMap<>();
        int i = 0;
        currentBranch = d.funName.name;
//        if(d.funName.name != "main")
//            programWriter.addToOutput(currentBranch, new Command("str", "RR"));
        if(d.decls.size() != 0)
            programWriter.addToOutput(currentBranch, new Command("link", Integer.toString(d.decls.size()-1)));

        for(Declaration varDec : d.decls){
            localVariableDeclarationOffset = i;
            this.visit(varDec);
            i++;
        }
        for(Statement funStmt : d.stats){
            this.visit(funStmt);
        }

        functionsEnvironment.put(d.funName.name, currentlocalVariablesPlusOffset);

    }

    @Override
    public void visit(VariableDeclaration d) {
        lsideIdentifier = true;
        this.visit(d.left);
        lsideIdentifier = false;
        this.visit(d.right);
        programWriter.addToOutput(currentBranch, new Command("stl", Integer.toString(localVariableDeclarationOffset)));
        currentlocalVariablesPlusOffset.put(d.left.name, localVariableDeclarationOffset);
    }

}