package typechecker;

import parser.types.Type;

public class EnvironmentType {
    public final Type type;
    public final boolean isGlobal;
    public final boolean isFunction;
    public final boolean isVarType;
    public EnvironmentType(Type type, boolean isGlobal, boolean isFunction, boolean isVarType){
        this.type = type;
        this.isGlobal = isGlobal;
        this.isFunction = isFunction;
        this.isVarType = isVarType;
    }

    @Override
    public Object clone() {
        return new EnvironmentType(this.type, this.isGlobal, this.isFunction, this.isVarType);
    }
}
