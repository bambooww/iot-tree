File Dir Read Write
==



Some applications require creating files, writing data, and closing files according to certain policies in a specific directory, forming a directory based data recording function. This module provides relevant support as a result.

The module itself needs to be limited to a certain file system directory and provides the following sub nodes:

## Open File In Dir

Open or create files in the directory according to the policy. Among them, this node can support subdirectories and file names based on time rules (triggered by message input), or provide file names through input messages

## Write To Current File
If the 'Open File In Dir' node above is successful, this node can input messages multiple times and perform file write operations according to the configured policy.

## Close Current File

After writing is complete, close the file operation.

