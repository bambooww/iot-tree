IOTTree Link Connector - Tcp Server
==

## 1 Creating ConnProvider of Tcp Server


Precondition: The connected device or program provides an external interface in Tcp Client mode, and will actively initiate a connection request to the server side, and start relevant data transmission after the link is successfully established. A typical application scenario is to deploy an IOTTree Server in the cloud with a fixed IP address, and then listen to a certain port in the cloud to provide Tcp Server access support. There are multiple devices on the device end, distributed over a large area, and connected to the cloud through mobile GPRS/4G modules for communication.

In an IOT-Tree project, there may be multiple Tcp servers listening, with each Tcp server listening to a different IP address or port. Therefore, there can be multiple ConnProviders for Tcp Server and it will automatically manage Tcp Client connections to this IP and port. Each Tcp link accepted and established corresponds to a Connection Point(ConnPt).

Assuming that our IoT application is preparing to collect temperature and humidity data within an area and transmit it to the cloud through 4G communication, each device's hardware will collect the same data content (only with differences in location and identification). IOT-Tree is deployed on a cloud server with the address "data.mydomain.com" and is ready to listen on port 30000.

In the project, click on the "Connectors" menu, select "Tcp Server", and in the pop-up editing dialog, edit the corresponding "Connections Provider". Fill in the following information:

```
Name=s1
Title=S1
Local Port=30000
Conn Identity=Common
```
<img src="../img/conn/c008.png">


Among them, "Local port=30000" is the port we plan to listen on, while not selecting "Local IP" means binding all local addresses. Of course, if you want to limit a specific IP address, then choose it.

Tcp Server access also has a "Conn Identity" option, which includes two options: "Common" and "Fix IP".

"Common" is a general situation that requires the client to immediately send direct identification information after a successful connection. The reason for this is as follows: when a device successfully establishes a Tcp connection with the cloud TcpServer, the server (IOT-Tree Server) cannot know which device this connection comes from on site, so the on-site device must immediately send its own identification information after the connection is successfully established. (Of course, for safety reasons, we can also have the device provide verification information and perform multiple data interactions through Tcp connections for validity verification. This process is related to the specific implementation and will not be explained in this document.). Based on this identifier, the server automatically assigns this connection to a already configured ConnPt.

"Fix IP" is generally used in internal networks, and its requirement is that each connected device (client side) IP address is fixed, which means that these fixed IPs can represent the identification of a certain device. At this point, once a Tcp connection is established between the device client and the IOTTree server, their identity is determined and no additional verification is required.


## 2 Adding ConnPt



Each client connector will correspond to a ConnPt. Right click on the "S1" node we created above, select "Add Connection", and fill in the following information in the pop-up dialog:


```
Name=c1
Title=1# dev
Socket Conn Id=c0001 
```
<img src="../img/conn/c009.png">



Among them, the "Socket Conn Id" is the unique identifier for the configuration of the device. Once a Tcp connection is established between this device and the IOTTree Server, this identifier will be sent immediately. The Server determines which device is connected to and will assign it to this ConnPt.

We can compare it with <a href="link_tcpclient.md">Tcp Client Connector</a> and find that each Tcp Client ConnPt is initiated by IOT-Tree, connecting to the remote Tcp Server. The Tcp Server provided by IOT-Server is created by Tcp Server ConnProvider,which will obtain the remote device request and establish a Tcp connection, and then assign it to the corresponding "ConnPt" based on the identification ID. Afterwards, there was no difference in the Tcp link between them.

Obviously, we can also create ConnPt for other devices under the Tcp Server ConnProvider S1:


```
Name=c2
Title=2# dev
Socket Conn Id=c0002

Name=c3
Title=3# dev
Socket Conn Id=c0003
```



In this way, S1 (Tcp Server ConnProvider) allows remote links with three restricted identifiers below.


## 3 Associate to channel



In order to see how the Tcp Server connector we have established and the three ConnPts included will be used in the future, we will create three new channels "ch_dev1", "ch_dev2", and "ch_dev3" on the middle project organization tree, and associate the three ConnPts with them (please refer to other documentation for specific channel establishment). As shown in the following figure:


<img src="../img/conn/c010.png">



At this point, the device drivers that can be used by channels "ch_dev1", "ch_dev2", and "ch_dev3" will be limited by the Tcp link. Right click on "ch_dev1" and select the "Select Driver" option. In the pop-up device driver selection dialog, you can see the list of restricted drivers. As shown in the following figure:


<img src="../img/conn/c011.png">



The specific content of the subsequent device data organization configuration is related to the selected device driver. This document will not be discussed further. Please refer to other documents for this part of the content.

We can clearly state that the separation of communication connector and device drivers allows IOT-Tree to have more flexible support when dealing with communication and device protocols.

