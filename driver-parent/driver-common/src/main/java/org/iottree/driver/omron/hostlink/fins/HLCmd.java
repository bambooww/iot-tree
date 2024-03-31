package org.iottree.driver.omron.hostlink.fins;

import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.driver.omron.hostlink.HLDriver;


public abstract class HLCmd
{
	final static int RECV_TIMEOUT_DEFAULT = 1000;

	final static int RECV_END_TIMEOUT_DEFAULT = 20;
	
	protected HLFinsDriver drv = null ;
	
	protected HLBlock block = null ; 
	
	protected long  lastRunT  = -1;
	
	protected long scanIntervalMS = 100 ;
	
	protected int scanErrIntervalMulti =3;
	
//	protected short devAddr = 2 ;
//	
//	protected FxMemTp fxMemTp = null ;
	
	protected long recvTimeout = RECV_TIMEOUT_DEFAULT;

	protected boolean bFixTO = true;//

	protected long recvEndTimeout = RECV_END_TIMEOUT_DEFAULT;
	
	protected long reqInterMS = 0 ;
	
	
	public HLCmd() //(short dev_addr,FxMemTp fx_mtp)
	{
//		devAddr = dev_addr;
//		fxMemTp = fx_mtp ;
	}
	
	public HLFinsDriver getDriver()
	{
		return drv;
	}
	
	public long getScanIntervalMS()
	{
		return scanIntervalMS + 100 * scanErrIntervalMulti;
	}
	

	public HLCmd withScanIntervalMS(long sms)
	{
		scanIntervalMS = sms;
		return this ;
	}

	public long getRecvTimeout()
	{
		return recvTimeout;
	}

	public HLCmd withRecvTimeout(long rto)
	{
		if (rto <= 0)
		{
			recvTimeout = RECV_TIMEOUT_DEFAULT;
			bFixTO = false;
		}
		else
		{
			recvTimeout = rto;
			bFixTO = true;
		}
		return this ;
	}

	public long getRecvEndTimeout()
	{
		return recvEndTimeout;
	}

	public HLCmd withRecvEndTimeout(long rto)
	{
		if (rto <= 0)
		{
			recvEndTimeout = RECV_END_TIMEOUT_DEFAULT;
		}
		else
		{
			recvEndTimeout = rto;
		}
		return this;
	}

	
	protected void initCmd(HLFinsDriver drv,HLBlock block)
	{
		//HLDevItem devitem
		this.drv = drv ;
		this.block = block ;
	}
	
	public boolean tickCanRun()
	{
		long ct = System.currentTimeMillis();
		if (ct - lastRunT > getScanIntervalMS())
		{
			lastRunT = ct;
			// System.out.println("11");
			return true;
		}
		return false;
	}
	
	public abstract boolean doCmd(InputStream inputs,OutputStream outputs)  throws Exception;
	

}
