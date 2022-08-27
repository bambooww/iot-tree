package org.iottree.core.basic;

/**
 * LIB : x86 window linus
 * MSB: MAC OS
 * @author jason.zhu
 *
 */
public enum ByteOrder
{
	LittleEndian(1),  //LSB
	BigEndian(2),  //MSB
	ModbusWord(3);
	
	private final int val ;
	
	ByteOrder(int v)
	{
		val = v ;
	}
	
	public int getVal()
	{
		return val ;
	}
	
	
}
