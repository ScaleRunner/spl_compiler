package expressions;

import util.Visitor;

/**
 * Interface for all expression AST node classes.
 */
public interface Expression {
    /**
     * Pretty-accept the expression to a string.
     */
    void accept(Visitor v);
}