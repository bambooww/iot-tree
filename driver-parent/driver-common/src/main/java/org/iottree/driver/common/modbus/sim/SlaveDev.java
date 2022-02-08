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
		return "Addr="+this.devAddr +" Seg Num="+segs.size() ;
	}
	
	public int getDevAddr()
	{
		return devAddr ;
	}
	
	public List<SlaveDevSeg> getSegs()
	{
		return this.segs;
	}
	
	
	public SlaveDevSeg getSegById(String id)
	{
		for(SlaveDevSeg seg:this.segs)
		{
			if(seg.getId().equals(id))
				return seg ;
		}
		return null ;
	}
	
	public void init()
	{
		for(SlaveDevSeg seg:segs)
			seg.init() ;
	}
	
	public boolean RT_init(StringBuilder failedr)
	{
		
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
