

Quickly understand IOT-Tree Server as your background support system
==




Like a database system, IOT-Tree Server can also be part of your IT infrastructure.

It is also illustrated by using examples.




# 1 Manage your device with IOT-Tree Server

When your IT infrastructure needs unified management of equipment, the access, channel and equipment functions of IOT tree server can greatly liberate your workload. Using IOT tree server, you can quickly organize various complex devices.

These devices are not necessarily the hardware devices and electrical circuits you can see, but also the software running for a 7x24 hour, or the running state of a server in a computer room. All these differences only need to have corresponding drivers.

After the device configuration is regular, IOT tree server uses a unified digital organization to standardize the overall data structure, and your other systems can easily use these data in the future.

This part is the basic function of IOT tree. You can refer to other relevant documents.



# 2 Use the HTTP restful interface to access the structured device data

IOT-Tree Server provides JSON data format composed of channel device tag group tag to external calls through web. Simple and clear API and data structure can directly provide real-time device operating conditions and other information for other IT software systems.




# 3 Use the store function to provide a database interface

IOT-Tree Server provides data storage functions of multiple policies based on its own tree hierarchy. You can efficiently classify and record the real-time or historical operation data of the device through the storage configuration of IOT-Tree Server according to your own database software.

We will constantly improve and add storage strategies, and can support a variety of relational databases or real-time databases.

Based on the storage configuration, your IT application can directly access the corresponding database system in the future. You can use traditional IT development methods to integrate the capabilities of IOT-Tree Server.




# 4 Integrate online customized UI inside IOT tree server

As you already know, IOT-Tree has a very powerful UI online definition tool. At the tree level, you can flexibly define the display UI interface or control UI interface required by your specific business. After deployment, you can also directly access these UI interfaces of IOT-Tree Server through a very intuitive URL and include them in your business system page. Of course, some pages can also be designed to the size of the mobile screen, so that your mobile app can integrate these UIs.




# 5 Deeper consolidation of IOT tree server

## 5.1 authority verification

If your application integrates the UI and other URL contents of IOT tree, you will certainly think about how the links corresponding to these UIs use the same permissions as your application system?

IOT tree server uses the permission plug-in configuration function. Your business system can provide a URL to accept permission query and configure it to IOT tree server for your project permission verification. When the IOT tree server outputs the UI of a URL, it will initiate a query permission request. Whether the current user can access a content is up to your application to control!

## 5.2 
