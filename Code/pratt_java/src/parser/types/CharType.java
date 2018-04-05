package parser.types;

import typechecker.Substitution;

public class CharType extends Type {

    private static CharType instance = null;

    private CharType(){

    }

    public static CharType getInstance(){
        if(instance == null){
            instance = new CharType();
        }
        return instance;
    }

    @Override
    public Type applySubstitution(Substitution substitution) {
        return this;
    }
}
