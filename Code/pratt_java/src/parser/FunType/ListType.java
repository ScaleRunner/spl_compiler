package parser.FunType;

import util.Visitor;

import java.util.Objects;

public class ListType extends Type{
    Type type;

    public ListType(Type type){
        this.type = type;
    }

    @Override
    public void accept(Visitor v) {
        
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListType listType = (ListType) o;
        return Objects.equals(type, listType.type);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type);
    }
}
