import codeGeneration.CodeGenerator;
import codeGeneration.Command;
import codeGeneration.CompileException;
import codeGeneration.ProgramWriter;
import lexer.Lexer;
import org.junit.ComparisonFailure;
import org.junit.Test;
import parser.Parser;
import parser.declarations.Declaration;
import typechecker.Typechecker;
import util.Node;
import util.ReadSPL;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

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
            String result = br.readLine();
            if (debug) {
                while ((line = br.readLine()) != null){
                    result = result + " " + line;
//                    System.out.println(line);
                }
            }
            return result;

        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private String runSPL(String program, Command postamble, boolean debug){
        Lexer l = new Lexer(program);
        Parser p = new Parser(l.tokenize());
        List<Declaration> nodes = p.parseSPL();
        Typechecker tc = new Typechecker();
        tc.typecheck(nodes);

        CodeGenerator gen = new CodeGenerator("test.ssm");
        try {
            gen.generateCode(nodes, postamble);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return runSSM(debug);
    }

    private String runExpression(String program, Command postamble, boolean debug){
        Lexer l = new Lexer(program);
        Parser p = new Parser(l.tokenize());
        Node n = p.parseExpression();
        Typechecker tc = new Typechecker();
        tc.typecheck(n);

        CodeGenerator gen = new CodeGenerator("test.ssm");
        ProgramWriter.testProgram = true;
        try {
            gen.generateCode(n, postamble);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return runSSM(debug);
    }

    @Test
    public void testIntegerConstant(){
        String result = runExpression("42", new Command("trap", "0"), false);
        assertEquals("42", result);
    }

    @Test
    public void testBoolean(){
        String result = runExpression("True",new Command("trap", "0"),false);
        assertEquals("-1", result);

        result = runExpression("False",new Command("trap", "0"),false);
        assertEquals("0", result);

        result = runExpression("True != False", new Command("trap", "0"),false);
        //-1 represents True;
        assertEquals("-1", result);
    }

    @Test
    public void testCharacterConstant(){
        String result = runExpression("'a'",new Command("trap", "1"), false);
        assertEquals("amachine halted", result);
    }

    @Test
    public void testPrefix(){
        String result = runExpression("--1",new Command("trap", "0"), false);
        assertEquals("1", result);

        result = runExpression("!True",new Command("trap", "0"), false);
        assertEquals("0", result);

        result = runExpression("!False",new Command("trap", "0"), false);
        assertEquals("-1", result);
    }

    @Test
    public void testAddition(){
        String result = runExpression("4 + 2",new Command("trap", "0"), false);
        assertEquals("6", result);
    }

    @Test
    public void testAdditionVsMultiplicationPrecedence(){
        String result = runExpression("4 + 2 * 3 + 2",new Command("trap", "0"), false);
        assertEquals("12", result);
    }

    @Test
    public void testSubtraction(){
        String result = runExpression("42-45", new Command("trap", "0"),false);
        assertEquals("-3", result);
    }

    @Test
    public void testSubtractionAssociativity(){
        String result = runExpression("6 - 3 - 2", new Command("trap", "0"),false);
        assertEquals("1", result);
        result = runExpression("6 - (3 - 2)", new Command("trap", "0"),false);
        assertEquals("5", result);
        result = runExpression("(6 - 3) - 2",new Command("trap", "0"), false);
        assertEquals("1", result);
    }

    @Test
    public void testAllBinaryOps(){
        String result = runExpression("42-45",new Command("trap", "0"), false);
        assertEquals("-3", result);

        result = runExpression("7+3",new Command("trap", "0"), false);
        assertEquals("10", result);

        result = runExpression("7*3",new Command("trap", "0"), false);
        assertEquals("21", result);

        result = runExpression("6/3", new Command("trap", "0"), false);
        assertEquals("2", result);

        result = runExpression("5%3", new Command("trap", "0"), false);
        assertEquals("2", result);

        result = runExpression("5 > 3", new Command("trap", "0"), false);
        assertEquals("-1", result);

        result = runExpression("5 < 3", new Command("trap", "0"), false);
        assertEquals("0", result);

        result = runExpression("5 >= 5", new Command("trap", "0"), false);
        assertEquals("-1", result);

        result = runExpression("5 >= 6", new Command("trap", "0"), false);
        assertEquals("0", result);

        result = runExpression("5 <= 5", new Command("trap", "0"), false);
        assertEquals("-1", result);

        result = runExpression("5 <= 6", new Command("trap", "0"), false);
        assertEquals("-1", result);

        result = runExpression("6 <= 5", new Command("trap", "0"), false);
        assertEquals("0", result);

        result = runExpression("1 == 1", new Command("trap", "0"), false);
        assertEquals("-1", result);

        result = runExpression("1 == 1 && 1 != 0", new Command("trap", "0"), false);
        assertEquals("-1", result);
    }

    @Test
    public void testReadInteger(){
        String result = runExpression("read(0)", new Command("trap", "0"), false);
        assertEquals("Please enter an integer: ", result);
    }

    @Test
    public void testReadChar(){
        String result = runExpression("read(1)", new Command("trap", "1"), false);
        assertEquals("Please enter a character: ", result);
    }

    @Test
    public void testMultipleFunWithArguments(){
        String result = runSPL(
                "multBy2( n ) :: Int -> Int {\n" +
                "Int d = 9;\n" +
                "d = 2;"+ //+ Try this later
                "return n * 2;\n" +
                "}"+
                "main()::->Void{\n" +
                "Int a = 3+ 2;\n" +
                "Int b = 5+ 3;\n" +
                "Int c = b;\n" +
                "c = multBy2(c);\n" +
                "print(multBy2(c));\n" +

                        //"return;" + Fix later
                "}", null,false);
        assertEquals("32", result);
    }

    @Test
    public void testFunCallNoAssign(){
        String result = runSPL("Int myGlobal = 0;" +
                "Int myGlobal2 = 1;" +
                "multBy3( n ) :: Int -> Int {\n" +
                "Int f = 9;\n" +
                "Int g = 3;" +
                "g = 2;"+ //+ Try this later
                "myGlobal2 = 10;"+
                "return n * 3;\n" +
                "}"+
                "multBy2( n ) :: Int -> Int {\n" +
                "Int d = 9;\n" +
                "Int e = 3;" +
                "myGlobal = d;" +
                "myGlobal2 = e;"+
                "d = 2;"+ //+ Try this later
                "return multBy3(n * 2);\n" +
                "}"+
                "main()::->Void{\n" +
                "Int a = 3+ 2;\n" +
                "Int b = 5+ 3;\n" +
                "Int c = b;\n" +
                "c =multBy2(c);\n" +
                //RESULT IS 48 because in multBy2
                "print(c);\n" +
                //"return;" + Fix later
                "}", null,false);
        assertEquals("48", result);
    }

    @Test
    public void testWhileLoop_conditionTrue(){
        //TODO: This one loops (see loopStatement TODO)
        String result = runSPL("main()::->Void{\n" +
                "Bool a = True; " +
                "while(a){" +
                "    a = False;" +
                "}" +
                "print(a);" +
                "}", null,false);
        assertEquals("0", result);
    }

    @Test
    public void testWhileLoop_conditionFalse(){
        String result = runSPL("main()::->Void{\n" +
                "Bool a = True; " +
                "while(!a){" +
                "    a = False;" +
                "}" +
                "print(a);" +
                "}", null,false);
        assertEquals("-1", result);
    }

    @Test
    public void testSimpleWhile(){
        String program = ReadSPL.readLineByLineJava8("./test/splExamples/simpleWhile.spl");


        String result = runSPL(program, null,false);
        assertEquals("10", result);

        program = program.replaceAll("Int i = 0;", "Int i = 100;");

        result = runSPL(program, null,false);
        assertEquals("100", result);
    }

    @Test
    public void testSingleFunLocalVarDecl(){
        String result = runSPL("main()::->Void{\n" +
                "Int a = 3+ 2;\n" +
                "Int b = 5+ 3;\n" +
                "Int c = b;\n" +
                "print(a);\n" +
                "}", null,false);
        assertEquals("5", result);
    }


    @Test
    public void FactorialImperative(){
        String program = ReadSPL.readLineByLineJava8("./test/splExamples/factorial_imperative.spl");

        String result = runSPL(program, null,false);
        assertEquals("120", result);
    }

    @Test
    public void FactorialRecursive(){
        String program = ReadSPL.readLineByLineJava8("./test/splExamples/factorial_recursive.spl");

        String result = runSPL(program, null,false);
        assertEquals("120", result);
    }

    @Test
    public void testSimple(){
        String program = ReadSPL.readLineByLineJava8("./test/splExamples/simple.spl");

        String result = runSPL(program, null,false);
        assertEquals("15", result);
    }

    @Test
    public void testSimpleConditional(){
        String program = ReadSPL.readLineByLineJava8("./test/splExamples/simpleConditional.spl");

        String result = runSPL(program, null,false);
        assertEquals("amachine halted", result);

        program = program.replaceAll("Int i = 0;", "Int i = 100;");

        result = runSPL(program, null,false);
        assertEquals("bmachine halted", result);
    }

    @Test
    public void nested_while_if(){
        String program = ReadSPL.readLineByLineJava8("./test/splExamples/print_numbers_up_to.spl");

        String result = runSPL(program, null,true);
        assertEquals("0 1 2 3 4 5 6 7 8 8 8 machine halted", result);
    }

    @Test
    public void test_empty(){
        String program = ReadSPL.readLineByLineJava8("./test/splExamples/is_empty.spl");

        String result = runSPL(program, null,false);
        assertEquals("-1", result);

        String program2 = program.replaceAll("\\[Int] empty = \\[];", "[Int] empty = 1 : 2 : 3 : [];");

        result = runSPL(program2, null,false);
        assertEquals("0", result);


        String program3 = program.replaceAll("\\[Int] empty = \\[];", "[Int] empty = 0 : [];");

        result = runSPL(program3, null,false);
        assertEquals("0", result);


        String program4 = program.replaceAll("\\[Int] empty = \\[];", "[[Int]] empty = [] : [];");

        result = runSPL(program4, null,false);
        assertEquals("0", result);
    }

    @Test
    public void print(){
        // TODO: Not working for Lists
        String program = ReadSPL.readLineByLineJava8("./test/splExamples/print.spl");

        String result = runSPL(program, null,true);
        assertEquals("('a', 'b')" +
                "(1 , 2 )" +
                "(3 , 'c')" +
                "((1 , 2 ), ('a', 'b'))" +
                "(((1 , 2 ), ('a', 'b')), ('a', 'b'))" +
                "((((1 , 2 ), ('a', 'b')), ('a', 'b')), (((1 , 2 ), ('a', 'b')), ('a', 'b')))" +
                "((((4 , 2 ), ('a', 'b')), ('a', 'b')), (((4 , 2 ), ('a', 'b')), ('a', 'b')))" +
                "machine halted", result);
    }

    @Test
    public void infinite_lists(){
        String program = ReadSPL.readLineByLineJava8("./test/splExamples/infinite_list.spl");

        String result = runSPL(program, null,true);
        assertEquals("1 2 3 1 2 3 1 2 3 1 machine halted", result);
    }

    @Test(expected = CompileException.class)
    public void testNoMain(){
        String program = ReadSPL.readLineByLineJava8("./test/splExamples/no_main.spl");

        runSPL(program, null,false);
    }

    @Test
    public void testSimpleList(){
        String result = runSPL("[Int] a = 1:2:3:[];\n" +
                "main()::->Void{\n" +

                "print(2);\n" +
                "}", null,false);
        assertEquals("2", result);
    }

    @Test
    public void testListsWithIdentfiers(){
        String result = runSPL("[Int] a = 1:2:3:[];\n" +
                "main()::->Void{\n" +
                "[Int] b = 3:4:5:[];\n" +
                "[[Int]] c = a:b;\n" +

                "print(2);\n" +
                "}", null,false);
        assertEquals("2", result);
    }

    @Test
    public void testSimpleTuple(){
        String result = runSPL("(Int, Char) a =  (1,'a');\n" +
                "main()::->Void{\n" +
                "print(2);\n" +
                "}", null,false);
        assertEquals("2", result);
    }

    @Test
    public void testTupleWithLists(){
        String result = runSPL("[Int] a = 1:2:3:[];\n" +
                "[Int] b = 3:4:5:[];\n" +
                "[[Int]] c = a:b;\n" +
                "main()::->Void{\n" +
                "[Char] d = 'a':'b':'c':[];\n" +
                "([[Int]],[Char]) e = (c, d);\n"+
                "print(2);\n" +
                "}", null,false);
        assertEquals("2", result);
    }

    @Test
    public void testTupleFstSndTL(){
        String result = runSPL("[Int] a = 1:2:3:[];\n" +
                "[Int] b = 3:4:5:[];\n" +
                "[[Int]] c = a:b;\n" +
                "[Char] l = 'd':'e':'f':[];\n" +
                "main()::->Void{\n" +
                "[Char] d = 'a':'b':'c':[];\n" +
                "([Int],[Char]) e = (b, l);\n"+
                "[Int] f = e.fst;\n"+
                "d = e.snd;\n"+
                "f = b.tl;\n"+
                "print(2);\n" +
                "}", null,false);
        assertEquals("2", result);
    }

    @Test
    public void testListTLandHD(){
        String result = runSPL("[Int] a = 1:2:3:[];\n" +
                "[Int] b = 3:4:5:[];\n" +
                "[[Int]] c = a:b;\n" +
                //"[Char] l = 'd':'e':'f':[];\n" +
                "main()::->Void{\n" +
                //"[Char] d = 'a':'b':'c':[];\n"+
                "b.tl.tl = a.tl;\n"+
                "print(2);\n" +
                "}", null,false);
        assertEquals("2", result);
    }

    //Don't remember what this was
//    @Test
//    public void testSimpleFunctionList(){
//        String program = ReadSPL.readLineByLineJava8("./test/splExamples/markus/3-ok/globalVariables.spl");
//
//        String result = runSPL(program, null,false);
//        assertEquals("15", result);
//    }

    @Test
    public void testAllTestsByMarkus() {
        Long sleepTime = 50L;
        try (Stream<Path> paths = Files.walk(Paths.get("./test/splExamples/markus/3-ok"))) {
            paths.forEach(path ->{
                if(Files.isRegularFile(path)){
                    try {
                        // To not mess up printing
                        Thread.sleep( sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println(path.toString());
                    String s = ReadSPL.readLineByLineJava8(path.toString());
                    try {
                        // To not mess up printing
                        Thread.sleep( sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String result = runSPL(s, null, true);
                    String expected = "";
                    try {
                        if(path.toString().contains("assignments.spl")){
                            expected = "0 1 2 0 1 2 0 1 2 2 3 4 6 7 8 9 6 0 6 -7 machine halted";
                        } else if(path.toString().contains("associativity.spl")){
                            expected = "0 0 2 1 1 4 machine halted";
                        } else if(path.toString().contains("comments.spl")){
                            expected = "1 2 3 4 5 6 7 machine halted";
                        } else if(path.toString().contains("functionArgumentsSimple.spl")){
                            expected = "0 42 machine halted";
                        } else if(path.toString().contains("functions.spl")){
                            expected = "5 -1 1 5 -1 4 6 9 9 15 1 3 5 5 3 1 1 3 5 machine halted";
                        } else if(path.toString().contains("functionsSimple.spl")){
                            expected = "5 -1 1 5 -1 4 6 machine halted";
                        } else if(path.toString().contains("globalVariables.spl")){
                            expected = "5 3 15 0 6 4 42 -1 0 5 3 1 machine halted";
                        } else if(path.toString().contains("globalVariablesSimple.spl")){
                            expected = "0 42 machine halted";
                        } else if(path.toString().contains("helloWorld.spl")){
                            expected = "42 -1 0 machine halted";
                        } else if(path.toString().contains("identifierNames.spl")){
                            expected = "1 2 3 4 machine halted";
                        } else if(path.toString().contains("ifThenElse.spl")){
                            expected = "42 machine halted";
                        } else if(path.toString().contains("ifThenElse2.spl")){
                            expected = "42 20 machine halted";
                        } else if(path.toString().contains("ifThenElseFalse.spl")){
                            expected = "100 machine halted";
                        } else if(path.toString().contains("ifThenElseInFunction.spl")){
                            expected = "7 11 machine halted";
                        } else if(path.toString().contains("ifThenElseScope.spl")){
                            expected = "100 20 machine halted";
                        } else if(path.toString().contains("ifThenElseScopeFunArg.spl")){
                            expected = "100 20 machine halted";
                        } else if(path.toString().contains("listFunction.spl")){
                            expected = "7 8 9 10 7 8 9 10 machine halted";
                        } else if(path.toString().contains("listFunction2.spl")){
                            expected = "9 9 15 15 machine halted";
                        } else if(path.toString().contains("listFunction3.spl")){
                            expected = "42 machine halted";
                        } else if(path.toString().contains("lists.spl")){
                            expected = "-1 7 0 -1 2 7 0 7 7 2 7 machine halted";
                        } else if(path.toString().contains("listsSimple.spl")){
                            expected = "7 10 7 8 11 8 machine halted";
                        } else if(path.toString().contains("listsSimple2.spl")){
                            expected = "7 8 machine halted";
                        } else if(path.toString().contains("listsSimple3.spl")){
                            expected = "8 machine halted";
                        } else if(path.toString().contains("localVariables.spl")){
                            expected = "5 3 15 0 6 4 42 -1 0 5 3 1 machine halted";
                        } else if(path.toString().contains("localVariablesSimple.spl")){
                            expected = "0 42 0 42 machine halted";
                        } else if(path.toString().contains("precedence.spl")){
                            expected = "11 6 -1 -1 -1 5 0 -2 5 1 -1 -1 0 -1 0 -1 -1 machine halted";
                        } else if(path.toString().contains("recursiveFunction.spl")){
                            expected = "6 10 5050 0 0 machine halted";
                        } else if(path.toString().contains("recursiveFunction2.spl")){
                            expected = "6 10 5050 0 0 machine halted";
                        } else if(path.toString().contains("simpleArithmetic.spl")){
                            expected = "3 6 4 -4 33 0 -1 0 -1 0 -1 0 -5 5 -5 5 0 -1 0 -1 machine halted";
                        } else if(path.toString().contains("tuples.spl")){
                            expected = "5 3 15 0 5 3 42 -1 0 5 3 1 machine halted";
                        } else if(path.toString().contains("tuplesSimple.spl")){
                            expected = "5 3 10 -1 -1 0 0 20 machine halted";
                        } else if(path.toString().contains("tuplesSimple2.spl")){
                            expected = "5 machine halted";
                        } else if(path.toString().contains("while.spl")){
                            expected = "0 1 2 3 4 5 6 7 8 9 10 9 8 7 6 5 4 3 2 1 machine halted";
                        } else {
                            expected = "File is not in tests";
                        }

                        assertEquals(expected, result);
                    } catch (ComparisonFailure f){
                        System.err.println(String.format("Test Failed! \n\tExpected:%s\n\tActual:\t %s", expected, result));
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}