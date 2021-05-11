package org.iottree.driver.common.modbus.slave;

/**
 * 支持bool值的内存表
 * 此表是数据Provider和modbus具体指令处理的一个中间数据区
 * 
 * 通过此数据区，外界可以通过modbus读取bool值，而provider更新里面的内容
 * @author jasonzhu
 *
 */
public class MBitsTable
{
	/**
	 * 本数据区对应的寄存器起始地址
	 */
	int regAddr = -1 ;
	
	/**
	 * 数据数量
	 */
	int regNum = -1 ;
	
	boolean[] regData = null ;
	
	
	public MBitsTable(int regaddr,int regnum)
	{
		this.regAddr = regaddr ;
		this.regNum = regnum ;
		
		regData = new boolean[regnum] ;
	}
	
	public int getRegAddr()
	{
		return regAddr ;
	}
	
	public int getRegNum()
	{
		return regNum ;
	}
	
	public boolean[] getRegData()
	{
		return regData ;
	}
	
	
	
}
