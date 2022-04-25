通过MQTT接入数据
==

IOT-Tree Server基本接入支持链路(Link)、绑定(Binder)和消息(Message)三种模式。他们各自有各自的特点。具体请参考 [IOT-Tree接入(Connector)][qk_conn]。

MQTT是一个典型的消息(Message)模式接入。MQTT本身的技术特点请自行参考相关文档，本文档认为您已经掌握了MQTT的相关技术。

MQTT通过订阅主题收到数据包之后，可以根据数据包中的内容格式，进行(Message)模式的处理——既可以通过绑定(bind)方式处理，也可以通过JS脚本进行处理。

# 1 


[qk_conn]: ../quick/quick_know_conn.md



