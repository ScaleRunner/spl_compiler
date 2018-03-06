package expressions;

import lexer.TokenType;

/**
 * A postfix unary arithmetic expression like "a!".
 */
public class PostfixExpression implements Expression {
    public PostfixExpression(Expression left, TokenType operator) {
        mLeft = left;
        mOperator = operator;
    }

    public void print(StringBuilder builder) {
        builder.append("(");
        mLeft.print(builder);
        builder.append(mOperator.toString()).append(")");
    }

    private final Expression mLeft;
    private final TokenType mOperator;
}
