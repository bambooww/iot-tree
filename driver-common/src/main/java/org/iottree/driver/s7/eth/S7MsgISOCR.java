package org.iottree.driver.s7.eth;

import java.io.IOException;

/**
 * after tcp socket connected, it msg send ISO Connect request pack
 * 
 * @author jason.zhu
 *
 */
public class S7MsgISOCR extends S7Msg
{
	// Telegrams
	// ISO Connection Request telegram (contains also ISO Header and COTP
	// Header)
	private static byte ISO_CR[] = {
			// TPKT (RFC1006 Header)
			(byte) 0x03, // RFC 1006 ID (3)
			(byte) 0x00, // Reserved, always 0
			(byte) 0x00, // High part of packet lenght (entire frame, payload
							// and Tconn.PDU included)
			(byte) 0x16, // Low part of packet lenght (entire frame, payload and
							// Tconn.PDU included)
			// COTP (ISO 8073 Header)
			(byte) 0x11, // conn.PDU Size Length
			(byte) 0xE0, // CR - Connection Request ID
			(byte) 0x00, // Dst Reference HI
			(byte) 0x00, // Dst Reference LO
			(byte) 0x00, // Src Reference HI
			(byte) 0x01, // Src Reference LO
			(byte) 0x00, // Class + Options Flags
			(byte) 0xC0, // conn.PDU Max Length ID
			(byte) 0x01, // conn.PDU Max Length HI
			(byte) 0x0A, // conn.PDU Max Length LO
			(byte) 0xC1, // Src TSAP Identifier
			(byte) 0x02, // Src TSAP Length (2 bytes)
			(byte) 0x01, // Src TSAP HI (will be overwritten)
			(byte) 0x00, // Src TSAP LO (will be overwritten)
			(byte) 0xC2, // Dst TSAP Identifier
			(byte) 0x02, // Dst TSAP Length (2 bytes)
			(byte) 0x01, // Dst TSAP HI (will be overwritten)
			(byte) 0x02 // Dst TSAP LO (will be overwritten)
	};

	// S7 conn.PDU Negotiation Telegram (contains also ISO Header and COTP Header)
	private static final byte S7_PN[] = { (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x19, (byte) 0x02, (byte) 0xf0,
			(byte) 0x80, // TPKT + COTP (see above for info)
			(byte) 0x32, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x08,
			(byte) 0x00, (byte) 0x00, (byte) 0xf0, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
			(byte) 0x00, (byte) 0x1e // conn.PDU Length Requested = HI-LO 480 bytes
	};

	
	
	


	private int recvPduLength(S7TcpConn conn) throws IOException, S7Exception
	{

		// Set conn.PDU Size Requested
		S7Util.setUInt16(S7_PN, 23, PDU_DEFAULT_LEN);
		// Sends the connection request telegram
		conn.send(S7_PN);

		int len = recvIsoPacket(conn);

		// check S7 Error
		if ((len == 27) && (conn.PDU[17] == 0) && (conn.PDU[18] == 0))
		{
			// Get conn.PDU Size Negotiated
			conn.pduLen = S7Util.getUInt16(conn.PDU, 25);
			if (conn.pduLen <= 0)
				throw new S7Exception("get conn.PDU lenght failedr");

			return conn.pduLen;
		}
		else
			throw new S7Exception("get conn.PDU lenght failedr");

	}

	public void processByConn(S7TcpConn conn) throws S7Exception, IOException
	{
		ISO_CR[16] = conn.tsapLocalHI;// ;
		ISO_CR[17] = conn.tsapLocalLO;// ;
		ISO_CR[20] = conn.tsapRemoteHI;

		ISO_CR[21] = conn.tsapRemoteLO;// ;

		// Sends the connection request telegram
		conn.send(ISO_CR);

		// Gets the reply (if any)
		int sz = recvIsoPacket(conn);

		if (sz == 22)
		{
			if (conn.pduType != (byte) 0xD0) // 0xD0 = CC Connection confirm
				throw new S7Exception("ISO Connected failed");
		}
		else
			throw new S7Exception("Invalid conn.PDU");

		recvPduLength(conn);
	}

}
