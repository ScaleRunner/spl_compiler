package parser.statements;

import util.Visitor;

public interface Statement {

    void accept(Visitor v);
}
