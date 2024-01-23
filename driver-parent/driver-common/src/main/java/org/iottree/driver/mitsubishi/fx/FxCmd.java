package org.iottree.driver.mitsubishi.fx;

import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.driver.s7.ppi.PPICmd;
import org.iottree.driver.s7.ppi.PPIDriver;
import org.iottree.driver.s7.ppi.PPIMemTp;

public abstract class FxCmd
{
	final static int RECV_TIMEOUT_DEFAULT = 1000;

	final static int RECV_END_TIMEOUT_DEFAULT = 20;
	
	FxDriver drv = null ;
	
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
	
	public FxCmd() //(short dev_addr,FxMemTp fx_mtp)
	{
//		devAddr = dev_addr;
//		fxMemTp = fx_mtp ;
	}
	
	public FxDriver getDriver()
	{
		return drv;
	}
	
	public long getScanIntervalMS()
	{
		return scanIntervalMS + 100 * scanErrIntervalMulti;
	}
	

	public FxCmd withScanIntervalMS(long sms)
	{
		scanIntervalMS = sms;
		return this ;
	}

	public long getRecvTimeout()
	{
		return recvTimeout;
	}

	public FxCmd withRecvTimeout(long rto)
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

	public FxCmd withRecvEndTimeout(long rto)
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

	
	void initCmd(FxDriver drv)
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
		
		outputs.write(FxMsg.ENQ);
		int c = FxMsg.readCharTimeout(inputs, timeout);
		return c==FxMsg.ACK ;
	}
}
