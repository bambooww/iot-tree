IOT-Tree Device Driver - Simens Ethernet
==
IOT-Tree内置西门子以太网驱动，可以通过以太网Tcp方式，直接访问S7-300/1200/1500等多种PLC型号。本部分内容以网上的一个使用例子来说明。

​
## 1 设备和环境准备
本例子以S7-1500作为测试PLC，你如果没有相关设备，可以安装西门子的仿真环境，配合NetToPLCsim软件模拟。

STEP 7 Basic/Professional 和 WinCC Basic/Comfort/Advanced

NetToPLCsim

具体请参考西门子官网相关内容。

### 1.1 PLC端和比对测试软件准备

通过TIA Portal对PLC进行本地控制编程，其中PLC IP地址为：192.168.18.8，端口：102

给PLC通电，下载程序启动。

使用KEPServerEx6进行连接测试，确保PLC外部连接正常，我配置的点位信息如下：

<img src="../img/dev/d013.png">

​点击Quick Client按钮，弹出窗口查看对应节点下的数据项，确保全部都是"Good"

<img src="../img/dev/d014.png">

### 1.2 IOT-Tree Server安装配置

你的设备可以运行在PC端或嵌入式系统中，相关的安装配置文档可以参考[Quick Start][qk_start]

[qk_start]:../quick_start.md

 以上所有准备完成之后，接下来我们就可以使用IOT-Tree通过相关驱动对接了。

## 2 IOT-Tree 端配置过程

在IOT-Tree中，点击进入项目配置界面，项目内容如下：

<img src="../img/dev/d015.png">

### 2.1 新增TcpClient Connector和通道
点击左上角Connectors，在菜单中选择Tcp Client，然后在弹出的窗口中，填写如下内容

<img src="../img/dev/d016.png">

其中，主要内容就是PLC的IP地址和端口，点击Ok按钮，就可以看到左边新增了一个TcpClient接入。

接着我们在项目根节点鼠标右键，选择New Channel，在弹出窗口中，填写如下内容：

<img src="../img/dev/d017.png">

其中，Driver点击选择Siemens TCP/IP Ethernet。点击OK完成通道的添加。此时，你可以看到s7eth通道左边和其他通道不一样，左边并没有连接正方形框。这是因为基于TCP/IP的PLC每个设备都会需要自己的Tcp连接，所以和以总线方式的通道不同，此驱动下面的接入关联必须通过设备进行。因此，我们在通道下面先增加PLC设备。

### 2.2 新增PLC设备并关联TcpClient
在通道s7eth节点上鼠标右键，选择New Device，在弹出窗口中填写设备信息：

```
Name=plc3
Model=S7-1500
```
<img src="../img/dev/d018.png">

其他都不需要填写了，点击OK之后，通道下面就会出现对应设备节点。此时，你会发现设备左边有个圆形连接框。

接着，鼠标点击接入c18_8的右边的小正方形不松开，拖拉线段到设备plc3左边的小圆形上方松开，系统就会建立接入到设备之间的关联，如下图：

<img src="../img/dev/d019.png">

从中我们可以看出，如果左边接入使用不同的以太网接入方式，也可以和相关设备进行对接。一般情况下，plc端实现的是Tcp Server，只能等待Tcp Client的接入。我们可以使用中间代理节点，代理节点可以是个client主动连接IOT-Tree Server，并在另一端使用client方式连接PLC，这样我们就可以更灵活的支持复杂的网络环境；可以看出接入和数据组织的分离，使得IOT-Tree能够更加优雅的应对各种设备和数据的接入。

点击plc3节点，在右边主内容区域点击Properties标签，我们可以修改S7 Communication Parameters相关机架和槽位参数。如下图：

<img src="../img/dev/d020.png">

### 2.3 新增设备数据标签(Tag)
S7-1500内部数据通过不同的存储区方式进行，如输出映像寄存器区Q、输入映像寄存器I、DB存储区等等。IOT-Tree Server对应的驱动也兼容此PLC数据寻址方式。

在主内容区域点击\[Tags]标签,下面的内容就是plc3设备对应的数据项列表界面。我们可以点击上方的+Add Tag按钮进行添加。

在弹出窗口中，我们填写如下内容：
```
Name=d1
Title=d1
Date type=uint32
R/W=Read/Write
Address=DB200,DBD0
```

编辑窗口如图所示：

<img src="../img/dev/d021.png">

点击OK之后，就可以看到列表中新增了这一项。用同样方法，我们新增如下内容：

```
Name=d2
Title=d2
Date type=uint16
R/W=Read/Write
Address=DB200.DBW1

Name=d3
Title=d3
Date type=uint16
R/W=Read/Write
Address=DB200.W3

Name=i1_0
Title=I1.0
Date type=bool
R/W=Read Only
Address=I1.0

Name=i1_1
Title=I1.1
Date type=bool
R/W=Read/Write
Address=I1.1

Name=q0_1
Title=Q0.1
Data Type=bool
R/W=Read/Write
Address=QX0.1

Name=q0_3
Title=q0_3
Data Type=bool
R/W=Read/Write
Address=Q0.3

Name=qb2
Title=QB2
Data Type=uint8
R/W=Read/Write
Address=QB2

Name=x2_6
Title=x2_6
Data Type=bool
R/W=Read/Write
Address=DB200.X2.6
```

最终，我们在设备plc3下面，完成了如下数据项列表:

<img src="../img/dev/d022.png">

其中，关键内容是每个Tag的Address内容，这个写法兼容西门子的PLC编程软件。另外一个对应的是值类型（Value type），可以看出输入的Address可能会限定Value type。在编写时，可以点击Address右边的“Check Address"按钮，就会自动帮你修改。

我们配置的这些Tag会在IOT-Tree Server的这个项目中被使用，很明显，如果你想让上位系统和PLC内部的程序协调配合做控制，那么通过一些公共的变量定义成Tag进行互相写入读取即可。

仔细看这些数据项的定义，可以发现与OPC软件KEPServerEx很相似。实时上，IOT-Tree Server确实可以作为一个OPC软件，其不同之处就是多了更多的功能，如在线UI绘制、脚本任务运行、基于HTTP的JSON格式输出，以更方便与物联网应用。

接下来，我们就可以运行查看效果了。

## 3 运行效果
在确保PLC已经通电运行，点击项目配置上方的绿色启动项目按钮。

<img src="../img/dev/d023.png">

可以看到，所有的数据点都正常运行了。此时，我们可以配合KEPServerEx软件，进行写入数据并交叉查看数据变化。

如对于q0_1这个点，你可以在Write列输入1，点击右边写入按钮，可以发现PLC的Q0.1端口有输出（指示灯也同时变亮，前提是此输出没有受到你的PLC程序控制）；同时查看Quick Client，可以发现q0_1的值也跟着变化了，反过来也一样。如下图：

<img src="../img/dev/d024.png">

## 4 更进一步
你可以在此项目中，新增人机交互节点（HMI），并且通过在线编辑功能进行操作界面的设计。这部分内容请参考上面推荐的连接。如，在我的项目中，在，我实现了如下监控画面：

<img src="../img/dev/d025.png">

或者，你也可以把项目中的组织节点直接输出http json格式的数据，方便其他系统调用实时数据。如你在plc3节点上鼠标右键，点击Access，在弹出窗口中可以查看输出的json格式数据，非常方便其他系统调用：

<img src="../img/dev/d026.png">
