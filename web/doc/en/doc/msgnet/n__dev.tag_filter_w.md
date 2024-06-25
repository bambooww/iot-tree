Node: Tags Write Filter
==


<cn></cn><en></en>




This node supports writing Tags, and the specific tag to be written is determined by the input message. Therefore, this node requires that the input message payload must contain JSON data in a specific format.

In node settings, it is possible to limit which Tags are allowed to be written by this node.

Due to the potential involvement of underlying drivers and communication in tag writing,there may be some delay, so this node also supports asynchronous running.



### Parameter settings

Double click to open the node parameter settings dialog box

#### Asynchronous Run



Check this option, and the Tags writing within the node will run asynchronously - that is, after receiving the input message, an internal running thread will be triggered, which can avoid the impact of long writing time on the real-time performance of the previous nodes.

<font color="red">Note: If asynchronous running is set, the node will ignore all input messages during the running process.</font>


#### Write Tags



Select the relevant input Tags to indicate the allowed write Tags for this node.


### Input Message Payload Format

1. Write single Tag command format

```
"payload":{
    "tag":"ch1.gg1.tag11",
    "value":true,
    "cmd_ts":12312445345,"cmd_to":20000
}
```

"cmd":"write_tag" Represents writing a single Tag

"cmd_ts":123124453 Represents the timestamp when this cmd was created

"cmd_to":20000  Represents the expiration time (in milliseconds) of this cmd. If the current time and the timestamp of the cmd being created exceed this expiration time, the message will not be processed

"tag":"ch1.gg1.tag11" Tag path

"value":true  Value to be written

2. Write multi Tags command format

```
"payload":{
    "cmd":"write_tags","cmd_ts":12312445345,"cmd_to":20000,
    "tags":[
        {"delay":0,   "tag":"ch1.gg1.tag11","value":true},
        {"delay":2000,"tag":"ch1.gg1.tag11","value":false}
    ]
}
```

"cmd":"write_tags" Represents writing multi Tags

"cmd_ts":123124453 Represents the timestamp when this cmd was created

"cmd_to":20000  Represents the expiration time (in milliseconds) of this cmd. If the current time and the timestamp of the cmd being created exceed this expiration time, the message will not be processed

"tags":[] Write tags content in specific order,each write tag has the following content:

　　"delay":2000  Delay before writing in milliseconds

　　"tag":"ch1.gg1.tag11" Tag path

　　"value":true  Value to be written
