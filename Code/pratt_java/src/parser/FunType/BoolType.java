package parser.FunType;

import com.sun.org.apache.xpath.internal.operations.Bool;
import typechecker.Substitution;
import util.Visitor;

import java.util.Objects;

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
    public void accept(Visitor v) {

    }

    @Override
    public Type applySubstitution(Substitution substitution) {
        return this;
    }
}
