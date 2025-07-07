package org.iottree.driver.omron.fins.cmds;

import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.UADev;
import org.iottree.core.util.IBSOutput;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.omron.fins.FinsBlock;
import org.iottree.driver.omron.fins.FinsCmd;
import org.iottree.driver.omron.fins.FinsDevItem;
import org.iottree.driver.omron.fins.FinsDriver;
import org.iottree.driver.omron.fins.FinsDriverEthTCP;
import org.iottree.driver.omron.fins.FinsException;
import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.fins.FinsModel;
import org.iottree.driver.omron.fins.FinsMsg;


public class FinsMemRCmd extends FinsCmd
{
	public static ILogger log = LoggerManager.getLogger(FinsMemRCmd.class) ;
	
	//private int baseAddr ; 
	
	private int readNum  ;
	
	private int startAddr ;
	
	private boolean bReadBit = false;
	
	transient private FinsMemRReq req = null ;
	
	//transient byte[] retBs = null ;
	private transient FinsMemRResp resp = null ;
	
	public FinsMemRCmd(int startaddr,int readnum,boolean readbit)
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
	protected void initCmd(FinsDriver drv,FinsBlock block)
	{
		super.initCmd(drv,block);
		
		FinsDevItem devitem = block.getDevItem() ;
		
		FinsModel m = (FinsModel)devitem.getUADev().getDrvDevModel();
		FinsMode fm = m.getFinsMode() ;
		FinsMemRReq crr = new FinsMemRReq(fm) ;
		
		UADev dev = devitem.getUADev() ;
		int sa1 = dev.getOrDefaultPropValueInt(FinsDriver.PG_FINS_NET, "sa1", 0) ;
		int da1 = dev.getOrDefaultPropValueInt(FinsDriver.PG_FINS_NET, "da1", 0) ;
		crr.asFinsHeader(0, da1, 0, 0, sa1, 0) ;
		// all read by word
		
		crr.asReqR(block.getPrefix(),bReadBit, startAddr,0,readNum) ;
		
		if(devitem.isNetTcp())
		{
			FinsDriverEthTCP fdn = (FinsDriverEthTCP)drv ;
			//crr.asFinsHeaderTcp(icf, gct, dna, da1, da2, sna, sa1, sa2, sid)
		}
		else if(devitem.isNetUdp())
		{
			throw new RuntimeException("no impl") ;
		}
		else
		{
			//crr.asFinsHeaderNet(icf, gct, dna, da1, da2, sna, sa1, sa2, sid) TODO
			//
			crr.asFinsHeaderSerial() ;
		}
		
		req = crr ;
	}
	
	public boolean doCmd(InputStream inputs,OutputStream outputs,StringBuilder failedr)  throws Exception
	{
		Thread.sleep(this.drv.getCmdInterval());
		resp = null;
		
		// FinsMemRResp resp = new FinsMemRResp(this.req) ;
		
		
//		try
//		{
			FinsMsg.clearInputStream(inputs,50) ;
			
			req.writeOutTCP(outputs);
			//String str = req.writeTo(outputs);
			//if(log.isDebugEnabled())
			//	log.debug("-> ["+str+"]");
			
			//FinsMemRResp resp = null;
			
			//resp = (FinsMemRResp)req.readRespFrom(inputs, outputs,this.recvTimeout,this.failedRetryC) ;
			int max_len = 10240 ;
			//StringBuilder failedr = new StringBuilder() ;
			resp = new FinsMemRResp(req) ;
			if(!resp.readFromAsTcp(inputs, this.recvTimeout,max_len,failedr))
			{
				if(log.isDebugEnabled())
					log.debug("read resp failed <- ["+failedr+"]");
			}
		
			onResp(resp);

//		}
//		catch(FinsException ee)
//		{
//			if(ee.getErrCode()==FinsException.ERR_TIMEOUT_SERIOUS)
//			{
//				failedr.append(ee.getMessage()) ;
//				return false;
//			}
//			
//			if(log.isDebugEnabled())
//				log.debug("err code:"+ee.getErrCode(), ee);
//		}
		return true;
	}
	
	
	private void onResp(FinsMemRResp resp)
	{
		//resp.get
		this.resp = resp ;
	}
	
	public FinsMemRReq getReq()
	{
		return this.req ;
	}
	
	public FinsMemRResp getResp()
	{
		return this.resp;
	}

	
}
