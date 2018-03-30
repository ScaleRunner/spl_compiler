package parser.FunType;

import util.Node;
import util.Visitor;

import java.util.List;

public class FType extends Node {
    List<Type> args;

    public FType(List<Type> args){
        this.args = args;
    }
    @Override
    public void accept(Visitor v) {

    }
}
