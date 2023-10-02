

Connector-Channel-Driver
==




[IOT tree server tree] [qn_tree] arranges all devices and resources involved in a project vertically. With the channel as the center, the communication and drivers required for device operation are integrated horizontally.


```
               Project
                 |
Connector --> Channel (Driver)
                 |
            Device/Tag Group
                 |
               Tag Group
                 |
               Tag
```



In a hierarchical relationship, Channel is an abstract concept that can be seen in a sense as a classification of devices under a project. Each available channel may corresponds to a Connector.

If the Connector is Link mode, the channel may also need to set up corresponding drivers to unify the protocols and drivers required for the same access and device operation that the device may use.

Of course, if the channel's corresponding connector is a Bind or Message, then there may be no need for driver settings.




## 1 Device drive

At present, there are many standard communication protocols in the Internet of things, such as modbus, can... And so on. In order to support and expand more and more devices, IOT tree server specifically implements pluggable device driver support. We will have more and more drivers to support your needs in the future.




### 1.1 Device definition

In order to be more convenient to use. IOT tree server specifically sets the organization and management functions of device definitions, which are independent of the project., Equipment definitions are managed through two levels: classification and equipment.

The equipment defined below the channel needs to have a better understanding of the corresponding protocol of the equipment, which requires relevant professional knowledge. Similar to the traditional OPC server, professionals can complete the definition of devices by selecting device drivers, setting device parameters, setting labels and driving address parameters corresponding to labels.

Once the equipment definition is completed, the defined equipment can be directly referenced in the project to form the specific equipment in the project. These specific devices require very few or no subsequent parameter settings.

All device definitions must be under the driver. If a device supports multiple protocols and the IOT tree server has corresponding drivers for different protocols, a device may have multiple definitions in the IOT tree server, each defined under different drivers.


For more detailed device definitions, please click here: 
