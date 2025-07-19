Module: Conn In Msg Handler
==



One major type of connector in the IOT-Tree project is message based, and it is obvious that the input of messages can be directly used by the message flow. This node is the module associated with the project link in definition.

## 1 Module Parameters

**Conn In**

This parameter will automatically list the connector(msg type) defined in the project, just select it.

After setting the module parameters, two child nodes will be automatically generated: the "Recv Msg" and the "Send Msg"

## 2 Recv Msg

The Recv Msg sub node is a starting node with only one output connection point. When the module configuration connector receives a message, it will retrieve, preprocess, and output the message through this node.

### 2.1 Recv Msg's parameters

**Transfer to str**

After selecting this parameter, if the received message is inputted as a byte array, it will be automatically converted into a string based on the encoding

**JSON Format**

After selecting, the message will be automatically converted to JSON format and output

## 3 Send Msg

In contrast to Recv Msg, the Send Msg node is an end node that can send data content in the message flow, which will eventually be output to remote related devices or systems through connector.

Of course, if the connector only supports one-way data reception, then this node will not work.

