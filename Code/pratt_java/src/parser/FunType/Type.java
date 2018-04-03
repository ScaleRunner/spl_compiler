package parser.FunType;

import typechecker.Substitution;
import util.Node;
import util.Visitor;

public abstract class Type {
    public abstract Type applySubstitution(Substitution substitution);

    // This is an alternative implementation of the unification algorithm.
    // Compare with TypeInference.unify. I'm not sure which one is better java
    // EXCEPT THIS ONE DOESN'T WORK :(((
//    public final Substitution unifyWith(Type t) {
//        if (this.equals(t))
//            return new Substitution();
//        else if (t instanceof TypeInt)
//            return unifyWith((TypeInt) t);
//        else if (t instanceof TypeBool)
//            return unifyWith((TypeBool) t);
//        else if (t instanceof TypeChar)
//            return unifyWith((TypeChar) t);
//        else if (t instanceof TypeFunction)
//            return unifyWith((TypeFunction) t);
//        else if (t instanceof TypeVariable)
//            return unifyWith((TypeVariable) t);
//        return null;
//    }
//
//    protected Substitution unifyWith(TypeInt t) {
//        return cannotUnify(this, t);
//    }
//
//    protected Substitution unifyWith(TypeBool t) {
//        return cannotUnify(this, t);
//    }
//
//    protected Substitution unifyWith(TypeChar t) {
//        return cannotUnify(this, t);
//    }
//
//    protected Substitution unifyWith(TypeFunction t) {
//        return cannotUnify(this, t);
//    }
//
//    protected Substitution unifyWith(TypeVariable t) {
//        Substitution s = new Substitution();
//        s.put(t.getVariable(), this);
//        return s;
//    }

    private Substitution cannotUnify(Type t1, Type t2) {
        throw new Error("cannot unify types " + t1.toString() + " and "
                + t2.toString());
    }

    public static void visitType(Visitor v, Type t){
        if (t.getClass() == BoolType.class) {
            v.visit((BoolType) t);
        } else if(t.getClass() == CharType.class){
            v.visit((CharType) t);
        } else if(t.getClass() == IntType.class){
            v.visit((IntType) t);
        } else if(t.getClass() == ListType.class){
            v.visit((ListType) t);
        } else if (t.getClass() == TupleType.class) {
            v.visit((TupleType) t);
        } else if(t.getClass() == VoidType.class){
            v.visit((VoidType) t);
        } else throw new UnsupportedOperationException(
                String.format("Visitation not implemented for Type %s", t.getClass().toString())
        );
    }
}
