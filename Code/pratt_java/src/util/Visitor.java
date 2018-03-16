package util;

import expressions.*;
import statements.AssignStatement;
import statements.ConditionalStatement;
import statements.LoopStatement;
import statements.Statement;

import java.util.List;

public interface Visitor {

    void visit(Expression e);

    void visit(List<Statement> es);

    void visit(AssignStatement e);

    void visit(BooleanExpression e);

    void visit(CallExpression e);

    void visit(ConditionalStatement e);

    void visit(IdentifierExpression e);

    void visit(IntegerExpression e);

    void visit(LoopStatement e);

    void visit(OperatorExpression e);

    void visit(PostfixExpression e);

    void visit(PrefixExpression e);

}