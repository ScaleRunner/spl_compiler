package typechecker;

import parser.types.Type;

public class EnvironmentType {
    public Type type;
    public boolean isGlobal;

    EnvironmentType(){
        this.type = null;
        this.isGlobal = false;
    }

    EnvironmentType(Type type, boolean isGlobal){
        this.type = type;
        this.isGlobal = isGlobal;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        EnvironmentType copiedEnvironmentType = new EnvironmentType();
        copiedEnvironmentType.type = this.type;
        copiedEnvironmentType.isGlobal = this.isGlobal;
        return copiedEnvironmentType;
    }

//    public static EnvironmentType deepCopyEnvironmentType(EnvironmentType toBeCopied){
//
//    }

}
