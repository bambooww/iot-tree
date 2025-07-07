package org.iottree.driver.omron.hostlink.fins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.DevAddr;
import org.iottree.core.DevDef;
import org.iottree.core.UADev;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.driver.omron.hostlink.HLAddr;
import org.iottree.driver.omron.hostlink.HLAddrSeg;
import org.iottree.driver.omron.hostlink.HLDriver;
import org.iottree.driver.omron.hostlink.HLModel;

public class HLDevItem
{
	HLFinsDriver driver = null ;
	
	private transient UADev uaDev = null ;
	private transient DevDef devDef = null ;
	
	//short devAddr = 2 ;
	
	//String ppiAddr = "QB0" ;
	
	short readNum = 1 ;
	
	HashMap<HLAddrSeg,HLBlock> seg2block = new HashMap<>() ;
	
	private transient List<HLAddr> fxAddrs = new ArrayList<>() ;
	
//	/**
//	 * true = net false=serial
//	 */
//	boolean bNetTcp = true ; 
	
	public HLDevItem(HLFinsDriver drv,UADev dev) //,boolean b_net_tcp)
	{
		driver = drv ;
		uaDev = dev ;
		//bNetTcp =drv instanceof b_net_tcp ;
	}
	
	public boolean isNetTcp()
	{
		return this.driver instanceof HLFinsDriverNet ;
	}
	
	public boolean isNetUdp()
	{
		return this.driver instanceof HLFinsDriverUDP ;
	}
	
	UADev getUADev()
	{
		return uaDev ;
	}
	

//	private List<HLAddr> filterAndSortAddrs(PPIMemTp tp)
//	{
//		ArrayList<HLAddr> r = new ArrayList<>() ;
//		for(HLAddr ma:this.ppiAddrs)
//		{
//			if(ma.getMemTp()==tp)
//				r.add(ma);
//		}
//		Collections.sort(r);
//		return r ;
//	}
	
	public boolean init(StringBuilder failedr)
	{
		List<DevAddr> addrs = uaDev.listTagsAddrAll() ;
		if(addrs==null||addrs.size()<=0)
		{
			failedr.append("no access addresses found") ;
			return false;
		}
		List<HLAddr> tmpads = new ArrayList<>() ;
		for(DevAddr d:addrs)
		{
			tmpads.add((HLAddr)d) ;
		}
		fxAddrs= tmpads ;
		
		
		//int failAfterSuccessive = uaDev.getOrDefaultPropValueInt("timing", "failed_tryn", 3);
		
		int blocksize_bit = 64;
		int blocksize_word = 32;
		if(devDef!=null)
		{
			blocksize_word = devDef.getOrDefaultPropValueInt("block_size", "word", 32);
			blocksize_bit = devDef.getOrDefaultPropValueInt("block_size", "bit", 64);
		}
		if(blocksize_bit<=0)
			blocksize_bit=64;
		if(blocksize_word<=0)
			blocksize_word=32;
		
		long reqto = uaDev.getOrDefaultPropValueLong("timing", "req_to", 100) ;;//devDef.getPropValueLong("timing", "req_to", 1000) ;
		int failed_tryn = uaDev.getOrDefaultPropValueInt("timing", "failed_tryn", 3) ;
		long inter_ms = uaDev.getOrDefaultPropValueLong("timing", "inter_req", 100) ;
		
		HLModel fx_m = (HLModel)uaDev.getDrvDevModel() ;
		
		
		//create cmd and address mapping
		for(String prefix:fx_m.listPrefix())
		{
			List<HLAddr> fxaddrs = filterAndSortAddrs(prefix) ;
			if(fxaddrs==null||fxaddrs.size()<=0)
				continue ;
			
			HashMap<HLAddrSeg,List<HLAddr>> seg2addrs  =fx_m.filterAndSortAddrs(prefix,fxaddrs);
			if(seg2addrs==null||seg2addrs.size()<=0)
				continue ;
			
			for(Map.Entry<HLAddrSeg, List<HLAddr>> seg2ads:seg2addrs.entrySet())
			{
				HLAddrSeg seg = seg2ads.getKey();
				HLBlock blk = null;
				if(seg.isValBitOnly())
					blk = new HLBlock(this,seg,seg2ads.getValue(),blocksize_bit,inter_ms,failed_tryn);
				else
					blk = new HLBlock(this,seg,seg2ads.getValue(),blocksize_word,inter_ms,failed_tryn);
				blk.prefix = prefix ;
				//blk.setTimingParam(reqto, recvto, inter_ms);
				blk.setTimingParam(reqto, inter_ms);
				if(blk.initCmds(driver))
					seg2block.put(seg2ads.getKey(),blk) ;
			}

		}

		return true;
	}
	

	private List<HLAddr> filterAndSortAddrs(String prefix)
	{
		ArrayList<HLAddr> r = new ArrayList<>() ;
		for(HLAddr ma:this.fxAddrs)
		{
			if(prefix.equals(ma.getPrefix()))
				r.add(ma);
		}
		Collections.sort(r);
		return r ;
	}
	
	
	public boolean doCmd(ConnPtStream ep,StringBuilder failedr)  throws Exception
	{
		for(HLBlock blk:seg2block.values())
		{
			if(!blk.runCmds(ep,failedr))
				return false;
		}

		return true;
	}
	
	public boolean RT_writeVal(DevAddr da, Object v)
	{
		HLAddr ma = (HLAddr)da ;
		HLBlock blk = seg2block.get(ma.getAddrSeg());
		if(blk==null)
			return false;

		return blk.setWriteCmdAsyn(ma,v) ;
	}
}
