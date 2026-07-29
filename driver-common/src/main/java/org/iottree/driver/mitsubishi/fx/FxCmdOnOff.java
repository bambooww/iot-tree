package org.iottree.driver.mitsubishi.fx;

import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.util.Convert;

/**
 * bit pos force On / Off Writer cmd
 * @author jason.zhu
 *
 */
public class FxCmdOnOff extends FxCmd
{
	private int baseAddr ; 
	
	private boolean bOn ;
	
	private int startAddr ;
	
	
	transient private FxMsgReqOnOff req = null ;
	
	private transient boolean bAck = false ;
	
	public FxCmdOnOff(int base_addr,int startaddr,boolean b_on)
	{
		this.baseAddr = base_addr ;
		this.startAddr = startaddr; //offet byte or T/C
		this.bOn = b_on;
	}
	
	public int getBaseAddr()
	{
		return this.baseAddr ;
	}
	
	public int getStartAddr()
	{
		return startAddr ;
	}
	
	public boolean isOn()
	{
		return this.bOn ;
	}
	
	public boolean isOff()
	{
		return !this.bOn ;
	}
	
//	public byte[] getRetData()
//	{
//		return this.retBs ;
//	}
	
	void initCmd(FxDriver drv,boolean b_ext)
	{
		super.initCmd(drv,b_ext);
		
		FxMsgReqOnOff reqr = new FxMsgReqOnOff() ;
		
		reqr.asStartAddr(this.baseAddr,startAddr).asOnOrOff(bOn).asExt(b_ext);
		
		req = reqr ;
	}
	
	public boolean doCmd(InputStream inputs,OutputStream outputs)  throws Exception
	{
		Thread.sleep(this.drv.getCmdInterval());
		
		//write
		byte[] bs1 = req.toBytes();
		
		//if(FxMsg.log.isTraceEnabled())
		{
			FxMsg.log.trace("req ->"+Convert.byteArray2HexStr(bs1, " "));
			System.out.println("req ->"+Convert.byteArray2HexStr(bs1, " ")) ;
		}
		FxMsg.clearInputStream(inputs,50) ;
		outputs.write(bs1);
		int c = FxMsg.readCharTimeout(inputs, recvTimeout);
		bAck = (c==FxMsg.ACK) ;
		System.out.println(" FxCmdOnOff - "+this.bAck) ;
		return true;
	}
	
	public boolean isAck()
	{
		return this.bAck ;
	}
	
	public FxMsgReqOnOff getReq()
	{
		return this.req ;
	}
	
}
