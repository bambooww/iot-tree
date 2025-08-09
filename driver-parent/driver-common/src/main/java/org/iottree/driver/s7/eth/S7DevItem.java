package org.iottree.driver.s7.eth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.DevDef;
import org.iottree.core.UADev;
import org.iottree.core.conn.ConnPtStream;


public class S7DevItem
{
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
	
	public S7DevItem(S7EthDriver drv,UADev dev)
	{
		s7Drv = drv ;
		uaDev = dev ;
	}
	
	UADev getUADev()
	{
		return uaDev ;
	}
	
	
	private HashSet<Integer> searchAddrsDBNums()
	{
		HashSet<Integer> rets = new HashSet<>() ;
		for(S7Addr ma:this.s7Addrs)
		{
			if(ma.getMemTp()==S7MemTp.DB)
			{
				int dbn = ma.getDBNum() ;
				rets.add(dbn) ;
			}
		}
		return rets ;
	}

	private List<S7Addr> filterAndSortAddrs(S7MemTp tp,int db_num)
	{
		ArrayList<S7Addr> r = new ArrayList<>() ;
		for(S7Addr ma:this.s7Addrs)
		{
			if(ma.chkSameArea(tp,db_num))
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
		
		for(Integer dbnum:this.searchAddrsDBNums())
		{
			initAddrs(S7MemTp.DB,dbnum,blocksize,inter_ms) ;
		}
		//create modbus cmd and address mapping
		for(S7MemTp mtp:S7MemTp.values())
		{
			if(mtp==S7MemTp.DB)
				continue ;
			
			initAddrs(mtp,0,blocksize,inter_ms) ;
		}

		return true;
	}
	
	private void initAddrs(S7MemTp mtp,int dbnum,int block_size,long scan_inter_ms)
	{
		List<S7Addr> addrs = filterAndSortAddrs(mtp,dbnum) ;
		if(addrs==null||addrs.size()<=0)
			return ;
		
		String areakey = addrs.get(0).getAreaKey();
		S7Block blk = new S7Block(addrs,block_size,scan_inter_ms);
		//blk.setTimingParam(reqto, recvto, inter_ms);
		if(blk.initCmds(s7Drv))
			tp2block.put(areakey,blk) ;
	}
	
	boolean doCmd(S7TcpConn conn)  throws Exception
	{
		for(S7Block blk:tp2block.values())
		{
//			try
//			{
				blk.runCmds(conn);
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
		}
		
//		PPICmd ppic = new PPICmd((short)2,"QB0",(short)3) ;
//		ppic.RT_init(ppiDrv);
//		ppic.doCmd_Test(ep) ;
		return true;
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
//		int intv ;
//		if(v instanceof Number)
//			intv = ((Number)v).intValue() ;
//		else
//			intv = Integer.parseInt(v+"");
		return blk.setWriteCmdAsyn(ma,v,failedr) ;
	}
}
