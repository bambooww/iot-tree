package org.iottree.driver.common;

import org.iottree.core.DevDriver;
import org.iottree.driver.common.modbus.ModbusCmd;

public class ModbusDrvTCP extends ModbusDrvRTU
{
	@Override
	public String getName()
	{
		return "modbus_tcp";
	}

	@Override
	public String getTitle()
	{
		return "Modbus TCP";
	}
	
	public DevDriver copyMe()
	{
		return new ModbusDrvTCP() ;
	}
	
	protected  boolean initDriver(StringBuilder failedr) throws Exception
	{
		boolean b = super.initDriver(failedr);
		if(!b)
			return false;
		for(ModbusDevItem di:this.modbusDevItems)
		{
			di.setModbusProtocal(ModbusCmd.Protocol.tcp);
		}
		return true;
	}
}
