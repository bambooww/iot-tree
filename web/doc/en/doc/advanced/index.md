Extends Functions of IOT-Tree
==



If you already have a basic understanding of IOT-Tree Server. Here, if you want IOT-Tree Server to bring you greater value and convenience, it is necessary to deeply understand the following contents.


## 1 Plugin Development



The IOT-Tree Server itself provides a variety of IoT support functions. But in many situations, using these functions directly cannot meet your needs.

For example, if you want to add some special features or Api to JS scripts running within the system, these special features cannot be provided by IOT-Tree Server. A typical scenario is that your organization has a special set of message push channels, such as an email server or a mobile SMS interface. You would like to add this message push function to the JS tasks running internally in the IOT-Tree Server. So using the IOT-Tree Server plugin support mechanism can achieve your goal.

Another scenario is that your business system integrates IOT-Tree, and you want to directly integrate the monitoring UI that IOT-Tree can provide in your existing large management system. At this point, a question arises - you must unify the access permissions of the monitoring page of IOT-Tree with your existing system. IOT-Tree provides authority plugin development support to address this issue.


Please refer to: [Plugins Development][plugin],  [Plugins Development-JsApi][plug_js_api]  , [Plugins Development-Authority][plug_auth]

[plugin]:./adv_plugin.md
[plug_js_api]:./adv_plugin_jsapi.md
[plug_auth]:./adv_plugin_auth.md


## 2 Remote control from master station to sub station

TODO

## 3 Device Simulator

TODO
