# Versions

### version 1.8.0 (Plan)


Starting from version 1.8, IOT Tree will not support JDK8 and 32-bit systems


### version 1.7.6


Msg flow: Complete relevant documents with a detailed and comprehensive explanation; The panel now has support for runtime customization, such as manually triggering nodes that can be input and then sent commands in the panel. Added PID control nodes and heating furnace simulator, and added PID Demo project.

Added HTTP Server URL connector, which can passively provide push notifications for receiving JSON messages.

Added microphone voice input node and voice recognition node, which can support text conversion of voice commands and provide basic support for voice control.

Improve the Omron Eth PLC driver and pass the CP2E Nxxx series PLC test.

Some bug handling.


### version 1.7.5


Implemented OPC UA client access again using milo1.0 library.

Tag management supports import and export to facilitate the quick creation and migration of a large number of tag points.

Some bug handling.



### version 1.7.1


The basic nodes of the message flow are basically complete, including timed triggers, memory queues, JS functions, templates, path switching, and system internal Tags reading and writing. Exec node tested parameter modification in Linux environment - it has been basically improved.

The message flow node supports more debugging information and asynchronous node operation control.

Added Modbus Slave emulator module (Pro ver).

Node parameter support library storage and usage.

Handling project import bugs in Linux environment.


### version 1.7.0


Message flow/network support has been added, which can replace all functions such as task scheduling control, data output storage, data routing, etc. In future versions of IOT-Tree, the root part of the tree will mainly focus on channels, drivers, devices, and tags; Regarding data usage, control logic, and external system integration, message flow is the main focus.

Please refer to:

<a href="msgnet/index.md" >Message Flow</a>

<a href="msgnet/mf_for_ctrl.md" >Using message flow to implement control logic</a>


### version 1.6.1


Add a main demo webapp. This webapp can provide you with reference for developing your own Java webapp, and then deploying it to IOT-Tree. In this way, you can customize the system's main page in IOT-Tree, such as adding a user page with navigation functions, which can also include your own applications.


<a href="advanced/adv_self_app.md" target="main">&nbsp;&nbsp;&nbsp;Custom Project Management (Webapp)</a>

### version 1.6.0


The middle tag adds support for writing values: triggering the corresponding JS script upon writing, enabling specific data updates. This support facilitates the integration with external systems by simplifying the issuance of control commands.

The basic framework of Router Out has been preliminarily implemented, enabling IOT-Tree to support more upper-level or external system data sharing based on messaging.

The tag list supports moving multiple selections to another container node.

The dictionary function has been improved, allowing tree organization and tags to support the selection or input of extended properties--this enables JSON-based data sharing to include extended properties content, with each properties name prefixed with "ext_".

Various optimizations have been implemented, including multilingual support for some pages.


### version 1.5.1


Fixed a bug in node localization caused by internal tag duplication in different projects, limiting node searches to only be conducted within the project

Adjusting alarm type multilingual XML support, improving multilingual support for some pages

New Omron PLC Hostlink FINS Serial device driver added



### version 1.5.0


Add MQTT-based deployment instance (IOTTree Node) data sharing and connector support. This provides preliminary distributed support for IOTTree, enabling hierarchical relationships between remote sub-stations and the center.



### version 1.4.0


Added multi-language xml resource file support, first configuration to add language selection. Revised multi-language support for some pages.

handle some bugs



### version 1.3.0


The new tag Time Segment Series Recorder (TSSR) time series data fast recording function is added. Support tag high-speed data change record under driving conditions. This record limits the number of data record entries, and supports memory cache and permanent storage.

Realize the overall framework of the TSSR based record processor, which can support configuration selection for secondary processing of some tag data. In addition, more processors can be implemented through continuous accumulation, which provides a basis for improving data analysis capability in the future.

Realize unified management support for business/data display UI dialog boxes. You can display output on the front end through configuration. For example, based on the above TSSR data record and processor, their are all can has UI dialog resources. You can configure to use them in the project.

Handling some bugs



### version 1.0.5


SQLite Native load java.io.tmpdir may has no access right bug fixed



### version 1.0.4


Add the Show Tags attribute to the HMI node of the project management end, so that the corresponding front-end UI supports Tag table data display configuration. If the Tag has historical data supported by the Store, it can pop up to display historical data;
Add pop-up window editing support for project node attribute values, which can support more diverse input of attribute values, such as tag selectors, colors, etc.

Handling some bugs


### version 1.0.3


Alert management object synchronization creation bug resolution
New project internal SQLite data source support
New AlertHandler internal data source and outer data source automatic recording function added



### version 1.0.2


Add device driver category and use a tree selection dialog box.
Tag editing has added data-driven limitations and improved check functionality.
Add some device drivers for Mitsubishi PLC.
Other optimization improvements


### version 1.0.1


CmdLineHandler Driver support JS impl
Alert Handler to out limit bug fixed
Modbus Driver Block Size bug fixed


### version 1.0.0


Added support for real-time and historical data writing to the database.

Improved JS editing prompt, added internal plugin support for $$http

The document is basically complete



### version 0.99.9


Alert function added, achieving flexible and intuitive alarm configuration support through a three-layer structure of alarm source, alarm handling, and alarm output.

Add and organize documents.


### version 0.99.8


Adapt and adjust the demo project based on the improvement and standardization of Client JS.
Improve plugin related functions.
Add and organize documents.


### version 0.99.7


Adapt and adjust the demo project based on the improvement and standardization of Server JS.
Added and organized documents for connector and device management instructions.

### version 0.99.6


Connectors has added the import and export function in Xml format, which facilitates the sharing of related configurations between projects.

Optimization and adjustment have been made to open Api for JS context, and annotation can be completed using JsDef.

Added and organized a document for the overall project description.


### version 0.99.5


Reorganized the entire document directory structure.Added some documents.


### version 0.99.4


The basic implementation of JS context is complete, and the editing interface has added support for context member tree directories.




### version 0.99.3
Tag adds anti-interference filtering support, and uses the average filtering method of anti pulse interference。Improved some documents.



### version 0.99.2
some bug has be fixed
WebSocket connector added




### version 0.99.1
DrawItem supports left-right and up-down mirror conversion, which greatly reduces the drawing effort of the hmi library.

Modify dynamic properties of component primitives without updating bugs

Implement some optimizations

Components support mapping internal DrawItem properties directly to component properties





### version 0.99.0
Connector supports direct device associations to support more flexible Ethernet device configurations;
New Siemens S7 Series (S7-300/1200/1500) PLC driver and support.
Some connection performance improvements were made.



### version 0.98.2

Improve HMISub related functionality and resolve associated bugs. New win_client based on WebView2 added. Add PPI protocol supports Siemens PLC S7-200.



### version 0.98.1
Improve HMI UI editing function; Added HMI Comp base library element. Some bugs have been fixed.



### version 0.98.0
HMI Lib and Dev Lib have been greatly improved and optimized to allow projects to run independently of Library support. The resource-related content is optimized so that the resource can be copied into a reference (ref) object.

At present, the three different hierarchical relationships of HMILib - DevLib - Prj are basically stable, and the related library content can be planned later.



### version 0.97.0
The HTTP URL connector supports HTML format data acquisition, page internal data positioning and extraction, and realizes page crawling and data aggregation based on Web pages.

The Device Library has been greatly improved. It can be managed by Library (import and export are supported). Each library has two levels of management: category and device.





### version 0.96.5
Some bug fixed under service model



### version 0.96.4
Implement OPC UA Server



### version 0.96.3
Implement OPC UA client Connector



### version 0.96.2
ConnPtMSG supports both JS Transfer and Binder. The Binder method is simple and clear, and it supports data location and acquisition quickly through pattern matching (xpath, jsonpath, etc.). JS Transfer supports flexible implementation and device discovery



### version 0.96.1
Improve access support for message types. It also supports project management connection list, dynamic blinking message support, such as support for new device discovery notifications, link initialization errors, and so on. Prepare to add a new message monitoring window later.



### version 0.96.0
Improve the data access, processing and channel Association supporting message mode. And support mqtt and other specific protocols.




### version 0.95.9
Use Mozilla Public License Version 2.0 (MPL). Add third-party plug-in license support.




### version 0.95.8
Upgrade the running environment to openjdk-jre17. Modify the implementation of connector binder architecture and add OPC DA client Connector support.



### version 0.95.7
Add access rights plug-in support, and it contains a demo reference; Corresponding document [best practice document][ref_hmi_auth] added in this document



### Version 0.95.6 

Support for the new Modbus Slave device simulator and an example of the entire process of industrial automation field implementation have been added. Improve Modbus protocol-related support to resolve bugs that occasionally interrupt connections. 





### version 0.95.5
Add local tag support. Local tag can exist as a global variable under the project node and is not associated with the driver under the channel. And automatic saving can be set.




### version 0.95.4
The installation package with JDK version is added under Windows environment, and win_panel is provided at the same time.





### version 0.95.3
The embedded Tomcat supports upgrading to 9.0 X version. Avoid the bug that the system class cannot be found in JSP compilation in jdk17 environment





### version 0.95.2
Improve the organization of help files inside the installation package. Through /doc, you can directly access the system's own documents, which support multiple languages.





### version 0.95.1
Multilingual support for documents






### version 0.95.0

Improve the maintenance of tags list and add new tags based on the existing list. Some regular settings can be completed quickly.





[ref_hmi_auth]:./case/case_ref_hmi_auth.md
