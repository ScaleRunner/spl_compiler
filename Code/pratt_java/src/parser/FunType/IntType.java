package parser.FunType;

import util.Visitor;

import java.util.Objects;

public class IntType extends BasicType{

    Basic b;

    public IntType(Basic b){
        this.b = b;
    }

    @Override
    public void accept(Visitor v) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntType intType = (IntType) o;
        return b == intType.b;
    }

    @Override
    public int hashCode() {

        return Objects.hash(b);
    }
}
