package org.iottree.driver.s7.eth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.iottree.core.UAVal;
import org.iottree.core.basic.IConnEndPoint;
import org.iottree.core.basic.MemSeg8;
import org.iottree.core.basic.MemTable;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

/**
 * a Block is in devitem with same data area
 * 
 * @author jason.zhu
 *
 */
public class S7Block
{
	public static final long MAX_Demotion_DELAY = 30000 ;
	
	static ILogger log = LoggerManager.getLogger("S7_Lib");
	
	
	S7MemTp memTp = null ;
	
	int dbNum = 0 ;
	
	String areaKey = null ;
	
	List<S7Addr> addrs = null ;
	
	int blockSize = 32 ;
	
	long scanInterMS = 100 ;
	
	MemTable<MemSeg8> memTb = new MemTable<>(8,65536*2) ;
	
	transient HashMap<S7Msg,List<S7Addr>> cmd2addr = new HashMap<>() ;
	
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
	
	private transient S7EthDriver ppiDrv = null ;
	
	public S7Block(List<S7Addr> addrs,
			int block_size,long scan_inter_ms)//,int failed_successive)
	{
		if(addrs==null||addrs.size()<=0)
			throw new IllegalArgumentException("addr cannot be emtpy");
		if(block_size<=0)
			throw new IllegalArgumentException("block cannot <=0 ");
		S7Addr addr = addrs.get(0);
		this.memTp = addr.getMemTp() ;
		this.dbNum = addr.getDBNum() ;
		this.areaKey = addr.getAreaKey() ;
		this.addrs = addrs ;
		this.blockSize = block_size ;
		this.scanInterMS = scan_inter_ms;
		//this.failedSuccessive = failed_successive;
	}
	
	public String getAreaKey()
	{
		return this.areaKey ;
	}
	
	public void setTimingParam(long req_to,long recv_to,long inter_reqms)
	{
		this.reqTO = req_to ;
		this.recvTO = recv_to ;
		this.interReqMs = inter_reqms ;
	}
		
	public S7MemTp getMemTp()
	{
		return memTp ;
	}
	
	
	public List<S7Addr> getAddrs()
	{
		return addrs ;
	}
	
	public MemTable<MemSeg8> getMemTable()
	{
		return this.memTb ;
	}
	
	boolean initCmds(S7EthDriver drv)
	{
		ppiDrv = drv ;
		
		if(addrs==null||addrs.size()<=0)
			return false ;

		S7Msg curcmd = null ;
		int cur_reg = -1 ;
		ArrayList<S7Addr> curaddrs = null ;
		for(S7Addr ma:addrs)
		{
			int regp = ma.getOffsetBytes() ;
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
			
			S7Addr lastma = curaddrs.get(curaddrs.size()-1) ;
			int regnum = lastma.getOffsetBytes()-cur_reg+lastma.getValTP().getValByteLen() ;
			//regnum = regnum/2+regnum%2;
			
			curcmd = new S7MsgRead().withParam(memTp, dbNum, cur_reg,regnum)
					.withScanIntervalMS(this.scanInterMS);//(this.getFC(),this.scanInterMS,
			curcmd.init(drv);
			cmd2addr.put(curcmd, curaddrs);
				
			cur_reg = regp ;
			curaddrs = new ArrayList<>() ;
			curaddrs.add(ma) ;
			continue ;
		}
		
		if(curaddrs.size()>0)
		{
			S7Addr lastma = curaddrs.get(curaddrs.size()-1) ;
			//int regnum = lastma.getRegEnd()-cur_reg;
			int regnum = (lastma.getOffsetBytes()-cur_reg)+lastma.getValTP().getValByteLen() ;
			//regnum = regnum/2+regnum%2;
			//curcmd = new ModbusCmdReadWords(this.getFC(),this.scanInterMS,
			//			devId,cur_reg,regnum);
			
			curcmd = new S7MsgRead().withParam(memTp,dbNum,cur_reg,regnum)
					.withScanIntervalMS(this.scanInterMS);
			curcmd.init(drv);
			//curcmd.setRecvTimeout(reqTO);
			//curcmd.setRecvEndTimeout(recvTO);
			cmd2addr.put(curcmd, curaddrs);
		}
		
//		for(S7Msg mc:cmd2addr.keySet())
//		{
//			mc.withRecvTimeout(reqTO).withRecvEndTimeout(recvTO);
//			if(log.isDebugEnabled())
//				log.debug("init modbus cmd="+mc);
//		}
		
		return true;
	}
	
	
	private void setAddrError(List<S7Addr> addrs)
	{
		if(addrs==null)
			return ;
		for(S7Addr ma:addrs)
			ma.RT_setVal(null); 
	}
	

	private Object getValByAddr(S7Addr da)
	{
		UAVal.ValTP vt = da.getValTP();
		if(vt==null)
			return null ;
		if(vt==UAVal.ValTP.vt_bool)
		{
			int regp = da.getOffsetBytes() ;
			int inbit = da.getInBits() ;
			int vv = memTb.getValNumber(UAVal.ValTP.vt_byte,regp).intValue() ;
			return (vv & (1<<inbit))>0 ;
		}
		else if(vt.isNumberVT())
		{
			return memTb.getValNumber(vt,da.getOffsetBytes()) ;
		}
		return null;
	}
	
	public boolean setValByAddr(S7Addr da,Object v)
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
			memTb.setValBool(da.getOffsetBytes(),da.getInBits(),bv) ;
			return true;
		}
		else if(vt.isNumberVT())
		{
			if(!(v instanceof Number))
				return false;
			memTb.setValNumber(vt,da.getOffsetBytes(),(Number)v) ;
			return true;
		}
		return false;
	}
	
	
	
	public boolean runCmds(S7TcpConn conn) throws Exception
	{
		this.runWriteCmdAndClear(conn);
		return runReadCmds(conn) ;
		
	}
	
	public void runCmdsErr()
	{
		runReadCmdsErr() ;
	}
	
	private void transMem2Addrs(List<S7Addr> addrs)
	{
		for(S7Addr ma:addrs)
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
	
	private boolean runReadCmds(S7TcpConn conn) throws Exception
	{
		//ArrayList<DevAddr> okaddrs = new ArrayList<>() ;
		boolean ret = true;
		for(S7Msg mc:cmd2addr.keySet())
		{
			if(!mc.tickCanRun())
				continue ;
			
			S7MsgRead cmdr = (S7MsgRead)mc ;
			Thread.sleep(this.interReqMs);
			
			List<S7Addr> addrs = cmd2addr.get(mc) ;
			
			conn.clearInputStream(50);
			byte[] retbs = null;
			
			try
			{
				cmdr.processByConn(conn);
				//cmdr.doCmd(ep.getInputStream(),ep.getOutputStream());
				retbs = cmdr.getReadRes() ;
			}
			catch(Exception e)
			{
				if(S7Msg.log.isDebugEnabled())
					S7Msg.log.error(e);
			}
			//retbs = resp.getRetData() ;
			
			if(retbs==null)
			{
				if(chkSuccessiveFailed(true))
				{
					setAddrError(addrs);
					ret = false;
				}
				continue ;
			}
			
			int offsetbs = cmdr.getPos();
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
		for(S7Msg mc:cmd2addr.keySet())
		{
			
			List<S7Addr> addrs = cmd2addr.get(mc) ;
			setAddrError(addrs);
			//transMem2Addrs(addrs);
		}
		return ret ;
	}
	
	private LinkedList<S7MsgWrite> writeCmds = new LinkedList<>() ;
	
	private void runWriteCmdAndClear(S7TcpConn conn) throws Exception
	{
		int s = writeCmds.size();
		if(s<=0)
			return ;
		
		S7Msg[] cmds = new S7Msg[s] ;
		synchronized(writeCmds)
		{
			for(int i = 0 ; i < s ; i ++)
				cmds[i] = writeCmds.removeFirst() ;
		}
		
		for(S7Msg mc:cmds)
		{
			if(!mc.tickCanRun())
				continue ;
			
			Thread.sleep(this.interReqMs);
			
			//mc.doCmd(ep.getInputStream(),ep.getOutputStream());
			conn.clearInputStream(50);
			mc.processByConn(conn);
		}
	}
	
	public boolean setWriteCmdAsyn(S7Addr addr, Object v)
	{
		S7MsgWrite mc = new S7MsgWrite().withParam(memTp, this.dbNum, addr,v);
		mc.init(ppiDrv);

		
		synchronized(writeCmds)
		{
			writeCmds.addLast(mc);
		}
		return true;
		
	}
}
