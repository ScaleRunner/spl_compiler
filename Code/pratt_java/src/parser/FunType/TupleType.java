package parser.FunType;

import typechecker.Substitution;
import util.Visitor;

import java.util.Objects;

public class TupleType extends Type{
    public final Type left;
    public final Type right;

    public TupleType(Type left, Type right){
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TupleType tupleType = (TupleType) o;
        return Objects.equals(left, tupleType.left) &&
                Objects.equals(right, tupleType.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(left, right);
    }

    @Override
    public Type applySubstitution(Substitution substitution) {
        throw new UnsupportedOperationException();
    }
}
