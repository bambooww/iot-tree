package org.iottree.driver.common.modbus;

import java.io.*;
import java.util.*;

import kotlin.NotImplementedError;


/**
 * 
 * @author Jason Zhu
 */
public abstract class ModbusCmd
{

	public final static short MODBUS_FC_READ_COILS = 0x01;

	public final static short MODBUS_FC_READ_DISCRETE_INPUT = 0x02;

	// ref modbus spc en page 15
	public final static short MODBUS_FC_READ_HOLD_REG = 0x03;

	public final static short MODBUS_FC_READ_INPUT_REG = 0x04;

	public final static short MODBUS_FC_WRITE_SINGLE_COIL = 0x05;

	public final static short MODBUS_FC_WRITE_SINGLE_REG = 0x06;

	public final static short MODBUS_FC_WRITE_MULTI_COIL = 0x0F;

	public final static short MODBUS_FC_WRITE_MULTI_REG = 0x10;

	static int[] auchCRCHi = new int[] { 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00,
			0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80,
			0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00,
			0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80,
			0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01,
			0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80,
			0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00,
			0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81,
			0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00,
			0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81,
			0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00,
			0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80,
			0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00,
			0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80,
			0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40 };

	static int[] auchCRCLo = new int[] { 0x00, 0xC0, 0xC1, 0x01, 0xC3, 0x03, 0x02, 0xC2, 0xC6, 0x06, 0x07, 0xC7, 0x05,
			0xC5, 0xC4, 0x04, 0xCC, 0x0C, 0x0D, 0xCD, 0x0F, 0xCF, 0xCE, 0x0E, 0x0A, 0xCA, 0xCB, 0x0B, 0xC9, 0x09, 0x08,
			0xC8, 0xD8, 0x18, 0x19, 0xD9, 0x1B, 0xDB, 0xDA, 0x1A, 0x1E, 0xDE, 0xDF, 0x1F, 0xDD, 0x1D, 0x1C, 0xDC, 0x14,
			0xD4, 0xD5, 0x15, 0xD7, 0x17, 0x16, 0xD6, 0xD2, 0x12, 0x13, 0xD3, 0x11, 0xD1, 0xD0, 0x10, 0xF0, 0x30, 0x31,
			0xF1, 0x33, 0xF3, 0xF2, 0x32, 0x36, 0xF6, 0xF7, 0x37, 0xF5, 0x35, 0x34, 0xF4, 0x3C, 0xFC, 0xFD, 0x3D, 0xFF,
			0x3F, 0x3E, 0xFE, 0xFA, 0x3A, 0x3B, 0xFB, 0x39, 0xF9, 0xF8, 0x38, 0x28, 0xE8, 0xE9, 0x29, 0xEB, 0x2B, 0x2A,
			0xEA, 0xEE, 0x2E, 0x2F, 0xEF, 0x2D, 0xED, 0xEC, 0x2C, 0xE4, 0x24, 0x25, 0xE5, 0x27, 0xE7, 0xE6, 0x26, 0x22,
			0xE2, 0xE3, 0x23, 0xE1, 0x21, 0x20, 0xE0, 0xA0, 0x60, 0x61, 0xA1, 0x63, 0xA3, 0xA2, 0x62, 0x66, 0xA6, 0xA7,
			0x67, 0xA5, 0x65, 0x64, 0xA4, 0x6C, 0xAC, 0xAD, 0x6D, 0xAF, 0x6F, 0x6E, 0xAE, 0xAA, 0x6A, 0x6B, 0xAB, 0x69,
			0xA9, 0xA8, 0x68, 0x78, 0xB8, 0xB9, 0x79, 0xBB, 0x7B, 0x7A, 0xBA, 0xBE, 0x7E, 0x7F, 0xBF, 0x7D, 0xBD, 0xBC,
			0x7C, 0xB4, 0x74, 0x75, 0xB5, 0x77, 0xB7, 0xB6, 0x76, 0x72, 0xB2, 0xB3, 0x73, 0xB1, 0x71, 0x70, 0xB0, 0x50,
			0x90, 0x91, 0x51, 0x93, 0x53, 0x52, 0x92, 0x96, 0x56, 0x57, 0x97, 0x55, 0x95, 0x94, 0x54, 0x9C, 0x5C, 0x5D,
			0x9D, 0x5F, 0x9F, 0x9E, 0x5E, 0x5A, 0x9A, 0x9B, 0x5B, 0x99, 0x59, 0x58, 0x98, 0x88, 0x48, 0x49, 0x89, 0x4B,
			0x8B, 0x8A, 0x4A, 0x4E, 0x8E, 0x8F, 0x4F, 0x8D, 0x4D, 0x4C, 0x8C, 0x44, 0x84, 0x85, 0x45, 0x87, 0x47, 0x46,
			0x86, 0x82, 0x42, 0x43, 0x83, 0x41, 0x81, 0x80, 0x40 };

	// crc16
	public static int modbus_crc16_check(byte[] pmsg, int msglen)
	{
		int hi = 0xFF; 
		int lo = 0xFF; 
		int idx; 

		for (int i = 0; i < msglen; i++)
		{
			idx = hi ^ (((int) pmsg[i]) & 0xFF); 
			hi = lo ^ auchCRCHi[idx];
			lo = auchCRCLo[idx];
		}
		return (hi << 8) | lo;
	}

	public static int modbus_crc16_check_seg(byte[] phead, int hlen, byte[] pmsg, int msglen, byte[] ptail, int tlen)
	{
		int hi = 0xFF; 
		int lo = 0xFF; 
		int idx; 

		if (phead != null)
		{
			for (int i = 0; i < hlen; i++)
			{
				idx = hi ^ (((int) phead[i]) & 0xFF);
				hi = lo ^ auchCRCHi[idx];
				lo = auchCRCLo[idx];
			}
		}

		if(pmsg!=null)
		{
			for (int i = 0; i < msglen; i++)
			{
				idx = hi ^ (((int) pmsg[i]) & 0xFF);
				hi = lo ^ auchCRCHi[idx];
				lo = auchCRCLo[idx];
			}
		}

		if (ptail != null)
		{
			for (int i = 0; i < tlen; i++)
			{
				idx = hi ^ (((int) ptail[i]) & 0xFF);
				hi = lo ^ auchCRCHi[idx];
				lo = auchCRCLo[idx];
			}
		}

		return (hi << 8) | lo;
	}

	public static enum Protocol
	{
		rtu(0), //
		tcp(1), //
		ascii(2);//

		private final int val;

		Protocol(int v)
		{
			val = v;
		}

		public int getIntValue()
		{
			return val;
		}

	}

	/**
	 * @param inputs
	 * @return
	 * @throws IOException
	 */
	public static ModbusCmd parseRequest(byte[] req, int[] pl)// throws
																// IOException
	{
		if (req == null || req.length <= 1)
			return null;

		//
		//short addr = (short) (req[0] & 0xFF);
		short fc = (short) (req[1] & 0xFF);

		switch (fc)
		{
		case MODBUS_FC_READ_COILS:
		case MODBUS_FC_READ_DISCRETE_INPUT:
			return ModbusCmdReadBits.createReqMC(req, pl);
		case MODBUS_FC_READ_HOLD_REG:
		case MODBUS_FC_READ_INPUT_REG:
			return ModbusCmdReadWords.createReqMC(req, pl);
		case MODBUS_FC_WRITE_SINGLE_COIL:
			return ModbusCmdWriteBit.createReqMC(req, pl);
		case MODBUS_FC_WRITE_SINGLE_REG:
			return ModbusCmdWriteWord.createReqMC(req, pl);
		case MODBUS_FC_WRITE_MULTI_COIL:
		case MODBUS_FC_WRITE_MULTI_REG:
		default:

		}

		return null;
	}

	//
	public final static int ERR_RECV_TIMEOUT = -1;

	public final static int ERR_RECV_END_TIMEOUT = -2;

	public final static int ERR_CRC = -3;
	
	public final static int ERR_RET = -4;

	
	final static int MAX_SCAN_MULTI = 50;

	// final static int RECV_TIMEOUT_MAX = 2000;//60 ;
	final static int RECV_TIMEOUT_MIN = 20;

	final static int RECV_TIMEOUT_DEFAULT = 1000;

	final static int RECV_END_TIMEOUT_DEFAULT = 20;
	
	public final static long SCAN_INTERVER_DEFAULT = 100 ;

	protected long scanIntervalMS = SCAN_INTERVER_DEFAULT;

	
	protected long maxRecvTOMS = 60000;

	private int scanErrIntervalMulti = 0;

	protected long recvTimeout = RECV_TIMEOUT_DEFAULT;

	protected boolean bFixTO = true;//

	protected long recvEndTimeout = RECV_END_TIMEOUT_DEFAULT;
	
	protected long reqInterMS = 0 ;

	private boolean bFixEndTO = true;

	protected short slaveAddr = 0;

	protected int tryTimes = 0;

	protected Protocol protocal = Protocol.rtu;
	
	protected byte[] mbap4Tcp = null ; 

	transient protected byte[] mbuss_adu = new byte[300];

	transient protected int mbuss_rnum = 0;

	private transient long lastRunT = 0;

	ModbusRunner belongToRunner = null;

	transient Object relatedObj = null;

	transient long runOnceT = -1;

	transient int errCount = 0;

	protected transient int lastTcpCC = 0;
	
	protected ModbusCmd()
	{}

	public ModbusCmd(long scan_inter_ms, int dev_addr)
	{
		this.slaveAddr = (short) (dev_addr & 0xFF);
		if (scan_inter_ms > 0)
			scanIntervalMS = scan_inter_ms;

		// if(max_recv_to_ms>0)
		// maxRecvTOMS = max_recv_to_ms;
		// if(recv_to>0)
		// recvTimeout = recv_to ;

		// if(recv_end_to>0)
		// recvEndTimeout = recv_end_to ;
	}

	// public abstract short getFC() ;

	/**
	 * @return
	 */
	public ModbusRunner getComRunner()
	{
		return belongToRunner;
	}

	public Protocol getProtocol()
	{
		return protocal;
	}

	public void setProtocol(Protocol p)
	{
		if (p == Protocol.ascii)
			throw new IllegalArgumentException("not support ascii protocol");

		protocal = p;
	}
	
	public abstract short getFC();

	public static int addCRC(byte[] data, int dlen)
	{
		int cr = modbus_crc16_check(data, dlen);// modbus_crc16_check_nor(data,
												// 0, dlen);
		data[dlen] = (byte) ((cr >> 8) & 0xFF);
		data[dlen + 1] = (byte) (cr & 0xFF);
		return dlen + 2;
	}

	/**
	 * @return
	 */
	public Object getRelatedObj()
	{
		return relatedObj;
	}

	public void setRelatedObj(Object o)
	{
		relatedObj = o;
	}

	public short getDevAddr()
	{
		return slaveAddr;
	}

	public long getScanIntervalMS()
	{
		return scanIntervalMS + 100 * scanErrIntervalMulti;
	}

	public void setScanIntervalMS(long sms)
	{
		scanIntervalMS = sms;
	}

	public long getRecvTimeout()
	{
		return recvTimeout;
	}

	public void setRecvTimeout(long rto)
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
	}

	public long getRecvEndTimeout()
	{
		return recvEndTimeout;
	}

	public void setRecvEndTimeout(long rto)
	{
		if (rto <= 0)
		{
			recvEndTimeout = RECV_END_TIMEOUT_DEFAULT;
			bFixEndTO = false;
		}
		else
		{
			recvEndTimeout = rto;
			bFixEndTO = true;
		}
	}

	public int getTryTimes()
	{
		return tryTimes;
	}

	public void setTryTimes(int tt)
	{
		tryTimes = tt;
	}

	public void setMaxRecvTO(long ms)
	{
		if (ms > 0)
			this.maxRecvTOMS = ms;
	}

	public boolean isReadCmd()
	{
		return false;
	}

	public Object[] getReadVals()
	{
		return null;
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

	public long getRunOnceTime()
	{
		return runOnceT;
	}

	public void setRunOnceTime(long t)
	{
		runOnceT = t;
	}

	public int getErrCount()
	{
		return errCount;
	}

	private void increaseErrCount()
	{
		if (errCount >= Integer.MAX_VALUE)
			return;

		errCount++;
	}

	public void doCmd(OutputStream outs, InputStream ins) throws Exception
	{
		try
		{
			doCmdInner(outs, ins);

			Object[] ovs = getReadVals();

			if (ovs != null)
				errCount = 0;
			else
				increaseErrCount();
		}
		catch (Exception e)
		{
			//System.out.println("do cmd err-------------------") ;
			//e.printStackTrace();
			increaseErrCount();
			throw e;
		}
	}

	private void doCmdInner(OutputStream outs, InputStream ins) throws Exception
	{
		int r = 0;
		//System.out.println("doCmdInner --1") ;
		switch(this.protocal)
		{
		case rtu:
			r = reqRespRTU(outs, ins);
			break;
		case tcp:
			r = reqRespTCP(outs, ins);
			break;
		case ascii:
			r = reqRespASCII(outs, ins);
			break;
		default:
			break;
		}
		
			
		//System.out.println("doCmdInner --2 r="+r) ;
//		if (r < 0)
//		{
//			for (int k = 0; k < tryTimes; k++)
//			{
//				Thread.sleep(reqInterMS);
//				if (this.protocal == Protocol.tcp)
//					r = reqRespTCP(outs, ins);
//				else
//					r = reqRespRTU(outs, ins);
//				if (r > 0)
//					break;
//			}
//		}
		//System.out.println("doCmdInner --2.5") ;
		if (!this.isReadCmd())
			return;
		//System.out.println("doCmdInner --3") ;
		// read cmd 
		if (r < 0)
		{
			switch (r)
			{
			case ERR_RECV_TIMEOUT:// recvTimeout may be adjust
				if (!bFixTO)
				{
					if (recvTimeout > 100)
					{
						recvTimeout += 100;
						// System.out.println("recv time out="+recvTimeout) ;
					}
					else if (recvTimeout > 10)
						recvTimeout += 10;
					else
						recvTimeout += 1;
					if (recvTimeout >= maxRecvTOMS)// RECV_TIMEOUT_MAX)
					{
						recvTimeout = maxRecvTOMS;// RECV_TIMEOUT_MAX;
						scanErrIntervalMulti++;
						if (scanErrIntervalMulti > MAX_SCAN_MULTI)
							scanErrIntervalMulti = MAX_SCAN_MULTI;
					}
				}
				break;
			case ERR_RECV_END_TIMEOUT:// recvEndTimeout may be adjust
				if (!bFixEndTO)
				{
					recvEndTimeout += 1;
				}
				break;
			case ERR_CRC:
			}
		}

		if (r > 0)
		{
			if (scanErrIntervalMulti > 0)
			{
				scanErrIntervalMulti = 0;
				if (!bFixTO)
					recvTimeout = RECV_TIMEOUT_DEFAULT;
			}
		}
		
		//System.out.println("doCmdInner --9") ;
	}

	public abstract int calRespLenRTU();
	
	/**
	 * 
	 * 
	 * @param ous
	 * @param ins
	 * @return
	 * @throws Exception
	 */
	protected abstract int reqRespRTU(OutputStream ous, InputStream ins) throws Exception;

	
	protected abstract int reqRespTCP(OutputStream ous, InputStream ins) throws Exception;
	
	protected int reqRespASCII(OutputStream ous, InputStream ins) throws Exception
	{
		return 0;
	}

	protected void clearInputStream(InputStream inputs) throws IOException
	{
		final byte[] tmpbs = new byte[100] ;
		int avn = 0;
		while ((avn=inputs.available()) > 0)
		{
			//inputs.skip(avn);
			inputs.read(tmpbs) ;
		}

		// System.out.println("clearSerialPortRecv len="+avn) ;
		mbuss_rnum = 0;
	}

	protected int chkCurRecvedLen(InputStream inputs) throws IOException
	{
		int avn = inputs.available();
		if (avn > 0)
		{
			int n = inputs.read(mbuss_adu, mbuss_rnum, avn);
			mbuss_rnum += n;
		}
		return mbuss_rnum;
	}

	int urx_end = 1, urx_count_last = 0, b_in_rx = 0;

	long tt_c = 0;

	protected void com_stream_recv_start(InputStream inputs) throws IOException
	{
		// pComDrv->drv_rx_clear() ;
		urx_end = 0;// ��ʼ����
		urx_count_last = 0;
		b_in_rx = 1;
		tt_c = System.currentTimeMillis();

		// tt_c = 0;
	}

	protected boolean com_stream_in_recving()
	{
		return b_in_rx > 0;
	}

	protected void com_stream_end()
	{
		b_in_rx = 0;
		urx_end = 1;
	}

	protected int com_stream_recv_chk_len_timeout(InputStream inputs) throws IOException
	{
		int rc = 0;
		if (urx_end != 0)
			return 0;

		rc = chkCurRecvedLen(inputs);// pComDrv->drv_rx_count() ;
		if (rc == 0)
		{
			if (System.currentTimeMillis() - tt_c > recvTimeout)
			{// ���ڶ�û�յ�����
				com_stream_end();
			}

			try
			{
				Thread.sleep(1);
			}
			catch (Exception ee)
			{
			}

			return 0;
		}

		// if(rc==urx_count_last)

		if (rc > urx_count_last)
		{//
			urx_count_last = rc;
			tt_c = System.currentTimeMillis();
			return rc;
		}
		else
		{
			try
			{
				Thread.sleep(1);
			}
			catch (Exception ee)
			{
			}
		}

		
		if (System.currentTimeMillis() - tt_c > recvEndTimeout)
		{// time out
			// #asm("cli")
			com_stream_end();
			// urx_end = 1 ;
			// #asm("sei")

			return rc;
		}

		return rc;
	}

	
	public String toString()
	{
		return "{dev:" + this.slaveAddr + ",recv_to:" + this.recvTimeout + ",recv_end_to:" + this.recvEndTimeout + "}";
	}


	public static byte[] createRespError(ModbusCmd mc,short addr,short req_fc)
	{
		switch(mc.getProtocol())
		{
		case tcp:
			return createRespErrorTCP(mc.mbap4Tcp,addr,req_fc) ;
		case ascii:
			throw new NotImplementedError() ;
		default:
			return createRespErrorRTU(addr,req_fc) ;
		}
	}
	
	protected static byte[] createRespErrorTCP(byte[] mbap,short addr,short req_fc)
	{
		//[ Transaction ID | Protocol ID | Length | Unit ID | Error Code | Exception Code ]
		byte[] bs = new byte[9] ;
		bs[0] = mbap[0];
		bs[1] = mbap[1];
		bs[2] = mbap[2];
		bs[3] = mbap[3];
		bs[4] = 0 ;
		bs[5] = 3 ;
		bs[6] = (byte)addr ;
		bs[7] = (byte)(req_fc + 0x80) ;
		bs[8] = 0x04 ;// device error
		return bs ;
	}
	
	protected static byte[] createRespErrorRTU(short addr,short req_fc)
	{
		//[ Transaction ID | Protocol ID | Length | Unit ID | Error Code | Exception Code ]
		byte[] bs = new byte[5] ;
		bs[0] = (byte)addr ;
		bs[1] = (byte)(req_fc + 0x80) ;
		bs[2] = 0x04 ;// device error
		int crc = modbus_crc16_check(bs,3);
	    bs[3] = (byte)((crc>>8) & 0xFF) ;
	    bs[4] = (byte)(crc & 0xFF) ;
		return bs ;
	}
	
}
