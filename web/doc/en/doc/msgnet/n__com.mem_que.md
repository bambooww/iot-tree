Node: Memory Queue
==


Memory queues provide an asynchronous message caching mechanism, with a queue and a processing thread inside this node. As long as there are messages in the queue, threads can retrieve and process them from the queue. If the queue is empty, the thread stops running and waits.

This node is a non reliable message caching mechanism, only used in message flow processing to address the possibility of slow running speed in most certain stages leading to significant time errors in the entire message flow. For example, a certain message source generates data with a high frequency, but the data is small. Through message flow processing, it ultimately needs to be written to a file or database. If every message triggers a file or database write, synchronization may in turn affect data collection, which is not allowed in situations where time accuracy is required. At this point, this node can be used and placed in front of the database write node. This can ensure the processing speed of the nodes in front of the process.

In addition, to ensure normal running, this node also provides more support according to the needs of different occasions, including the following:

1. Alarm length and maximum length: By setting these two parameters, memory abuse can be avoided. When the queue length exceeds the alarm length, an alarm will be generated. When the queue reaches its maximum length, subsequent messages are not allowed to enter and an error will be thrown directly. So, it is necessary to fully utilize and adjust these two parameters.

2. Message combination output support: If this feature is set, subsequent nodes can batch process data in the queue, which is very friendly for database writing. For relational databases, writing multiple records at once is much more efficient than writing a single record at a time.

3. Latest information priority mechanism: If the processed message requires high time accuracy and is allowed to be discarded. So this mechanism can be used to ensure memory security while also ensuring that the latest messages are processed. Multiple dropout strategies are also supported for old messages in the queue.(TODO)


<font color="red">


If the message you send must be delivered after processing, such as transaction data, you cannot rely solely on this node to help you solve the problem. What you should consider is that before sending after processing, the message must be saved, and only after local saving is successful can the front-end consider that the transaction data has been accepted. Then, push processing is carried out through the backend message flow, and there is a certain feedback mechanism to determine the delivery of the message before it can be marked or resent in the local record.

With the above ideas in mind, you can use external databases and message flows for overall design and implementation. Of course, you can also use this queue nodes for performance optimization.

The key point you need to ensure is that no matter how the system operates (including unexpected disasters), data cannot be lost and consistency cannot be lost.

</font>

