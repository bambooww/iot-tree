package org.iottree.driver.mitsubishi.fx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.ConnException;
import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.Lan;

/**
 * FX Protocal support FX PLC download RS442 Port
 * 
 * format：
 * 02 xxxx  03  checksum 
 * 
 * @author jason.zhu
 *
 */
public class FxDriver extends DevDriver
{
	
	protected List<FxDevItem> fxDevItems = null;//new ArrayList<>() ;

	long readTimeout = 3000 ;
	
	
	private long cmdInterval = 10 ;
	
	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return false;
	}

	@Override
	public DevDriver copyMe()
	{
		return new FxDriver();
	}

	@Override
	public String getName()
	{
		return "mitsubishi_fx";
	}

	@Override
	public String getTitle()
	{
		return "Mitsubishi FX";
	}

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
	
	static ArrayList<Model> models = new ArrayList<>() ;
	static
	{
		models.add(new FxModel_FX()) ;
		models.add(new FxModel_FX0()) ;
		models.add(new FxModel_FX0N()) ;
		models.add(new FxModel_FX2N()) ;
		models.add(new FxModel_FX3U()) ;
	}
	
	@Override
	public List<Model> getDevModels()
	{
		return models ;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		
		PropGroup gp = null;
		
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		gp = new PropGroup("timing",lan);//"Timing");

		gp.addPropItem(new PropItem("req_to",lan,PValTP.vt_int,false,null,null,1000)); //"Request Timeout(millisecond)",""
		gp.addPropItem(new PropItem("failed_tryn",lan,PValTP.vt_int,false,null,null,3)); //"Fail after successive times",""
		gp.addPropItem(new PropItem("recv_to",lan,PValTP.vt_int,false,null,null,200)); //"Receive response timeout(millisecond)",""
		gp.addPropItem(new PropItem("inter_req",lan,PValTP.vt_int,false,null,null,0)); //"Inter-request millisecond",""
		pgs.add(gp) ;
		
//		gp = new PropGroup("auto_demotion","Auto-Demotion");
//		gp.addPropItem(new PropItem("en","Enable auto-demotion on failure","",PValTP.vt_bool,false,new String[] {"Disabled","Enabled"},new Object[] {false,true},false));
//		gp.addPropItem(new PropItem("dm_tryc","Demote after Retry times","",PValTP.vt_int,false,null,null,3));
//		gp.addPropItem(new PropItem("dm_ms","Demote for millisecond","",PValTP.vt_int,false,null,null,10000));
//		gp.addPropItem(new PropItem("dm_no_req","Discard request during demotion","",PValTP.vt_bool,false,new String[] {"Disabled","Enabled"},new Object[] {false,true},false));
//		pgs.add(gp) ;
		
		gp = new PropGroup("block_size",lan);//"Block Sizes");
		gp.addPropItem(new PropItem("out_coils",lan,PValTP.vt_int,false,null,null,32)); //"Output Coils",""
		gp.addPropItem(new PropItem("reg",lan,PValTP.vt_int,false,null,null,32)); //"Internal Registers",""
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
		return null;
	}
	
	
	private static FxAddr FX_Addr = new FxAddr() ;
	
	private static ValTP[] LIMIT_VTPS = new ValTP[] {ValTP.vt_bool,ValTP.vt_int16,ValTP.vt_uint16,ValTP.vt_int32,ValTP.vt_uint32} ;

	@Override
	public DevAddr getSupportAddr()
	{
		return FX_Addr;
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
		
		//PPIDevItem di = new PPIDevItem(this) ;
		//ppiDevItems = Arrays.asList(di) ;
		
		List<UADev> devs = this.getBelongToCh().getDevs() ;
		
		ArrayList<FxDevItem> mdis=  new ArrayList<>() ;
		
		//create modbus cmds
		for(UADev dev:devs)
		{
			FxDevItem fdi = new FxDevItem(this,dev) ;
			StringBuilder devfr = new StringBuilder() ;
			if(!fdi.init(devfr))
				continue ;
			
			mdis.add(fdi) ;
		}
		
		this.fxDevItems = mdis;
		if(fxDevItems.size()<=0)
		{
			failedr.append("no fx cmd inited in driver") ;
			return false;
		}
		
		
		return true ;
	}
	
	boolean justOnConn = false;

	@Override
	protected void RT_onConnReady(ConnPt cp, UACh ch, UADev dev)
	{
		justOnConn = true ;
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp, UACh ch, UADev dev)
	{
		justOnConn = false;
	}

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
				boolean b_ack = FxCmd.checkDevReady(cpt.getInputStream(), cpt.getOutputStream(), 5000) ;
				if(!b_ack)
					throw new IOException("check dev not acked") ;
				return true ;
			}
			
			for(FxDevItem mdi:fxDevItems)
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
//		FxMsgReq fmBR = new FxMsgReq() ;
//		//fmBR.asAddrTp('X').asStationCode(5).asPCCode(0xff);
//		fmBR.asStartAddr(0xA0).asByteNum(32);
//		
//		
//		
//		int oldn = inputs.available() ;
//		if(oldn>0)
//			inputs.skip(oldn) ;
//		
//		outputs.write(fmBR.toBytes());
//		outputs.flush();
//		
//		FxMsgResp resp = new FxMsgResp(32) ;
//		//resp.asStationCode(5).asPCCode(0xff);
//		resp.readFromStream(inputs, 3000) ;
//		if(!resp.readOk)
//			throw new IOException("read failed,"+resp.errInf) ;
//		
//		byte[] bs = resp.byteBuf ;
//		System.out.println(Convert.byteArray2HexStr(bs, " ")) ;		
//		return true;
	}
	
	private FxDevItem getDevItem(UADev dev)
	{
		for(FxDevItem mdi:fxDevItems)
		{
			if(mdi.getUADev().equals(dev))
					return mdi ;
		}
		return null ;
	}

	@Override
	public boolean RT_writeVal(UACh ch,UADev dev,UATag tag, DevAddr da, Object v)
	{
		FxDevItem mdi = getDevItem(dev) ;
		if(mdi==null)
			return false;
		return mdi.RT_writeVal(da, v) ;
	}

	@Override
	public boolean RT_writeVals(UACh ch,UADev dev,UATag[] tags, DevAddr[] da, Object[] v)
	{
		throw new RuntimeException("no impl") ;
	}

}
