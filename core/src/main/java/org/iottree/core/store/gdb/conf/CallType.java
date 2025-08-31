package org.iottree.core.store.gdb.conf;

public enum CallType
{
    /// <remarks/>
    sql,
    
    /// <remarks/>
    pro,
    
    //  由于gdb访问数据库时不同数据库操作不同，为了使xml配置
    //文件能够运行在不同数据库中，需要对一些操作针对不同的数据库输出不同的语句
    //入，insert一条记录后，需要获得新的id，针对sqlserver - select @@IDENDITY
    //而derby使用 values IDENTITY_VAL_LOCAL()
    //所以，exe_type=auto_fit情况下，需要指定一个gdb内部自带的sql标记信息
    //如：上面的操作定义为 select_identity
    auto_fit,
}
