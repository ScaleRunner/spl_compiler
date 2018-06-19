package typechecker;

import parser.declarations.FunctionDeclaration;
import parser.types.Type;

import java.util.HashMap;
import java.util.Map;

public class Environment extends HashMap<String, EnvironmentType> {

    public boolean isGlobalVariable(String varName){
        EnvironmentType t = this.get(varName);
        if(t == null)
            return false;
        return !t.isFunction && t.isGlobal;
    }

    /**
     * Java Strings are immutable, Types are not they have to be copied
     * @param env environment to be copied
     * @return deepCopy of env
     */
	public static Environment deepCopy(Environment env){
	    Environment copy = new Environment();

        for (Map.Entry<String, EnvironmentType> entry : env.entrySet()) {
            copy.put(entry.getKey(), (EnvironmentType) entry.getValue().clone());
        }

	    return copy;
    }

    public void putFunction(String key, EnvironmentType value){
        this.put(key +"_func", value);
    }

    public EnvironmentType getFunction(String key){
        return this.get(key +"_func");
    }



}

