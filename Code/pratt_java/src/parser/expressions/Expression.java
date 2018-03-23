package parser.expressions;

import util.Node;
import util.Visitor;

/**
 * Interface for all expression AST node classes.
 */
public interface Expression extends Node {
    /**
     * Pretty-accept the expression to a string.
     */
    void accept(Visitor v);
}