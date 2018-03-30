package parser.FunType;

import typechecker.Substitution;
import util.Visitor;

import java.util.Objects;

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
    public void accept(Visitor v) {

    }

    @Override
    public Type applySubstitution(Substitution substitution) {
        return this;
    }
}
