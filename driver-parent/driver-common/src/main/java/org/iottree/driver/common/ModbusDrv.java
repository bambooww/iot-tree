package org.iottree.driver.common;

import java.io.*;
import java.util.*;

import org.iottree.core.*;
import org.iottree.core.basic.IConnEndPoint;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.conn.ConnPtStream;


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
		
		gp = new PropGroup("timing","Timing");
		gp.addPropItem(new PropItem("conn_to","Connect Timeout(second)","",PValTP.vt_int,false,null,null,3));
		gp.addPropItem(new PropItem("conn_tryc","Fail Retry times","",PValTP.vt_int,false,null,null,3));
		gp.addPropItem(new PropItem("req_to","Request Timeout(millisecond)","",PValTP.vt_int,false,null,null,1000));
		gp.addPropItem(new PropItem("inter_req","Inter-request millisecond","",PValTP.vt_int,false,null,null,0));
		pgs.add(gp) ;
		
		gp = new PropGroup("auto_demotion","Auto-Demotion");
		gp.addPropItem(new PropItem("en","Enable auto-demotion on failure","",PValTP.vt_bool,false,new String[] {"Disabled","Enabled"},new Object[] {false,true},false));
		gp.addPropItem(new PropItem("dm_tryc","Demote after Retry times","",PValTP.vt_int,false,null,null,3));
		gp.addPropItem(new PropItem("dm_ms","Demote for millisecond","",PValTP.vt_int,false,null,null,10000));
		gp.addPropItem(new PropItem("dm_no_req","Discard request during demotion","",PValTP.vt_bool,false,new String[] {"Disabled","Enabled"},new Object[] {false,true},false));
		pgs.add(gp) ;
		
		gp = new PropGroup("data_access","Data Access");
		gp.addPropItem(new PropItem("z_b_addr","Use Zero based addressing","",PValTP.vt_bool,false,null,null,true));
		gp.addPropItem(new PropItem("z_b_bit_in_reg","Use Zero based bit in register","",PValTP.vt_bool,false,null,null,true));
		gp.addPropItem(new PropItem("h_reg_b_mask_w","Use holding register bit mask write","",PValTP.vt_bool,false,null,null,false));
		gp.addPropItem(new PropItem("f06_reg1_w","Modbus 06 for single register writes","",PValTP.vt_bool,false,null,null,true));
		gp.addPropItem(new PropItem("f05_coil1_w","Modbus 05 for single coil writes","",PValTP.vt_bool,false,null,null,true));
		pgs.add(gp) ;
		
		gp = new PropGroup("data_encod","Data Encoding");
		gp.addPropItem(new PropItem("byte_ord_def","Use default Modbus byte order","",PValTP.vt_bool,false,null,null,true));
		gp.addPropItem(new PropItem("fw_low32","First word low in 32bit data types","",PValTP.vt_bool,false,null,null,true));
		gp.addPropItem(new PropItem("fdw_low64","First Dword low in 64bit data types","",PValTP.vt_bool,false,null,null,false));
		gp.addPropItem(new PropItem("modicon_ord","Modicon bit ordering(bit 0 in MSB)","",PValTP.vt_bool,false,null,null,false));
		pgs.add(gp) ;
		
		gp = new PropGroup("block_size","Block Sizes");
		gp.addPropItem(new PropItem("out_coils","Output Coils","",PValTP.vt_int,false,null,null,32));
		gp.addPropItem(new PropItem("in_coils","Input Coils","",PValTP.vt_int,false,null,null,32));
		gp.addPropItem(new PropItem("internal_reg","Internal Registers","",PValTP.vt_int,false,null,null,32));
		gp.addPropItem(new PropItem("holding","Holding","",PValTP.vt_int,false,null,null,32));
		pgs.add(gp) ;
		
		gp = new PropGroup("framing","Framing");
		gp.addPropItem(new PropItem("m_tcp_f","Use Modbus Tcp framing","",PValTP.vt_bool,false,null,null,false));
		gp.addPropItem(new PropItem("leading_bs","Leading bytes","",PValTP.vt_int,false,null,null,0));
		gp.addPropItem(new PropItem("trailing_bs","Trailing bytes","",PValTP.vt_int,false,null,null,0));
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
