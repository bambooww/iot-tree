Node: Tags Writer
==


Write the Tag based on the selection and assignment method. Due to the potential involvement of underlying drivers and communication in tag writing,there may be some delay, so this node also supports asynchronous running



In addition to obtaining a certain data in the message as a Tag write value, in other cases, the node does not perform any analysis or processing on the input message. All write actions are determined by the Tag itself.

After the node runs, it will directly output the input message (in asynchronous cases, the output message will be delayed).


### Parameter settings

Double click to open the node parameter settings dialog box

#### Asynchronous Run



Check this option, and the Tags writing within the node will run asynchronously - that is, after receiving the input message, an internal running thread will be triggered, which can avoid the impact of long writing time on the real-time performance of the previous nodes.

<font color="red">Note: If asynchronous running is set, the node will ignore all input messages during the running process.</font>


#### Write tag list parameters

Multiple Tags writes can be set and executed in sequence during runtime. Each Tag write has the following parameter settings


1. Delay(MS)



If this delay setting is greater than 0, the corresponding number of milliseconds will be delayed before writing this Tag. This is very useful for some control situations, such as issuing a switch button command, writing a Tag value of 1 first, waiting for a few seconds, and then writing 0 to the same Tag, which can simulate a button control process.


2. Tag



Click on the Tag input box to pop up a selection dialog box. Select OK to proceed.


#### Write Value



Obtain the type based on the value, and then select or input the relevant second one. These parameters can come from message members, node variables, and flow variables, as well as from constants.


