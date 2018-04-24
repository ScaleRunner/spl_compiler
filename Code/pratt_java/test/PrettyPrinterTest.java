import lexer.Lexer;
import lexer.Token;
import org.junit.Test;
import parser.Parser;
import parser.declarations.Declaration;
import parser.exceptions.ParseException;
import parser.expressions.*;
import parser.statements.Statement;
import util.Node;
import util.PrettyPrinter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PrettyPrinterTest {

    @Test
    public void testInteger() {
        Lexer l = new Lexer("42");
        Parser p = new Parser(l.tokenize());
        IntegerExpression e = (IntegerExpression) p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("42", pp.getResultString());
    }

    @Test
    public void testCharacter() {
        Lexer l = new Lexer("'a'");
        Parser p = new Parser(l.tokenize());
        Node e = p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("'a'", pp.getResultString());
    }

    @Test
    public void testEmptyList() {
        Lexer l = new Lexer("[]");
        Parser p = new Parser(l.tokenize());
        Expression e = p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("[]", pp.getResultString());
    }

    @Test(expected = ParseException.class) //FOR NOW
    public void testList() {
        Lexer l = new Lexer("[1, 4]");
        Parser p = new Parser(l.tokenize());
        Expression e = p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("[1, 4]", pp.getResultString());
    }

    @Test
    public void testIsEmpty() {
        Lexer l = new Lexer("isEmpty(a)");
        Parser p = new Parser(l.tokenize());
        Expression e =  p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("isEmpty(a)", pp.getResultString());
    }

    @Test
    public void testTuple() {
        Lexer l = new Lexer("(1,2)");
        Parser p = new Parser(l.tokenize());
        Expression e = p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("(1, 2)", pp.getResultString());
    }

    @Test
    public void testNestedTuple() {
        Lexer l = new Lexer("(1,(a,([],[])))");
        Parser p = new Parser(l.tokenize());
        Expression e = p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("(1, (a, ([], [])))", pp.getResultString());
    }

    @Test
    public void testReturn() {
        Lexer l = new Lexer("return ([]    , (\n 1 + 6 \t, b));");
        Parser p = new Parser(l.tokenize());
        Statement s = p.parseStatement();
        PrettyPrinter pp = new PrettyPrinter();
        s.accept(pp);
        assertEquals("return ([], (1 + 6, b));", pp.getResultString());
    }

    @Test
    public void testPrint() {
        Lexer l = new Lexer("print(        1);");
        Parser p = new Parser(l.tokenize());
        Statement s = p.parseStatement();
        PrettyPrinter pp = new PrettyPrinter();
        s.accept(pp);
        assertEquals("print(1);", pp.getResultString());
    }

    @Test
    public void testIfConditional() {
        Lexer l = new Lexer("if(a==b && 1 == 1){\n this=correct\n; this=True;}");
        Parser p = new Parser(l.tokenize());
        Statement s = p.parseStatement();
        PrettyPrinter pp = new PrettyPrinter();
        s.accept(pp);
        assertEquals("if(a == b && 1 == 1) {\n\tthis = correct;\n\tthis = True;\n}", pp.getResultString());
    }

    @Test
    public void testFullConditional() {
        Lexer l = new Lexer("if(a==b){\n this=True\n;} else {this = False;}");
        Parser p = new Parser(l.tokenize());
        Statement s = p.parseStatement();
        PrettyPrinter pp = new PrettyPrinter();
        s.accept(pp);
        assertEquals("if(a == b) {\n" +
                "\tthis = True;\n" +
                "} else {\n" +
                "\tthis = False;\n" +
                "}", pp.getResultString());
    }

    @Test
    public void testWhile() {
        Lexer l = new Lexer("while(a==b && 1 == 1){\n this=correct\n; this=True;}");
        Parser p = new Parser(l.tokenize());
        Statement s = p.parseStatement();
        PrettyPrinter pp = new PrettyPrinter();
        s.accept(pp);
        assertEquals("while(a == b && 1 == 1) {\n\tthis = correct;\n\tthis = True;\n}", pp.getResultString());
    }

    @Test
    public void testNestedLoop() {
        Lexer l = new Lexer("while(a==b && 1 == 1){ while(outer==True) {this=correct; this=True;}}");
        Parser p = new Parser(l.tokenize());
        Statement s = p.parseStatement();
        PrettyPrinter pp = new PrettyPrinter();
        s.accept(pp);
        assertEquals("while(a == b && 1 == 1) {\n\twhile(outer == True) {\n\t\tthis = correct;\n\t\tthis = True;\n\t}\n}", pp.getResultString());
    }

    @Test
    public void testCallStatement() {
        Lexer l = new Lexer("foo(b,a,r,1);");
        Parser p = new Parser(l.tokenize());
        Statement s = p.parseStatement();
        PrettyPrinter pp = new PrettyPrinter();
        s.accept(pp);
        assertEquals("foo(b, a, r, 1);", pp.getResultString());
    }

    @Test
    public void testAssignStatement() {
        Lexer l = new Lexer("foo=bar\n;");
        Parser p = new Parser(l.tokenize());
        Statement s = p.parseStatement();
        PrettyPrinter pp = new PrettyPrinter();
        s.accept(pp);
        assertEquals("foo = bar;", pp.getResultString());
    }

    @Test
    public void testIdentifier() {
        Lexer l = new Lexer("hello_its_me_");
        Parser p = new Parser(l.tokenize());
        IdentifierExpression e = (IdentifierExpression) p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("hello_its_me_", pp.getResultString());
    }

    @Test
    public void testBoolean() {
        Lexer l = new Lexer("True");
        Parser p = new Parser(l.tokenize());
        BooleanExpression e = (BooleanExpression) p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("True", pp.getResultString());
    }

    @Test
    public void testCall() {
        Lexer l = new Lexer("foo()");
        Parser p = new Parser(l.tokenize());
        CallExpression e = (CallExpression) p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("foo()", pp.getResultString());
    }

    @Test
    public void testCallArguments() {
        Lexer l = new Lexer("foo(ba, r,1, True)");
        Parser p = new Parser(l.tokenize());
        CallExpression e = (CallExpression) p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("foo(ba, r, 1, True)", pp.getResultString());
    }

    @Test
    public void testPlus() {
        Lexer l = new Lexer("foo + bar+ 1        + True");
        Parser p = new Parser(l.tokenize());
        Expression e = p.parseExpression();
        PrettyPrinter pp = new PrettyPrinter();
        e.accept(pp);
        assertEquals("foo + bar + 1 + True", pp.getResultString());
    }


    @Test
    public void testPlusMult() {
        Lexer l = new Lexer("res = 4 \n  + 2*3; \n");
        Parser p = new Parser(l.tokenize());
        Statement result = p.parseStatement();
        PrettyPrinter pp = new PrettyPrinter();
        pp.visit(result);
        assertEquals("res = 4 + 2 * 3;", pp.getResultString());
    }

    @Test
    public void testAllOps() {
        Lexer l = new Lexer("res.hd = 1 + 2 - 3 * 4 / 5 % 6 : [] && True || False != c == 2 < 1 > 2 >= 3 <= 4;");
        Parser p = new Parser(l.tokenize());
        Statement result = p.parseStatement();
        PrettyPrinter pp = new PrettyPrinter();
        pp.visit(result);
        assertEquals("res.hd = 1 + 2 - 3 * 4 / 5 % 6 : [] && True || False != c == 2 < 1 > 2 >= 3 <= 4;", pp.getResultString());
    }

    @Test
    public void testPrintInput() {
        Lexer l = new Lexer("foo(bar) + 3 + True + field.hd");
        List<Token> tokens = l.tokenize();

        String reprint = PrettyPrinter.printLine(tokens);
        assertEquals("foo(bar) + 3 + true + field.hd", reprint);
    }


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
        ArrayList<Declaration> result = p.parseSPL();

        PrettyPrinter pp = new PrettyPrinter();
        pp.visit(result);

        assertEquals("facR(n) :: Int -> Int {\n" +
                        "\tif(n < 2) {\n" +
                        "\t\treturn 1;\n" +
                        "\t} else {\n" +
                        "\t\treturn n * facR(n - 1);\n" +
                        "\t}\n" +
                        "}"
        , pp.getResultString());
    }

    @Test
    public void testToString() {
        Lexer l = new Lexer("facR( n ) :: Int -> Int {\n" +
                "if (n < 2 ) {\n " +
                "return 1;\n " +
                "} else {\n" +
                "return n * facR ( n - 1 );\n" +
                "}\n" +
                "}");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Declaration> result = p.parseSPL();

        PrettyPrinter pp = new PrettyPrinter();
        pp.visit(result);

        assertEquals("facR(n) :: Int -> Int {\n" +
                        "\tif(n < 2) {\n" +
                        "\t\treturn 1;\n" +
                        "\t} else {\n" +
                        "\t\treturn n * facR(n - 1);\n" +
                        "\t}\n" +
                        "}"
                , pp.getResultString());
        assertEquals(pp.getResultString(), result.get(0).toString());
    }

    @Test
    public void testThreeFunctions(){
        Lexer l = new Lexer("facR( n ) :: Int -> Int {\n" +
                "if (n < 2 ) {\n " +
                "return 1;\n " +
                "} else {\n" +
                "return n * facR ( n - 1 );\n" +
                "}\n" +
                "}\n" +
                "fun(a) :: Int -> Int {\n" +
                "a = 3;\n"+
                "return facR(a+1);\n" +
                "}\n"+
                "get2() :: -> Int {\n" +
                "return 2;\n" +
                "}\n"+
                "myfun() :: -> Int {\n" +
                "return get2();\n" +
                "}");
        List<Token> tokens = l.tokenize();
        Parser p = new Parser(tokens);
        ArrayList<Declaration> result = p.parseSPL();

        PrettyPrinter pp = new PrettyPrinter();
        pp.visit(result);
        System.out.println(pp.getResultString());
    }
}
