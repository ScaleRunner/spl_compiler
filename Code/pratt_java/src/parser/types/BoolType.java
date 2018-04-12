package parser.types;

import typechecker.Substitution;

public class BoolType extends Type {

    private static BoolType instance = null;

    private BoolType(){

    }

    public static BoolType getInstance(){
        if(instance == null){
            instance = new BoolType();
        }
        return instance;
    }

    @Override
    public Type applySubstitution(Substitution substitution) {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Bool";
    }
}
