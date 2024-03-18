package org.iottree.driver.common;

import java.io.*;
import java.util.*;

import org.iottree.core.*;
import org.iottree.core.basic.IConnEndPoint;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.Lan;


public abstract class ModbusDrv extends DevDriver
{
	/**
	 * use ethernet encapsulation or not
	 */
	//boolean bEth = false;
	
	public ModbusDrv()
	{}
	
	
	
	/**
	 * driver implements support ConnPt
	 * @return
	 */
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtStream.class ;
	}

	
	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		
		PropGroup gp = null;
		
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		
		
		gp = new PropGroup("timing",lan);//"Timing");
		//gp.addPropItem(new PropItem("conn_to","Connect Timeout(second)","",PValTP.vt_int,false,null,null,3));
		gp.addPropItem(new PropItem("conn_tryc",lan,PValTP.vt_int,false,null,null,3)); //"Fail Retry times",""
		gp.addPropItem(new PropItem("req_to",lan,PValTP.vt_int,false,null,null,1000)); //"Request Timeout(millisecond)",""
		gp.addPropItem(new PropItem("inter_req",lan,PValTP.vt_int,false,null,null,100)); //"Inter-request millisecond",""
		pgs.add(gp) ;
		
		gp = new PropGroup("auto_demotion",lan);//"Auto-Demotion");
		gp.addPropItem(new PropItem("en",lan,PValTP.vt_bool,false,new String[] {"Disabled","Enabled"},new Object[] {false,true},false)); //"Enable auto-demotion on failure",""
		gp.addPropItem(new PropItem("dm_tryc",lan,PValTP.vt_int,false,null,null,3)); //"Demote after Retry times",""
		gp.addPropItem(new PropItem("dm_ms",lan,PValTP.vt_int,false,null,null,10000)); //"Demote for millisecond",""
		gp.addPropItem(new PropItem("dm_no_req",lan,PValTP.vt_bool,false,new String[] {"Disabled","Enabled"},new Object[] {false,true},false)); //"Discard request during demotion",""
		pgs.add(gp) ;
		
		gp = new PropGroup("data_access",lan);//"Data Access");
		gp.addPropItem(new PropItem("z_b_addr",lan,PValTP.vt_bool,false,null,null,true)); //"Use Zero based addressing",""
		gp.addPropItem(new PropItem("z_b_bit_in_reg",lan,PValTP.vt_bool,false,null,null,true)); //"Use Zero based bit in register"
		gp.addPropItem(new PropItem("h_reg_b_mask_w",lan,PValTP.vt_bool,false,null,null,false)); //"Use holding register bit mask write",""
		gp.addPropItem(new PropItem("f06_reg1_w",lan,PValTP.vt_bool,false,null,null,true)); //"Modbus 06 for single register writes",""
		gp.addPropItem(new PropItem("f05_coil1_w",lan,PValTP.vt_bool,false,null,null,true)); //"Modbus 05 for single coil writes",""
		pgs.add(gp) ;
		
		gp = new PropGroup("data_encod",lan);//"Data Encoding");
		gp.addPropItem(new PropItem("byte_ord_def",lan,PValTP.vt_bool,false,null,null,true)); //"Use default Modbus byte order",""
		gp.addPropItem(new PropItem("fw_low32",lan,PValTP.vt_bool,false,null,null,true)); //"First word low in 32bit data types",""
		gp.addPropItem(new PropItem("fdw_low64",lan,PValTP.vt_bool,false,null,null,false)); //"First Dword low in 64bit data types",""
		gp.addPropItem(new PropItem("modicon_ord",lan,PValTP.vt_bool,false,null,null,false)); //"Modicon bit ordering(bit 0 in MSB)",""
		pgs.add(gp) ;
		

		
		
		gp = new PropGroup("block_size",lan);//"Block Sizes");
		gp.addPropItem(new PropItem("out_coils",lan,PValTP.vt_int,false,null,null,32)); //"Output Coils",""
		gp.addPropItem(new PropItem("in_coils",lan,PValTP.vt_int,false,null,null,32)); //"Input Coils",""
		gp.addPropItem(new PropItem("internal_reg",lan,PValTP.vt_int,false,null,null,32)); //"Internal Registers",""
		gp.addPropItem(new PropItem("holding",lan,PValTP.vt_int,false,null,null,32)); //"Holding",""
		pgs.add(gp) ;
		
		gp = new PropGroup("framing",lan);//"Framing");
		gp.addPropItem(new PropItem("m_tcp_f",lan,PValTP.vt_bool,false,null,null,false)); //"Use Modbus Tcp framing",""
		gp.addPropItem(new PropItem("leading_bs",lan,PValTP.vt_int,false,null,null,0)); //"Leading bytes",""
		gp.addPropItem(new PropItem("trailing_bs",lan,PValTP.vt_int,false,null,null,0)); //"Trailing bytes",""
		pgs.add(gp) ;
		
		return pgs;
	}

	private static ModbusAddr msAddr = new ModbusAddr() ;

	@Override
	public DevAddr getSupportAddr()
	{
		return msAddr;
	}

	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return true ;
	}
	
	
	public abstract IConnEndPoint getConnEndPoint() ;
}
