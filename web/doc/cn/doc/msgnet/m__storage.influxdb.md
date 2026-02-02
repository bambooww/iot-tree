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

## Read By Tags

功能节点：根据配置的标签列表读取对应的历史数据。此节点支持以json格式输入的查询时间参数，格式如下：
```
{
    "start_dt":213123123123， //unix毫秒时间
    "end_dt":234234234234  //unix毫秒时间
}
```
如果没有对应参数，则end_dt自动使用当前时间，start_dt通过节点配置的查询时间区间自动计算。

## Measurement (资源节点)

相当于数据表的对应资源定义


