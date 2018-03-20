package declarations;

import expressions.Expression;
import lexer.TokenType;
import util.Visitor;

import java.util.Objects;

public class VariableDeclaration implements Declaration{

    public final TokenType varType;
    public final Expression left;
    public final Expression right;

    public VariableDeclaration(TokenType varType, Expression name, Expression right) {
        this.varType = varType;
        this.left = name;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableDeclaration that = (VariableDeclaration) o;
        return varType == that.varType &&
                Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(varType, left, right);
    }

    @Override
    public void accept(Visitor v) {

    }
}
