package codeGeneration.python;

import codeGeneration.CompileException;
import util.CheckPython;
import util.ReadSPL;

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

    private final boolean splTypesInstalled;

    private final List<String> toAdd;

    public ProgramWriter(String filepath, String indent){
        this.filepath = filepath;
        this.program = new ArrayList<>();
        this.indent = indent;
        this.currIndent = "";
        this.currLine = "";
        this.splTypesInstalled = CheckPython.spl_types_installed();
        this.toAdd = new ArrayList<>();
    }

    public void addImport(String class_name){
        if(class_name.equals("Node")){
            if(this.splTypesInstalled)
                this.program.add(0, "from spl_types.lists import Node");
            else
                toAdd.add(ReadSPL.readLineByLineJava8("./src/main/python/spl_types/spl_types/lists.py"));
        } else if(class_name.equals("Tuple")){
            if(this.splTypesInstalled)
                this.program.add(0, "from spl_types.tuple import Tuple");
            else
                toAdd.add(ReadSPL.readLineByLineJava8("./src/main/python/spl_types/spl_types/tuple.py"));
        }
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

        if(this.toAdd.size() > 0) {
            System.err.println("WARNING: the python module spl_types is not found. Appending class files in the python output...");

            out.println("");
            out.println("######################");
            out.println("# SPL Custom Classes #");
            out.println("######################");
            out.println("");

            for(String classDef : this.toAdd){
                out.println(classDef);
            }

            out.println("");
            out.println("######################");
            out.println("#      SPL Code      #");
            out.println("######################");
            out.println("");
        }

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
