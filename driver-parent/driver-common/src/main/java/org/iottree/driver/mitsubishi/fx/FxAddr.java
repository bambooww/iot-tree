package org.iottree.driver.mitsubishi.fx;

import org.iottree.core.UAVal.ValTP;
import org.iottree.driver.mitsubishi.Addr;

/**
 * PLC inner address defintion
 * 
 *  D:   PLC-Address*2+1000H;   数据寄存器D bit16
    T:   PLC-Address+00C0H; //timer  bit16
    C:   PLC-Address*2+01C0H; //count  bit16
    S:   PLC-Address*3;  状态继电器
    M:   PLC-Address*2+0100H;   辅助继电器
    Y:   PLC-Address+00A0H; out
    X:   PLC-Address+0080H; input(只能读不能写，输入寄存器必须由外部信号驱动)
    
         PLC-Address元件是指最低位开始后的第N个元件的位置。
         
 * @author jason.zhu
 *
 */
public class FxAddr extends Addr
{
	FxModel fxModel ;
	
	transient FxAddrDef addrDef = null ;
	
	transient FxAddrSeg addrSeg = null ;
	
	public FxAddr()
	{}
	
	FxAddr(String addr_str,ValTP vtp,FxModel fx_m,String prefix,int addr_num,boolean b_valbit,int digit_num,boolean b_oct)
	{
		super(addr_str,vtp,prefix,addr_num,b_valbit,digit_num,b_oct) ;
		this.fxModel = fx_m ;
//		this.prefix = prefix ;
//		this.addrNum = addr_num ;
//		this.bValBit = b_valbit ;
//		this.digitNum = digit_num ;
//		this.bOct = b_oct ;
	}
	
	FxAddr asDef(FxAddrDef addr_def,FxAddrSeg seg)
	{
		this.addrDef = addr_def ;
		this.addrSeg = seg ;
		this.bWritable = this.addrSeg.isWritable() ;
		return this ;
	}
	
	public int getBytesInBase()
	{
		//FxAddrDef def = fxModel.getAddrDef(this.prefix) ;
		return this.addrSeg.calBytesInBase(this.addrNum) ;
	}
	

}
