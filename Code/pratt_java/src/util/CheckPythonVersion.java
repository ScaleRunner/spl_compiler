package util;

import codeGeneration.CompileException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CheckPythonVersion {
    public static String getPythonVersion(){
        try {
            List<String> command = new ArrayList<>();
            command.add("python3");
            command.add("--version");
            ProcessBuilder builder = new ProcessBuilder(command);
            final Process process = builder.start();
            InputStream is = process.getErrorStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            process.waitFor();

            String line = br.readLine();
            return line == null ? "python3" : "python";

        } catch (IOException e) {
            throw new CompileException("Stream could not be opened/closedn\n" + e.getMessage());
        } catch (InterruptedException e) {
            throw new CompileException("Python stopped abruptly\n" + e.getMessage());
        }
    }
}
