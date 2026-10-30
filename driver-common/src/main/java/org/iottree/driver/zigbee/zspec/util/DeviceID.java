package org.iottree.driver.zigbee.zspec.util;

import java.util.LinkedHashMap;

public class DeviceID
{
	int id ;
	
	String title ;
	
	String desc ;
	
	public DeviceID(int id,String tt,String desc)
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
	private static LinkedHashMap<Integer,DeviceID> ID2DEVICEID = new LinkedHashMap<>();
	
	private static void reg_dev(int id,String tt,String desc)
	{
		DeviceID cid = new DeviceID(id,tt,desc) ;
		ID2DEVICEID.put(id,cid) ;
	}
	
	public static DeviceID getDeviceID(int id)
	{
		return ID2DEVICEID.get(id) ;
	}
	
	static
	{
		//照明类（Lighting）
		reg_dev(0x0100,"On/Off Light"	         ,"开关灯（单色，仅开/关）");
		reg_dev(0x0101,"Dimmable Light"	         ,"可调光灯（支持亮度调节）");
		reg_dev(0x0102,"Color Light"	             ,"彩色灯（RGB）");
		reg_dev(0x010C,"Color Temperature Light"	 ,"色温灯（CW/暖白-冷白）");
		reg_dev(0x010D,"Extended Color Light"	 ,"扩展色灯（RGBW / RGBCW）");
		
		//开关与控制类（Switches & Controls）
		reg_dev(0x0103,"On/Off Light Switch"	,"开关灯开关（墙壁开关）");
		reg_dev(0x0104,"Dimmer Switch"	    ,"调光开关");
		reg_dev(0x0105,"Color Dimmer Switch"	,"彩色调光开关");
		reg_dev(0x0840,"Control Bridge"	    ,"控制桥（如遥控器中枢）");
		reg_dev(0x000F,"Generic Switch"	    ,"通用开关");
		
		//插座与执行器（Plugs & Actuators）
		reg_dev(0x010A,"On/Off Plug-in Unit"	            ,"开关插座");
		reg_dev(0x010B,"Dimmable Plug-In Unit"	        ,"可调光插座");
		reg_dev(0x010F,"Mounted On/Off Control"	        ,"固定安装开关（如继电器模块）");
		reg_dev(0x0110,"Mounted Dimmable Load Control"	,"固定安装调光负载");
		
		//传感器类（Sensors）
		reg_dev(0x0106,"Dimmer Ballast"	        ,"调光镇流器");
		reg_dev(0x0107,"Color Dimmer Ballast"	,"彩色调光镇流器");
		reg_dev(0x0402,"Temperature Sensor"	    ,"温度传感器");
		reg_dev(0x0403,"Pressure Sensor"	        ,"压力传感器");
		reg_dev(0x0405,"Humidity Sensor"	        ,"湿度传感器");
		reg_dev(0x0406,"Occupancy Sensor"	    ,"人体/占用传感器");
		reg_dev(0x0407,"Light Sensor"	        ,"光照传感器");
		reg_dev(0x0400,"IAS Zone"	            ,"安防区域（门磁、烟雾、燃气等）");
		
		//其他
		reg_dev(0x0005,"Zigbee Coordinator"	,"协调器（Zigbee 3.0）") ;
		reg_dev(0x0009,"Zigbee Router"	    ,"路由器") ;
		reg_dev(0x0303,"Pump"	            ,"水泵") ;
		reg_dev(0x0304,"Pump Controller"	    ,"水泵控制器") ;
		reg_dev(0x0070,"Refrigerator"	    ,"冰箱") ;
		reg_dev(0x0042,"Water Valve"	        ,"水阀") ;
	}
}
