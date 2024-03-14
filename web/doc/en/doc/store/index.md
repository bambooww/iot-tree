Data storage, processing, and presentation
==

## 1 Simple storage and output of Tag data



## 2 Internal timing segment recorder(TSS)

IOT-Tree内部基于SQLite实现了一个标签数据高速记录器。由于标签数据变化基于时间序列，联系记录值不变时，不会新增记录，所以称为时序端记录器(TSS Recorder)。

你只需要在标签上通过简单的配置，就可以让IOT-Tree项目在运行过程中，自动为您记录所有的采集变化值。

详细信息请参考[对应链接][tss]。

## 3 Secondary processing of recorded data

以上面的"TSS Recorder"为基础，IOT-Tree定义了一个数据分析处理框架。基于这个框架，系统可以统一管理所有的数据处理对象和每个处理对象输出的数据内容。并且以此为基础，定义输出数据结果内容所需要的展示UI。

详细信息请参考[对应链接][rec]。

[store]:./store.md
[tss]:./inner_tssdb.md
[rec]:./inner_rec.md
