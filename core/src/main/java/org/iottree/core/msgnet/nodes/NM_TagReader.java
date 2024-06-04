package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;

import org.iottree.core.UATag;
import org.iottree.core.UAVal;
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
			jo.putOpt("must_ok", this.bMustOk) ;
			return jo ;
		}
		
		public static TagItem fromJO(JSONObject jo)
		{
			TagItem ret = new TagItem() ;
			ret.tagPath = jo.optString("tag") ;
			ret.varName = jo.optString("varn") ;
			ret.bMustOk = jo.optBoolean("must_ok",false) ;
			return ret;
		}
	}
	
	
	ArrayList<TagItem> tagItems = new ArrayList<>() ;
	
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
		return 2;
	}

	@Override
	public String getOutTitle(int idx)
	{
		if(idx==0)
			return g("valid_data");
		if(idx==1)
			return g("invalid_tags");
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
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		JSONArray jarr = jo.optJSONArray("tags") ;
		ArrayList<TagItem> ccrs = new ArrayList<>() ;
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
					UATag tag = this.getBelongTo().getPrj().getTagByPath(tagpath) ;
					ccr.tag = tag ;
				}
			}
		}
		this.tagItems = ccrs ;
	}

	// --------------

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
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
			rto.asIdxMsg(1, new MNMsg().asPayload(errjarr)) ;
		return rto;
	}
}
