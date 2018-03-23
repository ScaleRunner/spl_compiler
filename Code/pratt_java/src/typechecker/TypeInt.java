package typechecker;

public class TypeInt extends Type {
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	public Type applySubstitution(Substitution substitution) {
		return this;
	}
	
	@Override
	protected Substitution unifyWith(TypeInt t)
	{
		return new Substitution();
	}
}
