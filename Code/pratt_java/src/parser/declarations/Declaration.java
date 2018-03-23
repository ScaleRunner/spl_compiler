package parser.declarations;

import util.Node;
import util.Visitor;

public interface Declaration extends Node {

    /**
     * Pretty-accept the expression to a string.
     */
    void accept(Visitor v);
}
