package org.iottree.driver.s7.ppi;

import java.util.*;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.DataUtil;
import org.iottree.core.DevAddr;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.ByteOrder;
import org.iottree.core.basic.IConnEndPoint;
import org.iottree.core.basic.MemSeg8;
import org.iottree.core.basic.MemTable;
import org.iottree.driver.common.*;

/**
 * BIT B W D four val type are in block
 * based on bytes
 * 
 * 
 * @author jason.zhu
 *
 */
public class PPIBlock
{
	public static final long MAX_Demotion_DELAY = 30000 ;
	
	static ILogger log = LoggerManager.getLogger("PPI_Lib");
	
	int devId = 1 ;
	PPIMemTp memTp = null ;
	
	List<PPIAddr> addrs = null ;
	
	int blockSize = 32 ;
	
	long scanInterMS = 100 ;
	
	MemTable<MemSeg8> memTb = new MemTable<>(8,65536*2) ;
	
	transient HashMap<PPICmd,List<PPIAddr>> cmd2addr = new HashMap<>() ;
	
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
	
	private transient PPIDriver ppiDrv = null ;
	
	public PPIBlock(int devid,PPIMemTp memtp,List<PPIAddr> addrs,
			int block_size,long scan_inter_ms)//,int failed_successive)
	{
		devId = devid ;
		if(addrs==null||addrs.size()<=0)
			throw new IllegalArgumentException("addr cannot be emtpy");
		if(block_size<=0)
			throw new IllegalArgumentException("block cannot <=0 ");
		this.memTp = memtp ;
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
		
	public PPIMemTp getMemTp()
	{
		return memTp ;
	}
	
	
	public List<PPIAddr> getAddrs()
	{
		return addrs ;
	}
	
	public MemTable<MemSeg8> getMemTable()
	{
		return this.memTb ;
	}
	
	boolean initCmds(PPIDriver drv)
	{
		ppiDrv = drv ;
		
		if(addrs==null||addrs.size()<=0)
			return false ;

		PPICmd curcmd = null ;
		int cur_reg = -1 ;
		ArrayList<PPIAddr> curaddrs = null ;
		for(PPIAddr ma:addrs)
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
			
			PPIAddr lastma = curaddrs.get(curaddrs.size()-1) ;
			int regnum = lastma.getOffsetBytes()-cur_reg+lastma.getValTP().getValByteLen() ;
			//regnum = regnum/2+regnum%2;
			curcmd = new PPICmdR((short)devId,memTp,cur_reg,(short)regnum)
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
			PPIAddr lastma = curaddrs.get(curaddrs.size()-1) ;
			//int regnum = lastma.getRegEnd()-cur_reg;
			int regnum = (lastma.getOffsetBytes()-cur_reg)+lastma.getValTP().getValByteLen() ;
			//regnum = regnum/2+regnum%2;
			//curcmd = new ModbusCmdReadWords(this.getFC(),this.scanInterMS,
			//			devId,cur_reg,regnum);
			
			curcmd = new PPICmdR((short)devId,memTp,cur_reg,(short)regnum)
					.withScanIntervalMS(this.scanInterMS);
			curcmd.initCmd(drv);
			//curcmd.setRecvTimeout(reqTO);
			//curcmd.setRecvEndTimeout(recvTO);
			cmd2addr.put(curcmd, curaddrs);
		}
		
		for(PPICmd mc:cmd2addr.keySet())
		{
			mc.withRecvTimeout(reqTO).withRecvEndTimeout(recvTO);
			if(log.isDebugEnabled())
				log.debug("init modbus cmd="+mc);
		}
		
		return true;
	}
	
//	/**
//	 * init as slave block.
//	 * it will set all bool value=false
//	 *                   word value=0
//	 */
//	public void initAsSlave()
//	{
//		boolean bbit = isBitCmd();
//		for(PPIAddr ma:this.addrs)
//		{
//			int regpos = ma.getRegPos() ;
//			//ma.getAddrTp()
//			int endpos = ma.getRegEnd() ;
//			int n = (endpos-regpos)/2;
//			if(bbit)
//			{
//				memTb.setValBool(regpos/8, regpos%8, false);
//			}
//			else
//			{
//				for(int k = 0 ; k < n; k ++)
//					memTb.setValNumber(ValTP.vt_int16, (regpos+k)*2, 0);
//			}
//		}
//	}
	
	
	
	private void setAddrError(List<PPIAddr> addrs)
	{
		if(addrs==null)
			return ;
		for(PPIAddr ma:addrs)
			ma.RT_setVal(null); 
	}
	

	private Object getValByAddr(PPIAddr da)
	{
		UAVal.ValTP vt = da.getValTP();
		if(vt==null)
			return null ;
		if(vt==UAVal.ValTP.vt_bool)
		{
			int regp = da.getOffsetBytes() ;
			int inbit = da.getInBits() ;
			int vv = memTb.getValNumber(UAVal.ValTP.vt_byte,regp,ByteOrder.LittleEndian).intValue() ;
			return (vv & (1<<inbit))>0 ;
		}
		else if(vt.isNumberVT())
		{
			return memTb.getValNumber(vt,da.getOffsetBytes(),ByteOrder.LittleEndian) ;
		}
		return null;
	}
	
	public boolean setValByAddr(PPIAddr da,Object v)
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
	
	
	
	public boolean runCmds(IConnEndPoint ep) throws Exception
	{
		this.runWriteCmdAndClear(ep);
		return runReadCmds(ep) ;
		
	}
	
	public void runCmdsErr()
	{
		runReadCmdsErr() ;
	}
	
	private void transMem2Addrs(List<PPIAddr> addrs)
	{
		for(PPIAddr ma:addrs)
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
	
//	public void onSnifferCmd(SnifferCmd sc)
//	{
//		if(this.devId!=sc.getDevId())
//			return ;
//		
//		for(ModbusCmd mc:cmd2addr.keySet())
//		{
//			List<PPIAddr> addrs = cmd2addr.get(mc) ;
//			onSnifferCmd(sc,mc,addrs);
//		}
//	}
//	
//	private void onSnifferCmd(SnifferCmd sc,ModbusCmd mc,List<PPIAddr> addrs)
//	{
//		for(PPIAddr maddr:addrs)
//		{
//			Object ov = sc.getValByAddr(maddr) ;
//			if(ov==null)
//				continue ;
//			
//			maddr.RT_setVal(ov);
//		}
//		
//		return ;
//	}
	
	private boolean runReadCmds(IConnEndPoint ep) throws Exception
	{
		//ArrayList<DevAddr> okaddrs = new ArrayList<>() ;
		boolean ret = true;
		for(PPICmd mc:cmd2addr.keySet())
		{
			if(!mc.tickCanRun())
				continue ;
			
			PPICmdR cmdr = (PPICmdR)mc ;
			Thread.sleep(this.interReqMs);
			
			List<PPIAddr> addrs = cmd2addr.get(mc) ;
			cmdr.doCmd(ep.getInputStream(),ep.getOutputStream());
			PPIMsgResp resp = cmdr.getResp();
			PPIMsgReq req = cmdr.getReq() ;
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
		for(PPICmd mc:cmd2addr.keySet())
		{
			
			List<PPIAddr> addrs = cmd2addr.get(mc) ;
			setAddrError(addrs);
			//transMem2Addrs(addrs);
		}
		return ret ;
	}
	
	private LinkedList<PPICmd> writeCmds = new LinkedList<>() ;
	
	private void runWriteCmdAndClear(IConnEndPoint ep) throws Exception
	{
		int s = writeCmds.size();
		if(s<=0)
			return ;
		
		PPICmd[] cmds = new PPICmd[s] ;
		synchronized(writeCmds)
		{
			for(int i = 0 ; i < s ; i ++)
				cmds[i] = writeCmds.removeFirst() ;
		}
		
		for(PPICmd mc:cmds)
		{
			if(!mc.tickCanRun())
				continue ;
			
			Thread.sleep(this.interReqMs);
			
			mc.doCmd(ep.getInputStream(),ep.getOutputStream());
		}
	}
	
	public boolean setWriteCmdAsyn(PPIAddr addr, Object v)
	{
		PPICmdW mc = new PPICmdW((short)devId,memTp,addr,v);
		mc.initCmd(ppiDrv);
//		switch(ma.getAddrTp())
//		{
//		case PPIAddr.COIL_OUTPUT:
//			boolean[] bvs = new boolean[1] ;
//			bvs[0] = (Boolean)v;
//			//mc = new ModbusCmdWriteBits(scanInterMS,this.devId,ma.getRegPos(),bvs) ;
//			mc = new ModbusCmdWriteBit(scanInterMS,this.devId,ma.getRegPos(), (Boolean)v) ;
//			mc.setRecvTimeout(reqTO);
//			mc.setRecvEndTimeout(recvTO);
//			//mc.setProtocol(modbusProtocal);
//			break ;
//		case PPIAddr.REG_HOLD:
//			if(!(v instanceof Number))
//				return false;
//			Number nv = (Number)v ;
//			ValTP vt = ma.getValTP() ;
//			int dlen = vt.getValByteLen()/2 ;
//			if(dlen<1)
//				return false;//not support
//			int[] vals = null ;
//			switch(vt)
//			{
//			case vt_int16:
//			case vt_uint16:
//				vals = new int[1] ;
//				vals[0] = nv.shortValue() ;
//				break ;
//			case vt_int32:
//			case vt_uint32:
//				vals = new int[2] ;
//				int intv = nv.intValue() ;
//				vals[1] = (intv>>16) & 0xFFFF ;
//				vals[0] = intv & 0xFFFF ;
//				break ;
//			case vt_int64:
//			case vt_uint64:
//				vals = new int[4] ;
//				long longv = nv.longValue() ;
//				vals[3] = (int)((longv>>48) & 0xFFFF) ;
//				vals[2] = (int)((longv>>32) & 0xFFFF) ;
//				vals[1] = (int)((longv>>16) & 0xFFFF) ;
//				vals[0] = (int)(longv & 0xFFFF) ;
//				break ;
//			case vt_float:
//				vals = new int[2] ;
//				intv = Float.floatToIntBits(nv.floatValue()) ;
//				vals[0] = (intv>>16) & 0xFFFF ;
//				vals[1] = intv & 0xFFFF ;
//				break ;
//			case vt_double:
//				vals = new int[4] ;
//				longv = Double.doubleToLongBits(nv.doubleValue()) ;
//				vals[0] = (int)((longv>>48) & 0xFFFF) ;
//				vals[1] = (int)((longv>>32) & 0xFFFF) ;
//				vals[2] = (int)((longv>>16) & 0xFFFF) ;
//				vals[3] = (int)(longv & 0xFFFF) ;
//				break ;
//			default:
//				return false;
//			}
//			
//			if(vals.length==1)
//			{
//				mc = new ModbusCmdWriteWord(this.scanInterMS,
//						devId,ma.getRegPos(),vals[0]);
//			}
//			else
//			{
//				mc = new ModbusCmdWriteWords(this.scanInterMS,
//						devId,ma.getRegPos(),vals);
//			}
//			 mc.setRecvTimeout(reqTO);
//			mc.setRecvEndTimeout(recvTO);
//			 break;
//		default:
//			return false;
//		}
		
		synchronized(writeCmds)
		{
			writeCmds.addLast(mc);
		}
		return true;
		
	}
}
