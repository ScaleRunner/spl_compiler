package codeGeneration.python;

import codeGeneration.CompileException;

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

    public static boolean testProgram = false;

    public ProgramWriter(String filepath, String indent){
        this.filepath = filepath;
        this.program = new ArrayList<>();
        this.indent = indent;
        this.currIndent = "";
        this.currLine = "";
    }

    public void addToOutput(String line, boolean space, boolean EoL){
        if(line.contains("def") && this.program.size() != 0)
            this.program.add(""); // Blank line for visual pleasure
        this.currLine += space ? line + " " : line;
        if(EoL){
            this.program.add(this.currIndent + this.currLine);
            this.currLine = "";
        }
    }

    public void addToOutput(String line, boolean space){
        addToOutput(line, space, false);
    }

    public void addIndent(){
        this.currIndent += this.indent;
    }

    public void removeIndent(){
        this.currIndent = this.currIndent.replaceFirst(this.indent, "");
    }

    public void addGlobal(String globalVariable){
        for(int i = this.program.size()-1; i >= 0; i--){
            String line = this.program.get(i);
            if(line.contains("def ")){
                this.program.add(i+1, this.currIndent + "global " + globalVariable);
                break;
            }
        }
    }

    public void writeToFile() throws FileNotFoundException {
        boolean main = false;

        PrintWriter out = new PrintWriter(filepath);
        for(String line : program) {
            out.println(line);
            if (line.contains("def main"))
                main = true;
        }
        if(!main && !testProgram)
            throw new CompileException("Every SPL program needs a main function");

        if(!testProgram){
            out.println(""); // Insert blank line for visual pleasure
            out.println("if __name__ == '__main__':");
            out.println(indent + "main()");

        }
        out.close();
    }
}
