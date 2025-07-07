IOTTree的消息接入(Message) - HTTP Server
==

HTTP Server消息接入依赖IOT-Tree本身的对外Web端口，通过配置对应的接入，可以为外部数据提交提供Restful接口。也即是，通过提供特定的URL，外部设备/系统可以发送数据(Http POST)到此接入中，而此IOT-Tree接入被动的获取消息。

此接入方式对于大批量的设备数据收集有着很大的优势。

1，首先此方法非常简单轻量，和MQTT相比，不需要专门的MQTT Server。对外提供的URL由IOT-Tree对应的Web端口直接提供
2，获取的消息可以由消息处理映射、转换到相关的标签(Tag)中
3，对于数据提供方，实现消息推送也非常简单

下面还是以一个实际的例子，对此接入功能进行详细说明

# 1 新建一个HTTP Server URL接入



