package org.iottree.driver.mitsubishi.fx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.DevAddr;
import org.iottree.core.DevDef;
import org.iottree.core.UADev;
import org.iottree.core.conn.ConnPtStream;

public class FxDevItem
{
	FxDriver fxDrv = null ;
	
	private transient UADev uaDev = null ;
	private transient DevDef devDef = null ;
	
	//short devAddr = 2 ;
	
	//String ppiAddr = "QB0" ;
	
	short readNum = 1 ;
	
	HashMap<FxAddrSeg,FxBlock> seg2block = new HashMap<>() ;
	
	private transient List<FxAddr> fxAddrs = new ArrayList<>() ;
	
	public FxDevItem(FxDriver drv,UADev dev)
	{
		fxDrv = drv ;
		uaDev = dev ;
	}
	
	UADev getUADev()
	{
		return uaDev ;
	}
	

//	private List<FxAddr> filterAndSortAddrs(PPIMemTp tp)
//	{
//		ArrayList<FxAddr> r = new ArrayList<>() ;
//		for(FxAddr ma:this.ppiAddrs)
//		{
//			if(ma.getMemTp()==tp)
//				r.add(ma);
//		}
//		Collections.sort(r);
//		return r ;
//	}
	
	boolean init(StringBuilder failedr)
	{
		List<DevAddr> addrs = uaDev.listTagsAddrAll() ;
		if(addrs==null||addrs.size()<=0)
		{
			failedr.append("no access addresses found") ;
			return false;
		}
		List<FxAddr> tmpads = new ArrayList<>() ;
		for(DevAddr d:addrs)
		{
			tmpads.add((FxAddr)d) ;
		}
		fxAddrs= tmpads ;
		
		
		int failAfterSuccessive = uaDev.getOrDefaultPropValueInt("timing", "failed_tryn", 3);
		
		int blocksize = 32;
		if(devDef!=null)
			blocksize = devDef.getOrDefaultPropValueInt("block_size", "out_coils", 32);//uaDev.getPropValueLong("block_size", "out_coils", 32);
		if(blocksize<=0)
			blocksize=32;
		
		long reqto = uaDev.getOrDefaultPropValueLong("timing", "req_to", 100) ;;//devDef.getPropValueLong("timing", "req_to", 1000) ;
		long recvto = uaDev.getOrDefaultPropValueLong("timing", "recv_to", 200) ;
		long inter_ms = uaDev.getOrDefaultPropValueLong("timing", "inter_req", 100) ;
		
		FxModel fx_m = (FxModel)uaDev.getDrvDevModel() ;
		
		
		//create cmd and address mapping
		for(String prefix:fx_m.listPrefix())
		{
			List<FxAddr> fxaddrs = filterAndSortAddrs(prefix) ;
			if(fxaddrs==null||fxaddrs.size()<=0)
				continue ;
			
			HashMap<FxAddrSeg,List<FxAddr>> seg2addrs  =fx_m.filterAndSortAddrs(prefix,fxaddrs);
			if(seg2addrs==null||seg2addrs.size()<=0)
				continue ;
			
			for(Map.Entry<FxAddrSeg, List<FxAddr>> seg2ads:seg2addrs.entrySet())
			{
				FxBlock blk = new FxBlock(seg2ads.getValue(),blocksize,inter_ms);
				blk.setTimingParam(reqto, recvto, inter_ms);
				if(blk.initCmds(fxDrv))
					seg2block.put(seg2ads.getKey(),blk) ;
			}
			
//			FxAddrSeg aseg = null ;
//			ArrayList<FxAddr> ads = null ;
//			for(FxAddr faddr:fxaddrs)
//			{
//				if(faddr.addrSeg!=aseg)
//				{
//					if(ads!=null && ads.size()>0)
//					{
//						FxBlock blk = new FxBlock(ads,blocksize,inter_ms);
//						blk.setTimingParam(reqto, recvto, inter_ms);
//						if(blk.initCmds(fxDrv))
//							seg2block.put(aseg,blk) ;
//					}
//					ads = new ArrayList<>() ;
//					aseg = faddr.addrSeg ;
//				}
//				
//				ads.add(faddr) ;
//			}
//			
//			if(ads!=null&&ads.size()>0)
//			{
//				FxBlock blk = new FxBlock(ads,blocksize,inter_ms);
//				blk.setTimingParam(reqto, recvto, inter_ms);
//				if(blk.initCmds(fxDrv))
//					seg2block.put(aseg,blk) ;
//			}
		}

		return true;
	}
	

	private List<FxAddr> filterAndSortAddrs(String prefix)
	{
		ArrayList<FxAddr> r = new ArrayList<>() ;
		for(FxAddr ma:this.fxAddrs)
		{
			if(prefix.equals(ma.prefix))
				r.add(ma);
		}
		Collections.sort(r);
		return r ;
	}
	
	
	boolean doCmd(ConnPtStream ep)  throws Exception
	{
		for(FxBlock blk:seg2block.values())
		{
			blk.runCmds(ep);
		}

		return true;
	}
	
	public boolean RT_writeVal(DevAddr da, Object v)
	{
		FxAddr ma = (FxAddr)da ;
		FxBlock blk = seg2block.get(ma.addrSeg);
		if(blk==null)
			return false;

		return blk.setWriteCmdAsyn(ma,v) ;
	}
}
