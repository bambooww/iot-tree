package org.iottree.driver.s7.ppi;

import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.util.Convert;

public class PPICmdR extends PPICmd
{


	private int readNum  ;
	
	private int offsetBytes ;
	
	
	transient private PPIMsgReq req = null ;
	
	transient private PPIMsgReqConfirm reqc = null ; 
	
	//transient byte[] retBs = null ;
	private transient PPIMsgResp resp = null ;
	
	public PPICmdR(short dev_addr,PPIMemTp ppi_mtp,int offset,int readnum)
	{
		super(dev_addr,ppi_mtp);
		
		this.offsetBytes = offset; //offet byte or T/C
		this.readNum = readnum; //read bytes
		//so T /C must 
	}
	
	public int getOffsetBytes()
	{
		return offsetBytes ;
	}
	
	public int getReadNum()
	{
		return readNum ;
	}
	
//	public byte[] getRetData()
//	{
//		return this.retBs ;
//	}
	
	void initCmd(PPIDriver drv)
	{
		super.initCmd(drv);
		
		if(this.ppiMemTp==PPIMemTp.T || this.ppiMemTp==PPIMemTp.C)
		{
			PPIMsgReqRTC reqr = new PPIMsgReqRTC() ;
			int rc ;
			int offset = this.offsetBytes ;
			//readNum is bytes to fit T C 
			if(this.ppiMemTp==PPIMemTp.T)
			{
				offset = this.offsetBytes/4 ;
				rc = readNum/4 ;
			}
			else
			{
				offset = this.offsetBytes/2 ;
				rc = readNum/2 ;
			}
			reqr.withMemTp(this.ppiMemTp).withTick(offset, (short)rc).withSorAddr(ppiDrv.getMasterID())
				.withDestAddr(devAddr);//(addr_str)
			this.req = reqr ;
		}
		else
		{
			PPIMsgReqR reqr = new PPIMsgReqR() ;
			reqr.withAddrByte(this.ppiMemTp,this.offsetBytes ,-1,readNum).withSorAddr(ppiDrv.getMasterID())
				.withDestAddr(devAddr);//(addr_str)
			this.req = reqr ;
		}	 
		
		reqc = new PPIMsgReqConfirm();
		reqc.withSorAddr(ppiDrv.getMasterID())
			.withDestAddr(devAddr) ;
	}
	
	public boolean doCmd(InputStream inputs,OutputStream outputs)  throws Exception
	{
		if(this.ppiMemTp==PPIMemTp.C || this.ppiMemTp==PPIMemTp.T)
			return doCmdTC(inputs,outputs);
		else
			return doCmdNor(inputs,outputs) ;
	}
	
	private boolean doCmdNor(InputStream inputs,OutputStream outputs)  throws Exception
	{
//		InputStream inputs = ep.getInputStream() ;
//		OutputStream outputs = ep.getOutputStream() ;
		Thread.sleep(ppiDrv.getCmdInterval());
		resp = null;
		PPIMsg.clearInputStream(inputs,50) ;
		//write
		byte[] bs1 = req.toBytes();
		byte[] bs2 = reqc.toBytes();
		
		if(PPIMsg.log.isTraceEnabled())
		{
			PPIMsg.log.trace("req "+this.ppiMemTp+" ->"+Convert.byteArray2HexStr(bs1, " "));
		}
		
		outputs.write(bs1);
		int c = PPIMsg.readCharTimeout(inputs, ppiDrv.getReadTimeout()) ;
		if(c!=0xE5 && c!=0xF9)
		{
			outputs.write(bs2);//make plc run ok
			Thread.sleep(10);
			return false;
		}
		Thread.sleep(10);//no sleep may do error
		
		//System.out.println("reqc->"+Convert.byteArray2HexStr(bs2, " "));
		outputs.write(bs2);
		Thread.sleep(5);
		StringBuilder failedr = new StringBuilder() ;
		PPIMsgRespR resp = PPIMsgRespR.parseFromStream(inputs, ppiDrv.getReadTimeout(),failedr) ;
		if(resp==null)
		{
			if(PPIMsg.log.isDebugEnabled())
				PPIMsg.log.debug(" failed="+failedr) ;
			return false;
		}
		//System.out.println("resp <-"+Convert.byteArray2HexStr(resp.toBytes(), " "));
		onResp(resp);
		
		return true;
	}
	
	public boolean doCmdTC(InputStream inputs,OutputStream outputs)  throws Exception
	{
//		InputStream inputs = ep.getInputStream() ;
//		OutputStream outputs = ep.getOutputStream() ;
		Thread.sleep(ppiDrv.getCmdInterval());
		//retBs = null;
		resp = null;
		//inputs.skip(inputs.available()) ;
		PPIMsg.clearInputStream(inputs,50) ;
		//write
		byte[] bs1 = req.toBytes();
		if(PPIMsg.log.isTraceEnabled())
		{
			PPIMsg.log.trace("req "+this.ppiMemTp+"  ->"+Convert.byteArray2HexStr(bs1, " "));
		}
		outputs.write(bs1);
		Thread.sleep(5);
		int c = PPIMsg.readCharTimeout(inputs, ppiDrv.getReadTimeout()) ;
		if(c!=0xE5 && c!=0xF9)
			return false;
		Thread.sleep(10);//no sleep may do error
		byte[] bs2 = reqc.toBytes();
		//System.out.println("reqc->"+Convert.byteArray2HexStr(bs2, " "));
		outputs.write(bs2);
		//Thread.sleep(1);
		StringBuilder failedr = new StringBuilder() ;
		PPIMsgRespRTC resp = PPIMsgRespRTC.parseFromStream(this.ppiMemTp,inputs, ppiDrv.getReadTimeout(),failedr) ;
		if(resp==null)
		{
			if(PPIMsg.log.isDebugEnabled())
				PPIMsg.log.debug(" failed="+failedr) ;
			return false;
		}
		//System.out.println("resp <-"+Convert.byteArray2HexStr(resp.toBytes(), " "));
		onResp(resp);
		
		return true;
	}
	
	

	private void onResp(PPIMsgRespR resp)
	{
		//resp.get
		//System.out.println(resp) ;
		this.resp = resp ;
		//retBs = resp.getRespData();
	}
	
	public PPIMsgReq getReq()
	{
		return this.req ;
	}
	
	private void onResp(PPIMsgRespRTC resp)
	{
		this.resp = resp ;
	}
	
	public PPIMsgResp getResp()
	{
		return this.resp;
	}
	
	public PPIMsgRespR getRespR()
	{
		return (PPIMsgRespR)this.resp;
	}
	
	public PPIMsgRespRTC getRespRTC()
	{
		return (PPIMsgRespRTC)resp ;
	}
	
	
}
