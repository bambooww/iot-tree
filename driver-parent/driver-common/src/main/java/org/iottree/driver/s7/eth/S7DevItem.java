package org.iottree.driver.s7.eth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.DevDef;
import org.iottree.core.DevDriver;
import org.iottree.core.UADev;
import org.iottree.core.UANode;
import org.iottree.core.UATag;
import org.iottree.core.UATagG;
import org.iottree.core.conn.ConnPtStream;


public class S7DevItem
{
	static class AddrsG
	{
		UATagG tagg = null ; //first TagG under UADev
		
		List<S7Addr> s7addrs = null ;
		
		HashMap<String,S7Block> tp2block = new HashMap<>() ;
		
		AddrsG(UATagG tagg,List<S7Addr> addrs)
		{
			this.tagg = tagg ;
			this.s7addrs = addrs ;
		}
		
		boolean chkRelatedTagG(UATag tag)
		{
			if(tagg==null)
				return false;
			UANode pn = tag;
			do
			{
				pn = pn.getParentNode() ;
				if(pn==tagg)
					return true ;
			}
			while(pn!=null) ;
			return false;
		}
	}
	
	S7EthDriver s7Drv = null ;
	
	private transient UADev uaDev = null ;
	private transient DevDef devDef = null ;

	short readNum = 1 ;
	
	/**
	 * tp2block
	 * key = dbxxx  or other memtp
	 */
	HashMap<String,S7Block> tp2block = new HashMap<>() ;
	
	private transient List<S7Addr> s7Addrs = new ArrayList<>() ;
	
	//AddrsG defAddrsG = null ;
	
	//HashMap<UATagG,AddrsG> tagg2ag = null ;
	
	List<AddrsG> addrsGS = null ;
	
	private long reqIntvMS = 20 ;
	
	public S7DevItem(S7EthDriver drv,UADev dev)
	{
		s7Drv = drv ;
		uaDev = dev ;
	}
	
	UADev getUADev()
	{
		return uaDev ;
	}
	
	
	private static HashSet<Integer> searchAddrsDBNums(List<S7Addr> addrs)
	{
		HashSet<Integer> rets = new HashSet<>() ;
		for(S7Addr ma:addrs)
		{
			if(ma.getMemTp()==S7MemTp.DB)
			{
				int dbn = ma.getDBNum() ;
				rets.add(dbn) ;
			}
		}
		return rets ;
	}

	private static List<S7Addr> filterAndSortAddrs(List<S7Addr> addrs,S7MemTp tp,int db_num)
	{
		ArrayList<S7Addr> r = new ArrayList<>() ;
		for(S7Addr ma:addrs)
		{
			if(ma.chkSameArea(tp,db_num))
				r.add(ma);
		}
		Collections.sort(r);
		return r ;
	}
	

	private static void initAddrs(HashMap<String,S7Block> tp2blk,S7EthDriver drv,List<S7Addr> addrs,S7MemTp mtp,int dbnum,int block_size,long read_inter_ms)
	{
		addrs = filterAndSortAddrs(addrs,mtp,dbnum) ;
		if(addrs==null||addrs.size()<=0)
			return ;
		
		String areakey = addrs.get(0).getAreaKey();
		S7Block blk = new S7Block(addrs,block_size,read_inter_ms);
		//blk.setTimingParam(reqto, recvto, inter_ms);
		if(blk.initCmds(drv))
			tp2blk.put(areakey,blk) ;
	}
	
	private static List<S7Addr> extractAddrs(List<UATag> tags)
	{
		if(tags==null)
			return null ;
		ArrayList<S7Addr> addrs = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (UATag tag : tags)
		{
			if (tag.isMidExpress()||tag.isLocalTag())
				continue;
			DevAddr da = tag.getDevAddr(sb);
			if (da == null || !(da instanceof S7Addr))
				continue;
			addrs.add((S7Addr)da);
		}
		return addrs;
	}
	
	private static List<AddrsG> listAddrsG(UADev dev)
	{
		ArrayList<AddrsG> rets = new ArrayList<>() ;
		rets.add(new AddrsG(null,new ArrayList<>())) ;
		
		List<UATagG> taggs = dev.getSubTagGs() ;
		HashSet<UATag> ignore_tags = new HashSet<>() ;
		if(taggs!=null)
		{
			for(UATagG tagg:taggs)
			{
				if(!tagg.isReadIntvEnabled())
					continue ;
				
				List<UATag> tags = tagg.listTagsAll() ;
				if(tags==null||tags.size()<=0)
					continue ;
				List<S7Addr> addrs = extractAddrs(tags) ;
				if(addrs==null||addrs.size()<=0)
					continue ;
				AddrsG ag = new AddrsG(tagg,addrs) ;
				ignore_tags.addAll(tags) ;
				
				rets.add(ag) ;
			}
		}
		
		List<UATag> tags = dev.listTagsAll();
		if (tags == null || tags.size() <= 0)
		{
			return null;
		}
		
		ArrayList<UATag> nor_tags = new ArrayList<>() ;
		for (UATag tag : tags)
		{
			if(ignore_tags.contains(tag))
				continue ;
			nor_tags.add(tag) ;
		}
		List<S7Addr> nor_addrs = extractAddrs(nor_tags) ;
		rets.get(0).s7addrs = nor_addrs ;
		return rets;
	}
	
	private void initAddrsG(AddrsG ag,int blk_size,long read_intv_ms)
	{
		if(ag.s7addrs==null||ag.s7addrs.size()<=0)
			return ;
		if(ag.tagg!=null)
		{
			read_intv_ms = ag.tagg.getReadIntvMS() ;
		}
		
		for(Integer dbnum:searchAddrsDBNums(ag.s7addrs))
		{
			initAddrs(ag.tp2block,this.s7Drv,ag.s7addrs,S7MemTp.DB,dbnum,blk_size,read_intv_ms) ;
		}
		//create modbus cmd and address mapping
		for(S7MemTp mtp:S7MemTp.values())
		{
			if(mtp==S7MemTp.DB)
				continue ;
			
			initAddrs(ag.tp2block,this.s7Drv,ag.s7addrs,mtp,0,blk_size,read_intv_ms) ;
		}
	}
	

	public long getRunIntv_DevCh()
	{
		long drv_int = this.uaDev.getOrDefaultPropValueLong("dev", "dev_intv", 100);
		if (drv_int < 0)
			drv_int = uaDev.getBelongToCh().getDriverIntMS();
		if (drv_int < 0)
			drv_int = 1000;
		return drv_int ;
	}

	
	boolean init(StringBuilder failedr)
	{
//		List<DevAddr> addrs = uaDev.listTagsAddrAll() ;
//		if(addrs==null||addrs.size()<=0)
//		{
//			failedr.append("no access addresses found") ;
//			return false;
//		}
//		List<S7Addr> tmpads = new ArrayList<>() ;
//		for(DevAddr d:addrs)
//		{
//			tmpads.add((S7Addr)d) ;
//		}
//		s7Addrs= tmpads ;
		
		//short devid = (short)uaDev.getOrDefaultPropValueLong("ppi_spk", "dev_addr", 1);
		//int failAfterSuccessive = uaDev.getOrDefaultPropValueInt("timing", "failed_tryn", 3);
		int blocksize =S7Msg.PDU_DEFAULT_LEN;
		
		//long reqto = uaDev.getOrDefaultPropValueLong("timing", "req_to", 100) ;//devDef.getPropValueLong("timing", "req_to", 1000) ;
		//long recvto = uaDev.getOrDefaultPropValueLong("timing", "recv_to", 200) ;
		long read_intv_ms = this.getRunIntv_DevCh() ;
		
		reqIntvMS = uaDev.getOrDefaultPropValueLong("timing", "inter_req", 20) ;
		
		//HashMap<UATagG,AddrsG> tagg2ag = new HashMap<>() ;
		List<AddrsG> ags = listAddrsG(uaDev) ;
		for(AddrsG ag:ags)
		{
			initAddrsG(ag,blocksize,read_intv_ms) ;
			
//			if(ag.tagg==null)
//				defAddrsG = ag ;
//			else
//				tagg2ag.put(ag.tagg,ag) ;
		}
		//this.tagg2ag = tagg2ag ;
		addrsGS = ags ;
		return true;
	}
	
	boolean doCmd(S7TcpConn conn)  throws Exception
	{
		for(AddrsG ag:this.addrsGS)
		{
			for(S7Block blk:ag.tp2block.values())
			{
				//blk.runCmds(conn);
				int r = blk.runNextCmd(conn) ;
				if(r==0)
					continue ;
				Thread.sleep(this.reqIntvMS); 
			}
		}

		return true;
	}
	

	void doCmdError(String errinf)
	{
		for(AddrsG ag:this.addrsGS)
		{
			for(S7Block blk:ag.tp2block.values())
			{
				blk.runReadCmdsErr(errinf);
			}
		}
	}
	
	private AddrsG getAddrsGByTag(UATag tag)
	{
		int n ;
		if(this.addrsGS==null||(n=this.addrsGS.size())<=0)
			return null ;
		for(int i = 1 ; i < n ; i ++)
		{
			AddrsG ag = this.addrsGS.get(i) ;
			if(ag.chkRelatedTagG(tag))
				return ag ;
		}
		return this.addrsGS.get(0) ;
	}
	
	public boolean RT_writeVal(UATag tag,DevAddr da, Object v,StringBuilder failedr)
	{
		S7Addr ma = (S7Addr)da ;
		AddrsG ag = getAddrsGByTag(tag) ;
		if(ag==null)
			return false;
		S7Block blk = ag.tp2block.get(ma.getAreaKey());
		if(blk==null)
		{
			failedr.append("no block found with "+ma.getAreaKey()) ;
			return false;
		}
		return blk.setWriteCmdAsyn(ma,v,failedr) ;
	}
	
	/*
	boolean init_old(StringBuilder failedr)
	{
		List<DevAddr> addrs = uaDev.listTagsAddrAll() ;
		if(addrs==null||addrs.size()<=0)
		{
			failedr.append("no access addresses found") ;
			return false;
		}
		List<S7Addr> tmpads = new ArrayList<>() ;
		for(DevAddr d:addrs)
		{
			tmpads.add((S7Addr)d) ;
		}
		s7Addrs= tmpads ;
		
		short devid = (short)uaDev.getOrDefaultPropValueLong("ppi_spk", "dev_addr", 1);
		//int devid = Integer.parseInt(uaDev.getId());
		
		int failAfterSuccessive = uaDev.getOrDefaultPropValueInt("timing", "failed_tryn", 3);
		
		int blocksize =S7Msg.PDU_DEFAULT_LEN;
		//if(devDef!=null)
		//	blocksize = devDef.getOrDefaultPropValueInt("block_size", "out_coils", 32);//uaDev.getPropValueLong("block_size", "out_coils", 32);
		//if(blocksize<=0)
		//	blocksize=32;
		
		long reqto = uaDev.getOrDefaultPropValueLong("timing", "req_to", 100) ;//devDef.getPropValueLong("timing", "req_to", 1000) ;
		long recvto = uaDev.getOrDefaultPropValueLong("timing", "recv_to", 200) ;
		long inter_ms = uaDev.getOrDefaultPropValueLong("timing", "inter_req", 20) ;
		
		for(Integer dbnum:searchAddrsDBNums(s7Addrs))
		{
			initAddrs(this.tp2block,this.s7Drv,this.s7Addrs,S7MemTp.DB,dbnum,blocksize,inter_ms) ;
		}
		//create modbus cmd and address mapping
		for(S7MemTp mtp:S7MemTp.values())
		{
			if(mtp==S7MemTp.DB)
				continue ;
			
			initAddrs(this.tp2block,this.s7Drv,this.s7Addrs,mtp,0,blocksize,inter_ms) ;
		}

		return true;
	}
	
	boolean doCmd(S7TcpConn conn)  throws Exception
	{
		for(S7Block blk:tp2block.values())
		{
			blk.runCmds(conn);
		}

		return true;
	}
	

	void doCmdError(String errinf)
	{
		for(S7Block blk:tp2block.values())
		{
			blk.runReadCmdsErr(errinf);
		}
	}
	
	
	public boolean RT_writeVal(DevAddr da, Object v,StringBuilder failedr)
	{
		S7Addr ma = (S7Addr)da ;
		S7Block blk = tp2block.get(ma.getAreaKey());
		if(blk==null)
		{
			failedr.append("no block found with "+ma.getAreaKey()) ;
			return false;
		}
		return blk.setWriteCmdAsyn(ma,v,failedr) ;
	}
	*/
}
