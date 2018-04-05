import static org.junit.Assert.*;

import lexer.Lexer;
import org.junit.Before;
import org.junit.Test;

import parser.statements.Statement;
import parser.types.Type;
import parser.types.Types;
import parser.Parser;
import parser.declarations.Declaration;
import parser.declarations.VariableDeclaration;
import parser.expressions.Expression;
import parser.statements.Statement;
import typechecker.*;
import util.Node;

import java.util.ArrayList;
import java.util.List;

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

    private void assertTypecheckFailure() {
        assertFalse(tc.getAllErrors(), tc.getAllErrors().length() == 0);
    }

    private Node typecheckStmt(String input) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l.tokenize());
        Statement stmt = p.parseStatement();
        tc.typecheck(stmt);
        return stmt;
    }

	private Node typecheckExpr(String input) {
		Lexer l = new Lexer(input);
		Parser p = new Parser(l.tokenize());
		Expression expr = p.parseExpression();
		tc.typecheck(expr);
		return expr;
	}

	private List<Node> typecheckSPL(String input) {
		Lexer l = new Lexer(input);
		Parser p = new Parser(l.tokenize());
		List<Declaration> decls = p.parseSPL();
		List<Node> nodes = new ArrayList<>();

		for(Declaration d : decls) {
			tc.typecheck(d);
			assertTypecheckSuccess();
			nodes.add(d);
		}
		return nodes;
	}

	@Test
	public void testCompareTypes() {
		assertEquals(Types.intType, Types.intType);
		assertEquals(Types.boolType, Types.boolType);
		assertNotEquals(Types.boolType, Types.intType);
		assertEquals(Types.listType(Types.intType), Types.listType(Types.intType));
		assertNotEquals(Types.listType(Types.intType), Types.tupleType(Types.intType, Types.intType));
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
    public void testTuple() {
        Node e = typecheckExpr("(True, 1)");
        assertTypecheckSuccess();
        assertEquals(Types.tupleType(Types.boolType, Types.intType), e.getType());
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

    @Test
    public void testPrefixNegation() {
        Node e = typecheckExpr("!True");
        assertTypecheckSuccess();
        assertEquals(Types.boolType, e.getType());
    }

    @Test
    public void testPrefixNegationOverComparison() {
        Node e = typecheckExpr("!(1 > 2)");
        assertTypecheckSuccess();
        assertEquals(Types.boolType, e.getType());
    }

    @Test
    public void testPrefixNegationError() {
        typecheckExpr("!1");
        assertTypecheckFailure();
    }

    @Test
    public void testPrefixMinus() {
        Node e = typecheckExpr("-10");
        assertTypecheckSuccess();
        assertEquals(Types.intType, e.getType());
    }

    @Test
    public void testPrefixMinusGrouped() {
        Node e = typecheckExpr("-(4 * 3) % 5");
        assertTypecheckSuccess();
        assertEquals(Types.intType, e.getType());
    }

	@Test
	public void testValidVarDecl() {
		List<Node> nodes = typecheckSPL("Int a = 3;\n" +
				"Bool a = True;\n" +
						"Char c = 'a';");
		for(Node n : nodes){
			assertEquals(Types.voidType, n.getType());
		}
	}

    @Test
    public void testEmptyReturn() {
        Node e = typecheckStmt("return;");
        assertTypecheckSuccess();
        assertEquals(Types.voidType, e.getType());
    }

    @Test
    public void testNonEmptyReturn() {
        Node e = typecheckStmt("return 1+3;");
        assertTypecheckSuccess();
        assertEquals(Types.intType, e.getType());
    }

    @Test
    public void testPrintInt() {
        Node e = typecheckStmt("print(1);");
        assertTypecheckSuccess();
        assertEquals(Types.intType, e.getType());
    }

    @Test
    public void testPrintList() {
        typecheckStmt("print(1:[]);");
        assertTypecheckFailure();
    }

    @Test
    public void testSimpleConditional() {
        Node e = typecheckStmt("if(True){}");
        assertTypecheckSuccess();
        assertEquals(Types.voidType, e.getType());
    }

    @Test
    public void testSimpleConditionalReturn() {
        Node e = typecheckStmt("if(True){return True;} else {return False;}");
        assertTypecheckSuccess();
        assertEquals(Types.boolType, e.getType());
    }

    @Test
    public void testConditionalReturn() {
        Node e = typecheckStmt("if(1 > 3 && True){return True == False;} else {return 1==2;}");
        assertTypecheckSuccess();
        assertEquals(Types.boolType, e.getType());
    }

    @Test
    public void testConditionalReturnMismatch() {
        typecheckStmt("if(1 > 3 && True){return 1;} else {return 1==2;}");
        assertTypecheckFailure();
    }

    @Test
    public void testWhile() {
        Node e = typecheckStmt("if(1 > 3){}");
        assertTypecheckSuccess();
        assertEquals(Types.voidType, e.getType());
    }

    @Test
    public void testWhileReturn() {
        Node e = typecheckStmt("if(1 > 3 && True){return 1:[];}");
        assertTypecheckSuccess();
        assertEquals(Types.listType(Types.intType), e.getType());
    }

    @Test
    public void testWhileInvalidCondition() {
        typecheckStmt("if(1){return 1:[];}");
        assertTypecheckFailure();
    }

	@Test
	public void testFuncDecl() {
		List<Node> nodes = typecheckSPL("facR( n ) :: Int -> Int {\n" +
				"if (n < 2 ) {\n " +
				"return 1;\n " +
				"} else {\n" +
				"return n * facR ( n - 1 );\n" +
				"}\n" +
				"}");
		for(Node n: nodes)
			assertEquals(Types.intType, n.getType());

	}

	@Test
	public void testTwoFuncDecl() {
		List<Node> nodes = typecheckSPL("facR( n ) :: Int -> Int {\n" +
				"if (n < 2 ) {\n " +
				"return 1;\n " +
				"} else {\n" +
				"return n * facR ( n - 1 );\n" +
				"}\n" +
				"}\n" +
				"id(a) :: Int -> Int {\n" +
				"a = 3;"+
				"return a+n;\n" +
				"}");
		for(Node n: nodes)
			assertEquals(Types.intType, n.getType());

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
