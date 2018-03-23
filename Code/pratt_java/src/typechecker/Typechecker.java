package typechecker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import parser.expressions.*;
import parser.statements.*;
import util.Node;
import util.TypeError;
import util.Visitor;

public class Typechecker implements Visitor {

	// These are for convenience.
	private static final Type typeInt = new TypeInt();
	private static final Type typeBool = new TypeBool();
    private static final Type typeChar = new TypeChar();

	private HashMap<String, Type> env;

	private List<TypeError> errors = null;

	public boolean typecheck(Node ast) {
		errors = new LinkedList<>();
		env = new HashMap<>();
		ast.accept(this);
		return errors.isEmpty();
	}

	public List<TypeError> getErrors() {
		return errors;
	}

	private void error(String errorMessage) {
		errors.add(new TypeError(errorMessage));
	}

	public void printErrors() {
		for (TypeError e : errors) {
			System.out.println(e.getErrorMessage());
		}
	}

	public String getAllErrors() {
		StringBuilder result = new StringBuilder();
		for (TypeError e : errors) {
			result.append(e.getErrorMessage());
			result.append("\n");
		}
		return result.toString();
	}

	@Override
	public void visit(Expression e) {

	}

	@Override
	public void visit(BooleanExpression e) {
		e.setType(new TypeBool());
	}

	@Override
	public void visit(CallExpression e) {

	}

    @Override
    public void visit(CharacterExpression e) {
        e.setType(new TypeChar());
    }

	@Override
	public void visit(IdentifierExpression e) {

	}

	@Override
	public void visit(IntegerExpression e) {
		e.setType(new TypeInt());
	}

	@Override
	public void visit(ListExpression e) {

	}

	@Override
	public void visit(OperatorExpression e) {
        /*
         * + : char, int
         * - : char, int
         * * : int
         * / : int
         * % : int
         * ==: int, char, bool
         * comperators
         */
        e.left.accept(this);
		e.right.accept(this);

		switch (e.operator) {
            case TOK_PLUS:
            case TOK_MINUS:
            case TOK_MULT:
            case TOK_DIV:
            case TOK_MOD:
                if (e.left.getType().equals(typeInt) && e.right.getType().equals(typeInt))
                    e.setType(typeInt);
                 else
                    error("Typechecker: Type Mismatch");
                break;

		    case TOK_LT:
            case TOK_GT:
            case TOK_GEQ:
            case TOK_EQ:
            case TOK_LEQ:
            case TOK_NEQ:
                if (e.left.getType().equals(typeInt) && e.right.getType().equals(typeInt))
                    e.setType(typeBool);
                break;

		default:
			error("Typechecker: Unknown operator " + e.operator);
			break;
		}
	}

	@Override
	public void visit(PostfixExpression e) {

	}

	@Override
	public void visit(PrefixExpression e) {

	}

	@Override
	public void visit(TupleExpression e) {

	}

	@Override
	public void visit(Statement s) {

	}

	@Override
	public void visit(List<Statement> ss) {

	}

	@Override
	public void visit(AssignStatement s) {

	}

	@Override
	public void visit(CallStatement s) {

	}

	@Override
	public void visit(ConditionalStatement s) {

	}

	@Override
	public void visit(LoopStatement s) {

	}

	@Override
	public void visit(PrintStatement s) {

	}

	@Override
	public void visit(ReturnStatement s) {

	}


//	@Override
//	public void visit(AstExprBinOp e) {
//
//	}
//
//	@Override
//	public void visit(AstExprBool e) {
//		e.setType(new TypeBool());
//	}
//

//	@Override
//	public void visit(AstAbstraction astLetBinding) {
//		// TODO: make a deep copy of the current environment and restore it at
//		// the end of the function! Otherwise the added definition will leak to
//		// outside the let binding.
//		if (astLetBinding.getAstType() == null) {
//			error("Typechecker: Function arguments must have types");
//		}
//		astLetBinding.getAstType().accept(this);
//		env.put(astLetBinding.getIdentifier(), astLetBinding.getAstType()
//				.getType());
//		astLetBinding.getBody().accept(this);
//		astLetBinding.setType(new TypeFunction(astLetBinding.getAstType()
//				.getType(), astLetBinding.getBody().getType()));
//	}
//
//	@Override
//	public void visit(AstTypeInt astTypeInt) {
//		astTypeInt.setType(typeInt);
//	}
//
//	@Override
//	public void visit(AstTypeBool astTypeBool) {
//		astTypeBool.setType(typeBool);
//	}
//
//	@Override
//	public void visit(AstIdentifier astIdentifier) {
//		astIdentifier.setType(env.get(astIdentifier.getIdentifier()));
//	}
//
//	@Override
//	public void visit(AstTypeFunction astTypeFunction) {
//		// TODO Auto-generated method stub
//
//	}

}
