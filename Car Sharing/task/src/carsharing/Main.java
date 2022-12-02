package carsharing;

import carsharing.manager.DatabaseManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String databaseName = getDatabaseNameFromArgs(args);
        DatabaseManager databaseManager = new DatabaseManager(databaseName);
        databaseManager.run();
    }

    /**
     *
     * @param args
     * @return
     * Command Line handler for program. If -databaseFileName is added, the next
     * space divided string will be the argument for the database filename.
     */
    private static String getDatabaseNameFromArgs(String[] args) {
        List<String> arguments = List.of(args);
        String commandName = CommandLineArguments.DATABASE_NAME.getCommandName();

        if (arguments.contains(commandName)) {
            int index = arguments.indexOf(commandName) + 1;
            if (index < arguments.size()) {
                return args[index];
            }
        }
        return null;
    }
}