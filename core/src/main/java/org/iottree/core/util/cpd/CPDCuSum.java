package org.iottree.core.util.cpd;

/**
 * 累积和控制图（CUSUM） - CUSUM 和概率模型适用于渐进变化的检测。
 * 
 * CUSUM 是一种统计过程控制工具，用于检测逐渐变化的变化。它通过累积偏差来检测突变。
 
 def cusum(data, threshold):
    s_pos, s_neg = 0, 0
    mean = np.mean(data)
    for i in range(len(data)):
        s_pos = max(0, s_pos + data[i] - mean - threshold)
        s_neg = max(0, s_neg - data[i] + mean - threshold)
        if s_pos > threshold or s_neg > threshold:
            return True
    return False
    
 * @author jason.zhu
 *
 */
public class CPDCuSum
{

}
