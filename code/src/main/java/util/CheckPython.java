package util;

import codeGeneration.CompileException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CheckPython {
    public static String getPythonVersion(){
        if(pythonVersionExists("python3")){
            return "python3";
        } else if(pythonVersionExists("python")){
            return "python";
        } else {
            throw new CompileException("'python' or 'python3' is not found in your path");
        }
    }

    private static boolean pythonVersionExists(String python){
        try {
            List<String> command = new ArrayList<>();
            command.add(python);
            command.add("--version");
            ProcessBuilder builder = new ProcessBuilder(command);

            final Process process = builder.start();

            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            process.waitFor();
            String line = br.readLine();
            if(line.contains("Python 2")){
                System.err.println("Python 2.x was chosen as interpreter for running this compiler, which is not supported.");
            }
            return true;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public static boolean spl_types_installed(){
        String python = getPythonVersion();
        try {
            String[] command = {python, "-c", "from spl_types.lists import Node"};//"\"; print('succes')\""};
            final Process process = Runtime.getRuntime().exec(command);

            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            return stdError.readLine() == null;

        } catch (IOException | NullPointerException e) {
            return false;
        }
    }
}