import lexer.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LexerTest {

    @Test
    public void testEmptyString() {
        Lexer l = new Lexer("");
        assertEquals(TokenType.TOK_EOF, l.nextToken().getType());
    }

    @Test
    public void testCommentRemoval() {
        Lexer l = new Lexer("// This is a comment \n" +
                "/* This is a comment block */");
        assertEquals(TokenType.TOK_EOF, l.nextToken().getType());
    }

    @Test
    public void testSingleDigitInteger() {
        Lexer l = new Lexer("5");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_INT, t.getType());
        assertEquals(5, t.getValue());
    }

    @Test
    public void testChar() {
        Lexer l = new Lexer("'a'");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_CHAR, t.getType());
        assertEquals('a', t.getValue());
    }

    @Test
    public void testCharNumeric() {
        Lexer l = new Lexer("'1'");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_CHAR, t.getType());
        assertEquals('1', t.getValue());
    }

    @Test(expected = TokenException.class)
    public void testCharNumericError() {
        Lexer l = new Lexer("'1");
        l.nextToken();
    }

    @Test
    public void testMultiDigitInteger() {
        Lexer l = new Lexer("4545372");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_INT, t.getType());
        assertEquals(4545372, t.getValue());
    }

    @Test
    public void testMultiDigitSignedInteger() {
        Lexer l = new Lexer("-4545372");
        Token t = l.nextToken();


        assertEquals(TokenType.TOK_MINUS, t.getType());
        t = l.nextToken();
        assertEquals(TokenType.TOK_INT, t.getType());


    }

    @Test
    public void testSinglePlus() {
        Lexer l = new Lexer("+");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_PLUS, t.getType());
    }

    @Test
    public void testSingleMinus() {
        Lexer l = new Lexer("-");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_MINUS, t.getType());
    }

    @Test
    public void testSingleMult() {
        Lexer l = new Lexer("*");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_MULT, t.getType());
    }

    @Test
    public void testSingleDiv() {
        Lexer l = new Lexer("/");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_DIV, t.getType());
    }

    @Test
    public void testSingleMod() {
        Lexer l = new Lexer("%");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_MOD, t.getType());
    }

    @Test
    public void testSingleCons() {
        Lexer l = new Lexer(":");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_CONS, t.getType());
    }

    @Test
    public void testSingleFuncDef() {
        Lexer l = new Lexer("::");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_FUNC_TYPE_DEF, t.getType());
    }

    @Test
    public void testMultipleIntegers() {
        Lexer l = new Lexer("12 34");
        Token t1 = l.nextToken();
        Token t2 = l.nextToken();
        assertEquals(TokenType.TOK_INT, t1.getType());
        assertEquals(TokenType.TOK_INT, t2.getType());
        assertEquals(12, t1.getValue());
        assertEquals(34, t2.getValue());
    }

    @Test
    public void testSimpleExpression() {
        Lexer l = new Lexer("1 + 12 + 300+4+");
        Token t1 = l.nextToken();
        Token t2 = l.nextToken();
        Token t3 = l.nextToken();
        Token t4 = l.nextToken();
        Token t5 = l.nextToken();
        Token t6 = l.nextToken();
        Token t7 = l.nextToken();
        Token t8 = l.nextToken();
        Token t9 = l.nextToken();
        assertEquals(TokenType.TOK_INT, t1.getType());
        assertEquals(TokenType.TOK_PLUS, t2.getType());
        assertEquals(TokenType.TOK_INT, t3.getType());
        assertEquals(TokenType.TOK_PLUS, t4.getType());
        assertEquals(TokenType.TOK_INT, t5.getType());
        assertEquals(TokenType.TOK_PLUS, t6.getType());
        assertEquals(TokenType.TOK_INT, t7.getType());
        assertEquals(TokenType.TOK_PLUS, t8.getType());
        assertEquals(TokenType.TOK_EOF, t9.getType());
    }

    @Test
    public void testMixedPlusesAndMinusesExpression() {
        Lexer l = new Lexer("1 - True + yes - if");
        Token t1 = l.nextToken();
        Token t2 = l.nextToken();
        Token t3 = l.nextToken();
        Token t4 = l.nextToken();
        Token t5 = l.nextToken();
        Token t6 = l.nextToken();
        Token t7 = l.nextToken();
        Token t8 = l.nextToken();
        Token t9 = l.nextToken();
        assertEquals(TokenType.TOK_INT, t1.getType());
        assertEquals(TokenType.TOK_MINUS, t2.getType());
        assertEquals(TokenType.TOK_BOOL, t3.getType());
        assertEquals(TokenType.TOK_PLUS, t4.getType());
        assertEquals(TokenType.TOK_IDENTIFIER, t5.getType());
        assertEquals(TokenType.TOK_MINUS, t6.getType());
        assertEquals(TokenType.TOK_KW_IF, t7.getType());
        assertEquals(TokenType.TOK_EOF, t9.getType());
    }

    @Test
    public void testSimpleDivison() {
        Lexer l = new Lexer("1 / 2");
        //1
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        // '/'
        assertEquals(TokenType.TOK_DIV, l.nextToken().getType());
        //2
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        //EOF
        assertEquals(TokenType.TOK_EOF, l.nextToken().getType());
    }

    @Test
    public void testSimpleModulo() {
        Lexer l = new Lexer("1 % 2");
        //1
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        // '/'
        assertEquals(TokenType.TOK_MOD, l.nextToken().getType());
        //2
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        //EOF
        assertEquals(TokenType.TOK_EOF, l.nextToken().getType());
    }

    @Test
    public void testMixedDivisionsAndMultiplicationsExpression() {
        Lexer l = new Lexer("1 / 12 * 300 / 4");

        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_DIV, l.nextToken().getType());
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_MULT, l.nextToken().getType());
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_DIV, l.nextToken().getType());
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_EOF, l.nextToken().getType());
    }

    @Test
    public void testMixedDivsMultsSumsSubsExpression() {
        Lexer l = new Lexer("1 + 12 * 300 / 4 - 100");

        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_PLUS, l.nextToken().getType());
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_MULT, l.nextToken().getType());
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_DIV, l.nextToken().getType());
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_MINUS, l.nextToken().getType());
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_EOF, l.nextToken().getType());
    }

    @Test
    public void testIdentifier() {
        Lexer l = new Lexer("missPiggy");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_IDENTIFIER, t.getType());
        assertEquals("missPiggy", ((TokenIdentifier) t).getValue());
    }

    @Test
    public void testSingleIdentifier() {
        Lexer l = new Lexer("v");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_IDENTIFIER, t.getType());
        assertEquals("v", ((TokenIdentifier) t).getValue());
    }

    @Test
    public void testIdentifier2() {
        Lexer l = new Lexer("miss_piggy_2");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_IDENTIFIER, t.getType());
        assertEquals("miss_piggy_2", ((TokenIdentifier) t).getValue());
    }

    @Test
    public void testIdentifierWithDigits() {
        Lexer l = new Lexer("mi55Piggy23");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_IDENTIFIER, t.getType());
        assertEquals("mi55Piggy23", ((TokenIdentifier) t).getValue());
    }

    @Test
    public void testKeywordIf() {
        Lexer l = new Lexer("if");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_KW_IF, t.getType());
    }

    @Test
    public void testKeywordPrefix() {
        Lexer l = new Lexer("iffy");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_IDENTIFIER, t.getType());
    }

    //Boolean and Logical Operators

    @Test
    public void testBooleanConstantTrue() {
        Lexer l = new Lexer("True");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_BOOL, t.getType());
        assertEquals(true, ((TokenBool) t).getValue());
    }

    @Test
    public void testBooleanConstantFalse() {
        Lexer l = new Lexer("False");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_BOOL, t.getType());
        assertEquals(false, ((TokenBool) t).getValue());
    }

    @Test
    public void testSingleEquals() {
        Lexer l = new Lexer("==");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_EQ, t.getType());
    }

    @Test
    public void testSingleEqualsComparison() {
        Lexer l = new Lexer(" 3 == 4");
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_EQ, l.nextToken().getType());
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
    }


    @Test
    public void testSingleLessThan() {
        Lexer l = new Lexer("<");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_LT, t.getType());
    }

    @Test
    public void testSingleLessThanComparison() {
        Lexer l = new Lexer(" 2 < 5");
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_LT, l.nextToken().getType());
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
    }

    @Test
    public void testSingleGreaterThan() {
        Lexer l = new Lexer("5 > 2");
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_GT, l.nextToken().getType());
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());

    }

    @Test
    public void testSingleLessThanEquals() {
        Lexer l = new Lexer("<=");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_LEQ, t.getType());
    }

    @Test
    public void testSingleLessThanEqualsComparison() {
        Lexer l = new Lexer(" 2 <= 5");
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_LEQ, l.nextToken().getType());
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
    }

    @Test
    public void testSingleGreaterThanEquals() {
        Lexer l = new Lexer(">=");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_GEQ, t.getType());
    }

    @Test
    public void testSingleGreatThanEqualsComparison() {
        Lexer l = new Lexer(" 2 >= 5");
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_GEQ, l.nextToken().getType());
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
    }

    @Test
    public void testSingleNotEquals() {
        Lexer l = new Lexer("!=");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_NEQ, t.getType());
    }

    @Test
    public void testSingleNotEqualsComparison() {
        Lexer l = new Lexer(" 2 != 5");
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
        assertEquals(TokenType.TOK_NEQ, l.nextToken().getType());
        assertEquals(TokenType.TOK_INT, l.nextToken().getType());
    }

    @Test
    public void testSingleNot() {
        Lexer l = new Lexer("!");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_NOT, t.getType());
    }

    @Test
    public void testSingleNotExpr() {
        Lexer l = new Lexer(" !True");
        assertEquals(TokenType.TOK_NOT, l.nextToken().getType());
        assertEquals(TokenType.TOK_BOOL, l.nextToken().getType());
    }

    @Test
    public void testSingleAnd() {
        Lexer l = new Lexer("&&");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_AND, t.getType());
    }

    @Test
    public void testSingleAndExpr() {
        Lexer l = new Lexer(" True && False");
        assertEquals(TokenType.TOK_BOOL, l.nextToken().getType());
        assertEquals(TokenType.TOK_AND, l.nextToken().getType());
        assertEquals(TokenType.TOK_BOOL, l.nextToken().getType());
    }

    @Test
    public void testSingleOr() {
        Lexer l = new Lexer("||");
        Token t = l.nextToken();
        assertEquals(TokenType.TOK_OR, t.getType());
    }

    @Test
    public void testSingleOrExpr() {
        Lexer l = new Lexer(" True || False");
        assertEquals(TokenType.TOK_BOOL, l.nextToken().getType());
        assertEquals(TokenType.TOK_OR, l.nextToken().getType());
        assertEquals(TokenType.TOK_BOOL, l.nextToken().getType());
    }

    ////////////////////////////
    // TESTING OF WHOLE LEXER
    ////////////////////////////
    @Test
    public void testSimple() {
        Lexer l = new Lexer("a + True + 1 + b.hd");
        List<Token> lexedTokenized = l.tokenize();

        List<Token> trueTokenized = new ArrayList<>();
        trueTokenized.add(new TokenIdentifier("a"));
        trueTokenized.add(new TokenOther(TokenType.TOK_PLUS));
        trueTokenized.add(new TokenBool(true));
        trueTokenized.add(new TokenOther(TokenType.TOK_PLUS));
        trueTokenized.add(new TokenInteger(1));
        trueTokenized.add(new TokenOther(TokenType.TOK_PLUS));
        trueTokenized.add(new TokenIdentifier("b"));
        trueTokenized.add(new TokenOther(TokenType.TOK_HD));


        for(int i = 0; i < trueTokenized.size(); i++){
            assertEquals(lexedTokenized.get(i), trueTokenized.get(i));
        }
//        System.out.println(lexedTokenized);
    }

    @Test(expected = TokenException.class)
    public void testWrongField() {
        Lexer l = new Lexer("a + True + 1 + b.hdd");
        List<Token> lexedTokenized = l.tokenize();
    }

    @Test
    public void testDoubleFieldExp() {
        Lexer l = new Lexer("a + True + 1 + b.hd.fst");
        List<Token> lexedTokenized = l.tokenize();

        List<Token> trueTokenized = new ArrayList<>();
        trueTokenized.add(new TokenIdentifier("a"));
        trueTokenized.add(new TokenOther(TokenType.TOK_PLUS));
        trueTokenized.add(new TokenBool(true));
        trueTokenized.add(new TokenOther(TokenType.TOK_PLUS));
        trueTokenized.add(new TokenInteger(1));
        trueTokenized.add(new TokenOther(TokenType.TOK_PLUS));
        trueTokenized.add(new TokenIdentifier("b"));

        trueTokenized.add(new TokenOther(TokenType.TOK_HD));
        trueTokenized.add(new TokenOther(TokenType.TOK_FST));


        for(int i = 0; i < trueTokenized.size(); i++){
            assertEquals(lexedTokenized.get(i), trueTokenized.get(i));
        }
//        System.out.println(lexedTokenized);
    }


}
