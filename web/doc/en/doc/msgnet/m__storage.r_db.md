Relational DB
==



Each relational database module can be configured to use a JDBC data source, which can be configured on the admin main page of IOT-Tree deployment instances.

It contains three child nodes: 'SQL select', 'SQL Insert/Update', and 'DB Table'.

## SQL Select query node

This node requires the input message payload to be an SQL statement or a JSON object containing query page information. 

Output one or more JSON objects based on the query results and parameter settings (which can be one object per row or multiple rows per object).

## SQL Insert/Update

This node requires the input message payload to be an SQL statement (insert data or update data). If successful, output the number of affected rows. If failed, output failure information from the failed connection point.

## DB Table Resource Node

This node is defined as a database table under the corresponding relational database data source. It can directly support other nodes that require the use of relational database table.

