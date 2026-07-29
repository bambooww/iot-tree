package org.iottree.driver.mitsubishi.fx;

import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.util.Convert;

public class FxCmdW extends FxCmd
{
	private int baseAddr ; 
	
	private byte[] wBytes  ;
	
	private int startAddr ;
	
	
	transient private FxMsgReqW req = null ;
	
	//transient byte[] retBs = null ;
	private transient boolean bAck = false ;
	
	public FxCmdW(int base_addr,int startaddr,byte[] w_bytes)
	{
		//super((short)0,fx_mtp);
		this.baseAddr = base_addr ;
		this.startAddr = startaddr; //offet byte or T/C
		this.wBytes = w_bytes;
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
	
	public byte[] getWriteBytes()
	{
		return wBytes ;
	}

	public boolean isAck()
	{
		return this.bAck ;
	}
	
	void initCmd(FxDriver drv,boolean b_ext)
	{
		super.initCmd(drv,b_ext);
		
		FxMsgReqW reqw = new FxMsgReqW() ;
		
		reqw.asStartAddr(this.baseAddr,startAddr).asBytesVal(this.wBytes).asExt(b_ext);
		
		req = reqw ;
	}
	
	public boolean doCmd(InputStream inputs,OutputStream outputs)  throws Exception
	{
		Thread.sleep(this.drv.getCmdInterval());

		//write
		byte[] bs1 = req.toBytes();
		
		if(FxMsg.log_w.isTraceEnabled())
		{
			FxMsg.log_w.trace("reqw ->"+Convert.byteArray2HexStr(bs1, " "));
			//System.out.println("reqw->"+Convert.byteArray2HexStr(bs1, " ")) ;
		}
		
		FxMsg.clearInputStream(inputs,50) ;
		outputs.write(bs1);
		int c = FxMsg.readCharTimeout(inputs, recvTimeout);
		bAck = (c==FxMsg.ACK) ;
		
		return true;
	}
	
	
	
	public FxMsgReqW getReq()
	{
		return this.req ;
	}
	
	
}
