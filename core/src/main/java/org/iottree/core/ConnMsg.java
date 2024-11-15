package org.iottree.core;

import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;

public class ConnMsg
{
	String uid ;
	
	String title = null ;
	
	String desc = "" ;
	
	String icon = "" ;
	
	String iconColor = "" ;
	
	String dlgUrl = null ;
	
	String dlgTitle =null ;
	
	public ConnMsg()
	{
		this.uid = CompressUUID.createNewId();
	}
	
	public String getId()
	{
		return uid ;
	}
	
	public String getTitle()
	{
		return title ;
	}
	
	public ConnMsg asTitle(String t)
	{
		this.title= t ;
		return this ;
	}
	
	public String getDesc()
	{
		return desc ;
	}
	
	public ConnMsg asDesc(String d)
	{
		this.desc = d ;
		return this ;
	}
	
	public String getIcon()
	{
		return icon ;
	}
	
	public ConnMsg asIcon(String icon)
	{
		this.icon = icon ;
		return this ;
	}
	
	public String getIconColor()
	{
		return iconColor ;
	}
	
	public ConnMsg asIconColor(String c)
	{
		this.iconColor = c ;
		return this ;
	}
	
	public String getDlgUrl()
	{
		return dlgUrl ;
	}
	
//	public ConnMsg asDlgUrl(String u)
//	{
//		this.dlgUrl = u ;
//		return this ;
//	}
	
	public String getDlgTitle()
	{
		return dlgTitle ;
	}
	
//	public ConnMsg asDlgTitle(String t)
//	{
//		this.dlgTitle = t; 
//		return this ;
//	}
	
	public ConnMsg asDlg(String url,String title)
	{
		this.dlgUrl = url ;
		this.dlgTitle = title;
		return this;
	}
	
	public String toListJsonStr()
	{
		return "{\"id\":\""+this.uid+"\",\"t\":\""+Convert.plainToJsStr(title)+"\",\"i\":\""+icon+"\",\"ic\":\""+iconColor+"\"}";
	}
	
	public String toFullJsonStr()
	{
		String ret = "{\"id\":\""+this.uid+"\",\"t\":\""+Convert.plainToJsStr(title)+"\",\"i\":\""+icon+"\",\"ic\":\""+iconColor+"\"";
		if(Convert.isNotNullEmpty(dlgUrl))
			ret += ",\"dlgu\":\""+Convert.plainToJsStr(dlgUrl)+"\",\"dlgt\":\""+Convert.plainToJsStr(dlgTitle)+"\"";
		ret += "}";
		return ret ;
	}
	
}
