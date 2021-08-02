
快速入门
====

[en]
 Quick Start
 ==
[/en]

# 运行条件

为了运行IOT-Tree,你需要有如下条件：

Java 运行环境版本在1.8以上，并且确保你的运行环境针对Java的环境变量如Path JAVA_HOME已经被设置好了。

例如：
那么windows环境下，您的Java安装目录为 c:\jdk1.8，请设置
```
set PATH=%PATH%;c:\jdk1.8\bin
set JAVA_HOME=c:\jdk1.8
```

在Linux环境下，请设置


[en]
# Prerequisites
To use IOT-Tree, you need to have:

Java >= 1.8 (Please make sure the environment path has been set)
[/en]

# 安装

下载压缩包，解压到你希望的目录下面即可

<a href="http://121.40.64.41/iottree/" target="_blank">下载地址</a>

[en]
# Installation

Download zip file,and unzip to directory you wanted.

<a href="http://121.40.64.41/iottree/" target="_blank">download address</a>
[/en]


# 配置

使用文本编辑器打开config.xml文件，配置里面的访问端口,默认值是9090，你可以修改成其他的端口号

# 启动

Windows下运行 iot-tree.bat

Linux下运行 iot-tree.sh

出现启动画面，并提示成功之后，可以开始使用

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

# 访问管理界面，修改管理员密码
打开浏览器，在地址栏中输入安装并启动IOT-Tree的主机地址。如下： 
```
http://localhost:9090
```

# Linux配置成开机自动启动
以上配置都确保成功之后，接下来您可以配置本服务开机自动启动。



