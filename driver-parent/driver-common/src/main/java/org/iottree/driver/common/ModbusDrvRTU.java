package org.iottree.driver.common;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.ConnManager;
import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.basic.IConnEndPoint;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.basic.ValChker;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.NetUtil;
import org.iottree.core.util.NetUtil.Adapter;


public class ModbusDrvRTU extends DevDriver
{
	@Override
	public String getName()
	{
		return "modbus_rtu";
	}

	@Override
	public String getTitle()
	{
		return "Modbus RTU";
	}
	
	public DevDriver copyMe()
	{
		return new ModbusDrvRTU() ;
	}
	
	@Override
	public List<PropGroup> getPropGroupsForCh()
	{
		return null ;
	}


	@Override
	public List<PropGroup> getPropGroupsForDevInCh()
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		PropGroup gp = new PropGroup("modbus_spk","Modbus Device");
		
		PropItem pi = new PropItem("mdev_addr","Modbus Device Address","",PValTP.vt_int,false,null,null,1) ;
		pi.setValChker(new ValChker<Number>() {

			@Override
			public boolean checkVal(Number v, StringBuilder failedr)
			{
				int vi = v.intValue() ;
				if(vi>=1&&vi<=255)
					return true ;
				failedr.append("modbus device address must between 1-255") ;
				return false;
			}});
		gp.addPropItem(pi);
		pgs.add(gp) ;
		return pgs ;
	}
	
	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		
		PropGroup gp = null;
		
		gp = new PropGroup("timing","Timing");
		//gp.addPropItem(new PropItem("conn_to","Connect Timeout(second)","",PValTP.vt_int,false,null,null,3));
		//
		gp.addPropItem(new PropItem("req_to","Request Timeout(millisecond)","",PValTP.vt_int,false,null,null,1000));
		gp.addPropItem(new PropItem("failed_tryn","Fail after successive times","",PValTP.vt_int,false,null,null,3));
		gp.addPropItem(new PropItem("recv_to","Receive response timeout(millisecond)","",PValTP.vt_int,false,null,null,200));
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

	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return false;
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtStream.class ;
	}

	@Override
	public boolean supportDevFinder()
	{//not support
		return false;
	}
	

	private static ModbusAddr msAddr = new ModbusAddr() ;
	
	private ArrayList<ModbusDevItem> modbusDevItems = new ArrayList<>() ;
	
	@Override
	public DevAddr getSupportAddr()
	{
		return msAddr ;
	}
	
	protected  boolean initDriver(StringBuilder failedr) throws Exception
	{
		List<UADev> devs = this.getBelongToCh().getDevs() ;
		
		ArrayList<ModbusDevItem> mdis=  new ArrayList<>() ;
		
		//create modbus cmds
		for(UADev dev:devs)
		{
			ModbusDevItem mdi = new ModbusDevItem(dev) ;
			StringBuilder devfr = new StringBuilder() ;
			if(!mdi.init(devfr))
				continue ;
			
			mdis.add(mdi) ;
		}
		
		modbusDevItems = mdis;
		if(modbusDevItems.size()<=0)
		{
			failedr.append("no modbus cmd inited in driver") ;
			return false;
		}
		return true ;
	}
	


	@Override
	protected void RT_onConnReady(ConnPt cp)
	{
		
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp)
	{
		
	}

	@Override
	protected boolean RT_runInLoop(StringBuilder failedr) throws Exception
	{
		ConnPtStream cpt = (ConnPtStream)this.getBindedConnPt() ;
		//if(true)
		//	throw new Exception("test") ;
		if(!cpt.isConnReady())
			return true ;// do nothing
		
		//try
		{
			for(ModbusDevItem mdi:modbusDevItems)
			{
				mdi.doModbusCmd(cpt);
			}
		}
//		catch(SocketException se)
//		{
//			
//		}
		return true;
	}
	
	private ModbusDevItem getDevItem(UADev dev)
	{
		for(ModbusDevItem mdi:modbusDevItems)
		{
			if(mdi.getUADev().equals(dev))
					return mdi ;
		}
		return null ;
	}

	@Override
	public boolean RT_writeVal(UADev dev,DevAddr da,Object v)
	{
		ModbusDevItem mdi = getDevItem(dev) ;
		if(mdi==null)
			return false;
		return mdi.RT_writeVal(da, v) ;
	}
	
	@Override
	public boolean RT_writeVals(UADev dev,DevAddr[] da,Object[] v)
	{
		throw new RuntimeException("no impl") ;
	}
}
