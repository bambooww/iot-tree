package org.iottree.driver.mitsubishi.fxnet;

import java.io.InputStream;
import java.io.OutputStream;


public abstract class FxNetCmd
{
	final static int RECV_TIMEOUT_DEFAULT = 1000;

	final static int RECV_END_TIMEOUT_DEFAULT = 20;
	
	FxNetDriver drv = null ;
	
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
	
	
	public FxNetCmd() //(short dev_addr,FxMemTp fx_mtp)
	{
//		devAddr = dev_addr;
//		fxMemTp = fx_mtp ;
	}
	
	public FxNetDriver getDriver()
	{
		return drv;
	}
	
	public long getScanIntervalMS()
	{
		return scanIntervalMS + 100 * scanErrIntervalMulti;
	}
	

	public FxNetCmd withScanIntervalMS(long sms)
	{
		scanIntervalMS = sms;
		return this ;
	}

	public long getRecvTimeout()
	{
		return recvTimeout;
	}

	public FxNetCmd withRecvTimeout(long rto)
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

	public FxNetCmd withRecvEndTimeout(long rto)
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

	
	void initCmd(FxNetDriver drv)
	{
		this.drv = drv ;
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
	
	
	public static boolean checkDevReady(InputStream inputs,OutputStream outputs,long timeout) throws Exception
	{
		int n = inputs.available() ;
		if(n>0)
			inputs.skip(n);
		
		outputs.write(FxNetMsg.ENQ);
		int c = FxNetMsg.readCharTimeout(inputs, timeout);
		return c==FxNetMsg.ACK ;
	}
}
