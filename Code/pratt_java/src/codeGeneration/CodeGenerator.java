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


    private HashMap<String, Integer> currentArgumentsPlusOffsettmp = new HashMap<>();

    private HashMap<String, Integer> currentlocalVariablesPlusOffset = new HashMap<>();

    private HashMap<String, HashMap<String, Integer>> functionsLocalsEnvironment = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> functionsArgsEnvironment = new HashMap<>();
    //need to work on it later
    private HashMap<String, Integer> argsPlusOffset = new HashMap<>();


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

        //SAVES previous MP
        //saves MP before putting arguments on stack
        programWriter.addToOutput(currentBranch, new Command("ldr", "MP"));

        for(Expression arg : e.args){
            this.visit(arg);
        }
        currentArgumentsPlusOffsettmp = functionsArgsEnvironment.get(e.function_name.name);
        programWriter.addToOutput(currentBranch, new Command("bsr", e.function_name.name));

        //after funcall was visitted makes SP point to saved old MP

//        programWriter.addToOutput(currentBranch, new Command("ldr", "SP"));
//        programWriter.addToOutput(currentBranch, new Command("ldc", Integer.toString(e.args.size())));
//        programWriter.addToOutput(currentBranch, new Command("sub"));
        programWriter.addToOutput(currentBranch, new Command("ajs", Integer.toString(-e.args.size())));

        //programWriter.addToOutput(currentBranch, new Command("str", "SP"));
        //stores old MP in MP
        programWriter.addToOutput(currentBranch, new Command("str", "MP"));


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

        this.visit(s.right);
        if(s.right instanceof CallExpression ){

            programWriter.addToOutput(currentBranch, new Command("ldr", "RR"));



        }

        programWriter.addToOutput(currentBranch, new Command("stl", Integer.toString(currentlocalVariablesPlusOffset.get(name))));

    }

    @Override
    public void visit(CallStatement s) {
    int i = 0;
    currentArgumentsPlusOffsettmp = functionsArgsEnvironment.get(s.function_name.name);
    //SAVES previous MP
        //saves MP before putting arguments on stack

        programWriter.addToOutput(currentBranch, new Command("ldr", "MP"));

        for(Expression arg : s.args){

            this.visit(arg);

        }
        if(currentArgumentsPlusOffsettmp.size() != 0){
            functionsArgsEnvironment.put(s.function_name.name, currentArgumentsPlusOffsettmp);
        }
        programWriter.addToOutput(currentBranch, new Command("bsr", s.function_name.name));

        //after funcall was visitted makes SP point to saved old MP
        programWriter.addToOutput(currentBranch, new Command("ldr", "SP"));
        programWriter.addToOutput(currentBranch, new Command("ldc", Integer.toString(s.args.size())));
        programWriter.addToOutput(currentBranch, new Command("sub"));
        programWriter.addToOutput(currentBranch, new Command("str", "SP"));
        //stores old MP in MP
        programWriter.addToOutput(currentBranch, new Command("str", "MP"));




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
     * TODO: If there would be a nested if, would this nice flowing through the next branch idea break?
     *
     * @param s
     */
    @Override
    public void visit(ConditionalStatement s) {

    }

    /**
     * For the loop statement:
     * - visit the condition
     * - branch based on the condition
     * - create the new branch loop
     *
     * The program layout should be as such:
     *      func:       ...
     *                  ....    |
     *                  ....    |- Here comes the condition check
     *                  ....    |
     *                  brf func_end
     *      func_loop:  ....    |
     *                  ....    |- Here comes the body of the loop
     *                  ....    |
     *
     *                  ....    |
     *                  ....    | - Here comes the condition check
     *                  ....    |
     *                  brt func_loop
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
     * TODO: If there would be a nested loop, would this nice flowing through the next branch idea break?
     *      * Maybe not if we would print the func_end{number} in descending order?
     *      * Meh, we probably have to do hard branching - i.e. if True go to this branch else go to this branch
     *
     * @param loopStatement
     */
    @Override
    public void visit(LoopStatement loopStatement) {

        //TODO: I think we should check the condition within the SSM right? Because now the condition can never change?
        //Just tested this, and it does not work. The condition is indeff True or False.
        // Could be because the Assignment is not even implemented yet ... :'D

        //Bookkeeping: create branch names and adjust counters
        String branchLoop = currentBranch + "_loop" + loopBranches;
        String brachEnd = currentBranch + "_end" + endBranches;

        this.loopBranches++;
        this.endBranches++;

        //Beginning of function:
        // Visit the condition
        this.visit(loopStatement.condition);

        // Branch based on the condition
        programWriter.addToOutput(currentBranch, new Command("brf", brachEnd));

        // Create the loop branch and build it
        currentBranch = branchLoop;
        for(Statement s : loopStatement.body){
            s.accept(this);
        }

        // Visit the condition again to check if the loop can be broken and branch on it
        this.visit(loopStatement.condition);
        programWriter.addToOutput(currentBranch, new Command("brt", branchLoop));

        // We are out of the loop
        // Create the end branch and continue building it
        currentBranch = brachEnd;
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
            if(s.arg instanceof CallExpression)
                programWriter.addToOutput(currentBranch, new Command("ldr",  "RR"));
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

        for(Declaration varDec : d.decls){
            localVariableDeclarationOffset = i;
            this.visit(varDec);
            i++;
        }
        for(Statement funStmt : d.stats){
            this.visit(funStmt);
        }

        functionsLocalsEnvironment.put(d.funName.name, currentlocalVariablesPlusOffset);
        functionsArgsEnvironment.put(d.funName.name, currentArgumentsPlusOffsettmp);

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