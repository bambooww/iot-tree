package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.ValUnit;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.util.CxtValRule;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 选择特定标签，然后配置特定名称var进行对应。生成简单的JSON数据
 * 
 * @author jason.zhu
 *
 */
public class NM_TagReader extends MNNodeMid
{
	public static class TagItem
	{
		String tagPath ;
		
		String varName ;
		
		boolean bMustOk = false;
		
		String title ;
		
		String unit ;

		private UATag tag = null ;
		
		public TagItem(String tagpath,String var_n,boolean b_must_ok)
		{
			this.tagPath = tagpath ;
			this.varName = var_n ;
			this.bMustOk = b_must_ok ;
		}
		
		private TagItem()
		{}
		
		public String getVarName()
		{
			if(Convert.isNullOrEmpty(this.varName))
				return tagPath ;
			return varName ;
		}
		
		public boolean isValid(StringBuilder failedr)
		{
			if(Convert.isNullOrEmpty(this.tagPath))
			{
				failedr.append("tag path cannot be null or empty") ;
				return false;
			}
			
			if(tag==null)
			{
				failedr.append("not tag with path ="+this.tagPath) ;
				return false;
			}
			return true ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.putOpt("tag", this.tagPath) ;
			jo.putOpt("varn", varName) ;
			jo.putOpt("t", this.title) ;
			jo.putOpt("unit", this.unit) ;
			jo.putOpt("must_ok", this.bMustOk) ;
			return jo ;
		}
		
		public JSONObject toTagJO()
		{
			JSONObject ret = new JSONObject().putOpt("var", this.varName)
					.putOpt("tag_path", this.tagPath);
			if(tag!=null)
			{
				ret.putOpt("tag_id", tag.getId()) ;
				ret.putOpt("tag_iid", tag.getIID()) ;
				if(Convert.isNotNullEmpty(this.title))
					ret.putOpt("tag_title", this.title) ;
				else
					ret.putOpt("tag_title", tag.getTitle()) ;
				ret.putOpt("tag_unit", tag.getUnit()) ;
				if(Convert.isNotNullEmpty(this.unit))
					ret.put("val_unit", this.unit);
				else
				{
					ValUnit vu = tag.getValUnit() ;
					if(vu!=null)
						ret.putOpt("val_unit", vu.getUnit()) ;
				}
				ValTP vtp = tag.getValTp() ;
				if(vtp!=null)
					ret.put("vtp", vtp.getStr());
			}
			return ret;
		}
		
		public static TagItem fromJO(JSONObject jo)
		{
			TagItem ret = new TagItem() ;
			ret.tagPath = jo.optString("tag") ;
			ret.varName = jo.optString("varn") ;
			ret.bMustOk = jo.optBoolean("must_ok",false) ;
			ret.title = jo.optString("t") ;
			ret.unit = jo.optString("unit") ;
			return ret;
		}
	}
	
	
	ArrayList<TagItem> tagItems = new ArrayList<>() ;
	
	boolean bTagValDetail = false;
	
	@Override
	public String getColor()
	{
		return "#a1cbde";
	}

	@Override
	public String getIcon()
	{
		return "\\uf02c";
	}

	@Override
	public JSONTemp getInJT()
	{
		return null;
	}

	@Override
	public JSONTemp getOutJT()
	{
		return null;
	}

	@Override
	public int getOutNum()
	{
		return 3;
	}
	
	@Override
	public String getOutColor(int idx)
	{
		if(idx==0)
			return null;
		if(idx==1)
			return "red";
		else if(idx==2)
			return "blue";
		return null ;
	}

	@Override
	public String getTP()
	{
		return "tag_reader";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_reader");
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(tagItems==null||tagItems.size()<=0)
			return true ;
		for(TagItem ti:this.tagItems)
		{
			if(!ti.isValid(failedr))
				return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		JSONArray jarr = new JSONArray() ;
		if(tagItems!=null)
		{
			for(TagItem ccr:this.tagItems)
			{
				JSONObject tmpjo = ccr.toJO() ;
				jarr.put(tmpjo) ;
			}
		}
		jo.put("tags",jarr) ;
		jo.put("tag_val_detail", bTagValDetail) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		JSONArray jarr = jo.optJSONArray("tags") ;
		ArrayList<TagItem> ccrs = new ArrayList<>() ;
		UAPrj prj = (UAPrj)this.getBelongTo().getContainer() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				TagItem ccr = TagItem.fromJO(tmpjo);
				if(ccr!=null)
					ccrs.add(ccr) ;
				
				String tagpath = ccr.tagPath ;
				if(Convert.isNotNullEmpty(tagpath))
				{
					UATag tag =prj.getTagByPath(tagpath) ;
					ccr.tag = tag ;
				}
			}
		}
		this.tagItems = ccrs ;
		
		this.bTagValDetail = jo.optBoolean("tag_val_detail",false) ;
		
		this.clearCache();
	}
	
	private void clearCache()
	{
		bOutList = true ;
	}

	// --------------
	
	private transient long RT_lastInvalidDT = -1 ;
	private transient JSONArray RT_lastInvalidTags = null ;

	private transient boolean bOutList = true;
	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{

		if(bOutList)
		{
			this.RT_onAfterNetRun();
			bOutList = false;
		}
		
		if(bTagValDetail)
			return RT_readTagOutDetail();
		else
			return RT_readTagOutSimple() ;
		
	}
	
	private RTOut RT_readTagOutDetail()
	{
		if(this.tagItems==null||this.tagItems.size()<=0)
			return null ;
		
		//boolean bok = true;
		JSONObject tmpjo = new JSONObject() ;
		JSONArray detail_jarr = new JSONArray() ;
		//JSONArray errjarr = new JSONArray() ;
		//bok = true ;
		for(TagItem ti:this.tagItems)
		{
			UATag tag = ti.tag ;
			if(tag==null)
				continue ;
			UAVal uav = tag.RT_getVal() ;
			JSONObject jo0 = null;
			if(uav==null||!uav.isValid())
			{
				jo0 = new JSONObject() ;
				jo0.put("valid",false) ;
			}
			else
			{
				JSONObject djo = uav.toDetailJO(tag) ;
				jo0 = new JSONObject(djo.toMap()) ;
				jo0.put("valid",true) ;
				jo0.putOpt("var_name",ti.getVarName()) ;
				//tmpjo.putOpt(ti.getVarName(), ) ;
				jo0.putOpt("tag_path", ti.tagPath) ;
				ValTP vtp = tag.getValTp() ;
				if(vtp!=null)
					jo0.put("vtp", vtp.getStr());
			}
			detail_jarr.put(jo0) ;
		}
		tmpjo.put("vals", detail_jarr) ;
		UAPrj prj = this.getPrj() ;
		tmpjo.put("prj_name", prj.getName()) ;
		tmpjo.put("prj_title", prj.getTitle()) ;
		RTOut rto = RTOut.createOutIdx() ;
		rto.asIdxMsg(0, new MNMsg().asPayload(tmpjo)) ;
		
		return rto;
	}
	
	private RTOut RT_readTagOutSimple()
	{
		if(this.tagItems==null||this.tagItems.size()<=0)
			return null ;
		
		boolean bok = true;
		JSONObject tmpjo = new JSONObject() ;
		JSONArray errjarr = new JSONArray() ;
		bok = true ;
		for(TagItem ti:this.tagItems)
		{
			UATag tag = ti.tag ;
			if(tag==null)
				continue ;
			UAVal uav = tag.RT_getVal() ;
			if(uav==null||!uav.isValid())
			{
				if(ti.bMustOk)
					bok= false;
				errjarr.put(ti.tagPath) ;
			}
			else
			{
				tmpjo.putOpt(ti.getVarName(), uav.getObjVal()) ;
			}
		}
		
		if(tmpjo.isEmpty() && errjarr.length()<=0)
			return null ;
		
		RTOut rto = RTOut.createOutIdx() ;
		if(bok && !tmpjo.isEmpty())
		{
			rto.asIdxMsg(0, new MNMsg().asPayload(tmpjo)) ;
		}
		if(errjarr.length()>0)
		{
			rto.asIdxMsg(1, new MNMsg().asPayload(errjarr)) ;
			RT_lastInvalidDT = System.currentTimeMillis() ;
			RT_lastInvalidTags = errjarr ;
		}
		return rto;
	}
	
	@Override
	protected void RT_onAfterNetRun()
	{
		//get tagitem and send out
		JSONObject pld = new JSONObject() ;
		JSONArray jarr = new JSONArray() ;
		if(this.tagItems!=null)
		{
			for(TagItem ti:this.tagItems)
			{
				JSONObject tjo = ti.toTagJO() ;
				jarr.put(tjo) ;
			}
		}
		pld.put("tags",jarr) ;
		UAPrj prj = this.getPrj() ;
		pld.put("prj_name", prj.getName()) ;
		pld.put("prj_title", prj.getTitle()) ;
		
		MNMsg msg = new MNMsg().asPayloadJO(pld) ;
		this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(2, msg));
	}

	@Override
	public String RT_getOutTitle(int idx)
	{
		if(idx==0)
			return g("valid_data");
		if(idx==1)
		{
			if(RT_lastInvalidTags==null || RT_lastInvalidDT<=0)
				return g("invalid_tags");
			else
			{
				return Convert.calcDateGapToNow(RT_lastInvalidDT)+"<br><pre>"+
						Convert.plainToHtml(RT_lastInvalidTags.toString(2)) +"</pre>";
			}
		}
		if(idx==2)
			return "output tag list" ;
		return null ;
	}
	
	@Override
	public String RT_getOutColor(int idx)
	{
		if(idx==0)
			return null;
		if(idx==1)
		{
			if(RT_lastInvalidTags==null || RT_lastInvalidDT<=0)
				return null ;
			return "red" ;
		}
			
		return null ;
	}

}
