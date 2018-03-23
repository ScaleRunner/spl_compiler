package parser.declarations;

import parser.expressions.Expression;
import parser.expressions.IdentifierExpression;
import lexer.Token;
import parser.statements.Statement;
import util.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lexer.TokenType;

public class FunctionDeclaration implements Declaration{
    IdentifierExpression funName;
    List<IdentifierExpression> args;
    List<Declaration> decls;
    List<Statement> stats;
    List<TokenType> fargsTypes;
    TokenType returnType;

    //TODO
    //FunType?

    public FunctionDeclaration(IdentifierExpression funName,List<IdentifierExpression> args, List<Declaration> decls, List<Statement> stats, List<TokenType> fargsTypes,
                               TokenType returnType) {
        this.funName = funName;
        this.args = args;
        this.decls = decls;
        this.stats = stats;
        this.fargsTypes = fargsTypes;
        this.returnType = returnType;
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
                Objects.equals(fargsTypes, that.fargsTypes) &&
                returnType == that.returnType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(funName, args, decls, stats, fargsTypes, returnType);
    }
}
