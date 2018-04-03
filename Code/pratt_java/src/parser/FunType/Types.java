package parser.FunType;

public class Types {

    public final static IntType intType = IntType.getInstance();
    public final static CharType charType = CharType.getInstance();
    public final static BoolType boolType = BoolType.getInstance();
    public final static VoidType voidType = VoidType.getInstance();
    public final static VarType varType = VarType.getInstance();

    public static ListType listType(Type type){
        return new ListType(type);
    }

    public static TupleType tupleType(Type left, Type right){
        return new TupleType(left, right);
    }

}
