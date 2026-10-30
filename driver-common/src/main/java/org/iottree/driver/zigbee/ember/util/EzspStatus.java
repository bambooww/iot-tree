package org.iottree.driver.zigbee.ember.util;

/** Status values used by EZSP. */
public enum EzspStatus
{
	    /** Success. */
	    SUCCESS (0x00),
	    /** Fatal error. */
	    SPI_ERR_FATAL (0x10),
	    /** The Response frame of the current transaction indicates the NCP has reset. */
	    SPI_ERR_NCP_RESET (0x11),
	    /** The NCP is reporting that the Command frame of the current transaction is oversized (the length byte is too large). */
	    SPI_ERR_OVERSIZED_EZSP_FRAME (0x12),
	    /** The Response frame of the current transaction indicates the previous transaction was aborted (nSSEL deasserted too soon). */
	    SPI_ERR_ABORTED_TRANSACTION (0x13),
	    /** The Response frame of the current transaction indicates the frame terminator is missing from the Command frame. */
	    SPI_ERR_MISSING_FRAME_TERMINATOR (0x14),
	    /** The NCP has not provided a Response within the time limit defined by WAIT_SECTION_TIMEOUT. */
	    SPI_ERR_WAIT_SECTION_TIMEOUT (0x15),
	    /** The Response frame from the NCP is missing the frame terminator. */
	    SPI_ERR_NO_FRAME_TERMINATOR (0x16),
	    /** The Host attempted to send an oversized Command (the length byte is too large) and the AVR's spi-protocol.c blocked the transmission. */
	    SPI_ERR_EZSP_COMMAND_OVERSIZED (0x17),
	    /** The NCP attempted to send an oversized Response (the length byte is too large) and the AVR's spi-protocol.c blocked the reception. */
	    SPI_ERR_EZSP_RESPONSE_OVERSIZED (0x18),
	    /** The Host has sent the Command and is still waiting for the NCP to send a Response. */
	    SPI_WAITING_FOR_RESPONSE (0x19),
	    /** The NCP has not asserted nHOST_INT within the time limit defined by WAKE_HANDSHAKE_TIMEOUT. */
	    SPI_ERR_HANDSHAKE_TIMEOUT (0x1a),
	    /** The NCP has not asserted nHOST_INT after an NCP reset within the time limit defined by STARTUP_TIMEOUT. */
	    SPI_ERR_STARTUP_TIMEOUT (0x1b),
	    /** The Host attempted to verify the SPI Protocol activity and version number), and the verification failed. */
	    SPI_ERR_STARTUP_FAIL (0x1c),
	    /** The Host has sent a command with a SPI Byte that is unsupported by the current mode the NCP is operating in. */
	    SPI_ERR_UNSUPPORTED_SPI_COMMAND (0x1d),
	    /** Operation not yet complete. */
	    ASH_IN_PROGRESS (0x20),
	    /** Fatal error detected by host. */
	    HOST_FATAL_ERROR (0x21),
	    /** Fatal error detected by NCP. */
	    ASH_NCP_FATAL_ERROR (0x22),
	    /** Tried to send DATA frame too long. */
	    DATA_FRAME_TOO_LONG (0x23),
	    /** Tried to send DATA frame too short. */
	    DATA_FRAME_TOO_SHORT (0x24),
	    /** No space for tx'ed DATA frame. */
	    NO_TX_SPACE (0x25),
	    /** No space for rec'd DATA frame. */
	    NO_RX_SPACE (0x26),
	    /** No receive data available. */
	    NO_RX_DATA (0x27),
	    /** Not in Connected state. */
	    NOT_CONNECTED (0x28),
	    /** The NCP received a command before the EZSP version had been set. */
	    ERROR_VERSION_NOT_SET (0x30),
	    /** The NCP received a command containing an unsupported frame ID. */
	    ERROR_INVALID_FRAME_ID (0x31),
	    /** The direction flag in the frame control field was incorrect. */
	    ERROR_WRONG_DIRECTION (0x32),
	    /**
	     * The truncated flag in the frame control field was set, indicating there was not enough memory available to
	     * complete the response or that the response would have exceeded the maximum EZSP frame length.
	     */
	    ERROR_TRUNCATED (0x33),
	    /**
	     * The overflow flag in the frame control field was set, indicating one or more callbacks occurred since the previous
	     * response and there was not enough memory available to report them to the Host.
	     */
	    ERROR_OVERFLOW (0x34),
	    /** Insufficient memory was available. */
	    ERROR_OUT_OF_MEMORY (0x35),
	    /** The value was out of bounds. */
	    ERROR_INVALID_VALUE (0x36),
	    /** The configuration id was not recognized. */
	    ERROR_INVALID_ID (0x37),
	    /** Configuration values can no longer be modified. */
	    ERROR_INVALID_CALL (0x38),
	    /** The NCP failed to respond to a command. */
	    ERROR_NO_RESPONSE (0x39),
	    /** The length of the command exceeded the maximum EZSP frame length. */
	    ERROR_COMMAND_TOO_LONG (0x40),
	    /** The UART receive queue was full causing a callback response to be dropped. */
	    ERROR_QUEUE_FULL (0x41),
	    /** The command has been filtered out by NCP. */
	    ERROR_COMMAND_FILTERED (0x42),
	    /** EZSP Security Key is already set */
	    ERROR_SECURITY_KEY_ALREADY_SET (0x43),
	    /** EZSP Security Type is invalid */
	    ERROR_SECURITY_TYPE_INVALID (0x44),
	    /** EZSP Security Parameters are invalid */
	    ERROR_SECURITY_PARAMETERS_INVALID (0x45),
	    /** EZSP Security Parameters are already set */
	    ERROR_SECURITY_PARAMETERS_ALREADY_SET (0x46),
	    /** EZSP Security Key is not set */
	    ERROR_SECURITY_KEY_NOT_SET (0x47),
	    /** EZSP Security Parameters are not set */
	    ERROR_SECURITY_PARAMETERS_NOT_SET (0x48),
	    /** Received frame with unsupported control byte */
	    ERROR_UNSUPPORTED_CONTROL (0x49),
	    /** Received frame is unsecure), when security is established */
	    ERROR_UNSECURE_FRAME (0x4a),
	    /** Incompatible ASH version */
	    ASH_ERROR_VERSION (0x50),
	    /** Exceeded max ACK timeouts */
	    ASH_ERROR_TIMEOUTS (0x51),
	    /** Timed out waiting for RSTACK */
	    ASH_ERROR_RESET_FAIL (0x52),
	    /** Unexpected ncp reset */
	    ASH_ERROR_NCP_RESET (0x53),
	    /** Serial port initialization failed */
	    ERROR_SERIAL_INIT (0x54),
	    /** Invalid ncp processor type */
	    ASH_ERROR_NCP_TYPE (0x55),
	    /** Invalid ncp reset method */
	    ASH_ERROR_RESET_METHOD (0x56),
	    /** XON/XOFF not supported by host driver */
	    ASH_ERROR_XON_XOFF (0x57),
	    /** ASH protocol started */
	    ASH_STARTED (0x70),
	    /** ASH protocol connected */
	    ASH_CONNECTED (0x71),
	    /** ASH protocol disconnected */
	    ASH_DISCONNECTED (0x72),
	    /** Timer expired waiting for ack */
	    ASH_ACK_TIMEOUT (0x73),
	    /** Frame in progress cancelled */
	    ASH_CANCELLED (0x74),
	    /** Received frame out of sequence */
	    ASH_OUT_OF_SEQUENCE (0x75),
	    /** Received frame with CRC error */
	    ASH_BAD_CRC (0x76),
	    /** Received frame with comm error */
	    ASH_COMM_ERROR (0x77),
	    /** Received frame with bad ackNum */
	    ASH_BAD_ACKNUM (0x78),
	    /** Received frame shorter than minimum */
	    ASH_TOO_SHORT (0x79),
	    /** Received frame longer than maximum */
	    ASH_TOO_LONG (0x7a),
	    /** Received frame with illegal control byte */
	    ASH_BAD_CONTROL (0x7b),
	    /** Received frame with illegal length for its type */
	    ASH_BAD_LENGTH (0x7c),
	    /** Received ASH Ack */
	    ASH_ACK_RECEIVED (0x7d),
	    /** Sent ASH Ack */
	    ASH_ACK_SENT (0x7e),
	    /** Received ASH Nak */
	    ASH_NAK_RECEIVED (0x7f),
	    /** Sent ASH Nak */
	    ASH_NAK_SENT (0x80),
	    /** Received ASH RST */
	    ASH_RST_RECEIVED (0x81),
	    /** Sent ASH RST */
	    ASH_RST_SENT (0x82),
	    /** ASH Status */
	    ASH_STATUS (0x83),
	    /** ASH TX */
	    ASH_TX (0x84),
	    /** ASH RX */
	    ASH_RX (0x85),
	    /** Failed to connect to CPC daemon or failed to open CPC endpoint */
	    CPC_ERROR_INIT (0x86),
	    /** No reset or error */
	    NO_ERROR (0xff);
	    
	    private final int val;
		//private final String title;

	    EzspStatus(int val) //, String title)
		{
			this.val = val;
		}
		
		public int getIntVal()
		{
			return this.val ;
		}
		
//		public String getTitle()
//		{
//			return this.title ;
//		}
		
		public static EzspStatus valOfInt(int v)
		{
			for(EzspStatus st:EzspStatus.values())
			{
				if(st.val==v)
					return st ;
			}
			return null ;
		}
}
