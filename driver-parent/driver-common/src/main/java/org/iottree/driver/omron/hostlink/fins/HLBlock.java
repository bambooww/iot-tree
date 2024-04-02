package org.iottree.driver.omron.hostlink.fins;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.iottree.core.util.xmldata.DataUtil;
import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.hostlink.HLAddr;
import org.iottree.driver.omron.hostlink.HLAddrSeg;
import org.iottree.driver.omron.hostlink.HLDriver;
import org.iottree.driver.omron.hostlink.HLModel;


public class HLBlock
{
	public static final long MAX_Demotion_DELAY = 30000 ;
	
	static ILogger log = LoggerManager.getLogger("HL_Block");
	
	HLDevItem devItem = null ;
	//int devId = 1 ;
	String prefix = null ;
	
	List<HLAddr> addrs = null ;
	
	int blockSize = 32 ;
	
	long scanInterMS = 100 ;
	
	MemTable<MemSeg8> memTb = new MemTable<>(8,65536*2) ;
	
	transient HashMap<HLCmd,List<HLAddr>> cmd2addr = new HashMap<>() ;
	
	private int failedSuccessive = 3 ;
	
	private long reqTO = 2000 ;
	
	//private long recvTO = 1000 ;
	
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
	
	private transient long lastWriteFailedDT = -1 ;
	
	private transient String lastWriteFailedInf = null ;
	
	private transient HLFinsDriver driver = null ;
	
	private transient HLAddrSeg addrSeg =null ;
	/**
	 * 
	 * @param addrs same prefix and sorted by addr num
	 * @param block_size
	 * @param scan_inter_ms
	 */
	HLBlock(HLDevItem devitem,HLAddrSeg seg,List<HLAddr> addrs,
			int block_size,long scan_inter_ms,int failed_successive)
	{
		this.devItem = devitem ;
		
		//devId = devid ;
		if(addrs==null||addrs.size()<=0)
			throw new IllegalArgumentException("addr cannot be emtpy");
		if(block_size<=0)
			throw new IllegalArgumentException("block cannot <=0 ");
		//this.memTp = memtp ;
		this.addrSeg = seg ;
		this.addrs = addrs ;
		this.blockSize = block_size ;
		this.scanInterMS = scan_inter_ms;
		this.failedSuccessive = failed_successive;
	}
	
	public void setTimingParam(long req_to,long inter_reqms)
	{
		this.reqTO = req_to ;
//		this.recvTO = recv_to ;
		this.interReqMs = inter_reqms ;
	}
		
//	public FxMemTp getMemTp()
//	{
//		return memTp ;
//	}
	
	
	public List<HLAddr> getAddrs()
	{
		return addrs ;
	}
	
	public MemTable<MemSeg8> getMemTable()
	{
		return this.memTb ;
	}
	
	public long getReqTimeout()
	{
		return this.reqTO ;
	}
	
//	public long getRecvTimeout()
//	{
//		return this.recvTO ;
//	}
	
	public long getInterReqMS()
	{
		return this.interReqMs ;
	}
	/**
	 * 
	 * @param drv
	 * @return
	 */
	protected boolean initCmds(HLFinsDriver drv)
	{

		driver = drv ;
		
		if(addrs==null||addrs.size()<=0)
			return false ;

		HLAddr fxaddr = addrs.get(0) ;

		HLCmd curcmd = null ;
		int cur_reg = -1 ;
		ArrayList<HLAddr> curaddrs = null ;
		boolean bbit_only = this.addrSeg.isValBitOnly() ;
		for(HLAddr ma:addrs)
		{
			int regp = ma.getAddrNum();//.getBytesInBase();//.getOffsetBytes() ;
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
			
			HLAddr lastma = curaddrs.get(curaddrs.size()-1) ;
			int blen = lastma.getValTP().getValByteLen() -2;
			if(blen<0)
				blen  = 0 ;
			int regnum = lastma.getAddrNum()-cur_reg+1+blen;

			
			curcmd = new HLFinsCmdMemR(cur_reg,regnum,bbit_only).withScanIntervalMS(this.scanInterMS); ;
					//(this.getFC(),this.scanInterMS,
			curcmd.initCmd(drv,this);
			cmd2addr.put(curcmd, curaddrs);
				
			cur_reg = regp ;
			curaddrs = new ArrayList<>() ;
			curaddrs.add(ma) ;
			continue ;
		}
		
		if(curaddrs.size()>0)
		{
			HLAddr lastma = curaddrs.get(curaddrs.size()-1) ;
			int blen = lastma.getValTP().getValByteLen() -2;
			if(blen<0)
				blen  = 0 ;
			int regnum = lastma.getAddrNum()-cur_reg+1+blen ;
//			if(!lastma.bValBit)
//				regnum += lastma.getValTP().getValByteLen() ;
			curcmd = new HLFinsCmdMemR(cur_reg,regnum,bbit_only)
					.withScanIntervalMS(this.scanInterMS);
			curcmd.initCmd(drv,this);
			//curcmd.setRecvTimeout(reqTO);
			//curcmd.setRecvEndTimeout(recvTO);
			cmd2addr.put(curcmd, curaddrs);
		}
		
		for(HLCmd mc:cmd2addr.keySet())
		{
			mc.withRecvTimeout(reqTO,failedSuccessive);//.withRecvEndTimeout(recvTO);
			if(log.isDebugEnabled())
				log.debug("init modbus cmd="+mc);
		}
		return true ;
	}
	
	
	private void setAddrError(List<HLAddr> addrs)
	{
		if(addrs==null)
			return ;
		for(HLAddr ma:addrs)
			ma.RT_setVal(null); 
	}
	

	private Object getValByAddr(HLAddr da)
	{
		UAVal.ValTP vt = da.getValTP();
		if(vt==null)
			return null ;
		int regp = da.getAddrNum()*2 ;
		int inbit = da.getBitNum() ;
		if(vt==UAVal.ValTP.vt_bool)
		{
			if(inbit>=0)
			{
				int vv = memTb.getValNumber(UAVal.ValTP.vt_int16,regp,ByteOrder.LittleEndian).intValue() ;
				return (vv & (1<<inbit))>0 ;
			}
			else if(addrSeg.isValBitOnly())
			{//one bool val - one byte
				byte bv = memTb.getValNumber(ValTP.vt_byte, da.getAddrNum()).byteValue() ;
				return bv != 0;
			}
		}
		else if(vt.isNumberVT())
		{
			Number nbv = memTb.getValNumber(vt,regp,ByteOrder.LittleEndian) ;
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
	
	public byte[] transValToBytesByAddr(HLAddr da,Object v,StringBuilder failedr)
	{
		UAVal.ValTP vt = da.getValTP();
		if(vt==null)
		{
			failedr.append("no valtp in addr") ;
			return null ;
		}
		if(vt==UAVal.ValTP.vt_bool)
		{
			failedr.append("vt_bool is not supported") ;
			return null ;
		}

		if(!vt.isNumberVT())
		{
			failedr.append("valtp is not number") ;
			return null ;
		}
		
		int intv ;
		if(v instanceof Number)
			intv = ((Number)v).intValue() ;
		else if(v instanceof String)
			intv = Integer.parseInt(""+v) ;
		else
		{
			failedr.append("invalid val,it must be number") ;
			return null ;
		}
		
		int blen = vt.getValByteLen() ;
		if(blen==4)
		{
			byte[] rets = new byte[4] ;
			DataUtil.intToBytes(intv,rets,0,ByteOrder.BigEndian) ;
			return rets ;
		}
		else if(blen==2)
		{
			byte[] rets = new byte[2] ;
			DataUtil.shortToBytes((short)intv,rets,0,ByteOrder.BigEndian) ;
			return rets ;
		}
		failedr.append("valtp is not 2 or 4") ;
		return null ;
	}
	
	public List<Short> transValToWordsByAddr(HLAddr da,Object v,StringBuilder failedr)
	{
		UAVal.ValTP vt = da.getValTP();
		if(vt==null)
		{
			failedr.append("no valtp in addr") ;
			return null ;
		}
		if(vt==UAVal.ValTP.vt_bool)
		{
			failedr.append("vt_bool is not supported") ;
			return null ;
		}

		if(!vt.isNumberVT())
		{
			failedr.append("valtp is not number") ;
			return null ;
		}
		
		int intv ;
		if(v instanceof Number)
			intv = ((Number)v).intValue() ;
		else if(v instanceof String)
			intv = Integer.parseInt(""+v) ;
		else
		{
			failedr.append("invalid val,it must be number") ;
			return null ;
		}
		
		int blen = vt.getValByteLen() ;
		if(blen==4)
		{
			short hv = (short)(intv>>16 & 0xFFFF) ;
			short lv = (short)(intv & 0xFFFF) ;
			return Arrays.asList(hv,lv) ;
		}
		else if(blen==2)
		{
//			byte[] rets = new byte[2] ;
//			DataUtil.shortToBytes((short)intv,rets,0,ByteOrder.BigEndian) ;
//			return rets ;
			return Arrays.asList((short)intv) ;
		}
		failedr.append("valtp is not 2 or 4") ;
		return null ;
	}
	
//	public boolean setValByAddr(HLAddr da,Object v)
//	{
//		UAVal.ValTP vt = da.getValTP();
//		if(vt==null)
//			return false ;
//		if(vt==UAVal.ValTP.vt_bool)
//		{
//			boolean bv = false;
//			if(v instanceof Boolean)
//				bv = (Boolean)v ;
//			else if(v instanceof Number)
//				bv = ((Number)v).doubleValue()>0 ;
//			else
//				return false;
//			memTb.setValBool(da.getBytesInBase(),da.getInBits(),bv) ;
//			return true;
//		}
//		else if(vt.isNumberVT())
//		{
//			if(!(v instanceof Number))
//				return false;
//			memTb.setValNumber(vt,da.getBytesInBase(),(Number)v) ;
//			return true;
//		}
//		return false;
//	}
	
	
	
	public boolean runCmds(IConnEndPoint ep,StringBuilder failedr) throws Exception
	{
		this.runWriteCmdAndClear(ep);
		return runReadCmds(ep,failedr) ;
		
	}
	
	public void runCmdsErr()
	{
		runReadCmdsErr() ;
	}
	
	private void transMem2Addrs(List<HLAddr> addrs)
	{
		for(HLAddr ma:addrs)
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
	

	private boolean runReadCmds(IConnEndPoint ep,StringBuilder failedr) throws Exception
	{
		//ArrayList<DevAddr> okaddrs = new ArrayList<>() ;
		boolean ret = true;
		boolean bbit_only = this.addrSeg.isValBitOnly() ;
		for(HLCmd mc:cmd2addr.keySet())
		{
			if(!mc.tickCanRun())
				continue ;
			
			HLFinsCmdMemR cmdr = (HLFinsCmdMemR)mc ;
			Thread.sleep(this.interReqMs);
			
			List<HLAddr> addrs = cmd2addr.get(mc) ;
			boolean cmdres = cmdr.doCmd(ep.getInputStream(),ep.getOutputStream(),failedr) ;
			HLFinsRespMemR resp = cmdr.getResp();
			HLFinsReqMemR req = cmdr.getReq() ;
			byte[] retbs = null;
			
			
			retbs = null;
			if(cmdres && resp!=null)
				retbs = resp.getReturnBytes() ;
			
			if(retbs==null)
			{
				if(chkSuccessiveFailed(true))
				{
					setAddrError(addrs);
					ret = false;
				}
				continue ;
			}
			

			int offsetbs = req.getBeginAddr() ;
			//int offsetbs = cmdr.getOffsetBytes() ;
			if(bbit_only)
			{
				for(int i = 0 ; i < retbs.length ; i ++)
				{
					byte b  = retbs[i] ;
					memTb.setValNumber(ValTP.vt_byte, offsetbs+i,b) ;
				}
			}
			else
			{
				memTb.setValBlock(offsetbs*2, retbs.length, retbs, 0);
			}
			
			transMem2Addrs(addrs);
			chkSuccessiveFailed(false) ;
		}
		return ret ;
	}
	
	private boolean runReadCmdsErr() //throws Exception
	{
		//ArrayList<DevAddr> okaddrs = new ArrayList<>() ;
		boolean ret = true;
		for(HLCmd mc:cmd2addr.keySet())
		{
			
			List<HLAddr> addrs = cmd2addr.get(mc) ;
			setAddrError(addrs);
			//transMem2Addrs(addrs);
		}
		return ret ;
	}
	
	private LinkedList<HLCmd> writeCmds = new LinkedList<>() ;
	
	private void runWriteCmdAndClear(IConnEndPoint ep) throws Exception
	{
		int s = writeCmds.size();
		if(s<=0)
			return ;
		
		HLCmd[] cmds = new HLCmd[s] ;
		synchronized(writeCmds)
		{
			for(int i = 0 ; i < s ; i ++)
				cmds[i] = writeCmds.removeFirst() ;
		}
		
		for(HLCmd mc:cmds)
		{
			if(!mc.tickCanRun())
				continue ;
			
			Thread.sleep(this.interReqMs);
			
			StringBuilder failedr = new StringBuilder() ;
			boolean res = mc.doCmd(ep.getInputStream(),ep.getOutputStream(),failedr);
			
			if(!res)
				log.error(failedr.toString()); 
			
			if(mc instanceof HLFinsCmdMemW)
			{
				HLFinsCmdMemW fcw = (HLFinsCmdMemW)mc ;
				if(!fcw.isAck())
					log.error(fcw.toString() +" is not ack");
				else
				{
					if(log.isDebugEnabled())
					{
						log.debug(fcw.toString() +" is run ok (ack=true)");
					}
				}
			}
		}
	}
	
	public boolean setWriteCmdAsyn(HLAddr fxaddr, Object v)
	{
		if(!addrSeg.matchAddr(fxaddr))
		{
			return false;
		}
		
		HLFinsCmdMemW fxcmd = null ;
		
		if(fxaddr.isBitVal())
		{
			boolean bv ;
			if(v instanceof Boolean)
				bv = (Boolean)v ;
			else if(v instanceof Number)
				bv = ((Number)v).intValue()>0 ;
			else if(v instanceof String)
				bv = "true".equalsIgnoreCase((String)v) || "1".equalsIgnoreCase((String)v) ;
			else
			{
				return false;
			}
			fxcmd = new HLFinsCmdMemW();
			fxcmd.withScanIntervalMS(this.scanInterMS);
			if(addrSeg.isValBitOnly())
				fxcmd.asBitOnlyVals(fxaddr.getAddrNum(), Arrays.asList(bv)) ;
			else
				fxcmd.asBitVals(fxaddr.getAddrNum(), fxaddr.getBitNum(), Arrays.asList(bv)) ;
			//(this.getFC(),this.scanInterMS,
		}
		else
		{
			
			StringBuilder failedr = new StringBuilder() ;
			List<Short> ws = transValToWordsByAddr(fxaddr,v,failedr) ;
			if(ws==null)
			{
				lastWriteFailedDT = System.currentTimeMillis() ;
				lastWriteFailedInf = failedr.toString() ;
				return false;
			}
			
			fxcmd = new HLFinsCmdMemW();
			fxcmd.withScanIntervalMS(this.scanInterMS);
			fxcmd.asWordVals(fxaddr.getAddrNum(), ws) ;
		}
		
		fxcmd.initCmd(driver,this);
		
		synchronized(writeCmds)
		{
			writeCmds.addLast(fxcmd);
		}
		return true;
		
	}
	
	public long getLastWriteFailedDT()
	{
		return lastWriteFailedDT;
	}
	
	public String getLastWriteFailedInf()
	{
		return this.lastWriteFailedInf ;
	}
}
