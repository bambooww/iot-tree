Node: On Off
==

Determine whether the input message has passed or blocked based on certain conditions, and also support frequency reduction pass operations for high-frequency messages. This node is very suitable for issuing device control instructions - it can determine whether the conditions for issuing instructions are met, and also avoid the execution time of instructions being much longer than the system's judgment time, resulting in multiple instructions being issued in a short period of time. <font color="green">This node will not create or modify messages, working in the form of switches or valves.</font>

### Parameter settings

Double click to open the node parameter settings dialog box

#### Property



This parameter can be provided by msg, node variable, or flow variable as a benchmark parameter for conditional judgment.


#### Condition

1. Check Type



Judgment types: such as comparison, null, existence, and conformity to a certain type


2. Operated object



Determine the type based on the conditions, and then select or input the relevant second or third input parameters, which can come from message members, node variables, and process variables, or from constants.


#### Output minimum interval(MS)



This parameter is set to be greater than 0, indicating that if the time interval between two messages is less than this parameter, even if the condition is met, the subsequent message will not be passed.

