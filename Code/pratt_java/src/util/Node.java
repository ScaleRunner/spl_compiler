package util;

import parser.types.Type;

public abstract class Node {

    private Type type = null;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public abstract void accept(Visitor v);
}
