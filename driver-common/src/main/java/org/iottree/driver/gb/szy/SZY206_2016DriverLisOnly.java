package org.iottree.driver.gb.szy;

import java.util.HashMap;
import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.DevDriverMsgOnly;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.util.ILang;

public class SZY206_2016DriverLisOnly extends DevDriverMsgOnly implements ILang
{
	public static class TermItem
	{
		String addrHex = null ;
		
		SZYFrame.UDTermUpFlow lastUpFlow = null ;
		
		public TermItem(String addrhex)
		{
			this.addrHex = addrhex ;
		}
		
		public String getAddrHex()
		{
			return this.addrHex ;
		}
		
		public SZYFrame.UDTermUpFlow getUpFlow()
		{
			return lastUpFlow ;
		}
	}
	
	SZYListener szyLis = new SZYListener() ;
	
	private UACh ch = null ; 
	

	private HashMap<String,TermItem> addrHex2Term = new HashMap<>() ;
	
	
	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return false;
	}
	
	IRecvCallback cb = new IRecvCallback() {
		
		@Override
		public void onRecvFrame(SZYFrame f)
		{
			RT_onFrameRecved(f) ;
		}
	};
	
	
	private synchronized TermItem getOrCreateTermItem(String addrhex)
	{
		TermItem ti = addrHex2Term.get(addrhex) ;
		if(ti==null)
		{
			ti = new TermItem(addrhex) ;
			addrHex2Term.put(addrhex,ti) ;
			RT_fireDrvWarn("找到新终端-"+addrhex) ;
		}
		return ti ;
	}
	
	private void RT_onFrameRecved(SZYFrame f)
	{
		//byte[] bs0 = f.packTo();
		//System.out.println(">>"+Convert.byteArray2HexStr(bs0)) ;
		//System.out.println(">>>["+f.getAddrHex()+"]  dir="+f.getDir()+"  "+f.getUserData()+" f="+f.getAddrA1()+" "+f.getAddrA2()) ;
		String addrhex = f.getAddrHex() ;
		TermItem ti = getOrCreateTermItem(addrhex) ;
		SZYFrame.UserData ud = f.getUserData() ;
		if(ud==null)
			return ;
		
		if(ud instanceof SZYFrame.UDTermUpFlow)
		{
			ti.lastUpFlow = (SZYFrame.UDTermUpFlow)ud ;
			RT_updateUpFlow(ti);
		}
	}

	@Override
	public void RT_onConnMsgIn(byte[] msgbs)
	{
		//System.out.println("SZY206_2016DriverLisOnly>>"+Convert.byteArray2HexStr(msgbs)) ;
		szyLis.onRecvedData(msgbs, cb);
	}

	@Override
	public DevDriver copyMe()
	{
		return new SZY206_2016DriverLisOnly();
	}

	@Override
	public String getName()
	{
		return "szy206_2016_lis_only";
	}

	@Override
	public String getTitle()
	{
		return "SZY206-2016只监听";
	}

	
	@Override
	public boolean supportDevFinder()
	{
		return true;
	}
	
	@Override
	public boolean updateFindedDevs(StringBuilder failedr)
	{
		ch = this.getBelongToCh() ;
		if(ch==null)
		{
			failedr.append("no ch found") ;
			return false;
		}
		
		try
		{
			for(TermItem ti:addrHex2Term.values())
			{
				reconstructDevTree(ti);
			}
			return true ;
		}
		catch(Exception ee)
		{
			failedr.append(ee.getMessage()) ;
			return false;
		}
	}
	
	private void reconstructDevTree(TermItem ti) throws Exception
	{
		String devn = "t_"+ti.getAddrHex() ;
		UADev dev = ch.getDevByName(devn) ;
		if(dev==null)
		{
			dev = ch.addDev(devn, "终端-"+ti.getAddrHex(), "", null, null, null) ;
		}
		if(ti.lastUpFlow!=null)
		{
			reconstructUpFlowTags(dev,ti.lastUpFlow) ;
		}
	}
	
	private void reconstructUpFlowTags(UADev dev,SZYFrame.UDTermUpFlow upf)
		throws Exception
	{
		 addNotExistedTag(dev,"flow","流速",UAVal.ValTP.vt_float);
		 addNotExistedTag(dev,"flow_t","累积流量",UAVal.ValTP.vt_float);
	}
	
	private void RT_updateUpFlow(TermItem ti)
	{
		SZYFrame.UDTermUpFlow upf = null ;
		if(ti==null||(upf=ti.lastUpFlow)==null)
			return ;
		ch = this.getBelongToCh() ;
		if(ch==null)
			return ;
		
		String devn = "t_"+ti.getAddrHex() ;
		UADev dev = ch.getDevByName(devn) ;
		if(dev==null)
			return ;
		Float f1 = upf.getFlow() ;
		if(f1!=null)
		{
			UATag tag = dev.getTagByName("flow") ;
			tag.RT_setValRaw(f1) ;
		}
		Float f2 = upf.getFlowT() ;
		if(f2!=null)
		{
			UATag tag = dev.getTagByName("flow_t") ;
			tag.RT_setValRaw(f2) ;
		}
	}
	
	private UATag addNotExistedTag(UADev dev,String tagn,String title,UAVal.ValTP vt) throws Exception
	{
		UATag tag = dev.getTagByName(tagn) ;
		if(tag!=null)
			return tag ;
		dev.addOrUpdateTag(null, false, tagn, title, "", null, vt, -1, null, -1, null, null) ;
		return tag ;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		return null;
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

	@Override
	public DevAddr getSupportAddr()
	{
		return null;
	}

	@Override
	protected void RT_onConnReady(ConnPt cp, UACh ch, UADev dev) throws Exception
	{
		
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp, UACh ch, UADev dev) throws Exception
	{
		
	}

	@Override
	protected boolean RT_runInLoop(UACh ch, UADev dev, StringBuilder failedr) throws Exception
	{
		return true;
	}

	@Override
	public boolean RT_writeVal(UACh ch, UADev dev, UATag tag, DevAddr da, Object v)
	{
		return false;
	}

	@Override
	public boolean RT_writeVals(UACh ch, UADev dev, UATag[] tags, DevAddr[] da, Object[] v)
	{
		return false;
	}

}
