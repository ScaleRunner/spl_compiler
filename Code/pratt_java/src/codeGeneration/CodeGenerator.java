package codeGeneration;

import lexer.TokenType;
import parser.declarations.Declaration;
import parser.declarations.FunctionDeclaration;
import parser.declarations.VariableDeclaration;
import parser.expressions.*;
import parser.statements.*;
import parser.types.*;
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
    private boolean leftsideVarDeclaration = false;
    private boolean leftsideAssignment = false;
    private int localVariableDeclarationOffset;
    private int globalVariableDeclarationOffset = 0;

    private boolean isFirstGlobalVariable = false;

    private HashMap<String, Integer> currentArgumentsPlusOffsettmp = new HashMap<>();

    private HashMap<String, Integer> currentlocalVariablesPlusOffset = new HashMap<>();
    private HashMap<String, Integer> GlobalVariablesPlusOffset = new HashMap<>();

    private HashMap<String, HashMap<String, Integer>> functionsLocalsEnvironment = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> functionsArgsEnvironment = new HashMap<>();
    private int numberOfGlobals = 0;

    private HashMap<String, Type> functionTypes = new HashMap<>();


    public CodeGenerator(String filepath) {
        this.programWriter = new ProgramWriter(filepath);
    }

    public void generateCode(List<Declaration> nodes, Command postamble) throws FileNotFoundException {

        for(Node n : nodes){
            if(n instanceof VariableDeclaration)
                if(((VariableDeclaration) n).isGlobal)
                    numberOfGlobals++;
            if(n instanceof FunctionDeclaration){
                functionTypes.put(((FunctionDeclaration) n).funName.name, ((FunctionDeclaration) n).funType.returnType);
            }
        }

        if(numberOfGlobals > 0)
            isFirstGlobalVariable = true;

        for(Node n : nodes){
            n.accept(this);
            if(n instanceof VariableDeclaration) {
                if (((VariableDeclaration) n).isGlobal)
                    numberOfGlobals++;
                }
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
        programWriter.addToOutput(currentBranch, new Command("ajs", Integer.toString(-currentArgumentsPlusOffsettmp.size())));

        //programWriter.addToOutput(currentBranch, new Command("str", "SP"));
        //stores old MP in MP
        programWriter.addToOutput(currentBranch, new Command("str", "MP"));
        //adjust SP
        //programWriter.addToOutput(currentBranch, new Command("ajs", Integer.toString(-e.args.size())));

        //Only loads result from call if function is not void
        if(!(functionTypes.get(e.function_name.name) instanceof VoidType))
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
            if (!leftsideVarDeclaration && (currentlocalVariablesPlusOffset.get(e.name) != null)) {
                programWriter.addToOutput(currentBranch, new Command("ldl", Integer.toString(currentlocalVariablesPlusOffset.get(e.name)))); //Loads value from address
            }
            //we assume it's an argument
            else if (!leftsideVarDeclaration && currentArgumentsPlusOffsettmp.get(e.name) != null) {
                //first arguments have higher offset;
                                    //2                                   1
                Integer offset = -currentArgumentsPlusOffsettmp.size() + currentArgumentsPlusOffsettmp.get(e.name);
                programWriter.addToOutput(currentBranch, new Command("ldl", (Integer.toString(offset - 1)))); //Loads value from add

                //-1 to go over return address;
            }
            else if (!leftsideVarDeclaration && GlobalVariablesPlusOffset.get(e.name) != null){
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
        this.visit(e.arg);
        programWriter.addToOutput(currentBranch, new Command("ldc", "0"));
        programWriter.addToOutput(currentBranch, new Command("eq"));
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
                // Store the last two elements on the stack and return the address of the last element
                programWriter.addToOutput(currentBranch, new Command("stmh", "2"));
                // We want the address of the first element, so subtract 1 from this address
                programWriter.addToOutput(currentBranch, new Command("ldc", "1"));
                programWriter.addToOutput(currentBranch, new Command("sub"));
                break;

            default:
                throw new CompileException(String.format("Invalid operator '%s'.", e.operator), e);
        }
    }

    @Override
    public void visit(PostfixExpression e) {

        this.visit(e.left);
//        if(leftsideVarDeclaration) {
//            programWriter.addToOutput(currentBranch, new Command("lda", "0"));
//        }
//        if(!leftsideVarDeclaration ) {

            if (e.operator == TokenType.TOK_FST) {
                programWriter.addToOutput(currentBranch, new Command("ldh", "0"));

            } else if (e.operator == TokenType.TOK_SND) {
                if(leftsideAssignment){
                    programWriter.addToOutput(currentBranch, new Command("ldc", "1"));
                    programWriter.addToOutput(currentBranch, new Command("add"));
                    programWriter.addToOutput(currentBranch, new Command("ldh", "0"));
                }
                else
                    programWriter.addToOutput(currentBranch, new Command("ldh", "1"));
                //programWriter.addToOutput(currentBranch, new Command("ldh", "1"));
            }
            //The big question here is:
            //All the rest of the things work using reference
            //For head however, a copy is needed.
            else if (e.operator == TokenType.TOK_HD) {

                programWriter.addToOutput(currentBranch, new Command("ldh", "0"));
                //If type of list is also a list, we need to put the head on the heap pointing to null
                //programWriter.addToOutput(currentBranch, new Command("not"));
            } else if (e.operator == TokenType.TOK_TL) {
                if(leftsideAssignment){
                    programWriter.addToOutput(currentBranch, new Command("ldc", "1"));
                    programWriter.addToOutput(currentBranch, new Command("add"));
                    programWriter.addToOutput(currentBranch, new Command("ldh", "0"));
                }
                else
                    programWriter.addToOutput(currentBranch, new Command("ldh", "1"));

            }
        }


//    }

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
        this.visit(e.left);
        this.visit(e.right);
        programWriter.addToOutput(currentBranch, new Command("stmh","2"));
        programWriter.addToOutput(currentBranch, new Command("ldc", "1"));
        programWriter.addToOutput(currentBranch, new Command("sub"));

    }

    @Override
    public void visit(Statement s) {
        Statement.visitStatement(this, s);
    }

    @Override
    public void visit(AssignStatement s) {
        if(s.name instanceof IdentifierExpression)
            leftsideVarDeclaration = true;
            //leftsideAssignment = true;
        leftsideAssignment = true;
        this.visit(s.name);
        leftsideAssignment = false;
        if(s.name instanceof IdentifierExpression)
            leftsideVarDeclaration = false;
        if(s.name instanceof PostfixExpression)
            programWriter.removeLastCommand(currentBranch);
//        if(s.name instanceof IdentifierExpression){
//            if(GlobalVariablesPlusOffset.get(name)!= null){
//                //Load address of first global variable
//
//            }
//        }


        //Value is put on the stack
//        leftsideAssignment = false;
        //leftsideVarDeclaration = false;
        this.visit(s.right);
//        if(s.right instanceof CallExpression ){
//            programWriter.addToOutput(currentBranch, new Command("ldr", "RR"));
//        }
        if(s.name instanceof IdentifierExpression){
            String name = ((IdentifierExpression) s.name).name;
            if(GlobalVariablesPlusOffset.get(name)!= null){
                //programWriter.addToOutput(currentBranch, new Command("ldr", "R5"));
                //After value to be store is put in the stack we store it
                programWriter.addToOutput(currentBranch, new Command("ldr", "R5"));
                programWriter.addToOutput(currentBranch, new Command("sta", Integer.toString(GlobalVariablesPlusOffset.get(name))));
            }
            else if(currentlocalVariablesPlusOffset.get(name)!= null) {
                programWriter.addToOutput(currentBranch, new Command("stl", Integer.toString(currentlocalVariablesPlusOffset.get(name))));
            }
            else if (currentArgumentsPlusOffsettmp.get(name) != null) {
                //first arguments have higher offset;
                //2                                   1
                Integer offset = -currentArgumentsPlusOffsettmp.size() + currentArgumentsPlusOffsettmp.get(name);

                programWriter.addToOutput(currentBranch, new Command("stl", (Integer.toString(offset - 1)))); //Loads value from add

                //-1 to go over return address;
            }
        }
        if(s.name instanceof PostfixExpression){
//            this.visit(s.right);
            programWriter.addToOutput(currentBranch, new Command("swp"));
            programWriter.addToOutput(currentBranch, new Command("sta", "0"));
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
        programWriter.addToOutput(currentBranch, new Command("ajs", Integer.toString(-currentArgumentsPlusOffsettmp.size())));

        //programWriter.addToOutput(currentBranch, new Command("str", "SP"));
        //stores old MP in MP
        programWriter.addToOutput(currentBranch, new Command("str", "MP"));
        //adjust SP
        //programWriter.addToOutput(currentBranch, new Command("ajs", Integer.toString(-e.args.size())));
        if(!(functionTypes.get(s.function_name.name) instanceof VoidType))
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
        if(s.arg.getType() instanceof CharType){
            this.visit(s.arg);
            programWriter.addToOutput(currentBranch, new Command("trap", "1"));
        }
        else if(s.arg.getType() instanceof IntType || s.arg.getType() instanceof BoolType) {
            this.visit(s.arg);
            programWriter.addToOutput(currentBranch, new Command("trap", "0"));
        }
        else if(s.arg.getType() instanceof TupleType){
            printTuple(s.arg, (TupleType) s.arg.getType(), new ArrayList<>());
        } else
            throw new CompileException(String.format("Printing is not supported for type %s", s.arg.getType()), s);
    }

    private void printTuple(Expression e, TupleType t, List<Command> preamble){
        this.visit(e);
        printCharacter('(');

        // Printing the first element
        preamble.add(new Command("ldh", "0"));
        printElement(e, t.left, preamble);
        preamble.remove(preamble.size()-1);

        printCharacter(',');
        printCharacter(' ');

        // Printing the second element
        this.visit(e);
        preamble.add(new Command("ldh", "1"));
        printElement(e, t.right, preamble);
        preamble.remove(preamble.size()-1);

        printCharacter(')');
    }

    private void printElement(Expression e, Type t, List<Command> preamble){
        if(preamble.size() > 0){
            for(Command c : preamble){
                programWriter.addToOutput(currentBranch, c);
            }
        }
        if(t instanceof IntType)
            programWriter.addToOutput(currentBranch, new Command("trap", "0"));
        else if(t instanceof CharType){
            printCharacter('\'');
            programWriter.addToOutput(currentBranch, new Command("trap", "1"));
            printCharacter('\'');
        } else if(t instanceof TupleType){
            printTuple(e, (TupleType) t, preamble);
        } else
            throw new CompileException(String.format("Printing of Type %s is not yet supported.", t), null);
    }

    private void printCharacter(Character c){
        switch (c){
            case '(':
                programWriter.addToOutput(currentBranch, new Command("ldc", "40"));
                break;
            case ')':
                programWriter.addToOutput(currentBranch, new Command("ldc", "41"));
                break;
            case '[':
                programWriter.addToOutput(currentBranch, new Command("ldc", "91"));
                break;
            case ']':
                programWriter.addToOutput(currentBranch, new Command("ldc", "93"));
                break;
            case ',':
                programWriter.addToOutput(currentBranch, new Command("ldc", "44"));
                break;
            case ' ':
                programWriter.addToOutput(currentBranch, new Command("ldc", "32"));
                break;
            case '\'':
                programWriter.addToOutput(currentBranch, new Command("ldc", "39"));
                break;
        }
        programWriter.addToOutput(currentBranch, new Command("trap", "1"));
    }

    @Override
    public void visit(ReturnStatement s) {
        if(s.arg != null)
            this.visit(s.arg);
        if(!currentBranch.equals("main")){
            if(!(s.arg instanceof CallExpression))
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
            programWriter.addToOutput(currentBranch, new Command("link", Integer.toString((d.decls.size()-1))));
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

        functionsLocalsEnvironment.put(d.funName.name, currentlocalVariablesPlusOffset);
        functionsArgsEnvironment.put(d.funName.name, currentArgumentsPlusOffsettmp);

        for(Statement funStmt : d.stats){
            this.visit(funStmt);
        }

        if(!d.funName.name.equals("main")) {
            //Fixed recursion
            programWriter.addToOutput(currentBranch, new Command("unlink"));
            //programWriter.addToOutput(currentBranch, new Command("ajs", "-1"));
            programWriter.addToOutput(currentBranch, new Command("ret"));

            //currentlocalVariablesPlusOffset.ge currentlocalVariablesPlusOffset.size()
        }



    }

    @Override
    public void visit(VariableDeclaration d) {

        if(isFirstGlobalVariable){
            programWriter.addToOutput(currentBranch, new Command("link", Integer.toString(numberOfGlobals-1)));
            programWriter.addToOutput(currentBranch, new Command("ldr", "MP"));
            programWriter.addToOutput(currentBranch, new Command("str", "R5"));
            isFirstGlobalVariable = false;
        }

        leftsideVarDeclaration = true;
        this.visit(d.left);
        leftsideVarDeclaration = false;
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