import parser.FunType.*;
import parser.declarations.Declaration;
import parser.declarations.FunctionDeclaration;
import parser.declarations.VariableDeclaration;
import parser.expressions.*;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import org.junit.Test;
import parser.Parser;
import parser.exceptions.CallException;
import parser.exceptions.ParseException;
import parser.exceptions.SemicolonError;
import parser.statements.*;
import util.Node;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ParserTest {

    @Test(expected = SemicolonError.class)
    public void no_semicolon() {
        Lexer l = new Lexer("foo = bar");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        p.parseBlock();
    }

    @Test(expected = SemicolonError.class)
    public void no_semicolon_second_line() {
        Lexer l = new Lexer("foo = bar; this=wrong");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        p.parseBlock();
    }

    @Test
    public void node_check(){
        Lexer l = new Lexer("foo = bar;");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Node result = p.parseStatement();

        Node expected = new AssignStatement(
                new IdentifierExpression("foo"), new IdentifierExpression("bar")
        );
        assertEquals(result, expected);
    }

    @Test
    public void testChar(){
        Lexer l = new Lexer("'a'");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Node result = p.parseExpression();

        Node expected = new CharacterExpression('a');
        assertEquals(result, expected);
    }

    @Test
    public void slides_example() {
        Lexer l = new Lexer("-5 + b");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression left = new PrefixExpression(
                TokenType.TOK_MINUS,
                new IntegerExpression(5)
        );
        Expression right = new IdentifierExpression("b");
        Expression expected = new OperatorExpression(left, TokenType.TOK_PLUS, right);
        assertEquals(result, expected);
    }

    @Test
    public void single_addition() {
        Lexer l = new Lexer("a + b");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression left = new IdentifierExpression("a");
        Expression right = new IdentifierExpression("b");
        Expression expected = new OperatorExpression(left, TokenType.TOK_PLUS, right);
        assertEquals(result, expected);
    }

    @Test
    public void single_assignment() {
        Lexer l = new Lexer("a = b;");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Statement> result = p.parseBlock();
        ArrayList<Statement> expected = new ArrayList<>();
        Expression left = new IdentifierExpression("a");
        Expression right = new IdentifierExpression("b");
        expected.add(new AssignStatement(left, right));
        assertEquals(result, expected);
    }

    @Test
    public void single_field() {
        Lexer l = new Lexer("a.hd");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression left = new IdentifierExpression("a");

        Expression expected = new PostfixExpression(left, TokenType.TOK_HD);
        assertEquals(result, expected);
    }

    @Test
    public void tuple() {
        Lexer l = new Lexer("(a, 1+True)");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression expected = new TupleExpression(
                new IdentifierExpression("a"),
                new OperatorExpression(
                        new IntegerExpression(1),
                        TokenType.TOK_PLUS,
                        new BooleanExpression(true)
                )
        );
        assertEquals(result, expected);
    }

    @Test
    public void nested_tuple() {
        Lexer l = new Lexer("((a, b), 1+True)");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression expected = new TupleExpression(
                new TupleExpression(
                        new IdentifierExpression("a"),
                        new IdentifierExpression("b")
                ),
                new OperatorExpression(
                        new IntegerExpression(1),
                        TokenType.TOK_PLUS,
                        new BooleanExpression(true)
                )
        );
        assertEquals(result, expected);
    }

    @Test(expected = ParseException.class)
    public void nested_tuple_unbalanced() {
        Lexer l = new Lexer("a=((a, b), 1+True));");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        p.parseStatement();
    }

    @Test
    public void list() {
        Lexer l = new Lexer("[]");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression expected = new ListExpression();
        assertEquals(result, expected);
    }

    @Test
    public void consOperator() {
        Lexer l = new Lexer("1:2:[]");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Node result = p.parseExpression();

        Node expected = new OperatorExpression(
                new IntegerExpression(1),
                TokenType.TOK_CONS,
                new OperatorExpression(
                        new IntegerExpression(2),
                        TokenType.TOK_CONS,
                        new ListExpression()
                )
        );
        assertEquals(result, expected);
    }

    @Test
    public void field_assignment() {
        Lexer l = new Lexer("a.hd.fst = 1;");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Statement> result = p.parseBlock();

        ArrayList<Statement> expected = new ArrayList<>();
        expected.add(
                new AssignStatement(
                        new PostfixExpression(
                                new PostfixExpression(
                                        new IdentifierExpression("a"),
                                        TokenType.TOK_HD
                                ),
                                TokenType.TOK_FST
                        ),
                        new IntegerExpression(1)
                )
        );
        assertEquals(result, expected);
    }

    @Test
    public void nested_addition() {
        // Expected: (a + b) + c
        Lexer l = new Lexer("a + b + c");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        // First make the OperatorExpression for the left
        Expression left_left = new IdentifierExpression("a");
        Expression left_right = new IdentifierExpression("b");
        Expression left = new OperatorExpression(left_left, TokenType.TOK_PLUS, left_right);

        // Now for the right
        Expression right = new IdentifierExpression("c");
        Expression expected = new OperatorExpression(left, TokenType.TOK_PLUS, right);
        assertEquals(result, expected);
    }

	@Test
	public void testAssignmentMixedWithStatement() {
	    Lexer l = new Lexer("a = b; c = a;if(a == c) { a = 2;}");
        List<Token> tokens = l.tokenize();
		Parser p = new Parser(tokens);
		ArrayList<Statement> result = p.parseBlock();

        Expression left = new IdentifierExpression("a");
        Expression right = new IdentifierExpression("b");
        Statement assign = new AssignStatement(left, right);

        ArrayList<Statement> actual = new ArrayList<>();

        actual.add(assign);
        actual.add(new AssignStatement(new IdentifierExpression("c"), new IdentifierExpression("a")));

        ArrayList<Statement> if_branch = new ArrayList<>();
        if_branch.add(new AssignStatement(new IdentifierExpression("a"), new IntegerExpression(2)));

        actual.add(new ConditionalStatement(
                new OperatorExpression(
                        new IdentifierExpression("a"),
                        TokenType.TOK_EQ,
                        new IdentifierExpression("c")),
                if_branch,
                new ArrayList<>()));
        assertEquals(result,actual);
	}

    @Test
    public void testEmptyStatement() {
        Lexer l = new Lexer("if(a == c) {}");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Statement> result = p.parseBlock();

        Expression left = new IdentifierExpression("a");
        Expression right = new IdentifierExpression("c");
        Expression condition = new OperatorExpression(left, TokenType.TOK_EQ, right);

        ArrayList<Statement> actual = new ArrayList<>();
        actual.add(new ConditionalStatement(condition, new ArrayList<>(), new ArrayList<>()));

        assertEquals(result, actual);
    }

    @Test
    public void testWhileStatement() {
        Lexer l = new Lexer("while(a == c) {a = stupid;}");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Statement> result = p.parseBlock();

        Expression condition = new OperatorExpression(
                new IdentifierExpression("a"),
                TokenType.TOK_EQ,
                new IdentifierExpression("c"));

        ArrayList<Statement> body = new ArrayList<>();
        body.add(new AssignStatement(
                        new IdentifierExpression("a"),
                        new IdentifierExpression("stupid")
                )
        );

        ArrayList<Statement> actual = new ArrayList<>();
        actual.add(new LoopStatement(condition, body));

        assertEquals(result, actual);
    }

    @Test
    public void testWhileEmptyStatement() {
        Lexer l = new Lexer("while(a == c) {}");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Statement> result = p.parseBlock();

        Expression condition = new OperatorExpression(
                new IdentifierExpression("a"),
                TokenType.TOK_EQ,
                new IdentifierExpression("c"));

        ArrayList<Statement> body = new ArrayList<>();

        ArrayList<Statement> actual = new ArrayList<>();
        actual.add(new LoopStatement(condition, body));

        assertEquals(result, actual);
    }

    @Test
    public void testReturnStatementEmpty() {
        Lexer l = new Lexer("return;");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Statement> result = p.parseBlock();

        ArrayList<Statement> actual = new ArrayList<>();

        actual.add(new ReturnStatement(null));

        assertEquals(result, actual);
    }

    @Test
    public void testReturnStatement() {
        Lexer l = new Lexer("return a;");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Statement> result = p.parseBlock();

        ArrayList<Statement> actual = new ArrayList<>();

        Expression arg = new IdentifierExpression("a");

        actual.add(new ReturnStatement(arg));

        assertEquals(result, actual);
    }

    @Test
    public void testPrintStatementEmpty() {
        Lexer l = new Lexer("print();");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Statement> result = p.parseBlock();

        ArrayList<Statement> actual = new ArrayList<>();

        actual.add(new PrintStatement(null));

        assertEquals(result, actual);
    }

    @Test
    public void testPrintStatement() {
        Lexer l = new Lexer("print(a + 1);");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Statement> result = p.parseBlock();

        ArrayList<Statement> actual = new ArrayList<>();

        Expression arg = new OperatorExpression(
                new IdentifierExpression("a"),
                TokenType.TOK_PLUS,
                new IntegerExpression(1)
        );

        actual.add(new PrintStatement(arg));

        assertEquals(result, actual);
    }

    @Test(expected = ParseException.class)
    public void testPrintStatementFaulty() {
        //TODO: Do we want a prettier error?
        Lexer l = new Lexer("print(a + 1, 1);");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        p.parseBlock();
    }

	@Test
	public void testNegativeIdentifier() {
        Lexer l = new Lexer("-a");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression right = new IdentifierExpression("a");
        Expression expected = new PrefixExpression(TokenType.TOK_MINUS, right);
        assertEquals(result, expected);
	}

    @Test
    public void testNestedNegativeIdentifier() {
        //Expected: -(-(-a))
        Lexer l = new Lexer("---a");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression inner = new IdentifierExpression("a");
        Expression expected = new PrefixExpression(
                TokenType.TOK_MINUS, new PrefixExpression(
                        TokenType.TOK_MINUS, new PrefixExpression(
                                TokenType.TOK_MINUS, inner
                        )
                )
        );
        assertEquals(result, expected);
    }

	@Test
	public void testMultiplication() {
        Lexer l = new Lexer("a * b");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression left = new IdentifierExpression("a");
        Expression right = new IdentifierExpression("b");
        Expression expected = new OperatorExpression(left, TokenType.TOK_MULT, right);
        assertEquals(result, expected);
	}

	@Test
	public void testMultiplicationAssociativity() {
        // Expected: (a + (((b * c) * d)) - e
        Lexer l = new Lexer("a + b * c * d - e");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression expected = new OperatorExpression(
                new OperatorExpression(
                        new IdentifierExpression("a"),
                        TokenType.TOK_PLUS,
                        new OperatorExpression(
                                new OperatorExpression(
                                        new IdentifierExpression("b"),
                                        TokenType.TOK_MULT,
                                        new IdentifierExpression("c")
                                ),
                                TokenType.TOK_MULT,
                                new IdentifierExpression("d")
                        )
                ),
                TokenType.TOK_MINUS,
                new IdentifierExpression("e")
        );
        assertEquals(result, expected);
	}

    @Test
	public void testAssignmentBoolean() {
        Lexer l = new Lexer("value = True;");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Statement> result = p.parseBlock();
        ArrayList<Statement> actual = new ArrayList<>();
        Expression left = new IdentifierExpression("value");
        Expression right = new BooleanExpression(true);
        actual.add(new AssignStatement(left, right));
        assertEquals(result, actual);
	}

    @Test
    public void testMultipleField() {
        Lexer l = new Lexer("value.tl.hd");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression left = new IdentifierExpression("value");
        left = new PostfixExpression(left, TokenType.TOK_TL);
        Expression actual = new PostfixExpression(left, TokenType.TOK_HD);


        assertEquals(result, actual);
    }

    @Test
    public void testMultipleFieldAndAssignment() {
        Lexer l = new Lexer("value.tl.hd = a;");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Statement> result = p.parseBlock();
        ArrayList<Statement> actual = new ArrayList<>();

        Expression left = new IdentifierExpression("value");
        left = new PostfixExpression(left, TokenType.TOK_TL);
        left = new PostfixExpression(left, TokenType.TOK_HD);
        actual.add(new AssignStatement(left, new IdentifierExpression("a")));


        assertEquals(result, actual);
    }

    @Test
    public void testInteger() {
        Lexer l = new Lexer("value = 1;");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Statement> result = p.parseBlock();
        ArrayList<Statement> actual = new ArrayList<>();
        Expression left = new IdentifierExpression("value") ;
        Expression right = new IntegerExpression(1);
        actual.add(new AssignStatement(left, right));
        assertEquals(result, actual);
    }

    @Test
    public void testModulo() {
        Lexer l = new Lexer("2 % 4");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression left = new IntegerExpression(2);
        Expression right = new IntegerExpression(4);
        Expression actual = new OperatorExpression(left, TokenType.TOK_MOD, right);
        assertEquals(result, actual);
    }

    @Test
    public void MomentOfTruth() {
        Lexer l = new Lexer("2 * True - - 3 +3");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression actual = new OperatorExpression(
                new OperatorExpression(
                        new OperatorExpression(
                                new IntegerExpression(2),
                                TokenType.TOK_MULT,
                                new BooleanExpression(true)
                        ),
                        TokenType.TOK_MINUS,
                        new PrefixExpression(TokenType.TOK_MINUS, new IntegerExpression(3))
                ),
                TokenType.TOK_PLUS,
                new IntegerExpression(3)
        );
        assertEquals(result, actual);
    }

	@Test
    public void testFunCall() {
        Lexer l = new Lexer("a()");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression actual = new CallExpression(
                new IdentifierExpression("a"),
                new ArrayList<>()
        );

        assertEquals(result, actual);
    }

    @Test
    public void testFunCallStatement() {
        Lexer l = new Lexer("a();");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Statement> result = p.parseBlock();

        ArrayList<Statement> expected = new ArrayList<>();
        expected.add(
                new CallStatement(
                        new CallExpression(
                                new IdentifierExpression("a"),
                                new ArrayList<>()
                        )
                )
        );

        assertEquals(result, expected);
    }

    @Test(expected = CallException.class)
    public void testFunCallError() {
        Lexer l = new Lexer("1()");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        p.parseExpression();
    }

    @Test
    public void testFunCallArguments() {
        Lexer l = new Lexer("a(foo, bar, 2, True)");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        ArrayList<Expression> args = new ArrayList<>();
        args.add(new IdentifierExpression("foo"));
        args.add(new IdentifierExpression("bar"));
        args.add(new IntegerExpression(2));
        args.add(new BooleanExpression(true));

        Expression actual = new CallExpression(
                new IdentifierExpression("a"),
                args
        );

        assertEquals(result, actual);
    }

    @Test
    public void testGrouping() {
        Lexer l = new Lexer("1 + 5 * !(4 + 3)");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression actual = new OperatorExpression(
                new IntegerExpression(1),
                TokenType.TOK_PLUS,
                new OperatorExpression(
                        new IntegerExpression(5),
                        TokenType.TOK_MULT,
                        new PrefixExpression(
                                TokenType.TOK_NOT,
                                new OperatorExpression(
                                        new IntegerExpression(4),
                                        TokenType.TOK_PLUS,
                                        new IntegerExpression(3)
                                )
                        )
                )
        );

        assertEquals(result, actual);
    }

    @Test
    public void testEquals() {
        Lexer l = new Lexer("a == b + 1");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression actual = new OperatorExpression(
                new IdentifierExpression("a"),
                TokenType.TOK_EQ,
                new OperatorExpression(
                        new IdentifierExpression("b"),
                        TokenType.TOK_PLUS,
                        new IntegerExpression(1)
                )
        );

        assertEquals(result, actual);
    }

    @Test
    public void testNotEquals() {
        Lexer l = new Lexer("a != b + 1");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression actual = new OperatorExpression(
                new IdentifierExpression("a"),
                TokenType.TOK_NEQ,
                new OperatorExpression(
                        new IdentifierExpression("b"),
                        TokenType.TOK_PLUS,
                        new IntegerExpression(1)
                )
        );

        assertEquals(result, actual);
    }

    @Test
    public void testGreaterEquals() {
        Lexer l = new Lexer("a >= b + 1");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression actual = new OperatorExpression(
                new IdentifierExpression("a"),
                TokenType.TOK_GEQ,
                new OperatorExpression(
                        new IdentifierExpression("b"),
                        TokenType.TOK_PLUS,
                        new IntegerExpression(1)
                )
        );

        assertEquals(result, actual);
    }

    @Test
    public void testLessEquals() {
        Lexer l = new Lexer("a <= b + 1");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression actual = new OperatorExpression(
                new IdentifierExpression("a"),
                TokenType.TOK_LEQ,
                new OperatorExpression(
                        new IdentifierExpression("b"),
                        TokenType.TOK_PLUS,
                        new IntegerExpression(1)
                )
        );

        assertEquals(result, actual);
    }

    @Test
    public void testGreaterThan() {
        Lexer l = new Lexer("a > b + 1");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression actual = new OperatorExpression(
                new IdentifierExpression("a"),
                TokenType.TOK_GT,
                new OperatorExpression(
                        new IdentifierExpression("b"),
                        TokenType.TOK_PLUS,
                        new IntegerExpression(1)
                )
        );

        assertEquals(result, actual);
    }

    @Test
    public void testLessThan() {
        Lexer l = new Lexer("a < b + 1");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression actual = new OperatorExpression(
                new IdentifierExpression("a"),
                TokenType.TOK_LT,
                new OperatorExpression(
                        new IdentifierExpression("b"),
                        TokenType.TOK_PLUS,
                        new IntegerExpression(1)
                )
        );

        assertEquals(result, actual);
    }

    @Test
    public void testAND() {
        Lexer l = new Lexer("a && b");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression actual = new OperatorExpression(
                new IdentifierExpression("a"),
                TokenType.TOK_AND,
                new IdentifierExpression("b")
        );

        assertEquals(result, actual);
    }

    @Test
    public void testOR() {
        Lexer l = new Lexer("a || b");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression actual = new OperatorExpression(
                new IdentifierExpression("a"),
                TokenType.TOK_OR,
                new IdentifierExpression("b")
        );

        assertEquals(result, actual);
    }

    @Test
    public void testComparisonPrecedence() {
        // Expected: ((True == (a > ((4 * b) + 5))) && (this == fun))
        Lexer l = new Lexer("True == a > 4 * b + 5 && this == fun");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        Expression actual = new OperatorExpression(
                new OperatorExpression(
                        new BooleanExpression(true),
                        TokenType.TOK_EQ,
                        new OperatorExpression(
                                new IdentifierExpression("a"),
                                TokenType.TOK_GT,
                                new OperatorExpression(
                                        new OperatorExpression(
                                                new IntegerExpression(4),
                                                TokenType.TOK_MULT,
                                                new IdentifierExpression("b")
                                        ),
                                        TokenType.TOK_PLUS,
                                        new IntegerExpression(5)
                                )
                        )
                ),
                TokenType.TOK_AND,
                new OperatorExpression(
                        new IdentifierExpression("this"),
                        TokenType.TOK_EQ,
                        new IdentifierExpression("fun")
                )
        );

        assertEquals(result, actual);
    }


    @Test(expected = ParseException.class)
    public void testSPLEmpty() {
        Lexer l = new Lexer("");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        List<Declaration> result = p.parseSPL();

    }

    @Test
    public void testSPLSingleVarDeclaration() {
        Lexer l = new Lexer("var alan = 5+3*2;");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        List<Declaration> result = p.parseSPL();
        List<Declaration> actual = new ArrayList<>();
        actual.add(new VariableDeclaration(TokenType.TOK_KW_VAR,
                            new IdentifierExpression("alan"),
                            new OperatorExpression(
                                    new IntegerExpression(5),
                                    TokenType.TOK_PLUS,
                                    new OperatorExpression(
                                            new IntegerExpression(3),
                                            TokenType.TOK_MULT,
                                            new IntegerExpression(2)
                                    )
                            )
                        )
                    );
        assertEquals(result, actual);

    }

    @Test
    public void testSPLSingleFunctionDeclaration() {
        Lexer l = new Lexer("sumOfTwoNumbers(a,b)::->Void{\n" +
                                    "a = b;\n" +
                                "}");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        List<Declaration> result = p.parseSPL();
        List<Declaration> actual = new ArrayList<>();
        List<IdentifierExpression> args = new ArrayList<>();
        List<Declaration> decls = new ArrayList<>();
        List<Statement> stats = new ArrayList<>();

        IdentifierExpression name = new IdentifierExpression("sumOfTwoNumbers");

        args.add(new IdentifierExpression("a"));
        args.add(new IdentifierExpression("b"));

        stats.add(new AssignStatement(new IdentifierExpression("a"), new IdentifierExpression("b")));

        List<Type> fargsType = new ArrayList<>();

        Type returnType = Types.voidType;

        FunType funtype = new FunType(fargsType, returnType);


        actual.add(new FunctionDeclaration(name,args, decls, stats, funtype));

        assertEquals(result, actual);

    }

    @Test
    public void testSPLSingleFunctionDeclarationNoArgsWithReturnType() {
        Lexer l = new Lexer("simpleAssignment():: -> Void{\na = b;\n}");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        List<Declaration> result = p.parseSPL();

        List<Declaration> actual = new ArrayList<>();

        IdentifierExpression name = new IdentifierExpression("simpleAssignment");

        List<IdentifierExpression> args = new ArrayList<>();
        List<Declaration> decls = new ArrayList<>();
        List<Statement> stats = new ArrayList<>();

        List<Type> fargsType = new ArrayList<>();

        Type returnType = Types.voidType;

        FunType funType = new FunType(fargsType, returnType);


        stats.add(new AssignStatement(new IdentifierExpression("a"), new IdentifierExpression("b")));

        actual.add(new FunctionDeclaration(name, args, decls, stats, funType));

        assertEquals(result, actual);

    }


    @Test
    public void testSPLSingleFunctionDeclarationWithReturnType() {
        Lexer l = new Lexer("sumOfTwoNumbers(a,b):: Int Int -> Void{\na = b;\n}");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        List<Declaration> result = p.parseSPL();

        List<Declaration> actual = new ArrayList<>();

        IdentifierExpression name = new IdentifierExpression("sumOfTwoNumbers");

        List<IdentifierExpression> args = new ArrayList<>();
        List<Declaration> decls = new ArrayList<>();
        List<Statement> stats = new ArrayList<>();

        List<Type> fargsType = new ArrayList<>();
        fargsType.add(Types.intType);
        fargsType.add(Types.intType);

        Type returnType = Types.voidType;

        FunType funType = new FunType(fargsType, returnType);

        args.add(new IdentifierExpression("a"));
        args.add(new IdentifierExpression("b"));

        stats.add(new AssignStatement(new IdentifierExpression("a"), new IdentifierExpression("b")));

        actual.add(new FunctionDeclaration(name, args, decls, stats, funType));

        assertEquals(result, actual);

    }




    @Test
    public void testSPLSingleFunctionDeclarationWithReturnTypeManyDeclarations() {
        Lexer l = new Lexer("sumOfTwoNumbers(a,b):: Int Int -> Void{\n" +
                                    "Int c = 0;\n " +
                                    "var useless = 2;\n " +
                                    "c = a + b;\n" +
                                    "return c;\n" +
                                "}");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        List<Declaration> result = p.parseSPL();

        List<Declaration> actual = new ArrayList<>();

        IdentifierExpression name = new IdentifierExpression("sumOfTwoNumbers");

        List<IdentifierExpression> args = new ArrayList<>();
        List<Declaration> decls = new ArrayList<>();
        List<Statement> stats = new ArrayList<>();

        List<Type> fargsType = new ArrayList<>();
        fargsType.add(Types.intType);
        fargsType.add(Types.intType);

        Type returnType = Types.voidType;
        FunType funType = new FunType(fargsType, returnType);

        args.add(new IdentifierExpression("a"));
        args.add(new IdentifierExpression("b"));

        decls.add(new VariableDeclaration(TokenType.TOK_KW_INT, new IdentifierExpression("c"), new IntegerExpression(0) ));
        decls.add(new VariableDeclaration(TokenType.TOK_KW_VAR,new IdentifierExpression("useless"), new IntegerExpression(2) ));


        stats.add(new AssignStatement(
                        new IdentifierExpression("c"),
                        new OperatorExpression(
                                new IdentifierExpression("a"),
                                TokenType.TOK_PLUS,
                                new IdentifierExpression("b")
                        )
                    )
        );


        stats.add(new ReturnStatement(new IdentifierExpression("c")));

        actual.add(new FunctionDeclaration(name, args, decls, stats, funType));

        assertEquals(result, actual);

    }
//    @Test
//    public void testStatementIf() {
//        Lexer l = new Lexer("if (a>0 && a * 2 < 4){ b = 5 *6 if (a == 2){ c = 3 } } else { a = 3 } ");
//        List<Token> tokens = l.tokenize();
//        Parser p = new Parser(tokens);
//        List<Expression> result = p.parseBlock();
//        List<Expression> aux = new ArrayList<>();
//        aux.add(new AssignStatement("c", new IntegerExpression(3)));
//        List<Expression> then = new ArrayList<>();
//        List<Expression> elsee = new ArrayList<>();
//        elsee.add(new AssignStatement("a", new IntegerExpression(3)));
//
//        then.add(new AssignStatement(
//                "b",
//                new OperatorExpression(
//                        new IntegerExpression(5),
//                        TokenType.TOK_MULT,
//                        new IntegerExpression(6)
//                )));
//        then.add(new ConditionalStatement(
//                new OperatorExpression(
//                        new IdentifierExpression("a"),
//                        TokenType.TOK_EQ,
//                        new IntegerExpression(2)),
//                aux,
//                null
//              )
//        );
//
//        List<Expression> actual = new ArrayList<>();
//        actual.add(new ConditionalStatement(
//                        new OperatorExpression(
//                                new OperatorExpression(
//                                        new IdentifierExpression("a"),
//                                        TokenType.TOK_GT,
//                                        new IntegerExpression(0)
//                                ),
//                                TokenType.TOK_AND,
//                                new OperatorExpression(
//                                        new OperatorExpression(
//                                                new IdentifierExpression("a"),
//                                                TokenType.TOK_MULT,
//                                                new IntegerExpression(2)),
//                                        TokenType.TOK_LT,
//                                        new IntegerExpression(4)
//                                )
//                        ),
//                        then,
//                        elsee
//                    )
//        );
    @Test
    public void testParanthesesBomb() {
        // Expected: ((True == (a > ((4 * b) + 5))) && (this == fun))
//        int n_parantheses = 5000000; // 10mb file on disk
        int n_parantheses = 500;
        StringBuilder sbInput = new StringBuilder();
        for(int i = 0; i < n_parantheses; i++){
            sbInput.append('(');
        }
        sbInput.append("cosy");
        for(int i = 0; i < n_parantheses; i++){
            sbInput.append(')');
        }

        Lexer l = new Lexer(sbInput.toString());
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        p.parseExpression();

        assertEquals(true, true);
    }

//    @Test
//    public void testStatementIf() {
//        Lexer l = new Lexer("if (a>0 && a * 2 < 4){ b = 5 *6 if (a == 2){ c = 3 } } else { a = 3 } ");
//        List<Token> tokens = l.tokenize();
//        Parser p = new Parser(tokens);
//        List<Expression> result = p.parseBlock();
//        List<Expression> aux = new ArrayList<>();
//        aux.add(new AssignStatement("a", new IntegerExpression(3)));
//        List<Expression> then = new ArrayList<>();
//        List<Expression> elsee = new ArrayList<>();
//        elsee.add(new AssignStatement("a", new IntegerExpression(3)));
//
//        then.add(new AssignStatement(
//                "b",
//                new OperatorExpression(
//                        new IntegerExpression(5),
//                        TokenType.TOK_MULT,
//                        new IntegerExpression(6)
//                )));
//        then.add(new ConditionalStatement(
//                        new OperatorExpression(
//                                new IdentifierExpression("a"),
//                                TokenType.TOK_EQ,
//                                new IntegerExpression(2)),
//                        aux,
//                        null
//                )
//        );
//
//        List<Expression> actual = new ArrayList<>();
//        actual.add(new ConditionalStatement(
//                        new OperatorExpression(
//                                new OperatorExpression(
//                                        new IdentifierExpression("a"),
//                                        TokenType.TOK_GT,
//                                        new IntegerExpression(0)
//                                ),
//                                TokenType.TOK_AND,
//                                new OperatorExpression(
//                                        new OperatorExpression(
//                                                new IdentifierExpression("a"),
//                                                TokenType.TOK_MULT,
//                                                new IntegerExpression(2)),
//                                        TokenType.TOK_LT,
//                                        new IntegerExpression(4)
//                                )
//                        ),
//                        then,
//                        elsee
//                )
//        );
//                        new OperatorExpression(
//                                new OperatorExpression(new IdentifierExpression("a"),TokenType.TOK_GT ,new IntegerExpression(0)),
//                                TokenType.TOK_AND,
//                                new OperatorExpression(
//                                        new OperatorExpression(new IdentifierExpression("a"),TokenType.TOK_MULT,new IntegerExpression(2)),
//                                        TokenType.TOK_LT,
//                                        new IntegerExpression(4))),
//                        new AssignStatement(
//                                "b",
//                                new OperatorExpression(
//                                        new IntegerExpression(
//                                                5),
//                                        TokenType.TOK_MULT,
//                                        new IntegerExpression(6)
//                                )
//                        ),
//                        new AssignStatement(
//                                "a",
//                                new IntegerExpression(3)
//
//                        )
//                )
//        );
//
//        assertEquals(result, actual);
//    }

//    @Test
//    public void testStatementWhile() {
//        Lexer l = new Lexer("while (a>0 && a * 2 < 4){ b = b + 1 } ");
//        List<Token> tokens = l.tokenize();
//        Parser p = new Parser(tokens);
//        List<Expression> result = p.parseBlock();
//
//
//        List<Expression> actual = new ArrayList<>();
//        actual.add(new LoopExpression(
//                    new OperatorExpression(
//                            new OperatorExpression(new IdentifierExpression("a"),TokenType.TOK_GT ,new IntegerExpression(0)),
//                            TokenType.TOK_AND,
//                            new OperatorExpression(
//                                    new OperatorExpression(new IdentifierExpression("a"),TokenType.TOK_MULT,new IntegerExpression(2)),
//                                    TokenType.TOK_LT,
//                                    new IntegerExpression(4))),
//                    new AssignStatement(
//                            "b",
//                            new OperatorExpression(
//                                new IdentifierExpression(
//                                        "b"),
//                                        TokenType.TOK_PLUS,
//                                        new IntegerExpression(1)
//                            )
//                    )
//        )
//        );
//
//
//        assertEquals(result, actual);
//    }

    @Test
    public void testSPLExample1() {
        Lexer l = new Lexer("facR( n ) :: Int -> Int {\n" +
                "if (n < 2 ) {\n " +
                    "return 1;\n " +
                "} else {\n" +
                    "return n * facR ( n - 1 );\n" +
                "}\n" +
            "}");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        List<Declaration> result = p.parseSPL();

        List<Declaration> actual = new ArrayList<>();

        IdentifierExpression name = new IdentifierExpression("facR");

        List<IdentifierExpression> args = new ArrayList<>();
        args.add(new IdentifierExpression("n"));

        List<Declaration> decls = new ArrayList<>();

        List<Statement> stats = new ArrayList<>();

        List<Statement> thenArm = new ArrayList<>();
        thenArm.add(new ReturnStatement(new IntegerExpression(1)));

        List<Statement> elseArm = new ArrayList<>();
        List<Expression> funArgs = new ArrayList<>();
        funArgs.add(new OperatorExpression(
                new IdentifierExpression("n"),
                TokenType.TOK_MINUS,
                new IntegerExpression(1)
        ));

        elseArm.add(new ReturnStatement(
                new OperatorExpression(
                    new IdentifierExpression("n"),
                    TokenType.TOK_MULT,
                    new CallExpression(
                            new IdentifierExpression("facR"),
                            funArgs
                )
        )));

        stats.add(new ConditionalStatement(
                new OperatorExpression(
                        new IdentifierExpression("n"),
                        TokenType.TOK_LT,
                        new IntegerExpression(2)
                ),
                thenArm,
                elseArm
        ));

        List<Type> fargsType = new ArrayList<>();
        fargsType.add(Types.intType);

        Type returnType = Types.intType;
        FunType funType = new FunType(fargsType, returnType);

        actual.add(new FunctionDeclaration(name, args, decls, stats, funType));

        assertEquals(result, actual);

    }

    @Test
    public void testSPLTupleListFunType() {
        Lexer l = new Lexer("facR( n ) :: (Int, Char) -> [Char] {\n" +
                "if (n < 2 ) {\n " +
                "return 1;\n " +
                "} else {\n" +
                "return n * facR ( n - 1 );\n" +
                "}\n" +
                "}");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        List<Declaration> result = p.parseSPL();

        List<Declaration> actual = new ArrayList<>();

        IdentifierExpression name = new IdentifierExpression("facR");

        List<IdentifierExpression> args = new ArrayList<>();
        args.add(new IdentifierExpression("n"));

        List<Declaration> decls = new ArrayList<>();

        List<Statement> stats = new ArrayList<>();

        List<Statement> thenArm = new ArrayList<>();
        thenArm.add(new ReturnStatement(new IntegerExpression(1)));

        List<Statement> elseArm = new ArrayList<>();
        List<Expression> funArgs = new ArrayList<>();
        funArgs.add(new OperatorExpression(
                new IdentifierExpression("n"),
                TokenType.TOK_MINUS,
                new IntegerExpression(1)
        ));

        elseArm.add(new ReturnStatement(
                new OperatorExpression(
                        new IdentifierExpression("n"),
                        TokenType.TOK_MULT,
                        new CallExpression(
                                new IdentifierExpression("facR"),
                                funArgs
                        )
                )));

        stats.add(new ConditionalStatement(
                new OperatorExpression(
                        new IdentifierExpression("n"),
                        TokenType.TOK_LT,
                        new IntegerExpression(2)
                ),
                thenArm,
                elseArm
        ));

        List<Type> fargsType = new ArrayList<>();
        fargsType.add(Types.tupleType(Types.intType, Types.charType));

        Type returnType = Types.listType(Types.charType);
        FunType funType = new FunType(fargsType, returnType);

        actual.add(new FunctionDeclaration(name, args, decls, stats, funType));

        assertEquals(result, actual);

    }

    @Test
    public void testSPLTupleListFunTypeReturnTuple() {
        Lexer l = new Lexer("facR( n ) :: [Int] -> (Char, Int) {\n" +
                "if (n < 2 ) {\n " +
                "return 1;\n " +
                "} else {\n" +
                "return n * facR ( n - 1 );\n" +
                "}\n" +
                "}");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        List<Declaration> result = p.parseSPL();

        List<Declaration> actual = new ArrayList<>();

        IdentifierExpression name = new IdentifierExpression("facR");

        List<IdentifierExpression> args = new ArrayList<>();
        args.add(new IdentifierExpression("n"));

        List<Declaration> decls = new ArrayList<>();

        List<Statement> stats = new ArrayList<>();

        List<Statement> thenArm = new ArrayList<>();
        thenArm.add(new ReturnStatement(new IntegerExpression(1)));

        List<Statement> elseArm = new ArrayList<>();
        List<Expression> funArgs = new ArrayList<>();
        funArgs.add(new OperatorExpression(
                new IdentifierExpression("n"),
                TokenType.TOK_MINUS,
                new IntegerExpression(1)
        ));

        elseArm.add(new ReturnStatement(
                new OperatorExpression(
                        new IdentifierExpression("n"),
                        TokenType.TOK_MULT,
                        new CallExpression(
                                new IdentifierExpression("facR"),
                                funArgs
                        )
                )));

        stats.add(new ConditionalStatement(
                new OperatorExpression(
                        new IdentifierExpression("n"),
                        TokenType.TOK_LT,
                        new IntegerExpression(2)
                ),
                thenArm,
                elseArm
        ));

        List<Type> fargsType = new ArrayList<>();
        fargsType.add(Types.listType(Types.intType));

        Type returnType =Types.tupleType(Types.charType, Types.intType);
        FunType funType = new FunType(fargsType, returnType);

        actual.add(new FunctionDeclaration(name, args, decls, stats, funType));

        assertEquals(result, actual);

    }

}
