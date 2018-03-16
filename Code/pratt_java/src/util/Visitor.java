package util;

import expressions.*;

import java.util.List;

public interface Visitor {

    void visit(Expression e);

    void visit(List<Expression> es);

    void visit(AssignExpression e);

    void visit(BooleanExpression e);

    void visit(CallExpression e);

    void visit(ConditionalExpression e);

    void visit(IdentifierExpression e);

    void visit(IntegerExpression e);

    void visit(LoopExpression e);

    void visit(OperatorExpression e);

    void visit(PostfixExpression e);

    void visit(PrefixExpression e);

}