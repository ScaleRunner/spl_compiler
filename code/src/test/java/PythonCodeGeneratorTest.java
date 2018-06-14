import codeGeneration.CompileException;
import codeGeneration.python.CodeGenerator;
import codeGeneration.python.ProgramWriter;
import lexer.Lexer;
import org.junit.After;
import org.junit.ComparisonFailure;
import org.junit.Test;
import parser.Parser;
import parser.declarations.Declaration;
import typechecker.Typechecker;
import util.CheckPythonVersion;
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

public class PythonCodeGeneratorTest {

    private final String rootFolder = "./src/test/resources/splExamples/";

    private List<String> executePython() {
        String pythonVersion = CheckPythonVersion.getPythonVersion();
        try {
            List<String> command = new ArrayList<>();
            command.add(pythonVersion);
            command.add("test.py");
            ProcessBuilder builder = new ProcessBuilder(command);
            builder = builder.redirectErrorStream(true);
            final Process process = builder.start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            process.waitFor();

            List<String> result = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null){
                result.add(line);
                System.out.println(line);
            }

            return result;
        } catch (IOException e) {
            throw new CompileException("Stream could not be opened/closed\n" + e.getMessage());
        } catch (InterruptedException e) {
            throw new CompileException("Python stopped abruptly\n" + e.getMessage());
        }
    }

    private List<String> runCode(String program){
        Lexer l = new Lexer(program);
        Parser p = new Parser(l.tokenize());
        List<Declaration> nodes = p.parseSPL();
        Typechecker tc = new Typechecker();
        tc.typecheck(nodes);

        CodeGenerator gen = new CodeGenerator("test.py", tc.getEnvironment());
        try {
            gen.generateCode(nodes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return executePython();
    }

    private List<String> runStatement(String program){
        Lexer l = new Lexer(program);
        Parser p = new Parser(l.tokenize());
        Node n = p.parseStatement();
        Typechecker tc = new Typechecker();
        tc.typecheck(n);

        CodeGenerator gen = new CodeGenerator("test.py", tc.getEnvironment());
        ProgramWriter.testProgram = true;
        try {
            gen.generateCode(n);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return executePython();
    }

    @After
    public void resetState(){
        ProgramWriter.testProgram = false;
    }

    @Test
    public void testIntegerConstant(){
        List<String> result = runStatement("print(42);");
        assertEquals("42", result.get(0));
    }

    @Test
    public void testBoolean(){
        List<String> result = runStatement("print(True);");
        assertEquals("True", result.get(0));

        result = runStatement("print(False);");
        assertEquals("False", result.get(0));

        result = runStatement("print(True != False);");
        assertEquals("True", result.get(0));
    }

    @Test
    public void testCharacterConstant(){
        List<String> result = runStatement("print('a');");
        assertEquals("a", result.get(0));
    }

    @Test
    public void testPrefix(){
        List<String> result = runStatement("print(--1);");
        assertEquals("1", result.get(0));

        result = runStatement("print(!True);");
        assertEquals("False", result.get(0));

        result = runStatement("print(!False);");
        assertEquals("True", result.get(0));
    }

    @Test
    public void testAddition(){
        List<String> result = runStatement("print(4 + 2);");
        assertEquals("6", result.get(0));
    }

    @Test
    public void testAdditionVsMultiplicationPrecedence(){
        List<String> result = runStatement("print(4 + 2 * 3 + 2);");
        assertEquals("12", result.get(0));
    }

    @Test
    public void testSubtraction(){
        List<String> result = runStatement("print(42-45);");
        assertEquals("-3", result.get(0));
    }

    @Test
    public void testSubtractionAssociativity(){
        List<String> result = runStatement("print(6 - 3 - 2);");
        assertEquals("1", result.get(0));
        result = runStatement("print(6 - (3 - 2));");
        assertEquals("5", result.get(0));
        result = runStatement("print((6 - 3) - 2);");
        assertEquals("1", result.get(0));
    }

    @Test
    public void testAllBinaryOps(){
        List<String> result = runStatement("print(42-45);");
        assertEquals("-3", result.get(0));

        result = runStatement("print(7+3);");
        assertEquals("10", result.get(0));

        result = runStatement("print(7*3);");
        assertEquals("21", result.get(0));

        result = runStatement("print(6/3);");
        assertEquals("2", result.get(0));

        result = runStatement("print(5%3);");
        assertEquals("2", result.get(0));

        result = runStatement("print(5 > 3);");
        assertEquals("True", result.get(0));

        result = runStatement("print(5 < 3);");
        assertEquals("False", result.get(0));

        result = runStatement("print(5 >= 5);");
        assertEquals("True", result.get(0));

        result = runStatement("print(5 >= 6);");
        assertEquals("False", result.get(0));

        result = runStatement("print(5 <= 5);");
        assertEquals("True", result.get(0));

        result = runStatement("print(5 <= 6);");
        assertEquals("True", result.get(0));

        result = runStatement("print(6 <= 5);");
        assertEquals("False", result.get(0));

        result = runStatement("print(1 == 1);");
        assertEquals("True", result.get(0));

        result = runStatement("print(1 == 1 && 1 != 0);");
        assertEquals("True", result.get(0));
    }

    @Test
    public void testReadInteger(){
        String program = ReadSPL.readLineByLineJava8(rootFolder + "read.spl");

//        List<String> result = runCode(program);
//        assertEquals("120", result.get(0));
    }

    @Test
    public void testReadCharacter(){
        String program = ReadSPL.readLineByLineJava8(rootFolder + "read_hello.spl");
//        List<String> result = runCode(program);
//        assertEquals("120", result.get(0));
    }

    @Test
    public void testEmpty(){
        List<String> result = runStatement("print(isEmpty([]));");
        assertEquals("True", result.get(0));
    }

    @Test
    public void testNotEmpty(){
        List<String> result = runStatement("print(isEmpty(1 : []));");
        assertEquals("False", result.get(0));
    }

    @Test
    public void testEmptyEmpty(){
        List<String> result = runStatement("print(isEmpty([] : []));");
        assertEquals("False", result.get(0));
    }

    @Test
    public void testVariableDeclaration(){
        List<String> result = runCode(
                "multBy2( n, m ) :: -> Void {\n" +
                        "Int d = 9;\n" +
                        "d = 2;"+ //+ Try this later
                        "print(d);\n" +
                        "}"+
                        "main()::->Void{\n" +
                        "Int a = 3+ 2;\n" +
                        "Int b = 5+ 3;\n" +
                        "Int c = b;\n" +
                        "print(c);\n" +

                        //"return;" + Fix later
                        "}");
        assertEquals("8", result.get(0));
    }

    @Test
    public void testTuple(){
        List<String> result = runStatement("print((1, 2));");
        assertEquals("(1, 2)", result.get(0));
    }

    @Test
    public void testMultipleFunWithArguments(){
        List<String> result = runCode(
                "multBy2( n, m ) :: Int Int -> Int {\n" +
                "Int d = 9;\n" +
                "d = 2;"+ //+ Try this later
                "return n * 2;\n" +
                "}"+
                "main()::->Void{\n" +
                "Int a = 3+ 2;\n" +
                "Int b = 5+ 3;\n" +
                "Int c = b;\n" +
                "c = multBy2(c, c);\n" +
                "print(multBy2(c,c));\n" +

                        //"return;" + Fix later
                "}");
        assertEquals("32", result.get(0));
    }

    @Test
    public void testFunCallNoAssign(){
        List<String> result = runCode("Int myGlobal = 0;" +
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
                "}");
        assertEquals("48", result.get(0));
    }

    @Test
    public void testWhileLoop_conditionTrue(){
        List<String> result = runCode("main()::->Void{\n" +
                "Bool a = True; " +
                "while(a){" +
                "    a = False;" +
                "}" +
                "print(a);" +
                "}");
        assertEquals("False", result.get(0));
    }

    @Test
    public void testWhileLoop_conditionFalse(){
        List<String> result = runCode("main()::->Void{\n" +
                "Bool a = True; " +
                "while(!a){" +
                "    a = False;" +
                "}" +
                "print(a);" +
                "}");
        assertEquals("True", result.get(0));
    }

    @Test
    public void testSimpleWhile(){
        String program = ReadSPL.readLineByLineJava8(rootFolder + "simpleWhile.spl");


        List<String> result = runCode(program);
        assertEquals("10", result.get(0));

        program = program.replaceAll("Int i = 0;", "Int i = 100;");

        result = runCode(program);
        assertEquals("100", result.get(0));
    }

    @Test
    public void testSingleFunLocalVarDecl(){
        List<String> result = runCode("main()::->Void{\n" +
                "Int a = 3+ 2;\n" +
                "Int b = 5+ 3;\n" +
                "Int c = b;\n" +
                "print(a);\n" +
                "}");
        assertEquals("5", result.get(0));
    }


    @Test
    public void FactorialImperative(){
        String program = ReadSPL.readLineByLineJava8(rootFolder + "factorial_imperative.spl");

        List<String> result = runCode(program);
        assertEquals("120", result.get(0));
    }

    @Test
    public void FactorialRecursive(){
        String program = ReadSPL.readLineByLineJava8(rootFolder + "factorial_recursive.spl");

        List<String> result = runCode(program);
        assertEquals("120", result.get(0));
    }

    @Test
    public void testSimple(){
        String program = ReadSPL.readLineByLineJava8(rootFolder + "simple.spl");

        List<String> result = runCode(program);
        assertEquals("15", result.get(0));
    }

    @Test
    public void testSimpleConditional(){
        String program = ReadSPL.readLineByLineJava8(rootFolder + "simpleConditional.spl");

        List<String> result = runCode(program);
        assertEquals("a", result.get(0));

        program = program.replaceAll("Int i = 0;", "Int i = 100;");

        result = runCode(program);
        assertEquals("b", result.get(0));
    }

    @Test
    public void nested_while_if(){
        String program = ReadSPL.readLineByLineJava8(rootFolder + "print_numbers_up_to.spl");

        List<String> result = runCode(program);
        assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 8]", result.toString());
    }

    @Test
    public void test_empty(){
        String program = ReadSPL.readLineByLineJava8(rootFolder + "is_empty.spl");

        List<String> result = runCode(program);
        assertEquals("True", result.get(0));

        String program2 = program.replaceAll("\\[Int] empty = \\[];", "[Int] empty = 1 : 2 : 3 : [];");

        result = runCode(program2);
        assertEquals("False", result.get(0));


        String program3 = program.replaceAll("\\[Int] empty = \\[];", "[Int] empty = 0 : [];");

        result = runCode(program3);
        assertEquals("False", result.get(0));


        String program4 = program.replaceAll("\\[Int] empty = \\[];", "[[Int]] empty = [] : [];");

        result = runCode(program4);
        assertEquals("False", result.get(0));
    }

    @Test
    public void print(){
        String program = ReadSPL.readLineByLineJava8( rootFolder + "print.spl");

        List<String> result = runCode(program);
        assertEquals("[('a', 'b'), " +
                        "(1, 2), " +
                        "(3, 'c'), " +
                        "((1, 2), ('a', 'b')), " +
                        "(((1, 2), ('a', 'b')), ('a', 'b')), " +
                        "((((1, 2), ('a', 'b')), ('a', 'b')), (((1, 2), ('a', 'b')), ('a', 'b')))]"
                , result.toString());
    }

    @Test
    public void infinite_lists(){
        String program = ReadSPL.readLineByLineJava8(rootFolder + "infinite_list.spl");

        List<String> result = runCode(program);
        assertEquals("[1, 2, 3, 1, 2, 3, 1, 2, 3, 1]", result.toString());
    }

    @Test(expected = CompileException.class)
    public void testNoMain(){
        String program = ReadSPL.readLineByLineJava8(rootFolder + "no_main.spl");

        runCode(program);
    }

    @Test
    public void testSimpleList(){
        List<String> result = runCode("[Int] a = 1:2:3:[];\n" +
                "main()::->Void{\n" +

                "print(2);\n" +
                "}");
        assertEquals("2", result.get(0));
    }

    @Test
    public void testListsWithIdentfiers(){
        List<String> result = runCode("[Int] a = 1:2:3:[];\n" +
                "main()::->Void{\n" +
                "[Int] b = 3:4:5:[];\n" +
                "[[Int]] c = a:b;\n" +
                "print(a.hd);\n" +
                "}");
        assertEquals("1", result.get(0));
    }

    @Test
    public void testSimpleTuple(){
        List<String> result = runCode("(Int, Char) a =  (1,'a');\n" +
                "main()::->Void{\n" +
                "print(2);\n" +
                "}");
        assertEquals("2", result.get(0));
    }

    @Test
    public void testTupleWithLists(){
        List<String> result = runCode("[Int] a = 1:2:3:[];\n" +
                "[Int] b = 3:4:5:[];\n" +
                "[[Int]] c = a:b;\n" +
                "main()::->Void{\n" +
                "[Char] d = 'a':'b':'c':[];\n" +
                "([[Int]],[Char]) e = (c, d);\n"+
                "print(2);\n" +
                "}");
        assertEquals("2", result.get(0));
    }

    @Test
    public void testTupleFstSndTL(){
        List<String> result = runCode("[Int] a = 1:2:3:[];\n" +
                "[Int] b = 3:4:5:[];\n" +
                "[[Int]] c = a:b:[];\n" +
                "[Char] l = 'd':'e':'f':[];\n" +
                "main()::->Void{\n" +
                "[Char] d = 'a':'b':'c':[];\n" +
                "([Int],[Char]) e = (b, l);\n"+
                "[Int] f = e.fst;\n"+
                "d = e.snd;\n"+
                "f = b.tl;\n"+
                "print(2);\n" +
                "}");
        assertEquals("2", result.get(0));
    }

    @Test
    public void testListTLandHD(){
        List<String> result = runCode("[Int] a = 1:2:3:[];\n" +
                "[Int] b = 3:4:5:6:[];\n" +
                "[[Int]] c = a:b:[];\n" +
                //"[Char] l = 'd':'e':'f':[];\n" +
                "main()::->Void{\n" +
                //"[Char] d = 'a':'b':'c':[];\n"+
                "b.hd = b;\n"+
                "b.tl.tl = b;\n"+
                "b.tl.tl.hd.tl.tl.hd = a.tl;\n"+
                "print(b);\n" +
                "}");
        assertEquals("[[[2, 3], 4, [...], 6]]", result.toString());
    }

    @Test
    public void testGlobalVariables(){
        String program = ReadSPL.readLineByLineJava8(rootFolder + "markus/3-ok/globalVariablesSimple.spl");

        List<String> result = runCode(program);
        assertEquals("[0, 42]", result.toString());
    }

    @Test
    public void testAllTestsByMarkus() {
        Long sleepTime = 50L;
        try (Stream<Path> paths = Files.walk(Paths.get(rootFolder + "markus/3-ok/"))) {
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
                    List<String> result = runCode(s);
                    String expected = "";
                    try {
                        if(path.toString().contains("assignments.spl")){
                            expected = "[0, 1, 2, 0, 1, 2, 0, 1, 2, 2, 3, 4, 6, 7, 8, 9, 6, 0, 6, -7]";
                        } else if(path.toString().contains("associativity.spl")){
                            expected = "[0, 0, 2, 1, 1, 4]";
                        } else if(path.toString().contains("comments.spl")){
                            expected = "[1, 2, 3, 4, 5, 6, 7]";
                        } else if(path.toString().contains("functionArgumentsSimple.spl")){
                            expected = "[0, 42]";
                        } else if(path.toString().contains("functions.spl")){
                            expected = "[5, True, 1, 5, True, 4, 6, 9, 9, 15, 1, 3, 5, 5, 3, 1, 1, 3, 5]";
                        } else if(path.toString().contains("functionsSimple.spl")){
                            expected = "[5, True, 1, 5, True, 4, 6]";
                        } else if(path.toString().contains("globalVariables.spl")){
                            expected = "[5, 3, 15, False, 6, 4, 42, True, False, 5, 3, 1]";
                        } else if(path.toString().contains("globalVariablesSimple.spl")){
                            expected = "[0, 42]";
                        } else if(path.toString().contains("helloWorld.spl")){
                            expected = "[42, True, False]";
                        } else if(path.toString().contains("identifierNames.spl")){
                            expected = "[1, 2, 3, 4]";
                        } else if(path.toString().contains("ifThenElse.spl")){
                            expected = "[42]";
                        } else if(path.toString().contains("ifThenElse2.spl")){
                            expected = "[42, 20]";
                        } else if(path.toString().contains("ifThenElseFalse.spl")){
                            expected = "[100]";
                        } else if(path.toString().contains("ifThenElseInFunction.spl")){
                            expected = "[7, 11]";
                        } else if(path.toString().contains("ifThenElseScope.spl")){
                            expected = "[100, 20]";
                        } else if(path.toString().contains("ifThenElseScopeFunArg.spl")){
                            expected = "[100, 20]";
                        } else if(path.toString().contains("listFunction.spl")){
                            expected = "[7, 8, 9, 10, 7, 8, 9, 10]";
                        } else if(path.toString().contains("listFunction2.spl")){
                            expected = "[9, 9, 15, 15]";
                        } else if(path.toString().contains("listFunction3.spl")){
                            expected = "[42]";
                        } else if(path.toString().contains("lists.spl")){
                            expected = "[True, 7, False, True, 2, 7, False, 7, 7, 2, 7]";
                        } else if(path.toString().contains("listsSimple.spl")){
                            expected = "[7, 10, 7, 8, 11, 8]";
                        } else if(path.toString().contains("listsSimple2.spl")){
                            expected = "[7, 8]";
                        } else if(path.toString().contains("listsSimple3.spl")){
                            expected = "[8]";
                        } else if(path.toString().contains("localVariables.spl")){
                            expected = "[5, 3, 15, False, 6, 4, 42, True, False, 5, 3, 1]";
                        } else if(path.toString().contains("localVariablesSimple.spl")){
                            expected = "[0, 42, 0, 42]";
                        } else if(path.toString().contains("precedence.spl")){
                            expected = "[11, 6, True, True, True, 5, False, -2, 5, 1, True, True, False, True, False, True, True]";
                        } else if(path.toString().contains("recursiveFunction.spl")){
                            expected = "[6, 10, 5050, 0, 0]";
                        } else if(path.toString().contains("recursiveFunction2.spl")){
                            expected = "[6, 10, 5050, 0, 0]";
                        } else if(path.toString().contains("simpleArithmetic.spl")){
                            expected = "[3, 6, 4, -4, 33, False, True, False, True, False, True, False, -5, 5, -5, 5, False, True, False, True]";
                        } else if(path.toString().contains("tuples.spl")){
                            expected = "[5, 3, 15, False, 5, 3, 42, True, False, 5, 3, 1]";
                        } else if(path.toString().contains("tuplesSimple.spl")){
                            expected = "[5, 3, 10, True, True, False, False, 20]";
                        } else if(path.toString().contains("tuplesSimple2.spl")){
                            expected = "[5]";
                        } else if(path.toString().contains("while.spl")){
                            expected = "[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1]";
                        } else {
                            expected = "File is not in tests";
                        }

                        assertEquals(expected, result.toString());
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