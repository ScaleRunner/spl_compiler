package codeGeneration;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import codeGeneration.writer.ProgramWriter;
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
    HashMap<String, Integer> localVariablesPlusOffset = new HashMap<>();
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
        lsideIdentifier = true;
        for(Expression arg : e.args){
            this.visit(arg);
        }
        lsideIdentifier = false;
        programWriter.addToOutput(currentBranch, new Command("bsr", e.function_name.name));
    }

    @Override
    public void visit(CharacterExpression e) {
        programWriter.addToOutput(currentBranch, new Command("ldc", Integer.toString( (int) e.name)));
    }

    @Override
    public void visit(IdentifierExpression e) {
        //If identifier is in the rhs of Assignment, we need to use an offset to load it to the stack;
        if(!lsideIdentifier && (localVariablesPlusOffset.get(e.name) !=null)){
            programWriter.addToOutput(currentBranch,
                    new Command("ldr", "MP")); //Loads value from MP (assuming MP is in origin)
            programWriter.addToOutput(currentBranch,
                    new Command("ldc",
                            Integer.toString(localVariablesPlusOffset.get(e.name)))
            ); // Loads offset FromOrigin
            programWriter.addToOutput(currentBranch, new Command("add"));// With this address we can load variable
            programWriter.addToOutput(currentBranch, new Command("lda", "0")); //Loads value from address
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

    /**
     * For the loop statement:
     * - visit the condition
     * - branch based on the condition
     * - create the new branch loop
     * @param loopStatement
     */
    @Override
    public void visit(LoopStatement loopStatement) {
        String oldBranchName = currentBranch;
        String newBranchName = currentBranch + "_loop";

        // Visit the condition
        this.visit(loopStatement.condition);

        // Branch based on the condition
        programWriter.addToOutput(currentBranch, new Command("brt", newBranchName));

        // Create a new branch and build it
        currentBranch = newBranchName;
        for(Statement s : loopStatement.body){
            s.accept(this);
        }

        // Visit the condition again to check if the loop can be broken and branch on it
        this.visit(loopStatement.condition);
        programWriter.addToOutput(currentBranch, new Command("brt", newBranchName));

        // We are out of the loop
        // Restore old branch name to continue building the oldbranch
        currentBranch = oldBranchName;
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

    }

    @Override
    public void visit(Declaration d) {
        Declaration.visitDeclaration(this, d);
    }

    @Override
    public void visit(FunctionDeclaration d) {

        int i = 0;
        currentBranch = d.funName.name;
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
    }

    @Override
    public void visit(VariableDeclaration d) {
        lsideIdentifier = true;
        this.visit(d.left);
        lsideIdentifier = false;
        this.visit(d.right);
        programWriter.addToOutput(currentBranch, new Command("stl", Integer.toString(localVariableDeclarationOffset)));
        localVariablesPlusOffset.put(d.left.name, localVariableDeclarationOffset);
    }

}