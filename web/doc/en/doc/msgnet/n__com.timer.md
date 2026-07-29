Node: Timer Trigger
==

This node is the trigger start message node in the process




There is a timer inside this node that can trigger new messages,based on certain strategies (such as time intervals)


### Parameter settings

Double click to open the node parameter settings dialog box

#### trigger once after xx mills second



If you check this option and fill in the delay milliseconds parameter. The node will delay this time before triggering the first message during startup.


#### Repeat trigger at interval XX mills second



You can set a fixed interval for subsequent repeated triggering messages. Messages can also be triggered at fixed time intervals within certain time periods based on options.


#### Frequency multiplication outputs



You can set any number of frequency multiplication outputs (the multiplication parameter must be an integer greater than 1). Nodes will automatically add output ports.

Note: All output ports share an internal timing thread, and different output ports have different running times in the future, with time accuracy affected by subsequent nodes.

