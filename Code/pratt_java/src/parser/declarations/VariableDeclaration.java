package parser.declarations;

import parser.types.Type;
import parser.expressions.Expression;
import parser.expressions.IdentifierExpression;
import util.Visitor;

import java.util.Objects;

public class VariableDeclaration extends Declaration{

    public Type varType;
    public final IdentifierExpression left;
    public final Expression right;
    public final boolean isGlobal;

    public VariableDeclaration(Type varType, IdentifierExpression name, Expression right, boolean isGlobal) {
        this.varType = varType;
        this.left = name;
        this.right = right;
        this.isGlobal = isGlobal;
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
        v.visit(this);
    }
}
