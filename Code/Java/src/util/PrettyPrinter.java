package util;

import lexer.TokenType;
import parser.AstExprBool;
import parser.AstExprBinOp;
import parser.AstExprInteger;

public class PrettyPrinter implements Visitor {
	StringBuilder result;

	public String getResultString() {
		return result.toString();
	}

	public PrettyPrinter() {
		result = new StringBuilder();
	}

	private void printToken(TokenType t) {
		switch (t) {
		case TOK_PLUS:
			result.append("+");
			break;
		case TOK_MULT:
			result.append("*");
			break;
		case TOK_KW_IF:
			result.append("if");
			break;
		default:
			throw new Error("PrettyPrinter: cannot print token " + t);
		}
	}

	@Override
	public void visit(AstExprInteger i) {
		result.append(i.getValue());
	}

	@Override
	public void visit(AstExprBinOp e) {
		e.getLeft().accept(this);
		printToken(e.getOperator());
		e.getRight().accept(this);
	}

	@Override
	public void visit(AstExprBool astBool) {
		// TODO Auto-generated method stub		
	}
}
