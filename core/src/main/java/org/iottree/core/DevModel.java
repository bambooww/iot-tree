package org.iottree.core;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UAVal.ValTP;

/**
 * driver support's model which may limit device addresses
 * and some other's param to limit device driver
 * 
 * @author zzj
 *
 */
public class DevModel // extends DevDef implements IPropChecker
{
	String name = null ;
	
	String title = null ;
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		return title ;
	}
	
	
//	DevCat devCat = null ;
//
//	ArrayList<String> dependDrvNames = new ArrayList<>() ;
//	
//	UADev belongToDev = null ;
//	
//	transient DevDriver dependDrv = null ;
//	
//	transient List<DevAddr> addrs = null ;
//	
//	public DevModel()
//	{}
//
//	public String getUniqueId()
//	{
//		if(this.devCat==null)
//			return "_."+this.getName();
//		return this.devCat.getName()+"."+this.getName();
//	}
//	
//	public abstract String getName();
//	
//	public abstract String getTitle();
//	
//	//public String getDr
//	
//	public DevDriver getDependDrv()
//	{
//		return dependDrv ;
//	}
//	
//	public UADev getBelongToDev()
//	{
//		return belongToDev ;
//	}
//	
//
//	
//	
//	public abstract DevModel copyMe();
//	
//	
//	final protected boolean getPropValBool(String groupn,String itemn,boolean defv)
//	{
//		return this.belongToDev.getOrDefaultPropValueBool(groupn, itemn, defv);
//	}
//	
//	final protected long getPropValInt(String groupn,String itemn,long defv)
//	{
//		return this.belongToDev.getOrDefaultPropValueLong(groupn, itemn, defv);
//	}
//	
//	final protected double getPropValFloat(String groupn,String itemn,double defv)
//	{
//		return this.belongToDev.getOrDefaultPropValueDouble(groupn, itemn, defv);
//	}
//	
//	final protected String getPropValStr(String groupn,String itemn,String defv)
//	{
//		return this.belongToDev.getOrDefaultPropValueStr(groupn, itemn, defv);
//	}
//	//-----------------------------
//	
//
//	public String getDevId()
//	{
//		return this.getPropValStr("dev", "devid", "") ;
//	}
//	
//	/**
//	 * check model prop value or other param
//	 * @param failedr
//	 * @return
//	 */
//	protected boolean CONF_supportFixItems()
//	{
//		return false;
//	}
//	
//	protected List<DevItem> CONF_getFixItems()
//	{
//		return null ;
//	}
//	
//	
//	protected List<DevAddr> RT_listModelAddrs()
//	{
//		return this.addrs;
//	}
//	
//	/**
//	 * setup device in model before run
//	 * this method will be called after Driver is be setup
//	 * 1)overrider can add some tag in node 
//	 * 2)overrider can analyse addresses and create some inner cmd.
//	 *   ready to after running.
//	 * @param drv
//	 * @param addrs
//	 * @param failedr
//	 * @return
//	 */
//	protected boolean RT_setupModel(List<DevAddr> addrs,StringBuilder failedr)
//	{
//		this.addrs = addrs ;
//		if(this.addrs==null)
//			this.addrs = new ArrayList<DevAddr>(0) ;
//		return true;
//	}
//	
//	/**
//	 * when related driver RT_initDriver success.
//	 * then models depended this driver will be init - this method will be called
//	 * only this method return true,then model RT_runInLoop method be called
//	 *  or model status will display err
//	 * @param failedr
//	 * @return
//	 */
//	protected abstract boolean RT_initModel(StringBuilder failedr) ;
//	
//	/**
//	 * call by driver,and run in loop interval
//	 */
//	protected abstract void RT_runInLoop();
//	
//	
//	protected abstract void RT_endModel() ;
//	
//	public abstract boolean RT_writeVal(DevAddr da,Object v) ;
//	
//	public boolean RT_writeValStr(DevAddr da,String strv)
//	{
//		ValTP tp = da.getValTP();
//		Object v = UAVal.transStr2ObjVal(tp, strv);
//		if(v==null)
//			return false;
//		RT_writeVal(da,v);
//		return true;
//	}
	
}
