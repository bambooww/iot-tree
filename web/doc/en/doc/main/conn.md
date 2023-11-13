IOT-Tree Connector
==



In IOT-Tree,Connector is the starting point of data source for each project. Whether your data source is a field device or other computer system, you need to configure the corresponding connector first.


## 1 Supported Connector Ways



IOT-Tree Server data source connector supports many kinds of ways, and it will provide more support with the continuous improvement. Currently, the supported connectors mainly includes serial port (COM), Tcp Client, Tcp Server, OPC DA, OPC UA, MQTT, HTTP URL, etc.


## 2 Three abstractions of connectors



IOT-Tree Server abstracts various connectors and forms three basic classifications: Link, Binder and Message. They correspond to the three main ways we obtain data.


### 2.1 Link



The link mode is mainly aimed at establishing a persistent two-way channel with the IOT-Tree Server for the connected source, and the data interacting on the channel needs to be supported by relevant drivers.

For example, the computer serial port (COM) and Tcp link will establish a relatively stable long link between the data source and IOT-Tree Server. Then, according to the requirements of the protocol required by the data source, the IOT-Tree Server uses a matching driver for processing. For example, Modbus protocol, or your private protocol.

The driver is generally configured on the channel associated with the link. Please refer to [Connector Channel Driver][conn_ch_drv] for details.

At present, traditional industrial field devices are connected to IOT-Tree Server through bus and Ethernet, which basically conform to Link mode.


### 2.2 Binder



Some data sources can directly provide structured data, and each data not only has a unique identifier (or path), but also carries content such as value data type, and this structure is relatively fixed. At this time, the connected data can be directly bound to the data structure definition under the associated channel (to establish a mapping relationship).

This mode generally supports OPC DA, OPC UA and other standard interfaces. OPC DA and OPC UA can generally be supported by the configuration software of the monitoring computer in the industrial field, or special software (such as the software PC Access corresponding to Siemens s7-200, and the general commercial software KepServer). Some high-performance PLCs can also directly support OPC standard interfaces, for example, Siemens s7-1200 can directly support OPC UA.


### 2.3 Message



If the data source comes with a content that can be processed independently each time, it will be abstracted into the message source mode. The message source mode can support two processing methods according to the characteristics of the message.

One is that the message format is relatively fixed, such as fixed json or xml format, so we may need to extract the data in a fixed location. At this point, the processing method can first locate the specific data in the path (such as jsonpath, xpath), and then establish a mapping relationship with the data under the associated channel through the binding method.

The other message format is that the message format is complex and unfixed or needs to be customized. At this time, IOT-Tree provides JS script processing support, which is the most flexible. It can not only extract the specific data set to the associated channel, but also has special support such as the discovery of new device.


[conn_ch_drv]: ./quick_know_ch_conn_drv.md
