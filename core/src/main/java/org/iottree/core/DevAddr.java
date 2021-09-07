package org.iottree.core;

import java.util.List;

import org.iottree.core.UAVal.ValTP;

/**
 * for every defferent device driver,each driver may has it's own addr rule.
 * e.g: 400001  A001 Q0.1
 * address rule may infer data type. bool,int8 int16 etc.
 * and other address may has some function or script to
 * 
 * so,it has a abstract class and driver may override it,to support it's own
 * @author zzj
 *
 */
public abstract class DevAddr
{
	String addr = null ;
	ValTP valTP = null ;
	
	
	UATag belongTo = null ;
	/**
	 * for config load
	 */
	public DevAddr()
	{}
	
	public DevAddr(String addr,ValTP vtp)
	{
		this.addr = addr ;
		this.valTP = vtp ;
	}
	
	public String getAddr()
	{
		return addr ;
	}
	
	public UATag getBelongTo()
	{
		return belongTo ;
	}
	/**
	 * 
	 * @param str
	 * @return failed return null,and may fill failedr reson desc
	 */
	public abstract DevAddr parseAddr(String str,ValTP vtp, StringBuilder failedr) ;
	
	public abstract boolean isSupportGuessAddr() ;
	
	/**
	 * support make addr fit to correct automatically
	 * @param str
	 * @return
	 */
	public abstract DevAddr guessAddr(String str) ;
	
	public abstract List<String> listAddrHelpers() ;
	
	public abstract UAVal.ValTP[] getSupportValTPs() ;
	
	public final ValTP getValTP()
	{
		return valTP ;
	}
	
//	public abstract int getRegPos();
//	
//	public abstract int getBitPos() ;
	
	public abstract boolean canRead() ;
	
	public abstract boolean canWrite() ;
	
	
	
	//public abstract int getAddrBitNum() ;
	
	//public abstract int getAddrByteNum() ;
	//---------------rt run
	
//	private transient Object rtVal = null ;
//	
//	private transient long lastValDT= System.currentTimeMillis() ;
//	
//	/**
//	 * quality
//	 */
//	private transient boolean lastQT= false ;
	
	private transient UAVal uaVal = new UAVal() ;
	
	public UAVal RT_getVal()
	{
		return uaVal ;
	}
	
	public long RT_getValDT()
	{
		return uaVal.getValDT() ;
	}
	
	public Object RT_getValObj()
	{
		return uaVal.getObjVal();
	}
	
	public boolean RT_getValQT()
	{
		return uaVal.isValid() ;
	}
	
	public void RT_setVal(Object v)
	{
		RT_setVal(v,true) ;
	}
	
	public void RT_setVal(Object v,boolean chk_chg)
	{
		if(chk_chg)
		{
			if(v==null)
			{//
				if(!uaVal.isValid())
					return ;
			}
			else if(uaVal.isValid() && v.equals(uaVal.getObjVal()))
			{
				return ;
			}
		}
		
//		uaVal.objVal = v ;
//		uaVal.valDT=System.currentTimeMillis() ;
//		uaVal.bValid=true;
		if(v==null)
			uaVal.setVal(false, null, System.currentTimeMillis());
		else
			uaVal.setVal(true, v, System.currentTimeMillis());
		
		//set his
		this.getBelongTo().RT_setUAVal(uaVal);
	}
}
