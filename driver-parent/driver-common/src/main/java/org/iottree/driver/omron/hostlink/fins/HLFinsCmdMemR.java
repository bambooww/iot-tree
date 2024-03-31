package org.iottree.driver.omron.hostlink.fins;

import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.hostlink.HLModel;
import org.iottree.driver.omron.hostlink.HLMsg;

import kotlin.NotImplementedError;

public class HLFinsCmdMemR extends HLCmd
{
	//private int baseAddr ; 
	
	private int readNum  ;
	
	private int startAddr ;
	
	
	transient private HLFinsReqMemR req = null ;
	
	//transient byte[] retBs = null ;
	private transient HLFinsRespMemR resp = null ;
	
	public HLFinsCmdMemR(int startaddr,int readnum)
	{
		//super((short)0,fx_mtp);
		//this.baseAddr = base_addr ;
		this.startAddr = startaddr; //offet byte or T/C
		this.readNum = readnum; //read bytes
		//so T /C must 
	}
	
//	public int getBaseAddr()
//	{
//		return this.baseAddr ;
//	}
	
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
	
	@Override
	protected void initCmd(HLFinsDriver drv,HLBlock block)
	{
		
		super.initCmd(drv,block);
		
		HLDevItem devitem = block.devItem ;
		
		HLModel m = (HLModel)devitem.getUADev().getDrvDevModel();
		FinsMode fm = m.getFinsMode() ;
		HLFinsReqMemR crr = new HLFinsReqMemR(fm) ;
		// all read by word
		
		crr.asReqR(block.prefix,false, startAddr,readNum) ;
		
		if(!devitem.bNetOrSerial)
		{
			crr.asFinsHeaderSerial() ;
			// TODO
		}
		else
		{
			//crr.asFinsHeaderNet(icf, gct, dna, da1, da2, sna, sa1, sa2, sid) TODO
			throw new NotImplementedError() ;
		}
		
		req = crr ;
	}
	
	public boolean doCmd(InputStream inputs,OutputStream outputs)  throws Exception
	{
		Thread.sleep(this.drv.getCmdInterval());
		resp = null;
		
		// HLFinsRespMemR resp = new HLFinsRespMemR(this.req) ;
		HLMsg.clearInputStream(inputs,50) ;
		req.writeTo(outputs);
		
		HLFinsRespMemR resp = (HLFinsRespMemR)req.readRespFrom(inputs, outputs) ;
		
		onResp(resp);
		
		return true;
	}
	
	
	private void onResp(HLFinsRespMemR resp)
	{
		//resp.get
		this.resp = resp ;
	}
	
	public HLFinsReqMemR getReq()
	{
		return this.req ;
	}
	
	public HLFinsRespMemR getResp()
	{
		return this.resp;
	}
	
}
