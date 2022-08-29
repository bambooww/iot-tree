package org.iottree.driver.s7.eth;


import java.io.IOException;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

public abstract class S7Msg
{
	static final ILogger log = LoggerManager.getLogger(S7Msg.class) ;
	
	public final static int RESULT_ADDRESS_OUT_OF_RANGE = 5;
	/* means the write data size doesn't fit item size */
	public final static int RESULT_CANNOT_EVALUATE_PDU = -123;
	public final static int RESULT_CPU_RETURNED_NO_DATA = -124;
	public final static int RESULT_EMPTY_RESULT_ERROR = -126;

	public final static int RESULT_EMPTY_RESULT_SET_ERROR = -127;

	public final static int RESULT_ITEM_NOT_AVAILABLE = 10;
	/* means a a piece of data is not available in the CPU, e.g. */
	/* when trying to read a non existing DB */
	/* CPU tells it does not support to read a bit block with a */
	/* length other than 1 bit. */
	public final static int RESULT_ITEM_NOT_AVAILABLE200 = 3;
	/* means a a piece of data is not available in the CPU, e.g. */
	/* when trying to read a non existing DB or bit bloc of length<>1 */
	/* This code seems to be specific to 200 family. */
	/* CPU tells there is no peripheral at address */
	public final static int RESULT_MULTIPLE_BITS_NOT_SUPPORTED = 6;
	public final static int RESULT_NO_PERIPHERAL_AT_ADDRESS = 1;

	public final static int RESULT_OK = 0; /* means all ok */
	public final static int RESULT_SHORT_PACKET = -1024;
	public final static int RESULT_TIMEOUT = -1025;
	public final static int RESULT_UNEXPECTED_FUNC = -128;
	public final static int RESULT_UNKNOWN_DATA_UNIT_SIZE = -129;

	public final static int RESULT_UNKNOWN_ERROR = -125;
	/* means the data address is beyond the CPUs address range */
	public final static int RESULT_WRITE_DATA_SIZE_MISMATCH = 7;

	public static String getResultDesc(int code)
	{
		switch (code)
		{
		case RESULT_OK:
			return "ok";
		case RESULT_MULTIPLE_BITS_NOT_SUPPORTED:
			return "the CPU does not support reading a bit block of length<>1";
		case RESULT_ITEM_NOT_AVAILABLE:
			return "the desired item is not available in the PLC";
		case RESULT_ITEM_NOT_AVAILABLE200:
			return "the desired item is not available in the PLC (200 family)";
		case RESULT_ADDRESS_OUT_OF_RANGE:
			return "the desired address is beyond limit for this PLC";
		case RESULT_CPU_RETURNED_NO_DATA:
			return "the PLC returned a packet with no result data";
		case RESULT_UNKNOWN_ERROR:
			return "the PLC returned an error code not understood by this library";
		case RESULT_EMPTY_RESULT_ERROR:
			return "this result contains no data";
		case RESULT_EMPTY_RESULT_SET_ERROR:
			return "cannot work with an undefined result set";
		case RESULT_CANNOT_EVALUATE_PDU:
			return "cannot evaluate the received PDU";
		case RESULT_WRITE_DATA_SIZE_MISMATCH:
			return "Write data size error";
		case RESULT_NO_PERIPHERAL_AT_ADDRESS:
			return "No data from I/O module";
		case RESULT_UNEXPECTED_FUNC:
			return "Unexpected function code in answer";
		case RESULT_UNKNOWN_DATA_UNIT_SIZE:
			return "PLC responds wit an unknown data type";
		case RESULT_SHORT_PACKET:
			return "Short packet from PLC";
		case RESULT_TIMEOUT:
			return "Timeout when waiting for PLC response";
		case 0x8000:
			return "function already occupied.";
		case 0x8001:
			return "not allowed in current operating status.";
		case 0x8101:
			return "hardware fault.";
		case 0x8103:
			return "object access not allowed.";
		case 0x8104:
			return "context is not supported.";
		case 0x8105:
			return "invalid address.";
		case 0x8106:
			return "data type not supported.";
		case 0x8107:
			return "data type not consistent.";
		case 0x810A:
			return "object does not exist.";
		case 0x8500:
			return "incorrect PDU size.";
		case 0x8702:
			return "address invalid.";
		case 0xd201:
			return "block name syntax error.";
		case 0xd202:
			return "syntax error function parameter.";
		case 0xd203:
			return "syntax error block type.";
		case 0xd204:
			return "no linked block in storage medium.";
		case 0xd205:
			return "object already exists.";
		case 0xd206:
			return "object already exists.";
		case 0xd207:
			return "block exists in EPROM.";
		case 0xd209:
			return "block does not exist.";
		case 0xd20e:
			return "no block does not exist.";
		case 0xd210:
			return "block number too big.";
		case 0xd240:
			return "unfinished block transfer in progress?";
		case 0xd241:
			return "protected by password.";
		default:
			return "no message defined for code: " + code + "!";
		}
	}
	
	protected static final int ISO_HEAD_LEN = 7; // TPKT+COTP Header Length
	
	protected static final int PDU_MIN_LEN = 16;
	protected static final int PDU_DEFAULT_LEN = 480;
	protected static final int PDU_MAX_LEN = PDU_DEFAULT_LEN + ISO_HEAD_LEN;


	protected static final byte WL_BYTE = 0x02;
	protected static final byte WL_COUNTER = 0x1C;
	protected static final byte WL_TIMER = 0x1D;

	// S7 Read/Write Request Header (contains also ISO Header and COTP Header)
	protected static final byte RW35[] = { // 31-35 bytes
				(byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x1f, // Telegram Length (Data  Size + 31 or  35)
				(byte) 0x02, (byte) 0xf0, (byte) 0x80, // COTP (see above for info)
				(byte) 0x32, // S7 Protocol ID
				(byte) 0x01, // Job Type
				(byte) 0x00, (byte) 0x00, // Redundancy identification
				(byte) 0x05, (byte) 0x00, // PDU Reference
				(byte) 0x00, (byte) 0x0e, // Parameters Length
				(byte) 0x00, (byte) 0x00, // Data Length = Size(bytes) + 4
				(byte) 0x04, // Function 4 Read Var, 5 Write Var
				(byte) 0x01, // Items count
				(byte) 0x12, // Var spec.
				(byte) 0x0a, // Length of remaining bytes
				(byte) 0x10, // Syntax ID
				WL_BYTE, // Transport Size
				(byte) 0x00, (byte) 0x00, // Num Elements
				(byte) 0x00, (byte) 0x00, // DB Number (if any, else 0)
				(byte) 0x84, // Area Type
				(byte) 0x00, (byte) 0x00, (byte) 0x00, // Area Offset
				// WR area
				(byte) 0x00, // Reserved
				(byte) 0x04, // Transport size  
				(byte) 0x00, (byte) 0x00, // Data Length * 8 (if not timer or counter)
		};

	protected static final int RW_LEN = 35;


	protected static int recvIsoPacket(S7TcpConn conn) throws IOException, S7Exception
	{
		int size = 0;
		while (true)
		{
			conn.recv(conn.PDU, 0, 4); // TPKT 4 bytes

			size = S7Util.getUInt16(conn.PDU, 2);
			// Check 0 bytes Data Packet (only TPKT+COTP = 7 bytes)
			if (size == ISO_HEAD_LEN)
				conn.recv(conn.PDU, 4, 3); // Skip remaining 3 bytes and no end
			else
			{
				if ((size > PDU_MAX_LEN) || (size < PDU_MIN_LEN))
					throw new S7Exception("invalid conn.PDU");
				break;
			}
		}

		conn.recv(conn.PDU, 4, 3); // Skip remaining 3 COTP bytes
		conn.pduType = conn.PDU[5];

		conn.recv(conn.PDU, 7, size - ISO_HEAD_LEN);

		return size;

	}
	
	protected S7EthDriver driver = null ;
	
	protected long scanIntervalMS = 100 ;
	
	private transient long lastRunT = -1 ;
	/**
	 * impl will use this to
	 * @param conn
	 */
	public abstract void processByConn(S7TcpConn conn) throws S7Exception,IOException;
	
	void init(S7EthDriver drv)
	{
		this.driver = drv ;
	}
	
	public S7Msg withScanIntervalMS(long ms)
	{
		this.scanIntervalMS = ms ;
		return this ;
	}
	
	public boolean tickCanRun()
	{
		long ct = System.currentTimeMillis();
		if (ct - lastRunT >scanIntervalMS)// getScanIntervalMS())
		{
			lastRunT = ct;
			// System.out.println("11");
			return true;
		}
		return false;
	}
	
	
}
