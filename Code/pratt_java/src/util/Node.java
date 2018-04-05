package util;

import parser.declarations.Declaration;
import parser.expressions.Expression;
import parser.statements.Statement;
import parser.types.Type;

public abstract class Node {

    private Type type = null;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public abstract void accept(Visitor v);

    static void visitNode(Visitor v, Node n){
        if(n instanceof Declaration){
            Declaration.visitDeclaration(v, (Declaration) n);
        } else if(n instanceof Statement){
            Statement.visitStatement(v, (Statement) n);
        } else if(n instanceof Expression){
            Expression.visitExpression(v, (Expression) n);
        } else {
            throw new UnsupportedOperationException(String.format("Cannot visit Node of type %s", n.getClass()));
        }
    }

    @Override
    public String toString() {
        PrettyPrinter pp = new PrettyPrinter();
        visitNode(pp, this);
        return pp.getResultString();
    }
}
