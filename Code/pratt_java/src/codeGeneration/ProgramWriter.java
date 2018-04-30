package codeGeneration;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ProgramWriter {

    private final List<String> output;
    private final String filepath;


    public ProgramWriter(String filepath){
        this.output = new ArrayList<>();
        this.filepath = filepath;
    }

    public void addToOutput(String command){
        addToOutput("", command, "");
    }

    public void addToOutput(String command, String args){
        addToOutput("", command, args);
    }

    public void addToOutput(String branch, String command, String args){
        if(!branch.equals("")){
            branch = branch + ":";
        }
        String outputLine = String.format("%-10s %-10s %-10s", branch, command, args);
        output.add(outputLine);
    }

    public void writeToFile() throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filepath);
        for (String line : output) {
            out.println(line);
        }
        out.close();
    }
}
