package org.iottree.driver.s7.eth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.iottree.core.UAVal;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataUtil;
/**
 *                                                                                                                                                                         WR area
 *                       0   1   2   3   4  5   6   7   8   9  10 11 12 13 14 15 16 17 18 19 20 21  22 23  24  25  26  27 28 29 30  31 32 33 34 35 36 --
 *                       TLEN-------  COTP--- S7   J   RI---  PR--- PL---  DL---  F   IC VS LR SI  TS  NE---   DBN-  AT AO------   R  TS  DL--- data---
 *                       
 *                       03 00 00 1f  02 f0 80 32 01  00 00 05 00 00 0E 00 00 04 01 12 0a 10   *  00  00  00 00  84 00 00 00  00 04 00 00
 *                       QB1=11
 *                       03 00 00 24 02 F0 80 32 01  00 00 05 00 00 0E 00 05 05 01 12 0A 10 02 00  01  00  00 82 00 00 08  00 04 00 08 0B
 *                       db10.WORD18=0xFFFE=65534; 
 *                       03 00 00 25 02 F0 80 32 01 00 00 00  05 00 0E 00 00  05 01 12 0A 10 02 00 02  00  0A  84 00 00 90  00 04 00 10 FF FE
 *                       DB10. X2.6=1
 *                       03 00 00 24 02 F0 80 32 01 00 00 00  08 00 0E 00 05 05 01 12 0A 10 01 00  01  00 0A  84 00 00 16  00 03 00 01 01
 *                       DB200. X2.6=1
 *                       03 00 00 24 02 F0 80 32 01 00 00 00  08 00 0E 00 05 05 01 12 0A 10 01 00  01  00 C8  84 00 00 16  00 03 00 01 01
 *                       03 00 00 24 02 F0 80 32 01 00 00 00 08 00 0E 00  05 05 01 12 0A 10 02 00  01  00 C8  84 00 00  16 00 03 00 01 00
				
				TLEN   Telegram Length (Data  Size + 31 or 35)  4bytes
				COPT  3bytes
				S7 Protocol ID  1byte
				J  Job Type
				RI  Redundancy identification  2byte
				PR  PDU Reference
				PL Parameters Length
				DL  Data Length = Size(bytes) + 4
				F   Function 4 Read Var, 5 Write Var
				IC  Items count
				VS  Var spec.
				LR Length of remaining bytes
				SI  Syntax ID
				TS S7WLByte, // Transport Size bit=1  byte=2  C=1C T=1D
				NE  Num Elements
				DBN  DB Number (if any, else 0)
				AT  Area Type
				AO  Area Offset
				// WR area
				R  Reserved
				TS  Transport size    0x03=write bit 0x04=write byte
				DL Data Length * 8 (if not timer or counter)

 * @author jason.zhu
 *
 */
public class S7MsgWrite extends S7Msg
{
	
	/**

	 * @param conn
	 * @param area_memtp
	 * @param db_num
	 * @param pos
	 * @param readnum
	 * @param bs
	 * @throws IOException
	 * @throws S7Exception
	 */
	public static void writeArea(S7TcpConn conn, S7MemTp area_memtp, int db_num, int pos, int readnum, byte[] bs)
			throws IOException, S7Exception
	{
		int ele_byte_n = 1;

		// If we are addressing Timers or counters the element size is 2
		if ((area_memtp == S7MemTp.C) || (area_memtp == S7MemTp.T))
			ele_byte_n = 2;

		int addr;
		int ele_num;
		int data_size;
		int iso_size;
		int len;
		int offset = 0;
		
		int ele_max = (conn.pduLen - 35) / ele_byte_n; // 18 = Reply telegram header
		int ele_tot = readnum;

		while (ele_tot > 0)
		{
			ele_num = ele_tot;
			if (ele_num > ele_max)
				ele_num = ele_max;

			data_size = ele_num * ele_byte_n;
			iso_size = RW_LEN + data_size;

			// Setup the telegram
			System.arraycopy(RW35, 0, conn.PDU, 0, RW_LEN);
			// Whole telegram Size
			S7Util.setUInt16(conn.PDU, 2, iso_size);
			// Data Length
			len = data_size + 4;
			S7Util.setUInt16(conn.PDU, 15, len);
			// write
			conn.PDU[17] = (byte) 0x05;
			conn.PDU[18] = 0x01;
			// mem tp and db number
			conn.PDU[27] = (byte) area_memtp.getVal();
			if (area_memtp == S7MemTp.DB)
				S7Util.setUInt16(conn.PDU, 25, db_num);
			else if(area_memtp == S7MemTp.V)
				S7Util.setUInt16(conn.PDU, 25, 1);

			if ((area_memtp == S7MemTp.C) || (area_memtp == S7MemTp.T))
			{
				addr = pos;
				len = data_size;
				if (area_memtp == S7MemTp.C)
					conn.PDU[22] = WL_COUNTER;
				else
					conn.PDU[22] = WL_TIMER;
			}
			else
			{
				addr = pos *8;
				len = data_size *8;
			}
			// Num elements
			S7Util.setUInt16(conn.PDU, 23, ele_num);
			// Address into the PLC
			conn.PDU[30] = (byte) (addr & 0x0FF);
			addr = addr >> 8;
			conn.PDU[29] = (byte) (addr & 0x0FF);
			addr = addr >> 8;
			conn.PDU[28] = (byte) (addr & 0x0FF);
			// Length
			S7Util.setUInt16(conn.PDU, 33, len);

			// Copies the Data
			System.arraycopy(bs, offset, conn.PDU, 35, data_size);

			if(log.isTraceEnabled())
			{
				String tmps = Convert.byteArray2HexStr(conn.PDU, 0, iso_size," ") ;
				log.trace("S7Msg write ->"+tmps);
			}
			
			//System.out.println("w="+tmps) ;
			
			conn.send(conn.PDU, iso_size);

			len = recvIsoPacket(conn);
			
			if(log.isTraceEnabled())
			{
				if(len>0&&len<1000)
				{
					String tmps = Convert.byteArray2HexStr(conn.PDU, 0, len," ") ;
					log.trace("S7Msg recv <-"+tmps);
				}
			}

			if (len == 22)
			{
				if ((S7Util.getUInt16(conn.PDU, 17) != 0) || (conn.PDU[21] != (byte) 0xFF))
					throw new S7Exception("S7MsgWrite date err,return PDU err");
			}
			else
				throw new S7Exception("invalid pdu");

			offset += data_size;
			ele_tot -= ele_num;
			pos += ele_num * ele_byte_n;
		}
	}
	
	
	public static void writeAreaBit(S7TcpConn conn, S7MemTp area_memtp, int db_num, int pos,int bitidx, int wnum, boolean v)
			throws IOException, S7Exception
	{
		int ele_byte_n = 1;

		// If we are addressing Timers or counters the element size is 2
		if ((area_memtp == S7MemTp.C) || (area_memtp == S7MemTp.T))
			ele_byte_n = 2;

		int addr;
		int ele_num;
		int data_size;
		int iso_size;
		int len;
		int offset = 0;
		
		int ele_max = (conn.pduLen - 35) / ele_byte_n; // 18 = Reply telegram
														// header
		int ele_tot = wnum;

		while (ele_tot > 0)
		{
			ele_num = ele_tot;
			if (ele_num > ele_max)
				ele_num = ele_max;

			data_size = ele_num * ele_byte_n;
			iso_size = RW_LEN + data_size;

			// Setup the telegram
			System.arraycopy(RW35, 0, conn.PDU, 0, RW_LEN);
			// Whole telegram Size
			S7Util.setUInt16(conn.PDU, 2, iso_size);
			
			conn.PDU[11] = 0x00;
			conn.PDU[12] = 0x08;
			
			// Data Length
			len = data_size + 4;
			S7Util.setUInt16(conn.PDU, 15, len);
			// write
			conn.PDU[17] = (byte) 0x05;
			// mem tp and db number
			conn.PDU[27] = (byte) area_memtp.getVal();
			if (area_memtp == S7MemTp.DB)
				S7Util.setUInt16(conn.PDU, 25, db_num);
			else if(area_memtp == S7MemTp.V)
				S7Util.setUInt16(conn.PDU, 25, 1);

			conn.PDU[22] = 0x01;
			if ((area_memtp == S7MemTp.C) || (area_memtp == S7MemTp.T))
			{
				addr = pos;
				len = data_size;
				if (area_memtp == S7MemTp.C)
					conn.PDU[22] = WL_COUNTER;
				else
					conn.PDU[22] = WL_TIMER;
			}
			else
			{
				addr = pos *8+bitidx;
				len = data_size;// * 8;
			}
			// Num elements
			S7Util.setUInt16(conn.PDU, 23, ele_num);
			// Address into the PLC
			conn.PDU[30] = (byte) (addr & 0x0FF);
			addr = addr >> 8;
			conn.PDU[29] = (byte) (addr & 0x0FF);
			addr = addr >> 8;
			conn.PDU[28] = (byte) (addr & 0x0FF);
			// Length
			conn.PDU[32] = 0x03;//write bit
			S7Util.setUInt16(conn.PDU, 33, len);

			// Copies the Data
			//System.arraycopy(bs, offset, conn.PDU, 35, data_size);
			conn.PDU[35] = (byte)(v?1:0) ;

			if(log.isTraceEnabled())
			{
				String tmps = Convert.byteArray2HexStr(conn.PDU, 0, iso_size," ") ;
				log.trace("S7Msg write bit ->"+tmps);
			}
			//System.out.println("send="+Convert.byteArray2HexStr(conn.PDU, 0, iso_size, " "));
			conn.send(conn.PDU, iso_size);
			// 03 00 00 24 02 F0 80 32 01 00 00 05 00 00 0E 00 05 05 01 12 0A 10 02 00 01 00 C8 84 00 00 16 00 03 00 01 01
			// 03 00 00 24 02 F0 80 32 01 00 00 00 08 00 0E 00 05 05 01 12 0A 10 01 00 01 00 C8 84 00 00 16 00 03 00 01 01
			// 03 00 00 24 02 F0 80 32 01 00 00 00 08 00 0E 00 05 05 01 12 0A 10 02 00 01 00 C8 84 00 00 16 00 03 00 01 00
			//byte[] tmpbs = Convert.hexStr2ByteArray("03 00 00 24 02 F0 80 32 01 00 00 00  08 00 0E 00 05 05 01 12 0A 10 01 00  01  00 C8  84 00 00 16  00 03 00 01 01");
			//conn.send(tmpbs, tmpbs.length);

			len = recvIsoPacket(conn);
			
			if(log.isTraceEnabled())
			{
				if(len>0&&len<1000)
				{
					String tmps = Convert.byteArray2HexStr(conn.PDU, 0, len," ") ;
					log.trace("S7Msg recv <-"+tmps);
				}
			}
			//   0  1   2   3  4   5   6  7   8   9  10 11 12 13 14 15 16 17 18 19 20 21
			//  03 00 00 16 02 F0 80 32 03 00 00 00 08 00 02 00 01 00 00 05 01 FF
			//  03 00 00 16 02 F0 80 32 03 00 00 05 00 00 02 00 01 00 00 05 01 07
			if (len == 22)
			{
				//System.out.println("recv="+Convert.byteArray2HexStr(conn.PDU, 0, 22, " "));
				if ((S7Util.getUInt16(conn.PDU, 17) != 0) || (conn.PDU[21] != (byte) 0xFF))
					throw new S7Exception("write date err");
			}
			else
				throw new S7Exception("invalid pdu");

			offset += data_size;
			ele_tot -= ele_num;
			pos += ele_num * ele_byte_n;
		}
	}
	
	S7MemTp areaMemtp;
	int dbNum;
	int pos;
	int bitIdx = -1 ;
	int writeNum;
	Object writeObj = null ;
	byte[] writeV;
	
	public S7MsgWrite withParam(S7MemTp area_memtp, int db_num, int pos, int bitidx,int writenum)
	{
		this.areaMemtp = area_memtp ;
		this.dbNum = db_num;
		this.pos = pos ;
		this.bitIdx = bitidx;
		this.writeNum = writenum ;
		//this.writeV = write_v ;
		
		return this ;
	}
	
	public S7MsgWrite withParam(S7MemTp area_memtp, int db_num, S7Addr addr,Object v,String str_encod) //throws UnsupportedEncodingException
	{
		this.areaMemtp = area_memtp ;
		this.dbNum = db_num;
		this.pos = addr.getOffsetBytes() ;
		this.bitIdx = addr.getInBits() ;
		this.writeNum = addr.getBytesNum();
		this.writeObj = v ;
		this.writeV = transVToBytes(addr,v,str_encod);
		if(this.writeV==null && this.bitIdx<0)
			return null;
		return this ;
	}
	
	private byte[] transVToBytes(S7Addr addr,Object v,String str_encod)// throws UnsupportedEncodingException
	{
		S7ValTp s7vtp = addr.getMemValTp() ;
		if(s7vtp==null ||v==null)
			return null ;
		UAVal.ValTP vtp = addr.getValTP();// s7vtp.getValTP() ;
		int bn = s7vtp.getByteNum() ;
		if(vtp.isNumberVT())
		{
			Number num = (Number)v;
			if(vtp.isNumberFloat())
			{
				//byte[] bs = new byte[4] ;
				return DataUtil.floatToBytes(num.floatValue());
			}
			else
			{
				switch(bn)
				{
				case 1:
					return new byte[] {num.byteValue()} ;
				case 2:
					int intv = num.intValue();
					return new byte[] { (byte) ((intv>>8) & 0xFF) , (byte)(intv & 0xFF)};
				case 4:
					intv = num.intValue();
					return new byte[] { (byte) ((intv>>24) & 0xFF),(byte) ((intv>>16) & 0xFF),(byte) ((intv>>8) & 0xFF) , (byte)(intv & 0xFF)};
				}
			}
		}
		else if(s7vtp==S7ValTp.STRING)
		{
			String strv = v.toString() ;
			byte[] bv = null;
			if(Convert.isNotNullEmpty(str_encod))
			{
				try
				{
					bv = strv.getBytes(str_encod) ;
				}
				catch(Exception ee)
				{
					ee.printStackTrace();
					return null ;
				}
			}
			else
				bv = strv.getBytes() ;
			bn = addr.getBytesNum() ;
			int in_num = addr.inNum ;
			if(in_num+2!=bn || in_num>255)
				return null ;
			if(bv.length>in_num)
				return null ;
			byte[] ret = new byte[bn] ;
			ret[0] = (byte)in_num ;
			ret[1] = (byte)bv.length ;
			System.arraycopy(bv, 0, ret, 2, bv.length);
			return ret;
		}

		
		return null ;
	}
	
//	public S7MsgWrite withWriteData(byte[] bs)
//	{
//		this.writeV = bs ;
//		return this ;
//	}
	
	@Override
	public void processByConn(S7TcpConn conn) throws S7Exception, IOException
	{
		if(this.bitIdx>=0)
		{
			boolean rv ;
			if(this.writeObj instanceof Boolean)
			{
				rv = (Boolean)this.writeObj ;
			}
			else if(this.writeObj instanceof Number)
			{
				rv = ((Number)this.writeObj).intValue()>0 ;
			}
			else
			{
				//String tmps = ""+this.writeObj ;
				//rv = "1".equals(tmps) || "true".equalsIgnoreCase(tmps) ;
				return;//do nothing
			}
				
			writeAreaBit(conn, areaMemtp, dbNum, pos, bitIdx,writeNum, rv);
		}
		else
		{
			writeArea(conn, areaMemtp, dbNum, pos, writeNum, this.writeV);
		}
	}
}
