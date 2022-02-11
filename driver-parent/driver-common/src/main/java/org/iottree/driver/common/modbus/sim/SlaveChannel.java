package org.iottree.driver.common.modbus.sim;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.UATag;
import org.iottree.core.sim.SimChannel;
import org.iottree.core.sim.SimConn;
import org.iottree.core.sim.SimCP;
import org.iottree.core.sim.SimDev;
import org.iottree.core.task.TaskAction;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.util.xmldata.data_val;
import org.iottree.driver.common.modbus.ModbusCmd;
import org.iottree.driver.common.modbus.ModbusCmdReadBits;
import org.iottree.driver.common.modbus.ModbusCmdReadWords;
import org.iottree.driver.common.modbus.ModbusCmdWriteBit;
import org.iottree.driver.common.modbus.ModbusCmdWriteWord;
import org.iottree.driver.common.modbus.ModbusParserReq;
import org.iottree.driver.common.modbus.sim.SlaveDevSeg.BoolDatas;
import org.iottree.driver.common.modbus.sim.SlaveDevSeg.Int16Datas;
import org.iottree.driver.common.modbus.sim.SlaveDevSeg.SlaveData;

/**
 *  a slave channel will match a bus
 *  which will has one or more devices
 * @author jason.zhu
 *
 */
@data_class
public class SlaveChannel extends SimChannel  implements Runnable
{
	private ArrayList<Integer> limitIds = null; 
	public SlaveChannel()
	{
		
	}
	
	public String getTp()
	{
		return "mslave" ;
	}
	
	public String getTpTitle()
	{
		return "Modbus Slave" ;
	}
	
	public SimDev createNewDev()
	{
		return new SlaveDev() ;
	}
		
	static final int BUF_LEN = 255;
	

	private void delay(int ms)
	{
		try
		{
			Thread.sleep(ms) ;
		}
		catch(Exception ee)
		{}
	}
	
	public boolean RT_init(StringBuilder failedr)
	{
		if(!super.RT_init(failedr))
			return false;
		
		List<SimDev> devs = listDevItems() ;
		if(devs==null||devs.size()<=0)
		{
			failedr.append("no devices in channel") ;
			return false;
		}
		
		ArrayList<Integer> ids = new ArrayList<>() ;
		for(SimDev dev:devs)
		{
			if(!dev.RT_init(failedr))
			{
				return false;
			}
			
			SlaveDev sd = (SlaveDev)dev;
			ids.add(sd.getDevAddr()) ;
		}
		limitIds = ids ;
		
		
		return true ;
	}
	
	protected void RT_runConnInLoop(SimConn sc) throws Exception
	{
		ModbusParserReq mp = (ModbusParserReq)sc.getRelatedOb() ;
		if(mp==null)
		{
			mp = new ModbusParserReq() ;
			mp.asLimitDevIds(limitIds);
			sc.setRelatedOb(mp);
		}
		
		PushbackInputStream inputs = sc.getPushbackInputStream();
		ModbusCmd reqmc = mp.parseReqCmdInLoopRTU(inputs) ;
		if(reqmc==null)
			return ;
		byte[] respbs = onReqAndResp(reqmc) ;
		if(respbs!=null)
		{
			OutputStream outputs = sc.getConnOutputStream() ;
			outputs.write(respbs) ;
			outputs.flush() ;
		}
	}
		
	protected void RT_runConnInLoop0(SimConn sc) throws Exception
	{
			//boolean b_in_recv = false;
			int last_dlen = 0 ;
			long last_dt = -1 ;
			
			long last_no_dt = System.currentTimeMillis() ;
			
			byte[] buf = new byte[BUF_LEN] ;
			int len = 0 ;
			
			while (true)
			{
				//
				delay(1) ;
//				
//				SimCP conn = getConn() ;
//				if(conn==null)
//					continue ;
				
				InputStream inputs =sc.getConnInputStream();
				OutputStream outputs = sc.getConnOutputStream();
				if(inputs==null)
					continue;
				
				if(last_dlen==0)
				{//no data,not in recv
					//System.out.println("avlen="+serInputs.available()) ;
					
	//				int c = serInputs.read(buf,len,BUF_LEN-len) ;
	//				if(c>0)
	//					len += c ;
	//				System.out.println("rlen="+len) ;
					if(inputs.available()<=0)
					{
						delay(5) ;
						if(System.currentTimeMillis()-last_no_dt>5000)
						{
							last_no_dt = System.currentTimeMillis() ;
							sc.pulseConn();
						}
						else
						{
							
						}
					}
					else
					{
						last_dlen = inputs.available() ;
						last_dt = System.currentTimeMillis() ;
					}
					
					continue;
				}
				
				//recv
				if(inputs.available()>last_dlen)
				{
					last_dlen = inputs.available() ;
					last_dt = System.currentTimeMillis() ;
					continue ;
				}
				
				//check recv end
				if(System.currentTimeMillis()-last_dt<10)
				{//recv not end
					continue ;
				}
				
				int rlen = last_dlen ;
				try
				{
					//recv end
					if(last_dlen>255)
					{//err data
						inputs.skip(last_dlen) ;
						continue ;
					}
				}
				finally
				{
					last_dlen = 0 ;
					last_dt = 0 ;
				}
				
				byte[] rdata = new byte[rlen] ;
				inputs.read(rdata) ;
				
				long st = System.currentTimeMillis() ;
				
				int[] pl = new int[1] ;
				pl[0] = 0 ;
				
				do
				{
					if(pl[0]<0)
						break ;
					
					if(pl[0]>0)
					{
						byte[] crbs = new byte[rdata.length-pl[0]] ;
						System.arraycopy(rdata, pl[0], crbs, 0, crbs.length) ;
						rdata = crbs ;
					}
					
					byte[] respbs = onReadReqAndResp(rdata,pl) ;
					
					//System.out.println("2 on req resp cost="+(System.currentTimeMillis()-st)+" replen="+respbs.length) ;
					if(respbs!=null)
					{
						outputs.write(respbs) ;
						outputs.flush() ;
					}
				}
				while(pl[0]>=0) ;
			}// end of while
	}
	
	/**
	 * @param reqbs
	 * @return
	 */
	private byte[] onReadReqAndResp(byte[] reqbs,int[] parseleft)
	{
		ModbusCmd mc = ModbusCmd.parseRequest(reqbs,parseleft) ;
		if(mc==null)
			return null ;
		
		return onReqAndResp(mc);
	}

	private byte[] onReqAndResp(ModbusCmd mc)
	{
		if(mc instanceof ModbusCmdReadBits)
		{
			ModbusCmdReadBits mcb =(ModbusCmdReadBits)mc ;
			return onReqAndRespReadBits(mcb);
		}
		else if(mc instanceof ModbusCmdReadWords)
		{
			return onReqAndRespReadWords((ModbusCmdReadWords)mc) ;
		}
		else if(mc instanceof ModbusCmdWriteBit)
		{
			ModbusCmdWriteBit wb = (ModbusCmdWriteBit)mc ;
			return onReqAndRespWriteBit(wb) ;
		}
		else if(mc instanceof ModbusCmdWriteWord)
		{
			ModbusCmdWriteWord ww = (ModbusCmdWriteWord)mc ;
			return onReqAndRespWriteWord(ww) ;
		}
		
		return null ;
	}
	
	private byte[] onReqAndRespWriteBit(ModbusCmdWriteBit mcb)
	{
		short fc = mcb.getFC() ;
		
		short devid = mcb.getDevAddr();
		
		int req_idx = mcb.getRegAddr() ;
		boolean bv = mcb.getWriteVal() ;
		
		for(SimDev d:this.listDevItems())
		{
			SlaveDev di = (SlaveDev)d ;
			if(di.getDevAddr()!=devid)
				continue ;

			List<SlaveDevSeg> segs = di.getSegs() ;
			for(SlaveDevSeg seg:segs)
			{
				if(seg.getFC()!=ModbusCmd.MODBUS_FC_READ_COILS)
					continue ;
				int seg_regidx = seg.getRegIdx() ;
				int seg_regnum = seg.getRegNum() ;
				if(req_idx<seg_regidx)
					continue ;
				if(req_idx>=seg_regidx+seg_regnum)
					continue ;
				
				seg.setSlaveDataBool(req_idx-seg_regidx,bv) ;
				break ;
			}
		}
		
		return ModbusCmdWriteBit.createResp(devid,req_idx,bv);
	}
	
	
	private byte[] onReqAndRespWriteWord(ModbusCmdWriteWord mcb)
	{
		short fc = mcb.getFC() ;
		
		short devid = mcb.getDevAddr();
		
		int req_idx = mcb.getRegAddr() ;
		int bv = mcb.getWriteVal() ;
		
		for(SimDev d:this.listDevItems())
		{
			SlaveDev di = (SlaveDev)d ;
			if(di.getDevAddr()!=devid)
				continue ;

			List<SlaveDevSeg> segs = di.getSegs() ;
			for(SlaveDevSeg seg:segs)
			{
				if(seg.getFC()!=ModbusCmd.MODBUS_FC_READ_HOLD_REG)
					continue ;
				int seg_regidx = seg.getRegIdx() ;
				int seg_regnum = seg.getRegNum() ;
				if(req_idx<seg_regidx)
					continue ;
				if(req_idx>=seg_regidx+seg_regnum)
					continue ;
				
				seg.setSlaveDataInt16(req_idx-seg_regidx,(short)bv) ;
				break ;
			}
		}
		
		return ModbusCmdWriteWord.createResp(devid,(short)req_idx,(short)bv);
	}

	private byte[] onReqAndRespReadBits(ModbusCmdReadBits mcb)
	{
		short fc = mcb.getFC() ;
		
		short devid = mcb.getDevAddr();
		
		int req_idx = mcb.getRegAddr() ;
		int req_num = mcb.getRegNum() ;
		boolean[] resp = new boolean[req_num] ;
		for(int i=0;i<req_num;i++)
			resp[i] = false;
		
		for(SimDev d:this.listDevItems())
		{
			SlaveDev di = (SlaveDev)d ;
			if(di.getDevAddr()!=devid)
				continue ;

			List<SlaveDevSeg> segs = di.getSegs() ;
			for(SlaveDevSeg seg:segs)
			{
				if(seg.getFC()!=fc)
					continue ;
				int seg_regidx = seg.getRegIdx() ;
				int seg_regnum = seg.getRegNum() ;
				if(req_idx+req_num<=seg_regidx)
					continue ;
				if(req_idx>seg_regidx+seg_regnum)
					continue ;
				
//				SlaveData sd = seg.getSlaveData() ;
//				if(sd==null || !(sd instanceof BoolDatas))
//				{
//					continue ;
//				}
//				BoolDatas msb = (BoolDatas)sd ;
//				//
//				boolean[] bs = msb.getBoolDatas();//.getBoolUsingDatas() ;
				boolean[] bs = seg.getSlaveDataBool() ;
				if(bs==null)
					continue ;
				
				if(req_idx<seg_regidx)
				{
					if(req_idx+req_num<seg_regidx+bs.length)
						System.arraycopy(bs, 0, resp, seg_regidx-req_idx, req_num-(seg_regidx-req_idx)) ;
					else
						System.arraycopy(bs, 0, resp, seg_regidx-req_idx, bs.length) ;
				}
				else
				{
					if(req_idx+req_num<seg_regidx+bs.length)
						System.arraycopy(bs,req_idx-seg_regidx, resp, 0, req_num) ;
					else
						System.arraycopy(bs,req_idx-seg_regidx, resp, 0, bs.length-(req_idx-seg_regidx)) ;
				}
			}
		}
		
		return ModbusCmdReadBits.createResp(devid,mcb.getFC(),resp);
	}
	
	
	private byte[] onReqAndRespReadWords(ModbusCmdReadWords mcb)
	{
		int fc = mcb.getFC() ;
		short devid = mcb.getDevAddr();
		
		int req_idx = mcb.getRegAddr() ;
		int req_num = mcb.getRegNum() ;
		
		
		short[] resp = new short[req_num] ;
		for(int i=0;i<req_num;i++)
			resp[i] = 0;
		
		for(SimDev d:this.listDevItems())
		{
			SlaveDev di = (SlaveDev)d ;
			if(di.getDevAddr()!=devid)
				continue ;

			List<SlaveDevSeg> segs = di.getSegs() ;
			for(SlaveDevSeg seg:segs)
			{
				if(seg.getFC()!=fc)
					continue ;
				int seg_regidx = seg.getRegIdx() ;
				int seg_regnum = seg.getRegNum() ;
				if(req_idx+req_num<=seg_regidx)
					continue ;
				if(req_idx>seg_regidx+seg_regnum)
					continue ;
				
//				SlaveData sd = seg.getSlaveData() ;
//				if(sd==null || !(sd instanceof Int16Datas))
//				{
//					continue ;
//				}
//				Int16Datas msb = (Int16Datas)sd ;

//				short[] bs = msb.getInt16Datas() ;
				
				short[] bs = seg.getSlaveDataInt16() ;
				if(bs==null)
					continue ;
				
				if(req_idx<seg_regidx)
				{
					if(req_idx+req_num<seg_regidx+bs.length)
						System.arraycopy(bs, 0, resp, seg_regidx-req_idx, req_num-(seg_regidx-req_idx)) ;
					else
						System.arraycopy(bs, 0, resp, seg_regidx-req_idx, bs.length) ;
				}
				else
				{
					if(req_idx+req_num<seg_regidx+bs.length)
						System.arraycopy(bs,req_idx-seg_regidx, resp, 0, req_num) ;
					else
						System.arraycopy(bs,req_idx-seg_regidx, resp, 0, bs.length-(req_idx-seg_regidx)) ;
				}
			}
		}
		
		return ModbusCmdReadWords.createResp(devid,mcb.getFC(),resp);
	}

	@Override
	protected void onConnOk(SimConn sc)
	{
		
	}

	@Override
	protected void onConnBroken(SimConn sc)
	{
		
	}
}
