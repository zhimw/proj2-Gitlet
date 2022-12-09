# proj2-Gitlet

# Gitlet Design Document

**Name**: Zhimei Wang

## Classes and Data Structures

### Commit
#### Instance Variables
* Message - contains the message of a commit.
* Timestamp - time at which a commit was created. Assigned by the constructor.
* Parent - the parent commit of the commit object.
#### Methods
* getter methods for the message, timestamp, parent.

### Repository
To store gitlet commands.
#### Methods
* setupPersistence() - set up the file system 
* init() - starts a new repository and creates the .gitlet folder.
* add(fileName) - add the file with the fileName in the repository to the staging area. 
* commit(message) - saves a snapshot of tracked files in the current commit and staging area so they can be restored at a later time, creating a new commit.

### Class 3

#### Fields

1. Field 1
2. Field 2

## Algorithms

## Persistence
### File System
* CWD/ -- current working directory that has files that needs to be tracked change on
     - .gitlet/ -- the .gitlet folder for all persistent data in the CWD folder
     - HEAD -- the file that has the address of the head commit
     - Commits/ -- the folder that contains all the commits history
     - Staging/ -- the folder for data to be staged before a commit
          - Addition/ - the folder for addition, has blobs that needs to be added
          - Removal/ - the folder for removal, has blobs that needs to be removed
     - Branches/ -- the folder for all the branches, with the default branch to be master
          - master -- the file that stores the commits inside the master branch
