package cli;

import codeGeneration.python.CodeGenerator;
import lexer.Lexer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import parser.Parser;
import parser.declarations.Declaration;
import typechecker.Typechecker;
import util.CheckPython;
import util.PrettyPrinter;
import util.ReadSPL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class Runner {

    private final String path;
    private final boolean python;
    private final boolean compileOnly;
    private final boolean reformat;

    public Runner(CommandLine cmd) throws ParseException {
        this.python = cmd.hasOption("p");
        this.compileOnly = cmd.hasOption("c");
        this.reformat = cmd.hasOption("r");

        if(!cmd.hasOption("i")){
            throw new ParseException("An input file must be provided!");
        }

        String filepath = cmd.getOptionValue("i");
        if (filepath.endsWith(".spl")) {
            this.path = filepath.replace(".spl", "");
        } else {
            throw new InputMismatchException("The input file should be an SPL file.");
        }
    }

    public void execute() throws IOException, InterruptedException {
        String inputfile = path + ".spl";
        String outputfile = python ? path + ".py" : path + ".ssm";

        String program = ReadSPL.readLineByLineJava8(inputfile);

        Lexer l = new Lexer(program);
        Parser p = new Parser(l.tokenize());
        List<Declaration> nodes = p.parseSPL();
        if(reformat){
            PrettyPrinter.writeToFile(inputfile, nodes);
            return;
        }
        Typechecker tc = new Typechecker();
        tc.typecheck(nodes);

        if (python) {
            CodeGenerator codeGenerator = new codeGeneration.python.CodeGenerator(outputfile, tc.getEnvironment());
            codeGenerator.generateCode(nodes);
        } else {
            codeGeneration.ssm.CodeGenerator codeGenerator = new codeGeneration.ssm.CodeGenerator(outputfile);
            codeGenerator.generateCode(nodes, null);
        }

        if (!compileOnly) {
            ProcessBuilder processBuilder = python ? createPythonProcess(outputfile) : createSSMProcess(outputfile);
            runProcess(processBuilder);
        }
    }

    private void runProcess(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        final Process process = processBuilder.start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        process.waitFor();

        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    private ProcessBuilder createPythonProcess(String filename) {
        String pythonVersion = CheckPython.getPythonVersion();
        List<String> command = new ArrayList<>();
        command.add(pythonVersion);
        command.add(filename);
        ProcessBuilder builder = new ProcessBuilder(command);
        builder = builder.redirectErrorStream(true);
        return builder;
    }

    private ProcessBuilder createSSMProcess(String filename) {
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-jar");
        command.add("ssm.jar");
        command.add("--cli");
        command.add("--file");
        command.add(filename);
        return new ProcessBuilder(command);
    }
}