package parser.FunType;

import util.Visitor;

import java.util.Objects;

public class BoolType extends BasicType{
    Basic b;

    public BoolType(Basic b){
        this.b = b;
    }

    @Override
    public void accept(Visitor v) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoolType boolType = (BoolType) o;
        return b == boolType.b;
    }

    @Override
    public int hashCode() {

        return Objects.hash(b);
    }
}
