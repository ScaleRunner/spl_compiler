package codeGeneration.python;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ProgramWriter {

    private final String filepath;
    private final List<String> program;
    private String currLine;

    // For keeping track of how many indents we have to do
    private String currIndent;

    // Token used for indentation
    private final String indent;

    public ProgramWriter(String filepath, String indent){
        this.filepath = filepath;
        this.program = new ArrayList<>();
        this.indent = indent;
        this.currIndent = "";
        this.currLine = "";
    }

    public void addToOutput(String line, boolean EoL){
        this.currLine += line + " ";
        if(EoL){
            this.program.add(this.currIndent + this.currLine);
            this.currLine = "";
        }
    }

    public void addIndent(){
        this.currIndent += this.indent;
    }

    public void removeIndent(){
        this.currIndent = this.currIndent.replace(this.indent, "");
    }

    public void printLast(){
        String lastLine = this.program.remove(program.size());
        this.program.add(String.format("print(%s)", lastLine));
    }

    public void writeToFile() throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filepath);
        for(String line : program){
            out.println(line);
        }
        out.close();
    }
}
