Node: Debug
==

This node is an auxiliary node that supports message viewing during flow debugging or runtime




This is an end node that can print and output a list of input messages. If it is a JSON structure, it can also view the specific internal content.

There is a switch configuration button on the right side of the node. If it is closed and saved to the server, the node will not push input messages to the UI interface when running on the server.

The message list is displayed in the area below the expansion of the node. There are two buttons above the display, "clear" and "start/stop", which correspond to clearing and pausing the display of new messages in the message list. Pausing the display of new messages allows you to have the opportunity to pause and view a certain message while in the high-frequency message incoming. Although the list is not updated at this time, the server will still push messages - our UI only temporarily ignores and does not display them.


### Parameter settings

Double click to open the node parameter settings dialog box

#### Buffer Num



You can set the maximum number of messages that this node can display when listing messages. This can avoid the browser memory being heavily occupied when displaying high-frequency message lists.


