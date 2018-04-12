package parser.types;

import typechecker.Substitution;

import java.util.Objects;

public class ListType extends Type{

    public Type listType;

    public ListType(Type listType){
        this.listType = listType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListType listType = (ListType) o;
        return Objects.equals(this.listType, listType.listType);
    }

    @Override
    public int hashCode() {

        return Objects.hash(listType);
    }

    @Override
    public Type applySubstitution(Substitution substitution) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return String.format("List[%s]", listType);
    }
}

