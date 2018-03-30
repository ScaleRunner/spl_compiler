package parser.FunType;

import util.Visitor;

import java.util.Objects;

public class TupleType extends Type{
    Type left;
    Type right;

    public TupleType(Type left, Type right){
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(Visitor v) {

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
}
