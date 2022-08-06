package org.iottree.driver.s7.ppi;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.DevDef;
import org.iottree.core.UADev;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.Convert;
import org.iottree.driver.common.ModbusAddr;
import org.iottree.driver.common.modbus.ModbusBlock;

public class PPIDevItem
{
	PPIDriver ppiDrv = null ;
	
	private transient UADev uaDev = null ;
	private transient DevDef devDef = null ;
	
	//short devAddr = 2 ;
	
	//String ppiAddr = "QB0" ;
	
	short readNum = 1 ;
	
	HashMap<PPIMemTp,PPIBlock> tp2block = new HashMap<>() ;
	
	private transient List<PPIAddr> ppiAddrs = new ArrayList<>() ;
	
	public PPIDevItem(PPIDriver drv,UADev dev)
	{
		ppiDrv = drv ;
		uaDev = dev ;
	}
	
	UADev getUADev()
	{
		return uaDev ;
	}
	

	private List<PPIAddr> filterAndSortAddrs(PPIMemTp tp)
	{
		ArrayList<PPIAddr> r = new ArrayList<>() ;
		for(PPIAddr ma:this.ppiAddrs)
		{
			if(ma.getMemTp()==tp)
				r.add(ma);
		}
		Collections.sort(r);
		return r ;
	}
	
	boolean init(StringBuilder failedr)
	{
		List<DevAddr> addrs = uaDev.listTagsAddrAll() ;
		if(addrs==null||addrs.size()<=0)
		{
			failedr.append("no access addresses found") ;
			return false;
		}
		List<PPIAddr> tmpads = new ArrayList<>() ;
		for(DevAddr d:addrs)
		{
			tmpads.add((PPIAddr)d) ;
		}
		ppiAddrs= tmpads ;
		
		short devid = (short)uaDev.getOrDefaultPropValueLong("ppi_spk", "dev_addr", 1);
		//int devid = Integer.parseInt(uaDev.getId());
		
		int failAfterSuccessive = uaDev.getOrDefaultPropValueInt("timing", "failed_tryn", 3);
		
		int blocksize = 32;
		if(devDef!=null)
			blocksize = devDef.getOrDefaultPropValueInt("block_size", "out_coils", 32);//uaDev.getPropValueLong("block_size", "out_coils", 32);
		if(blocksize<=0)
			blocksize=32;
		
		long reqto = uaDev.getOrDefaultPropValueLong("timing", "req_to", 100) ;;//devDef.getPropValueLong("timing", "req_to", 1000) ;
		long recvto = uaDev.getOrDefaultPropValueLong("timing", "recv_to", 200) ;
		long inter_ms = uaDev.getOrDefaultPropValueLong("timing", "inter_req", 100) ;
		
		
		//create modbus cmd and address mapping
		for(PPIMemTp mtp:PPIMemTp.values())
		{
			List<PPIAddr> ppiaddrs = filterAndSortAddrs(mtp) ;
			if(ppiaddrs==null||ppiaddrs.size()<=0)
				continue ;
			
			PPIBlock blk = new PPIBlock(devid,mtp,ppiaddrs,blocksize,inter_ms);
			blk.setTimingParam(reqto, recvto, inter_ms);
			if(blk.initCmds(ppiDrv))
				tp2block.put(mtp,blk) ;
		}

		return true;
	}
	
	boolean doCmd(ConnPtStream ep)  throws Exception
	{
		for(PPIBlock blk:tp2block.values())
		{
			blk.runCmds(ep);
		}
		
//		PPICmd ppic = new PPICmd((short)2,"QB0",(short)3) ;
//		ppic.RT_init(ppiDrv);
//		ppic.doCmd_Test(ep) ;
		return true;
	}
	
	public boolean RT_writeVal(DevAddr da, Object v)
	{
		PPIAddr ma = (PPIAddr)da ;
		PPIBlock blk = tp2block.get(ma.getMemTp());
		if(blk==null)
			return false;
//		int intv ;
//		if(v instanceof Number)
//			intv = ((Number)v).intValue() ;
//		else
//			intv = Integer.parseInt(v+"");
		return blk.setWriteCmdAsyn(ma,v) ;
	}
}
