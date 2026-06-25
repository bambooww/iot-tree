package org.iottree.core.drv;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.DevManager;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.json.JSONArray;
import org.json.JSONObject;

public class LocPrjMapDriver extends DevDriver
{
	public static final String NAME = "loc_prj_map";
	
	public static abstract class MapItem
	{
		public abstract void RT_update() ;
		
		public abstract void synTags() ;
		
		static void updateValTag2Tag(UATag ptag,UATag ctag)
		{
			UAVal p_uav = ptag.RT_getVal() ;
			UAVal.ValTP p_vt = ptag.getValTp() ;
			long p_updt = p_uav.getValDT() ;
			
			UAVal c_uav = ctag.RT_getVal() ;
			UAVal.ValTP c_vt = ctag.getValTp() ;
			if(c_uav.getValDT()==p_updt)
				return ; //no chg
			boolean bvalid = p_uav.isValid() ;
			long chgdt = p_uav.getValChgDT() ;

			Object ov = p_uav.getObjVal();
			if(p_vt!=c_vt && bvalid)
			{
				String strv = "";
				if (ov != null)
					strv = "" + ov;
				try
				{
					ov = UAVal.transStr2ObjVal(ctag.getValTp(), strv);
				}
				catch(Exception ee)
				{
					UAVal uav = new UAVal("transfer str="+strv+" error",ee) ;
					ctag.RT_setUAValOnlyAlert(uav);
					return ;
				}
			}
			
			UAVal uav = new UAVal(bvalid, ov, p_updt, chgdt);
			// tag.RT_setValStr(strv, true);
			ctag.RT_setUAValOnlyAlert(uav);
		}
	}
	
	public static class TagItem extends MapItem
	{
		//String id ;
		
		String tagInPrj ;
		
		String tagInCh ;
		
		private transient PrjItem belongTo ;
		
		TagItem(PrjItem pi)
		{
			this.belongTo = pi ;
		}
		
		public JSONObject toJO()
		{
			return new JSONObject()//.put("id", this.id)
					.putOpt("tip", this.tagInPrj).putOpt("tic", this.tagInCh) ;
		}
		
		public JSONObject toJODetail()
		{
			JSONObject jo = this.toJO() ;
			UATag ptag = this.getPrjTag() ;
			UATag ctag = this.getChTag() ;
			if(ptag!=null)
				jo.put("tip_vt",ptag.getValTp().getStr()) ;
			if(ctag!=null)
				jo.put("tic_vt",ctag.getValTp().getStr()) ;
			return jo ;
		}
		
		public boolean fromJO(JSONObject jo)
		{
//			this.id = jo.optString("id") ;
//			if(Convert.isNullOrEmpty(this.id))
//				return false;
			this.tagInPrj = jo.optString("tip") ;
			if(Convert.isNullOrEmpty(this.tagInPrj))
				return false;
			this.tagInCh = jo.optString("tic") ;
			return true ;
		}
		
		private transient UATag prjTag = null ;
		
		private transient UATag chTag = null ;
		
		private UATag getPrjTag()
		{
			if(prjTag!=null)
				return this.prjTag ;
			if(Convert.isNullOrEmpty(this.tagInPrj))
				return null ;
			UAPrj prj = this.belongTo.getPrj() ;
			if(prj==null)
				return null ;
			return this.prjTag = prj.getTagByPath(this.tagInPrj) ;
		}
		
		private UATag getChTag()
		{
			if(chTag!=null)
				return this.chTag ;
			if(Convert.isNullOrEmpty(this.tagInCh))
				return null ;
			UACh ch = this.belongTo.getSelfCh() ;
			if(ch==null)
				return null ;
			return this.chTag = ch.getTagByPath(this.tagInCh) ;
		}
		
		public void RT_update()
		{
			UATag ptag = this.getPrjTag() ;
			UATag ctag = this.getChTag() ;
			if(ptag==null||ctag==null)
				return ;

			updateValTag2Tag(ptag,ctag) ;
		}
		
		@Override
		public void synTags()
		{}
	}
	
	public static class DirItem extends MapItem
	{
		String id ;
		
		String dirInPrj ;
		
		String dirInCh ;
		
		PrjItem belongTo ;
		
		DirItem(PrjItem pi)
		{
			this.belongTo = pi ;
		}
		
		public JSONObject toJO()
		{
			return new JSONObject().put("id", this.id)
					.putOpt("dip", this.dirInPrj).putOpt("dic", this.dirInCh) ;
		}
		
		public JSONObject toJODetail()
		{
			JSONObject jo = this.toJO() ;
			return jo ;
		}
		
		public boolean fromJO(JSONObject jo)
		{
			this.id = jo.optString("id") ;
			if(Convert.isNullOrEmpty(this.id))
				return false;
			this.dirInPrj = jo.optString("dip") ;
			this.dirInCh = jo.optString("dic") ;
			return true ;
		}
		
		private transient UANodeOCTagsCxt prjDir = null ;
		
		private transient UANodeOCTagsCxt chDir = null ;
		
		private UANodeOCTagsCxt getPrjDir()
		{
			if(prjDir!=null)
				return this.prjDir ;
			if(this.belongTo.prj==null)
				return null ;
			
			UANode nd = this.belongTo.prj.getDescendantNodeByPath(this.dirInPrj);
			if(nd==null || !(nd instanceof UANodeOCTagsCxt))
				return null ;
			return this.prjDir = (UANodeOCTagsCxt)nd;
		}
		
		private UANodeOCTagsCxt getChDir()
		{
			if(chDir!=null)
				return this.chDir ;
			if(this.belongTo.prj==null)
				return null ;
			UACh ch = this.belongTo.getSelfCh() ;
			if(ch==null)
				return null ;
			UANode nd = ch.getDescendantNodeByPath(this.dirInCh);
			if(nd==null || !(nd instanceof UANodeOCTagsCxt))
				return null ;
			return this.chDir = (UANodeOCTagsCxt)nd;
		}
		
		@Override
		public void RT_update()
		{
			UANodeOCTagsCxt p_dir = this.getPrjDir() ;
			UANodeOCTagsCxt c_dir = this.getChDir() ;
			if(p_dir==null||c_dir==null)
				return ;
			
			List<UATag> c_dir_tags = c_dir.getNorTags();
			if(c_dir_tags==null||c_dir_tags.size()<=0)
				return ;
			for(UATag c_tag:c_dir_tags)
			{
				UATag p_tag = p_dir.getTagByName(c_tag.getName()) ;
				if(p_tag==null)
					continue ;
				updateValTag2Tag(p_tag,c_tag) ;
			}
		}
		
		@Override
		public void synTags()
		{
			UANodeOCTagsCxt p_dir = this.getPrjDir() ;
			UANodeOCTagsCxt c_dir = this.getChDir() ;
			if(p_dir==null||c_dir==null)
				return ;
			//check dir syn all tags
			
		}
	}
	
	public static class PrjItem
	{
		UACh selfCh ;
		
		String prjid ;
		
		LinkedHashMap<String,DirItem> dirInPrj2di = new LinkedHashMap<>() ;
		
		LinkedHashMap<String,TagItem> tagInPrj2ti = new LinkedHashMap<>() ;
		
		public PrjItem(UACh self_ch)
		{
			this.selfCh = self_ch ;
		}
		
		public UAPrj getPrj()
		{
			return UAManager.getInstance().getPrjById(this.prjid) ;
		}
		
		public UACh getSelfCh()
		{
			return this.selfCh ;
		}
		
		public JSONObject toJO()
		{
			JSONObject ret = new JSONObject() ;
			ret.putOpt("prjid", this.prjid) ;
			JSONArray jarr = new JSONArray() ;
			ret.put("tis", jarr) ;
			for(TagItem ti:tagInPrj2ti.values())
			{
				jarr.put(ti.toJO()) ;
			}
			jarr = new JSONArray() ;
			ret.put("dis", jarr) ;
			for(DirItem ti:dirInPrj2di.values())
			{
				jarr.put(ti.toJO()) ;
			}
			return ret ;
		}
		
		public JSONObject toJODetail()
		{
			JSONObject ret = new JSONObject() ;
			
			ret.putOpt("prjid", this.prjid) ;
			UAPrj prj= getPrj();
			if(prj!=null)
				ret.put("prjt",prj.getTitle()) ;
				
			JSONArray jarr = new JSONArray() ;
			ret.put("tis", jarr) ;
			for(TagItem ti:tagInPrj2ti.values())
			{
				jarr.put(ti.toJODetail()) ;
			}
			jarr = new JSONArray() ;
			ret.put("dis", jarr) ;
			for(DirItem ti:dirInPrj2di.values())
			{
				jarr.put(ti.toJODetail()) ;
			}
			return ret ;
		}
		
		public boolean fromJO(JSONObject jo)
		{
			this.prjid = jo.optString("prjid") ;
			JSONArray jarr = jo.optJSONArray("tis") ;
			if(jarr!=null)
			{
				int n = jarr.length() ;
				for(int i = 0 ; i < n ; i ++)
				{
					JSONObject tmpjo = jarr.getJSONObject(i) ;
					TagItem ti = new TagItem(this) ;
					if(ti.fromJO(tmpjo))
						tagInPrj2ti.put(ti.tagInPrj,ti) ;
				}
			}
			jarr = jo.optJSONArray("dis") ;
			if(jarr!=null)
			{
				int n = jarr.length() ;
				for(int i = 0 ; i < n ; i ++)
				{
					JSONObject tmpjo = jarr.getJSONObject(i) ;
					DirItem ti = new DirItem(this) ;
					if(ti.fromJO(tmpjo))
						dirInPrj2di.put(ti.dirInPrj,ti) ;
				}
			}
			return true;
		}
		
		private transient UAPrj prj = null ; 
		
		public void RT_update()
		{
			if(prj==null)
				prj = UAManager.getInstance().getPrjById(this.prjid) ;

			if(prj==null)
				return ;
			
			for(MapItem ti:tagInPrj2ti.values())
			{
				ti.RT_update();
			}
			for(MapItem ti:dirInPrj2di.values())
			{
				ti.RT_update();
			}
		}
	}
	
	
	@Override
	public DevDriver copyMe()
	{
		return new LocPrjMapDriver();
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public String getTitle()
	{
		return "Local Project Map";
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return null;
	}
	
	@Override
	public boolean isIgnoreConnPt()
	{
		return true;
	}
	
	@Override
	public boolean hasDriverConfigPage()
	{
		return true;
	}

	@Override
	public boolean supportDevFinder()
	{
		return false;
	}
	

	@Override
	public DevAddr getSupportAddr()
	{
		return null;
	}
	

	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		return null;
	}
	
	public static final String GROUP_N = "loc_prj_map";
	
	public static final String PI_MAP_PM = "map_pm";

	@Override
	public List<PropGroup> getPropGroupsForCh(UACh ch)
	{
//		ArrayList<PropGroup> pgs = new ArrayList<>() ;
//		
//		PropGroup gp = null;
//		
//		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
//		
//		gp = new PropGroup(GROUP_N,lan);//"Connections");
//		gp.addPropItem(new PropItem(PI_MAP_PM,lan,PValTP.vt_str,false,null,null,"")
//				.withPop("drv_spc")); //"Title",""
//		pgs.add(gp) ;
//		
//		return pgs;
		return null ;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		return null;
	}


	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return false;
	}
	
	public LinkedHashMap<String,PrjItem> parseFromJArr(JSONArray jarr)
	{
		LinkedHashMap<String,PrjItem> ret = new LinkedHashMap<>() ;
		if(jarr==null)
			return ret;
		int n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			PrjItem pi = new PrjItem(this.getBelongToCh()) ;
			if(pi.fromJO(jo))
				ret.put(pi.prjid,pi) ;
		}
		return ret ;
	}

	private LinkedHashMap<String,PrjItem> id2prjitem = null ;
	
	@Override
	protected boolean initDriver(StringBuilder failedr) throws Exception
	{
		UACh ch = this.getBelongToCh() ;
		if(ch==null)
			return false ;
		
		String jstr = ch.getDrvSpcConfigTxt();//ch.getOrDefaultPropValueStr(GROUP_N, PI_MAP_PM, "") ;
		if(Convert.isNullOrEmpty(jstr))
		{
			id2prjitem = new LinkedHashMap<>() ; ;
			return false;
		}
		JSONArray jarr = new JSONArray(jstr) ;
		id2prjitem =  parseFromJArr(jarr) ;
		return true ;
	}
	

	@Override
	protected void RT_onConnReady(ConnPt cp, UACh ch, UADev dev) throws Exception
	{
		
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp, UACh ch, UADev dev) throws Exception
	{
		
	}
	
	/**
	 * call by other project
	 * @param tag
	 */
	public void RT_onOtherPrjTagUpdate(UATag tag)
	{
		if(this.id2prjitem==null||id2prjitem.size()<=0)
			return ;
		UAPrj prj = tag.getBelongToPrj();
//		LinkedHashMap<String,PrjItem> id2p = RT_getPrjItems() ;
//		PrjItem pi = id2p.get(prj.getId());
	}

	@Override
	protected boolean RT_runInLoop(UACh ch, UADev dev, StringBuilder failedr) throws Exception
	{
		if(id2prjitem==null||id2prjitem.size()<=0)
			return true ;
		for(PrjItem pi:id2prjitem.values())
		{
			pi.RT_update();
		}
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
