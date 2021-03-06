



IOT-Tree Server 是什么
==





IOT-Tree Server是一个物联网接入、数据规整、人机交互展示的一个服务软件系统。

她使用一套清晰明了的架构，内部包含通信接入、通道、设备驱动、数据标签等多个层次进行构建。使得物联网系统在各个方面统一以树状方式进行管理和组织。

在此树状管理基础之上，IOT-Tree Server提供了基于Web方式的在线组态界面（人机交互界面）的设计、配置和在线应用。

IOT-Tree Server是使用Java开发的开源软件。其管理界面完全基于Web方式。







[快速开始][quick_start]

[快速入门][quick_link]

[案例演示][demo_link]

<a href="https://github.com/bambooww/iot-tree/releases" target="_blank">下载<a>








# 1 IOT-Tree Server能干什么





## 1.1 能够作为一个工业现场组态软件

您可以使用IOT-Tree Server作为工业现场的PC端组态软件。

通过现场通信情况选择合适的接入方式，并且选择合适的驱动及关联设备。您可以迅速地使得现场项目在计算机管理端变有序和清晰。接着，您可以根据运行工况快速配置人机交互组态界面。

如果某个设备或子站比较复杂，您可以先设计子的控制界面，然后在总的控制界面进行引用。

如果一个设备比较复杂，但已经定义好了自身能够提供的界面元素，那么你只需要引入此设备，设备关联的界面元素可以直接成为您项目中被引用的内容。







<img src="./doc/img/prj3.png">


### 参考案例

<div style="display:none">[灯光控制][lamp_demo] </div>

[一个工业控制现场][case_auto_demo]








## 1.2 系统能够嵌入设备成为一个控制器

如果您有个嵌入式设备，性能足以运行Java虚拟机，那么IOT-Tree Server可以方便的使得您的设备成为一个控制器。并且这个控制器能够支持在线配置和UI的设计，同时提供在线实时运行服务。

[嵌入式控制器例子][embed_ctrl_demo]  




## 1.3 系统可以作为云端专用物联网server

如果您是个互联网公司，刚刚接触物联网项目。受限于对专业设备接入、数据管理经验的缺乏，或者您从头开始研究开发相关设备接入协议和数据收集管理。这个过程会同时占用您大量的开发资源和时间。

你可以尝试IOT-Tree Server，就像数据库服务一样，让她成为你的项目后面支撑的一个服务器。

如果您的接入设备恰好能够被我们内部自带的驱动支持，那么IOT-Tree Server估计会给你一个惊喜。

按照我们的开源策略，您的大部分项目都可以免费使用。很重要的一点是，随着时间的推移，我们支持的驱动肯定会越来越多，希望IOT-Tree Server能给你节省大量的成本，并且获得更多的收益。





## 1.4 系统可以成为分布式应用的一个节点

IOT-Tree Server可以以项目为单位，基于多种通信方式部署成一个共享节点。节点本地是一个可以独立运行的完整自动化监控现场。而网络中的其他IOT-Tree Server，在项目中可以引用此节点成为自身的一部分，成为此节点的上一级节点。

此分布式功能可以极大的方便跨区域监控现场的联动。如每个监控现场只需要有宽带，对接云端消息队列服务器进行安全的通信，就可以方便的形成上下级关系，顶端IOT-Tree Server节点系统可以对通信链路进行监视，并可以利用此通信状态作为系统通信异常来多相关应对的功能。





## 其他

如果您想深入了解，请参考相关资料。

[快速开始][quick_start]

[快速入门][quick_link]

[案例演示][demo_link]

如果你使用系统碰到问题或者有好的建议，可以给我们发邮件反馈：iottree@hotmail.com





[quick_start]: ./doc/quick_start.md
[quick_link]: ./doc/quick/index.md
[demo_link]: ./doc/case/index.md

[dl_link]: https://github.com/bambooww/iot-tree/releases

[pump_demo]: ./doc/case/example_psd.md
[case_auto_demo]: ./doc/case/case_auto.md
[lamp_demo]: ./doc/case/example_lamp_demo.md

[embed_ctrl_demo]: ./doc/case/example_embed.md
