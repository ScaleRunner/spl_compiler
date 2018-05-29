package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CheckPythonVersion {
    public static String getPythonVersion(){
        try {
            List<String> command = new ArrayList<>();
            command.add("python3");
            command.add("--version");
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
            // If the code reaches this point, we can assume that the command is successful
            return "python3";

        } catch (IOException e) {
            return "python";
        }
    }
}
