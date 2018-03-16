import static org.junit.Assert.*;

import expressions.*;
import lexer.Lexer;
import org.junit.Test;

import parser.Parser;
import util.PrettyPrinter;

public class PrettyPrinterTest {

	@Test
	public void testInteger() {
	    Lexer l = new Lexer("42");
		Parser p = new Parser(l.tokenize());
		IntegerExpression e = (IntegerExpression) p.parseExpression();
		PrettyPrinter pp = new PrettyPrinter();
		e.accept(pp);
		assertEquals("42", pp.getResultString());
	}

	@Test
    public void testIdentifier() {
        Lexer l = new Lexer("hello_its_me_");
        Parser p = new Parser(l.tokenize());
        IdentifierExpression e = (IdentifierExpression) p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("hello_its_me_", pp.getResultString());
    }

    @Test
    public void testBoolean() {
        Lexer l = new Lexer("True");
        Parser p = new Parser(l.tokenize());
        BooleanExpression e = (BooleanExpression) p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("True", pp.getResultString());
    }

    @Test
    public void testCall() {
        Lexer l = new Lexer("foo()");
        Parser p = new Parser(l.tokenize());
        CallExpression e = (CallExpression) p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("foo()", pp.getResultString());
    }

    @Test
    public void testCallArguments() {
        Lexer l = new Lexer("foo(ba, r,1, True)");
        Parser p = new Parser(l.tokenize());
        CallExpression e = (CallExpression) p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("foo(ba, r, 1, True)", pp.getResultString());
    }

    @Test
    public void testPlus() {
        Lexer l = new Lexer("foo + bar+ 1        + True");
        Parser p = new Parser(l.tokenize());
        Expression e = p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("foo + bar + 1 + True", pp.getResultString());
    }

//
//	@Test
//	public void testPlusMult() {
//		Parser p = new Parser("4   + 2*3\n+7* 8 \t  *9");
//		AstNode ast = p.pExpr();
//		PrettyPrinter pp = new PrettyPrinter();
//		ast.accept(pp);
//		assertEquals("4+2*3+7*8*9", pp.getResultString());
//	}

}
