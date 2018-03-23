import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import parser.Parser;
import typechecker.TypeBool;
import typechecker.TypeFunction;
import typechecker.TypeInt;
import typechecker.Typechecker;

public class TypecheckerTest {
	private Typechecker tc = null;

	@Before
	public void setUp() throws Exception {
		tc = new Typechecker();
	}
//
//	private void assertTypecheckSuccess() {
//		assertTrue(tc.getAllErrors(), tc.getAllErrors().length() == 0);
//	}

//	private AstNode typecheckExpr(String input) {
//		Parser p = new Parser(input);
//		AstExpr expr = p.pExpr();
//		tc.typecheck(expr);
//		return expr;
//	}
//
//	@Test
//	public void testCompareTypes() {
//		assertEquals(new TypeInt(), new TypeInt());
//		assertEquals(new TypeBool(), new TypeBool());
//		assertNotEquals(new TypeInt(), new TypeBool());
//	}
//
//	@Test
//	public void testIntegerConstant() {
//		AstNode e = typecheckExpr("5");
//		assertTypecheckSuccess();
//		assertEquals(new TypeInt(), e.getType());
//	}
//
//	@Test
//	public void testBooleanConstantTrue() {
//		AstNode eTrue = typecheckExpr("True");
//		AstNode eFalse = typecheckExpr("False");
//		assertTypecheckSuccess();
//		assertEquals(new TypeBool(), eTrue.getType());
//		assertEquals(new TypeBool(), eFalse.getType());
//	}
//
//	@Test
//	public void testPlus() {
//		AstNode e = typecheckExpr("5 + 3");
//		assertTypecheckSuccess();
//		assertEquals(new TypeInt(), e.getType());
//	}
//
//	@Test
//	public void testLessThan() {
//		AstNode e = typecheckExpr("5 < 3");
//		assertTypecheckSuccess();
//		assertEquals(new TypeBool(), e.getType());
//	}
//
//	@Test
//	public void testLetUnrelated() {
//		AstNode e = typecheckExpr("fun b : Bool . 5");
//		assertTypecheckSuccess();
//		assertEquals(new TypeFunction(new TypeBool(), new TypeInt()),
//				e.getType());
//	}
//
//	@Test
//	public void testLambdaBool() {
//		AstNode e = typecheckExpr("fun x : Bool. x");
//		assertTypecheckSuccess();
//		assertEquals(new TypeFunction(new TypeBool(), new TypeBool()),
//				e.getType());
//	}
//
//	@Test
//	public void testLetInt() {
//		AstNode e = typecheckExpr("fun x : Int . x + 1");
//		assertTypecheckSuccess();
//		assertEquals(new TypeFunction(new TypeInt(), new TypeInt()),
//				e.getType());
//	}
//
//	@Test
//	public void testNestedLet() {
//		AstNode e = typecheckExpr("fun x : Int . fun y : Int . x + y");
//		assertTypecheckSuccess();
//		assertEquals(new TypeFunction(new TypeInt(), new TypeFunction(
//				new TypeInt(), new TypeInt())), e.getType());
//	}

}
