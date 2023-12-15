HMI Client
==

你使用IOT-Tree的时候，可能会遇到这样一个典型需求：

1 IOT-Tree运行在一个工业自动化监控中心，这台计算机接入现场设备数据，并配置了相关的监控画面（HMI）；

2 这台计算机日常工作中持续运行，由工业现场相关工作人员使用；

3 你希望日常运行过程中，只要这台计算机启动，那么就会自动打开监控画面，并且充满整个屏幕；

4 监控画面启动时不需要输入密码；

为了支持以上需要，IOT-Tree在发布的release程序包中，专门有个client目录，里面提供了这个客户端HMI程序支持。

## 1 HMI Client runtime environment

当前实现的客户端只支持Windows系统，使用C# .net开发，内部使用微软的WebView2控件支持。

后续我们计划使用Chromium实现客户端，使之能够支持Linux系统，敬请期待。

## 2 IOT-Tree部署和权限设定

## 3 HMI Client Configuration


## 4 运行界面说明
