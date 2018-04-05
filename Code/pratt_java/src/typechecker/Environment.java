package typechecker;

import parser.types.Type;

import java.util.HashMap;
import java.util.Map;

public class Environment extends HashMap<String, Type> {
	// just shut up, okay?
	private static final long serialVersionUID = 42L;
	
	// Applies a substitution to all types in the environment
	public void applySubstitution(Substitution s) {
		for (Entry<String, Type> pair : this.entrySet()) {
			this.put(pair.getKey(), pair.getValue().applySubstitution(s));
		}
	}

    /**
     * Java Strings are immutable, Types are not they have to be copied
     * @param env environment to be copied
     * @return deepCopy of env
     */
	public static Environment deepCopy(Environment env){
	    Environment copy = new Environment();

        for (Map.Entry<String, Type> entry : env.entrySet()) {
            try {
                copy.put(entry.getKey(), (Type) entry.getValue().clone());
            } catch (CloneNotSupportedException e) {
                System.err.println("Type object is not cloneable.");
                e.printStackTrace();
            }
        }

	    return copy;
    }
}
