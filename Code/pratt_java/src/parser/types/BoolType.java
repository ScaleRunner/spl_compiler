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
}
