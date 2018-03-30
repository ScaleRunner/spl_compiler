package parser.FunType;

import typechecker.Substitution;
import util.Visitor;

import java.util.Objects;

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
    public void accept(Visitor v) {

    }

    @Override
    public Type applySubstitution(Substitution substitution) {
        return this;
    }
}
