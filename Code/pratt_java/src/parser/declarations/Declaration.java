package parser.declarations;

import util.Visitor;

public interface Declaration {

    /**
     * Pretty-accept the expression to a string.
     */
    void accept(Visitor v);
}
