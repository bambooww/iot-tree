package org.iottree.driver.omron.fins;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.ConnException;
import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.DevDriver.Model;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.Lan;

public abstract class FinsDriver extends DevDriver
{
	long readTimeout = 3000 ;
	
	
	private long cmdInterval = 10 ;
	
	protected List<FinsDevItem> devItems = null;//new ArrayList<>() ;


	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtStream.class;
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
		
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		gp = new PropGroup("timing",lan);//"Timing");

		gp.addPropItem(new PropItem("req_to",lan,PValTP.vt_int,false,null,null,2000)); //"Request Timeout(millisecond)",""
		gp.addPropItem(new PropItem("failed_tryn",lan,PValTP.vt_int,false,null,null,3)); //"Fail after successive times",""
		//gp.addPropItem(new PropItem("recv_to",lan,PValTP.vt_int,false,null,null,1500)); //"Receive response timeout(millisecond)",""
		gp.addPropItem(new PropItem("inter_req",lan,PValTP.vt_int,false,null,null,10)); //"Inter-request millisecond",""
		pgs.add(gp) ;
		
		gp = new PropGroup("block_size",lan);//"Block Sizes");
		gp.addPropItem(new PropItem("bit",lan,PValTP.vt_int,false,null,null,512)); //"Output Coils",""
		gp.addPropItem(new PropItem("word",lan,PValTP.vt_int,false,null,null,256)); //"Internal Registers",""
		pgs.add(gp) ;
		
		return pgs;
	}
	
	public static final String PG_FINS_NET = "fins_net" ;
	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		PropGroup gp = new PropGroup(PG_FINS_NET,lan);//"Modbus Device");
		
		PropItem pi = new PropItem("sna",lan,PValTP.vt_int,false,null,null,0) ; //
		gp.addPropItem(pi);
		pi = new PropItem("sa1",lan,PValTP.vt_int,false,null,null,1) ; // PC IP4 last value 
		gp.addPropItem(pi);
		
		pi = new PropItem("dev_id_ip",lan,PValTP.vt_str,false,null,null,"") ; //
		gp.addPropItem(pi);
		
		pi = new PropItem("dna",lan,PValTP.vt_int,false,null,null,0) ; //
		gp.addPropItem(pi);
		
		pi = new PropItem("da1",lan,PValTP.vt_int,false,null,null,0) ; //
		gp.addPropItem(pi);
		pi = new PropItem("da2",lan,PValTP.vt_int,false,null,null,0) ; //
		gp.addPropItem(pi);
		
//		pi.setValChker(new ValChker<Number>() {
//
//			@Override
//			public boolean checkVal(Number v, StringBuilder failedr)
//			{
//				int vi = v.intValue() ;
//				if(vi>=1&&vi<=255)
//					return true ;
//				failedr.append("modbus device address must between 1-255") ;
//				return false;
//			}});
		
		
		pgs.add(gp) ;

		return pgs ;
	}

	@Override
	public List<PropGroup> getPropGroupsForCh(UACh ch)
	{
		return null;
	}


	@Override
	public List<Model> getDevModels()
	{
		return FinsModel.getModelsAll() ;
	}

	private static FinsAddr FINS_Addr = new FinsAddr() ;
	
	private static ValTP[] LIMIT_VTPS = new ValTP[] {ValTP.vt_bool,ValTP.vt_int16,ValTP.vt_uint16,ValTP.vt_int32,ValTP.vt_uint32} ;

	@Override
	public DevAddr getSupportAddr()
	{
		return FINS_Addr;
	}
	
	@Override
	public ValTP[] getLimitValTPs(UADev dev)
	{
		return LIMIT_VTPS ;
	}


	public long getReadTimeout()
	{
		return this.readTimeout ;
	}
	
	public long getCmdInterval()
	{
		return this.cmdInterval ;
	}
	

	protected  boolean initDriver(StringBuilder failedr) throws Exception
	{
		super.initDriver(failedr) ;

		List<UADev> devs = this.getBelongToCh().getDevs() ;
		
		ArrayList<FinsDevItem> mdis=  new ArrayList<>() ;
		
		//create modbus cmds
		for(UADev dev:devs)
		{
			FinsDevItem fdi = new FinsDevItem(this,dev) ;
			StringBuilder devfr = new StringBuilder() ;
			if(!fdi.init(devfr))
				continue ;
			
			mdis.add(fdi) ;
		}
		
		this.devItems = mdis;
		if(devItems.size()<=0)
		{
			failedr.append("no fx cmd inited in driver") ;
			return false;
		}
		
		
		return true ;
	}
	
	protected abstract boolean onJustOnConn(ConnPtStream cp,UACh ch)  throws Exception;

	@Override
	protected boolean RT_runInLoop(UACh ch, UADev dev, StringBuilder failedr) throws Exception
	{
		ConnPtStream cpt = (ConnPtStream)this.getBindedConnPt() ;
		if(cpt==null)
			return true ;
		
		if(!cpt.isConnReady())
			return true ;// do nothing
		
		//OutputStream outputs = cpt.getOutputStream() ;
		//InputStream inputs = cpt.getInputStream() ;
		
		
		try
		{
			if(justOnConn)
			{
				justOnConn = false;
				onJustOnConn(cpt, ch) ; 
				return true ;
			}
			
			for(FinsDevItem mdi:devItems)
			{
				StringBuilder fsb = new StringBuilder() ;
				if(!mdi.doCmd(cpt,fsb))
				{
					String warn = mdi.getUADev().getName()+" run err: "+fsb.toString();
					this.RT_fireDrvWarn(warn);
					if(log.isWarnEnabled())
						log.warn(warn);
				}
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
			e.printStackTrace();
			if(log.isErrorEnabled())
				log.error("RT_runInLoop err", e);
		}
		return true;
	}

	private FinsDevItem getDevItem(UADev dev)
	{
		for(FinsDevItem mdi:devItems)
		{
			if(mdi.getUADev().equals(dev))
					return mdi ;
		}
		return null ;
	}
	

	boolean justOnConn = false;

	@Override
	protected void RT_onConnReady(ConnPt cp, UACh ch, UADev dev) throws Exception
	{
		justOnConn = true ;
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp, UACh ch, UADev dev) throws Exception
	{
		justOnConn = false ;
	}


	
	@Override
	public boolean RT_writeVal(UACh ch, UADev dev, UATag tag, DevAddr da, Object v)
	{
		FinsDevItem mdi = getDevItem(dev) ;
		if(mdi==null)
			return false;
		return mdi.RT_writeVal(da, v) ;
	}

	@Override
	public boolean RT_writeVals(UACh ch, UADev dev, UATag[] tags, DevAddr[] da, Object[] v)
	{
		return false;
	}
}
