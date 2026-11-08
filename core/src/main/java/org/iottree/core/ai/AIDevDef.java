package org.iottree.core.ai;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * AI Device Definition
 *    it can be used in UADev
 *    
 * 1,ai assistant name
 * 2,device action params (include tags in)
 * 
 * @author jason.zhu
 *
 */
public class AIDevDef
{
	public static abstract class Param
	{
		String uid ;
		
		String key ;
		
		String title ; //title or description
		
		Object current_value ;
		
		/**
		 * Param current data by read tag rt value
		 */
		String bindReadTag ;
		
		/**
		 * Param set data by write data to tag
		 */
		String bindWriteTag ;
		
		public Param()
		{
			this.uid = IdCreator.newSeqId() ;
		}
		
		public abstract String getTP() ;
		
		public String getUid()
		{
			return this.uid ;
		}
		
		public String getTitle()
		{
			return this.title ;
		}
		
		public String getKey()
		{
			if(this.key==null)
				return "" ;
			return this.key ;
		}
		
		public String getBindReadTag()
		{
			return this.bindReadTag ;
		}
		
		public String getBindWriteTag()
		{
			return this.bindWriteTag ;
		}
		
		public Object RT_getCurrentValue()
		{
			return this.current_value ;
		}
		
		public void RT_setCurrentValue(String cv)
		{
			this.current_value = cv ;
		}
		
		public abstract boolean RT_checkStrVal(String strv) ;
		
		
		public JSONObject toJO()
		{
			JSONObject ret = new JSONObject() ;
			//ret.put("uid", this.uid) ;
			ret.put("title", this.title) ;
			ret.put("key", this.key) ;
			ret.put("type", this.getTP()) ;
			ret.put("bind_r_tag",this.bindReadTag) ;
			ret.put("bind_w_tag",this.bindWriteTag) ;
			return ret ;
		}
		
		public JSONObject toPromptJO(UAPrj prj,StringBuilder failedr)
		{
			if(Convert.isNullOrEmpty(this.key))
			{
				failedr.append("no key set") ;
				return null ;
			}
			
			if(Convert.isNullOrEmpty(this.title))
			{
				failedr.append("no title set") ;
				return null ;
			}
			
			JSONObject ret = new JSONObject().put("key",this.key).put("name",this.title).put("type", this.getTP()) ;
			if(Convert.isNotNullEmpty(this.bindReadTag))
			{
				UATag tag = prj.getTagByPath(bindReadTag) ;
				if(tag!=null)
				{
					UAVal uav = tag.RT_getVal() ;
					if(uav!=null&&uav.isValid())
						ret.putOpt("current_value", uav.getObjVal()) ;
				}
			}
			return ret;
		}
		
		public boolean fromJO(JSONObject jo)
		{
			//this.uid = jo.optString("uid") ;
			//if(Convert.isNullOrEmpty(this.uid))
			//	return false;
			this.title = jo.optString("title") ;
			this.key = jo.optString("key") ;
			this.bindReadTag = jo.optString("bind_r_tag") ;
			this.bindWriteTag = jo.optString("bind_w_tag") ;
			return true ;
		}
		
		public static Param transFromJO(JSONObject jo)
		{
			String tp = jo.optString("type") ;
			if(tp==null)
				return null ;
			Param pm = null ;
			switch(tp)
			{
			case "state":
				pm = new ParamState() ;
				break;
			case "range":
				pm = new ParamRange() ;
				break;
			default:
				return null ;
			}
			
			if(pm.fromJO(jo))
				return pm ;
			return null ;
		}
	}
	
	public static class ValItem
	{
		public String value ; // on|off 1|2|3
		public String title ;
		
		public ValItem(String v,String t)
		{
			this.value = v ;
			this.title = t ;
		}
		
		public JSONObject toJO()
		{
			return new JSONObject().put("value", this.value).putOpt("title", this.title) ;
		}
		
		public static ValItem fromJO(JSONObject jo)
		{
			if(jo==null)
				return null ;
			String v = jo.optString("value") ;
			if(Convert.isNullOrEmpty(v))
				return null ;
			return new ValItem(v,jo.optString("title","")) ;
		}
	}
	
	public static class ParamState extends Param
	{
		ArrayList<ValItem> allowed_values = new ArrayList<>();

		@Override
		public String getTP()
		{
			return "state";
		}
		
		@Override
		public boolean RT_checkStrVal(String strv)
		{
			if(this.allowed_values==null)
				return false;
			for(ValItem vi:this.allowed_values)
			{
				if(vi.value.equals(strv))
					return true ;
			}
			return false;
		}
		
		@Override
		public JSONObject toJO()
		{
			JSONObject ret = super.toJO() ;
			JSONArray jarr = new JSONArray() ;
			if(allowed_values!=null)
			{
				for(ValItem vi:this.allowed_values)
					jarr.put(vi.toJO()) ;
			}
			ret.putOpt("allowed_values", jarr);
			return ret ;
		}
		
		@Override
		public JSONObject toPromptJO(UAPrj prj,StringBuilder failedr)
		{
			JSONObject ret = super.toPromptJO(prj,failedr) ;
			if(ret==null)
				return null ;
			if(this.allowed_values==null||this.allowed_values.size()<=0)
			{
				failedr.append("no allowed_values set") ;
				return null ;
			}
			
			JSONArray jarr = new JSONArray() ;
			if(allowed_values!=null)
			{
				for(ValItem vi:this.allowed_values)
					jarr.put(vi.toJO()) ;
			}
			ret.putOpt("allowed_values", jarr);
			
			return ret;
		}
		
		@Override
		public boolean fromJO(JSONObject jo)
		{
			boolean b = super.fromJO(jo) ;
			if(!b)
				return false;
			JSONArray jarr = jo.optJSONArray("allowed_values") ;
			this.allowed_values = new ArrayList<>() ;
			if(jarr!=null)
			{
				for(int i = 0 ; i < jarr.length() ; i ++)
				{
					Object ob = jarr.get(i) ;
					if(ob==null||"".equals(ob))
						continue ;
					if(ob instanceof String)
						this.allowed_values.add(new ValItem((String)ob,"")) ;
					else if(ob instanceof JSONObject)
					{
						ValItem vi = ValItem.fromJO((JSONObject)ob) ;
						if(vi==null)
							continue ;
						this.allowed_values.add(vi) ;
					}
				}
			}
			return true ;
		}
	}
	
	public static class ParamRange extends Param
	{
		Double minVal = null ;
		
		Double maxVal = null ;

		@Override
		public String getTP()
		{
			return "range";
		}
		
		@Override
		public boolean RT_checkStrVal(String strv)
		{
			try
			{
				double dv = Double.parseDouble(strv) ;
				if(minVal!=null && dv<minVal)
					return false;
				if(maxVal!=null && dv>maxVal)
					return false;
				
				return true ;
			}
			catch(Exception ee)
			{
				return false;
			}
		}
		
		@Override
		public JSONObject toJO()
		{
			JSONObject ret = super.toJO() ;
			ret.putOpt("min", minVal);
			ret.putOpt("max", maxVal);
			return ret ;
		}
		
		@Override
		public JSONObject toPromptJO(UAPrj prj,StringBuilder failedr)
		{
			JSONObject ret = super.toPromptJO(prj,failedr) ;
			if(ret==null)
				return null ;
			return ret.putOpt("min", minVal).putOpt("max", maxVal);
		}
		
		@Override
		public boolean fromJO(JSONObject jo)
		{
			boolean b = super.fromJO(jo) ;
			if(!b)
				return false;
			if(jo.has("min"))
			{
				Object v = jo.get("min") ;
				if(v instanceof Number)
					this.minVal = ((Number)v).doubleValue() ;
				else if(v instanceof String && !"".equals(v))
					this.minVal = Double.parseDouble((String)v) ;
			}
			if(jo.has("max"))
			{
				Object v = jo.get("max") ;
				if(v instanceof Number)
					this.maxVal = ((Number)v).doubleValue() ;
				else if(v instanceof String && !"".equals(v))
					this.maxVal = Double.parseDouble((String)v) ;
			}
			return true ;
		}
	}
	
	String aiaName ; //ai assistant name
	
	ArrayList<Param> parameters = new ArrayList<>() ;
	
	public String getAIAName()
	{
		return this.aiaName ;
	}
	
	public List<Param> getParams()
	{
		return this.parameters ;
	}
	
	public static AIDevDef parseFrom(String aia_name,JSONObject pm_jo)
	{
		AIDevDef ret = new AIDevDef() ;
		ret.aiaName = aia_name ;
		JSONArray jarr = pm_jo.optJSONArray("params") ;
		ArrayList<Param> pms = new ArrayList<>() ;
		if(jarr!=null)
		{
			for(int i = 0 ; i < jarr.length() ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				Param pm = Param.transFromJO(tmpjo) ;
				if(pm==null)
					continue ;
				pms.add(pm) ;
			}
		}
		if(pms.size()<=0)
			return null ;
		ret.parameters = pms ;
		return ret;
	}
}
