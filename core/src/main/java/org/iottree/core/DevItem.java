package org.iottree.core;

/**
 * Device inner data item
 * it's a entity class ,used for define Dev limit tags
 * 1)it's used by DevModel to provider limited item 
 * @author zzj
 *
 */
public class DevItem
{
	String name = null ;
	
	String title = null ;
	
	String desc = null ;
	
	String addr = null ;
	
	boolean canW = false ;
	
	UAVal.ValTP valTp = null ;
	
	long scanRate = 100 ;
	
	public DevItem()
	{}
	
	public DevItem(String name,String title,String desc,String addr,UAVal.ValTP vt,boolean canw,long sr)
	{
		this.name = name ;
		this.title = title ;
		this.desc = desc ;
		this.addr = addr ;
		valTp = vt;
		canW = canw ;
		scanRate = sr ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		return title ;
	}
	
	public String getDesc()
	{
		return desc ;
	}
	
	public String getAddr()
	{
		return addr ;
	}
	
	public UAVal.ValTP getValTP()
	{
		return valTp;
	}
	
	public boolean canWrite()
	{
		return canW;
	}
	
	public long getScanRate()
	{
		return scanRate ;
	}
}
