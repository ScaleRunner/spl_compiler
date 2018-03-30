package parser.FunType;

import util.Node;
import util.Visitor;

import java.util.List;

public class RetType extends Node {

    List<Type> rettype;

    public RetType(List<Type> rettype){
        this.rettype = rettype;
    }

    @Override
    public void accept(Visitor v) {

    }
}
