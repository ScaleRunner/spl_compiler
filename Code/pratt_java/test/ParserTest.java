import static org.junit.Assert.*;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;

import org.junit.Test;
import parser.Parser;

import java.util.Iterator;

public class ParserTest {

	@Test
	public void testInteger() {
	    Lexer l = new Lexer("5");
        Iterator<Token> iter = l.tokenize().iterator();
		Parser p = new Parser(iter);

	}
//
//	@Test
//	public void testNegativeInteger() {
//		SPLParser p = new SPLParser("-5");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprInteger(-5), ast);
//	}
//
//	@Test
//	public void testIdentifier() {
//		SPLParser p = new SPLParser("-5");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprInteger(-5), ast);
//	}
//
//	@Test
//	public void testSimpleAddition() {
//		SPLParser p = new SPLParser("1+2");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBinOp(new AstExprInteger(1),
//				TokenType.TOK_PLUS, new AstExprInteger(2)), ast);
//	}
//
//	@Test
//	public void testMultipleAddition() {
//		SPLParser p = new SPLParser("1+2+3");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBinOp(new AstExprBinOp(new AstExprInteger(1),
//				TokenType.TOK_PLUS, new AstExprInteger(2)), TokenType.TOK_PLUS,
//				new AstExprInteger(3)), ast);
//	}
//
//	@Test
//	public void testMixedAdditionSubtraction() {
//		SPLParser p = new SPLParser("1+2- 3");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBinOp(new AstExprBinOp(new AstExprInteger(1),
//				TokenType.TOK_PLUS, new AstExprInteger(2)),
//				TokenType.TOK_MINUS, new AstExprInteger(3)), ast);
//	}
//
//	@Test
//	public void testMixedAdditionMultiplication1() {
//		SPLParser p = new SPLParser("1*2+3");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBinOp(new AstExprBinOp(new AstExprInteger(1),
//				TokenType.TOK_MULT, new AstExprInteger(2)), TokenType.TOK_PLUS,
//				new AstExprInteger(3)), ast);
//	}
//
//	@Test
//	public void testMixedAdditionMultiplication2() {
//		SPLParser p = new SPLParser("1+2*3");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBinOp(new AstExprInteger(1),
//				TokenType.TOK_PLUS, new AstExprBinOp(new AstExprInteger(2),
//						TokenType.TOK_MULT, new AstExprInteger(3))), ast);
//	}
//
//	@Test
//	public void testMixedAdditionMultiplicationModulo() {
//		SPLParser p = new SPLParser("1+2*3%5");
//		AstExpr ast = p.pExpr();
//		assertEquals(
//				new AstExprBinOp(
//						new AstExprInteger(1),
//						TokenType.TOK_PLUS,
//						new AstExprBinOp(
//								new AstExprBinOp(
//										new AstExprInteger(2),
//										TokenType.TOK_MULT,
//										new AstExprInteger(3)),
//
//										TokenType.TOK_MOD,
//										new AstExprInteger(5)
//
//
//								)
//
//
//						)
//
//				, ast);
//		System.out.println(ast.toString());
//	}
//
//	@Test
//	public void testMultiplicationLeftAssociative() {
//		SPLParser p = new SPLParser("1*2*3");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBinOp(new AstExprBinOp(new AstExprInteger(1),
//				TokenType.TOK_MULT, new AstExprInteger(2)), TokenType.TOK_MULT,
//				new AstExprInteger(3)), ast);
//	}
//
//	@Test
//	public void testBooleanTrue() {
//		SPLParser p = new SPLParser("True");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBool(true), ast);
//	}
//	@Test
//	public void testFunCall() {
//		SPLParser p = new SPLParser("void()");
//		AstExpr ast = p.pExpr();
//		assertEquals(
//				new AstExprFunCall(
//						new AstExprIdentifier("void"))
//
//				, ast);
//		System.out.println(ast.toString());
//	}
//
//	@Test
//	public void testIdentifier() {
//		SPLParser p = new SPLParser("alan");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprIdentifier("alan")
//					, ast);
//		System.out.println(ast.toString());
//	}
//
//	@Test
//	public void testBooleanTrue() {
//		SPLParser p = new SPLParser("True");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBool(true), ast);
//	}
//
//	@Test
//	public void testIdentifierField() {
//		SPLParser p = new SPLParser("alan.hd");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBinOp(
//								new AstExprIdentifier("alan"),
//								TokenType.TOK_DOT,
//								new AstExprField("hd")
//									)
//					, ast);
//		System.out.println(ast.toString());
//	}
//
//	@Test
//	public void testIdentifierMultipleFields() {
//		SPLParser p = new SPLParser("alan.hd.fst");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBinOp(
//								new AstExprIdentifier("alan"),
//								TokenType.TOK_DOT,
//								new AstExprBinOp(
//										new AstExprIdentifier("hd"),
//										TokenType.TOK_DOT,
//										new AstExprField("fst")
//											)
//									)
//					, ast);
//		System.out.println(ast.toString());
//	}
//
//	@Test
//	public void testIdentifierWrongFieldValue() {
//		SPLParser p = new SPLParser("alan.fst");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBinOp(
//								new AstExprIdentifier("alan"),
//								TokenType.TOK_DOT,
//								new AstExprField("fst")
//									)
//					, ast);
//		System.out.println(ast.toString());
//	}
//
//	@Test
//	public void testIdentifierOp2Identifier() {
//		SPLParser p = new SPLParser("alan + abc + az");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBinOp(
//
//										new AstExprIdentifier("alan"),
//										TokenType.TOK_PLUS,
//									new AstExprBinOp(
//									new AstExprIdentifier("abc"),
//
//									TokenType.TOK_PLUS,
//									new AstExprIdentifier("az")))
//					, ast);
//		System.out.println(ast.toString());
//	}
//
//@Test
//	public void testMultiplicationLeftAssociative() {
//		SPLParser p = new SPLParser("1*2*3");
//		AstExpr ast = p.pExpr();
//		assertEquals(
//					new AstExprBinOp(
//								new AstExprBinOp(
//										new AstExprInteger(1),
//										TokenType.TOK_MULT,
//										new AstExprInteger(2)),
//									TokenType.TOK_MULT,
//									new AstExprInteger(3)), ast);
//	}
//
}
