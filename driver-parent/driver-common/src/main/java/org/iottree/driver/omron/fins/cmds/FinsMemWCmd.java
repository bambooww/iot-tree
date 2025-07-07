package org.iottree.driver.omron.fins.cmds;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.iottree.core.UADev;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.omron.fins.FinsBlock;
import org.iottree.driver.omron.fins.FinsCmd;
import org.iottree.driver.omron.fins.FinsDevItem;
import org.iottree.driver.omron.fins.FinsDriver;
import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.fins.FinsModel;
import org.iottree.driver.omron.fins.FinsMsg;

import kotlin.NotImplementedError;

public class FinsMemWCmd extends FinsCmd
{
	public static ILogger log = LoggerManager.getLogger(FinsMemWCmd.class) ;
	
	private int startAddr ;
	
	private int bitPos =-1;
	
	private boolean bBitOnly = false;
	
	private List<Boolean> bitVals = null ;
	
	private List<Short> wordVals = null ;
	
	transient private FinsMemWReq req = null ;
	
	transient private FinsMemWResp resp = null ;
	
	//transient byte[] retBs = null ;
	private transient boolean bAck = false ;
	
	public FinsMemWCmd()
	{
		//super((short)0,fx_mtp);
		///offet byte or T/C
		//this.wBytes = w_bytes;
		//so T /C must 
	}
	
	public FinsMemWCmd asBitVals(int startaddr,int bit_pos,List<Boolean> bitvals)
	{
		if(bit_pos<0 || bit_pos>15)
			throw new IllegalArgumentException("invalid bit pos") ;
		this.startAddr = startaddr;
		this.bitPos = bit_pos ;
		this.bitVals = bitvals ;
		this.bBitOnly = false ;
		return this ;
	}
	
	public FinsMemWCmd asBitOnlyVals(int startaddr,List<Boolean> bitvals)
	{
		this.startAddr = startaddr;
		//this.bitPos = bit_pos ;
		this.bitVals = bitvals ;
		this.bBitOnly = true ;
		return this ;
	}
	
	public FinsMemWCmd asWordVals(int startaddr,List<Short> wvals)
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
	
	public void initCmd(FinsDriver drv,FinsBlock block)
	{
		super.initCmd(drv,block);
		
		FinsDevItem devitem = block.getDevItem() ;
		
		FinsModel m = (FinsModel)devitem.getUADev().getDrvDevModel();
		FinsMode fm = m.getFinsMode() ;
		FinsMemWReq reqw = new FinsMemWReq(fm) ;
		if(this.bitPos>=0)
			reqw.asReqWBit(block.getPrefix(), startAddr,bitPos, bitVals.size(), bitVals) ;
		else if(bBitOnly)
			reqw.asReqWBit(block.getPrefix(), startAddr,0, bitVals.size(), bitVals) ;
		else
			reqw.asReqWWord(block.getPrefix() ,startAddr,wordVals.size(),wordVals) ;
		
		UADev dev = devitem.getUADev() ;
		int sa1 = dev.getOrDefaultPropValueInt(FinsDriver.PG_FINS_NET, "sa1", 0) ;
		int da1 = dev.getOrDefaultPropValueInt(FinsDriver.PG_FINS_NET, "da1", 0) ;
		reqw.asFinsHeader(0, da1, 0, 0, sa1, 0) ;
		
		if(devitem.isNetTcp())
		{
			
		}
		else
		{
			//reqw.asFinsHeaderSerial() ;
			// TODO
			//crr.asFinsHeaderNet(icf, gct, dna, da1, da2, sna, sa1, sa2, sid) TODO
			//throw new NotImplementedError() ;
		}
		
		req = reqw ;
	}
	
	public boolean doCmd(InputStream inputs,OutputStream outputs,StringBuilder failedr)  throws Exception
	{
		Thread.sleep(this.drv.getCmdInterval());

		resp = null;
		
		// HLFinsRespMemR resp = new HLFinsRespMemR(this.req) ;
		FinsMsg.clearInputStream(inputs,50) ;
		//String str = req.writeTo(outputs);
		req.writeOutTCP(outputs);
		
//		if(log.isDebugEnabled())
//			log.debug("-> ["+str+"]");
		
		resp = new FinsMemWResp(req) ;
		int max_len = 10240 ;
		if(!resp.readFromAsTcp(inputs, this.recvTimeout,max_len,failedr))
		{
			if(log.isDebugEnabled())
				log.debug("write resp failed <- ["+failedr+"]");
		}
		
		//FinsMemWResp resp = (FinsMemWResp)req.readRespFrom(inputs, outputs,this.recvTimeout,this.failedRetryC) ;
		
		if(resp.getErrCode()==0)
		{
			onRespOk(resp);
			
			//if(log.isDebugEnabled())
			//	log.debug("<- ok ["+resp.getRetTxt()+"]");
		}
		else
		{
			onRespErr(resp) ;
			//if(log.isDebugEnabled())
			//	log.debug("<- err ["+resp.getRetTxt()+"] "+resp.getFinsEndCode().getErrorInf());
		}
		
		
		return true;
	}
	
	private void onRespOk(FinsMemWResp resp)
	{
		//resp.get
		this.resp = resp ;
		bAck = resp!=null ;
	}
	
	private void onRespErr(FinsMemWResp resp)
	{
		//resp.get
		this.resp = resp ;
		bAck = resp!=null ;
	}
	
	public FinsMemWReq getReq()
	{
		return this.req ;
	}
	
	public FinsMemWResp getResp()
	{
		return this.resp;
	}
	
}
