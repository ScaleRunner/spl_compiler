package parser.FunType;

import typechecker.Substitution;
import util.Visitor;

public class VarType extends Type {

    private static VarType instance = null;

    private VarType(){

    }

    public static VarType getInstance(){
        if(instance == null){
            instance = new VarType();
        }
        return instance;
    }

    @Override
    public Type applySubstitution(Substitution substitution) {
        return this;
    }
}
