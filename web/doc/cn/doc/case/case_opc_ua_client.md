
通过OPC UA Client接入数据
==



OPC UA是在OPC DA之后的统一架构，原来的OPC DA应用程序只能运行在基于Windows的PC系统上，而新的OPC统一架构（OPC UA）统一了现行标准，采样面向服务的架构，并且与平台无关。

OPC UA已经被越来越多的设备（如PLC）和软件支持，相关的技术标准请参考对应的资料。

本文针对已经能够提供OPC UA Server端服务的设备或软件作为被接入对象，通过IOT-Tree Server提供的OPC UA Client接入选择，进行数据接入。

和[通过OPC DA Client接入数据][case_opc_da]这个案例类似，OPC UA也属于绑定（Bind)方式接入的一种。


# 1 环境准备

## 1.1 安装配置KEPServerEx V6

请到KEPServerEx官网获取此软件，并根据提示安装启动。

在主管理界面中，打开KEPServerEx安装目录下面的 "\KEPServerEx\Projects\simdemo.opf"，这是一个软件自带的演示OPC配置文件，相关的通道、设备和数据组织如下图所示：




[case_opc_da]: ./case_opc_da.md
