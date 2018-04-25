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

public class Typechecker implements Visitor {

	// These are for convenience.
	private final Type emptyListType = Types.emptyListType;

	private Environment env;
	private HashMap<String, List<Type>> functionSignatures;

	private List<TypeError> errors;

	public Typechecker(){
		this.functionSignatures = new HashMap<>();
		this.errors = new LinkedList<>();
		this.env = new Environment();
	}
	public boolean typecheck(Node ast) {
		ast.accept(this);
		return errors.isEmpty();
	}

	public List<TypeError> getErrors() {
		return errors;
	}

	private void error(String errorMessage, Node n) {
		errors.add(new TypeError(String.format("%s \n\tError occurred in:\n%s",errorMessage, n)));
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
		e.setType(Types.boolType);
	}

	@Override
	public void visit(CallExpression e) {
		for(Expression exp : e.args)
			this.visit(exp);
		List<Type> funArgs = functionSignatures.get(e.function_name.name);
		if(funArgs == null)
			error(String.format("Function %s was not defined.", e.function_name.name), e);
		else {
			if (funArgs.size() != e.args.size()) {
				error(String.format("Number of arguments in function call do not match. \n\tExpected: %s\n\tActual: %s",
						functionSignatures.get(e.function_name.name).size(), e.args.size()), e);
			} else {
				for (int i = 0; i < funArgs.size(); i++) {
					if (!funArgs.get(i).equals(e.args.get(i).getType()) &&
                            !(funArgs.get(i) instanceof ListType && e.args.get(i).getType() instanceof ListType)) {
						error(String.format("Incompatible types in function call in argument %s\n\tExpected type: %s\n\tActual type: %s",
								i + 1, funArgs.get(i), e.args.get(i).getType()), e);
					}
				}
			}
		}
		e.setType(env.get(e.function_name.name));
	}

	@Override
	public void visit(CharacterExpression e) {
		e.setType(Types.charType);
	}

	@Override
	public void visit(IdentifierExpression e) {
		Type idType = env.get(e.name);
		if(idType == null)
			error(String.format("Variable %s out of scope or undefined.", e.name), e);
		else
			e.setType(env.get(e.name));
	}

	@Override
	public void visit(IntegerExpression e) {
		e.setType(Types.intType);
	}

	@Override
	public void visit(isEmptyExpression e) {
		this.visit(e.arg);
		if(! (e.arg.getType() instanceof ListType))
			error(String.format("isEmpty function needs argument of type List not %s", e.arg.getType()), e);
		e.setType(Types.boolType);

	}

	@Override
	public void visit(ListExpression e) {
		// A list expression starts out as an empty list, so initially its nothing
		e.setType(Types.listType(emptyListType));
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
					if(!e.left.getType().equals(e.right.getType())){
						error("Left and right side of an expression must have the same Type.", e);
					}
					else
						e.setType(Types.intType);
					break;

				case TOK_LT:
				case TOK_GT:
				case TOK_GEQ:
				case TOK_EQ:
				case TOK_LEQ:
				case TOK_NEQ:
					if(e.left.getType() != e.right.getType()){
						error("Left and right side of and expression must have the same Type.", e);
					}
					else
						e.setType(Types.boolType);
					break;
				case TOK_CONS:
					consTypecheckAux(e);
					break;
				default:
                    error(String.format("Invalid operator %s for Type Int and Type %s", e.operator.getValue(), e.right.getType()), e);
					break;
			}

		}
		else if(e.left.getType() instanceof CharType){
			switch (e.operator) {
				case TOK_PLUS:
				case TOK_MINUS:
					if(e.left.getType() != e.right.getType()){
						error("Left and right side of an expression must have the same Type.", e);
					}
					else
						e.setType(Types.charType);
					break;

				case TOK_LT:
				case TOK_GT:
				case TOK_GEQ:
				case TOK_EQ:
				case TOK_LEQ:
				case TOK_NEQ:
					if(e.left.getType() != e.right.getType()){
						error("Left and right side of an expression must have the same Type.", e);
					}
					else
						e.setType(Types.boolType);
					break;
				case TOK_CONS:
					consTypecheckAux(e);
					break;
				default:
                    error(String.format("Invalid operator %s for Type Char and Type %s", e.operator.getValue(), e.right.getType()), e);
					break;
			}
		}
		else if(e.left.getType() instanceof BoolType){
			switch (e.operator) {
				case TOK_NEQ:
				case TOK_EQ:
				case TOK_AND:
				case TOK_OR:
					if(e.left.getType() != e.right.getType()){
						error("Left and right side of an expression must have the same Type.", e);
					}
					else
						e.setType(Types.boolType);
					break;
				case TOK_CONS:
					consTypecheckAux(e);
					break;

				default:
					error(String.format("Invalid operator %s for Type Bool and Type %s", e.operator.getValue(), e.right.getType()), e);
					break;
			}
		}
		else if(e.left.getType() instanceof ListType){
			switch (e.operator) {
				case TOK_CONS:
					consTypecheckAux(e);
					break;
				default:
					error(String.format("Invalid operator %s for ListType %s and Type %s", e.operator.getValue(), e.left.getType(), e.right.getType()), e);
					break;
			}
		}
		else if(e.left.getType() instanceof  TupleType){
			switch (e.operator) {
				case TOK_CONS:
					consTypecheckAux(e);
					break;

				default:
					error(String.format("Invalid operator %s for TupleType %s and Type %s", e.operator.getValue(), e.left.getType(), e.right.getType()), e);
					break;
			}
		}
		else{
			error(String.format("Type %s is not defined for TypeChecking in expressions.", e.left.getType()), e);
		}
	}

	@Override
	public void visit(PostfixExpression e) {
		this.visit(e.left);
		if(e.left.getType() instanceof ListType){
			ListType t = (ListType) e.left.getType();
			switch (e.operator){
				case TOK_HD:
					e.setType(t.listType);
					break;
				case TOK_TL:
					e.setType(t);
					break;
				default:
					error(String.format("Operator %s is undefined for type %s", e.operator.getValue(), t), e);
			}
		} else if(e.left.getType() instanceof TupleType){
			TupleType t = (TupleType) e.left.getType();
			switch (e.operator){
				case TOK_FST:
					e.setType(t.left);
					break;
				case TOK_SND:
					e.setType(t.right);
					break;
				default:
					error(String.format("Operator %s is undefined for type %s", e.operator.getValue(), t), e);
			}
		} else {
			error(String.format("Operator %s is undefined for Type %s", e.operator.getValue(), e.left.getType()), e);
		}
	}

	@Override
	public void visit(PrefixExpression e) {
		this.visit(e.right);
		if(e.operator == TokenType.TOK_NOT){
			if(e.right.getType() == Types.boolType) {
				e.setType(Types.boolType);
			} else{
				error("You can only negate boolean expressions", e);
			}
		}
		else if(e.operator == TokenType.TOK_MINUS){
			if(e.right.getType() == Types.intType){
				e.setType(Types.intType);
			} else {
				error("The minus is only allowed for integer expressions", e);
			}
		} else {
			error(String.format("Unsupported prefix operator '%s' for type '%s'",e.operator.getValue(), e.right.getType()), e);
		}
	}

	@Override
	public void visit(TupleExpression e) {
		this.visit(e.left);
		this.visit(e.right);
		if ((e.left.getType() == Types.voidType) ||(e.right.getType() == Types.voidType)) {
			error("Tuples cannot have listType Void.", e);
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
		if(s.name instanceof IdentifierExpression){
			IdentifierExpression id = (IdentifierExpression) s.name;

			Type variableType = env.get(id.name);

			if(variableType == null){
				error(String.format("Variable %s is not defined", id.name), s);
			} else if(!variableType.equals(s.right.getType()))
				error(String.format("Type %s cannot be assigned to variable %s.\n\tExpected: %s \n\tActual: %s",
						s.right.getType(), id.name, variableType, s.right.getType()),s);
			s.setType(Types.voidType);
		}

		//need a different check, due to id.field.
		else if(s.name.getClass() == PostfixExpression.class){
			this.visit((PostfixExpression)s.name);
			if(!s.name.getType().equals(s.right.getType()))
				error(String.format("Type %s cannot be assigned to variable %s.\n\tExpected: %s \n\tActual: %s",
						s.right.getType(), s.name , s.name.getType(), s.right.getType()),s);
			s.setType(Types.voidType);
		}
	}

	@Override
	public void visit(CallStatement s) {
		for(Expression exp : s.args)
			this.visit(exp);
		List<Type> funArgs = functionSignatures.get(s.function_name.name);
		if(funArgs == null)
			error("Function "+ s.function_name.name + " was not defined.",s);
		else {
			if (funArgs.size() != s.args.size()) {
				error("Number of arguments in function call do not match.\nExpected: " +
						functionSignatures.get(s.function_name.name).size() +
						" and received: " + s.args.size(),s);
			} else {
				for (int i = 0; i < funArgs.size(); i++) {
					if (!funArgs.get(i).equals(s.args.get(i).getType())) {
						error("Incompatible types in function call.\n In argument " + (i + 1) + " expected type: " +
								funArgs.get(i) +
								" and received: " + s.args.get(i).getType(), s);
					}

				}

			}
		}
		s.setType(env.get(s.function_name.name));
	}

	@Override
	public void visit(ConditionalStatement conditionalStatement) {
		this.visit(conditionalStatement.condition);
		if(conditionalStatement.condition.getType() != Types.boolType){
			error(String.format("The condition should be of type Boolean, but it has type '%s' in condition %s",
					conditionalStatement.condition.getType(), conditionalStatement.condition), conditionalStatement);
		}
		Type thenBranchType = this.visit(conditionalStatement.then_expression);
		conditionalStatement.setType(thenBranchType);

		if(conditionalStatement.else_expression.size() != 0){
			Type elseBranchType = this.visit(conditionalStatement.else_expression);
			if(thenBranchType != elseBranchType){
				error(String.format("The return statements of both conditional branches should be of the same type. \n" +
						"\tActual: (then) %s, (else) %s", thenBranchType, elseBranchType), conditionalStatement);
			}
		}
	}

	@Override
	public void visit(LoopStatement s) {
		this.visit(s.condition);
		if(s.condition.getType() != Types.boolType){
			error(String.format("The condition should be of type Boolean and is of type '%s' in condition %s",
					s.condition.getType(), s.condition), s);
		}
		s.setType(this.visit(s.body));
	}

	@Override
	public void visit(PrintStatement s) {
		this.visit(s.arg);
		if(s.arg.getType() instanceof ListType || s.arg.getType() instanceof TupleType){
			error("Print statements cannot handle lists or tuples.", s);
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

	private Type returnType(List<Statement> statements){
		Type returnType = null;
		for(Statement s : statements){
			if(s instanceof ReturnStatement){
				ReturnStatement returnStmt = (ReturnStatement) s;
				returnType = returnStmt.getType();
			} else if(s instanceof ConditionalStatement){
				ConditionalStatement condStmt = (ConditionalStatement) s;
				returnType = returnType(condStmt.then_expression);
			} else if(s instanceof LoopStatement){
				LoopStatement loopStmt = (LoopStatement) s;
				returnType = returnType(loopStmt.body);
			}
		}
		return returnType;
	}

	@Override
	public void visit(FunctionDeclaration d) {
		//Backup original environment to fix let binding
		Environment backup = Environment.deepCopy(env);

		//counter to aux argtypes
		int argsCount = 0;
		//Not sure if I've tested return type void. Probably did.

		//set functiontype
		d.setType(d.funType.returnType);
		env.put(d.funName.name, d.funType.returnType);
		functionSignatures.put(d.funName.name, d.funType.argsTypes);

		//check if arguments and argument types match
		if(d.args.size() != d.funType.argsTypes.size()){
			if(d.args.size() > d.funType.argsTypes.size())
				error("There are more argument types than function arguments", d);
			else
				error("There are more function arguments than argument types", d);
		}

		//set argument types if there are any
		if(!d.args.isEmpty()){
			for(IdentifierExpression id: d.args){
				if(argsCount < d.funType.argsTypes.size())
					env.put(id.name, d.funType.argsTypes.get(argsCount++));
				else
					env.put(id.name, null);
			}
		}


		if(!d.decls.isEmpty()){
			for(VariableDeclaration varDecl : d.decls){
				this.visit(varDecl);
			}
		}

		this.visit(d.stats);
		Type returnType = returnType(d.stats);
		//Had to add equals method for IntType, it seems like the instance system is not working as it should.
		if(!d.funType.returnType.equals(returnType) ){
			if(returnType != null)
				error(String.format("The return type of the function is not equal to the actual return type. " +
						"\n\tExpected: %s \n\tActual: %s", d.funType.returnType, returnType), d);
		}

		env = backup;
		//add function signature to environment, so other functions below it can still use it.
		env.put(d.funName.name, d.funType.returnType);

	}

	@Override
	public void visit(VariableDeclaration d) {
		this.visit(d.right);
		if(d.right.getType() instanceof ListType){
			if(((ListType) d.right.getType()).listType == emptyListType){// we're dealing with an empty list

				if(d.varType instanceof VarType){
					//Only do this if type is var, otherwise a variable such as:
					//[Int] a = [];
					//Would have type Var(null);
					d.varType = Types.varType(d.right.getType());
			    }
			    else{
					d.right.setType(d.varType);
				}


            }
		}
		if(d.varType.equals(d.right.getType()) || d.varType instanceof VarType) {
            if(env.get(d.left.name) != null){
                error(String.format("Variable %s is already defined!", d.left.name), d);
            } else {
                env.put(d.left.name, d.right.getType());
            }
		} else
            error(String.format("Variable %s, of type %s cannot have an assignment of type %s.",
                    d.left, d.varType, d.right.getType()), d);
        d.setType(Types.voidType);
	}

	private void consTypecheckAux(OperatorExpression e){
		if(!(e.right.getType() instanceof ListType)){
			error("Right hand side of cons expression must have listType list", e);
			return;
		}

		//This is need for stuff like []:[] or ([]:(([]:[]:[]):([]:[])))... use your imagination
		if(checkOnlyEmptyListType(e.left.getType()) && (!(checkOnlyEmptyListType(e.right.getType())))){
			error("Empty list on the lhs of cons operation is not allowed if rhs is not only composed by emptyList", e);
			return;
		}


		ListType listTypeRight = (ListType) e.right.getType();

		if(listTypeRight.listType == emptyListType){
			if(e.left.getType() instanceof ListType)
				listTypeRight.listType = ((ListType) e.left.getType()).listType;
			else
				listTypeRight.listType = e.left.getType();
			e.right.setType(listTypeRight);
		}

		//if(listTypeRight.listType instanceof TupleType ||){


		//If rhs has list type of left.type, it's fine
		if(((ListType) e.right.getType()).listType.equals(e.left.getType())){
			e.setType(e.right.getType());
			return;
		}
		//If rhs and lhs have the same list type, new list of any of their types.
		//also fine
		else if(e.right.getType().equals(e.left.getType())){
			e.setType(new ListType(e.right.getType()));
		}
		//If one  of the sides of the expression has an empty list, needs more checks
		else if(checkEmptyListTypeNull(e.left.getType()) || checkEmptyListTypeNull(e.right.getType()) ){
			if(checkEmptyListTypeNull(e.left.getType()) && (!checkEmptyListTypeNull(e.right.getType())) ){
				//Infer lhs type based on right
				fixExpressionEmptyListType(e.left, e.right.getType(), e);

			}
			else if((!checkEmptyListTypeNull(e.left.getType())) && (checkEmptyListTypeNull(e.right.getType()))){
				//Infer rhs type based on left
				fixExpressionEmptyListType(e.right, e.left.getType(), e);
				//e.right.setType(fix);
			}

		}
		else
			error("LHS and RHS of cons expression are not the same", e);

		if((e.left.getType() instanceof ListType) && (e.right.getType() instanceof ListType)){
			if(((ListType) e.left.getType()).listType.equals(((ListType) e.right.getType()).listType)){
				e.setType(new ListType(e.left.getType()));
			}
			else if(((ListType) e.right.getType()).listType.equals(e.left.getType())){
				e.setType(e.right.getType());
			}
			else{
				error("Incompatible list types \n"+e.left.getType()+ " and \n"+e.right.getType(), e);
			}
		}
		else
			e.setType(e.right.getType());

	}

	private boolean checkEmptyListTypeNull(Type e){
	    if(e instanceof TupleType) {
	        Type left = ((TupleType) e).left;
	        Type right = ((TupleType) e).right;
            return (checkEmptyListTypeNull(left) || checkEmptyListTypeNull(right));
	    }
        else if(e instanceof ListType) {
			Type listType = ((ListType) e).listType;
			return listType == emptyListType || checkEmptyListTypeNull(listType);
		}
//        else if (e instanceof EmptyListType)
//        	return true;
        else
            return false;

    }

	private boolean checkOnlyEmptyListType(Type e){
//		if(e instanceof TupleType) {
//			Type left = ((TupleType) e).left;
//			Type right = ((TupleType) e).right;
//			return (checkOnlyEmptyListType(left) && checkOnlyEmptyListType(right));
//		}
//		else
		if(e instanceof ListType) {
			Type listType = ((ListType) e).listType;
			return listType == emptyListType || checkOnlyEmptyListType(listType);
		}
		else return e instanceof EmptyListType;

	}

    private void fixExpressionEmptyListType(Expression emptyListTypeExpr, Type fixer, Expression e) {
		Type toBeFixed = emptyListTypeExpr.getType();

		if(checkEmptyListTypeNull(toBeFixed)) {

			if (toBeFixed instanceof ListType && fixer instanceof ListType) {
				if (((ListType) toBeFixed).listType == emptyListType) {
					//TODO: double check this later
					emptyListTypeExpr.setType(fixer);

				} else if (checkEmptyListTypeNull(toBeFixed)) {
					if (emptyListTypeExpr instanceof OperatorExpression) {
						Expression leftOpExp = ((OperatorExpression) emptyListTypeExpr).left;
						Expression rightOpExp = ((OperatorExpression) emptyListTypeExpr).right;
						//Update types of lhs of expression
						//fixer.listType is necessary because of this example, try to break this
						//[[(Int, [Char])]] g = ((1, []):[]) : (((2, ('a':[])):[]):[]);
						fixExpressionEmptyListType(leftOpExp, ((ListType) fixer).listType, e);
						//Update types of rhs of expression
						fixExpressionEmptyListType(rightOpExp, leftOpExp.getType(), e);

//						);
						if (((OperatorExpression) emptyListTypeExpr).left.getType().equals(((OperatorExpression) emptyListTypeExpr).right.getType())) {
							emptyListTypeExpr.setType(new ListType(((OperatorExpression) emptyListTypeExpr).right.getType()));
						} else if (((OperatorExpression) emptyListTypeExpr).left.getType().equals(((ListType) ((OperatorExpression) emptyListTypeExpr).right.getType()).listType)) {
							emptyListTypeExpr.setType(new ListType(((ListType) ((OperatorExpression) emptyListTypeExpr).right.getType()).listType));
						}

					} else if (emptyListTypeExpr instanceof ListExpression) {
						Type t = ((ListType)emptyListTypeExpr.getType()).listType;
						//if setType with new List(inferEmpt... has problem...
						emptyListTypeExpr.setType(inferEmptyListType(t, fixer, e));
						//emptyListTypeExpr.setType(fixer);
					}
				}

			} else if (toBeFixed instanceof ListType) {
				Type listType = ((ListType) toBeFixed).listType;
				if(listType == emptyListType){
					emptyListTypeExpr.setType(new ListType(fixer));
				}
				else if(listType.getClass() == fixer.getClass()) {
					if (checkEmptyListTypeNull(listType)) {
						//from here
						if (emptyListTypeExpr instanceof OperatorExpression) {
							Expression leftOpExp = ((OperatorExpression) emptyListTypeExpr).left;
							Expression rightOpExp = ((OperatorExpression) emptyListTypeExpr).right;

							fixExpressionEmptyListType(leftOpExp, fixer, e);
							fixExpressionEmptyListType(rightOpExp, leftOpExp.getType(), e);

							if (((OperatorExpression) emptyListTypeExpr).left.getType().equals(((OperatorExpression) emptyListTypeExpr).right.getType())) {
								emptyListTypeExpr.setType(new ListType(((OperatorExpression) emptyListTypeExpr).right.getType()));
							} else if (((OperatorExpression) emptyListTypeExpr).left.getType().equals(((ListType) ((OperatorExpression) emptyListTypeExpr).right.getType()).listType)) {
								emptyListTypeExpr.setType(new ListType(((ListType) ((OperatorExpression) emptyListTypeExpr).right.getType()).listType));
							}
						} else if (emptyListTypeExpr instanceof ListExpression) {
							Type t = ((ListType) emptyListTypeExpr.getType()).listType;
							emptyListTypeExpr.setType(new ListType(inferEmptyListType(t, fixer, e)));
							//emptyListTypeExpr.setType(new ListType(fixer));

							//check if left and right have children...
						}
					} else {
						if (!(listType.equals(fixer))) {
							error("Typechecker: invalid list types", emptyListTypeExpr);
						}
					}
				}
				else error("Typechecker: invalid list types "+ toBeFixed +" "+fixer, e);
			} else if (((toBeFixed instanceof TupleType)) && ((fixer instanceof TupleType))) {
					//if (toBeFixed.equals(emptyListType)) {
					//	emptyListTypeExpr.setType(fixer);
					//} else if (toBeFixed instanceof TupleType && fixer instanceof TupleType) {
					((TupleExpression) emptyListTypeExpr).left.setType((inferEmptyListType(((TupleType) (toBeFixed)).left, ((TupleType) fixer).left, e)));
					((TupleExpression) emptyListTypeExpr).right.setType((inferEmptyListType(((TupleType) (toBeFixed)).right, ((TupleType) fixer).right, e)));
					emptyListTypeExpr.setType(new TupleType(((TupleExpression) emptyListTypeExpr).left.getType(), ((TupleExpression) emptyListTypeExpr).right.getType()));

			}
			else if (toBeFixed instanceof TupleType && ((fixer instanceof ListType))) {
				Type listType = ((ListType) fixer).listType;
				TupleExpression t = (TupleExpression) emptyListTypeExpr;
				Expression leftTupleExpr = t.left;
				Expression rightTupleExpr = t.right;

				if(! (listType instanceof TupleType)) {
					error("EmptyTypeInference mismatch :Lhs has type Tuple and RHS does not. ("+listType+") ", e );
				}
				else{

					TupleType listTuple = (TupleType) listType;
					fixExpressionEmptyListType(leftTupleExpr, listTuple.left, e );
					fixExpressionEmptyListType(rightTupleExpr, listTuple.right, e);
					emptyListTypeExpr.setType(
							new TupleType(
							leftTupleExpr.getType(),
									rightTupleExpr.getType()

					));
				}

			} else {
				error("Typechecker: invalid list types", emptyListTypeExpr);
			}
		}


    }

	//TODO: spend some more time to check this for more cases where things should not work.
	private Type inferEmptyListType(Type left, Type right, Expression e){


		if(left instanceof TupleType && right instanceof TupleType) {
			return new TupleType(
					(inferEmptyListType(((TupleType) left).left ,((TupleType) right).left, e)),
					inferEmptyListType(((TupleType) left).right, ((TupleType) right).right, e));

		}
		else if(left instanceof TupleType && right instanceof ListType){
			//need some more thinking.
			if(((ListType) right).listType instanceof TupleType)
				return new ListType(inferEmptyListType(left, new TupleType(((TupleType) ((ListType) right).listType).left, ((TupleType) ((ListType) right).listType).right), e));
			else{
					//Error
				return null;
			}


		}
		else if(left instanceof ListType && right instanceof TupleType){
			//need some more thinking.
			Type leftListType = ((ListType) left).listType;
			if(leftListType instanceof TupleType)
				return new ListType(new TupleType(
						(inferEmptyListType(((TupleType) ((ListType) left).listType).left ,((TupleType) right).left,e)),
						inferEmptyListType(((TupleType) ((ListType) left).listType).right, ((TupleType) right).right,e)));
			else
				//don't know yet.
				return null;


		}
		else if(left instanceof ListType && right instanceof ListType){
			if(((ListType) right).listType == emptyListType)
				return left;
			else
				return new ListType(inferEmptyListType(((ListType) left).listType, ((ListType) right).listType, e));
		}
		else if(left == emptyListType)
			return right;
		else if (right == emptyListType)
			return left;
		else if (right.equals(left))
			return left;
		error("Type mismatch in empty list inference. Left: "+left + " Right: "+right, e);
		return null;

	}

	public Type getVariableType(String name){
        return env.get(name);
    }

}


