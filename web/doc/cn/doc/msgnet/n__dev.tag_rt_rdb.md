实时数据表同步
==


通过配置的关系数据表，触发所有标签实时数据的同步更新，可以为外界提供基于关系数据库表形式的实时数据查询

此节点必须和关系数据库模块包含的数据库表资源节点配合使用。

每次有输入消息时，触发一次数据库表的同步操作，项目中的每个标签Tag都会在对应的表中建立一行数据，并且里面的值和时间列会根据标签实际变化情况进行同步。




<img src="../img/msgnet/oth01.png">
