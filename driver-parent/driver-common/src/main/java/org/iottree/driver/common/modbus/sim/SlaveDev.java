package org.iottree.driver.common.modbus.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.DevDef;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.sim.SimDev;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.util.xmldata.data_val;
import org.iottree.driver.common.ModbusAddr;
import org.iottree.driver.common.modbus.ModbusBlock;

/**
 * according to Device Lib DevDef,it create a slave device item
 * it will be used as Modbus Slave
 * 
 * @author jason.zhu
 */
@data_class
public class SlaveDev extends SimDev// implements IXmlDataable
{
	
	private transient List<ModbusAddr> maddrs = new ArrayList<>() ;
	
	ModbusBlock mbCoilIn = null ;
	ModbusBlock mbCoilOut = null ;
	ModbusBlock mbRegIn = null ;
	ModbusBlock mbRegHold = null ;
	
	@data_val(param_name = "dev_addr")
	int devAddr = 1 ;
	
	@data_obj(obj_c = SlaveDevSeg.class, param_name = "segs")
	List<SlaveDevSeg> segs = new ArrayList<>();
	
	public SlaveDev()
	{
		
	}
	
	public String getDevTitle()
	{
		return "Addr="+this.devAddr ;
	}
	
	public int getDevAddr()
	{
		return devAddr ;
	}
	
	public List<SlaveDevSeg> getSegs()
	{
		return this.segs;
	}
	
	public boolean RT_init(StringBuilder failedr)
	{
		List<ModbusAddr> tmpads = new ArrayList<>() ;
		StringBuilder sb = new StringBuilder() ;
		List<UATag> tags = this.getTags() ;
		for(UATag tag:tags)
		{
				if (tag.isMidExpress()||tag.isLocalTag())
					continue;
				String str = tag.getAddress() ;
				UAVal.ValTP vtp = tag.getValTpRaw() ;
				ModbusAddr ma = ModbusAddr.parseModbusAddr(str, vtp, sb) ;
				if(ma==null)
					continue ;
				tmpads.add(ma);
		}
		maddrs = tmpads ;
		
		List<ModbusAddr> coil_in_addrs = filterAndSortAddrs(ModbusAddr.COIL_INPUT) ;
		List<ModbusAddr> coil_out_addrs = filterAndSortAddrs(ModbusAddr.COIL_OUTPUT) ;
		List<ModbusAddr> reg_input_addrs = filterAndSortAddrs(ModbusAddr.REG_INPUT) ;
		List<ModbusAddr> reg_hold_addrs = filterAndSortAddrs(ModbusAddr.REG_HOLD) ;
		
		if(coil_in_addrs.size()>0)
		{
			ModbusBlock mb = new ModbusBlock(devAddr,ModbusAddr.COIL_INPUT,coil_in_addrs,
					32,100,0);
			if(mb.initReadCmds())
			{
				mbCoilIn = mb;
				mb.initAsSlave();
			}
		}
		if(coil_out_addrs.size()>0)
		{
			ModbusBlock mb = new ModbusBlock(devAddr,ModbusAddr.COIL_OUTPUT,coil_out_addrs,
					32,100,0);
			if(mb.initReadCmds())
			{
				mbCoilOut = mb;
				mb.initAsSlave();
			}
		}
		if(reg_input_addrs.size()>0)
		{
			ModbusBlock mb = new ModbusBlock(devAddr,ModbusAddr.REG_INPUT,reg_input_addrs,
					32,100,0);
			if(mb.initReadCmds())
			{
				mbRegIn = mb;
				mb.initAsSlave();
			}
		}
		if(reg_hold_addrs.size()>0)
		{
			ModbusBlock mb = new ModbusBlock(devAddr,ModbusAddr.REG_HOLD,reg_hold_addrs,
					32,100,0);
			if(mb.initReadCmds())
			{
				mbRegHold = mb;
				mb.initAsSlave();
			}
		}
		
		return true;
	}
	
	private List<ModbusAddr> filterAndSortAddrs(short addrtp)
	{
		ArrayList<ModbusAddr> r = new ArrayList<>() ;
		for(ModbusAddr ma:this.maddrs)
		{
			if(ma.getAddrTp()==addrtp)
				r.add(ma);
		}
		Collections.sort(r);
		return r ;
	}

}
