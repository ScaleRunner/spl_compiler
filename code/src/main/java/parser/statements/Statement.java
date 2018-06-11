package parser.statements;

import util.Node;
import util.Visitor;

public abstract class Statement extends Node {

    public static void visitStatement(Visitor v, Statement s){
        if (s.getClass() == AssignStatement.class) {
            v.visit((AssignStatement) s);
        } else if (s.getClass() == CallStatement.class) {
            v.visit((CallStatement) s);
        } else if (s.getClass() == ConditionalStatement.class) {
            v.visit((ConditionalStatement) s);
        } else if (s.getClass() == LoopStatement.class) {
            v.visit((LoopStatement) s);
        } else if (s.getClass() == PrintStatement.class) {
            v.visit((PrintStatement) s);
        } else if (s.getClass() == ReturnStatement.class) {
            v.visit((ReturnStatement) s);
        } else {
            throw new UnsupportedOperationException(
                    String.format("The statement %s is not found...", s.getClass().toString())
            );
        }
    }

}
