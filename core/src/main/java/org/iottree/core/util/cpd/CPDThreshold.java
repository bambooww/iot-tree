package org.iottree.core.util.cpd;

/**
 * 基于阈值的方法  -  阈值法和滑动窗口方法适用于简单变化的检测
 * 
 * 通过设置一个固定的阈值来检测突变。当新数据点与前一个数据点的差异超过阈值时，
 * 即认为发生了突变。
 * threshold = 10
if abs(new_data_point - previous_data_point) > threshold:
    print("Detected a change point")
 * @author jason.zhu
 *
 */
public class CPDThreshold
{

}
