package cli;

import org.apache.commons.cli.*;

public class CLI {

    private final String[] args;
    private final CommandLineParser parser;
    private CommandLine cmd;

    public CLI(String[] args) {
        this.args = args;
        this.parser = new DefaultParser();
    }

    private Options getOptions() {
        Options options = new Options();
        options.addRequiredOption("i", "input-file", true, "The SPL filepath");
        options.addOption("p", "python", false, "Compile to Python instead of SSM");
        options.addOption("c", "compile-only", false, "Only compile the code, do not run it");

        return options;
    }

    public CommandLine parse() throws ParseException {
        return parser.parse(getOptions(), args);
    }

    public void help() {
        System.err.println("Arguments could not be parsed!");
        HelpFormatter help = new HelpFormatter();
        help.printHelp("Compiler", getOptions());
    }

}
