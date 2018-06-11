package parser.types;

import typechecker.Substitution;

import java.util.Objects;

public class VarType extends Type {

    private static VarType instance = null;
    public Type type = null;

    public VarType(Type type){
        this.type = type;
    }

    public VarType(){
        this(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarType varType = (VarType) o;
        return Objects.equals(type, varType.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public Type applySubstitution(Substitution substitution) {
        return this;
    }

    @Override
    public String toString() {
        return String.format("Var(%s)", type);
    }
}
