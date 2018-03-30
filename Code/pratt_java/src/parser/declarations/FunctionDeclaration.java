package parser.declarations;

import parser.FunType.FunType;
import parser.FunType.Type;
import parser.expressions.IdentifierExpression;
import parser.statements.Statement;
import util.Visitor;

import java.util.List;
import java.util.Objects;

public class FunctionDeclaration extends Declaration{
    IdentifierExpression funName;
    List<IdentifierExpression> args;
    List<Declaration> decls;
    List<Statement> stats;
    FunType funType;

    //TODO
    //Type?

    public FunctionDeclaration(IdentifierExpression funName,List<IdentifierExpression> args, List<Declaration> decls,
                               List<Statement> stats, FunType funType) {
        this.funName = funName;
        this.args = args;
        this.decls = decls;
        this.stats = stats;
        this.funType = funType;
    }

    @Override
    public void accept(Visitor v) {

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
