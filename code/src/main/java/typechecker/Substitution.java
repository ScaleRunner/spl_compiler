package typechecker;

import parser.types.Type;

import java.util.HashMap;

// We extend HashMap to give it a better name
public class Substitution extends HashMap<String, Type> {
	// just shut up, okay?
	private static final long serialVersionUID = 42L;
}
