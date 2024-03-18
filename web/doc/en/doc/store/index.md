Data storage, processing, and presentation
==

## 1 Simple storage and output of Tag data



## 2 Internal timing segment recorder(TSS)

为了满足更多的需要，从1.3版本开始，IOT-Tree内部基于SQLite实现了一个标签数据高速记录器。

由于标签数据变化基于时间序列，联系记录值不变时，不会新增记录，所以称为时序端记录器(TSS Recorder)。

你只需要在标签上通过简单的配置，就可以让IOT-Tree项目在运行过程中，自动为您记录所有的采集变化值。由于内部使用SQLite，你不需要专门使用特定的数据库就可以满足绝大多数应用场合。

详细信息请参考[对应链接][tss]。

有了这个基础，IOT-Tree还专门为数据处理设计了一个架构，可以很方便的对这些数据进行二次加工。

## 3 Secondary processing of recorded data

以上面的"TSS Recorder"为基础，IOT-Tree定义了一个数据分析处理框架。基于这个框架，系统可以统一管理所有的数据处理对象和每个处理对象输出的数据内容。并且以此为基础，定义输出数据结果内容所需要的展示UI。

考虑到数据如何具体展示使用，这是每个使用者都会有自己特殊要求的

详细信息请参考[对应链接][rec]。

[store]:./store.md
[tss]:./inner_tssdb.md
[rec]:./inner_rec.md
