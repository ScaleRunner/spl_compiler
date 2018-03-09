import static org.junit.Assert.*;

import expressions.*;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;

import org.junit.Test;
import parser.Parser;

import java.util.List;

public class ParserTest {

    @Test
    public void single_addition() {
        Lexer l = new Lexer("a + b");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression left = new IdentifierExpression("a");
        Expression right = new IdentifierExpression("b");
        Expression expected = new OperatorExpression(left, TokenType.TOK_PLUS, right);
        assertEquals(result, expected);
    }

    @Test
    public void nested_addition() {
        // Expected: (a + b) + c
        Lexer l = new Lexer("a + b + c");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        // First make the OperatorExpression for the left
        Expression left_left = new IdentifierExpression("a");
        Expression left_right = new IdentifierExpression("b");
        Expression left = new OperatorExpression(left_left, TokenType.TOK_PLUS, left_right);

        // Now for the right
        Expression right = new IdentifierExpression("c");
        Expression expected = new OperatorExpression(left, TokenType.TOK_PLUS, right);
        assertEquals(result, expected);
    }

	@Test
	public void testAssignment() {
	    Lexer l = new Lexer("a = b");
        List<Token> tokens = l.tokenize();
		Parser p = new Parser(tokens);
		Expression result = p.parseExpression();

		String left = "a";
		Expression right = new IdentifierExpression("b");
		Expression actual = new AssignExpression(left, right);
		assertEquals(result, actual);
	}

	@Test
	public void testNegativeIdentifier() {
        Lexer l = new Lexer("-a");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression right = new IdentifierExpression("a");
        Expression expected = new PrefixExpression(TokenType.TOK_MINUS, right);
        assertEquals(result, expected);
	}

    @Test
    public void testNestedNegativeIdentifier() {
        //Expected: -(-(-a))
        Lexer l = new Lexer("---a");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression inner = new IdentifierExpression("a");
        Expression expected = new PrefixExpression(
                TokenType.TOK_MINUS, new PrefixExpression(
                        TokenType.TOK_MINUS, new PrefixExpression(
                                TokenType.TOK_MINUS, inner
                        )
                )
        );
        StringBuilder builder = new StringBuilder();
        result.print(builder);
        System.out.println(builder.toString());
        assertEquals(result, expected);
    }

	@Test
	public void testMultiplication() {
        Lexer l = new Lexer("a * b");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression left = new IdentifierExpression("a");
        Expression right = new IdentifierExpression("b");
        Expression expected = new OperatorExpression(left, TokenType.TOK_MULT, right);
        assertEquals(result, expected);
	}

	@Test
	public void testMultiplicationAssociativity() {
        // Expected: (a + (((b * c) * d)) - e
        Lexer l = new Lexer("a + b * c * d - e");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression expected = new OperatorExpression(
                new OperatorExpression(
                        new IdentifierExpression("a"),
                        TokenType.TOK_PLUS,
                        new OperatorExpression(
                                new OperatorExpression(
                                        new IdentifierExpression("b"),
                                        TokenType.TOK_MULT,
                                        new IdentifierExpression("c")
                                ),
                                TokenType.TOK_MULT,
                                new IdentifierExpression("d")
                        )
                ),
                TokenType.TOK_MINUS,
                new IdentifierExpression("e")
        );
        assertEquals(result, expected);
	}

    @Test
	public void testBoolean() {
        Lexer l = new Lexer("value = True");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        String left = "value";
        Expression right = new BooleanExpression(true);
        Expression actual = new AssignExpression(left, right);
        assertEquals(result, actual);
	}

    @Test
    public void testInteger() {
        Lexer l = new Lexer("value = 1");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        String left = "value";
        Expression right = new IntegerExpression(1);
        Expression actual = new AssignExpression(left, right);
        assertEquals(result, actual);
    }

    @Test
    public void testModulo() {
        Lexer l = new Lexer("2 % 4");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression left = new IntegerExpression(2);
        Expression right = new IntegerExpression(4);
        Expression actual = new OperatorExpression(left, TokenType.TOK_MOD, right);
        assertEquals(result, actual);
    }



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
}
