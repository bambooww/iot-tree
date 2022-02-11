


Case of industrial automation system
==



This case is a complete application process in the industrial field. If you have relevant working experience, you can quickly master IOT-Tree Server as a tool for your work in the industrial field. 







# 1 site and control requirements 

## 1.1 site facilities and control requirements 






One of the subsystems in the industrial field has a water storage tank. There is a pump behind the water inlet. The water storage tank is controlled by controlling the start and stop of the pump. The water tank is 5 meters high, so water level needs to be monitored and controlled to ensure that the water level is kept within a certain range. 

The outlet docks with the next subsystem of the industrial process. In addition, an agent is added to the water flow rate at the outlet, which is provided by another independent automation device to control the application of the agent through the opening of the electric valve. 

The control requirements are as follows: 

(1) When the water level is below the low value of 1.1 meters, the pump must start the input water, and when the water level is above 4.5 meters, the pump must stop to prevent water overflow. By controlling the water level within a certain range, the output water can be guaranteed uninterrupted. 

(2) Use the reference scale tables of water flow velocity and opening to control the opening of the valve on the pipeline for dispensing medicines according to the flow rate of the outlet. (Note: The outlet flow rate is controlled by the next subsystem, which is indeterminate for this system) 



<img src="../img/case_auto1.png"/>




## 1.2 Automation-related devices 

### 1.2.1 Pump Control Related devices 




On-site water pumps can be started and stopped by simply interrupting the power supply through the contactor, cooperating with intermediate relay and feedback contacts, and finally starting and stopping control by two 24VDC relay coils. A coil power-on triggers the start of the pump, and a coil power-on triggers the stop of the pump. At the same time, the operation status of the pump is feedback through a passive contact. 

In the control box, a 24VDC switch I/O module is configured, providing RS485 Modbus RTU interface, device address 11, do0 connection to start coil, do1 connection to stop coil. DI0 connects to passive feedback contacts. The register addresses corresponding to do0, do1, and DI0 are 000001,000002,100001, respectively. 

In addition, the on-site control box has two trigger buttons (start button and stop button) and a pump operation status indicator. Used to support field personnel for manual intervention for emergency control. 





### 1.2.2 Valve opening control and water level monitoring equipment 

Put a 0-5 meter water level meter inside the water tank, supply 24VDC power and output 4-20mA current signal. Install an electric valve on the dosing pipeline with opening control via 4-20mA input control. 

In the field control box, configure a multi-channel analogue input and output module, provide RS485 Modbus RTU interface, device address 12, AIN0 (register address 300001, corresponding to 4-20mA value of 2000-10000) to receive the current signal of water level meter, DA0 (register address 400001, corresponding to 4-20mA value of 2000-10000) to add the control signal of electric valve to the medicine. 







### 1.2.3 outlet electromagnetic flowmeter 

An electromagnetic flow meter is installed at the water tank outlet and its transmitter also provides an RS485 Modbus RTU interface to read instantaneous and accumulated flow. Device address 13, flow rate in cubic meters per second, 4 bytes floating point number with register 404113 as high bit and 404114 as low bit. 





### 1.2.3 Upper control computer or embedded controller 

The above devices are connected to the upper system through a twisted shielded cable as RS485 bus, and all the device serial parameters are [9600 N 8 1]. The upper system can be either an industrial computer or an embedded ARM controller with IOT-Tree Server installed and configured internally. 

RS485 bus is connected to the upper controller and corresponds to the serial port COM5 inside the software. Of course, you can also configure a serial server module for RS485 to Ethernet. This enables IOT-Tree Server to transparently access the RS485 bus via Tcp. IOT-Tree Server has 192.168.0.18 device addresses for field use of intranet. Serial server address 192.168.0.10, port 12345. 



# 2 上位系统IOT-Tree Server作为子系统控制器配置

在前面的相关设备安装、接线成功之后，接下来主要工作就集中在控制器的IOT-Tree的配置上了。在工控机或嵌入控制器安装IOT-Tree Server请参考相关文档。本次案例IOT-Tree Server对外提供9090 http协议端口。

如果你在IOT-Tree Server本地工控机上，只需要打开浏览器，访问http://localhost:9090/admin 地址就可以登录进入IOT-Tree Server的管理、配置和控制界面。如果你在远程计算机上，则可以访问http://192.168.0.18:9090/admin 进行访问。

## 2.1 新建项目

登录IOT-Tree Server管理界面之后，在Local Projects中点击Add按钮，新增项目，如下图：

<img src="../img/case_auto2.png">

项目名称和标题如下：
```
watertank
Water tank and Medicament dosing
```
成功之后，在Local Projects列表中，就会出现新增加的项目。点击此项目，即可进入项目详细配置界面。

## 2.2 新增Connector

如果你运行IOT-Tree Server的设备直接通过串口连接现场RS485总线，则应该选择Connector - COM。如果通过以太网Tcp转RS485的串口服务器连接，那么应该选择Connector - Tcp Client方式。

他们分别对应的输入如下图：

<img src="../img/case_auto3.png">

以下内容，都以Tcp Client方式进行进行推进。接下来我们就要在树形Browser里面新增Channel-Device两个层次的内容了。

## 2.3 新增通道和设备

Browser下面已经有个项目根节点"watertank"，鼠标右键在弹出的菜单中选择"New Channel"，在弹出的对话框中，输入或选择如下内容。确定之后，"watertank"下面新增了这个通道。
```
Name = ch1
Title = channel1
Driver = Modbus RTU
```

<img src="../img/case_auto4.png">

在通道下面，我们就可以添加设备了。在RS485总线上的设备有3个：开关量模块、模拟量模块和流量计。我们分别取名为dio、aio、flow。

在新增的通道节点鼠标右键选择"New Device"，在弹出对话框中，只需要填写如下内容，其中里面的Device选项保留空（原因是当前的设备并没有存入设备库）：
```
Name = dio
```
<img src="../img/case_auto5.png">

通过相同方式我们只需要填写一个设备Name，新增另外两个设备aio、flow。

设备添加完成之后，通道下面就有了这3个设备节点，此时在主内容区域，点击"Properties"标签页，并且选择点击设备节点dio。您可以看到Modbus RTU设备详细的设置参数列表显示其中。由于dio设备的地址是11，我们只需要修改"Modbus Device Address"这一项的内容改为11，并且点击右上角的"Apply"按钮即可。如下图：

<img src="../img/case_auto6.png">

您接着点击另外两个设备aio、flowmeter节点，并且修改"Modbus Device Address"对应的参数分别为12、13。注意：不要忘记点击"Apply"按钮生效！

### 2.3.1 关联Connector和通道

在Tcp Client下发的c1链接或COM的c2链接右边，有个小正方形框，鼠标移到上方会改变颜色，按下左键并移动，会出现连接线。把连接线拉伸到通道ch1左边的小正方形框，松开左键，即可完成链接到通道的关联，如下图：

<img src="../img/case_auto6.1.png">

## 2.4 配置设备Tags

### 2.4.1 水泵控制开关量模块对应Tags

现在，我们应设置设备内部关联的数据了。在主内容区域点击"[Tags]"标签，然后点击设备节点"dio"，在标签下面的内容区域显示路径"/watertank/ch1/dio",此时列表区域没有任何Tag数据。

鼠标点击"+Add Tag"按钮，在弹出的对话框输入如下内容：
```
Name = pstart
Title = pump start do0
Data type = bool
R/W = Read/Write
Address = 000001
```
此Tags对应水泵启动线圈输出do0，如下图：
<img src="../img/case_auto7.png">

我们用同样的操作新增另外两个Tag，分别对应水泵停止线圈，水泵运行状态反馈无源触点。
```
Name = pstop
Title = pump stop do1
Data type = bool
R/W = Read/Write
Address = 000002
```
```
Name = p_running
Title = pump running state di0
Data type = bool
R/W = Read Only
Address = 100001
```
最终，在设备节点"dio"下面，有3个Tags，如下图：

<img src="../img/case_auto8.png">

### 2.4.2 水位和阀门控制模拟量Tags

和水泵控制类似，我们针对设备aio，配置阀门输入输出模拟量tag，和水位只读模拟量tag
```
Name = valve_da0
Title = valve_da0
Data type = int16
R/W = Read/Write
Address = 400001
```
```
Name = wl_ain0
Title = wl_ain0
Data type = int16
R/W = Read Only
Address = 300001
```
上面两个点是模拟量模块输入输出的原始值，取值范围为2000-10000。我们需要把水位值转换为0-5.0范围内的浮点数，阀门开度转换为0-100的整数。为此，我们新增了wl_val和valve_val两个tag，并且设置里面的transfer参数。

对于wl_val标签，tag主界面和之前原始值对应的Tag类似，点击Transfer输入框，在弹出的界面中选择Scaling选项，然后再Data type选择float。Scaling Type选择Linear。Raw Value Range中，High=10000、Low=2000。Scaled Value Range中，High=5、Low=0。如下图：

<img src="../img/case_auto9.png">

对于valve_val标签，tag主界面和之前原始值对应的Tag类似，点击Transfer输入框，在弹出的界面中选择Scaling选项，然后再Data type选择int16。Scaling Type选择Linear。Raw Value Range中，High=10000、Low=2000。Scaled Value Range中，High=100、Low=0。如下图：

<img src="../img/case_auto10.png">

最终设备aio下面的标签列表如下图：

<img src="../img/case_auto11.png">

### 2.4.3 电磁流量计流速Tag

在设备flow节点下面，新增两个Tag如下，分别对应流量值的高位和低位
```
Name = flow_h
Title = speed high
Data type = int16
R/W = Read
Address = 404113
```
```
Name = flow_l
Title = speed low
Data type = int16
R/W = Read Only
Address = 404113
```
接下来，点击“+Add Middle Tag”按钮，新增中间Tag，填写内容如下：

<img src="../img/case_auto12.png">

请注意，中间节点Express输入的是js脚本。此节点脚本是一个函数调用，输入flow_h和flow_l两个标签值，通过组合计算得到流量的浮点数值（单位是立方米/秒）。确定之后，我们得到如下标签列表：

<img src="../img/case_auto13.png">

### 2.4.4 启动测试Tags

配置好这些设备Tags之后，我们接下来应该先结合现场设备，测试这些Tags的有效性。

点击项目管理界面的启动按钮，启动项目。注意观察Connectors的连接状态，观察Browser下面的树形结构的通道状态。你会发现通道左边的小齿轮进入了转动状态，并且如果和现场设备连接成功的化，对应的Connector右边图标也会变成连接状态。如图所示：

<img src="../img/case_auto14.png">

在右边主内容区域点击[Tags]标签,并在Browser下面的树型内容中点击项目根节点，此时[Tags]下面会列举出所有设备的Tag，并且如果现场设备都运行正常的情况下面，所有的Tag都应该有对应的数值,并且Quality都显示✓，如下图所示：

<img src="../img/case_auto15.png">

此时，说明我们配置的设备和Tags都运行正常，现场设备的数据都读取正常。查看水位计读数和现场水位是否一致，流量计的读数是否和现场仪器表头的读数一致。

接下来我们可以针对不同的设备进行局部调试：

在ch1.dio.pstart、ch1.dio.pstop对应的Tag中，Write列的输入框，分别写入1，并点击右边的写入小按钮，下达水泵启动或停止指令。并在现场观察水泵运行状态，同时，也查看ch1.dio.p_running对应的值的变化。

在ch1.aio.valve_val对应的Tag中，，Write列的输入框，写入0-100范围内的值（0阀门关闭、100阀门全开），点击写入小按钮。并在现场观察阀门开度变化情况。如下图所示：

<img src="../img/case_auto16.png">

以上一切都正常之后，说明我们配置的设备和Tags都没有问题，并且和现场设备都进行了有效的关联。

## 2.5 设计监控画面

通过以上步骤，我们准备好了所有的设备和对应的数据Tag，接下来我们基于这些内容，设计监控画面。

鼠标右键点击项目根节点，在弹出的菜单中选择"New HMI"。在弹出的"Add HMI"对话框中，填写如下内容，完成之后，在下面根节点下面会出现u1节点：

<img src="../img/case_auto17.png">

