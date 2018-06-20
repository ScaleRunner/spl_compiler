package cli;

import org.apache.commons.cli.*;

public class CLI {

    private final String[] args;
    private final CommandLineParser parser;

    public CLI(String[] args) {
        this.args = args;
        this.parser = new DefaultParser();
    }

    private Options getOptions() {
        Options options = new Options();
        options.addRequiredOption("i", "input-file", true, "The SPL filepath");
        options.addOption("c", "compile-only", false, "Only compile the code, do not run it");
        options.addOption("r", "reformat", false, "Reformat SPL code and exit (WARNING: Removes comments)");
        options.addOption("v", "version", false, "Print the version of the compiler");
        options.addOption("p", "python", false, "Compile to Python instead of SSM");
        options.addOption("h", "help", false, "Show compiler usage");

        return options;
    }

    public CommandLine parse() throws ParseException {
        return parser.parse(getOptions(), args);
    }

    public void help() {
        HelpFormatter help = new HelpFormatter();
        help.printHelp("Compiler", getOptions());
    }

}
