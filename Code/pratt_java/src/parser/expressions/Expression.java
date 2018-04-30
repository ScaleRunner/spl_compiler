package parser.expressions;

import util.Node;
import util.Visitor;

/**
 * Interface for all expression AST node classes.
 */
public abstract class Expression extends Node {

    public static void visitExpression(Visitor v, Expression e){
        if (e instanceof BooleanExpression) {
            v.visit((BooleanExpression) e);
        } else if(e instanceof CallExpression){
            v.visit((CallExpression) e);
        } else if(e instanceof CharacterExpression){
            v.visit((CharacterExpression) e);
        } else if(e instanceof IdentifierExpression){
            v.visit((IdentifierExpression) e);
        } else if(e instanceof IntegerExpression){
            v.visit((IntegerExpression) e);
        } else if(e instanceof isEmptyExpression){
            v.visit((isEmptyExpression) e);
        } else if (e instanceof ListExpression) {
            v.visit((ListExpression) e);
        } else if(e instanceof OperatorExpression){
            v.visit((OperatorExpression) e);
        } else if(e instanceof PostfixExpression){
            v.visit((PostfixExpression) e);
        } else if(e instanceof PrefixExpression){
            v.visit((PrefixExpression) e);
        } else if (e instanceof TupleExpression) {
            v.visit((TupleExpression) e);
        } else if (e instanceof ReadExpression) {
            v.visit((ReadExpression) e);
        }
    }
    
}