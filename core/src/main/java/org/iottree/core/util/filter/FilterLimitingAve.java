package org.iottree.core.util.filter;

/**
 * 
 限幅平均滤波法

    A、方法：

        相当于“限幅滤波法”+“递推平均滤波法”

        每次采样到的新数据先进行限幅处理，

        再送入队列进行递推平均滤波处理

    B、优点：

        融合了两种滤波法的优点

        对于偶然出现的脉冲性干扰，可消除由于脉冲干扰所引起的采样值偏差

    C、缺点：

        比较浪费RAM

6、限幅平均滤波法



略 参考子程序1、3
 
 #define A 10

char value;

char filter()

{

   char  new_value;

   new_value = get_ad();

   if ( ( new_value - value > A ) || ( value - new_value > A )

      return value;

   return new_value;

       

} 


--------------------------------
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
       

 * @author zzj
 *
 */
public class FilterLimitingAve
{

}
