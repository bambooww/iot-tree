package org.iottree.core.util.filter;

/**
 * 
 算术平均滤波法

    A、方法：

        连续取N个采样值进行算术平均运算

        N值较大时：信号平滑度较高，但灵敏度较低

        N值较小时：信号平滑度较低，但灵敏度较高

        N值的选取：一般流量，N=12；压力：N=4

    B、优点：

        适用于对一般具有随机干扰的信号进行滤波

        这样信号的特点是有一个平均值，信号在某一数值范围附近上下波动

    C、缺点：

        对于测量速度较慢或要求数据计算速度较快的实时控制不适用

        比较浪费RAM

3、算术平均滤波法



#define N 12

char filter()

{

   int  sum = 0;

   for ( count=0;count<N;count++)

   {

      sum + = get_ad();

      delay();

   }

   return (char)(sum/N);

}
       
 * 
 * @author zzj
 *
 */
public class FilterArithmeticMean
{

}
