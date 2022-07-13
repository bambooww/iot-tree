

# Versions



### version 0.95.0

Improve the maintenance of tags list and add new tags based on the existing list. Some regular settings can be completed quickly.




### version 0.95.1
Multilingual support for documents




### version 0.95.2
Improve the organization of help files inside the installation package. Through /doc, you can directly access the system's own documents, which support multiple languages.




### version 0.95.3
The embedded Tomcat supports upgrading to 9.0 X version. Avoid the bug that the system class cannot be found in JSP compilation in jdk17 environment




### version 0.95.4
The installation package with JDK version is added under Windows environment, and win_panel is provided at the same time.




### version 0.95.5
Add local tag support. Local tag can exist as a global variable under the project node and is not associated with the driver under the channel. And automatic saving can be set.



### Version 0.95.6 

Support for the new Modbus Slave device simulator and an example of the entire process of industrial automation field implementation have been added. Improve Modbus protocol-related support to resolve bugs that occasionally interrupt connections. 





### version 0.95.7
Add access rights plug-in support, and it contains a demo reference; Corresponding document [best practice document][ref_hmi_auth] added in this document




### version 0.95.8
Upgrade the running environment to openjdk-jre17. Modify the implementation of connector binder architecture and add OPC DA client Connector support.




### version 0.95.9
Use Mozilla Public License Version 2.0 (MPL). Add third-party plug-in license support.




### version 0.96.0
Improve the data access, processing and channel Association supporting message mode. And support mqtt and other specific protocols.



### version 0.96.1
Improve access support for message types. It also supports project management connection list, dynamic blinking message support, such as support for new device discovery notifications, link initialization errors, and so on. Prepare to add a new message monitoring window later.



### version 0.96.2
ConnPtMSG supports both JS Transfer and Binder. The Binder method is simple and clear, and it supports data location and acquisition quickly through pattern matching (xpath, jsonpath, etc.). JS Transfer supports flexible implementation and device discovery



### version 0.96.3
Implement OPC UA client Connector



### version 0.96.4
Implement OPC UA Server



### version 0.96.5
Some bug fixed under service model



### version 0.97.0
The HTTP URL connector supports HTML format data acquisition, page internal data positioning and extraction, and realizes page crawling and data aggregation based on Web pages.

The Device Library has been greatly improved. It can be managed by Library (import and export are supported). Each library has two levels of management: category and device.



### version 0.98.0
HMI Lib and Dev Lib have been greatly improved and optimized to allow projects to run independently of Library support. The resource-related content is optimized so that the resource can be copied into a reference (ref) object.

At present, the three different hierarchical relationships of HMILib - DevLib - Prj are basically stable, and the related library content can be planned later.




[ref_hmi_auth]:./case/case_ref_hmi_auth.md
