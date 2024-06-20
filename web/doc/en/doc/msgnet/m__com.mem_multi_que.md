Module: Memory Multi Queue
==


The memory multi queue module provides an asynchronous message caching mechanism, which has an internal processing thread. Multiple associated queues can be established from it during use, which share processing threads within the module. This module, combined with multiple queue sharing threads, can be used in situations where message processing time is high but the frequency of generated messages is not high. This can save system thread resources.

<font color="green">Corresponding memory queue nodes, each with its own processing thread</font>

This associated queue node is a non reliable message caching mechanism, only used in message flow processing to address the large time errors that may occur in the entire message flow due to the slow running speed of most certain links. For example, a certain message source generates data with a high frequency, but the data is small. Through message flow processing, it ultimately needs to be written to a file or database. If every message triggers a file or database write, synchronization may in turn affect data collection, which is not allowed in situations where time accuracy is required. At this point, this node can be used and placed in front of the database write node. This can ensure the processing speed of the nodes in front of the process.



