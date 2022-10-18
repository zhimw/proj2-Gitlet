package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     *  java gitlet.Main add hello.txt
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {

            /** Creates a new Gitlet version-control system in the current directory. This system will automatically
             start with one commit: a commit that contains no files and has the commit message initial commit
             (just like that, with no punctuation). It will have a single branch: master, which initially points
             to this initial commit, and master will be the current branch. The timestamp for this initial commit
             will be 00:00:00 UTC, Thursday, 1 January 1970 in whatever format you choose for dates (this is
             called “The (Unix) Epoch”, represented internally by the time 0.) Since the initial commit in all
             repositories created by Gitlet will have exactly the same content, it follows that all repositories
             will automatically share this commit (they will all have the same UID) and all commits in all
             repositories will trace back to it. */
            case "init":
                // TODO: handle the `init` command
                // get the CWD
                File CWD = new File(System.getProperty("user.dir"));
                Commit initial = new Commit("initial commit", null);
                // branches? here we need to initialize master branch and have it point to initial commit
                //
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
        }
    }
}
