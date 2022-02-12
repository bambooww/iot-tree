package org.iottree.driver.common.modbus.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.DevDef;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.sim.SimDev;
import org.iottree.core.sim.SimTag;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
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
	
	protected List<SimTag> listSimTagsInner()
	{
		
		ArrayList<SimTag> rets = new ArrayList<>() ;
		for(SlaveDevSeg seg:segs)
		{
			List<SlaveTag> tags = seg.getSlaveTags() ;
			if(tags==null)
				continue ;
			for(SimTag t:tags)
			{
				SlaveTag st = (SlaveTag)t ;
				st.relatedSeg = seg ;
			}
			rets.addAll(tags) ;
		}
		
		return rets ;
	}
	
	
	public SlaveTag findTagBySegIdx(String segid,int segidx)
	{
		for(SlaveDevSeg seg:segs)
		{
			if(seg.getId().equals(segid))
			{
				List<SlaveTag> tags = seg.getSlaveTags() ;
				if(tags==null)
					return null ;
				for(SlaveTag st:tags)
				{
					if(segidx==st.getRegIdx())
						return st ;
				}
			}
		}
		return null ;
	}
	
	public SlaveTag setTag(String segid,int regidx,String name) throws Exception
	{
		SlaveDevSeg seg = this.getSegById(segid) ;
		if(seg==null)
			return null ;
		
		if(Convert.isNullOrEmpty(name))
		{
			return seg.removeSlaveTag(regidx) ;
		}
		
		StringBuilder failedr = new StringBuilder() ;
		if(!Convert.checkVarName(name, true, failedr))
			throw new Exception(failedr.toString()) ;
		
		SlaveTag st = (SlaveTag)this.getSimTagByName(name) ;
		if(st!=null)
		{
			if(st.getRegIdx()!=regidx||!st.getRelatedSeg().getId().equals(segid))
				throw new Exception("tag name ["+name+"] is existed!") ;
			
			st.asName(name) ;
			clearBuffer();
			return st ;
		}

		
		
		st = seg.setSlaveTag(regidx, name) ;
		
		clearBuffer();
		return st ; 
	}
	

	public SimDev asDevTags(List<UATag> tags)
	{
		//this.devId = devid ;
		
		for(UATag uat:tags)
		{
			//this.tags = tags ;
		}
		
		return this ;
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
