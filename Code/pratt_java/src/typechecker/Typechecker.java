package typechecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import parser.FunType.*;
import parser.declarations.Declaration;
import parser.declarations.FunctionDeclaration;
import parser.declarations.VariableDeclaration;
import parser.expressions.*;
import parser.statements.*;
import util.Node;
import util.TypeError;
import util.Visitor;

import static parser.FunType.Types.intType;

public class Typechecker implements Visitor {

	// These are for convenience.
	private final Type typeInt = intType;
	private final Type typeBool = Types.boolType;
    private final Type typeChar = Types.charType;
    private final Type typeVoid = Types.voidType;

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
		Expression.visitExpression(this, e);
	}

	@Override
	public void visit(BooleanExpression e) {
		e.setType(typeBool);
	}

	@Override
	public void visit(CallExpression e) {
        e.setType(env.get(e.function_name.name));
	}

    @Override
    public void visit(CharacterExpression e) {
        e.setType(typeChar);
    }

	@Override
	public void visit(IdentifierExpression e) {
        e.setType(env.get(e.name));
	}

	@Override
	public void visit(IntegerExpression e) {
		e.setType(typeInt);
	}

	@Override
	public void visit(ListExpression e) {
	    // A list expression starts out as an empty list, so initially its nothing
        e.setType(Types.listType(null));
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

		if(e.left.getType() instanceof IntType){

			switch (e.operator) {
				case TOK_PLUS:
				case TOK_MINUS:
				case TOK_MULT:
				case TOK_DIV:
				case TOK_MOD:
					if(e.left.getType() != e.right.getType()){
						error("Typechecker: Left and right side of and expression must have the same listType.");
					}
					else
						e.setType(typeInt);
					break;

				case TOK_LT:
				case TOK_GT:
				case TOK_GEQ:
				case TOK_EQ:
				case TOK_LEQ:
				case TOK_NEQ:
					if(e.left.getType() != e.right.getType()){
						error("Typechecker: Left and right side of and expression must have the same listType.");
					}
					else
						e.setType(typeBool);
					break;
				case TOK_CONS:
					consTypecheckAux(e);
					break;
				default:
					error("Typechecker: Unknown operator " + e.operator);
					break;
			}

		}
		else if(e.left.getType() instanceof CharType){
			switch (e.operator) {
				case TOK_PLUS:
				case TOK_MINUS:
					if(e.left.getType() != e.right.getType()){
						error("Typechecker: Left and right side of and expression must have the same listType.");
					}
					else
						e.setType(typeChar);
					break;

				case TOK_LT:
				case TOK_GT:
				case TOK_GEQ:
				case TOK_EQ:
				case TOK_LEQ:
				case TOK_NEQ:
					if(e.left.getType() != e.right.getType()){
						error("Typechecker: Left and right side of and expression must have the same listType.");
					}
					else
						e.setType(typeBool);
					break;
				case TOK_CONS:
					consTypecheckAux(e);
					break;
				default:
					error("Typechecker: Unknown operator " + e.operator);
					break;
			}
		}
		else if(e.left.getType() instanceof BoolType){
			switch (e.operator) {
				case TOK_LT:
				case TOK_GT:
				case TOK_GEQ:
				case TOK_EQ:
				case TOK_LEQ:
				case TOK_NEQ:
				case TOK_AND:
				case TOK_OR:
					if(e.left.getType() != e.right.getType()){
						error("Typechecker: Left and right side of and expression must have the same listType.");
					}
					else
						e.setType(typeBool);
					break;
				case TOK_CONS:
					consTypecheckAux(e);
					break;

				default:
					error("Typechecker: Invalid operator " + e.operator + " for listType Bool");
					break;
				}
		}
//		else if(e.getType() instanceof ListType){
//
//		}
		else if(e.left.getType() instanceof  TupleType){
			switch (e.operator) {
				case TOK_CONS:
					consTypecheckAux(e);
					break;

				default:
					error("Typechecker: Invalid operator " + e.operator + " for listType Bool");
					break;
			}
		}
		else{
			error("Invalid listType for expression " + e.operator);
		}






	}

	@Override
	public void visit(PostfixExpression e) {
        e.setType(e.left.getType());
	}

	@Override
	public void visit(PrefixExpression e) {
        e.setType(e.right.getType());
	}

	@Override
	public void visit(TupleExpression e) {
        if ((e.left.getType() == Types.voidType) ||(e.right.getType() == Types.voidType)) {
			error("Tuples cannot have listType Void.");
		}
		this.visit(e.left);
        this.visit(e.right);
		e.setType(Types.tupleType(e.left.getType(), e.right.getType() ));

	}

	@Override
	public void visit(Statement s) {
		Statement.visitStatement(this, s);

	}

	@Override
	public void visit(AssignStatement s) {
		this.visit(s.right);
		if(env.get(s.name) != s.right.getType())
			error("Type "+env.get(s.name)+ " cannot be assigned using type "+ s.right.getType() );
		s.setType(Types.voidType);
	}

	@Override
	public void visit(CallStatement s) {
        s.setType(env.get(s.function_name.name));
	}

	@Override
	public void visit(ConditionalStatement s) {

	}

	@Override
	public void visit(LoopStatement s) {

	}

	@Override
	public void visit(PrintStatement s) {
        s.setType(s.arg.getType());
	}

	@Override
	public void visit(ReturnStatement s) {
        s.setType(s.arg.getType());
	}

    @Override
    public void visit(Declaration d) {
		Declaration.visitDeclaration(this, d);

    }

	@Override
    public void visit(FunctionDeclaration d) {

    }

    @Override
    public void visit(VariableDeclaration d) {
		this.visit(d.right);
		if(d.varType.equals(d.right.getType())) {
			env.put(d.left.name, d.varType);

		}
		else
			error("Type " + d.varType.toString() + "cannot receive type " + d.right.getType().toString());
		d.setType(Types.voidType);


    }

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

	private void consTypecheckAux(OperatorExpression e){
		if(!(e.right.getType() instanceof ListType)){
			error("Typechecker: Right hand side of cons expression must have listType list");
		}

		ListType listTypeRight = (ListType) e.right.getType();

		if(listTypeRight.listType == null){
			listTypeRight.listType = e.left.getType();
			e.right.setType(listTypeRight);
		}

		if(!e.left.getType().equals(listTypeRight.listType)){
			error("Typechecker: Left and right side of and expression must have the same listType. "+
					e.left.getType() + ' ' + listTypeRight.listType );
		}

		e.setType(listTypeRight);
	}

}


