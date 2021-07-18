快速理解IOT-Tree Server
==

如果你想深入了解IOT-Tree Server，那么建议你阅读本部分内容，每个内容都不多，你只需要花费很少的时间就可以搞定。本部分内容掌握之后，你可以更好的利用IOT-Tree Server，让她给你带来更大的价值。

当然，如果本部分内容你都掌握，同时你还想深入，那么建议你查看[高级内容][advanced_link]

# 内容清单

## 1 快速理解IOT-Tree Server的树

本部分内容是理解IOT-Tree Server整体的关键部分。IOT-Tree Server建立的项目，某种意义上就是一颗树。树的根部是项目(Project)，接下来是通道(Channel)（通道可以关联设备驱动Device Driver和接入Connector）。通道下面是设备(Device)。

[查看详细][qn_tree]

## 2 快速理解 接入(Connector)-通道(Channel)-驱动(Driver)

在IOT-Tree Server树状层次结构基础上，围绕通道(Channel)，IOT-Tree Server对通信接入和设备驱动专门做了单独的子系统。

通过理解此部分的内容，你可以对IOT-Tree Server就会有个整体的认识。

[查看详细][qn_chconndrv]

## 3 快速理解设备定义Device Definition

在IOT-Tree Server中的设备都需要预先定义，这些定义都必须在某个已经存在的驱动下面。

通过预先定义的设备，不仅可以为你积累越来越多的设备，而且厂家还可也根据自身设备的复杂程度，通过设备定义来屏蔽复杂的驱动知识和专业的参数配置，让设备使用人员更加轻松。

[查看详细][qn_devdef]

## 4 快速理解TcpServer接入器

很多云端物联网系统都提供了TcpServer方式的接入，这种系统一般都可以支持大量的分散的底层设备或工业现场的接入。IOT-Tree Server专门针对此情景，设定了Tcp Server接入支持。

[查看详细][qn_conn_tcpserver]


## 5 快速了解交互UI(HMI)

IOT-Tree Server直接在树状层次管理结构中支持人机交互界面UI的定义、在线编辑和层次包含引用。

所有的UI都基于Web方式进行管理、编辑和部署。这是IOT-Tree Server给你带来巨大方便的重要部分。

最重要的，在设备定义(Device Definition)中，你也可以根据设备的情况定义和设备相关的交互UI组件。这样，这些设备管理的UI组件和设备一样，可以直接在项目中重复引用。

[查看详细][qn_hmi]

## 6 快速了解IOT-Tree Server作为你的后台支撑系统

和数据库服务软件类似，IOT-Tree Server也可以作为你的设备管理系统服务软件，作为你的IT基础设施的一部分。

[查看详细][qn_server]

[qn_tree]: ./quick_know_tree.md
[qn_chconndrv]: ./quick_know_ch_conn_drv.md
[qn_devdef]: ./quick_know_devdef.md
[qn_hmi]: ./quick_know_hmi.md
[qn_server]: ./quick_know_server.md
[qn_conn_tcpserver]: ./quick_know_tcpserver_connector.md

[advanced_link]: ../advanced/index.md

