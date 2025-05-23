package org.iottree.driver.common;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.iottree.core.ConnException;
import org.iottree.core.ConnManager;
import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.basic.IConnEndPoint;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.basic.ValChker;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.NetUtil;
import org.iottree.core.util.NetUtil.Adapter;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.common.modbus.ModbusCmd;
import org.iottree.driver.common.modbus.sniffer.SnifferRTUCh;


public class ModbusDrvRTU extends DevDriver
{
	private static ILogger log = LoggerManager.getLogger(ModbusDrvRTU.class) ;
	
	private boolean bSniffer = false;
	
	private SnifferRTUCh snifferCh = null ;
	
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
	
	private static final int SNIFFER_MODEL = 1 ;
	
	@Override
	public List<PropGroup> getPropGroupsForCh(UACh ch)
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		PropGroup gp = new PropGroup("modbus_ch",lan);//"Modbus in Channel");
		
		PropItem pi = new PropItem("run_model",lan,PValTP.vt_int,false,
				new String[] {"Normal","Sniffer"},new Object[] {0,SNIFFER_MODEL},0) ; //"Modbus Driver Run Model",""
		
		gp.addPropItem(pi);
		pgs.add(gp) ;
		return pgs ;
	}


	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		PropGroup gp = new PropGroup("modbus_spk",lan);//"Modbus Device");
		
		PropItem pi = new PropItem("mdev_addr",lan,PValTP.vt_int,false,null,null,1) ; //"Modbus Device Address",""
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
		
//		gp = new PropGroup("timing","Timing");
//		//gp.addPropItem(new PropItem("conn_to","Connect Timeout(second)","",PValTP.vt_int,false,null,null,3));
//		//
//		gp.addPropItem(new PropItem("req_to","Request Timeout(millisecond)","",PValTP.vt_int,false,null,null,1000));
//		gp.addPropItem(new PropItem("failed_tryn","Fail after successive times","",PValTP.vt_int,false,null,null,3));
//		gp.addPropItem(new PropItem("recv_to","Receive response timeout(millisecond)","",PValTP.vt_int,false,null,null,200));
//		gp.addPropItem(new PropItem("inter_req","Inter-request millisecond","",PValTP.vt_int,false,null,null,0));
//		pgs.add(gp) ;
		
		return pgs ;
	}
	
	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		
		PropGroup gp = null;
		
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		
		gp = new PropGroup("timing",lan);//"Timing");
		

		//gp.addPropItem(new PropItem("conn_to","Connect Timeout(second)","",PValTP.vt_int,false,null,null,3));
		//
		gp.addPropItem(new PropItem("scan_intv",lan,PValTP.vt_int,false,null,null,ModbusCmd.SCAN_INTERVER_DEFAULT)); //"Scan Interval(millisecond)",""
		gp.addPropItem(new PropItem("req_to",lan,PValTP.vt_int,false,null,null,1000)); //,"Request Timeout(millisecond)",""
		gp.addPropItem(new PropItem("failed_tryn",lan,PValTP.vt_int,false,null,null,3)); //"Fail after successive times",""
		gp.addPropItem(new PropItem("recv_to",lan,PValTP.vt_int,false,null,null,200)); //"Receive response timeout(millisecond)",""
		gp.addPropItem(new PropItem("inter_req",lan,PValTP.vt_int,false,null,null,0)); //"Inter-request millisecond",""
		
		pgs.add(gp) ;
		
		gp = new PropGroup("auto_demotion",lan);//"Auto-Demotion");
		
		gp.addPropItem(new PropItem("en",lan,PValTP.vt_bool,false,new String[] {"Disabled","Enabled"},new Object[] {false,true},false)); //"Enable auto-demotion on failure",""
		gp.addPropItem(new PropItem("dm_tryc",lan,PValTP.vt_int,false,null,null,3)); //"Demote after Retry times",""
		gp.addPropItem(new PropItem("dm_ms",lan,PValTP.vt_int,false,null,null,10000)); //"Demote for millisecond",""
		gp.addPropItem(new PropItem("dm_no_req",lan,PValTP.vt_bool,false,new String[] {"Disabled","Enabled"},new Object[] {false,true},false)); //"Discard request during demotion",""
//		gp.addPropItem(new PropItem("en","Enable auto-demotion on failure","",PValTP.vt_bool,false,new String[] {"Disabled","Enabled"},new Object[] {false,true},false));
//		gp.addPropItem(new PropItem("dm_tryc","Demote after Retry times","",PValTP.vt_int,false,null,null,3));
//		gp.addPropItem(new PropItem("dm_ms","Demote for millisecond","",PValTP.vt_int,false,null,null,10000));
//		gp.addPropItem(new PropItem("dm_no_req","Discard request during demotion","",PValTP.vt_bool,false,new String[] {"Disabled","Enabled"},new Object[] {false,true},false));
		pgs.add(gp) ;
		
		gp = new PropGroup("data_access",lan);//"Data Access");
		gp.addPropItem(new PropItem("z_b_addr",lan,PValTP.vt_bool,false,null,null,true)); //"Use Zero based addressing",""
		gp.addPropItem(new PropItem("z_b_bit_in_reg",lan,PValTP.vt_bool,false,null,null,true)); //"Use Zero based bit in register"
		gp.addPropItem(new PropItem("h_reg_b_mask_w",lan,PValTP.vt_bool,false,null,null,false)); //"Use holding register bit mask write",""
		gp.addPropItem(new PropItem("f06_reg1_w",lan,PValTP.vt_bool,false,null,null,true)); //"Modbus 06 for single register writes",""
		gp.addPropItem(new PropItem("f05_coil1_w",lan,PValTP.vt_bool,false,null,null,true)); //"Modbus 05 for single coil writes",""
		
//		gp.addPropItem(new PropItem("z_b_addr","Use Zero based addressing","",PValTP.vt_bool,false,null,null,true));
//		gp.addPropItem(new PropItem("z_b_bit_in_reg","Use Zero based bit in register","",PValTP.vt_bool,false,null,null,true));
//		gp.addPropItem(new PropItem("h_reg_b_mask_w","Use holding register bit mask write","",PValTP.vt_bool,false,null,null,false));
//		gp.addPropItem(new PropItem("f06_reg1_w","Modbus 06 for single register writes","",PValTP.vt_bool,false,null,null,true));
//		gp.addPropItem(new PropItem("f05_coil1_w","Modbus 05 for single coil writes","",PValTP.vt_bool,false,null,null,true));
		pgs.add(gp) ;
		
		gp = new PropGroup("data_encod",lan);//"Data Encoding");
		gp.addPropItem(new PropItem("byte_ord_def",lan,PValTP.vt_bool,false,null,null,true)); //"Use default Modbus byte order",""
		gp.addPropItem(new PropItem("fw_low32",lan,PValTP.vt_bool,false,null,null,true)); //"First word low in 32bit data types",""
		gp.addPropItem(new PropItem("fdw_low64",lan,PValTP.vt_bool,false,null,null,false)); //"First Dword low in 64bit data types",""
		gp.addPropItem(new PropItem("modicon_ord",lan,PValTP.vt_bool,false,null,null,false)); //"Modicon bit ordering(bit 0 in MSB)",""

//		gp.addPropItem(new PropItem("byte_ord_def","Use default Modbus byte order","",PValTP.vt_bool,false,null,null,true));
//		gp.addPropItem(new PropItem("fw_low32","First word low in 32bit data types","",PValTP.vt_bool,false,null,null,true));
//		gp.addPropItem(new PropItem("fdw_low64","First Dword low in 64bit data types","",PValTP.vt_bool,false,null,null,false));
//		gp.addPropItem(new PropItem("modicon_ord","Modicon bit ordering(bit 0 in MSB)","",PValTP.vt_bool,false,null,null,false));
		pgs.add(gp) ;
		
		gp = new PropGroup("block_size",lan);//"Block Sizes");
		gp.addPropItem(new PropItem("out_coils",lan,PValTP.vt_int,false,null,null,32)); //"Output Coils",""
		gp.addPropItem(new PropItem("in_coils",lan,PValTP.vt_int,false,null,null,32)); //"Input Coils",""
		gp.addPropItem(new PropItem("internal_reg",lan,PValTP.vt_int,false,null,null,32)); //"Internal Registers",""
		gp.addPropItem(new PropItem("holding",lan,PValTP.vt_int,false,null,null,32)); //"Holding",""
		
//		gp.addPropItem(new PropItem("out_coils","Output Coils","",PValTP.vt_int,false,null,null,32));
//		gp.addPropItem(new PropItem("in_coils","Input Coils","",PValTP.vt_int,false,null,null,32));
//		gp.addPropItem(new PropItem("internal_reg","Internal Registers","",PValTP.vt_int,false,null,null,32));
//		gp.addPropItem(new PropItem("holding","Holding","",PValTP.vt_int,false,null,null,32));
		pgs.add(gp) ;
		
		gp = new PropGroup("framing",lan);//"Framing");
		gp.addPropItem(new PropItem("m_tcp_f",lan,PValTP.vt_bool,false,null,null,false)); //"Use Modbus Tcp framing",""
		gp.addPropItem(new PropItem("leading_bs",lan,PValTP.vt_int,false,null,null,0)); //"Leading bytes",""
		gp.addPropItem(new PropItem("trailing_bs",lan,PValTP.vt_int,false,null,null,0)); //"Trailing bytes",""
//		gp.addPropItem(new PropItem("m_tcp_f","Use Modbus Tcp framing","",PValTP.vt_bool,false,null,null,false));
//		gp.addPropItem(new PropItem("leading_bs","Leading bytes","",PValTP.vt_int,false,null,null,0));
//		gp.addPropItem(new PropItem("trailing_bs","Trailing bytes","",PValTP.vt_int,false,null,null,0));
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
	
	protected ArrayList<ModbusDevItem> modbusDevItems = new ArrayList<>() ;
	
	@Override
	public DevAddr getSupportAddr()
	{
		return msAddr ;
	}
	
	protected  boolean initDriver(StringBuilder failedr) throws Exception
	{
		Object pv = this.getBelongToCh().getPropValue("modbus_ch", "run_model") ;
		if(pv!=null&&pv instanceof Number)
		{
			bSniffer = ((Number)pv).intValue() == SNIFFER_MODEL;
		}
		
		List<UADev> devs = this.getBelongToCh().getDevs() ;
		
		ArrayList<ModbusDevItem> mdis=  new ArrayList<>() ;
		
		//create modbus cmds
		for(UADev dev:devs)
		{
//			if(dev.getName().equals("enc600"))
//			{
//				System.out.println("enc600") ;
//			}
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
		
		if(bSniffer)
		{
			snifferCh = new SnifferRTUCh();
		}
		return true ;
	}
	


	@Override
	protected void RT_onConnReady(ConnPt cp,UACh ch,UADev dev)
	{
		
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp,UACh ch,UADev dev)
	{
		
	}
	
	protected long getRunInterval()
	{
		if(bSniffer)
			return 10 ;
		else
			return super.getRunInterval();
	}

	@Override
	protected boolean RT_runInLoop(UACh ch,UADev dev,StringBuilder failedr) throws Exception
	{
		ConnPtStream cpt = (ConnPtStream)this.getBindedConnPt() ;
		if(cpt==null)
			return true ;
		//if(true)
		//	throw new Exception("test") ;
		if(!cpt.isConnReady())
			return true ;// do nothing
		
		//if(log.isDebugEnabled())
		//	log.debug("RT_runInLoop conn ready ,run do modbus");
		try
		{
			if(bSniffer)
			{//
				//System.out.println(" RT_runInLoop in sniffer") ;
				InputStream inputs = cpt.getInputStream();
				int dlen = inputs.available() ;
				if(dlen<=0)
					return true;
				byte[] bs = new byte[dlen] ;
				inputs.read(bs) ;
				snifferCh.onSniffedData(bs,(sc)->{
					for(ModbusDevItem mdi:modbusDevItems)
					{
						mdi.onSnifferCmd(sc);
					}
				});
			}
			else
			{
				for(ModbusDevItem mdi:modbusDevItems)
				{
					mdi.doModbusCmd(cpt);
				}
				checkConnBroken(cpt) ;
			}
		}
		catch(ConnException se)
		{
			//System.out.println("errdt==="+Convert.toFullYMDHMS(new Date()));
//			if(log.isDebugEnabled())
//				log.debug("RT_runInLoop err", se);
			if(log.isWarnEnabled())
			{
				log.warn("RT_runInLoop err with cpt close", se);
			}
			cpt.close();
			//se.printStackTrace();
			for(ModbusDevItem mdi:modbusDevItems)
			{
				mdi.doModbusCmdErr();
			}
		}
		catch(Exception e)
		{
			if(log.isWarnEnabled())
			{
				log.warn("RT_runInLoop err not cpt close", e);
			}
			//e.printStackTrace();
			//if(log.isErrorEnabled())
			//	log.debug("RT_runInLoop err", e);
		}
		return true;
	}
	
	
	/**
	 * linux 下拔网线可能需要很长时间才知道，因此使用这个读取失败时间作为连接断开依据
	 * @param cpt
	 * @throws Exception
	 */
	private void checkConnBroken(ConnPtStream cpt) throws Exception
	{
		long lastreadok = -1 ;
		for(ModbusDevItem mdi:modbusDevItems)
		{
			long tmpdt = mdi.getLastReadOkDT() ;
			if(tmpdt>0 && tmpdt>lastreadok)
				lastreadok = tmpdt ;
		}
		
		if(lastreadok>0)
		{// linux 下拔网线可能需要很长时间才知道，因此使用这个读取失败时间作为连接断开依据
			ConnPtStream cpts = (ConnPtStream)this.getBelongToCh().getConnPt() ;
			long read_no_to = cpts.getReadNoDataTimeout() ;
			if(read_no_to>0 && System.currentTimeMillis()-lastreadok>read_no_to)
			{
				if(log.isDebugEnabled())
					log.debug("ModbusDrvRT last read ok timeout with "+read_no_to+",connpt ["+cpts.getTitle()+"] will be closed");
				cpt.close();
			}
		}
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
	public boolean RT_writeVal(UACh ch,UADev dev,UATag tag,DevAddr da,Object v)
	{
		ModbusDevItem mdi = getDevItem(dev) ;
		if(mdi==null)
			return false;
		return mdi.RT_writeVal(da, v) ;
	}
	
	@Override
	public boolean RT_writeVals(UACh ch,UADev dev,UATag[] tags,DevAddr[] da,Object[] v)
	{
		throw new RuntimeException("no impl") ;
	}
}
