package org.iottree.driver.zigbee.ember;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PushbackInputStream;

import org.iottree.core.util.Convert;

public abstract class ASHFrame
{
	public static final int TP_DATA = 0x0;
	public static final int TP_ACK = 0b1000_0000;
	public static final int TP_NAK = 0b1010_0000;
	public static final int TP_RST = 0b1100_0000;
	public static final int TP_RSTACK = 0b1100_0001;
	public static final int TP_ERROR = 0b1100_0010;

	public static final int END_FLAG = 0x7E;
	
	public static final int CANCEL_FLAG = 0x1A;

	/**
	 * The NCP enters the FAILED state if it detects one of the following
	 * errors:
	 * 
	 * An abnormal internal reset due to an error, failed assertion, or fault.
	 * 
	 * Exceeding the maximum number of consecutive acknowledgement timeouts.
	 * 
	 * When the NCP enters the FAILED state, the NCP sends an ERROR frame
	 * containing a reset or error code and will reply to all subsequent frames
	 * received, except RST, with an ERROR frame. To reinitialize the ASH
	 * protocol, the Host must reset the NCP by either asserting the nRESET pin
	 * or sending the RST frame. The following table lists the codes returned by
	 * the NCP in the Reset Code byte of a RSTACK frame or in the Error Code
	 * byte of an ERROR frame. Silicon Labs wireless mesh chips can detect
	 * numerous reset fault causes beyond those in the table. When sent to the
	 * host, these new reset codes have 0x80 added to the value returned by
	 * their HAL’s reset code.
	 */
	public static final int RESET_UNKNOWN = 0x00;
	public static final int RESET_EXTERNAL = 0x01;
	public static final int RESET_POWER_ON = 0x02;
	public static final int RESET_WATCHDOG = 0x03;
	public static final int RESET_ASSERT = 0x06;
	public static final int RESET_BOOTLOADER = 0x09;
	public static final int RESET_SOFTWARE = 0x0B;
	public static final int ERROR_EXCEED_ACK_TIMEOUT_C = 0x51;
	public static final int ERROR_CHIP_SPEC_RESET = 0x80;

	public static int calFrameTP(int ctrl_b)
	{
		if ((ctrl_b & 0b1000_0000) == 0)
			return TP_DATA;
		switch (ctrl_b & 0b1110_0000)
		{
		case TP_ACK:
			return TP_ACK;
		case TP_NAK:
			return TP_NAK;
		}
		switch (ctrl_b)
		{
		case TP_RST:
			return TP_RST;
		case TP_RSTACK:
			return TP_RSTACK;
		case TP_ERROR:
			return TP_ERROR;
		}
		return -1;
	}

	private static ASHFrame createFrame(EmberManager emgr,int ctrl_b)
	{
		int tp = calFrameTP(ctrl_b);
		switch (tp)
		{
		case TP_DATA:
			return new ASHFrmData(emgr,ctrl_b);//.asLegacy(false);
		case TP_ACK:
			return new ASHFrmAck(ctrl_b);
		case TP_NAK:
			return new ASHFrmNak(ctrl_b);
		case TP_RST:
			return new ASHFrmRst();
		case TP_RSTACK:
			return new ASHFrmRstAck();
		case TP_ERROR:
			return new ASHFrmError();
		}
		return null;
	}

	protected int ctrl; // control byte

	public ASHFrame(int ctrl)
	{
		this.ctrl = ctrl;
	}

	public abstract int getDataFieldLen();

	protected abstract boolean packDataField(byte[] data, int offset, int dflen);

	protected abstract boolean unpackDataField(byte[] data, int offset, int dflen,StringBuilder failedr);

	public byte[] pack()
	{
		return pack(false) ;
	}
	
	public byte[] pack(boolean b_stuffing)
	{
		int dflen = this.getDataFieldLen();
		if (dflen < 0)
			throw new RuntimeException("no datafield len");
		byte[] ret = new byte[4 + dflen];
		ret[0] = (byte) this.ctrl;
		if (dflen > 0)
		{
			packDataField(ret, 1, dflen);
			randomize(ret, 1, dflen); // randomize
		}
		int crc = computeCrc16(ret, 0, dflen + 1);
		putCrc16ToBytes(crc, ret, 1 + dflen);
		ret[dflen + 3] = END_FLAG;
		if(b_stuffing)
			return transByteStuffing(ret,ret.length) ;
		return ret;
	}
	
	public abstract String toShortString() ;

//	public static void test() {
//	    // 原始帧去掉 7E，去掉首字节 0x33(Control)，去掉末尾 2 字节 CRC(97 EB)
//	    byte[] dataField = new byte[]{
//	        (byte)0x21, (byte)0xA9, (byte)0x07, (byte)0x2A,
//	        (byte)0x0C, (byte)0xB0, (byte)0x59
//	    };
//
//	    randomize(dataField,0,dataField.length);
//	    System.out.print("test===");
//	    for (byte b : dataField) {
//	        System.out.printf("%02X ", b & 0xFF);
//	    }
//	    
//	    System.out.println("");
//	    // 输出: 03 00 01 53 03 09 00
//	}
	/**
	 * 解析一个完整的数据包
	 * 
	 * @param bs
	 * @return
	 */
	private boolean unpack(byte[] bs, int len,StringBuilder failedr)
	{
		if (bs[len - 1] != 0x7E)
			return false; // end flag err
		
		int rcrc = readCrc16(bs, len - 3);
		int crc = computeCrc16(bs, 0, len - 3);
		if (rcrc != crc)
		{
			if (failedr != null)
				failedr.append("crc err");
			return false; // crc err
		}
		
		int dflen = len - 4;
		if (dflen > 0 && this instanceof ASHFrmData)
		{
			randomize(bs, 1, dflen); // derandomize
			//System.out.println("derandomize="+Convert.byteArray2HexStr(bs, "-")) ;
			//test();
		}
		
		return unpackDataField(bs, 1, dflen,failedr);
	}

	public static final ASHFrame parseFrom(EmberManager emgr,byte[] bs, int len,boolean b_unstuffing, StringBuilder failedr)
	{
		//System.out.println("src="+Convert.byteArray2HexStr(bs, "-")) ;
		if(b_unstuffing)
		{
			bs = transByteUnStuffing(bs, len) ;
			len = bs.length ;
			//System.out.println("unstuff="+Convert.byteArray2HexStr(bs, "-")) ;
		}
		
		int ctrl_b = bs[0] & 0xFF;
		ASHFrame r = createFrame(emgr,ctrl_b);
		if (r == null)
		{
			if (failedr != null)
				failedr.append("new frame err");
			return null;
		}
		if (r.unpack(bs, len, failedr))
			return r;
		return null;
	}

	public static final ASHFrame parseFrom(EmberManager emgr,byte[] bs, StringBuilder failedr)
	{
		return parseFrom(emgr,bs, bs.length,false, failedr);
	}

	public static final ASHFrame parseFrom(EmberManager emgr,byte[] bs)
	{
		return parseFrom(emgr,bs, bs.length,false, null);
	}

	private static final byte[] transByteStuffing(byte[] bs, int len)
	{
		// reserved bytes and byte stuffing
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int flen = bs.length - 1;
		for (int i = 0; i < flen; i++)
		{
			int c = bs[i] & 0xFF;
			switch (c)
			{
			case 0x7E:
			case 0x7D:
			case 0x11:
			case 0x13:
			case 0x18:
			case 0x1A:
				baos.write(0x7D); // 转义
				baos.write(c ^ 0x20);
				break;
			default:
				baos.write(c);
			}
		}
		baos.write(ASHFrame.END_FLAG);
		return baos.toByteArray();
	}
	
	private static final byte[] unstuffing(byte[] bs,int len) throws Exception
	{
		int respST = 0 ;
		int respLen = 0 ;
		int c = 0 ;
		ByteArrayInputStream bais = new ByteArrayInputStream(bs,0,len) ;
		PushbackInputStream inputs = new PushbackInputStream(bais) ;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
		while(inputs.available()>0)
		{
			c=inputs.read() ;
			
			switch(respST)
			{
			case 0:
				if(c==ASHFrame.END_FLAG)
					continue ;//ignore continues end flag
				if(c==0xFF)
				{//当前不在帧内，按唤醒/待处理信号处理
					continue;
				}
				inputs.unread(c);
//				this.respBS[0] = (byte)c ;
				respLen = 0 ;
				respST = 1 ;
				break;
			case 1: //帧内解析
				switch(c)
				{
				case ASHFrame.END_FLAG://end
					baos.write(ASHFrame.END_FLAG);
					return baos.toByteArray() ;
				case 0x7D:
					respST = 2 ;
					break;
				case 0x1A: //丢弃当前帧缓存
					respLen = 0 ;
					baos.reset();
					break;
				case 0x18: //丢弃当前帧缓冲,进入“丢弃到下一个 0x7E”状态
					respST = 0 ;
					baos.reset();
					break ;
				case 0x11: //仅做流控，不写入帧缓冲
				case 0x13:
					break ;
				default:
					baos.write(c);
					break ;
				}
				break ;
			case 2: // 转义
				baos.write(c^0x20);
				respLen ++ ;
				respST = 1 ;
				break ;
			default:
				respST = 0 ;
				break;
			}
		}
		return null ;
	}
	
	private static final byte[] transByteUnStuffing(byte[] bs, int len)
	{
		// reserved bytes and byte stuffing
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		int flen = bs.length - 1;
//		for (int i = 0; i < flen; i++)
//		{
//			int c = bs[i] & 0xFF;
//			if(c==0x7D)
//			{
//				i ++ ;
//				c = bs[i] & 0xFF;
//				baos.write(c ^ 0x20);
//			}
//			else
//			{
//				baos.write(c);
//			}
//		}
//		baos.write(ASHFrame.END_FLAG);
//		return baos.toByteArray();
		

		try
		{
			return unstuffing(bs, len) ;
		}
		catch ( Exception e)
		{
			e.printStackTrace();
			return null ;
		}
	}

	private static final int LFSR_SEED = 0x42 ;
	private static final int LFSR_POLY = 0xb8;
	/**
     * Randomizes array contents by XORing with an 8-bit pseudo random sequence.
     * This reduces the likelihood that byte-stuffing will greatly increase the size of the payload.
     * (This could happen if a DATA frame contained repeated instances of the same reserved byte value.)
     *
     * Returns buffer as-is if randomize is OFF.
     * @param buffer IN/OUT
     * @param len
     */
	public static void randomize(byte[] buffer,int offset, int len) {
	    int outIdx = 0;
	    int seed = LFSR_SEED;

	    while (len-- > 0) {
	        buffer[outIdx+offset] ^= (byte) seed;
	        outIdx++;

	        // 和 TS 版完全等价：seed & 1 为真时异或多项式，否则直接右移
	        if ((seed & 1) != 0) {
	            seed = (seed >>> 1) ^ LFSR_POLY;
	        } else {
	            seed = seed >>> 1;
	        }
	    }
	}
	
//	public static void randomize(byte[] data,int offset,int len)
//	{
//	    int state = 0x42; // 初始种子
//	    //byte[] result = new byte[data.length];
//
//	    for (int i = 0; i < len; i++) {
//	        int randomByte = state & 0xFF;
//	        data[offset+i] = (byte) (data[offset+i] ^ randomByte);
//
//	        // LFSR 状态更新：反馈 = bit0 ^ bit2 ^ bit3 ^ bit4 ^ bit0
//	        int lsb = state & 1;
//	        int feedback = ((state >> 0) ^ (state >> 2) ^ (state >> 3) ^ (state >> 4) ^ lsb) & 1;
//	        state = ((state >> 1) | (feedback << 7)) & 0xFF;
//	    }
//	    //return result;
//	}
//	
//	public static byte[] randomize1(byte[] data) {
//		    int state = 0x42; // 初始种子
//		    byte[] result = new byte[data.length];
//
//		    for (int i = 0; i < data.length; i++) {
//		        int randomByte = state & 0xFF;
//		        result[i] = (byte) (data[i] ^ randomByte);
//
//		        // Galois LFSR，多项式 0xB8
//		        // 等价于 x^8 + x^7 + x^5 + x^4 + 1
//		        int lsb = state & 1;
//		        state = (state >>> 1) ^ (lsb != 0 ? 0xB8 : 0);
//		    }
//		    return result;
//	}


	private static final int POLYNOMIAL = 0x1021; // x^16 + x^12 + x^5 + 1
	private static final int INITIAL_VALUE = 0xFFFF;

	public static int computeCrc16(byte[] data, int offset, int length)
	{
		if (data == null || length <= 0)
			return INITIAL_VALUE;

		int crc = INITIAL_VALUE;
		for (int i = offset; i < offset + length; i++)
		{
			crc ^= (data[i] & 0xFF) << 8;
			for (int bit = 0; bit < 8; bit++)
			{
				if ((crc & 0x8000) != 0)
					crc = (crc << 1) ^ POLYNOMIAL;
				else
					crc = crc << 1;
			}
			crc &= 0xFFFF;
		}

		return crc;
	}

	/**
	 * 按 ASH 大端模式输出 CRC 字节：高字节在前，低字节在后。
	 */
	public static void putCrc16ToBytes(int crc, byte[] out_data, int offset)
	{
		out_data[offset] = (byte) ((crc >> 8) & 0xFF);
		out_data[offset + 1] = (byte) (crc & 0xFF);
	}

	/**
	 * 读取两个字节的crc值
	 * 
	 * @param data
	 * @param offset
	 * @return
	 */
	public static int readCrc16(byte[] data, int offset)
	{
		int r = (data[offset] & 0xFF) << 8;
		r += data[offset + 1] & 0xFF;
		return r;
	}
	
//	private static int halCommonCrc16(int newByte,int prevResult)
//	{
//	    /*
//	     *    16bit CRC notes:
//	     *    "CRC-CCITT"
//	     *     poly is g(X) = X^16 + X^12 + X^5 + 1  (0x1021)
//	     *     used in the FPGA (green boards and 15.4)
//	     *     initial remainder should be 0xFFFF
//	     */
//	    prevResult = ((prevResult >> 8) & 0xffff) | ((prevResult << 8) & 0xffff);
//	    prevResult ^= newByte;
//	    prevResult ^= (prevResult & 0xff) >> 4;
//	    prevResult ^= (((prevResult << 8) & 0xffff) << 4) & 0xffff;
//
//	    prevResult ^= (((prevResult & 0xff) << 5) & 0xff) | (((((prevResult & 0xff) >> 3) & 0xffff) << 8) & 0xffff);
//
//	    return prevResult;
//	}
//	
//	public static int computeCrc16_z2m(byte[] data, int offset, int length)
//	{
//		int crc = 0xFFFF;
//
//		for (int i = 0 ; i < length ; i ++)
//		{
//			int b = data[i+offset] & 0xFF ; 
//		    crc = halCommonCrc16(b, crc);
//		}
//		return crc ;
//	}
	
//	/**
//	 * 计算 ASH 帧 CRC-16/CCITT-FALSE 输入 data 应是 CRC 之前的原始字节，不能包含 byte stuffing
//	 * 后插入的转义字节。
//	 */
//	public static int computeCrc16_old(byte[] data,int offset,int len)
//	{
//	    int crc = 0xFFFF;
//
//	    for (int k = 0 ; k < len ; k ++) //byte b : data)
//	    {
//	    	byte b = data[offset+k] ;
//	        crc ^= (b & 0xFF);
//
//	        for (int i = 0; i < 8; i++)
//	        {
//	            if ((crc & 0x0001) != 0)
//	            {
//	                crc = (crc >> 1) ^ 0x8408;
//	            }
//	            else
//	            {
//	                crc >>= 1;
//	            }
//	        }
//	    }
//
//	    crc ^= 0xFFFF;
//
//	    return crc & 0xFFFF;
//	}
	
}
