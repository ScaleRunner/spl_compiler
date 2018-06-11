package parser.declarations;

import util.Node;
import util.Visitor;

public abstract class Declaration extends Node {

    public static void visitDeclaration(Visitor v, Declaration d){
        if (d.getClass() == FunctionDeclaration.class) {
            v.visit((FunctionDeclaration) d);
        } else if(d.getClass() == VariableDeclaration.class){
            v.visit((VariableDeclaration) d);
        } else throw new UnsupportedOperationException(
                String.format("Class visitation not implemented for class %s", d.getClass().toString())
        );
    }

}
