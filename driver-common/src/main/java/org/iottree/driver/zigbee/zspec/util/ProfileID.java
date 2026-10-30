package org.iottree.driver.zigbee.zspec.util;

/**
 * Zigbee 3.0 之后，ZLL 设备也迁移到 0x0104 Profile，通过 Device ID 区分照明设备。
 * 新设备开发建议统一使用 0x0104。
 * 
 * @author jason.zhu
 *
 */
public enum ProfileID
{
	ZHA(0x0104,"Zigbee Home Automation (ZHA)","最常用，覆盖照明、家电、传感器、安防等全品类，Zigbee 3.0 统一使用此 Profile"),
	ZLL(0xC05E, "Zigbee Light Link (ZLL)","专为照明设计，支持 TouchLink 近场配对，飞利浦 Hue 等早期智能灯使用"), 
	SE(0x0109, "Smart Energy (SE)","面向智能电表、能源管理"),
	BA(0x0105, "Building Automation","楼宇自动化"),
	TA(0x0107, "Telecom Applications","电信应用"),
	PHHC(0x0108, "Personal Home & Hospital Care","个人健康护理");

	private final int val;
	private final String title;
	private final String desc;

	ProfileID(int val, String title,String desc)
	{
		this.val = val;
		this.title = title;
		this.desc = desc ;
	}

	public int getValInt()
	{
		return val;
	}

	public String getTitle()
	{
		return title;
	}
	
	public String getDesc()
	{
		return this.desc ;
	}
	
	public static ProfileID valOfInt(int v)
	{
		switch(v)
		{
		case 0x0104:
			return ZHA ;
		case 0xC05E:
			return ZLL;
		case 0x0109:
			return SE;
		case 0x0105:
			return BA;
		case 0x0107:
			return TA;
		case 0x0108:
			return PHHC;
		default:
			return null ;
		}
	}
}
