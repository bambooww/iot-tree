package org.iottree.driver.common.modbus;

public abstract class ModbusCmdRead extends ModbusCmd
{

	public ModbusCmdRead(long scan_inter_ms, int dev_addr)
	{
		super(scan_inter_ms,dev_addr) ;
	}
	
	public abstract int getRegAddr() ;
	
	public abstract int getRegNum();
}
