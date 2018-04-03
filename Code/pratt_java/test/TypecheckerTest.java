import static org.junit.Assert.*;

import lexer.Lexer;
import org.junit.Before;
import org.junit.Test;

import parser.FunType.Type;
import parser.FunType.Types;
import parser.Parser;
import parser.expressions.Expression;
import typechecker.*;
import util.Node;

public class TypecheckerTest {
	private Typechecker tc = null;
	// These are for convenience.
	private final Type typeInt = Types.intType;
	private final Type typeBool = Types.boolType;
	private final Type typeChar = Types.charType;
	private final Type typeVoid = Types.voidType;


	@Before
	public void setUp(){
		tc = new Typechecker();
	}

	private void assertTypecheckSuccess() {
		assertTrue(tc.getAllErrors(), tc.getAllErrors().length() == 0);
	}

	private Node typecheckExpr(String input) {
		Lexer l = new Lexer(input);
		Parser p = new Parser(l.tokenize());
		Expression expr = p.parseExpression();
		tc.typecheck(expr);
		return expr;
	}

	@Test
	public void testCompareTypes() {
		assertEquals(Types.intType, Types.intType);
		assertEquals(Types.boolType, Types.boolType);
		assertNotEquals(Types.boolType, Types.intType);
	}

	@Test
	public void testIntegerConstant() {
		Node e = typecheckExpr("5");
		assertTypecheckSuccess();
		assertEquals(typeInt, e.getType());
	}

    @Test
    public void testCharacterConstant() {
        Node e = typecheckExpr("'a'");
        assertTypecheckSuccess();
        assertEquals(typeChar, e.getType());
    }

    @Test
	public void testBooleanConstantTrueAndFalse() {
		Node eTrue = typecheckExpr("True");
		Node eFalse = typecheckExpr("False");
		assertTypecheckSuccess();
		assertEquals(Types.boolType, eTrue.getType());
		assertEquals(Types.boolType, eFalse.getType());
	}

	@Test
	public void testPlus() {
		Node e = typecheckExpr("5 + 3");
		assertTypecheckSuccess();
		assertEquals(typeInt, e.getType());
	}

	@Test
	public void testLessThan() {
		Node e = typecheckExpr("5 < 3");
		assertTypecheckSuccess();
		assertEquals(Types.boolType, e.getType());
	}

	@Test
	public void testLessThanChar() {
		Node e = typecheckExpr("'5' < '3'");
		assertTypecheckSuccess();
		assertEquals(Types.boolType, e.getType());
	}

	@Test
	public void testConsIntEmpty() {
		Node e = typecheckExpr("5 : []");
		assertTypecheckSuccess();
		assertEquals(Types.listType(Types.intType), e.getType());
	}

	@Test
	public void testConsIntNotEmpty() {
		Node e = typecheckExpr("1:2:3:[]");
		assertTypecheckSuccess();
		assertEquals(Types.listType(Types.intType), e.getType());
	}

	@Test
	public void testConsCharNotEmpty() {
		Node e = typecheckExpr("'a':'b':'c':[]");
		assertTypecheckSuccess();
		assertEquals(Types.listType(Types.charType), e.getType());
	}

	@Test
	public void testConsBoolNotEmpty() {
		Node e = typecheckExpr("False:False:True:[]");
		assertTypecheckSuccess();
		assertEquals(Types.listType(Types.boolType), e.getType());
	}

	@Test
	public void testConsTupleNotEmpty() {
		Node e = typecheckExpr("(1,'a'):(2,'b'):(3,'c'):[]");
		assertTypecheckSuccess();
		assertEquals(Types.listType(Types.tupleType(typeInt, typeChar)), e.getType());
	}



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
