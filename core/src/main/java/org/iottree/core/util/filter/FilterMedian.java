package org.iottree.core.util.filter;

/**
 * 
 中位值滤波法

    A、方法：

        连续采样N次（N取奇数）

        把N次采样值按大小排列

        取中间值为本次有效值

    B、优点：

        能有效克服因偶然因素引起的波动干扰

        对温度、液位的变化缓慢的被测参数有良好的滤波效果

    C、缺点：

        对流量、速度等快速变化的参数不宜

2、中位值滤波法

  N值可根据实际情况调整

    排序采用冒泡法

#define N  11

char filter()

{

   char value_buf[N];

   char count,i,j,temp;

   for ( count=0;count<N;count++)

   {

      value_buf[count] = get_ad();

      delay();

   }

   for (j=0;j<N-1;j++)

   {

      for (i=0;i<N-j;i++)

      {

         if ( value_buf[i]>value_buf[i+1] )

         {

            temp = value_buf[i];

            value_buf[i] = value_buf[i+1];

             value_buf[i+1] = temp;

         }

      }

   }

   return value_buf[(N-1)/2];

}   
 * @author zzj
 *
 */
public class FilterMedian
{

}
