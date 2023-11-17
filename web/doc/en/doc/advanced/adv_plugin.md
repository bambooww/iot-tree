Plugins Development
==

## 1 The plugin mechanism of IOT-Tree Server



The IOT-Tree Server provides a variety of IoT support functions. But in many situations, using these functions directly cannot meet your needs.

For example, if you want to add some special features or Api to JS scripts running within the system, these special features cannot be provided by IOT-Tree Server. A typical scenario is that your organization has a special set of message push channels, such as an email server or a mobile SMS interface. You would like to add this message push function to the JS tasks running internally in the IOT-Tree Server. So using the IOT-Tree Server plugin support mechanism can achieve your goal.

Due to the fact that IOT-Tree Server is developed using the Java language, these plugins must also be developed using the Java language. Therefore, you must have some experience in Java language development.


### 1.1 Location of plugins in the system

The location of plugins is under the installation directory of the IOT-Tree Server, "$IOT_TREE/data/plugins/". Each plugin corresponds to a directory.

There must be a "config.json" file under each directory to explain the configuration information of this plugin. Meanwhile, as plugins may rely on some Java classes and library files (. jar). Therefore, there can also be  "classes/" and "lib/" directories internally. Among them, the classes file stores Java classes, while lib stores jar files.

Of course, if the plugin you implement itself requires more configuration files or resource files, it can also be stored under this directory. Then, it can be loaded or used in the Java class that implements the plugin.


<img src="../img/plug1.png">



The above is an example of a demonstration plugin that comes with the system after installing the IOT-Tree Server. You can view it in the directory where you installed it.


### 1.2 config.json



The config.json file itself is a standard JSONObject file format. Each plugin has a unique name, and each JSON object has "name" and "title" attributes.

Next, other attributes are set according to different plug-in types.


## 2 Supported plugin types



Each plugin directory must provide a config.json file, and we can define specific plugin support within this JSON object. It is obvious that you can define multiple types of plugin support in a plugin directory.

Currently, the IOT-Tree Server supports the following plugins.


### 2.1 Plugin - JsApi



The JsApi plugin can enable IOT-Tree Server server-side JS scripts to support more special APIs, which will make the system more tailored to your business needs. In [JS Script Usage][js_main], it is mentioned that in the script context running on the server side, there is a member name format of $$xxx (starting with two $ symbols), which is the support object provided by the Js plugin.


Please refer to:[Plugin - JsApi][plug_js_api]

[js_main]:../js/index.md

### 2.2 Plugin - Authority



The IOT-Tree Server provides various service interfaces externally, such as real-time JSON data URL interfaces under tree nodes, display and access of monitoring HMI UI, etc. In your business system, it may be necessary to embed it into your business pages on the browser side.

In addition, if the device operation UI you define may also require users to input and issue instructions, and your business system already has a set of users and permissions, how to make the IOT-Tree Server use your existing user and permission management mechanism uniformly. It can be implemented using the authority plugin.


Please refer to:[Plugin - Authority][plug_auth]

[plug_js_api]: ./adv_plugin_jsapi.md
[plug_auth]: ./adv_plugin_auth.md
