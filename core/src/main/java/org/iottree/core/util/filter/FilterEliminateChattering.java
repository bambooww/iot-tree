package org.iottree.core.util.filter;

/**
 * 
 消抖滤波法

    A、方法：

        设置一个滤波计数器

        将每次采样值与当前有效值比较：

        如果采样值＝当前有效值，则计数器清零

        如果采样值<>当前有效值，则计数器+1，并判断计数器是否>=上限N(溢出)

            如果计数器溢出,则将本次值替换当前有效值,并清计数器

    B、优点：

        对于变化缓慢的被测参数有较好的滤波效果,

        可避免在临界值附近控制器的反复开/关跳动或显示器上数值抖动

    C、缺点：

        对于快速变化的参数不宜

        如果在计数器溢出的那一次采样到的值恰好是干扰值,则会将干扰值当作有效值导入系

统

9、消抖滤波法

#define N 12

char filter()

{

   char count=0;

   char new_value;

   new_value = get_ad();

   while (value !=new_value);

   {

      count++;

      if (count>=N)   return new_value;

       delay();

      value = get_ad();

   }

   return value;   
}
 * @author zzj
 *
 */
public class FilterEliminateChattering
{

}
