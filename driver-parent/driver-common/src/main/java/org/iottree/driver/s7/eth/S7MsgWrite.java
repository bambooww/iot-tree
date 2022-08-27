package org.iottree.driver.s7.eth;

import java.io.IOException;


public class S7MsgWrite extends S7Msg
{
	private static final int Size_WR = 35;

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
		
		int ele_max = (conn.pduLen - 35) / ele_byte_n; // 18 = Reply telegram
														// header
		int ele_tot = readnum;

		while (ele_tot > 0)
		{
			ele_num = ele_tot;
			if (ele_num > ele_max)
				ele_num = ele_max;

			data_size = ele_num * ele_byte_n;
			iso_size = Size_WR + data_size;

			// Setup the telegram
			System.arraycopy(S7_RW, 0, conn.PDU, 0, Size_WR);
			// Whole telegram Size
			S7Util.setUInt16(conn.PDU, 2, iso_size);
			// Data Length
			len = data_size + 4;
			S7Util.setUInt16(conn.PDU, 15, len);
			// Function
			conn.PDU[17] = (byte) 0x05;
			// Set DB Number
			conn.PDU[27] = (byte) area_memtp.getVal();
			if (area_memtp == S7MemTp.DB)
				S7Util.setUInt16(conn.PDU, 25, db_num);

			// Adjusts Start and word length
			if ((area_memtp == S7MemTp.C) || (area_memtp == S7MemTp.T))
			{
				addr = pos;
				len = data_size;
				if (area_memtp == S7MemTp.C)
					conn.PDU[22] = S7WLCounter;
				else
					conn.PDU[22] = S7WLTimer;
			}
			else
			{
				addr = pos << 3;
				len = data_size << 3;
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

			conn.send(conn.PDU, iso_size);

			len = recvIsoPacket(conn);

			if (len == 22)
			{
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
	int writeNum;
	byte[] writeRes;
	
	public S7MsgWrite withParam(S7MemTp area_memtp, int db_num, int pos, int writenum)
	{
		this.areaMemtp = area_memtp ;
		this.dbNum = db_num;
		this.pos = pos ;
		this.writeNum = writenum ;
		
		return this ;
	}
	
	public S7MsgWrite withParam(S7MemTp area_memtp, int db_num, S7Addr addr,Object v)
	{
		this.areaMemtp = area_memtp ;
		this.dbNum = db_num;
		this.pos = addr.getOffsetBytes() ;
		this.writeNum = addr.getBytesNum();
		
		return this ;
	}
	
	public S7MsgWrite withWriteData(byte[] bs)
	{
		this.writeRes = bs ;
		return this ;
	}
	@Override
	public void processByConn(S7TcpConn conn) throws S7Exception, IOException
	{
		writeArea(conn, areaMemtp, dbNum, pos, writeNum, this.writeRes);
		
	}

}
