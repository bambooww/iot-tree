package org.iottree.driver.profibus;

/**
 * Profibus Message pack
 * @author jason.zhu
 *
 */
public class PFMsg
{
	/**
	 * one byte acknowledgement
	 */
	public static final short PK_ACK = 0xE5;
	
	/**
	 * no data pk 
	 * [SD1][DA][SA][FC][FCS][ED]
	 */
	public static final short SD1 = 0x10;
	
	/**
	 * var length pk
	 * 
	 * SD2	LE LEr	SD2	DA	SA	FC 	DSAP 	SSAP 	PDU 	FCS	ED
	 */
	public static final short SD2 = 0x68;
	
	/**
	 * fixed length pk
	 * 
	 * SD3	DA	SA	 FC	PDU	FCS	ED
	 */
	public static final short SD3 = 0xA2;
	
	/**
	 * Token
	 * SD4	DA	SA 	ED
	 */
	public static final short SD4 = 0xDC;
	
	
	/**
	 * start byte in msg pk
	 */
	short sd ;
	
	/**
	 * target address
	 */
	short da;
	
	/**
	 * sor addr
	 */
	short sa;
	
	public PFMsg()
	{}
	
	/**
	 * msg first (start) byte —— msg type
	 * @return
	 */
	public short getStartD()
	{
		return this.sd ;
	}
	
	public boolean isTokenMsg()
	{
		return this.sd==SD4 ;
	}
	
	/**
	 * Source Address
	 * @return
	 */
	public short getSorAddr()
	{
		return sa ;
	}
	
	/**
	 * target address
	 * @return
	 */
	public short getDestAddr()
	{
		return da ;
	}
}
