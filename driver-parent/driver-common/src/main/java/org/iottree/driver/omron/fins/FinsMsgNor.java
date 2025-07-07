package org.iottree.driver.omron.fins;

import java.io.OutputStream;

import org.iottree.core.util.IBSOutput;

/**
 * normal communication msg
 * 
 * @author jason.zhu
 *
 */
public abstract class FinsMsgNor extends FinsMsg
{

	protected FinsMode mode ; 

	short dna = -1 ; //desction network addr 
	
	short da1 = -1 ;
	
	short da2 = -1 ;
	
	short sna = -1 ;
	
	short sa1 = -1 ;
	
	short sa2 = -1 ;
	
	public FinsMsgNor(FinsMode fins_mode)
	{
		super();
		
		this.mode = fins_mode ;
	}
	
	/**
	 * dna - network address
	 * 
		 * 1 to 127 (01 to 7F Hex)
			Local node address: 00 Hex
	 * 
	 * da1  node address
	 * 
	 * 		1 to 254 (01 to FE Hex) (See note.)
			Note The node addresses differ for each
			network.
			Internal Communications in PLC: 00 Hex
			For Controller Link: 01 to 3E Hex (1 to 62)
			For Ethernet Units with model numbers
			ending in ETN21: 01 to FE Hex (1 to 254)
			For Ethernet Units with other model
			numbers: 01 to 7E Hex (1 to 126)
	 * 
	 * da2 unit address:
	 * 
		  	CPU Unit: 00 Hex
			•CPU Bus Unit: Unit No.+ 10 Hex
			•Special I/O Unit: Unit No.+ 20 Hex
			•Inner Board: E1 Hex
			•Computer: 01 Hex
			•Unit connected to network: FE Hex
	 * @param dna
	 * @param da1
	 * @param da2
	 * @return
	 */
	public FinsMsg asDest(short dna,short da1,short da2)
	{
		this.dna = dna ;
		this.da1 = da1 ;
		this.da2 = da2 ;
		return this ;
	}
	
	public FinsMsg asSor(short sna,short sa1,short sa2)
	{
		this.sna = sna ;
		this.sa1 = sa1 ;
		this.sa2 = sa2 ;
		return this ;
	}
	
	

	/**
	 * int tmpi = isNeedResp()?0:1 ;
		if(!isReqOrResp())
			tmpi |= 0x80 ;
	 * @return
	 */
	protected abstract short getICF() ;
	
	
	
//	protected abstract boolean isNeedResp() ;
//	/**
//	 * true=request  false=response
//	 * @return
//	 */
//	protected abstract boolean isReqOrResp() ;
	//public abstract int getCommand() ;
	
	
}
