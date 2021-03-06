
# 版本说明





### 版本 0.95.0

改善tags列表维护，增加基于现有的列表复制新增tag，一些有规律的设置可以快速完成。




### 版本 0.95.1

 文档的多语言支持




### 版本 0.95.2

 改进安装包内部自带的帮助文件组织。通过/doc就可以直接访问系统自带的文档，这些文档支持多语言。




### 版本 0.95.3

内嵌Tomcat支持升级到9.x版本。避免jdk17环境下jsp编译找不到System类的bug




### 版本 0.95.4
windows环境下面增加自带jdk版本的安装包，同时提供了win_panel功能




### 版本 0.95.5
新增Local Tag支持，Local Tag可以作为项目节点下面全局变量存在，和通道下面的驱动不关联。并且可以设置自动保存。



### 版本 0.95.6
新增 Modbus Slave 设备模拟器的支持，新增一个完整工业自动化现场的实施全过程例子。改进Modbus协议相关支持，解决连接偶尔中断的bug。




### 版本 0.95.7
新增访问权限插件支持，并且里面包含一个demo参考；本新增对应文档 [最佳实践文档][ref_hmi_auth]


### 版本 0.95.8
运行环境升级为openjdk-jre17。修改Connector Binder架构实现，新增OPC DA Client链接器支持。




### 版本 0.95.9
使用开源许可Mozilla Public License Version 2.0 (MPL)。 增加第三方插件许可支持。




### 版本 0.96.0
改进完善OPC-DA相关功能。新增支持消息方式的数据接入，处理和通道关联。并支持mqtt等具体协议。



### 版本 0.96.1
改进完善消息类型的接入支持。并支持项目管理Connection列表中，动态闪烁消息支持，如支持新设备发现通知，链接初始化错误等。后续准备新增消息监视窗口。



### 版本 0.96.2
ConnPtMSG方式同时支持JS Transfer和Binder。其中Binder方式使用简单明了，通过模式匹配（xpath、jsonpath等方式）快速支持数据的定位和获取；JS Transfer方式可以支持灵活的实现方式，可以支持设备的发现



### 版本 0.96.3
实现OPC UA接入支持



### 版本 0.96.4
实现OPC UA输出支持



### 版本 0.96.5
service 模型下的一些bug处理



### 版本 0.97.0
HTTP Url Connector支持HTML格式的数据获取和页面内部数据定位和提取，实现基于网页的页面爬虫和数据聚合功能。

设备库有了很大的完善提升，可以允许以库为单位进行管理（支持导入导出）。每个库有分类和分类下面的设备两级管理。




### 版本 0.98.0
HMI Lib、Dev Lib做了比较大的改善和优化，使得项目可以脱离库的支持而独立运行。并且优化了资源相关内容，使得资源可以被复制到引用(ref)对象中。
目前HMILib - DevLib - Prj 三个不同的层次关系已经基本稳定，后续可以开始对相关的库内容进行规划。





[ref_hmi_auth]:./case/case_ref_hmi_auth.md
