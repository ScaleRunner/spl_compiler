package cli;

import cli.CLI;
import cli.Runner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        CLI cli = new CLI(args);

        try {
            CommandLine cmd = cli.parse();
            if(cmd.hasOption("h")){
                cli.help();
                return;
            }
            Runner runner = new Runner(cmd);
            runner.execute();
        } catch (ParseException e) {
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
