import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import codeGeneration.CodeGenerator;
import lexer.Lexer;
import org.junit.Test;

import parser.Parser;
import parser.declarations.Declaration;
import util.Node;

public class CodeGeneratorTest {

    private String runSSM(boolean debug) {
        try {
            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("-jar");
            command.add("ssm.jar");
            command.add("--cli");
            command.add("--file");
            command.add("test.ssm");
            ProcessBuilder builder = new ProcessBuilder(command);
            final Process process = builder.start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line;
            String result;
            result = line = br.readLine();
            if (debug) {
                do {
                    System.out.println(line);
                } while ((line = br.readLine()) != null);
            }
            return result;

        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private String runSPL(String program, boolean debug){
        Lexer l = new Lexer(program);
        Parser p = new Parser(l.tokenize());
        List<Declaration> nodes = p.parseSPL();
        CodeGenerator gen = new CodeGenerator();
        try {
            gen.generateCode(nodes, "test.ssm", "trap 0");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return runSSM(debug);
    }

    private String runExpression(String program, boolean debug){
        Lexer l = new Lexer(program);
        Parser p = new Parser(l.tokenize());
        Node n = p.parseExpression();
        CodeGenerator gen = new CodeGenerator();
        try {
            gen.generateCode(n, "test.ssm", "trap 0");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return runSSM(debug);
    }

    @Test
    public void testIntegerConstant(){
        String result = runExpression("42", false);
        assertEquals("42", result);
    }

    @Test
    public void testBoolean(){
        String result = runExpression("True", false);
        assertEquals("-1", result);

        result = runExpression("False", false);
        assertEquals("0", result);

        result = runExpression("True != False", false);
        //-1 represents True;
        assertEquals("-1", result);
    }

    @Test
    public void testPrefix(){
        String result = runExpression("--1", false);
        assertEquals("1", result);
        //TODO: Make sense of the following?
        /*
         True is represented as the integer 1
            complement representation:
                                             +    1
                                            0000 0001
         NEG does complement negation:
                                             -
                                            1111 1110
         And then +1?                        -     2?
                                            1111 1111
        */
        result = runExpression("!True", false);
        assertEquals("0", result);

        result = runExpression("!False", false);
        assertEquals("1", result);
    }

    @Test
    public void testAddition(){
        String result = runExpression("4 + 2", false);
        assertEquals("6", result);
    }

    @Test
    public void testAdditionVsMultiplicationPrecedence(){
        String result = runExpression("4 + 2 * 3 + 2", false);
        assertEquals("12", result);
    }

    @Test
    public void testSubtraction(){
        String result = runExpression("42-45", false);
        assertEquals("-3", result);
    }

    @Test
    public void testSubtractionAssociativity(){
        String result = runExpression("6 - 3 - 2", false);
        assertEquals("1", result);
        result = runExpression("6 - (3 - 2)", false);
        assertEquals("5", result);
        result = runExpression("(6 - 3) - 2", false);
        assertEquals("1", result);
    }

    @Test
    public void testAllBinaryOps(){
        String result = runExpression("42-45", false);
        assertEquals("-3", result);

        result = runExpression("7+3", false);
        assertEquals("10", result);

        result = runExpression("7*3", false);
        assertEquals("21", result);

        result = runExpression("6/3", false);
        assertEquals("2", result);

        result = runExpression("5%3", false);
        assertEquals("2", result);

        result = runExpression("5 > 3", false);
        assertNotEquals("0", result);

        result = runExpression("5 < 3", false);
        assertEquals("0", result);

        result = runExpression("5 >= 5", false);
        assertNotEquals("0", result);

        result = runExpression("5 >= 6", false);
        assertEquals("0", result);

        result = runExpression("5 <= 5", false);
        assertNotEquals("0", result);

        result = runExpression("5 <= 6", false);
        assertNotEquals("0", result);

        result = runExpression("6 <= 5", false);
        assertEquals("0", result);

        result = runExpression("1 == 1", false);
        assertNotEquals("0", result);

        result = runExpression("1 == 1 && 1 != 0", false);
        assertNotEquals("1", result);
    }

}