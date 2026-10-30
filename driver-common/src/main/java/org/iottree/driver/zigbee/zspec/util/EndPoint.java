package org.iottree.driver.zigbee.zspec.util;

/**
 *   设备内的功能模块
 *   
 *   定义：8 位编号（1~240），标识设备内部的一个逻辑功能单元。
     作用：一个物理设备可以有多个端点，每个端点代表一种独立功能
 * 
 *      端点范围	                    用途	                                                           是否可用
		 0	                 ZDO（ZigBee 设备对象），负责设备配置和管理	                   ❌ 保留
		 1 ~ 240	             应用程序可用，可自由定义	                                              ✅ 可用
		 241 ~ 254	          保留端点，不可用于普通应用	                                             ❌ 保留
		 255	                     广播消息                                                             ❌ 保留
		
		其中，端点 242（0xF2）是 ZigBee Green Power 专用端点，
		用于普通 ZigBee 设备与 Green Power 设备（如无线动能开关）交互。

二、zigbee-herdsman 为何使用 242
	zigbee-herdsman 使用 242 端点，是因为它作为协调器/网关，需要：
	接收 Green Power 设备消息：Green Power 设备（如 Philips Hue 动能开关）通过 242 端点与协调器通信。
	注册端点回调：zigbee-herdsman 启动时通过 AF_REGISTER 注册端点 242，绑定应用层回调函数，接收来自 Green Power 设备和其他设备的消息。
	
三、你的程序该如何定义端点
	如果你的程序想替代 zigbee-herdsman，端点号选择建议如下：
	1. 普通应用端点（推荐）
	选择 1 ~ 240 范围内的任意端点号，例如：
	    #define MY_ENDPOINT  1   // 自定义端点号，1~240 均可
	    
	然后通过 afRegister() 注册该端点，绑定输入/输出簇列表和回调函数。

2. 如果需要兼容 Green Power
	如果你的程序需要接收 Green Power 设备（如 Hue 动能开关）的消息，则必须注册 242 端点，
	并实现 Green Power 代理功能。

3. 多端点场景
	一个 ZigBee 设备可以注册多个端点，例如：
	// 端点 1：On/Off 控制
	Endpoint(1, input_clusters=[0x0006], output_clusters=[])
	
	// 端点 2：Level Control 调光
	Endpoint(2, input_clusters=[0x0008], output_clusters=[])
	
	// 端点 242：Green Power 代理（如需兼容）
	Endpoint(242, input_clusters=[0x0021], output_clusters=[])

 * @author jason.zhu
 */
public class EndPoint
{
	/**
	 * 端点 242（0xF2）是 ZigBee Green Power 专用端点，
	 * 用于普通 ZigBee 设备与 Green Power 设备（如无线动能开关）交互。
	 */
	public static final int GP = 242 ;
}
