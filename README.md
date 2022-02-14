What is IOT-Tree Server?
==
 <a href="./web/doc/cn/README.md" target="_blank">中文</a>
 <a href="./web/doc/jp/README.md" target="_blank">日本語</a>
 
IOT tree server is a service software system with Internet of things access, data normalization and human-computer interaction display.

She uses a clear architecture, including communication access, channel, device driver, data tag and other levels. The Internet of things system is managed and organized in a tree like manner in all aspects.

Based on this tree management, IOT tree server provides the design, configuration and online application of online configuration interface (Human-Computer Interaction Interface) based on Web.

IOT tree server is an open source software developed using Java. It's interface is completely web-based.

[Quick Start][quick_start]

[Quick Links][quick_link]

[Demo][demo_link]

[Download][dl_link]

# 1 What does IOT tree server do?




## 1.1 It can be used as an industrial field configuration software

You can use IOT-Tree server as the PC side configuration software in the industrial field.

Select the appropriate access mode according to the field communication situation, and select the appropriate drive and associated equipment. You can quickly make field projects orderly and clear at the computer management end. Then, you can quickly configure the human-computer interaction configuration interface according to the operating conditions.

If a device or sub station is complex, you can design the sub control interface first, and then reference it in the general control interface.

If a device is complex but has defined the interface elements it can provide, you only need to introduce the device, and the interface elements associated with the device can directly become the referenced content in your project.

Of course, according to our open source strategy, most of your projects can be used for free.


<img src="./web/doc/en/doc/img/prj3.png">




### Use Case
<div style="display:none">[Lamp Demo][lamp_demo] </div>

<div style="display:none">  [Pump Controller Demo][pump_demo] </div>

[Demonstration of Industrial Automation][case_auto_demo]

## 1.2 embedded into the device

If you have an embedded device with sufficient performance to run Java virtual machine, IOT tree server can easily make your device a controller. And the controller can support online configuration and UI design, and provide online real-time operation services.

[Embedded Demo][embed_ctrl_demo]  




## 1.3 As a IOT server

If you are an Internet company, you have just come into contact with the Internet of things project. Limited by the lack of experience in professional equipment access and data management, or you research and develop relevant equipment access protocols and data collection management from scratch. This process will take up a lot of your development resources and time at the same time.

You can try IOT tree server, just like database service, and make it a server supported behind your project.

If your access device happens to be supported by our built-in driver, IOT tree server is expected to give you a surprise.

According to our open source strategy, most of your projects can be used for free. A very important point is that over time, we will support more and more drivers. I hope IOT tree server can save you a lot of costs and get more benefits.



## 1.4 As a node of distributed applications

IOT tree server can be deployed as a shared node based on multiple communication modes by project. The local node is a complete automatic monitoring site that can run independently. Other IOT tree servers in the network can refer to this node in the project to become a part of themselves and become the upper level node of this node.

This distributed function can greatly facilitate the linkage of cross regional monitoring sites. For example, each monitoring site only needs broadband for secure communication with the cloud message queue server, which can easily form a hierarchical relationship. The top IOT tree server node system can monitor the communication link and use this communication state as the system communication exception to deal with multiple related functions.





## Know more

[Quick Start][quick_start]

[Quick Learn][quick_link]

[Demo][demo_link]

If you encounter problems with the system or have suggestions, you can send us email feedback: iottree@hotmail.com

[quick_start]: ./web/doc/en/doc/quick_start.md
[quick_link]: ./web/doc/en/doc/quick/index.md
[demo_link]: ./web/doc/en/doc/case/index.md
[dl_link]: https://github.com/bambooww/iot-tree/releases


[pump_demo]: ./web/doc/en/doc/case/example_psd.md
[lamp_demo]: ./web/doc/en/doc/case/example_lamp_demo.md
[embed_ctrl_demo]: ./web/doc/en/doc/case/example_embed.md

