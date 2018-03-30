package parser.FunType;

import parser.declarations.parselets.TypesParselet;
import util.Node;
import util.Visitor;

import java.util.List;
import java.util.Objects;

public class FunType extends Node {
    List<Type> argsTypes;
    Type returnType;

    public FunType(List<Type> argsTypes , Type returnType){
        this.argsTypes = argsTypes;
        this.returnType = returnType;
    }

    @Override
    public void accept(Visitor v) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunType funType = (FunType) o;
        return Objects.equals(argsTypes, funType.argsTypes) &&
                Objects.equals(returnType, funType.returnType);
    }

    @Override
    public int hashCode() {

        return Objects.hash(argsTypes, returnType);
    }
}
