package typechecker;

import parser.types.Type;

import java.util.HashMap;

public class Environment extends HashMap<String, Type> {
	// just shut up, okay?
	private static final long serialVersionUID = 42L;
	
	// Applies a substitution to all types in the environment
	public void applySubstitution(Substitution s) {
		for (Entry<String, Type> pair : this.entrySet()) {
			this.put(pair.getKey(), pair.getValue().applySubstitution(s));
		}
	}
}
