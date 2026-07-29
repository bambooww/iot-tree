package org.iottree.driver.s7.ppi;

import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.basic.ByteOrder;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataUtil;

public class PPICmdW extends PPICmd
{
	PPIAddr writeAddr = null ;
	
	int wVal  ;
	
	transient private PPIMsgReqW req = null ;
	transient private PPIMsgReqConfirm reqc = null ; 
	
	public PPICmdW(short dev_addr, PPIMemTp ppi_mtp,PPIAddr addr,Object wval)
	{
		super(dev_addr, ppi_mtp);
		this.writeAddr = addr ;
		//if(addr.getBytesNum())
		
		if(wval instanceof Number)
		{
			if(wval instanceof Float)
			{
				byte[] bs = DataUtil.floatToBytes((Float)wval);
				this.wVal = DataUtil.bytesToInt(bs, ByteOrder.LittleEndian) ;
			}
			else
			{
				this.wVal = ((Number)wval).intValue() ;
			}
		}
		else if(wval instanceof Boolean)
			this.wVal = (((Boolean)wval).booleanValue())?1:0;
		else
			this.wVal = Integer.parseInt(wval+"") ;
	}
	
	void initCmd(PPIDriver drv)
	{
		super.initCmd(drv);
		
		int offetbytes = this.writeAddr.getOffsetBytes() ;
		int inbit = this.writeAddr.getInBits() ;
		req = new PPIMsgReqW();
		req.withWriteVal(writeAddr, this.wVal)
			.withAddrByte(this.ppiMemTp,offetbytes ,inbit).withSorAddr(ppiDrv.getMasterID())
			.withDestAddr(devAddr);//(addr_str)

		reqc = new PPIMsgReqConfirm();
		reqc.withSorAddr(ppiDrv.getMasterID())
			.withDestAddr(devAddr) ;
	}

	@Override
	public boolean doCmd(InputStream inputs, OutputStream outputs) throws Exception
	{
		Thread.sleep(ppiDrv.getCmdInterval());
		
		byte[] bs2 = reqc.toBytes();
		//retBs = null;
		inputs.skip(inputs.available()) ;
		outputs.write(bs2);
		byte[] retbs = PPIMsg.readFromStream(inputs, ppiDrv.getReadTimeout()) ;
		if(retbs==null)
		{
			return false;
		}
		if(retbs.length==1)
		{
			//confirm ok
		}
		else
		{
			//StringBuilder failedr = new StringBuilder() ;
			//PPIMsgRespR respr = PPIMsgRespR.parseFromBS(retbs, failedr) ;
			//if(respr==null)
			//	return false;
		}
		//int c = PPIMsg.readCharTimeout(inputs, ppiDrv.getReadTimeout()) ;
		//if(c!=0xE5 && c!=0xF9)
		//	return false;
		//write
		Thread.sleep(10);//no sleep may do error
		inputs.skip(inputs.available()) ;
		byte[] bs1 = req.toBytes();
		if(PPIMsg .log_w.isTraceEnabled())
			PPIMsg.log_w.trace("req w->"+Convert.byteArray2HexStr(bs1, " "));
		
		outputs.write(bs1);
		int c = PPIMsg.readCharTimeout(inputs, ppiDrv.getReadTimeout()) ;
		if(c!=0xE5 && c!=0xF9)
			return false;
		Thread.sleep(10);//no sleep may do error
		
		outputs.write(bs2);
		//System.out.println("reqc->"+Convert.byteArray2HexStr(bs2, " "));
		//Thread.sleep(1);
		c = PPIMsg.readCharTimeout(inputs, ppiDrv.getReadTimeout()) ;
		if(c!=0xE5 && c!=0xF9)
			return false;
		
		return true;
	}

}
