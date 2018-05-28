package codeGeneration.python;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ProgramWriter {

    private final String filepath;
    private final List<String> program;

    // For keeping track of how many indents we have to do
    private String currIndent;

    // Token used for indentation
    private final String indent;

    public ProgramWriter(String filepath, String indent){
        this.filepath = filepath;
        this.program = new ArrayList<>();
        this.indent = indent;
        this.currIndent = "";
    }

    public void addToOutput(String line){
        this.program.add(line);
    }

    public void addIndent(){
        this.currIndent += this.indent;
    }

    public void removeIndent(){
        this.currIndent = this.currIndent.replace(this.indent, "");
    }

    public void writeToFile() throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filepath);
        for(String line : program){
            out.println(line);
        }
        out.close();
    }
}
