package org.iottree.driver.common.modbus.slave;

/**
 * bool value mem table
 * 
 * this is middle data area between Provider and Modbus Cmd Handler
 * 
 * outter will read bool values from here,and provider can update data here
 * 
 * @author jasonzhu
 *
 */
public class MBitsTable
{
	/**
	 * 
	 */
	int regAddr = -1 ;
	
	/**
	 *
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
