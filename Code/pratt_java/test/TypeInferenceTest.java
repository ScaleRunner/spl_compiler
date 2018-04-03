//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.assertNotEquals;
//
//import org.junit.Test;
//
//import parser.Parser;
//import typechecker.Substitution;
//import typechecker.Type;
//import typechecker.TypeBool;
//import typechecker.TypeFunction;
//import typechecker.TypeInference;
//import typechecker.TypeInt;
//import typechecker.TypeVariable;
//
//public class TypeInferenceTest {
//
////	private void assertTypecheckSuccess(TypeInference tc) {
////		assertTrue(tc.getAllErrors(), tc.getAllErrors().length() == 0);
////	}
//
////	private void assertTypecheckFailure(TypeInference tc, String message) {
////		assertEquals(message, tc.getAllErrors());
////	}
//
////	private AstExpr parseExpr(String input) {
////		Parser p = new Parser(input);
////		AstExpr expr = p.pExpr();
////		return expr;
////	}
//
//	@Test
//	public void testUnificationFails() {
//		// Example from the slides where unification fails
//		Type t1 = new TypeFunction(new TypeFunction(new TypeVariable("a"),
//				new TypeInt()), new TypeVariable("a"));
//		Type t2 = new TypeFunction(new TypeFunction(new TypeBool(),
//				new TypeInt()), new TypeInt());
//		try {
//			TypeInference.unify(t1, t2);
//			assertTrue("unify must throw an exception", false);
//		} catch (Error e) {
//			assertEquals("cannot unify types", e.getMessage());
//		}
//	}
//
//	@Test
//	public void testUnificationSucceeds() {
//		// Same as before, but now two different listType variables make unification
//		// succeed
//		Type t1 = new TypeFunction(new TypeFunction(new TypeVariable("a"),
//				new TypeInt()), new TypeVariable("b"));
//		Type t2 = new TypeFunction(new TypeFunction(new TypeBool(),
//				new TypeInt()), new TypeInt());
//		Substitution s = TypeInference.unify(t1, t2);
//		assertTrue("must have found a", s.containsKey("a"));
//		assertTrue("must have found b", s.containsKey("b"));
//		assertEquals(new TypeBool(), s.get("a"));
//		assertEquals(new TypeInt(), s.get("b"));
//	}
//
//	@Test
//	public void testUnifyTwoTypeVariables() {
//		Type t1 = new TypeVariable("a1");
//		Type t2 = new TypeVariable("a2");
//		Substitution s = TypeInference.unify(t1, t2);
//		assertEquals(t1.applySubstitution(s), t2.applySubstitution(s));
//	}
//
//	@Test
//	public void testSubstitutionInt() {
//		Type t = new TypeInt();
//		Substitution subst = new Substitution();
//		subst.put("a", new TypeInt());
//		assertEquals(new TypeInt(), t.applySubstitution(subst));
//	}
//
//	@Test
//	public void testSubstitutionVar() {
//		Type t = new TypeVariable("a");
//		Substitution subst = new Substitution();
//		subst.put("a", new TypeBool());
//		assertEquals(new TypeBool(), t.applySubstitution(subst));
//	}
//
//	@Test
//	public void testSubstitutionFunction() {
//		Type t = new TypeFunction(new TypeVariable("a"), new TypeVariable("b"));
//		Substitution subst = new Substitution();
//		subst.put("a", new TypeBool());
//		subst.put("b", new TypeInt());
//		assertEquals(new TypeFunction(new TypeBool(), new TypeInt()),
//				t.applySubstitution(subst));
//	}
//
//	@Test
//	public void testSubstitutionRepeated() {
//		Type t = new TypeVariable("a");
//		Substitution subst = new Substitution();
//		subst.put("a", new TypeVariable("b"));
//		subst.put("b", new TypeInt());
//		assertEquals(new TypeInt(), t.applySubstitution(subst));
//	}
//
//	@Test
//	public void testSubstitutionRepeatedInFunction() {
//		Type t = new TypeFunction(new TypeVariable("a"), new TypeVariable("b"));
//		Substitution subst = new Substitution();
//		subst.put("a", new TypeVariable("b"));
//		subst.put("b", new TypeInt());
//		assertEquals(new TypeFunction(new TypeInt(), new TypeInt()),
//				t.applySubstitution(subst));
//	}
//
////	@Test
////	public void testIntegerConstant() {
////		AstExpr e = parseExpr("5");
////		TypeInference tc = new TypeInference(e);
////		assertTypecheckSuccess(tc);
////		assertEquals(new TypeInt(), e.getType());
////	}
////
////	@Test
////	public void testPlusWithConstants() {
////		AstExpr e = parseExpr("5 + 3");
////		TypeInference tc = new TypeInference(e);
////		assertTypecheckSuccess(tc);
////		assertEquals(new TypeInt(), e.getType());
////	}
////
////	@Test
////	public void testPlusWithTypeError() {
////		AstExpr e = parseExpr("5 + True");
////		TypeInference tc = new TypeInference(e);
////		assertTypecheckFailure(tc, "cannot unify types\n");
////	}
////
////	@Test
////	public void testIdentityFunction() {
////		AstExpr e = parseExpr("fun x . x");
////		TypeInference tc = new TypeInference(e);
////		assertTypecheckSuccess(tc);
////		assertTrue("result must be a function",
////				e.getType() instanceof TypeFunction);
////		TypeFunction t = (TypeFunction) e.getType();
////		assertEquals(t.getArgType(), t.getResultType());
////	}
////
////	@Test
////	public void testConstantFunction() {
////		AstExpr e = parseExpr("fun x . fun y . x");
////		TypeInference tc = new TypeInference(e);
////		assertTypecheckSuccess(tc);
////		// all this bullshit just to check that the listType is a -> b -> a
////		assertTrue("result must be a function",
////				e.getType() instanceof TypeFunction);
////		TypeFunction t = (TypeFunction) e.getType();
////		assertTrue("result listType must be a function",
////				t.getResultType() instanceof TypeFunction);
////		TypeFunction t2 = (TypeFunction) t.getResultType();
////		assertTrue("input must be listType variable",
////				t.getArgType() instanceof TypeVariable);
////		assertTrue("second input must be listType variable",
////				t2.getArgType() instanceof TypeVariable);
////		assertTrue("result must be listType variable",
////				t2.getResultType() instanceof TypeVariable);
////		assertEquals(t.getArgType(), t2.getResultType());
////		assertNotEquals(t.getArgType(), t2.getArgType());
////	}
////
////	@Test
////	public void testAdditionFunction() {
////		AstExpr e = parseExpr("fun x . fun y . x + y");
////		TypeInference tc = new TypeInference(e);
////		assertTypecheckSuccess(tc);
////		assertEquals(new TypeFunction(new TypeInt(), new TypeFunction(
////				new TypeInt(), new TypeInt())), e.getType());
////	}
//
//}
