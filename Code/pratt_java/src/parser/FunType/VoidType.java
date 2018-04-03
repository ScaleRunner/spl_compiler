package parser.FunType;

import typechecker.Substitution;
import util.Visitor;

import java.util.Objects;

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

