HMI and Dialog</a>
==

### HMI node and online editing



IOT-Tree specifically implements support for online monitoring HMI for IoT systems (including automated monitoring). It also defines HMI-type nodes specifically for this purpose in the project tree. These HMI-type nodes can use various configured resources in the context of the corresponding node container.

You only need to define or reference the relevant HMI nodes in your project to easily create rich and colorful monitoring page. It can be said that this HMI node and online editing function support are important components of IOT-Tree.

Please refer to the subsequent chapters for relevant information.


### Dialog-based data display function


In version 1.3, IOT-Tree began to support internal tag data recording, which raised the requirement of how to process and display these data. To this end, IOT-Tree provides a simple UI management function based on dialogs to ensure simplicity and clarity.
You only need to select tags and display templates on the management end to define your own dialog items (UI Items), which can then be used directly by end users in the monitoring page.

Due to the ever-changing display and use of data, it is impossible for us to meet all the needs of various users. Therefore, we only provide the above simple functions, which are expected to meet the needs of a large portion of users. In subsequent versions, we plan to implement a front-end UI plug-in development and management function: you only need to be familiar with html js related technologies to develop some UI plug-ins and deploy them to specific directories. Then you can select related RESTful resources in the dialog management and configure some specific UI items.

