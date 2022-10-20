package org.iottree.core.util.filter;

/**
 * 
 中位值平均滤波法（又称防脉冲干扰平均滤波法）

    A、方法：

        相当于“中位值滤波法”+“算术平均滤波法”

        连续采样N个数据，去掉一个最大值和一个最小值

        然后计算N-2个数据的算术平均值

        N值的选取：3~14

    B、优点：

        融合了两种滤波法的优点

        对于偶然出现的脉冲性干扰，可消除由于脉冲干扰所引起的采样值偏差

    C、缺点：

        测量速度较慢，和算术平均滤波法一样

        比较浪费RAM

5、中位值平均滤波法（又称防脉冲干扰平均滤波法）

#define N 12

char filter()

{

   char count,i,j;

   char value_buf[N];

   int  sum=0;

   for  (count=0;count<N;count++)

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

   for(count=1;count<N-1;count++)

      sum += value[count];

   return (char)(sum/(N-2));

}

 * @author zzj
 *
 */
public class FilterMedianAve
{

}
