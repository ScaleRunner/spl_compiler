package typechecker;

import parser.types.Type;

public class EnvironmentType {
    public final Type type;
    public final boolean isGlobal;
    public final boolean isFunction;

    public EnvironmentType(Type type, boolean isGlobal, boolean isFunction){
        this.type = type;
        this.isGlobal = isGlobal;
        this.isFunction = isFunction;
    }

    @Override
    public Object clone() {
        return new EnvironmentType(this.type, this.isGlobal, this.isFunction);
    }
}
