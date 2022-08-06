package org.iottree.driver.s7.ppi;

import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.Convert;

public abstract class PPICmd
{
	final static int RECV_TIMEOUT_DEFAULT = 1000;

	final static int RECV_END_TIMEOUT_DEFAULT = 20;
	
	PPIDriver ppiDrv = null ;
	
	protected long  lastRunT  = -1;
	
	protected long scanIntervalMS = 100 ;
	
	protected int scanErrIntervalMulti =3;
	
	protected short devAddr = 2 ;
	
	protected PPIMemTp ppiMemTp = null ;
	
	protected long recvTimeout = RECV_TIMEOUT_DEFAULT;

	protected boolean bFixTO = true;//

	protected long recvEndTimeout = RECV_END_TIMEOUT_DEFAULT;
	
	protected long reqInterMS = 0 ;
	
	public PPICmd(short dev_addr,PPIMemTp ppi_mtp)
	{
		devAddr = dev_addr;
		ppiMemTp = ppi_mtp ;
		//this.readNum = readnum;
	}
	
	public PPIDriver getDriver()
	{
		return ppiDrv;
	}
	
	public long getScanIntervalMS()
	{
		return scanIntervalMS + 100 * scanErrIntervalMulti;
	}
	

	public PPICmd withScanIntervalMS(long sms)
	{
		scanIntervalMS = sms;
		return this ;
	}

	public long getRecvTimeout()
	{
		return recvTimeout;
	}

	public PPICmd withRecvTimeout(long rto)
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

	public PPICmd withRecvEndTimeout(long rto)
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

	
	void initCmd(PPIDriver drv)
	{
		ppiDrv = drv ;
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
	
//	boolean doCmd_Test(ConnPtStream ep)  throws Exception
//	{
//		InputStream inputs = ep.getInputStream() ;
//		OutputStream outputs = ep.getOutputStream() ;
//		
//		PPIMsgReqR req = new PPIMsgReqR() ;
//		req.withSorAddr(ppiDrv.getMasterID())
//			.withDestAddr(devAddr)
//			.withAddr(addr_str)
//			 ;
//		
//		PPIMsgReqConfirm reqc = new PPIMsgReqConfirm();
//		reqc.withSorAddr(ppiDrv.getMasterID())
//			.withDestAddr(devAddr) ;
//		
//		Thread.sleep(ppiDrv.getCmdInterval());
//		
//		inputs.skip(inputs.available()) ;
//		//write
//		byte[] bs1 = req.toBytes();
//		//System.out.println("req->"+Convert.byteArray2HexStr(bs1, " "));
//		outputs.write(bs1);
//		int c = PPIMsg.readCharTimeout(inputs, ppiDrv.getReadTimeout()) ;
//		if(c!=0xE5 && c!=0xF9)
//			return false;
//		Thread.sleep(10);//no sleep may do error
//		byte[] bs2 = reqc.toBytes();
//		//System.out.println("reqc->"+Convert.byteArray2HexStr(bs2, " "));
//		outputs.write(bs2);
//		//Thread.sleep(1);
//		StringBuilder failedr = new StringBuilder();
//		PPIMsgRespR resp = PPIMsgRespR.parseFromStream(inputs, ppiDrv.getReadTimeout(),failedr) ;
//		if(resp==null)
//			return false;
//		
//		System.out.println(resp);
//		return true;
//	}
}
