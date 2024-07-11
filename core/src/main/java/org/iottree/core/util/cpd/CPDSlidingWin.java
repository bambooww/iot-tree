package org.iottree.core.util.cpd;

import org.iottree.core.basic.ValAlert;
import org.iottree.core.basic.ValAlertTp;

/**
 * 滑动窗口方法 - 阈值法和滑动窗口方法适用于简单变化的检测
 * 
 * 使用滑动窗口计算窗口内数据的统计特性（如均值、标准差），并检测窗口间统计特性的显著变化。
 * 
 window_size = 10
window1 = data[-window_size:]
window2 = data[-2*window_size:-window_size]
mean1 = np.mean(window1)
mean2 = np.mean(window2)
if abs(mean1 - mean2) > threshold:
    print("Detected a change point")
 * @author jason.zhu
 *
 */
public class CPDSlidingWin // extends ValAlertTp
{

	
}
