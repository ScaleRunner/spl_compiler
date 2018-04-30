package codeGeneration.writer;

import codeGeneration.Command;

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

    private final Map<String, List<Command>> branchMap;

    public ProgramWriter(String filepath){
        this.filepath = filepath;
        branchMap = new HashMap<>();
    }

    public void addToOutput(String branchName, Command command){
        List<Command> branchCommands = branchMap.get(branchName);
        if(branchCommands == null){ //This branch did not exist yet
            branchCommands = new ArrayList<>();
        }
        branchCommands.add(command);
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
     * @throws FileNotFoundException
     */
    public void writeToFile() throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filepath);

        List<Command> rootCommands = branchMap.remove("root");

        if(rootCommands != null){
            for(Command command : rootCommands){
                out.println(writeCommand("", command));
            }
        }

        List<Command> mainCommands = branchMap.remove("main");
        if(mainCommands != null)
            out.println(writeCommand("", new Command("bra", "main")));

        for(Map.Entry<String, List<Command>> entry : branchMap.entrySet()){
            String branchName = entry.getKey();
            List<Command> commands = entry.getValue();

            out.println(writeCommand(branchName, commands.get(0)));
            for(int i = 1; i < commands.size(); i ++){
                out.println(writeCommand("", commands.get(i)));
            }
        }

        if(mainCommands != null){
            out.println(writeCommand("main", mainCommands.get(0)));
            for(int i = 1; i < mainCommands.size(); i ++){
                out.println(writeCommand("", mainCommands.get(i)));
            }
        }

        out.close();
    }
}
