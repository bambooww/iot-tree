Device,Device Library and Drivers
==


We have already been informed from the previous documents about the channel and device related regulations of IOT-Tree. Among them, the device driver is set by the channel, and multiple devices below the channel are also managed uniformly by this channel. This structure is in line with the actual deployment topology of automation systems and IoT systems. For example, a channel can correspond to a fieldbus, and the devices below can also correspond to the devices attached to the fieldbus.

Upon careful analysis, we can find that the devices beneath a channel are essentially inherent related properties and the resources it contains, such as descendant nodes and tags. In reality, we have used multiple devices of the same model from a certain manufacturer. After the configuration of IOT-Tree is completed, most of the content is the same (some differences are only related to device address properties and update times).  And the tag data below the device is also the same. If a device is relatively complex internally, we can also define device UI under this device, which can also be shared.

Therefore, IOT-Tree has introduced a separate "Device Library" for device management. This device library is independent of specific projects and has a three-level management mechanism of "Library-Category-Device" to meet the diverse device world.


## 1 Device Definition



To distinguish between the devices in the "Device Library" and the specific devices used in the project, we refer to the devices in the "Device Library" as "device definitions". The device definition is essentially similar to the device nodes in a project, and can be seen as a branch in the project tree. This device definition branch has its own tags, sub nodes, and even its own UI (device UI).


In IOT-Tree, device definitions have their own editing and management UI, please refer to[Device definition][defdev]。

[defdev]:./dev_def.md

## 2 Device Library



After login to IOT-Tree main management UI, the "Device Library" can manage all device libraries and internal device definitions of this deployment instance. Due to the support for import and export functions, you can easily import the device definitions accumulated in other projects in a new deployment environment.

Click on a "library item" and you can manage categories - add, modify, delete, etc. Under the category, you can add new devices, modify device names, or delete devices. For detailed information, please refer to [Device Library][dev_lib].

Among them, clicking the device editing button will pop up a new browser window, entering the editing UI of the device definition. This editing interface is also organized in a device tree, similar to the device configuration in the project.


Please refer to[Device definition][defdev]。

[dev_lib]:./dev_lib.md

## 3 Device Driver



Currently, various communications such as RS485 bus and Ethernet bus are supported in industrial sites. There are a large number of communication protocols, and device drivers are basically the implementation of these protocols, while also taking into account various abnormal situations in industrial sites and taking relevant countermeasures. It can be said that device drivers are the implementation of protocols and also the continuous improvement and polishing of feedback on the actual operation of devices.

In IOT-Tree, device driver is created based on connector links, messaging, and other modes to support diverse devices in industrial and IoT sites. These device drivers are set in the channels associated with the ConnPt. Device drivers are defined in the form of plugins, which can be easily implemented and added as needed. This chapter provides detailed explanations specifically for different device drivers.

Due to the fact that the devices in the project are located below the channel and the drivers are located inside the channel, the device definition can essentially be separated from the device drivers. Therefore, the device definition in IOT-Tree mainly refers to the data organization and management. Each data item(tag) establishes a relationship through its address and driver. We have achieved the lightest coupling and maintained maximum simplicity.



