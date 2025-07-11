package org.iottree.core;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.HostAccess;
import org.iottree.core.alert.AlertItem;
import org.iottree.core.alert.AlertManager;
import org.iottree.core.basic.ValEvent;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsDef;
import org.iottree.core.cxt.JsEnv;
import org.iottree.core.cxt.JsMethod;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.cxt.JsSub;
import org.iottree.core.cxt.JsSubOb;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.plugin.PlugJsApi;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * node support
 * 
 * @author zzj
 *
 */
@data_class
public abstract class UANodeOCTagsCxt extends UANodeOCTags
{
	/**
	 * 
	 */
	//private static final long serialVersionUID = 7775465863975907067L;

	/**
	 * runtime context
	 */
	transient UAContext rtCxt = null;

	@data_obj(obj_c = UAHmi.class)
	List<UAHmi> hmis = new ArrayList<>();

	public UANodeOCTagsCxt()
	{
		super();
	}

	public UANodeOCTagsCxt(String name, String title, String desc)
	{
		super(name, title, desc);
	}

	@Override
	protected void copyTreeWithNewSelf(IRoot root,UANode new_self, String ownerid, 
			boolean copy_id, boolean root_subnode_id,HashMap<IRelatedFile,IRelatedFile> rf2new)
	{
		super.copyTreeWithNewSelf(root,new_self, ownerid, copy_id, root_subnode_id,rf2new);
		UANodeOCTagsCxt self = (UANodeOCTagsCxt) new_self;
		self.hmis.clear();
		for (UAHmi hmi : hmis)
		{
			UAHmi nt = new UAHmi();
			
			if (root_subnode_id)
			{
				if(root!=null)
					nt.id = root.getRootNextId();
				else
					nt.id = this.getNextIdByRoot();
			}
			//	nt.id = this.getNextIdByRoot();
			hmi.copyTreeWithNewSelf(root,nt, ownerid, copy_id, root_subnode_id,rf2new);
			self.hmis.add(nt);
		}
	}

	// void constructNodeTree()
	// {
	// for(UAHmi hmi:hmis)
	// {
	// //tgg.belongToDev = this ;
	// hmi.parentNode = this ;
	// }
	// super.constructNodeTree();
	// }
	
	public  List<UAHmi> listSubHmiNodesAll()
	{
		ArrayList<UAHmi> rets = new ArrayList<>() ;
		listSubHmiNodesAll(rets) ;
		return rets ;
	}
	
	private void listSubHmiNodesAll(List<UAHmi> hmis)
	{
		if(this.hmis!=null)
			hmis.addAll(this.hmis) ;
		List<UANode> ns = this.getSubNodes();
		if (ns == null)
			return ;
		for (UANode n : ns)
		{
			if (n instanceof UANodeOCTagsCxt)
			{
				((UANodeOCTagsCxt) n).listSubHmiNodesAll(hmis);
			}
		}
	}

	public List<UAHmi> getHmis()
	{
		return hmis;
	}

	public UAHmi getHmiById(String id)
	{
		for (UAHmi ch : hmis)
		{
			if (id.contentEquals(ch.getId()))
				return ch;
		}
		return null;
	}

	public UAHmi getHmiByName(String n)
	{
		for (UAHmi ch : hmis)
		{
			if (n.contentEquals(ch.getName()))
				return ch;
		}
		return null;
	}

	public UAHmi addHmi(String tp, String name, String title, String desc, HashMap<String, Object> uiprops)
			throws Exception
	{
		UAUtil.assertUAName(name);

		UANode subn = this.getSubNodeByName(name);
		// UAHmi ch = getHmiByName(name);
		if (subn != null)
		{
			throw new IllegalArgumentException("subnode with name=" + name + " existed");
		}
		UAHmi ch = new UAHmi(name, title, desc, tp);
		if (uiprops != null)
		{
			for (Map.Entry<String, Object> n2v : uiprops.entrySet())
			{
				ch.OCUnit_setProp(n2v.getKey(), n2v.getValue());
			}
		}
		// ch.belongTo = this;
		ch.id = this.getNextIdByRoot();
		hmis.add(ch);
		this.constructNodeTree();

		save();
		return ch;
	}

	public UAHmi updateHmi(UAHmi hmi, String name, String title, String desc) throws Exception
	{
		UAUtil.assertUAName(name);

		UAHmi oldhmi = getHmiByName(name);
		if (oldhmi != null && oldhmi != hmi)
		{
			throw new IllegalArgumentException("hmi with name=" + name + " existed");
		}
		// ch = new UAHmi(name, title, desc, "");
		hmi.setNameTitle(name, title, desc);
		// ch.belongTo = this;
		// hmis.add(ch);
		this.constructNodeTree();

		save();
		return hmi;
	}

	void delHmi(UAHmi ch) throws Exception
	{
		hmis.remove(ch);
		save();
		File f = ch.getHmiUIFile();
		if (f.exists())
			f.delete();
	}

	/**
	 * 
	 * @param hmi
	 * @return
	 * @throws Exception 
	 */
	public UAHmi pasteHmi(UAHmi hmi) throws Exception
	{
		UAHmi nt = new UAHmi();
		
		HashMap<IRelatedFile,IRelatedFile> rf2new = new HashMap<>();
		nt.id = this.getNextIdByRoot() ;
		hmi.copyTreeWithNewSelf((IRoot)this.getTopNode(),nt, null, false, true, rf2new);
		String oldn = nt.getName() ;
		UAHmi oldhmi = this.getHmiByName(oldn) ;
		if(oldhmi!=null)
		{
			String newn = this.calNextSubNameAuto(oldn);
			nt.setNameTitle(newn, nt.getTitle(), nt.getDesc()) ;
		}
		hmis.add(nt);
		this.constructNodeTree();
		save();
		Convert.copyRelatedFile(rf2new);
		return nt ;
	}
	
	public List<UANode> getSubNodes()
	{
		List<UANode> rets = super.getSubNodes();
		rets.addAll(hmis);
		return rets;
	}

	public List<UANodeOCTagsCxt> getSubNodesCxt()
	{
		List<UANode> subs = getSubNodes();
		ArrayList<UANodeOCTagsCxt> rets = new ArrayList<>();
		for (UANode n : subs)
		{
			if (n instanceof UANodeOCTagsCxt)
				rets.add((UANodeOCTagsCxt) n);
		}
		return rets;
	}
	
	public UATag findTagById(String id)
	{
		UANode uan = this.findNodeById(id);
		if (uan == null || !(uan instanceof UATag))
		{
			return null;
		}
		return (UATag) uan;
	}

	@Override
	public List<IOCBox> OC_getSubs()
	{
		List<IOCBox> rets = super.OC_getSubs();
		if (hmis != null)
			rets.addAll(hmis);
		return rets;
	}

	/**
	 * 
	 * @return
	 */
	public UAContext RT_getContext()
	{
		if (rtCxt != null)
			return rtCxt;

		synchronized (this)
		{
			if (rtCxt != null)
				return rtCxt;

			try
			{
				rtCxt = new UAContext(this);
				return rtCxt;
			}
			catch ( Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}

	protected UAContext RT_reContext()
	{
		try
		{
			rtCxt = new UAContext(this);
			return rtCxt;
		}
		catch ( Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	// -----------------script support

	/**
	 * support cxt key to be found
	 * 
	 * @param jsk
	 * @return
	 */
	public abstract boolean CXT_containsKey(String jsk);

	public abstract Object CXT_getByKey(String jsk);

	/**
	 * calculate mid tag values in this context
	 */
	protected void CXT_calMidTagsValLocal()
	{
		List<UATag> midtags = this.listTagsMid();
		for (UATag mtg : midtags)
		{
			mtg.CXT_calMidVal();
		}
	}

	final void CXT_calMidTagsVal()
	{
		// cal sub first
		List<UANodeOCTagsCxt> subncxts = this.getSubNodesCxt();
		if (subncxts != null)
		{
			for (UANodeOCTagsCxt subncxt : subncxts)
			{
				subncxt.CXT_calMidTagsVal();
			}
		}

		CXT_calMidTagsValLocal();
	}
	
	
	public void DEF_renderJson(Writer w,boolean b_inc_sys,HashMap<String, Object> extpms) throws IOException
	{
		w.write("{\"id\":\"" + this.id + "\",\"n\":\"" + this.getName() + "\",\"t\":\""+Convert.plainToJsStr(this.getTitle())+"\",\"tp\":\""+this.getNodeTp()+"\"");
		if (extpms != null)
		{
			for (Map.Entry<String, Object> n2v : extpms.entrySet())
			{
				String tmpn = n2v.getKey();
				if ("id".contentEquals(tmpn) || "n".contentEquals(tmpn))
					throw new RuntimeException("extend pms cannot has name id and n");
				Object tmpv = n2v.getValue();
				if (tmpv instanceof Boolean || tmpv instanceof Number)
					w.write(",\"" + tmpn + "\":" + tmpv);
				else
					w.write(",\"" + tmpn + "\":\"" + tmpv + "\"");
			}
		}
		
		JSONObject jo = this.getExtAttrJO() ;
		if(jo!=null)
		{
			//w.write(",\"ext\":" + jo.toString() );
			UTIL_renderExtPropsByJO(w,jo,false) ; 
		}
		
		
		DEF_renderJsonTags(w, b_inc_sys) ;
		
		
		DEF_renderJsonSubs(w, b_inc_sys,extpms) ;
		
		w.write("}");
	}
	
	private void DEF_renderJsonTags(Writer w,boolean b_inc_sys) throws IOException
	{
		w.write(",\"tags\":[");
		boolean bfirst = true;
		List<UATag> tags = null;
		if(b_inc_sys)
			tags = this.listTags();
		else
			tags = this.getNorTags() ;
		for (UATag tg : tags)
		{
			if (!bfirst)
				w.write(",");
			else
				bfirst = false;
			tg.renderDefJson(w);
		}
		w.write("]");
	}
	
	private void DEF_renderJsonSubs(Writer w,boolean b_inc_sys,HashMap<String, Object> extpms) throws IOException
	{
		boolean bfirst;
		List<UANodeOCTagsCxt> subtgs = this.getSubNodesCxt() ;
		if(subtgs!=null&&subtgs.size()>0)
		{
			w.write(",\"subs\":[");
			bfirst = true;
			for (UANodeOCTagsCxt subtg : subtgs)
			{
				if (!bfirst)
					w.write(",");
				else
					bfirst = false;

				subtg.DEF_renderJson(w, b_inc_sys,extpms) ;
			}
			w.write("]");
		}
	}
	
	public void CXT_renderRTJsonFlat(Writer w,boolean b_sys) throws IOException
	{
		w.write("[");
		List<UATag> tags = null ;
		if(b_sys)
			tags = this.listTagsAll() ;
		else
			tags = this.listTagsNorAll() ;
		boolean bfirst = true ;
		for(UATag tag : tags)
		{
			if (!bfirst)
				w.write(",");
			else
				bfirst = false;
			
			JSONObject rt = tag.RT_toFlatJson() ;
			rt.write(w) ;
		}
		
		w.write("]");
	}
	
	public void CXT_renderDefJsonFlat(Writer w,boolean b_sys) throws IOException
	{
		w.write("[");
		List<UATag> tags = null ;
		if(b_sys)
			tags = this.listTagsAll() ;
		else
			tags = this.listTagsNorAll() ;
		boolean bfirst = true ;
		for(UATag tag : tags)
		{
			if (!bfirst)
				w.write(",");
			else
				bfirst = false;
			
			JSONObject tmpjo = tag.toDefJO() ;
			tmpjo.write(w) ;
		}
		
		w.write("]");
	}

	public void CXT_renderJson(Writer w) throws IOException
	{
		CXT_renderJson(w, null,null);
	}

//	public void CXT_renderJson(Writer w, HashMap<String, Object> extpms) throws IOException
//	{
//		CXT_renderJson(w, -1, extpms);
//	}
//
	public boolean CXT_renderJson(Writer w, long lastdt) throws IOException
	{
		return CXT_renderJson(w, null,lastdt, null);
	}

	public boolean CXT_renderJson(Writer w, HashMap<UATag,Long> tag2lastdt, HashMap<String, Object> extpms) throws IOException
	{
		return CXT_renderJson(w, tag2lastdt, -1,extpms);
	}
	
	public boolean CXT_renderJson(Writer w, HashMap<UATag,Long> tag2lastdt, long g_lastdt,HashMap<String, Object> extpms) throws IOException
	{
		return CXT_renderJson(w, tag2lastdt, g_lastdt,extpms,false) ; 
	}
	
	public boolean CXT_renderJson(Writer w, HashMap<UATag,Long> tag2lastdt, long g_lastdt,HashMap<String, Object> extpms,boolean ignore_sys_tag) throws IOException
	{
		return CXT_renderJson(w, tag2lastdt, g_lastdt,extpms,ignore_sys_tag,true) ;
	}

	public boolean CXT_renderJson(Writer w, HashMap<UATag,Long> tag2lastdt, long g_lastdt,HashMap<String, Object> extpms,boolean ignore_sys_tag,boolean inc_sub) throws IOException
	{
		boolean bchg=false;
		//long maxdt=-1 ;
		
		w.write("{\"id\":\"" + this.id + "\",\"n\":\"" + this.getName() + "\",\"t\":\""+this.getTitle()+"\",\"tp\":\""+this.getNodeTp()+"\"");
		if (extpms != null)
		{
			for (Map.Entry<String, Object> n2v : extpms.entrySet())
			{
				String tmpn = n2v.getKey();
				if ("id".contentEquals(tmpn) || "n".contentEquals(tmpn))
					throw new RuntimeException("extend pms cannot has name id and n");
				Object tmpv = n2v.getValue();
				if (tmpv instanceof Boolean || tmpv instanceof Number)
					w.write(",\"" + tmpn + "\":" + tmpv);
				else
					w.write(",\"" + tmpn + "\":\"" + tmpv + "\"");
			}
		}
		
		JSONObject jo = this.getExtAttrJO() ;
		if(jo!=null)
		{
			//w.write(",\"ext\":" + jo.toString() );
			UTIL_renderExtPropsByJO(w,jo,false) ; 
		}
		
		
		if(renderJsonTags(w, tag2lastdt, g_lastdt,ignore_sys_tag))
			bchg = true;
		
		if(inc_sub)
		{
			if(renderJsonSubs(w, tag2lastdt))
				bchg = true ;
		}
		
		JSONArray jarr = this.CXT_getAlertsJArr() ;
		if(jarr!=null && jarr.length()>0)
		{// alert_handlers/alert_items
			w.write(",\"has_alert\":true,\"alerts\":");
			jarr.write(w) ;
		}
		
		w.write("}");
		return bchg;
	}

	private boolean renderJsonSubs(Writer w, HashMap<UATag, Long> tag2lastdt) throws IOException
	{
		boolean bchg =false;
		boolean bfirst;
		List<UANodeOCTagsCxt> subtgs = this.getSubNodesCxt() ;
		if(subtgs!=null&&subtgs.size()>0)
		{
			w.write(",\"subs\":[");
			bfirst = true;
			for (UANodeOCTagsCxt subtg : subtgs)
			{
				if (!bfirst)
					w.write(",");
				else
					bfirst = false;

				if(subtg.CXT_renderJson(w, tag2lastdt, null))
					bchg=true;
			}
			w.write("]");
		}
		return bchg;
	}

	private boolean renderJsonTags(Writer w, HashMap<UATag, Long> tag2lastdt, long g_lastdt,boolean ignore_sys) throws IOException
	{
		boolean bchg=false;
		w.write(",\"tags\":[");
		boolean bfirst = true;
		List<UATag> tags = this.tags;
		if(!ignore_sys)
			tags = this.listTags();
		
		for (UATag tg : tags)
		{
			UAVal val = tg.RT_getVal();
			long dt_chg = -1;
			
			if (val != null)
				dt_chg = val.getValChgDT();
			
			if(tag2lastdt!=null)
			{
				Long lastdt = tag2lastdt.get(tg) ;
				if (lastdt!=null && lastdt > 0 && dt_chg>0 && dt_chg <= lastdt)
					continue;
				if(dt_chg<0 && lastdt!=null && lastdt<0)
					continue ;
				
				lastdt = dt_chg ;
				tag2lastdt.put(tg, lastdt);
			}
			else if(g_lastdt>0)
			{
				if (dt_chg <= g_lastdt)
					continue;
			}
			
			bchg=true;

			if (!bfirst)
				w.write(",");
			else
				bfirst = false;
			
			tg.renderJson(null,w);
		}
		w.write("]");
		return bchg;
	}

	public boolean CXT_renderTagsJson(Writer w, boolean bsys, long lastdt) throws IOException
	{
		List<UANodeOCTags> tns = this.listSelfAndSubTagsNode();

		w.write("[");
		boolean bfirst = true;
		for (UANodeOCTags tn : tns)
		{
			List<UATag> tags = null;
			if (bsys)
				tags = tn.listTags();
			else
				tags = tn.getNorTags();

			// String tn_id = tn.getId() ;
			// String tn_path = tn.getNodePath() ;
			for (UATag tg : tags)
			{
				long dt_chg = -1;
				
				
				UAVal val = tg.RT_getVal();

				if (val != null)
				{
								// Date(val.getValDT())) ;
					dt_chg = val.getValChgDT();// Convert.toFullYMDHMS(new
						
				}
				else
				{
					dt_chg = System.currentTimeMillis();
				}


				if (lastdt > 0 && dt_chg <= lastdt)
					continue;
				

				if (!bfirst)
					w.write(",");
				else
					bfirst = false;

				tg.CXT_renderTagJson(w) ;

			}
		}
		w.write("]");
		return true;
	}
	
	public List<ValEvent> CXT_listAlerts()
	{
		ArrayList<ValEvent> rets = new ArrayList<>() ;
		this.iteratorAllTags((tg)->{
			List<ValEvent> vas = tg.getValAlerts() ;
			if(vas==null)
				return ;
			for(ValEvent va:vas)
			{
				if(va.RT_is_triggered())
					rets.add(va) ;
			}
		});
		return rets ;
	}
	
	public JSONArray CXT_getAlertsJArr()
	{
		UAPrj prj= this.getBelongToPrj() ;
		
		JSONArray jarr = new JSONArray() ;
		AlertManager.getInstance(prj.getId()).getHandlers().forEach((id,ah)->{
			List<AlertItem> items = ah.RT_getAlertItems() ;
			if(items==null||items.size()<=0)
				return ;
			JSONObject ah_jo = null ;
			JSONArray ai_jarr = null ;
			for(AlertItem ai:items)
			{
				UATag tag = ai.getTag() ;
				if(!tag.isUnderNode(this))
					continue ;
				//
				if(ah_jo==null)
				{
					ah_jo = ah.RT_toJO() ;
					ai_jarr = new JSONArray() ;
				}
				
				ai_jarr.put(ai.toJO()) ;
			}
			if(ah_jo==null)
				return ;
			ah_jo.put("items", ai_jarr) ;
			jarr.put(ah_jo) ;
		});
		
		return jarr ;
	}
	
	public static List<JsProp> JS_getSysPropsCxt()
	{
		ArrayList<JsProp> jps = new ArrayList<>() ;
		jps.add(new JsProp("$sys",UAContext.sys,null,true,"system","System support func")) ;
		jps.add(new JsProp("$util",UAContext.util,null,true,"util","System util func")) ;
		jps.add(new JsProp("$debug",UAContext.debug,null,true,"system","System debug func")) ;
		HashMap<String,PlugJsApi> gvar2obj = PlugManager.getInstance().getJsApiAll();
		if(gvar2obj!=null)
		{
			for(Map.Entry<String, PlugJsApi> n2o:gvar2obj.entrySet())
			{
				String k = n2o.getKey();
				PlugJsApi jsapi= n2o.getValue() ;
				jps.add(new JsProp("$$"+k,jsapi,null,true,"plugin_"+k,jsapi.getDesc())) ;
			}
		}
		return jps;
	}
	

	private List<JsProp> globalPropsCxt = null ;
	
	/**
	 * get global props in this node as context
	 * @return
	 */
	protected List<JsProp> JS_getGlobalPropsCxt()
	{
		if(globalPropsCxt!=null)
			return globalPropsCxt ;
		
		ArrayList<JsProp> jps = new ArrayList<>() ;
		jps.addAll(JS_getSysPropsCxt()) ;
		
		UANode topn = this.getTopNode() ;
		if(topn instanceof UAPrj)
			jps.add(new JsProp("$prj",(UAPrj)topn,UAPrj.class,true,"project","Project obj in context")) ;
		UACh ch  = this.getBelongToCh() ;
		if(ch!=null)
			jps.add(new JsProp("$ch",ch,null,true,"Channel","Channel obj in context")) ;
		UADev dev = this.getBelongToDev() ;
		if(dev!=null)
			jps.add(new JsProp("$dev",dev,null,true,"Device","Device obj in context")) ;
		jps.add(new JsProp("$this",this,null,true,this.getTitle(),this.getDesc())) ;
		globalPropsCxt = jps;
		return jps ;
	}
	
	public Object JS_get(String key)
	{
		Object r = super.JS_get(key);
		if (r != null)
			return r;
		
		if(key.startsWith("$$"))
		{//JsApi
			String plug_n = key.substring(2) ;
			HashMap<String,PlugJsApi> gvar2obj = PlugManager.getInstance().getJsApiAll();
			if(gvar2obj==null)
				return null ;
			return gvar2obj.get(plug_n) ;
		}
		
		if(key.startsWith("$"))
		{
			switch(key)
			{
			case "$prj":
				return this.getBelongToPrj() ;
			case "$ch":
				return this.getBelongToCh() ;
			case "$dev":
				return this.getBelongToDev() ;
			case "$this":
				return this ;
			case "$sys":
				return UAContext.sys;
			case "$debug":
				return UAContext.debug;
			case "$util":
				return UAContext.util;
			}
			
			//env
			JsEnv env = JsEnv.getInThLoc();
			if(env!=null)
			{
				return env.JS_get(key) ;
			}
		}
		
		return null ;
	}
	
	private transient List<JsSub> js_cxt_root_subs = null ;
	/**
	 * this node will be a context, and get root props
	 * @return
	 */
	public final List<JsSub> JS_CXT_get_root_subs()
	{
		if(js_cxt_root_subs==null)
		{
			List<JsSub> rets = new ArrayList<>() ;
			List<JsProp> jps = JS_getGlobalPropsCxt();
			rets.addAll(jps) ;
			
			List<JsSub> subs = this.JS_get_subs() ;
			if(subs!=null)
			{
				for(JsSub sub:subs)
				{
					if(sub instanceof JsMethod || !sub.hasSub())
						continue ;// root has no method
					if(sub instanceof JsProp && ((JsProp)sub).isSysTag())
						continue ;
					rets.add(sub);
				}
			}
			js_cxt_root_subs = rets ;
		}
		
		JsEnv env = JsEnv.getInThLoc() ;
		if(env==null)
			return js_cxt_root_subs ;
		
		ArrayList<JsSub> rets = new ArrayList<>(js_cxt_root_subs);
		List<JsProp> jps = env.JS_get_props() ;
		if(jps!=null)
			rets.addAll(jps) ;
		return rets ;
	}
	
	public final List<JsSub> JS_CXT_get_root_subs(boolean no_parent,boolean no_this)
	{
		if(!no_parent)
			return JS_CXT_get_root_subs() ;
		
		List<JsSub> rets = new ArrayList<>() ;
		List<JsProp> jps = JS_getSysPropsCxt();
		rets.addAll(jps) ;
		if(!no_this)
			rets.add(new JsProp("$this",this,null,true,this.getTitle(),this.getDesc())) ;
		
		List<JsSub> subs = this.JS_get_subs() ;
		if(subs!=null)
		{
			for(JsSub sub:subs)
			{
				if(sub instanceof JsMethod || !sub.hasSub())
					continue ;// root has no method
				if(sub instanceof JsProp && ((JsProp)sub).isSysTag())
					continue ;
				rets.add(sub);
			}
		}
		
		JsEnv env = JsEnv.getInThLoc() ;
		if(env!=null)
		{
			jps = env.JS_get_props() ;
			if(jps!=null)
				rets.addAll(jps) ;
		}
		
		return rets ;
	}
	
	public final JsSub JS_CXT_get_root_sub(String name)
	{
		for(JsSub jp: JS_CXT_get_root_subs())
		{
			if(jp.getName().equals(name))
				return jp ;
		}
		return null ;
	}
	
	/**
	 * this node will be a context, and get sub prop by unique id
	 * @param sub_prop_id  like xxx.xx
	 * @return
	 */
	public final JsSubOb JS_CXT_get_sub_by_id(String sub_id)
	{
		if(Convert.isNullOrEmpty(sub_id))
			return null ;
		
		List<String> ss = Convert.splitStrWith(sub_id, ".") ;
		String rootn = ss.get(0) ;
		int n = ss.size() ;
		JsSub jp1 = JS_CXT_get_root_sub(rootn) ;
		if(jp1==null)
			return null ;
		Object pv = this.JS_get(rootn) ;
		if(n==1)
			return new JsSubOb(jp1,pv) ;

		JsSub jss = null ;
		for(int i = 1 ; i < n ; i ++)
		{
			if(pv==null || !(pv instanceof JSObMap))
				return null ;
			
			JSObMap pv_ob = (JSObMap)pv ; 
			
			String name = ss.get(i) ;
			jss = pv_ob.JS_get_sub(name) ;
			if(jss==null)
				return null ;
			pv = pv_ob.JS_get(name) ;
		}
		return new JsSubOb(jss,pv) ;
	}
	
	
	//
	@JsDef
	public List<UATag> RT_filterTagsSelf(Object val)
	{
		ArrayList<UATag> rets = new ArrayList<>() ;
		for(UATag tag : this.getNorTags())
		{
			UAVal v = tag.RT_getVal() ;
			if(v==null||!v.isValid())
				continue ;
			if(val.equals(v.getObjVal()))
				rets.add(tag) ;
		}
		return rets ;
	}
}
