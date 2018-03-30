package parser.FunType;

import util.Visitor;

import java.util.Objects;

public class CharType extends BasicType {

    Basic b;

    public CharType(Basic b){
        this.b = b;
    }

    @Override
    public void accept(Visitor v) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharType charType = (CharType) o;
        return b == charType.b;
    }

    @Override
    public int hashCode() {

        return Objects.hash(b);
    }
}
