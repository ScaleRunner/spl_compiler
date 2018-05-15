package codeGeneration;

import parser.expressions.IdentifierExpression;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramWriter {

    // Number of characters that should be written in each column of the output
    public final static int LENGTH = 10;

    private final String filepath;

    private final List<String> branchNames;

    private final Map<String, List<Command>> branchMap;

    public static boolean testProgram;

    public ProgramWriter(String filepath){
        this.filepath = filepath;
        this.branchMap = new HashMap<>();
        this.branchNames = new ArrayList<>();
        testProgram = false;
    }

    public void addToOutput(String branchName, Command command){
        List<Command> branchCommands = branchMap.get(branchName);
        if(branchCommands == null){ //This branch did not exist yet
            branchCommands = new ArrayList<>();
            branchNames.add(branchName);
        }
        branchCommands.add(command);
        branchMap.put(branchName, branchCommands);
    }

    public void removeLastCommand(String branchName){
        List<Command> branchCommands = branchMap.get(branchName);
        if(branchCommands == null){ //This branch did not exist yet
            throw new CompileException("This branch did not exist yet", null);
        }
        branchCommands.remove(branchCommands.size()-1);
        branchMap.put(branchName, branchCommands);
    }

    private String writeCommand(String branchName, Command command){
        if(!branchName.equals("")){
            branchName = branchName + ":";
        }
        String format = "%-" + LENGTH + "s %s";
        return String.format(format, branchName, command);
    }

    /**
     * Gathers all commands with the same name and write them to the file.
     * They are printed in the order that they are created.
     * @throws FileNotFoundException
     */
    public void writeToFile() throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filepath);

        // Check if there is a main function
        if(!branchNames.contains("main") && !testProgram){
            throw new CompileException("An SPL program requires a main function.", new IdentifierExpression("The whole program"));
        }

        // You should always go to the main function after globals have been declared
        addToOutput("root", new Command("bra", "main"));

        // 'root' should always go first
        if(branchNames.indexOf("root") != 0){
            branchNames.remove("root");
            branchNames.add(0, "root");
        }

        // The main function should always end with the 'halt' instruction
        addToOutput("main", new Command("halt"));

        for(String branchName : branchNames){
            List<Command> commands = branchMap.remove(branchName);

            // We don't want the label 'root' to be printed
            branchName = branchName.equals("root")? "" : branchName;

            // Write the first command of a branch with the corresponding label
            out.println(writeCommand(branchName, commands.get(0)));

            for(int i = 1; i < commands.size(); i ++){
                out.println(writeCommand("", commands.get(i)));
            }
        }

        out.close();
    }
}
