package org.iottree.driver.omron.hostlink.fins;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.hostlink.HLModel;
import org.iottree.driver.omron.hostlink.HLMsg;

import kotlin.NotImplementedError;

public class HLFinsCmdMemW extends HLCmd
{
	public static ILogger log = LoggerManager.getLogger(HLFinsCmdMemW.class) ;
	
	private int startAddr ;
	
	private int bitPos =-1;
	
	private boolean bBitOnly = false;
	
	private List<Boolean> bitVals = null ;
	
	private List<Short> wordVals = null ;
	
	transient private HLFinsReqMemW req = null ;
	
	transient private HLFinsRespOnlyEnd resp = null ;
	
	//transient byte[] retBs = null ;
	private transient boolean bAck = false ;
	
	public HLFinsCmdMemW()
	{
		//super((short)0,fx_mtp);
		///offet byte or T/C
		//this.wBytes = w_bytes;
		//so T /C must 
	}
	
	public HLFinsCmdMemW asBitVals(int startaddr,int bit_pos,List<Boolean> bitvals)
	{
		if(bit_pos<0 || bit_pos>15)
			throw new IllegalArgumentException("invalid bit pos") ;
		this.startAddr = startaddr;
		this.bitPos = bit_pos ;
		this.bitVals = bitvals ;
		this.bBitOnly = false ;
		return this ;
	}
	
	public HLFinsCmdMemW asBitOnlyVals(int startaddr,List<Boolean> bitvals)
	{
		this.startAddr = startaddr;
		//this.bitPos = bit_pos ;
		this.bitVals = bitvals ;
		this.bBitOnly = true ;
		return this ;
	}
	
	public HLFinsCmdMemW asWordVals(int startaddr,List<Short> wvals)
	{
		this.startAddr = startaddr;
		this.bitPos = -1 ;
		this.wordVals = wvals ;
		this.bBitOnly = false ;
		return this ;
	}
	
	
	public int getStartAddr()
	{
		return startAddr ;
	}
	

	public boolean isAck()
	{
		return this.bAck ;
	}
	
	protected void initCmd(HLFinsDriver drv,HLBlock block)
	{
		super.initCmd(drv,block);
		
		HLDevItem devitem = block.devItem ;
		
		HLModel m = (HLModel)devitem.getUADev().getDrvDevModel();
		FinsMode fm = m.getFinsMode() ;
		HLFinsReqMemW reqw = new HLFinsReqMemW(fm) ;
		if(this.bitPos>=0)
			reqw.asReqWBit(block.prefix, startAddr,bitPos, bitVals.size(), bitVals) ;
		else if(bBitOnly)
			reqw.asReqWBit(block.prefix, startAddr,0, bitVals.size(), bitVals) ;
		else
			reqw.asReqWWord(block.prefix ,startAddr,wordVals.size(),wordVals) ;
		
		if(!devitem.bNetOrSerial)
		{
			reqw.asFinsHeaderSerial() ;
			// TODO
		}
		else
		{
			//crr.asFinsHeaderNet(icf, gct, dna, da1, da2, sna, sa1, sa2, sid) TODO
			throw new NotImplementedError() ;
		}
		
		req = reqw ;
	}
	
	public boolean doCmd(InputStream inputs,OutputStream outputs,StringBuilder failedr)  throws Exception
	{
		Thread.sleep(this.drv.getCmdInterval());

		resp = null;
		
		// HLFinsRespMemR resp = new HLFinsRespMemR(this.req) ;
		HLMsg.clearInputStream(inputs,50) ;
		String str = req.writeTo(outputs);
		
		if(log.isDebugEnabled())
			log.debug("-> ["+str+"]");
		
		HLFinsRespOnlyEnd resp = (HLFinsRespOnlyEnd)req.readRespFrom(inputs, outputs,this.recvTimeout,this.failedRetryC) ;
		
		if(resp!=null && resp.isFinsEndOk())
		{
			onRespOk(resp);
			
			if(log.isDebugEnabled())
				log.debug("<- ok ["+resp.getRetTxt()+"]");
		}
		else
		{
			onRespErr(resp) ;
			if(log.isDebugEnabled())
				log.debug("<- err ["+resp.getRetTxt()+"] "+resp.getFinsEndCode().getErrorInf());
		}
		
		
		return true;
	}
	
	private void onRespOk(HLFinsRespOnlyEnd resp)
	{
		//resp.get
		this.resp = resp ;
		bAck = resp!=null ;
	}
	
	private void onRespErr(HLFinsRespOnlyEnd resp)
	{
		//resp.get
		this.resp = resp ;
		bAck = resp!=null ;
	}
	
	public HLFinsReqMemW getReq()
	{
		return this.req ;
	}
	
	public HLFinsRespOnlyEnd getResp()
	{
		return this.resp;
	}
	
}
