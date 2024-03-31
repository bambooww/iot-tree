package org.iottree.driver.omron.fins;

import org.iottree.core.util.ILang;

/**
 * doc page 177
 * 
 * @author jason.zhu
 *
 */
public class FinsEndCode implements ILang
{
	int mainCode ;
	
	boolean networkRelayErr = false;
	
	int subCode ;
	
	boolean fatalCPUUnitErr = false;
	boolean nonfatalCPUUnitError = false;
	
//	String mainTitle ;
//	
//	String subTitle ;
//	
//	String chkPoint ;
//	
//	String probableCause ;
//	
//	String correction ;
	
	public FinsEndCode(short main,short sub)
	{
		this.mainCode = main & 0x7F ;
		networkRelayErr = (main & 0x80) > 0 ;
		
		this.subCode = sub & 0x3F ;
		fatalCPUUnitErr = (sub &0x80)>0 ;
		nonfatalCPUUnitError = (sub&0x40)>0 ;
	}
	
	public boolean isNormal()
	{
		return this.mainCode == 0 && this.subCode==0 ;
	}
	
	public int getMainCode()
	{
		return mainCode ;
	}
	
	public boolean isNetworkRelayErr()
	{
		return this.networkRelayErr ;
	}
	
	public int getSubCode()
	{
		return this.subCode ;
	}
	
	public boolean isFatalCPUUnitErr()
	{
		return this.fatalCPUUnitErr ;
	}
	
	public boolean isNonfatalCPUUnitErr()
	{
		return this.nonfatalCPUUnitError ;
	}
	
	public String getMainTitle()
	{
		return g("endc_"+mainCode) ;
	}
	
	public String getSubTitle()
	{
		return g("endc_"+mainCode+"_"+subCode) ;
	}
	
	public String getCheckPoint()
	{
		return g("endc_"+mainCode+"_"+subCode,"chkpt","") ;
	}
	
	public String getProbableCause()
	{
		return g("endc_"+mainCode+"_"+subCode,"cause","") ;
	}
	
	public String getCorrection()
	{
		return g("endc_"+mainCode+"_"+subCode,"correction","") ;
	}
	
	public String getErrorInf()
	{
		return mainCode+" "+subCode ;
	}
}
