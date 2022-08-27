package org.iottree.driver.s7.ppi;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iottree.core.ConnException;
import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.ValChker;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.common.ModbusAddr;
import org.iottree.driver.common.ModbusDevItem;

/**
 * s7-200 ppi driver
 * 
 * @author jason.zhu
 */
public class PPIDriver extends DevDriver
{
	private static ILogger log = LoggerManager.getLogger(PPIDriver.class) ;
	
	protected List<PPIDevItem> ppiDevItems = null;//new ArrayList<>() ;
	
	private short masterId = 0 ;
	
	private long readTimeout = 3000;
	
	private long cmdInterval = 10 ;
	
	@Override
	public String getName()
	{
		return "s7_200_ppi";
	}
	
	

	@Override
	public String getTitle()
	{
		return "Siemens S7-200";
	}
	
	public short getMasterID()
	{
		return this.masterId ;
	}
	
	public long getReadTimeout()
	{
		return this.readTimeout ;
	}
	
	public long getCmdInterval()
	{
		return this.cmdInterval ;
	}
	
	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return false;
	}

	@Override
	public DevDriver copyMe()
	{
		return new PPIDriver();
	}


	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtStream.class ;
	}

	@Override
	public boolean supportDevFinder()
	{
		return false;
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
		
		gp = new PropGroup("block_size","Block Sizes");
		gp.addPropItem(new PropItem("out_coils","Output Coils","",PValTP.vt_int,false,null,null,32));
		gp.addPropItem(new PropItem("in_coils","Input Coils","",PValTP.vt_int,false,null,null,32));
		gp.addPropItem(new PropItem("internal_reg","Internal Registers","",PValTP.vt_int,false,null,null,32));
		gp.addPropItem(new PropItem("holding","Holding","",PValTP.vt_int,false,null,null,32));
		pgs.add(gp) ;
		
		return pgs;
	}

	@Override
	public List<PropGroup> getPropGroupsForCh(UACh ch)
	{
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		PropGroup gp = new PropGroup("ppi_spk","PPI Device");
		
		PropItem pi = new PropItem("dev_addr","Device Address","",PValTP.vt_int,false,null,null,1) ;
		pi.setValChker(new ValChker<Number>() {

			@Override
			public boolean checkVal(Number v, StringBuilder failedr)
			{
				int vi = v.intValue() ;
				if(vi>=1&&vi<=255)
					return true ;
				failedr.append("PPI device address must between 1-255") ;
				return false;
			}});
		gp.addPropItem(pi);
		pgs.add(gp) ;

		return pgs ;
	}
	
	private static PPIAddr ppiAddr = new PPIAddr() ;

	@Override
	public DevAddr getSupportAddr()
	{
		return ppiAddr;
	}
	
	protected  boolean initDriver(StringBuilder failedr) throws Exception
	{
		super.initDriver(failedr) ;
		
		//PPIDevItem di = new PPIDevItem(this) ;
		//ppiDevItems = Arrays.asList(di) ;
		
		List<UADev> devs = this.getBelongToCh().getDevs() ;
		
		ArrayList<PPIDevItem> mdis=  new ArrayList<>() ;
		
		//create modbus cmds
		for(UADev dev:devs)
		{
			PPIDevItem mdi = new PPIDevItem(this,dev) ;
			StringBuilder devfr = new StringBuilder() ;
			if(!mdi.init(devfr))
				continue ;
			
			mdis.add(mdi) ;
		}
		
		this.ppiDevItems = mdis;
		if(ppiDevItems.size()<=0)
		{
			failedr.append("no ppi cmd inited in driver") ;
			return false;
		}
		
		
		return true ;
	}

	@Override
	protected void RT_onConnReady(ConnPt cp,UACh ch,UADev dev)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp,UACh ch,UADev dev)
	{
		
	}

	@Override
	protected boolean RT_runInLoop(UACh ch,UADev dev,StringBuilder failedr) throws Exception
	{
		if(ppiDevItems==null)
			return true;
		
		ConnPtStream cpt = (ConnPtStream)this.getBindedConnPt() ;
		if(cpt==null)
			return true ;
		//if(true)
		//	throw new Exception("test") ;
		if(!cpt.isConnReady())
			return true ;// do nothing

		try
		{
			for(PPIDevItem mdi:ppiDevItems)
			{
				mdi.doCmd(cpt);
			}
		}
		catch(ConnException se)
		{
			//System.out.println("errdt==="+Convert.toFullYMDHMS(new Date()));
			//se.printStackTrace();
			if(log.isDebugEnabled())
				log.debug("RT_runInLoop err", se);
			cpt.close();
			
			
			
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			if(log.isErrorEnabled())
				log.debug("RT_runInLoop err", e);
		}
		return true;
	}
	
	private PPIDevItem getDevItem(UADev dev)
	{
		for(PPIDevItem mdi:ppiDevItems)
		{
			if(mdi.getUADev().equals(dev))
					return mdi ;
		}
		return null ;
	}

	@Override
	public boolean RT_writeVal(UADev dev,DevAddr da,Object v)
	{
		PPIDevItem mdi = getDevItem(dev) ;
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
