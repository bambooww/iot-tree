package org.iottree.driver.omron.fins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.DevAddr;
import org.iottree.core.DevDef;
import org.iottree.core.UADev;
import org.iottree.core.conn.ConnPtStream;

public class FinsDevItem
{
	FinsDriver driver = null ;
	
	private transient UADev uaDev = null ;
	private transient DevDef devDef = null ;
	
	//short devAddr = 2 ;
	
	//String ppiAddr = "QB0" ;
	
	short readNum = 1 ;
	
	HashMap<FinsAddrSeg,FinsBlock> seg2block = new HashMap<>() ;
	
	private transient List<FinsAddr> fxAddrs = new ArrayList<>() ;
	
//	/**
//	 * true = net false=serial
//	 */
//	boolean bNetTcp = true ; 
	
	public FinsDevItem(FinsDriver drv,UADev dev) //,boolean b_net_tcp)
	{
		driver = drv ;
		uaDev = dev ;
		//bNetTcp =drv instanceof b_net_tcp ;
	}
	
	public boolean isNetTcp()
	{
		return this.driver instanceof FinsDriverEthTCP ;
	}
	
	public boolean isNetUdp()
	{
		//return this.driver instanceof FinsDriverUDP ;
		return false;
	}
	
	public UADev getUADev()
	{
		return uaDev ;
	}
	

//	private List<FinsAddr> filterAndSortAddrs(PPIMemTp tp)
//	{
//		ArrayList<FinsAddr> r = new ArrayList<>() ;
//		for(FinsAddr ma:this.ppiAddrs)
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
		List<FinsAddr> tmpads = new ArrayList<>() ;
		for(DevAddr d:addrs)
		{
			tmpads.add((FinsAddr)d) ;
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
		
		FinsModel fx_m = (FinsModel)uaDev.getDrvDevModel() ;
		
		
		//create cmd and address mapping
		for(String prefix:fx_m.listPrefix())
		{
			List<FinsAddr> fxaddrs = filterAndSortAddrs(prefix) ;
			if(fxaddrs==null||fxaddrs.size()<=0)
				continue ;
			
			HashMap<FinsAddrSeg,List<FinsAddr>> seg2addrs  =fx_m.filterAndSortAddrs(prefix,fxaddrs);
			if(seg2addrs==null||seg2addrs.size()<=0)
				continue ;
			
			for(Map.Entry<FinsAddrSeg, List<FinsAddr>> seg2ads:seg2addrs.entrySet())
			{
				FinsAddrSeg seg = seg2ads.getKey();
				FinsBlock blk = null;
				if(seg.isValBitOnly())
					blk = new FinsBlock(this,seg,seg2ads.getValue(),blocksize_bit,inter_ms,failed_tryn);
				else
					blk = new FinsBlock(this,seg,seg2ads.getValue(),blocksize_word,inter_ms,failed_tryn);
				blk.prefix = prefix ;
				//blk.setTimingParam(reqto, recvto, inter_ms);
				blk.setTimingParam(reqto, inter_ms);
				if(blk.initCmds(driver))
					seg2block.put(seg2ads.getKey(),blk) ;
			}

		}

		return true;
	}
	

	private List<FinsAddr> filterAndSortAddrs(String prefix)
	{
		ArrayList<FinsAddr> r = new ArrayList<>() ;
		for(FinsAddr ma:this.fxAddrs)
		{
			if(prefix.equals(ma.getPrefix()))
				r.add(ma);
		}
		Collections.sort(r);
		return r ;
	}
	
	
	public boolean doCmd(ConnPtStream ep,StringBuilder failedr)  throws Exception
	{
		for(FinsBlock blk:seg2block.values())
		{
			if(!blk.runCmds(ep,failedr))
				return false;
		}

		return true;
	}
	
	public boolean RT_writeVal(DevAddr da, Object v)
	{
		FinsAddr ma = (FinsAddr)da ;
		FinsBlock blk = seg2block.get(ma.getAddrSeg());
		if(blk==null)
			return false;

		return blk.setWriteCmdAsyn(ma,v) ;
	}
}
