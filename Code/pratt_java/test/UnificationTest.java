import static org.junit.Assert.*;

import org.junit.Test;

import typechecker.Substitution;
import typechecker.Type;
import typechecker.TypeBool;
import typechecker.TypeFunction;
import typechecker.TypeInt;
import typechecker.TypeVariable;


public class UnificationTest {
	
	@Test
	public void testUnifyTwoTypeVariables() {
		Type t1 = new TypeVariable("a1");
		Type t2 = new TypeVariable("a2");
		Substitution s = t1.unifyWith(t2);
		assertEquals(t1.applySubstitution(s), t2.applySubstitution(s));
	}

	@Test
	public void testUnificationSucceeds() {
		Type t1 = new TypeFunction(new TypeFunction(new TypeVariable("a"),
				new TypeInt()), new TypeVariable("b"));
		Type t2 = new TypeFunction(new TypeFunction(new TypeBool(),
				new TypeInt()), new TypeInt());
		Substitution s = t1.unifyWith(t2);
		assertTrue("must have found a", s.containsKey("a"));
		assertTrue("must have found b", s.containsKey("b"));
		assertEquals(new TypeBool(), s.get("a"));
		assertEquals(new TypeInt(), s.get("b"));
	}

	@Test
	public void testUnificationFails() {
		// Example from the slides where unification fails
		Type t1 = new TypeFunction(new TypeFunction(new TypeVariable("a"),
				new TypeInt()), new TypeVariable("a"));
		Type t2 = new TypeFunction(new TypeFunction(new TypeBool(),
				new TypeInt()), new TypeInt());
		try {
			t1.unifyWith(t2);
			assertTrue("unify must throw an exception", false);
		} catch (Error e) {
			assertTrue("error message must start with cannot unify types", e.getMessage().startsWith("cannot unify types"));
		}
	}
	
	@Test
	public void testUnificationVariableAndFunction() {
		Type t1 = new TypeVariable("a");
		Type t2 = new TypeFunction(new TypeInt(), new TypeBool());
		Substitution s = t1.unifyWith(t2);
		assertTrue("must have found a", s.containsKey("a"));
		assertEquals(t1.applySubstitution(s), t2.applySubstitution(s));
	}

}
