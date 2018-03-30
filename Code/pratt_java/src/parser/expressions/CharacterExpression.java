package parser.expressions;

import util.Visitor;

import java.util.Objects;

/**
 * Boolean Expression: True, False
 */
public class CharacterExpression extends Expression {

    public final char name;

    public CharacterExpression(char name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterExpression that = (CharacterExpression) o;
        return name == that.name;
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
