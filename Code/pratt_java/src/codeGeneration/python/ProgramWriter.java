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

    public void printLast(){
        String lastLine = this.program.remove(program.size());
        this.program.add(String.format("print(%s)", lastLine));
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
            out.println("if __name__ == '__main__':");
            out.println(indent + "main()");

        }
        out.close();
    }
}
