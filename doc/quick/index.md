

Quick understanding IOT-Tree Server
==




If you want to know more about IOT tree server, you are recommended to read this section. There are not many contents, and you only need to spend a little time to do it. After mastering the contents of this part, you can make better use of IOT tree server to bring you greater value.

Of course, if you have mastered this part and you want to go deeper, it is recommended that you read [advanced content][advanced_link]




# List



## 1 Quickly understand the tree of IOT tree server

This part is a key part for understanding the whole IOT tree server. The project established by IOT tree server is a tree in a sense. The root of the tree is the project, followed by the channel (the channel can be associated with the device driver and the access connector). Below the channel is the device.

[detail][qn_tree]





## 2 Connector - Channel - Driver

Based on the tree hierarchy of IOT tree server, IOT tree server has made a separate subsystem for communication access and device driver around the channel.

By understanding the contents of this section, you can have an overall understanding of IOT tree server.

[detail][qn_chconndrv]




## 3 Quickly understand device definition

Devices in IOT-Tree server can be defined in advance. These definitions must be under an existing driver.

Through pre-defined device, you can not only accumulate more and more equipment for you, but also manufacturers can shield complex driving knowledge and professional parameter configuration through equipment definition according to the complexity of their own equipment, so as to make equipment users easier.

[detail][qn_devdef]



## 4 Quickly understand tcpserver accessor

Many cloud IOT systems provide tcpserver access, which can generally support a large number of decentralized underlying devices or industrial site access. The IOT tree server specifically sets TCP server access support for this scenario.

[detail][qn_conn_tcpserver]




## 5 Quick understanding of interactive UI (HMI)

IOT tree server directly supports the definition, online editing and reference of human-computer interface UI in the tree hierarchy management structure.

All UIs are managed, edited and deployed based on the web. This is an important part of IOT tree server that brings you great convenience.

Most importantly, in the device definition, you can also define interactive UI components related to the device according to the situation of the device. In this way, the UI components managed by these devices, like devices, can be directly referenced repeatedly in the project.

[detail][qn_hmi]

[Edit operating instructions][qn_hmi_edit]



## 6 Quickly understand IOT tree server as your background support system

Similar to database service software, IOT tree server can also be used as your device management system service software as part of your IT infrastructure.

[detail][qn_server]



## 7 Quickly learn about IOT tree server projects

With a quick understanding of the above contents, let's take the IOT tree server project as the end of the overall quick understanding.

[qn_tree]: ./quick_know_tree.md
[qn_chconndrv]: ./quick_know_ch_conn_drv.md
[qn_devdef]: ./quick_know_devdef.md
[qn_hmi]: ./quick_know_hmi.md
[qn_hmi_edit]: ./quick_know_hmi_edit.md
[qn_server]: ./quick_know_server.md
[qn_conn_tcpserver]: ./quick_know_tcpserver_connector.md

[advanced_link]: ../advanced/index.md

