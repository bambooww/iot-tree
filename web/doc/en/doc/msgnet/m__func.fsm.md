Module: Finite State Machine (FSM)
==



The finite state machine in IOT-Tree message flow is different from the state machine implementation in programs, and its operating mechanism should be fully understood when using it.

Each state machine is represented by a main module and can be associated with multiple state child nodes. There is a running thread inside the main module, which is shared by all child state nodes.


<img src="../img/msgnet/fsm01.png">



Among them, FSM unified input nodes can make the state machine a message forwarding switch.


## 1 State Node Description



All states corresponding to a state machine module can have at most one state node activated at a time.

Each state is a circle with one input port and four output ports. Among them, as long as there is a message input port, it will cause the state machine to enter this state - at this time, this state will have a circular mark.

The state node has four output ports, corresponding to "in" "run" "out" and "current status message forwarding", which will trigger message output in the following situations.


**in** port



when the state enters, this port will trigger message output - the message flow can handle some processing when entering this state through this port (such as initializing some variables, etc.).


**out** port



When the state exits (transitions to another state node), this port will trigger message output - the flow can handle some processing when exiting this state.


**run** port


When the state node is in an active state, this port will trigger messages at regular intervals. This time interval is determined by the parameters defined internally in the node.


**⟶** port



This port is used in conjunction with FSM "unified input" nodes and forward incoming messages.


## 2 Message forwarding



When a message is input to the "FSM unified input" node, the "⟶" port of the active state node of the state machine will output this message. Therefore, the state machine comes with a built-in message switching function.


<img src="../img/msgnet/fsm02.png">

