import static org.junit.Assert.*;

import expressions.*;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;

import org.junit.Test;
import parser.Parser;

import java.util.ArrayList;
import java.util.List;

public class ParserTest {

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
	public void testAssignment() {
	    Lexer l = new Lexer("a = b");
        List<Token> tokens = l.tokenize();
		Parser p = new Parser(tokens);
		Expression result = p.parseExpression();

		String left = "a";
		Expression right = new IdentifierExpression("b");
		Expression actual = new AssignExpression(left, right);
		assertEquals(result, actual);
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
        StringBuilder builder = new StringBuilder();
        result.print(builder);
        System.out.println(builder.toString());
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
	public void testBoolean() {
        Lexer l = new Lexer("value = True");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        String left = "value";
        Expression right = new BooleanExpression(true);
        Expression actual = new AssignExpression(left, right);
        assertEquals(result, actual);
	}

    @Test
    public void testInteger() {
        Lexer l = new Lexer("value = 1");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        Expression result = p.parseExpression();

        String left = "value";
        Expression right = new IntegerExpression(1);
        Expression actual = new AssignExpression(left, right);
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

    @Test
    public void testStatementIf() {
        Lexer l = new Lexer("if (a>0 && a * 2 < 4){ b = 5 *6 if (a == 2){ c = 3 } } else { a = 3 } ");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        List<Expression> result = p.parseBlock();
        List<Expression> aux = new ArrayList<>();
        aux.add(new AssignExpression("c", new IntegerExpression(3)));
        List<Expression> then = new ArrayList<>();
        List<Expression> elsee = new ArrayList<>();
        elsee.add(new AssignExpression("a", new IntegerExpression(3)));

        then.add(new AssignExpression(
                "b",
                new OperatorExpression(
                        new IntegerExpression(5),
                        TokenType.TOK_MULT,
                        new IntegerExpression(6)
                )));
        then.add(new ConditionalExpression(
                new OperatorExpression(
                        new IdentifierExpression("a"),
                        TokenType.TOK_EQ,
                        new IntegerExpression(2)),
                aux,
                null
              )
        );

        List<Expression> actual = new ArrayList<>();
        actual.add(new ConditionalExpression(
                        new OperatorExpression(
                                new OperatorExpression(
                                        new IdentifierExpression("a"),
                                        TokenType.TOK_GT,
                                        new IntegerExpression(0)
                                ),
                                TokenType.TOK_AND,
                                new OperatorExpression(
                                        new OperatorExpression(
                                                new IdentifierExpression("a"),
                                                TokenType.TOK_MULT,
                                                new IntegerExpression(2)),
                                        TokenType.TOK_LT,
                                        new IntegerExpression(4)
                                )
                        ),
                        then,
                        elsee
                    )
        );
//                        new OperatorExpression(
//                                new OperatorExpression(new IdentifierExpression("a"),TokenType.TOK_GT ,new IntegerExpression(0)),
//                                TokenType.TOK_AND,
//                                new OperatorExpression(
//                                        new OperatorExpression(new IdentifierExpression("a"),TokenType.TOK_MULT,new IntegerExpression(2)),
//                                        TokenType.TOK_LT,
//                                        new IntegerExpression(4))),
//                        new AssignExpression(
//                                "b",
//                                new OperatorExpression(
//                                        new IntegerExpression(
//                                                5),
//                                        TokenType.TOK_MULT,
//                                        new IntegerExpression(6)
//                                )
//                        ),
//                        new AssignExpression(
//                                "a",
//                                new IntegerExpression(3)
//
//                        )
//                )
//        );

        assertEquals(result, actual);
    }

    @Test
    public void testStatementWhile() {
        Lexer l = new Lexer("while (a>0 && a * 2 < 4){ b = b + 1 } ");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        List<Expression> result = p.parseBlock();


        List<Expression> actual = new ArrayList<>();
        actual.add(new LoopExpression(
                    new OperatorExpression(
                            new OperatorExpression(new IdentifierExpression("a"),TokenType.TOK_GT ,new IntegerExpression(0)),
                            TokenType.TOK_AND,
                            new OperatorExpression(
                                    new OperatorExpression(new IdentifierExpression("a"),TokenType.TOK_MULT,new IntegerExpression(2)),
                                    TokenType.TOK_LT,
                                    new IntegerExpression(4))),
                    new AssignExpression(
                            "b",
                            new OperatorExpression(
                                new IdentifierExpression(
                                        "b"),
                                        TokenType.TOK_PLUS,
                                        new IntegerExpression(1)
                            )
                    )
        )
        );


        assertEquals(result, actual);
    }


//
//	@Test
//	public void testIdentifierField() {
//		SPLParser p = new SPLParser("alan.hd");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBinOp(
//								new AstExprIdentifier("alan"),
//								TokenType.TOK_DOT,
//								new AstExprField("hd")
//									)
//					, ast);
//		System.out.println(ast.toString());
//	}
//
//	@Test
//	public void testIdentifierMultipleFields() {
//		SPLParser p = new SPLParser("alan.hd.fst");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBinOp(
//								new AstExprIdentifier("alan"),
//								TokenType.TOK_DOT,
//								new AstExprBinOp(
//										new AstExprIdentifier("hd"),
//										TokenType.TOK_DOT,
//										new AstExprField("fst")
//											)
//									)
//					, ast);
//		System.out.println(ast.toString());
//	}
//
//	@Test
//	public void testIdentifierWrongFieldValue() {
//		SPLParser p = new SPLParser("alan.fst");
//		AstExpr ast = p.pExpr();
//		assertEquals(new AstExprBinOp(
//								new AstExprIdentifier("alan"),
//								TokenType.TOK_DOT,
//								new AstExprField("fst")
//									)
//					, ast);
//		System.out.println(ast.toString());
//	}
}
