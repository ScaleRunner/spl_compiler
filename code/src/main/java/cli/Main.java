package cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static final String VERSION = "SPL Compiler v1.2.2";

    public static void main(String[] args) {

        CLI cli = new CLI(args);

        try {
            CommandLine cmd = cli.parse();
            if (cmd.hasOption("v")) {
                System.out.println(VERSION);
                return;
            }
            if (cmd.hasOption("h")) {
                cli.help();
                return;
            }
            Runner runner = new Runner(cmd);
            runner.execute();
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            cli.help();
        } catch (FileNotFoundException e) {
            System.err.println("The inputfile could not be found!");
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Execution halted abruptly!");
        } catch (IOException e) {
            System.err.println("The outputfile could not be created! Do you have the correct rights?\n");
            e.printStackTrace();
        }

    }
}
