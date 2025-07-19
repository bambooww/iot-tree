时序数据库 InfluxDB2
==


本模块是时序数据库InfluxDB2对应的使用支持节点。每个模块可以配置使用一个InfluxDB2数据源，此数据源在IOT-Tree部署实例的管理首页进行配置。

它包含如下子节点：

## InfluxDB Query

支持Flux语法，对数据库进行查询并输出

## InfluxDB Write

支持Flux语法，对数据库进行写入

## JSON To Points

辅助节点，支持JSON对象到数据库数据点的转换

## Transfer Tags To Points

辅助节点，支持标签到数据库数据点的转换

## Measurement (资源节点)

相当于数据表的对应资源定义


