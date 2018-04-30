package codeGeneration;

import codeGeneration.writer.ProgramWriter;

import java.util.ArrayList;
import java.util.List;

public class Command {

    public final String command;
    public final List<String> args;

    public Command(String command, List<String> args){
        this.command = command;
        this.args = args;
    }

    public Command(String command, String arg){
        this(command, new ArrayList<String>() {{
            add(arg);
        }});
    }

    public Command(String command){
        this(command, new ArrayList<>());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String format = "%-" + ProgramWriter.LENGTH + "s";
        sb.append(String.format(format, command));
        for(String arg : args){
            sb.append(" ");
            sb.append(arg);
        }
        return sb.toString();
    }
}
