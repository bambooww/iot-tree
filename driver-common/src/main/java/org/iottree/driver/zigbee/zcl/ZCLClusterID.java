package org.iottree.driver.zigbee.zcl;

import java.util.LinkedHashMap;

/**
 * 功能簇
 * 
 * Cluster 是功能的最小单元，每个 Cluster 包含一组属性（Attribute）和命令（Command）。
 * 
 * @author jason.zhu
 */
public class ZCLClusterID
{
	int id ;
	
	String title ;
	
	String desc ;
	
	public ZCLClusterID(int id,String tt,String desc)
	{
		this.id = id ;
		this.title = tt ;
		this.desc = desc ;
	}
	
	public int getId()
	{
		return this.id ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public String getDesc()
	{
		return this.desc ;
	}
	
	//
	private static LinkedHashMap<Integer,ZCLClusterID> ID2CLUSTERID = new LinkedHashMap<>();
	
	private static void reg_cluster(int id,String tt,String desc)
	{
		ZCLClusterID cid = new ZCLClusterID(id,tt,desc) ;
		ID2CLUSTERID.put(id,cid) ;
	}
	
	public static ZCLClusterID getClusterID(int id)
	{
		return ID2CLUSTERID.get(id) ;
	}
	
	static
	{
		//通用簇（General Clusters）— 所有设备几乎都支持
		reg_cluster(0x0000,"Basic"	                    ,"设备基本信息（厂商名、型号、ZCL 版本、电源类型等）— 所有设备强制支持");
		reg_cluster(0x0001,"Power Configuration"	    ,"电源配置（电池电量、电压、报警阈值）");
		reg_cluster(0x0002,"Device Temperature Config"	,"设备内部温度配置与报警");
		reg_cluster(0x0003,"Identify"	                ,"让设备进入识别模式（如灯闪烁），用于物理定位");
		reg_cluster(0x0004,"Groups"	                    ,"组管理（将设备加入/移出组，实现群控）");
		reg_cluster(0x0005,"Scenes"	                    ,"场景管理（保存/调用一组设备的状态快照）");
		reg_cluster(0x0006,"On/Off"	                    ,"开关控制（开、关、切换）— 灯/插座的核心簇");
		reg_cluster(0x0007,"On/Off Switch Configuration","开关行为配置（如切换/翻转模式）");
		reg_cluster(0x0008,"Level Control"	            ,"亮度/级别控制（调光、风扇速度等）");
		reg_cluster(0x0009,"Alarms"	                    ,"报警通知");
		reg_cluster(0x000A,"Time"	                    ,"时间同步");
		reg_cluster(0x000B,"RSSI Location"	            ,"位置信息交换");
		reg_cluster(0x000C,"Analog Input (Basic)"	    ,"模拟量输入接口");
		reg_cluster(0x000D,"Analog Output (Basic)"	    ,"模拟量输出接口");
		reg_cluster(0x000E,"Analog Value (Basic)"	    ,"模拟值设置（控制系统参数）");
		reg_cluster(0x000F,"Binary Input (Basic)"	    ,"二进制输入接口");
		reg_cluster(0x0010,"Binary Output (Basic)"	    ,"二进制输出接口");
		reg_cluster(0x0011,"Binary Value (Basic)"	    ,"二进制值设置");
		reg_cluster(0x0012,"Multistate Input (Basic)"	,"多状态输入接口");
		reg_cluster(0x0013,"Multistate Output (Basic)"	,"多状态输出接口");
		reg_cluster(0x0014,"Multistate Value (Basic)"	,"多状态值设置");
		reg_cluster(0x0019,"OTA Upgrade"	            ,"固件空中升级");
		
		// 测量与传感簇（Measurement & Sensing）
		reg_cluster(0x0400,"Illuminance Measurement"	            ,"光照度测量（单位：lux）");
		reg_cluster(0x0401,"Illuminance Level Sensing Config"	,"光照级别传感配置");
		reg_cluster(0x0402,"Temperature Measurement"	            ,"温度测量（单位：0.01°C）");
		reg_cluster(0x0403,"Pressure Measurement"	            ,"气压测量");
		reg_cluster(0x0404,"Flow Measurement"	                ,"流量测量");
		reg_cluster(0x0405,"Relative Humidity"	                ,"相对湿度测量");
		reg_cluster(0x0406,"Occupancy Sensing"	                ,"人体占用传感（有人/无人）");
		reg_cluster(0x0B04,"Electrical Measurement"	            ,"电气测量（电压、电流、功率、能耗）");
		
		//照明专用簇（Lighting）
		reg_cluster(0x0300,"Color Control"	             ,"颜色控制（色温、色坐标、饱和度、色环）");
		reg_cluster(0x0301,"Ballast Configuration"	     ,"镇流器配置");
		reg_cluster(0x0302,"Fan Control"	                 ,"风扇控制");
		reg_cluster(0x0303,"Dehumidification Control"	 ,"除湿控制");
		reg_cluster(0x0304,"Thermostat UI Configuration"	 ,"温控器 UI 配置");
		
		// 安防与生命安全（Security & Safety）
		reg_cluster(0x0500,"IAS Zone"	,"入侵报警区域（门磁、PIR、玻璃破碎）");
		reg_cluster(0x0501,"IAS ACE"	    ,"入侵报警控制面板（布防/撤防）");
		reg_cluster(0x0502,"IAS WD"	    ,"入侵报警警告设备（警号、闪光灯）");
		reg_cluster(0x0503,"Fire Zone"	,"火灾报警区域");
		reg_cluster(0x0504,"Fire ACE"	,"火灾控制面板");
		reg_cluster(0x0505,"Fire WD"	    ,"火灾警告设备");
		
		//ZLL 专用簇（Profile 0xC05E）
		reg_cluster(0x1000,"ZLL Commissioning",	"ZLL 入网配置（TouchLink 配对）") ;
		
	}
}
