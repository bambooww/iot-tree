package org.iottree.driver.common.modbus;

import java.util.*;

public interface ModbusRunListener
{
	public void onModbusReadData(ModbusCmd mc,Object[] vals)
		throws Exception;
	
	public void onModbusReadChanged(ModbusCmd mc,HashMap<Integer,Object> addr2val) ; ;
	
	public void onModbusReadFailed(ModbusCmd mc) ;
	
	
	public void onModbusCmdRunError()throws Exception ;
}
