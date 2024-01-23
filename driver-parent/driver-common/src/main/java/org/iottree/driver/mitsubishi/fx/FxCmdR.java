package org.iottree.driver.mitsubishi.fx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.util.Convert;
import org.iottree.driver.s7.ppi.PPIDriver;


public class FxCmdR extends FxCmd
{
	private int baseAddr ; 
	
	private int readNum  ;
	
	private int startAddr ;
	
	
	transient private FxMsgReq req = null ;
	
	//transient byte[] retBs = null ;
	private transient FxMsgResp resp = null ;
	
	public FxCmdR(int base_addr,int startaddr,int readnum)
	{
		//super((short)0,fx_mtp);
		this.baseAddr = base_addr ;
		this.startAddr = startaddr; //offet byte or T/C
		this.readNum = readnum; //read bytes
		//so T /C must 
	}
	
	public int getBaseAddr()
	{
		return this.baseAddr ;
	}
	
	public int getStartAddr()
	{
		return startAddr ;
	}
	
	public int getReadNum()
	{
		return readNum ;
	}
	
//	public byte[] getRetData()
//	{
//		return this.retBs ;
//	}
	
	void initCmd(FxDriver drv)
	{
		super.initCmd(drv);
		
		FxMsgReq reqr = new FxMsgReq() ;
		
		reqr.asStartAddr(this.baseAddr,startAddr).asByteNum(readNum);
		
		req = reqr ;
	}
	
	public boolean doCmd(InputStream inputs,OutputStream outputs)  throws Exception
	{
		Thread.sleep(this.drv.getCmdInterval());
		resp = null;
		
		//write
		byte[] bs1 = req.toBytes();
		
		//if(FxMsg.log.isTraceEnabled())
		{
			FxMsg.log.trace("req ->"+Convert.byteArray2HexStr(bs1, " "));
			System.out.println("req ->"+Convert.byteArray2HexStr(bs1, " ")) ;
		}
		FxMsgResp resp = new FxMsgResp(this.readNum) ;
		
		FxMsg.clearInputStream(inputs,50) ;
		outputs.write(bs1);
		resp.readFromStream(inputs, this.recvTimeout) ;
		if(!resp.readOk)
		{
				if(FxMsg.log.isDebugEnabled())
					FxMsg.log.debug("read failed,"+resp.errInf) ;
				return false;
			//throw new IOException("read failed,"+resp.errInf) ;
		}
		
		byte[] bs = resp.byteBuf ;
		System.out.println(Convert.byteArray2HexStr(bs, " ")) ;		
		
		//System.out.println("resp <-"+Convert.byteArray2HexStr(resp.toBytes(), " "));
		onResp(resp);
		
		return true;
	}
	
	
	private void onResp(FxMsgResp resp)
	{
		//resp.get
		this.resp = resp ;
	}
	
	public FxMsgReq getReq()
	{
		return this.req ;
	}
	
	public FxMsgResp getResp()
	{
		return this.resp;
	}
	
}
