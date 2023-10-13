

IOTTree Bind- OPC UA Client
==


OPC UA是在OPC DA之后的统一架构，原来的OPC DA应用程序只能运行在基于Windows的PC系统上，而新的OPC统一架构（OPC UA）统一了现行标准，采样面向服务的架构，并且与平台无关。

OPC UA已经被越来越多的设备（如PLC）和软件支持，相关的技术标准请参考对应的资料。

本文针对已经能够提供OPC UA Server端服务的设备或软件作为被接入对象，通过IOT-Tree Server提供的OPC UA Client接入选择，进行数据接入。

和[通过OPC DA Client接入数据][case_opc_da]这个案例类似，OPC UA也属于绑定（Bind)方式接入的一种。



# 1 Environment Preparation 

## 1.1 Installation Configuration KEPServerEx V6 

Please go to the KEPServerEx website to get this software and install and start it as prompted. 

In the main management interface, open "\KEPServerEx\Projects\simdemo.opf" under the KEPServerEx installation directory, which is a demonstration OPC configuration file that comes with the software. The related channels, devices and data organizations are shown below: 



[case_opc_da]: ./case_opc_da.md
