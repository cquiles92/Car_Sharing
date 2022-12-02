package carsharing;

/**
 * Enum of Command Line Args.
 */

public enum CommandLineArguments {
    DATABASE_NAME("-databaseFileName");
    private final String commandName;

    CommandLineArguments(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
