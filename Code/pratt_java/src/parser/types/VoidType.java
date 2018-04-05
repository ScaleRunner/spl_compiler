package parser.types;

import typechecker.Substitution;

public class VoidType extends Type{

    private static VoidType instance;

    private VoidType(){

    }

    public static VoidType getInstance(){
        if(instance == null){
            instance = new VoidType();
        }
        return instance;
    }

    @Override
    public Type applySubstitution(Substitution substitution) {
        return this;
    }
}

