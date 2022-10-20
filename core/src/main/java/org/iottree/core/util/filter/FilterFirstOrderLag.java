package org.iottree.core.util.filter;

/**
 
 一阶滞后滤波法

    A、方法：

        取a=0~1

        本次滤波结果=（1-a）*本次采样值+a*上次滤波结果

    B、优点：

        对周期性干扰具有良好的抑制作用

        适用于波动频率较高的场合

    C、缺点：

        相位滞后，灵敏度低

        滞后程度取决于a值大小

        不能消除滤波频率高于采样频率的1/2的干扰信号
        
    为加快程序处理速度假定基数为100，a=0~100 

#define a 50

char value;

char filter()

{

   char  new_value;

   new_value = get_ad();

   return (100-a)*value + a*new_value;

}    
 
 * @author zzj
 *
 */
public class FilterFirstOrderLag
{

}
