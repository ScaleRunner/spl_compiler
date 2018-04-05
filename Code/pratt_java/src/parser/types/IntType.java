package parser.types;

import typechecker.Substitution;

public class IntType extends Type{

    private static IntType instance;

    private IntType(){

    }

    public static IntType getInstance(){
        if(instance == null){
            instance = new IntType();
        }
        return instance;
    }

    @Override
    public Type applySubstitution(Substitution substitution) {
        return this;
    }
}
