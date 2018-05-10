package codeGeneration;

import lexer.TokenType;
import parser.declarations.Declaration;
import parser.declarations.FunctionDeclaration;
import parser.declarations.VariableDeclaration;
import parser.expressions.*;
import parser.statements.*;
import parser.types.CharType;
import util.Node;
import util.Visitor;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CodeGenerator implements Visitor {

    private final ProgramWriter programWriter;

    // String which denotes the branch on which you are currently writing to
    private String currentBranch = "root";

    // Counters for the number of then branches in the current function.
    // Needed if there are multiple or nested if conditions
    private int thenBranches = 0;
    // Counters for the number of end branches in the current function.
    // Needed if there are multiple if conditions and/or while loops or nested if conditions
    private int endBranches = 0;
    // Counters for the number of loop branches in the current function.
    // Needed if there are multiple and/or nested while loops
    private int loopBranches = 0;

    //Controls what to do if identifier is in lhs of Assignment (stl)
    //Or rhs (load from MP + offset);
    private boolean lsideIdentifier = false;
    private int localVariableDeclarationOffset;
    private int globalVariableDeclarationOffset = 0;

    boolean isFirstGlobalVariable = false;

    private HashMap<String, Integer> currentArgumentsPlusOffsettmp = new HashMap<>();

    private HashMap<String, Integer> currentlocalVariablesPlusOffset = new HashMap<>();
    private HashMap<String, Integer> GlobalVariablesPlusOffset = new HashMap<>();

    private HashMap<String, HashMap<String, Integer>> functionsLocalsEnvironment = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> functionsArgsEnvironment = new HashMap<>();
    int numberOfGlobals = 0;


    public CodeGenerator(String filepath) {
        this.programWriter = new ProgramWriter(filepath);
    }

    public void generateCode(List<Declaration> nodes, Command postamble) throws FileNotFoundException {

        for(Node n : nodes){
            if(n instanceof VariableDeclaration)
                if(((VariableDeclaration) n).isGlobal)
                    numberOfGlobals++;
        }

        if(numberOfGlobals > 0)
            isFirstGlobalVariable = true;

        for(Node n : nodes){
            n.accept(this);
            if(n instanceof VariableDeclaration)
                if(((VariableDeclaration) n).isGlobal)
                    numberOfGlobals++;
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

        //SAVES previous MP
        //saves MP before putting arguments on stack
        programWriter.addToOutput(currentBranch, new Command("ldr", "MP"));

        for(Expression arg : e.args){
            this.visit(arg);
        }
        currentArgumentsPlusOffsettmp = functionsArgsEnvironment.get(e.function_name.name);
        programWriter.addToOutput(currentBranch, new Command("bsr", e.function_name.name));

        //REASON: restore old MP
        programWriter.addToOutput(currentBranch, new Command("ajs", Integer.toString(-1)));

        //programWriter.addToOutput(currentBranch, new Command("str", "SP"));
        //stores old MP in MP
        programWriter.addToOutput(currentBranch, new Command("str", "MP"));
        //adjust SP
        //programWriter.addToOutput(currentBranch, new Command("ajs", Integer.toString(-e.args.size())));
        programWriter.addToOutput(currentBranch, new Command("ldr", "RR"));

    }

    @Override
    public void visit(CharacterExpression e) {
        programWriter.addToOutput(currentBranch, new Command("ldc", Integer.toString( (int) e.name)));
    }

    @Override
    public void visit(IdentifierExpression e) {
        //If identifier is in the rhs of Assignment, we need to use an offset to load it to the stack;
        if(functionsLocalsEnvironment.get(currentBranch) != null)
            currentlocalVariablesPlusOffset = functionsLocalsEnvironment.get(currentBranch);
        if(currentlocalVariablesPlusOffset != null) {
            if (!lsideIdentifier && (currentlocalVariablesPlusOffset.get(e.name) != null)) {
                programWriter.addToOutput(currentBranch, new Command("ldl", Integer.toString(currentlocalVariablesPlusOffset.get(e.name)))); //Loads value from address
            }
            //we assume it's an argument
            else if (!lsideIdentifier && currentArgumentsPlusOffsettmp.get(e.name) != null) {
                //first arguments have higher offset;
                                    //2                                   1
                Integer offset = -currentArgumentsPlusOffsettmp.size() +currentArgumentsPlusOffsettmp.get(e.name)  ;
                programWriter.addToOutput(currentBranch, new Command("ldl",  (Integer.toString(offset - 1)))); //Loads value from add
                //-1 to go over return address;
            }
            else if (!lsideIdentifier && GlobalVariablesPlusOffset.get(e.name) != null){
                //loads register that points to first variable
                programWriter.addToOutput(currentBranch, new Command("ldr", "R5")); //Loads value from address

                programWriter.addToOutput(currentBranch, new Command("lda", Integer.toString(GlobalVariablesPlusOffset.get(e.name)))); //Loads value from address
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
        //Empty list is null represented by 0
        programWriter.addToOutput(currentBranch, new Command("ldc", "0"));
        //programWriter.addToOutput(currentBranch, new Command("sth"));

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
            case TOK_CONS:
                programWriter.addToOutput(currentBranch, new Command("stmh", "2"));
                programWriter.addToOutput(currentBranch, new Command("ldc", "1"));
                programWriter.addToOutput(currentBranch, new Command("sub"));
                break;

            default:
                throw new CompileException(String.format("Invalid operator '%s'.", e.operator), e);
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
            throw new CompileException("Invalid operator", e);
    }

    @Override
    public void visit(ReadExpression e) {
        String arg = e.arg.name == 0 ? "10": "11";
        programWriter.addToOutput(currentBranch, new Command("trap", arg));
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
//        if(s.name instanceof IdentifierExpression){
//            if(GlobalVariablesPlusOffset.get(name)!= null){
//                //Load address of first global variable
//
//            }
//        }


        //Value is put on the stack
        this.visit(s.right);
//        if(s.right instanceof CallExpression ){
//            programWriter.addToOutput(currentBranch, new Command("ldr", "RR"));
//        }
        if(s.name instanceof IdentifierExpression){
            if(GlobalVariablesPlusOffset.get(name)!= null){
                //programWriter.addToOutput(currentBranch, new Command("ldr", "R5"));
                //After value to be store is put in the stack we store it
                programWriter.addToOutput(currentBranch, new Command("ldr", "R5"));
                programWriter.addToOutput(currentBranch, new Command("sta", Integer.toString(GlobalVariablesPlusOffset.get(name))));
            }
            else
                programWriter.addToOutput(currentBranch, new Command("stl", Integer.toString(currentlocalVariablesPlusOffset.get(name))));
        }
    }

    @Override
    public void visit(CallStatement s) {
        //SAVES previous MP
        //saves MP before putting arguments on stack
        programWriter.addToOutput(currentBranch, new Command("ldr", "MP"));

        for(Expression arg : s.args){
            this.visit(arg);
        }
        currentArgumentsPlusOffsettmp = functionsArgsEnvironment.get(s.function_name.name);
        programWriter.addToOutput(currentBranch, new Command("bsr", s.function_name.name));
        //REASON: restore old MP
        programWriter.addToOutput(currentBranch, new Command("ajs", Integer.toString(-1)));

        //programWriter.addToOutput(currentBranch, new Command("str", "SP"));
        //stores old MP in MP
        programWriter.addToOutput(currentBranch, new Command("str", "MP"));
        //adjust SP
        //programWriter.addToOutput(currentBranch, new Command("ajs", Integer.toString(-e.args.size())));
        programWriter.addToOutput(currentBranch, new Command("ldr", "RR"));
    }

    /**
     * For the if statement:
     * - visit the condition
     * - branch based on condition
     * - create the branch for the then statement {branch}_then
     * - create the branch for the else statement inside the original branch (i.e. directly underneath the brt check)
     * - create the branch for what comes after the conditional statement {branch}_end
     *
     * The program layout should be as such:
     *      func:       ...
     *                  ....    |
     *                  ....    |- Here comes the condition check
     *                  ....    |
     *                  brt func_then
     *                  ....    |
     *                  ....    |- Here comes the else part
     *                  ....    |
     *                  bra func_end
     *      func_then:  ....
     *                  ....
     *      func_end:   ....    |
     *                  ....    | - Here comes the rest of the function 'func'
     *                  ....    |
     *
     * Such that if the condition is true, you would go into the func_then branch and afterwards continue to the
     * func_end branch, but if the condition was false, you would execute the else part and afterwards skip the
     * func_then branch by immediately going to the func_end branch.
     *
     * @param conditionalStatement: The AST node belonging to a conditionalStatement
     */
    @Override
    public void visit(ConditionalStatement conditionalStatement) {
        //Bookkeeping: create branch names and adjust counters
        String branchThen = currentBranch + "_then" + thenBranches;
        String branchEnd = currentBranch + "_end" + endBranches;
        this.thenBranches++;
        this.endBranches++;

        //Begin code generation for conditional statements
        // Check condition
        this.visit(conditionalStatement.condition);
        // Branch based on condition
        programWriter.addToOutput(currentBranch, new Command("brt", branchThen));

        // Here you visit your else statements
        for(Statement s : conditionalStatement.else_expression){
            this.visit(s);
        }
        // You don't want to end up in the then statements, so skip ahead
        programWriter.addToOutput(currentBranch, new Command("bra", branchEnd));

        // Generate code for the then branch
        currentBranch = branchThen;
        for(Statement s : conditionalStatement.then_expression){
            this.visit(s);
        }

        // Done with the conditional, continue generating the rest
        currentBranch = branchEnd;

        // Ensure that the endBranch exists:
        programWriter.addToOutput(currentBranch, new Command("nop"));
    }

    /**
     * For the loop statement:
     * - visit the condition
     * - branch based on the condition
     * - create the new branch loop
     *
     * The program layout should be as such:
     *      func:       ...
     *                  bra func_loop
     *      func_loop:  ....    |
     *                  ....    |- Here comes the condition check
     *                  ....    |
     *                  brf func_end
     *                  ....    |
     *                  ....    |- Here comes the body of the loop
     *                  ....    |
     *                  bra func_loop
     *
     *      func_end:   ....    |
     *                  ....    | - Here comes the rest of the function 'func'
     *                  ....    |
     *
     * Such that if the condition is False, the program would go directly to the end of the function
     * (i.e. skip the body). If the condition was True, the program would just continue into the body of the loop,
     * after this body is done, check the condition again. This time, if the condition is True, repeat the branch
     * func_loop. Otherwise, the program would continue to the branch func_end.
     *
     * @param loopStatement: the AST Node for the loopStatement
     */
    @Override
    public void visit(LoopStatement loopStatement) {
        //Bookkeeping: create branch names and adjust counters
        String branchLoop = currentBranch + "_loop" + loopBranches;
        String branchEnd = currentBranch + "_end" + endBranches;
        this.loopBranches++;
        this.endBranches++;

        programWriter.addToOutput(currentBranch, new Command("bra", branchLoop));

        //Beginning of while
        // Change branchname and visit the condition
        currentBranch = branchLoop;
        this.visit(loopStatement.condition);

        // Branch based on the condition
        programWriter.addToOutput(currentBranch, new Command("brf", branchEnd));

        // Create the Loop Body
        for(Statement s : loopStatement.body){
            s.accept(this);
        }

        // We are out of the loop, now branch back to the beginning to check the condition again
        programWriter.addToOutput(currentBranch, new Command("bra", branchLoop));

        // Create the end branch and continue building it
        currentBranch = branchEnd;

        // Ensure that the endBranch exists:
        programWriter.addToOutput(currentBranch, new Command("nop"));
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
        if(!currentBranch.equals("main")){
            if(! (s.arg instanceof CallExpression))
                programWriter.addToOutput(currentBranch, new Command("str", "RR"));
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
        // Reset the number of branches in this function

        this.thenBranches = 0;
        this.loopBranches = 0;
        this.endBranches = 0;
        int argOffset = 0;
        currentArgumentsPlusOffsettmp = new HashMap<>();
        for(IdentifierExpression arg: d.args){
            currentArgumentsPlusOffsettmp.put(arg.name, argOffset);
            argOffset++;
        }
        currentlocalVariablesPlusOffset = new HashMap<>();
        int i = 0;
        currentBranch = d.funName.name;
//        if(d.funName.name != "main")
//            programWriter.addToOutput(currentBranch, new Command("str", "RR"));
        if(d.decls.size() != 0)
            programWriter.addToOutput(currentBranch, new Command("link", Integer.toString(d.decls.size()-1)));
        else{
            programWriter.addToOutput(currentBranch, new Command("link", "0"));
//            programWriter.addToOutput(currentBranch, new Command("ldc", "1"));
//            programWriter.addToOutput(currentBranch, new Command("add"));
            //programWriter.addToOutput(currentBranch, new Command("str", "MP"));
        }


        for(Declaration varDec : d.decls){
            localVariableDeclarationOffset = i;
            this.visit(varDec);
            i++;
        }
        for(Statement funStmt : d.stats){
            this.visit(funStmt);
        }

        if(!d.funName.name.equals("main")) {
            //Fixed recursion
            programWriter.addToOutput(currentBranch, new Command("ajs", "-1"));
            programWriter.addToOutput(currentBranch, new Command("ret"));
        }

        functionsLocalsEnvironment.put(d.funName.name, currentlocalVariablesPlusOffset);
        functionsArgsEnvironment.put(d.funName.name, currentArgumentsPlusOffsettmp);

    }

    @Override
    public void visit(VariableDeclaration d) {

        if(isFirstGlobalVariable){
            programWriter.addToOutput(currentBranch, new Command("link", Integer.toString(numberOfGlobals-1)));
            programWriter.addToOutput(currentBranch, new Command("ldr", "MP"));
            programWriter.addToOutput(currentBranch, new Command("str", "R5"));
            isFirstGlobalVariable = false;
        }

        lsideIdentifier = true;
        this.visit(d.left);
        lsideIdentifier = false;
        this.visit(d.right);
        if(d.isGlobal){


            programWriter.addToOutput(currentBranch, new Command("stl", Integer.toString(globalVariableDeclarationOffset)));
            GlobalVariablesPlusOffset.put(d.left.name, globalVariableDeclarationOffset);
            globalVariableDeclarationOffset++;

        }
        else{
            programWriter.addToOutput(currentBranch, new Command("stl", Integer.toString(localVariableDeclarationOffset)));
            currentlocalVariablesPlusOffset.put(d.left.name, localVariableDeclarationOffset);
        }
    }

}