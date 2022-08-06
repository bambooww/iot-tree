package org.iottree.driver.s7.ppi;

/**
 * SD DA SA FC FCS ED
 * @author jason.zhu
 *
 */
public class PPIMsgReqConfirm extends PPIMsg
{
	static byte FC = 0x5C ;
	
	/**
	 * sor address
	 */
	short sa ;
	
	/**
	 * target address
	 */
	short da;
	
	
	@Override
	protected short getStartD()
	{
		return SD_REQ_CONFIRM;
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
	
	
	public PPIMsgReqConfirm withSorAddr(short sa)
	{
		this.sa = sa ;
		return this ;
	}
	
	public PPIMsgReqConfirm withDestAddr(short da)
	{
		this.da = da ;
		return this ;
	}


	@Override
	public byte[] toBytes()
	{
		
		byte[] rets = new byte[6];
		rets[0] = (byte)getStartD();
		rets[1] = (byte)da ;
		rets[2] = (byte)sa;
		rets[3] = FC;
		rets[4] = calChkSum(rets,1,3) ;
		rets[5] = 0x16 ;//end
		
		return rets ;
	}
}
