package parser.expressions;

import util.Node;
import util.Visitor;

/**
 * Interface for all expression AST node classes.
 */
public abstract class Expression extends Node {

    public static void visitExpression(Visitor v, Expression e){
        if (e.getClass() == BooleanExpression.class) {
            v.visit((BooleanExpression) e);
        } else if(e.getClass() == CallExpression.class){
            v.visit((CallExpression) e);
        } else if(e.getClass() == CharacterExpression.class){
            v.visit((CharacterExpression) e);
        } else if(e.getClass() == IdentifierExpression.class){
            v.visit((IdentifierExpression) e);
        } else if(e.getClass() == IntegerExpression.class){
            v.visit((IntegerExpression) e);
        } else if(e.getClass() == isEmptyExpression.class){
            v.visit((isEmptyExpression) e);
        } else if (e.getClass() == ListExpression.class) {
            v.visit((ListExpression) e);
        } else if(e.getClass() == OperatorExpression.class){
            v.visit((OperatorExpression) e);
        } else if(e.getClass() == PostfixExpression.class){
            v.visit((PostfixExpression) e);
        } else if(e.getClass() == PrefixExpression.class){
            v.visit((PrefixExpression) e);
        } else if (e.getClass() == TupleExpression.class) {
            v.visit((TupleExpression) e);
        }
    }
    
}