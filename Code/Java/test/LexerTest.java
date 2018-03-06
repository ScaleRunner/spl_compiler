import static org.junit.Assert.*;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenBool;
import lexer.TokenIdentifier;
import lexer.TokenInteger;
import lexer.TokenType;

import org.junit.Test;

public class LexerTest {

	@Test
	public void testEmptyString() {
		Lexer l = new Lexer("");
		assertEquals(TokenType.TOK_EOF, l.nextToken().getTokenType());
	}

	@Test
	public void testSingleDigitInteger() {
		Lexer l = new Lexer("5");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_INT, t.getTokenType());
		assertEquals(5, ((TokenInteger) t).getValue());
	}

	@Test
	public void testMultiDigitInteger() {
		Lexer l = new Lexer("4545372");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_INT, t.getTokenType());
		assertEquals(4545372, ((TokenInteger) t).getValue());
	}

	@Test
	public void testMultiDigitSignedInteger() {
		Lexer l = new Lexer("-4545372");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_INT, t.getTokenType());
		assertEquals(-4545372, ((TokenInteger) t).getValue());
	}

	@Test
	public void testSinglePlus() {
		Lexer l = new Lexer("+");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_PLUS, t.getTokenType());
	}

	@Test
	public void testSingleMinus() {
		Lexer l = new Lexer("-");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_MINUS, t.getTokenType());
	}

	@Test
	public void testSingleMult() {
		Lexer l = new Lexer("*");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_MULT, t.getTokenType());
	}
	
	@Test
	public void testSingleDiv() {
		Lexer l = new Lexer("/");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_DIV, t.getTokenType());
	}
	
	@Test
	public void testSingleMod() {
		Lexer l = new Lexer("%");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_MOD, t.getTokenType());
	}
	
	@Test
	public void testSingleCons() {
		Lexer l = new Lexer(":");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_CONS, t.getTokenType());
	}
	
	@Test
	public void testSingleFuncDef() {
		Lexer l = new Lexer("::");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_FUNC_TYPE_DEF, t.getTokenType());
	}

	@Test
	public void testMultipleIntegers() {
		Lexer l = new Lexer("12 34");
		Token t1 = l.nextToken();
		Token t2 = l.nextToken();
		assertEquals(TokenType.TOK_INT, t1.getTokenType());
		assertEquals(TokenType.TOK_INT, t2.getTokenType());
		assertEquals(12, ((TokenInteger) t1).getValue());
		assertEquals(34, ((TokenInteger) t2).getValue());
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
		assertEquals(TokenType.TOK_INT, t1.getTokenType());
		assertEquals(TokenType.TOK_PLUS, t2.getTokenType());
		assertEquals(TokenType.TOK_INT, t3.getTokenType());
		assertEquals(TokenType.TOK_PLUS, t4.getTokenType());
		assertEquals(TokenType.TOK_INT, t5.getTokenType());
		assertEquals(TokenType.TOK_PLUS, t6.getTokenType());
		assertEquals(TokenType.TOK_INT, t7.getTokenType());
		assertEquals(TokenType.TOK_PLUS, t8.getTokenType());
		assertEquals(TokenType.TOK_EOF, t9.getTokenType());
	}

	@Test
	public void testMixedPlussesAndMinusesExpression() {
		Lexer l = new Lexer("1 - 12 + 300 - 4");
		Token t1 = l.nextToken();
		Token t2 = l.nextToken();
		Token t3 = l.nextToken();
		Token t4 = l.nextToken();
		Token t5 = l.nextToken();
		Token t6 = l.nextToken();
		Token t7 = l.nextToken();
		Token t8 = l.nextToken();
		Token t9 = l.nextToken();
		assertEquals(TokenType.TOK_INT, t1.getTokenType());
		assertEquals(TokenType.TOK_MINUS, t2.getTokenType());
		assertEquals(TokenType.TOK_INT, t3.getTokenType());
		assertEquals(TokenType.TOK_PLUS, t4.getTokenType());
		assertEquals(TokenType.TOK_INT, t5.getTokenType());
		assertEquals(TokenType.TOK_MINUS, t6.getTokenType());
		assertEquals(TokenType.TOK_INT, t7.getTokenType());
		assertEquals(TokenType.TOK_EOF, t9.getTokenType());
	}
	
	@Test
	public void testSimpleDivison() {
		Lexer l = new Lexer("1 / 2");
		//1
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		// '/'
		assertEquals(TokenType.TOK_DIV, l.nextToken().getTokenType());
		//2
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		//EOF
		assertEquals(TokenType.TOK_EOF, l.nextToken().getTokenType());
	}
	
	@Test
	public void testSimpleModulo() {
		Lexer l = new Lexer("1 % 2");
		//1
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		// '/'
		assertEquals(TokenType.TOK_MOD, l.nextToken().getTokenType());
		//2
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		//EOF
		assertEquals(TokenType.TOK_EOF, l.nextToken().getTokenType());
	}
	
	@Test
	public void testMixedDivisionsAndMultiplicationsExpression() {
		Lexer l = new Lexer("1 / 12 * 300 / 4");
		
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_DIV, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_MULT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_DIV, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_EOF, l.nextToken().getTokenType());
	}
	
	@Test
	public void testMixedDivsMultsSumsSubsExpression() {
		Lexer l = new Lexer("1 + 12 * 300 / 4 - 100");
		
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_PLUS, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_MULT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_DIV, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_MINUS, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_EOF, l.nextToken().getTokenType());
	}

	@Test
	public void testIdentifier() {
		Lexer l = new Lexer("missPiggy");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_IDENTIFIER, t.getTokenType());
		assertEquals("missPiggy", ((TokenIdentifier) t).getValue());
	}
	
	@Test
	public void testSingleIdentifier() {
		Lexer l = new Lexer("v");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_IDENTIFIER, t.getTokenType());
		assertEquals("v", ((TokenIdentifier) t).getValue());
	}

	@Test
	public void testIdentifier2() {
		Lexer l = new Lexer("miss_piggy_2");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_IDENTIFIER, t.getTokenType());
		assertEquals("miss_piggy_2", ((TokenIdentifier) t).getValue());
	}

	@Test
	public void testIdentifierWithDigits() {
		Lexer l = new Lexer("mi55Piggy23");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_IDENTIFIER, t.getTokenType());
		assertEquals("mi55Piggy23", ((TokenIdentifier) t).getValue());
	}

	@Test
	public void testKeywordIf() {
		Lexer l = new Lexer("if");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_KW_IF, t.getTokenType());
	}

	@Test
	public void testKeywordPrefix() {
		Lexer l = new Lexer("iffy");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_IDENTIFIER, t.getTokenType());
	}
	
	//Boolean and Logical Operators

	@Test
	public void testBooleanConstantTrue() {
		Lexer l = new Lexer("True");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_BOOL, t.getTokenType());
		assertEquals(true, ((TokenBool)t).getValue());
	}

	@Test
	public void testBooleanConstantFalse() {
		Lexer l = new Lexer("False");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_BOOL, t.getTokenType());
		assertEquals(false, ((TokenBool)t).getValue());
	}
	
	@Test
	public void testSingleEquals(){
		Lexer l = new Lexer("==");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_EQ, t.getTokenType());
	}
	
	@Test
	public void testSingleEqualsComparison(){
		Lexer l = new Lexer(" 3 == 4");
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_EQ, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
	}
	
	
	@Test
	public void testSingleLessThan(){
		Lexer l = new Lexer("<");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_LT, t.getTokenType());
	}
	
	@Test
	public void testSingleLessThanComparison(){
		Lexer l = new Lexer(" 2 < 5");
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_LT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
	}
	
	@Test
	public void testSingleGreaterThan(){
		Lexer l = new Lexer("5 > 2");
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_GT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		
	}
	
	@Test
	public void testSingleLessThanEquals(){
		Lexer l = new Lexer("<=");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_LEQ, t.getTokenType());
	}
	
	@Test
	public void testSingleLessThanEqualsComparison(){
		Lexer l = new Lexer(" 2 <= 5");
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_LEQ, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
	}
	
	@Test
	public void testSingleGreaterThanEquals(){
		Lexer l = new Lexer(">=");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_GEQ, t.getTokenType());
	}
	
	@Test
	public void testSingleGreatThanEqualsComparison(){
		Lexer l = new Lexer(" 2 >= 5");
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_GEQ, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
	}
	
	@Test
	public void testSingleNotEquals(){
		Lexer l = new Lexer("!=");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_NEQ, t.getTokenType());
	}
	
	@Test
	public void testSingleNotEqualsComparison(){
		Lexer l = new Lexer(" 2 != 5");
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_NEQ, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_INT, l.nextToken().getTokenType());
	}
	
	@Test
	public void testSingleNot(){
		Lexer l = new Lexer("!");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_NOT, t.getTokenType());
	}
	
	@Test
	public void testSingleNotExpr(){
		Lexer l = new Lexer(" !True");
		assertEquals(TokenType.TOK_NOT, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_BOOL, l.nextToken().getTokenType());
	}
	
	@Test
	public void testSingleAnd(){
		Lexer l = new Lexer("&&");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_AND, t.getTokenType());
	}
	
	@Test
	public void testSingleAndExpr(){
		Lexer l = new Lexer(" True && False");
		assertEquals(TokenType.TOK_BOOL, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_AND, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_BOOL, l.nextToken().getTokenType());
	}
	
	@Test
	public void testSingleOr(){
		Lexer l = new Lexer("||");
		Token t = l.nextToken();
		assertEquals(TokenType.TOK_OR, t.getTokenType());
	}
	
	@Test
	public void testSingleOrExpr(){
		Lexer l = new Lexer(" True || False");
		assertEquals(TokenType.TOK_BOOL, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_OR, l.nextToken().getTokenType());
		assertEquals(TokenType.TOK_BOOL, l.nextToken().getTokenType());
	}
		
	
	
}
