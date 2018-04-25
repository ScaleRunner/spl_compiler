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

    private String runProgram(String program, boolean debug){
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

    @Test
    public void testIntegerConstant(){
        String result = runProgram("42", false);
        assertEquals("42", result);
    }

    @Test
    public void testAddition(){
        String result = runProgram("4 + 2", false);
        assertEquals("6", result);
    }

    @Test
    public void testAdditionVsMultiplicationPrecedence(){
        String result = runProgram("4 + 2 * 3 + 2", false);
        assertEquals("12", result);
    }

    @Test
    public void testSubtraction(){
        String result = runProgram("42-45", false);
        assertEquals("-3", result);
    }

    @Test
    public void testSubtractionAssociativity(){
        String result = runProgram("6 - 3 - 2", false);
        assertEquals("1", result);
        result = runProgram("6 - (3 - 2)", false);
        assertEquals("5", result);
        result = runProgram("(6 - 3) - 2", false);
        assertEquals("1", result);
    }

}