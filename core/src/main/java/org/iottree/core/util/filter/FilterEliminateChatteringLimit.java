package org.iottree.core.util.filter;

/**
 限幅消抖滤波法

    A、方法：

        相当于“限幅滤波法”+“消抖滤波法”

        先限幅,后消抖

    B、优点：

        继承了“限幅”和“消抖”的优点

        改进了“消抖滤波法”中的某些缺陷,避免将干扰值导入系统

    C、缺点：

        对于快速变化的参数不宜

}
 
 * @author zzj
 *
 */
public class FilterEliminateChatteringLimit
{

}
