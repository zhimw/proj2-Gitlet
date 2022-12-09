package gitlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

// TODO: any imports you need here
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/** Represents a gitlet repository and creates a file system.
 *
 *  @author Zhimei Wang
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used.
     *
     * CWD/ -- current working directory that has files that needs to be tracked change on
     *  - .gitlet/ -- the .gitlet folder for all persistent data in the CWD folder
     *      - master -- the
     *      - HEAD -- the file that has the address of the head commit
     *      - Commits/ -- the folder that contains all the commits history
     *      - Staging/ -- the folder for data to be staged before a commit
     *          - Addition/ - the folder for addition, has blobs that needs to be added
     *          - Removal/ - the folder for removal, has blobs that needs to be removed
     *      - Branches/ -- the folder for all the branches, with the default branch to be master
     *
     *
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fileSystem */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File COMMITS = join(GITLET_DIR, "Commits");
    public static final File STAGING = join(GITLET_DIR, "Staging");
    public static final File BRANCHES = join(GITLET_DIR, "Branches");

    public static final File REMOVE = join(STAGING, "Removal");

    public void setupPersistence() {
        GITLET_DIR.mkdir();
        COMMITS.mkdir();
        STAGING.mkdir();
        BRANCHES.mkdir();
        try {
            HEAD.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Usage: java gitlet.Main init
     *
     * Description: Creates a new Gitlet version-control system in the current directory. This system will automatically
     * start with one commit: a commit that contains no files and has the commit message initial commit (just like that,
     * with no punctuation). It will have a single branch: master, which initially points to this initial commit, and
     * master will be the current branch. The timestamp for this initial commit will be 00:00:00 UTC, Thursday, 1 January
     * 1970 in whatever format you choose for dates (this is called 'The (Unix) Epoch', represented internally by the time
     * 0.) Since the initial commit in all repositories created by Gitlet will have exactly the same content, it follows
     * that all repositories will automatically share this commit (they will all have the same UID) and all commits in all
     * repositories will trace back to it.
     *
     * Runtime: Should be constant relative to any significant measure.
     *
     * Failure cases: If there is already a Gitlet version-control system in the current directory, it should abort.
     * It should NOT overwrite the existing system with a new one.
     * Should print the error message: A Gitlet version-control system already exists in the current directory.
     */
    public void init() throws NoSuchAlgorithmException {
        String prev = Utils.readContentsAsString(HEAD);
        if (prev.length() != 0) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
            return;
        }
        // create the initial commit and save it in the .gitlet/Commits folder as initial commit
        Commit initial = new Commit("initial commit", null);

        String commitSHA1 = getSHA1(initial);

        File commitFile = Utils.join(COMMITS, commitSHA1);
        Utils.writeObject(commitFile, initial);

        // create the master branch and save the initial commit file name and path as the content in the master branch
        File masterFile = Utils.join(BRANCHES, "master");
        String commit_name = commitFile.getName();
        Utils.writeContents(masterFile, commit_name);

        // save the master branch's address in the HEAD pointer
        String master_path = masterFile.getPath();
        Utils.writeContents(HEAD, master_path);


        // TODO: What is UID
    }

    /**
     *Private helper method to get the SHA-1 string of an object.
     */
    private String getSHA1(Object obj) throws NoSuchAlgorithmException {
        // convert the object into byte array
        byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            bytes = bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // use MessageDigest to digest the byte array and convert it into SHA1 byte array, and convert the SHA1 byte array into string
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(bytes);
        byte[] hash = md.digest();
        String res = "";
        for(byte b : hash ) {
            res += String.format("%02x", b);
        }
        return res;
    }

    /**
     * Usage: java gitlet.Main add [file name]
     *
     * Description: Adds a copy of the file as it currently exists to the staging area (see the description of the commit
     * command). For this reason, adding a file is also called staging the file for addition. Staging an already-staged
     * file overwrites the previous entry in the staging area with the new contents. The staging area should be somewhere
     * in .gitlet. If the current working version of the file is identical to the version in the current commit, do not
     * stage it to be added, and remove it from the staging area if it is already there (as can happen when a file is
     * changed, added, and then changed back to it’s original version). The file will no longer be staged for removal
     * (see gitlet rm), if it was at the time of the command.
     *
     * Runtime: In the worst case, should run in linear time relative to the size of the file being added and lgN, for N
     * the number of files in the commit.
     *
     * Failure cases: If the file does not exist, print the error message File does not exist. and exit without changing anything.
     */
    public void add(String fileName) throws NoSuchAlgorithmException {

        File inFile = Utils.join(CWD, fileName);
        // if the file exist in the CWD, proceed with the next steps
        if (inFile.exists()) {
            // read the file from the CWD and get the SHA1 string of the file content
            String inFileContent = Utils.readContentsAsString(inFile);
            String inFileSHA1 = getSHA1(inFileContent);

            // check if the same file is added to the staging area, if so doesn't do anything
            File outFile = join(STAGING, "Addition/"+ fileName);
            outFile.getParentFile().mkdir();

            // check if a file name with the same SHA1 string exist in the Commits folder, if it's there we don't do anything
            // and remove the file from the staging area if the file is there
            File checkExistFile = Utils.join(COMMITS, inFileSHA1);
            if (checkExistFile.exists()) {
                if (outFile.exists()) {
                    outFile.delete();
                    return;
                }
            }
            // if in the Commits folder there's no file has the same SHA1 string name, then create the file and add it to the staging area
            else {
                try {
                    outFile.createNewFile();
                    Utils.writeContents(outFile, inFileContent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // if the file with the file name does not exist, print out the error message and return
        else {
            System.out.println("File does not exist.");
            System.exit(0);
            return;
        }

    }




    /**
     * Usage: java gitlet.Main commit [message]
     *
     * Description: Saves a snapshot of tracked files in the current commit and staging area so they can be restored at
     * a later time, creating a new commit. The commit is said to be tracking the saved files. By default, each commit’s
     * snapshot of files will be exactly the same as its parent commit’s snapshot of files; it will keep versions of files
     * exactly as they are, and not update them. A commit will only update the contents of files it is tracking that have
     * been staged for addition at the time of commit, in which case the commit will now include the version of the file
     * that was staged instead of the version it got from its parent. A commit will save and start tracking any files that
     * were staged for addition but weren’t tracked by its parent. Finally, files tracked in the current commit may be
     * untracked in the new commit as a result being staged for removal by the rm command (below).
     *
     * The bottom line: By default a commit has the same file contents as its parent. Files staged for addition and removal
     * are the updates to the commit. Of course, the date (and likely the mesage) will also different from the parent.
     *
     * Some additional points about commit:
     *      The staging area is cleared after a commit.
     *
     *      The commit command never adds, changes, or removes files in the working directory (other than those in the
     *      .gitletdirectory). The rm command will remove such files, as well as staging them for removal, so that they
     *      will be untracked after a commit.
     *
     *      Any changes made to files after staging for addition or removal are ignored by the commit command, which only
     *      modifies the contents of the .gitlet directory. For example, if you remove a tracked file using the Unix rm
     *      command (rather than Gitlet’s command of the same name), it has no effect on the next commit, which will still
     *      contain the (now deleted) version of the file.
     *
     *      After the commit command, the new commit is added as a new node in the commit tree.
     *
     *      The commit just made becomes the "current commit", and the head pointer now points to it. The previous head
     *      commit is this commit’s parent commit.
     *
     *      Each commit should contain the date and time it was made.
     *
     *      Each commit has a log message associated with it that describes the changes to the files in the commit. This
     *      is specified by the user. The entire message should take up only one entry in the array args that is passed
     *      to main. To include multiword messages, you’ll have to surround them in quotes.
     *
     *      Each commit is identified by its SHA-1 id, which must include the file (blob) references of its files, parent
     *      reference, log message, and commit time.
     *
     * Runtime: Runtime should be constant with respect to any measure of number of commits. Runtime must be no worse than
     * linear with respect to the total size of files the commit is tracking. Additionally, this command has a memory
     * requirement: Committing must increase the size of the .gitlet directory by no more than the total size of the files
     * staged for addition at the time of commit, not including additional metadata. This means don’t store redundant copies
     * of versions of files that a commit receives from its parent (hint: remember that blobs are content addressable and
     * use the SHA1 to your advantage). You are allowed to save whole additional copies of files; don’t worry about only
     * saving diffs, or anything like that.
     *
     * Failure cases: If no files have been staged, abort. Print the message No changes added to the commit. Every commit
     * must have a non-blank message. If it doesn’t, print the error message Please enter a commit message. It is not a
     * failure for tracked files to be missing from the working directory or changed in the working directory. Just ignore
     * everything outside the .gitlet directory entirely.
     */
    public void commit(String message) {
        // read from my computer the head commit object and the staging area


        // clone the HEAD commit
        // modify its message and timestamp according to the user input
        // use the staging area in order to modify the files tracked by the new commit

        // write back any new object made or any modified objects read earlier

    }

}
