package typechecker;

public class TypeVariable extends Type {
	private String variable;

	public String getVariable() {
		return variable;
	}

	public TypeVariable(String variable) {
		this.variable = variable;
	}

	@Override
	public Type applySubstitution(Substitution substitution) {
		if (substitution.containsKey(variable)) {
			return substitution.get(variable).applySubstitution(substitution);
		} else {
			return this;
		}
	}
	
	@Override
	protected Substitution unifyWith(TypeVariable t)
	{
		Substitution s = new Substitution();
		s.put(variable, t);
		return s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((variable == null) ? 0 : variable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeVariable other = (TypeVariable) obj;
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable))
			return false;
		return true;
	}

}
