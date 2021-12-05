


 Quick Start
 ==




# Prerequisites

In order to run IOT tree, you need the following conditions:

Java running environment version is above 1.8, and ensure that your running environment is specific to Java environment variables such as path Java_ Home has been set.

For example:

In Windows environment, your Java installation directory is C:\JDK1.8, please set

```
set PATH=%PATH%;c:\jdk1.8\bin
set JAVA_HOME=c:\jdk1.8
```

在Linux环境下，请设置



# Installation

Download zip file,and unzip to directory you wanted.

<a href="http://121.40.64.41/iottree/" target="_blank">download address</a>




# Modify configuration

Use a text editor to open the config.xml file and configure the access port in it. The default value is 9090. You can modify it to other port numbers



# Start

Run iot-tree.bat under Windows

Run iot-tree.sh under Linux

After the startup screen appears and prompts success, you can start to use it

```
_________ _______ _________           _________ _______  _______  _______
\__   __/(  ___  )\__   __/           \__   __/(  ____ )(  ____ \(  ____ \
   ) (   | (   ) |   ) (                 ) (   | (    )|| (    \/| (    \/
   | |   | |   | |   | |      _____      | |   | (____)|| (__    | (__
   | |   | |   | |   | |     (_____)     | |   |     __)|  __)   |  __)
   | |   | |   | |   | |                 | |   | (\ (   | (      | (
___) (___| (___) |   | |                 | |   | ) \ \__| (____/\| (____/\
\_______/(_______)   )_(                 )_(   |/   \__/(_______/(_______/   version xx.xx.xx

web port: 9090
iottree->
```


# Access the management ui and modify the administrator password

Open the browser and enter the host address to install and start IOT tree in the address bar. As follows:

```
http://localhost:9090
```


# boot automatically in linux

After the above configurations are successful, you can configure the service to start automatically.



