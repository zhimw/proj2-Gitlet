package gitlet;

import java.io.File;
import java.security.NoSuchAlgorithmException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     *  java gitlet.Main add hello.txt
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        // if args is not empty, proceed with the rest of the commands
        if (args.length > 0) {
            Repository newObject = new Repository();
            String firstArg = args[0];
            switch(firstArg) {
                case "init":
                    // do we need to validate number of args?
                    if (args.length > 1) {
                        System.out.println("No additional argument allowed for this command.");
                        break;
                    }
                    newObject.setupPersistence();
                    newObject.init();
                    break;
                case "add":
                    if (args.length > 2) {
                        System.out.println("The command only takes in 1 additional argument.");
                        break;
                    }
                    String fileName = args[1];
                    newObject.add(fileName);
                    break;
                case "commit":
                    if (args.length > 2) {
                        System.out.println("The command only takes in 1 additional argument.");
                        break;
                    }
                    String message = args[1];
                    newObject.commit(message);
                    break;

                    // TODO: fill in the rest of the commands

                default:
                    System.out.println(String.format("No command with that name exist."));
                    System.exit(0);
            }
            return;
        }
        // if args is empty, print message and exit
        else {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

    }

    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }
}
