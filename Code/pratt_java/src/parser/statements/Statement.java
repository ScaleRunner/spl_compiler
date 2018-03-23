package parser.statements;

import util.Node;
import util.Visitor;

public interface Statement extends Node {

    void accept(Visitor v);
}
