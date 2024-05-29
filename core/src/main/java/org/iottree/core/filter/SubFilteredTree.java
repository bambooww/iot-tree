package org.iottree.core.filter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UANodeOCTagsGCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.dict.DataClass;
import org.iottree.core.dict.DictManager;
import org.iottree.core.dict.PrjDataClass;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * sub branch tree,in which node or leaf is filtered from original tree
 * 
 * @author jason.zhu
 *
 */
public class SubFilteredTree
{
	static Lan lan = Lan.getLangInPk(SubFilteredTree.class);

	public static enum PropFitTP
	{
		has, prefix, eq
	}

	public static class PropFit
	{
		String propName;

		// String propTitle ;

		PropFitTP fitTP;

		String fitVal;

		public PropFit()
		{
		}

		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject();
			jo.put("pn", propName);
			jo.put("pftp", fitTP.name());
			jo.putOpt("fit_v", fitVal);
			return jo;
		}

		public static PropFit fromJO(JSONObject jo)
		{
			String pn = jo.optString("pn");
			String pftp = jo.optString("pftp");
			if (Convert.isNullOrEmpty(pn) || Convert.isNullOrEmpty(pftp))
				return null;
			PropFit ret = new PropFit();
			ret.propName = pn;
			ret.fitTP = PropFitTP.valueOf(pftp);
			ret.fitVal = jo.optString("fit_v");
			return ret;
		}
	}

	public static class PropDef
	{
		String propName;

		String propTitle;

		public PropDef(String n, String t)
		{
			this.propName = n;
			this.propTitle = t;
		}

		public String getPropName()
		{
			return this.propName;
		}

		public String getPropTitle()
		{
			return this.propTitle;
			// return lan.g("pn_"+this.propName) ;
		}
	}

	public static List<PropDef> ALL_PROP_DEFS = Arrays.asList(new PropDef("n", lan.g("pn_n")),
			new PropDef("t", lan.g("pn_t")));

	public static List<PropDef> getExtPropDefsCont(UAPrj prj)
	{
		PrjDataClass pdc = DictManager.getInstance().getPrjDataClassByPrjId(prj.getId());
		if (pdc == null)
			return null;

		ArrayList<PropDef> rets = new ArrayList<>();
		Collection<DataClass> dcs = pdc.getDataClassAll();
		for (DataClass dc : dcs)
		{
			if (!dc.isBindForContainer())
				continue;
			String dc_name = dc.getClassName();
			String dc_title = dc.getClassTitle();
			rets.add(new PropDef(dc_name, dc_title));
		}
		return rets;
	}

	public static List<PropDef> getExtPropDefsTag(UAPrj prj)
	{
		PrjDataClass pdc = DictManager.getInstance().getPrjDataClassByPrjId(prj.getId());
		if (pdc == null)
			return null;

		ArrayList<PropDef> rets = new ArrayList<>();
		Collection<DataClass> dcs = pdc.getDataClassAll();
		for (DataClass dc : dcs)
		{
			if (!dc.hasBindFor(UATag.NODE_TP))
				continue;
			String dc_name = dc.getClassName();
			String dc_title = dc.getClassTitle();
			rets.add(new PropDef(dc_name, dc_title));
		}
		return rets;
	}

	protected UAPrj prj = null;
	/**
	 * sub root
	 */
	private String subRootPath = null;

	private UANodeOCTagsCxt rootNode = null;

	/**
	 * null - all node prj , ch ,dev, tagg
	 */
	private HashSet<String> containerTPSet = new HashSet<>();

	private boolean containerTPSetEn = false;

	private HashSet<String> containerExtSet = new HashSet<>();

	private boolean containerExtSetEn = false;

	/**
	 * true- include sys tag
	 */
	private boolean tagIncSys = false;

	/**
	 * null - all tag
	 */
	private HashSet<String> tagExtSet = new HashSet<>();

	private boolean tagExtSetEn = false;

	public SubFilteredTree(UAPrj prj)
	{
		this.prj = prj;
	}

	public UAPrj getPrj()
	{
		return this.prj;
	}

	public SubFilteredTree asSubRootPath(String rootp)
	{
		UANode n = UAManager.getInstance().findNodeByPath(this.subRootPath);
		if (!(n instanceof UANodeOCTagsCxt))
			throw new IllegalArgumentException("no cxt node found with path=" + rootp);
		rootNode = (UANodeOCTagsCxt) n;
		return this;
	}

	public String getSubRootPath()
	{
		if (this.subRootPath == null)
			return "";
		return this.subRootPath;
	}

	// public SubFilteredTree asCFiltersOr(List<ICFilter> cfs)
	// {
	// contFiltersOr.addAll(cfs) ;
	// return this ;
	// }
	//
	// public SubFilteredTree asTFiltersOr(List<Filter> tfs)
	// {
	// tagFiltersOr.addAll(tfs) ;
	// return this ;
	// }

	public UANodeOCTagsCxt getRootNode()
	{
		if (Convert.isNullOrEmpty(this.subRootPath))
			return this.prj;
		if (rootNode != null)
			return rootNode;
		UANode n = UAManager.getInstance().findNodeByPath(this.subRootPath);
		if (!(n instanceof UANodeOCTagsCxt))
			throw new IllegalArgumentException("no cxt node found with path=" + subRootPath);
		rootNode = (UANodeOCTagsCxt) n;
		return rootNode;
	}

	public boolean isContainerTPSetEn()
	{
		return this.containerTPSetEn;
	}

	public HashSet<String> getContainerTPSet()
	{
		return this.containerTPSet;
	}

	public boolean isContainerExtSetEn()
	{
		return this.containerExtSetEn;
	}

	public HashSet<String> getContainerExtSet()
	{
		return this.containerExtSet;
	}

	public boolean isTagIncSys()
	{
		return this.tagIncSys;
	}

	public boolean isTagExtSetEn()
	{
		return this.tagExtSetEn;
	}

	public HashSet<String> getTagExtSet()
	{
		return this.tagExtSet;
	}
	
	public JSONObject toDefJO()
	{
		JSONObject jo = new JSONObject();
		jo.putOpt("sub_root_path", this.subRootPath);
		jo.put("c_tps_en", this.containerTPSetEn);
		jo.putOpt("c_tps", this.containerTPSet);

		jo.put("c_exts_en", this.containerExtSetEn);
		jo.putOpt("c_exts", this.containerExtSet);

		jo.put("tag_inc_sys", this.tagIncSys);

		jo.put("tag_exts_en", this.tagExtSetEn);
		jo.putOpt("tag_exts", this.tagExtSet);

		return jo;
	}

	public boolean fromDefJO(JSONObject jo)
	{
		this.subRootPath = jo.optString("sub_root_path");

		this.containerTPSetEn = jo.optBoolean("c_tps_en", false);
		JSONArray jarr = jo.optJSONArray("c_tps");
		if (jarr != null)
		{
			HashSet<String> tps = new HashSet<>();
			int n = jarr.length();
			for (int i = 0; i < n; i++)
				tps.add(jarr.getString(i));
			this.containerTPSet = tps;
		}

		this.containerExtSetEn = jo.optBoolean("c_exts_en", false);
		jarr = jo.optJSONArray("c_exts");
		if (jarr != null)
		{
			HashSet<String> tps = new HashSet<>();
			int n = jarr.length();
			for (int i = 0; i < n; i++)
				tps.add(jarr.getString(i));
			this.containerExtSet = tps;
		}

		this.tagIncSys = jo.optBoolean("tag_inc_sys", false);

		this.tagExtSetEn = jo.optBoolean("tag_exts_en", false);
		jarr = jo.optJSONArray("tag_exts");
		if (jarr != null)
		{
			HashSet<String> tps = new HashSet<>();
			int n = jarr.length();
			for (int i = 0; i < n; i++)
				tps.add(jarr.getString(i));
			this.tagExtSet = tps;
		}

		return true;
	}
	
	private boolean checkContNodeFit(UANodeOCTagsCxt node)
	{
		if(this.containerTPSetEn)
		{
			String tp = node.getNodeTp() ;
			if(!this.containerTPSet.contains(tp))
				return false;
		}
		
		if(this.containerExtSetEn)
		{
			JSONObject ejo = node.getExtAttrJO() ;
			if(ejo==null)
				return false;
			for(String ext:this.containerExtSet)
			{
				if(ejo.has(ext))
					return true ;
			}
		}
		return true;
	}
	
	private boolean checkTagFit(UATag tag)
	{
		if(!this.tagExtSetEn)
			return true ;
		
		JSONObject ejo = tag.getExtAttrJO() ;
		if(ejo==null)
			return false;
		for(String ext:this.tagExtSet)
		{
			if(ejo.has(ext))
				return true ;
		}
		return false;
	}

	public JSONObject RT_getFilteredJO()
	{
		UANodeOCTagsCxt rootn = getRootNode();
		if(rootn==null)
			return null ;
		
		return RT_renderToJO(rootn);
	}

	private JSONObject RT_renderToJO(UANodeOCTagsCxt node)
	{
		JSONObject jo = new JSONObject();
		// long maxdt=-1 ;
		jo.put("id", node.getId());
		jo.put("n", node.getName());
		jo.put("t", node.getTitle());
		jo.put("tp", node.getNodeTp());

		JSONObject extjo = node.getExtAttrJO();
		if (extjo != null)
		{
			UTIL_transExtPropsToJO(jo, extjo);
		}

		JSONArray jarr = getTagsJarr(node);
		if (jarr != null)
			jo.put("tags", jarr);

		jarr = getSubsJarr(node);
		if (jarr != null)
			jo.put("subs", jarr);

		jarr = node.CXT_getAlertsJArr();
		if (jarr != null && jarr.length() > 0)
		{// alert_handlers/alert_items
			jo.put("has_alert", true);
			jo.put("alerts", jarr);
		}
		return jo;
	}

	private JSONArray getTagsJarr(UANodeOCTagsCxt node)
	{
		List<UATag> tags = null;
		
		if(this.tagIncSys) //
			tags = node.listTags();
		else
			tags = node.getNorTags() ;
		if(tags==null||tags.size()<=0)
			return null ;
		
		JSONArray jarr = new JSONArray();
		for (UATag tg : tags)
		{
			if(!checkTagFit(tg))
				continue ;
			JSONObject tmpjo = tg.RT_toJson(false, true, true);
			jarr.put(tmpjo);
		}
		if(jarr.length()<=0)
			return null ;
		return jarr;
	}

	private JSONArray getSubsJarr(UANodeOCTagsCxt node)
	{
		List<UANodeOCTagsCxt> subtgs = node.getSubNodesCxt();
		if (subtgs == null || subtgs.size() <= 0)
			return null;

		JSONArray jarr = new JSONArray();
		for (UANodeOCTagsCxt subtg : subtgs)
		{
			if(!checkContNodeFit(subtg))
				continue ;
			
			JSONObject tmpjo = RT_renderToJO(subtg);
			if (tmpjo != null)
				jarr.put(tmpjo);
		}
		if(jarr.length()<=0)
			return null ;
		return jarr;
	}

	static void UTIL_transExtPropsToJO(JSONObject tar, JSONObject ext_jo)
	{
		for (String n : JSONObject.getNames(ext_jo))
		{
			Object ob = ext_jo.get(n);
			tar.put("ext_" + n, ob);
		}

	}
}
