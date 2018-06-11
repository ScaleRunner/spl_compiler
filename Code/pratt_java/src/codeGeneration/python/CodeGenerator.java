package codeGeneration.python;

import codeGeneration.CompileException;
import lexer.TokenType;
import parser.declarations.Declaration;
import parser.declarations.FunctionDeclaration;
import parser.declarations.VariableDeclaration;
import parser.expressions.*;
import parser.statements.*;
import parser.types.EmptyListType;
import typechecker.Environment;
import util.Node;
import util.Visitor;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodeGenerator implements Visitor {

    private boolean lhsAssignment = false;
    private final ProgramWriter programWriter;

    private int countNestedTail = 0;
    private int countNestedHead = 0;
    private int totalNestedHDTL = 0;
    private boolean nestedTL = false;
    private boolean nestedHD = false;
    private int postCall = 0;
    private String variablePost = null;
    private boolean useNested = false;

    private final Environment env;
    private ArrayList<String> variablesUsedAsGlobal;

    public CodeGenerator(String filepath, Environment env) {
        this.programWriter = new ProgramWriter(filepath, "\t");
        this.env = env;
        this.variablesUsedAsGlobal = new ArrayList<>();
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
        String line = e.name ? "True" : "False";
        programWriter.addToOutput(line, true);
    }

    @Override
    public void visit(CallExpression e) {
        this.visit(e.function_name);

        programWriter.addToOutput("(", false);
        for(int i = 0; i < e.args.size(); i ++){
            this.visit(e.args.get(i));
            if(i < e.args.size() - 1)
                programWriter.addToOutput(",", true);
        }
        programWriter.addToOutput(")", false);
    }

    @Override
    public void visit(CharacterExpression e) {
        programWriter.addToOutput(String.format("'%c'", e.name), true);
    }

    @Override
    public void visit(IdentifierExpression e) {
        if(lhsAssignment && postCall >1){
            variablePost = e.name;
        }
        if(this.env.isGlobalVariable(e.name) && !this.variablesUsedAsGlobal.contains(e.name)){
            this.variablesUsedAsGlobal.add(e.name);
        }
        programWriter.addToOutput(e.name, false);
    }

    @Override
    public void visit(IntegerExpression e) {
        programWriter.addToOutput(String.valueOf(e.name), true);
    }

    @Override
    public void visit(isEmptyExpression e) {
        programWriter.addToOutput("not", true);
        this.visit(e.arg);
    }

    @Override
    public void visit(ListExpression e) {
        programWriter.addToOutput("[]", false);
    }

    @Override
    public void visit(OperatorExpression e) {
        programWriter.addToOutput("(", false);
        if(e.operator != TokenType.TOK_CONS) // we need to do it separately for lists
            this.visit(e.left);

        switch (e.operator) {
            // arithmetic binary functions
            case TOK_PLUS:
                programWriter.addToOutput(" +", true);
                break;
            case TOK_MULT:
                programWriter.addToOutput(" *", true);
                break;
            case TOK_MINUS:
                programWriter.addToOutput(" -", true);
                break;
            case TOK_MOD:
                programWriter.addToOutput(" %", true);
                break;
            case TOK_DIV:
                programWriter.addToOutput(" //", true);
                break;

            // Boolean
            case TOK_AND:
                programWriter.addToOutput(" and", true);
                break;
            case TOK_OR:
                programWriter.addToOutput(" or", true);
                break;

            // Comparison
            case TOK_EQ:
                programWriter.addToOutput(" is", true);
                break;
            case TOK_NEQ:
                programWriter.addToOutput(" is not", true);
                break;
            case TOK_LT:
                programWriter.addToOutput(" <", true);
                break;
            case TOK_GT:
                programWriter.addToOutput(" >", true);
                break;
            case TOK_LEQ:
                programWriter.addToOutput(" <=", true);
                break;
            case TOK_GEQ:
                programWriter.addToOutput(" >=", true);
                break;
            case TOK_CONS:
                programWriter.addToOutput("[", false);
                this.visit(e.left);
                programWriter.addToOutput("]", true);

                programWriter.addToOutput(" +", true);

                if(e.right.getType() instanceof EmptyListType){
                    programWriter.addToOutput("[", false);
                    this.visit(e.left);
                    programWriter.addToOutput("]", true);
                } else {
                    this.visit(e.right);
                }
                break;

            default:
                throw new CompileException(String.format("Invalid operator '%s'.", e.operator), e);
        }
        if(e.operator != TokenType.TOK_CONS) // we need to do it separately for lists
            this.visit(e.right);
        programWriter.addToOutput(")", false);
    }

    @Override
    public void visit(PostfixExpression e) {

        /*
        * This version uses the number of nested tails as index of array;
        * Therefore, nested tails + head, only works if the element of the list in the given index (number of nested tl's) is a list
        * */
        if(postCall == 0){
            countNestedTail =0;
            countNestedHead=0;
        }
        postCall++;

        this.visit(e.left);

        switch (e.operator) {
            case TOK_FST:
                programWriter.addToOutput("[0]", false, false);
                break;

            case TOK_SND:
                programWriter.addToOutput("[1]", false, false);
                break;

            case TOK_HD:
                if(!lhsAssignment)
                    programWriter.addToOutput("[0]", false, false);
                else {

                    countNestedHead++;
                    totalNestedHDTL++;
                    if(nestedTL){
                        nestedTL = false;
                        programWriter.addToOutput("["+countNestedTail+"]", false, false);
                        useNested = true;
                        countNestedTail = 0;
                    }
                    if(totalNestedHDTL < postCall){
                        nestedHD = true;
                    }
                    else if (totalNestedHDTL == postCall){
                        programWriter.addToOutput("[0]", false, false);
                        nestedHD = false;
                    }

                }
                break;

            case TOK_TL:
                if(!lhsAssignment)
                    programWriter.addToOutput("[1:]", false, false);
                else{
                    countNestedTail++;
                    totalNestedHDTL++;
                    if(nestedHD){
                        nestedHD = false;
                        for(int i =0; i< countNestedHead; i++){
                            programWriter.addToOutput("[0]", false, false);
                        }

                        useNested = true;
                        countNestedHead = 0;
                    }
                    if(totalNestedHDTL < postCall){
                        nestedTL = true;
                    }
                    else if (totalNestedHDTL == postCall){
                        programWriter.addToOutput(" ["+countNestedTail+"]", false, false);
                        nestedTL = false;
                    }
                }
                break;
        }
    }

    @Override
    public void visit(PrefixExpression e) {
        switch (e.operator) {
            case TOK_MINUS:
                programWriter.addToOutput("-", false);
                break;

            case TOK_NOT:
                programWriter.addToOutput(" not", true);
                break;
        }
        this.visit(e.right);
    }

    @Override
    public void visit(ReadExpression e) {
        if(e.arg.name == 1){
            programWriter.addToOutput("input('Please enter a character: ')", false, true);
        } else {
            programWriter.addToOutput("int(input('Please enter an integer: '))", false, true);
        }
    }

    @Override
    public void visit(TupleExpression e) {
        programWriter.addToOutput("(", false);
        this.visit(e.left);
        programWriter.addToOutput(",", true);
        this.visit(e.right);
        programWriter.addToOutput(")", false);
    }

    @Override
    public void visit(Statement s) {
        Statement.visitStatement(this, s);
    }

    @Override
    public void visit(AssignStatement s) {
        lhsAssignment = true;
        this.visit(s.name);
        lhsAssignment = false;
        programWriter.addToOutput( " =", true, false);
        this.visit(s.right);
        programWriter.addToOutput( "", false, true);
    }

    @Override
    public void visit(CallStatement s) {
        this.visit(s.function_name);

        programWriter.addToOutput("(", false);
        for(int i = 0; i < s.args.size(); i ++){
            this.visit(s.args.get(i));
            if(i < s.args.size() - 1)
                programWriter.addToOutput(",", true);
        }
        programWriter.addToOutput(")", false, true);
    }

    @Override
    public void visit(ConditionalStatement conditionalStatement) {
        programWriter.addToOutput("if", true);
        this.visit(conditionalStatement.condition);
        programWriter.addToOutput(":", false, true);
        programWriter.addIndent();
        for(Statement s : conditionalStatement.then_expression){
            visit(s);
        }
        programWriter.removeIndent();
        if(conditionalStatement.else_expression.size() > 0){
            programWriter.addToOutput("else:", false, true);
            programWriter.addIndent();
            for(Statement s : conditionalStatement.else_expression){
                visit(s);
            }
            programWriter.removeIndent();
        }
    }

    @Override
    public void visit(LoopStatement loopStatement) {
        programWriter.addToOutput("while", true, false);
        this.visit(loopStatement.condition);
        programWriter.addToOutput(":", false, true);
        programWriter.addIndent();
        for(Statement s: loopStatement.body){
            this.visit(s);
        }
        programWriter.removeIndent();
    }

    @Override
    public void visit(PrintStatement s) {
        programWriter.addToOutput("print(", false);
        this.visit(s.arg);
        programWriter.addToOutput(")", true, true);
    }

    @Override
    public void visit(ReturnStatement s) {
        programWriter.addToOutput("return", true, false);
        this.visit(s.arg);
        programWriter.addToOutput("", false, true);
    }

    @Override
    public void visit(Declaration d) {
        Declaration.visitDeclaration(this, d);
    }

    @Override
    public void visit(FunctionDeclaration d) {
         this.variablesUsedAsGlobal.clear();

        programWriter.addToOutput( "def", true, false);
        this.visit(d.funName);
        int n_args = d.args.size();
        programWriter.addToOutput( "(", false, false);
        for(IdentifierExpression a:d.args){
            this.visit(a);
            if(n_args >1) {
                programWriter.addToOutput(",", true);
            }
            n_args--;
        }
        programWriter.addToOutput( ")", false, false);
        programWriter.addToOutput(":",false, true);
        programWriter.addIndent();
        for(VariableDeclaration vd: d.decls){
            this.visit(vd);
        }

        for(Statement s:d.stats){
            countNestedTail = 0;
            countNestedHead = 0;
            totalNestedHDTL = 0;
            postCall = 0;
            this.visit(s);
        }

        Collections.reverse(this.variablesUsedAsGlobal); // Add them in order used
        for(String globalVar : this.variablesUsedAsGlobal){
            programWriter.addGlobal(globalVar);
        }

        programWriter.removeIndent();

    }

    @Override
    public void visit(VariableDeclaration d) {
        this.visit(d.left);
        programWriter.addToOutput( " =", true, false);
        this.visit(d.right);
        programWriter.addToOutput( "", false, true);
    }
}