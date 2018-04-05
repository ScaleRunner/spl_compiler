package parser.declarations;

import parser.types.FunType;
import parser.expressions.IdentifierExpression;
import parser.statements.Statement;
import util.Visitor;

import java.util.List;
import java.util.Objects;

public class FunctionDeclaration extends Declaration{
    public final IdentifierExpression funName;
    public final List<IdentifierExpression> args;
    public final List<VariableDeclaration> decls;
    public final List<Statement> stats;
    public final FunType funType;

    //TODO
    //Type?

    public FunctionDeclaration(IdentifierExpression funName,List<IdentifierExpression> args, List<VariableDeclaration> decls,
                               List<Statement> stats, FunType funType) {
        this.funName = funName;
        this.args = args;
        this.decls = decls;
        this.stats = stats;
        this.funType = funType;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionDeclaration that = (FunctionDeclaration) o;
        return Objects.equals(funName, that.funName) &&
                Objects.equals(args, that.args) &&
                Objects.equals(decls, that.decls) &&
                Objects.equals(stats, that.stats) &&
                Objects.equals(funType, that.funType);
    }

    @Override
    public int hashCode() {

        return Objects.hash(funName, args, decls, stats, funType);
    }
}
