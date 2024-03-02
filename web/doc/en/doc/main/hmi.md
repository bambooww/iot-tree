
Node:(HMI)
==



One exciting feature of IOT-Tree Server is its ability to provide online interactive UI definition, design, and publishing directly in your project.

Corresponding UI(HMI) nodes can be defined at each level of Project, Channel, Device, and Tag Group. And follow the node to include relevant resource access. For example, the UI Node under the device can reference the resources of the device and all nodes below it. The UI node in the root directory of the project can use all the resources of the entire project - this resource includes Sub-UI under sub nodes.

The UI (HMI) node supports online editing, and the draw items inside can use the relevant content in the library. In addition, the unique path of UI nodes in the project is the resource path in the external access URL during project runtime.

<img src="../img/hmi_prj_ch.png"/>



The UI (HMI) content is quite extensive, and a dedicated chapter is used in this document for detailed explanation.



For detailed information, please refer to[HMI][hmi_idx]


[hmi_idx]: ../hmi/index.md
