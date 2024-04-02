package org.iottree.driver.omron.hostlink.fins;

import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.hostlink.HLException;
import org.iottree.driver.omron.hostlink.HLModel;
import org.iottree.driver.omron.hostlink.HLMsg;

import kotlin.NotImplementedError;

public class HLFinsCmdMemR extends HLCmd
{
	public static ILogger log = LoggerManager.getLogger(HLFinsCmdMemR.class) ;
	
	//private int baseAddr ; 
	
	private int readNum  ;
	
	private int startAddr ;
	
	private boolean bReadBit = false;
	
	transient private HLFinsReqMemR req = null ;
	
	//transient byte[] retBs = null ;
	private transient HLFinsRespMemR resp = null ;
	
	public HLFinsCmdMemR(int startaddr,int readnum,boolean readbit)
	{
		//super((short)0,fx_mtp);
		//this.baseAddr = base_addr ;
		this.startAddr = startaddr; //offet byte or T/C
		this.readNum = readnum; //read bytes
		this.bReadBit = readbit ;
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
	
	public boolean isReadBit()
	{
		return bReadBit;
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
		
		crr.asReqR(block.prefix,bReadBit, startAddr,0,readNum) ;
		
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
	
	public boolean doCmd(InputStream inputs,OutputStream outputs,StringBuilder failedr)  throws Exception
	{
		Thread.sleep(this.drv.getCmdInterval());
		resp = null;
		
		// HLFinsRespMemR resp = new HLFinsRespMemR(this.req) ;
		HLMsg.clearInputStream(inputs,50) ;
		String str = req.writeTo(outputs);
		if(log.isDebugEnabled())
			log.debug("-> ["+str+"]");
		
		HLFinsRespMemR resp = null;
		
		try
		{
			resp = (HLFinsRespMemR)req.readRespFrom(inputs, outputs,this.recvTimeout,this.failedRetryC) ;
		
			onResp(resp);
			
	//		if(log.isDebugEnabled())
	//			log.debug("<- ["+resp.getRetTxt()+"]");
			
			//onResp(resp);
			
			if(resp.isFinsEndOk())
			{
				if(log.isDebugEnabled())
					log.debug("ok <- ["+resp.getRetTxt()+"]");
			}
			else
			{
				//onRespErr(resp) ;
				if(log.isDebugEnabled())
					log.debug("err <- ["+resp.getRetTxt()+"] "+resp.getFinsEndCode().getErrorInf());
			}
		}
		catch(HLException ee)
		{
			if(ee.getErrCode()==HLException.ERR_TIMEOUT_SERIOUS)
			{
				failedr.append(ee.getMessage()) ;
				return false;
			}
			
			if(log.isDebugEnabled())
				log.debug("err code:"+ee.getErrCode(), ee);
		}
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
