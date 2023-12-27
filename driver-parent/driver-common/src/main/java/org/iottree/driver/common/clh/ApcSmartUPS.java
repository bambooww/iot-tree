package org.iottree.driver.common.clh;

import java.util.List;

import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.Convert;
import org.iottree.driver.common.CmdLineDrv;
import org.iottree.driver.common.CmdLineHandler;

public class ApcSmartUPS extends CmdLineHandler
{
	boolean bConnOk = false;
	
	
	private UATag tagTemp = null ;
	
	private UATag tagLowBatteryV = null ;
	private UATag tagUpsOverload = null ;
	private UATag tagUpsUseBattery = null ;
	private UATag tagUpsOnline = null ;
	
	private UATag tagBatteryStdV = null ; // 48
	private UATag tagBatteryV = null ; // dd.dd
	private UATag tagBatteryCap = null ; // dd.dd
	
	
	@Override
	public String getName()
	{
		return "apc_smart_ups";
	}

	@Override
	public String getTitle()
	{
		return "APC Smart UPS";
	}

	@Override
	public String getDesc()
	{
		return "APC Smart UPS RS232";
	}
	
	protected CmdLineHandler copyMe()
	{
		return new ApcSmartUPS() ;
	}

	@Override
	protected int getRecvMaxLen()
	{
		return 100;
	}

	@Override
	public boolean init(CmdLineDrv cld,StringBuilder sb) throws Exception
	{
		super.init(cld, sb) ;
		
		UACh ch = this.belongTo.getBelongToCh() ;
		//this.belongTo.get
		//boolean bdirty = false;
		
		tagTemp = ch.getOrAddTag("temp", "Temperature", "UPS temperature", ValTP.vt_float, false) ;
		
		tagLowBatteryV = ch.getOrAddTag("low_b_v", "Low battery voltage alarm", "Low battery voltage alarm", ValTP.vt_bool, false) ;
		tagUpsOverload = ch.getOrAddTag("st_overload", "UPS Overloaded Running", "Ups Overloaded Running", ValTP.vt_bool, false) ;
		tagUpsUseBattery = ch.getOrAddTag("st_use_battery", "UPS Using battery inverter", "Using battery inverter", ValTP.vt_bool, false) ;
		tagUpsOnline = ch.getOrAddTag("st_online", "UPS Using Online", "Using Online", ValTP.vt_bool, false) ;
		
		tagBatteryStdV = ch.getOrAddTag("battery_stdv", "Battery standard  voltage", "Battery standard  voltage", ValTP.vt_int32, false) ;  ; // 48
		tagBatteryV = ch.getOrAddTag("battery_v", "Battery voltage", "Battery voltage", ValTP.vt_float, false) ; // dd.dd
		tagBatteryCap = ch.getOrAddTag("battery_cap", "Battery capacity", "Battery capacity", ValTP.vt_float, false) ; // dd.dd
		
		if(ch.isDirty())
			ch.save();
		return true;
	}
	
	//replace by channel,not need device.  ch is a device too
	
//	public boolean init(CmdLineDrv cld,StringBuilder sb) throws Exception
//	{
//		super.init(cld, sb) ;
//		
//		UACh ch = this.belongTo.getBelongToCh() ;
//		//this.belongTo.get
//		List<UADev> devs = ch.getDevs() ;
//		boolean bdirty = false;
//		for(UADev dev:devs)
//		{
//			tagTemp = dev.getOrAddTag("temp", "Temperature", "UPS temperature", ValTP.vt_float, false) ;
//			
//			tagLowBatteryV = dev.getOrAddTag("low_b_v", "Low battery voltage alarm", "Low battery voltage alarm", ValTP.vt_bool, false) ;
//			tagUpsOverload = dev.getOrAddTag("st_overload", "UPS Overloaded Running", "Ups Overloaded Running", ValTP.vt_bool, false) ;
//			tagUpsUseBattery = dev.getOrAddTag("st_use_battery", "UPS Using battery inverter", "Using battery inverter", ValTP.vt_bool, false) ;
//			tagUpsOnline = dev.getOrAddTag("st_online", "UPS Using Online", "Using Online", ValTP.vt_bool, false) ;
//			
//			tagBatteryStdV = dev.getOrAddTag("battery_stdv", "Battery standard  voltage", "Battery standard  voltage", ValTP.vt_int32, false) ;  ; // 48
//			tagBatteryV = dev.getOrAddTag("battery_v", "Battery voltage", "Battery voltage", ValTP.vt_float, false) ; // dd.dd
//			tagBatteryCap = dev.getOrAddTag("battery_cap", "Battery capacity", "Battery capacity", ValTP.vt_float, false) ; // dd.dd
//			
//			if(dev.isDirty())
//				bdirty = true;
//		}
//		
//		if(bdirty)
//			ch.save();
//		return true;
//	}

//	@Override
//	public void RT_init() throws Exception
//	{
//		
//	}
	
	boolean bJustConn = false;

	@Override
	public void RT_onConned(ConnPtStream cpt) throws Exception
	{
		super.RT_onConned(cpt);
		bJustConn = true ;
	}

	@Override
	public void RT_onDisconn(ConnPtStream cpt) throws Exception
	{
		super.RT_onDisconn(cpt);
		
		bConnOk = false;
	}
	
	protected boolean RT_useNoWait()
	{
		return false;
	}

	@Override
	public void RT_runInLoop(ConnPtStream cpt) throws Exception
	{
		if(bJustConn)
		{
			
			this.sendRecvSyn("Y\r\n", (bsucc,ret,error)->{ //temp
				if(bsucc)
				{
					bJustConn = false;
					bConnOk = "SM\r\n".equals(ret) ;
				}
				else
				{
					this.belongTo.RT_fireDrvWarn("Device has not SM response") ;
				}
			});
			return ;
		}
		if(!bConnOk)
			return ;

		this.sendRecvSyn("Q\r\n", (bsucc,ret,error)->{ //temp
			if(bsucc)
			{
				try
				{
					int intv = Integer.parseInt(ret.trim()) ;
					boolean low_b = (intv & (1<<6)) > 0 ;
					boolean overload = (intv & (1<<5)) > 0 ;
					boolean use_b = (intv & (1<<4)) > 0 ;
					boolean use_online = (intv & (1<<3)) > 0 ;
					this.tagLowBatteryV.RT_setValRaw(low_b) ;
					this.tagUpsOverload.RT_setValRaw(overload) ;
					this.tagUpsUseBattery.RT_setValRaw(use_b) ;
					this.tagUpsOnline.RT_setValRaw(use_online) ;
				}
				catch(Exception eee)
				{
					String err = "reponse error:"+eee.getMessage() ;
					this.tagLowBatteryV.RT_setValErr(err) ;
					this.tagUpsOverload.RT_setValErr(err) ;
					this.tagUpsUseBattery.RT_setValErr(err) ;
					this.tagUpsOnline.RT_setValErr(err) ;
				}
			}
			else
			{
				String err = "reponse error:"+error ;
				this.tagLowBatteryV.RT_setValErr(err) ;
				this.tagUpsOverload.RT_setValErr(err) ;
				this.tagUpsUseBattery.RT_setValErr(err) ;
				this.tagUpsOnline.RT_setValErr(err) ;
			}
		});
		
		sendCmdSyn(tagTemp,"C","C temp") ;
		sendCmdSyn(tagBatteryStdV,"g","Battery standard voltage") ;
		sendCmdSyn(tagBatteryV,"B","Battery voltage") ;
		sendCmdSyn(tagBatteryCap,"f","Battery capacity") ;

		
	}
	
	private void sendCmdSyn(UATag tag,String cmd,String title) throws Exception
	{
		this.sendRecvSyn(cmd+"\r\n", (bsucc,ret,error)->{ //temp
			if(bsucc)
			{
				try
				{
					ret = ret.trim() ;
					ValTP vt = tag.getValTp() ;
					Object v = null ;
					if(vt==ValTP.vt_bool)
						v = "true".equalsIgnoreCase(ret) || "1".equals(ret) ;
					else if(vt==ValTP.vt_float)
						v = Float.parseFloat(ret) ;
					else if(vt==ValTP.vt_int32)
						v = Integer.parseInt(ret);
					if(v!=null)
						tag.RT_setValRaw(v) ;
				}
				catch(Exception eee)
				{
					//this.belongTo.RT_fireDrvWarn("sendRecvSyn C err:"+eee.getMessage()) ;
					tag.RT_setValErr("Response "+title+" err:"+eee.getMessage(), eee);
				}
			}
			else
			{
				tag.RT_setValErr("Response "+title+" error:"+error, null);
				//this.belongTo.RT_fireDrvWarn("Device has not C temp response") ;
			}
		});
	}

	@Override
	public void RT_onRecved(String cmd)
	{
		if(Convert.isNullOrEmpty(cmd))
			return ;
		System.out.println("on recv cmd="+cmd) ;
		cmd = cmd.trim() ;
		switch(cmd)
		{
		case "%":
			tagLowBatteryV.RT_setValRaw(true) ;
			break ;
		case "!":
			break ;
		}
	}
}
