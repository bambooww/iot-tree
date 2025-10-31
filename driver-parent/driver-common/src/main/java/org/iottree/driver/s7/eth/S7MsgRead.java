package org.iottree.driver.s7.eth;

import java.io.IOException;

public class S7MsgRead extends S7Msg
{
	
	private static final int Size_RD = 31;
	
	
	private static int calBytes(S7MemTp area_memtp, int readnum)
	{
		int ele_byte_n = 1;

		// If we are addressing Timers or counters the element size is 2
		if ((area_memtp == S7MemTp.C) || (area_memtp == S7MemTp.T))
			ele_byte_n = 2;

		return readnum * ele_byte_n;
	}


	/**
	 * 
	 * @param conn
	 * @param area_memtp
	 * @param db_num memtp is not db will set 0
	 * @param pos
	 * @param readnum
	 * @param bs
	 * @throws S7Exception
	 * @throws IOException
	 */
	static void readArea(S7TcpConn conn, S7MemTp area_memtp, int db_num, int pos, int readnum, byte[] bs)
			throws S7Exception, IOException
	{
		int ele_byte_n = 1;

		// If we are addressing Timers or counters the element size is 2
		if ((area_memtp == S7MemTp.C) || (area_memtp == S7MemTp.T))
			ele_byte_n = 2;

		int addr;
		int ele_num;
		int req_bytes;
		int ele_max = (conn.pduLen - 18) / ele_byte_n; // 18 = Reply telegram
														// header
		int ele_tot = readnum;
		int len;
		int offset = 0;

		while ((ele_tot > 0))
		{
			ele_num = ele_tot;
			if (ele_num > ele_max)
				ele_num = ele_max;

			req_bytes = ele_num * ele_byte_n;

			// Setup the telegram
			System.arraycopy(RW35, 0, conn.PDU, 0, Size_RD);
			// Set DB Number
			conn.PDU[27] = (byte) area_memtp.getVal();
			// Set Area
			if (area_memtp == S7MemTp.DB) // S7.S7AreaDB)
				S7Util.setUInt16(conn.PDU, 25, db_num);
			else  if(area_memtp==S7MemTp.V)
				S7Util.setUInt16(conn.PDU, 25, 1); //DB1 等于V段

			// Adjusts Start and word length
			if ((area_memtp == S7MemTp.C) || (area_memtp == S7MemTp.T))
			{
				addr = pos;
				if (area_memtp == S7MemTp.C)
					conn.PDU[22] = WL_COUNTER;
				else
					conn.PDU[22] = WL_TIMER;
			}
			else
				addr = pos << 3;

			// Num elements
			S7Util.setUInt16(conn.PDU, 23, ele_num);

			// Address into the PLC (only 3 bytes)
			conn.PDU[30] = (byte) (addr & 0x0FF);
			addr >>= 8;
			conn.PDU[29] = (byte) (addr & 0x0FF);
			addr >>= 8;
			conn.PDU[28] = (byte) (addr & 0x0FF);

			conn.send(conn.PDU, Size_RD);

			len = recvIsoPacket(conn);

			if (len >= 25)
			{
				if ((len - 25 == req_bytes) && (conn.PDU[21] == (byte) 0xFF))
				{
					System.arraycopy(conn.PDU, 25, bs, offset, req_bytes);
					offset += req_bytes;
				}
				else
					throw new S7Exception("read err");
			}
			else
				throw new S7Exception("invalid pdu");

			ele_tot -= ele_num;
			pos += ele_num * ele_byte_n;
		}
	}
	
	
	

	S7MemTp areaMemtp;
	int dbNum;
	int pos;
	int readNum;
	byte[] readRes;

	private boolean readOk = false;

	public S7MsgRead withParam(S7MemTp area_memtp, int db_num, int pos, int readnum)
	{
		this.areaMemtp = area_memtp;
		this.dbNum = db_num;
		this.pos = pos;
		this.readNum = readnum;
		return this;
	}
	
	public int getPos()
	{
		return this.pos ;
	}

	public byte[] getReadRes()
	{
		return this.readRes;
	}

	public boolean isReadOk()
	{
		return this.readOk;
	}

	@Override
	public void processByConn(S7TcpConn conn) throws S7Exception, IOException
	{
		int n = calBytes(this.areaMemtp, this.readNum);
		byte[] bs = new byte[n];
		readArea(conn, areaMemtp, dbNum, pos, readNum, bs);

		this.readRes = bs;
		this.readOk = true;
	}
}
