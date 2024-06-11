Node: switch
==

When there is an input message, determine which channel to output from based on certain conditions. <font color="green">This node does not create or modify messages and works as a switcher.</font>

### Parameter settings

Double click to open the node parameter settings dialog box

#### Property



This parameter can be provided by message msg, node variable, or flow variable as a benchmark parameter for judging different channels in the rules.


#### Condition Rules



You can set multiple setting rules, each corresponding to one output.

 Each rule can have the following parameters:



1. Check Type



Each rule can set expression judgment types, such as comparison, null judgment, judgment of existence, and compliance with a certain type


2. Operated object



Determine the type based on the conditions, and then select or input the relevant second or third input parameters, which can come from message members, node variables, and process variables, or from constants.


#### Otherwise



After selecting the "Otherwise" option, there will be a default output channel, which will output from this special channel when other rules do not meet the conditions.

