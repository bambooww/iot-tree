package org.iottree.driver.gb.szy;

/**
 * 数据域——报警状态
 * @author jason.zhu
 *
 */
public class DFTermAlertST extends DataField
{
	int alert ;
	
	int term ;
	/**
	 * 前2字节-报警信息
	 * 后2字节-终端状态
	 * @param bs
	 */
	public DFTermAlertST(byte[] bs)
	{
		if(bs.length!=4)
			throw new IllegalArgumentException("alert st bs len=4") ;
		alert = bs[1] & 0xFF;
		alert <<=8 ;
		alert += bs[0] & 0xFF ;
		
		term = bs[3] & 0xFF;
		term <<=8 ;
		term += bs[2] & 0xFF ;
	}
	
	public int getAlertInf()
	{
		return this.alert;
	}
	
	public int getTermInf()
	{
		return this.term ;
	}
}
