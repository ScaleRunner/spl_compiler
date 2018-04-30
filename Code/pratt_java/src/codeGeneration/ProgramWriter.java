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
        output.add(command);
    }

    public void writeToFile() throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filepath);
        for (String line : output) {
            out.println(line);
        }
        out.close();
    }
}
