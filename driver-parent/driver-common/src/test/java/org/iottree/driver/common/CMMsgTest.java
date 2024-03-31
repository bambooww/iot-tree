package org.iottree.driver.common;

import java.io.IOException;

import org.iottree.driver.common.modbus.ModbusCmd;
import org.iottree.driver.omron.hostlink.HLMsg;
import org.iottree.driver.omron.hostlink.cmode.CMCmd;
import org.junit.Test;

public class CMMsgTest
{
	@Test
    public void testMsgTest() throws IOException
    {
		String ss = "@00RD00000001" ;
		byte[] bs = ss.getBytes() ;
		String chsum = HLMsg.calFCS(ss) ;
		int sum = ModbusCmd.modbus_crc16_check(bs,bs.length) ;
		
		System.out.println("chksum="+chsum) ;
    }
}
