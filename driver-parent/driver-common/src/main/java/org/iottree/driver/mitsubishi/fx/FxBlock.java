package org.iottree.driver.mitsubishi.fx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.ByteOrder;
import org.iottree.core.basic.IConnEndPoint;
import org.iottree.core.basic.MemSeg8;
import org.iottree.core.basic.MemTable;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

import com.google.common.primitives.UnsignedInteger;

public class FxBlock
{
	public static final long MAX_Demotion_DELAY = 30000 ;
	
	static ILogger log = LoggerManager.getLogger("Fx_Block");
	
	//int devId = 1 ;
	String prefix = null ;
	
	List<FxAddr> addrs = null ;
	
	int blockSize = 32 ;
	
	long scanInterMS = 100 ;
	
	MemTable<MemSeg8> memTb = new MemTable<>(8,65536*2) ;
	
	transient HashMap<FxCmd,List<FxAddr>> cmd2addr = new HashMap<>() ;
	
	private int failedSuccessive = 3 ;
	
	private long reqTO = 1000 ;
	
	private long recvTO = 100 ;
	
	private long interReqMs = 0 ;
	
	transient int failedCount = 0 ;
	
	/**
	 * 
	 */
	private transient long lastFailedDT = -1 ;
	
	/**
	 * Number of consecutive errors
	 */
	private transient int lastFailedCC  = 0 ;
	
	private transient FxDriver ppiDrv = null ;
	
	/**
	 * 
	 * @param addrs same prefix and sorted by addr num
	 * @param block_size
	 * @param scan_inter_ms
	 */
	FxBlock(List<FxAddr> addrs,
			int block_size,long scan_inter_ms)//,int failed_successive)
	{
		//devId = devid ;
		if(addrs==null||addrs.size()<=0)
			throw new IllegalArgumentException("addr cannot be emtpy");
		if(block_size<=0)
			throw new IllegalArgumentException("block cannot <=0 ");
		//this.memTp = memtp ;
		this.addrs = addrs ;
		this.blockSize = block_size ;
		this.scanInterMS = scan_inter_ms;
		//this.failedSuccessive = failed_successive;
	}
	
	public void setTimingParam(long req_to,long recv_to,long inter_reqms)
	{
		this.reqTO = req_to ;
		this.recvTO = recv_to ;
		this.interReqMs = inter_reqms ;
	}
		
//	public FxMemTp getMemTp()
//	{
//		return memTp ;
//	}
	
	
	public List<FxAddr> getAddrs()
	{
		return addrs ;
	}
	
	public MemTable<MemSeg8> getMemTable()
	{
		return this.memTb ;
	}
	
	boolean initCmds(FxDriver drv)
	{
		ppiDrv = drv ;
		
		if(addrs==null||addrs.size()<=0)
			return false ;

		FxAddr fxaddr = addrs.get(0) ;
		int base_addr = fxaddr.addrSeg.getBaseAddr() ;
		//System.out.println("11") ;
		FxCmd curcmd = null ;
		int cur_reg = -1 ;
		ArrayList<FxAddr> curaddrs = null ;
		for(FxAddr ma:addrs)
		{
			int regp = ma.getBytesInBase();//.getOffsetBytes() ;
			if(cur_reg<0)
			{
				cur_reg = regp ;
				curaddrs = new ArrayList<>() ;
				curaddrs.add(ma) ;
				continue ;
			}
			
			int bytelen = (regp-cur_reg)+1;
			//if(ma.getRegEnd()<=cur_reg+this.blockSize)
			if(bytelen<=this.blockSize)
			{
				curaddrs.add(ma) ;
				continue;
			}
			
			FxAddr lastma = curaddrs.get(curaddrs.size()-1) ;
			int regnum = lastma.getBytesInBase()-cur_reg+lastma.getValTP().getValByteLen() ;
//			if(!ma.bValBit)
//				regnum += lastma.getValTP().getValByteLen() ;

			curcmd = new FxCmdR(base_addr,cur_reg,regnum)
					.withScanIntervalMS(this.scanInterMS);//(this.getFC(),this.scanInterMS,
			curcmd.initCmd(drv);
			cmd2addr.put(curcmd, curaddrs);
				
			cur_reg = regp ;
			curaddrs = new ArrayList<>() ;
			curaddrs.add(ma) ;
			continue ;
		}
		
		if(curaddrs.size()>0)
		{
			FxAddr lastma = curaddrs.get(curaddrs.size()-1) ;
			int regnum = lastma.getBytesInBase()-cur_reg+lastma.getValTP().getValByteLen() ;
//			if(!lastma.bValBit)
//				regnum += lastma.getValTP().getValByteLen() ;
			curcmd = new FxCmdR(base_addr,cur_reg,regnum)
					.withScanIntervalMS(this.scanInterMS);
			curcmd.initCmd(drv);
			//curcmd.setRecvTimeout(reqTO);
			//curcmd.setRecvEndTimeout(recvTO);
			cmd2addr.put(curcmd, curaddrs);
		}
		
		for(FxCmd mc:cmd2addr.keySet())
		{
			mc.withRecvTimeout(reqTO).withRecvEndTimeout(recvTO);
			if(log.isDebugEnabled())
				log.debug("init modbus cmd="+mc);
		}
		return true ;
	}
	
	
	
	private void setAddrError(List<FxAddr> addrs)
	{
		if(addrs==null)
			return ;
		for(FxAddr ma:addrs)
			ma.RT_setVal(null); 
	}
	

	private Object getValByAddr(FxAddr da)
	{
		UAVal.ValTP vt = da.getValTP();
		if(vt==null)
			return null ;
		if(vt==UAVal.ValTP.vt_bool)
		{
			int regp = da.getBytesInBase() ;
			int inbit = da.getInBits() ;
			int vv = memTb.getValNumber(UAVal.ValTP.vt_byte,regp,ByteOrder.LittleEndian).intValue() ;
			return (vv & (1<<inbit))>0 ;
		}
		else if(vt.isNumberVT())
		{
			Number nbv = memTb.getValNumber(vt,da.getBytesInBase(),ByteOrder.BigEndian) ;
//			if(vt.getValByteLen()==4)
//			{
//				nbv = memTb.getValNumber(vt,da.getBytesInBase(),ByteOrder.ModbusWord) ;
//				
//				if(vt==ValTP.vt_uint32||vt==ValTP.vt_int32)
//				{
//					int intv = nbv.intValue() ;
//					int tmpl = intv>>16 & 0xFFFF ;
//					intv = intv<<16 & 0xFFFF0000;
//					intv = intv | tmpl ;
//					if(vt==ValTP.vt_uint32)
//						return UnsignedInteger.fromIntBits(intv) ;
//					else
//						return intv ;
//				}
//			}
			return nbv ;
				
		}
		return null;
	}
	
	public boolean setValByAddr(FxAddr da,Object v)
	{
		UAVal.ValTP vt = da.getValTP();
		if(vt==null)
			return false ;
		if(vt==UAVal.ValTP.vt_bool)
		{
			boolean bv = false;
			if(v instanceof Boolean)
				bv = (Boolean)v ;
			else if(v instanceof Number)
				bv = ((Number)v).doubleValue()>0 ;
			else
				return false;
			memTb.setValBool(da.getBytesInBase(),da.getInBits(),bv) ;
			return true;
		}
		else if(vt.isNumberVT())
		{
			if(!(v instanceof Number))
				return false;
			memTb.setValNumber(vt,da.getBytesInBase(),(Number)v) ;
			return true;
		}
		return false;
	}
	
	
	
	public boolean runCmds(IConnEndPoint ep) throws Exception
	{
		this.runWriteCmdAndClear(ep);
		return runReadCmds(ep) ;
		
	}
	
	public void runCmdsErr()
	{
		runReadCmdsErr() ;
	}
	
	private void transMem2Addrs(List<FxAddr> addrs)
	{
		for(FxAddr ma:addrs)
		{
			Object ov = getValByAddr(ma) ;
			if(ov!=null)
				ma.RT_setVal(ov);
		}
	}
	
	private boolean chkSuccessiveFailed(boolean bfailed)
	{
		if(!bfailed)
		{
			failedCount = 0 ;
			
			lastFailedDT = -1 ;
			lastFailedCC  = 0 ;
			return false;
		}
		
		lastFailedDT = System.currentTimeMillis() ;
		lastFailedCC  ++ ;
		if(lastFailedCC>3600)
			lastFailedCC = 3600 ;
		
		failedCount ++;
		if(failedCount>=this.failedSuccessive)
		{
			failedCount = this.failedSuccessive;
			return true;
		}
		return false;
	}
	
	public boolean checkDemotionCanRun()
	{
		if(lastFailedCC<=0)
			return true ;
		
		long dur_dt = lastFailedCC*1000;
		if(dur_dt>MAX_Demotion_DELAY)
		{
			dur_dt =  MAX_Demotion_DELAY ;
		}
		if(System.currentTimeMillis() - lastFailedDT<dur_dt)
		{
			//System.out.println(" checkDemotionCanRun false---"+this.devId+" dur="+dur_dt);
			return false;
		}
		
		return true;
	}
	

	private boolean runReadCmds(IConnEndPoint ep) throws Exception
	{
		//ArrayList<DevAddr> okaddrs = new ArrayList<>() ;
		boolean ret = true;
		for(FxCmd mc:cmd2addr.keySet())
		{
			if(!mc.tickCanRun())
				continue ;
			
			FxCmdR cmdr = (FxCmdR)mc ;
			Thread.sleep(this.interReqMs);
			
			List<FxAddr> addrs = cmd2addr.get(mc) ;
			cmdr.doCmd(ep.getInputStream(),ep.getOutputStream());
			FxMsgResp resp = cmdr.getResp();
			FxMsgReq req = cmdr.getReq() ;
			byte[] retbs = null;
			if(resp==null)
				continue ;
			
			retbs = resp.getRetData() ;
			int offsetbs = req.getRetOffsetBytes() ;
			if(retbs==null)
			{
				if(chkSuccessiveFailed(true))
				{
					setAddrError(addrs);
					ret = false;
				}
				continue ;
			}
			
			//int offsetbs = cmdr.getOffsetBytes() ;
			memTb.setValBlock(offsetbs, retbs.length, retbs, 0);
			transMem2Addrs(addrs);
			chkSuccessiveFailed(false) ;
		}
		return ret ;
	}
	
	private boolean runReadCmdsErr() //throws Exception
	{
		//ArrayList<DevAddr> okaddrs = new ArrayList<>() ;
		boolean ret = true;
		for(FxCmd mc:cmd2addr.keySet())
		{
			
			List<FxAddr> addrs = cmd2addr.get(mc) ;
			setAddrError(addrs);
			//transMem2Addrs(addrs);
		}
		return ret ;
	}
	
	private LinkedList<FxCmd> writeCmds = new LinkedList<>() ;
	
	private void runWriteCmdAndClear(IConnEndPoint ep) throws Exception
	{
		int s = writeCmds.size();
		if(s<=0)
			return ;
		
		FxCmd[] cmds = new FxCmd[s] ;
		synchronized(writeCmds)
		{
			for(int i = 0 ; i < s ; i ++)
				cmds[i] = writeCmds.removeFirst() ;
		}
		
		for(FxCmd mc:cmds)
		{
			if(!mc.tickCanRun())
				continue ;
			
			Thread.sleep(this.interReqMs);
			
			mc.doCmd(ep.getInputStream(),ep.getOutputStream());
		}
	}
	
	public boolean setWriteCmdAsyn(FxAddr addr, Object v)
	{
//		PPICmdW mc = new PPICmdW((short)devId,memTp,addr,v);
//		mc.initCmd(ppiDrv);
//
//		
//		synchronized(writeCmds)
//		{
//			writeCmds.addLast(mc);
//		}
		return true;
		
	}
}
