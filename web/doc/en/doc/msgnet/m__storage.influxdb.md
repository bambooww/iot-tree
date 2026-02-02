Time series database InfluxDB2
==



This module is the usage support node corresponding to the time series database InfluxDB2. Each module can be configured to use an InfluxDB2 data source, which can be configured on the admin homepage of IOT-Tree deployment instances.

It includes the following sub nodes:

## InfluxDB Query

Support Flux syntax, query the database and output

## InfluxDB Write

Support Flux syntax for writing to the database.

## JSON To Points

Auxiliary node, supporting the conversion of JSON objects to database data points

## Transfer Tags To Points

Auxiliary node, supporting the conversion of tags to database data points

## Read By Tags

Function node: Read the corresponding historical data based on the configured tag list. This node supports query time parameters input in JSON format, with the format as follows:
```
{
    "start_dt": 213123123123, // unix time in milliseconds
    "end_dt": 234234234234  // unix time in milliseconds
}
```
If there are no corresponding parameters, end_dt will automatically use the current time, and start_dt will be automatically calculated based on the query time period configured by the node.

## Measurement (Resource Node)

Equivalent to the corresponding resource definition of a data table

