package expressions;

import lexer.TokenType;

/**
 * A binary arithmetic expression like "a + b" or "c ^ d".
 */
public class OperatorExpression implements Expression {
    public OperatorExpression(Expression left, TokenType operator, Expression right) {
        mLeft = left;
        mOperator = operator;
        mRight = right;
    }

    public void print(StringBuilder builder) {
        builder.append("(");
        mLeft.print(builder);
        builder.append(" ").append(mOperator.toString()).append(" ");
        mRight.print(builder);
        builder.append(")");
    }

    private final Expression mLeft;
    private final TokenType mOperator;
    private final Expression mRight;
}
