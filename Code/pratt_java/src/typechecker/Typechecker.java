package typechecker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import compiler.CompileException;
import lexer.TokenType;
import parser.types.*;
import parser.declarations.Declaration;
import parser.declarations.FunctionDeclaration;
import parser.declarations.VariableDeclaration;
import parser.expressions.*;
import parser.statements.*;
import util.Node;
import util.TypeError;
import util.Visitor;

import static parser.types.Types.intType;

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
	    this.visit(e.right);
        if(e.operator == TokenType.TOK_NOT){
            if(e.right.getType() == Types.boolType) {
                e.setType(Types.boolType);
            } else{
                error("You can only negate boolean expressions");
            }
        }
	    else if(e.operator == TokenType.TOK_MINUS){
            if(e.right.getType() == Types.intType){
                e.setType(Types.intType);
            } else {
                error("The minus is only allowed for integer expressions");
            }
        } else {
            error(String.format("Unsupported prefix operator '%s' for type '%s'",e.operator.getValue(), e.right.getType()));
        }
	}

	@Override
	public void visit(TupleExpression e) {
        this.visit(e.left);
        this.visit(e.right);
        if ((e.left.getType() == Types.voidType) ||(e.right.getType() == Types.voidType)) {
            error("Tuples cannot have listType Void.");
        }
        e.setType(Types.tupleType(e.left.getType(), e.right.getType() ));

	}

	@Override
	public void visit(Statement s) {
		Statement.visitStatement(this, s);
	}

	public Type visit(List<Statement> statementBlock){
	    Type blockType = Types.voidType;
        for(Statement s : statementBlock){
            this.visit(s);
            if(s instanceof ReturnStatement){
                if(blockType != Types.voidType){ // i.e. you already saw a return statement
                    throw new CompileException("Having two return statements is not allowed.");
                }
                ReturnStatement ret = (ReturnStatement) s;
                blockType = ret.arg.getType();
            }
        }
        return blockType;
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
	public void visit(ConditionalStatement conditionalStatement) {
        this.visit(conditionalStatement.condition);
        if(conditionalStatement.condition.getType() != Types.boolType){
            error(String.format("The condition should be of type Boolean, is of type '%s' in condition %s",
                    conditionalStatement.condition.getType(), conditionalStatement.condition));
        }
        Type thenBranchType = this.visit(conditionalStatement.then_expression);
        conditionalStatement.setType(thenBranchType);

        if(conditionalStatement.else_expression.size() != 0){
            Type elseBranchType = this.visit(conditionalStatement.else_expression);
            if(thenBranchType != elseBranchType){
                error(String.format("The return statements of both conditional branches should be of the same type. \n" +
                        "\tActual: (then) %s, (else) %s", thenBranchType, elseBranchType));
            }
        }
	}

	@Override
	public void visit(LoopStatement s) {
        this.visit(s.condition);
        if(s.condition.getType() != Types.boolType){
            error(String.format("The condition should be of type Boolean, is of type '%s' in condition %s",
                    s.condition.getType(), s.condition));
        }
        s.setType(this.visit(s.body));
	}

	@Override
	public void visit(PrintStatement s) {
	    this.visit(s.arg);
	    if(s.arg.getType() instanceof ListType || s.arg.getType() instanceof TupleType){
	        error("Print statements cannot handle lists or tuples.");
        }
        s.setType(s.arg.getType());
	}

	@Override
	public void visit(ReturnStatement s) {
	    if(s.arg == null){
	        s.setType(Types.voidType);
        } else {
            this.visit(s.arg);
            s.setType(s.arg.getType());
        }
    }

    @Override
    public void visit(Declaration d) {
		Declaration.visitDeclaration(this, d);
    }

	@Override
    public void visit(FunctionDeclaration d) {
		//counter to aux argtypes
		int argsCount = 0;
		//Not sure if I've test return type void. Probably did.

		//set functiontype
		d.setType(d.funType.returnType);
		env.put(d.funName.name, d.funType.returnType);

		//check if arguments and argument types match
		if(d.args.size() != d.funType.argsTypes.size()){
			if(d.args.size() < d.funType.argsTypes.size())
				error("There are missing types for some function arguments");
			else
				error("There are too many argument types for the function arguments");
		}

		//set argument types if there are any
		if(!d.args.isEmpty()){
			for(IdentifierExpression id: d.args){
				env.put(id.name, d.funType.argsTypes.get(argsCount++));
			}
		}

		if(!d.decls.isEmpty()){
			for(VariableDeclaration varDecl : d.decls){
				this.visit(varDecl);
			}
		}

		for(Statement stmt : d.stats){
			this.visit(stmt);
		}


    }

    @Override
    public void visit(VariableDeclaration d) {
		this.visit(d.right);
		if(d.varType.equals(d.right.getType())) {
			env.put(d.left.name, d.varType);

		} else
			error(String.format("Variable %s, of type %s cannot have an assignment of type %s.",
                    d.left, d.varType, d.right.getType()));
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


