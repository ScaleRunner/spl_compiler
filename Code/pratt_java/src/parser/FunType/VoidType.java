package parser.FunType;

import util.Visitor;

import java.util.Objects;

public class VoidType extends Type{
    Return b;

    public VoidType(Return b){
        this.b = b;
    }

    @Override
    public void accept(Visitor v) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoidType voidType = (VoidType) o;
        return b == voidType.b;
    }

    @Override
    public int hashCode() {

        return Objects.hash(b);
    }
}
