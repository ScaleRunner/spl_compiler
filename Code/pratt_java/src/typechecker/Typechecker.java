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
	private final Type typeInt = Types.intType;
	private final Type typeBool = Types.boolType;
    private final Type typeChar = Types.charType;
    private final Type typeVoid = Types.voidType;

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
		for(Expression exp : e.args)
			this.visit(exp);
		List<Type> funArgs = functionSignatures.get(e.function_name.name);
		if(funArgs == null)
			error("Function "+ e.function_name.name + " was not defined.");
		else {
			if (funArgs.size() != e.args.size()) {
				error("Number of arguments in function call do not match.\nExpected: " +
						functionSignatures.get(e.function_name.name).size() +
						" and received: " + e.args.size());
			} else {
				for (int i = 0; i < funArgs.size(); i++) {
					if (!funArgs.get(i).equals(e.args.get(i).getType())) {
						error("Incompatible types in function call.\n In argument " + (i + 1) + " expected type: " +
								funArgs.get(i) +
								" and received: " + e.args.get(i).getType());
					}

				}

			}
		}
		e.setType(env.get(e.function_name.name));
	}

    @Override
    public void visit(CharacterExpression e) {
        e.setType(typeChar);
    }

	@Override
	public void visit(IdentifierExpression e) {
		Type idType = env.get(e.name);
		if(idType == null)
			error("Variable "+ e.name+" out of scope or undefined.");
		else
        e.setType(env.get(e.name));
	}

	@Override
	public void visit(IntegerExpression e) {
		e.setType(typeInt);
	}

	@Override
	public void visit(isEmptyExpression e) {
		this.visit(e.arg);
		if(! (e.arg.getType() instanceof ListType))
			error("isEmpty function needs argument of type List not " +e.arg.getType());
		e.setType(Types.boolType);

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
					if(!e.left.getType().equals(e.right.getType())){
						error("Typechecker: Left and right side of an expression must have the same Type.");
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
						error("Typechecker: Left and right side of and expression must have the same Type.");
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
						error("Typechecker: Left and right side of and expression must have the same Type.");
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
						error("Typechecker: Left and right side of and expression must have the same Type.");
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
						error("Typechecker: Left and right side of and expression must have the same Type.");
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
					error("Typechecker: Invalid operator " + e.operator + " for Type Bool");
					break;
			}
		}
		else{
			error("Invalid operator Type "+ e.left.getType()+" for expression " + e.operator);
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
                    error(String.format("Operator %s is undefined for type %s", e.operator, t));
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
                    error(String.format("Operator %s is undefined for type %s", e.operator, t));
            }
        } else {
            error(String.format("Operator %s is undefined for type %s", e.operator, e.left.getType()));
        }
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
        IdentifierExpression id = (IdentifierExpression) s.name;

        Type variableType = env.get(id.name);

        if(variableType == null){
            error(String.format("Variable %s is not defined", id.name));
        } else if(variableType != s.right.getType())
			error(String.format("Type %s cannot be assigned to variable %s.\n\t Expected: %s \n\t Actual: %s",
                    s.right.getType(), id.name, variableType, s.right.getType()));
		s.setType(Types.voidType);
	}

	@Override
	public void visit(CallStatement s) {
		for(Expression e : s.args)
			this.visit(e);
		List<Type> funArgs = functionSignatures.get(s.function_name.name);
        if(funArgs.size() != s.args.size()) {
			error("Number of arguments in function call does not match.\nExpected: " +
					functionSignatures.get(s.function_name.name).size() +
					" parameter and received: " + s.args.size() + "parameters");
		}
		else{
        	for(int i = 0; i < funArgs.size(); i++){
        		if(!funArgs.get(i).equals(s.args.get(i))){
					error("Incompatible types in function call.\n In argument "+ (i+1) +"expected type: " +
							funArgs.get(i) +
							"and received: " + s.args.get(i));
        		}

			}

		}
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

		this.visit(d.stats);
		Type returnType = returnType(d.stats);
		//Had to add equals method for IntType, it seems like the instance system is not working as it should.
		if(!d.funType.returnType.equals(returnType) ){
			if(returnType != null)
		    	error(String.format("The return type of the function is not equal to the actual return type. " +
                    "\n\tExpected: %s \n\t Actual: %s", d.funType.returnType, returnType));
        }

        env = backup;
		//add function signature to environment, so other functions below it can still use it.
		env.put(d.funName.name, d.funType.returnType);

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


