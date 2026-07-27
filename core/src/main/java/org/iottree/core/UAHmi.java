package org.iottree.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;

import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.bind.BindDI;
import org.iottree.core.bind.EventBindItem;
import org.iottree.core.bind.PropBindItem;
import org.iottree.core.cxt.JsDef;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.web.LoginUtil;
import org.iottree.core.util.web.LoginUtil.UserAuthItem;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.iottree.core.ws.WSServer;
import org.iottree.core.ws.WebSocketConfig;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * hmi can be defined in node cxt. and it can be edited online in brw,with cxt
 * tags to be binded.
 * 
 * @author jason.zhu
 */
@data_class
@JsDef(name = "hmi", title = "Hmi", desc = "Hmi Node", icon = "icon_hmi")
public class UAHmi extends UANodeOC implements IOCUnit, IRelatedFile
{
	public static final String NODE_TP = "hmi";

	@data_val(param_name = "tp")
	String hmiTp = "";

	@data_val(param_name = "conn_brk_ppt")
	String connBrkPrompt = "";

	@data_val(param_name = "not_run_ppt")
	String notRunPrompt = "";

	@data_val(param_name = "show_tags")
	String showTags = "";
	
	@data_val(param_name = "bk_color")
	String bkColor = "";
	
	@data_val(param_name = "is_3d")
	boolean b3D = false;

	public UAHmi()
	{
	}

	public UAHmi(String name, String title, String desc, String tp)
	{
		super(name, title, desc);
		// this.connTp = conntp ;
		hmiTp = tp;
	}

	public String getNodeTp()
	{
		return NODE_TP;
	}

	public UANodeOCTagsCxt getBelongTo()
	{
		return (UANodeOCTagsCxt) this.getParentNode();
	}

	protected void copyTreeWithNewSelf(IRoot root, UANode new_self, String ownerid, boolean copy_id,
			boolean root_subnode_id, HashMap<IRelatedFile, IRelatedFile> rf2new)
	{
		super.copyTreeWithNewSelf(root, new_self, ownerid, copy_id, root_subnode_id, rf2new);
		UAHmi self = (UAHmi) new_self;
		self.hmiTp = this.hmiTp;
		self.connBrkPrompt = this.connBrkPrompt;
		self.b3D = this.b3D ;
		if (rf2new != null)
			rf2new.put(this, self);
	}

	public String getHmiTp()
	{
		return hmiTp;
	}

	public String getConnBrokenPrompt()
	{
		return this.connBrkPrompt;
	}

	public String getNotRunPrompt()
	{
		return notRunPrompt;
	}
	
	public String getShowTagsTxt()
	{
		return this.showTags ;
	}
	
	public String getBkColor()
	{
		return this.bkColor ;
	}
	
	public boolean is3D()
	{
		return this.b3D ;
	}

	private List<PropGroup> hmiPGS = null;
	
	private transient LinkedHashMap<UATag,String> showTag2Title = null ;
	

	@Override
	protected void onPropNodeValueChged()
	{
		hmiPGS = null;
		showTag2Title = null ;
	}
	
	public LinkedHashMap<UATag,String> getShowTag2Title()
	{
		LinkedHashMap<UATag,String> ret = showTag2Title ;
		if(ret!=null)
			return ret ;
		
		ret = new LinkedHashMap<>() ;
		UANodeOCTagsCxt cxtn = this.getBelongTo() ;
		if(Convert.isNotNullEmpty(this.showTags))
		{
			JSONArray jarr = new JSONArray(this.showTags) ;
			int len = jarr.length() ;
			for(int i = 0 ; i < len ; i ++)
			{
				JSONObject jo = jarr.getJSONObject(i) ;
				String tag = jo.optString("tag") ;
				if(Convert.isNullOrEmpty(tag))
					continue ;
				
				UANode nn = cxtn.getDescendantNodeByPath(tag) ;
				if(nn==null)
					continue ;
				if(!(nn instanceof UATag))
					continue ;
				UATag tagn = (UATag)nn ;
				String tt = jo.optString("title") ;
				if(Convert.isNullOrEmpty(tt))
				{
					int k = tag.lastIndexOf('.') ;
					if(k>=0)
						tt = tag.substring(k+1) ;
					else
						tt = tag ;
				}
				ret.put(tagn, tt) ;
			}
		}
		
		this.showTag2Title = ret ;
		return ret ;
	}
	

	@Override
	public List<PropGroup> listPropGroups()
	{
		if (hmiPGS != null)
			return hmiPGS;
		ArrayList<PropGroup> pgs = new ArrayList<>();
		List<PropGroup> lpgs = super.listPropGroups();
		if (lpgs != null)
			pgs.addAll(lpgs);
		pgs.add(getHmiPropGroup());
		hmiPGS = pgs;
		return pgs;
	}

	private PropGroup getHmiPropGroup()
	{
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		PropGroup r = new PropGroup("hmi",lan);//, "HMI(UI)");
		
		r.addPropItem(new PropItem("conn_borken_prompt",lan,
				PValTP.vt_str, false, null, null, "")); // "Conn Broken Prompt", "Conn Broken Prompt Show in UI"
		r.addPropItem(new PropItem("not_run_prompt", lan,
				PValTP.vt_str, false, null, null, "")); //"Not Run Prompt", "Project is not run prompt Show in UI"

		r.addPropItem(new PropItem("show_tags", lan, PValTP.vt_str, false,
				null, null, "").withTxtMultiLine(true).withPop(PropItem.POP_N_SEL_TAGS));//, "Select Tags")); //"Show Tags", "Tags data will show in HMI client."
		// r.addPropItem(new PropItem("devid","Dev Id","Device
		// ID",PValTP.vt_str,false,null,null,""));
		
		r.addPropItem(new PropItem("bk_color",lan,
				PValTP.vt_str, false, null, null, ""));
		
		return r;
	}

	public Object getPropValue(String groupn, String itemn)
	{
		if ("hmi".contentEquals(groupn))
		{
			switch (itemn)
			{
			case "conn_borken_prompt":
				return this.connBrkPrompt;
			case "not_run_prompt":
				return notRunPrompt;
			case "show_tags":
				return this.showTags;
			case "bk_color":
				return bkColor;
			}
		}
		Object locv = super.getPropValue(groupn, itemn);

		return locv;
	}

	public boolean setPropValue(String groupn, String itemn, String strv)
	{
		if ("hmi".contentEquals(groupn))
		{
			switch (itemn)
			{
			case "conn_borken_prompt":
				this.connBrkPrompt = strv;
				return true;// do nothing
			case "not_run_prompt":
				notRunPrompt = strv;
				return true;
			case "show_tags":
				this.showTags = strv;
				return true;
			case "bk_color":
				this.bkColor = strv;
				return true;
			}
		}
		return super.setPropValue(groupn, itemn, strv);
	}

	@Override
	public String OCUnit_getUnitTemp()
	{
		return "hmi";
	}

	@Override
	protected boolean chkValid()
	{
		return true;
	}

	File getHmiUIFile()
	{
		UAHmi rbhmi = (UAHmi) this.getRefBranchNode();
		if (rbhmi != null)
		{
			return rbhmi.getHmiUIFile();
		}

		ISaver saver = (ISaver) this.getTopNode();

		File subdir = saver.getSaverDir();
		if (!subdir.exists())
			subdir.mkdirs();
		return new File(subdir, "hmi_" + this.getId() + ".txt");
	}

	public File getRelatedFile()
	{
		ISaver rep = (ISaver) this.getTopNode();

		File subdir = rep.getSaverDir();
//		if (!subdir.exists())
//			subdir.mkdirs();
		return new File(subdir, "hmi_" + this.getId() + ".txt");
	}
	
	public String getHmiFileName()
	{
		return "hmi_" + this.getId() + ".txt";
	}

	private transient List<BindDI> binds = null;

	public String loadHmiUITxt() throws IOException
	{
		File savef = getHmiUIFile();
		if (!savef.exists())
			return "";
		return Convert.readFileTxt(savef, "UTF-8");
	}

	public void saveHmiUITxt(String txt) throws FileNotFoundException, IOException
	{
		UAHmi rbhmi = (UAHmi) this.getRefBranchNode();
		if (rbhmi != null)
			throw new IOException("hmi has refer branch node");

		File savef = getHmiUIFile();
		try (FileOutputStream fos = new FileOutputStream(savef))
		{
			fos.write(txt.getBytes("utf-8"));
		}

		binds = null;
	}

	@Override
	public List<UANode> getSubNodes()
	{
		return null;
	}

	public void delFromParent() throws Exception
	{
		this.getBelongTo().delHmi(this);
	}

	public BindDI getBind(String diid)
	{
		List<BindDI> bdis = getBinds();
		if (bdis == null)
			return null;

		for (BindDI bdi : bdis)
		{
			if (diid.equals(bdi.getId()))
				return bdi;
		}
		return null;
	}

	public List<BindDI> getBinds()
	{
		if (binds != null)
			return binds;

		ArrayList<BindDI> pbs = new ArrayList<>();
		String txt = null;
		try
		{
			txt = loadHmiUITxt();
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}

		if (txt == null || (txt = txt.trim()).equals(""))
		{
			binds = pbs;
			return pbs;
		}

		JSONObject jobj = new JSONObject(txt);
		JSONArray jarr = jobj.optJSONArray("dis");
		if (jarr == null)
		{
			binds = pbs;
			return pbs;
		}

		int len = jarr.length();
		for (int i = 0; i < len; i++)
		{
			JSONObject dijo = jarr.getJSONObject(i);
			String itemid = dijo.optString("id");

			ArrayList<PropBindItem> pbis = new ArrayList<>();
			ArrayList<EventBindItem> ebis = new ArrayList<>();

			JSONObject jo = dijo.optJSONObject("_prop_binder");
			if (jo != null)
			{
				for (String k : jo.keySet())
				{
					JSONObject bdob = jo.optJSONObject(k);
					if (bdob == null)
						continue;
					String bdtxt = bdob.optString("txt");
					if (bdtxt == null || bdtxt.equals(""))
						continue;
					boolean bexp = "true".equals(bdob.optString("exp"));
					boolean need_tag_cached = false;
					if(bdob.has("need_tag_cached"))
					{
						need_tag_cached = "true".equals(bdob.optString("need_tag_cached"));
					}
					PropBindItem pbi = new PropBindItem(this,k, bexp, bdtxt,need_tag_cached);
					pbis.add(pbi);
				}
			}

			jo = dijo.optJSONObject("_event_binder");
			if (jo != null)
			{
				for (String k : jo.keySet())
				{
					JSONObject bdob = jo.optJSONObject(k);
					if (bdob == null)
						continue;
					String serverjs = bdob.optString("serverjs");
					if (serverjs == null || serverjs.equals(""))
						continue;
					String runname = bdob.optString("runname") ;
					EventBindItem ebi = new EventBindItem(this,k, serverjs,runname);
					ebis.add(ebi);
				}
			}

			if (pbis.size() > 0 || ebis.size() > 0)
			{
				pbs.add(new BindDI(this,itemid, pbis, ebis));
			}
		}

		binds = pbs;
		return pbs;
	}

	public List<PropBindItem> getPropBindItem_NeedTagCached()
	{
		ArrayList<PropBindItem> rets = new ArrayList<>() ;
		List<BindDI> bdis = this.getBinds() ;
		if(bdis==null)
			return rets ;
		for(BindDI bdi:bdis)
		{
			List<PropBindItem> pbis = bdi.getPropBindItems() ;
			if(pbis==null || pbis.size()<=0)
				continue ;
			for(PropBindItem pbi:pbis)
			{
				if(pbi.isNeedTagCached())
					rets.add(pbi) ;
			}
		}
		return rets ;
	}
	//
	// public void RT_getBindVal()
	// {
	// List<PropBindItem> items = getPropBindItems();
	// if(items==null||items.size()<=0)
	// return ;
	// UANodeOCTagsCxt ntags = this.getBelongTo() ;
	// for(PropBindItem pbi:items)
	// {
	// UAVal v = pbi.RT_getVal(ntags) ;
	// }
	//
	// }

	public boolean isMainInPrj()
	{
		UANode uan = this.getTopNode();
		if (!(uan instanceof UAPrj))
			return false;
		return this.getId().equals(((UAPrj) uan).getHmiMainId());
	}

	public boolean setMainInPrj() throws Exception
	{
		UANode uan = this.getTopNode();
		if (!(uan instanceof UAPrj))
			return false;
		((UAPrj) uan).setHmiMainId(this.getId());
		return true;
	}
	
	public static class ClientEvent
	{
		public final UAHmi hmi ;
		public final OperUser operUser;
		public final String diid;
		public final String eventn;
		public final String runn;
		public final String val;
		
		public ClientEvent(UAHmi hmi,OperUser oper_user,String diid, String eventn, String runn,String val)
		{
			this.hmi = hmi ;
			this.operUser = oper_user ;
			this.diid = diid ;
			this.eventn = eventn ;
			this.runn = runn ;
			this.val = val ;
		}
		
		public String getUserName()
		{
			if(this.operUser==null)
				return "" ;
			return this.operUser.name;
		}
		
		public String getHmiPath()
		{
			return this.hmi.getNodeCxtPathInPrj() ;
		}
		
		public String getDIId()
		{
			if(diid==null)
				return "";
			return diid ;
		}
		
		public String getRunName()
		{
			if(runn==null)
				return "";
			return runn ;
		}
		
		public String getValStr()
		{
			if(val==null)
				return "";
			return val ;
		}
		
		public JSONObject toJO()
		{
			JSONObject ret = new JSONObject().put("hmi", hmi.getNodeCxtPathInPrj())
					.putOpt("diid", this.diid).putOpt("event", this.eventn).put("run", this.runn).putOpt("val", val) ;
			if(operUser!=null)
				ret.put("user", operUser.name) ;
			return ret;
		}
		
		@Override
		public String toString()
		{
			String ret = "ClientEvent ";
			if(this.operUser!=null)
				ret += " user:"+operUser.name ;
			ret += " diid:"+diid ;
			ret += " event:"+eventn ;
			ret += " run:"+runn ;
			ret += " val:"+val ;
			return ret ;
		}
	}
	
	
	public static class OperUser
	{
		public String name ;
		
		public String nameDis ;
		
		public HashSet<String> roles ;
		
		public long authDT = System.currentTimeMillis() ;
		
		public boolean bPrjOper = false;
		
		OperUser(UserAuthItem u)
		{
			this.name = u.getUserName() ;
			this.nameDis = u.getDisName() ;
			List<String> rns = u.getRoleNames() ;
			if(rns!=null&&rns.size()>0)
			{
				this.roles = new HashSet<>() ;
				this.roles.addAll(rns);
			}
		}
		
		OperUser(String prj_oper)
		{
			bPrjOper = true;
			this.name = this.nameDis = prj_oper ;
		}
		
		public boolean hasRole(String rolen)
		{
			if("admin".equals(name))
				return true ;
			if(this.roles==null)
				return false;
			if(this.roles.contains("admin"))
				return true;
			return this.roles.contains(rolen) ;
		}
		
		public JSONObject toJO()
		{
			JSONObject ret = new JSONObject().put("n", this.name).putOpt("disn", this.nameDis)
					.put("auth_dt", this.authDT).put("prj_oper", bPrjOper);
			if(roles!=null)
				ret.put("roles",roles) ;
			return ret ;
		}
	}
	
	
	public boolean OPER_checkWriteUserRight(UAHmi.OperUser operuser)
	{
		HashSet<String> rs = getWriteRolesUsed() ;
		if(rs==null||rs.size()<=0)
			return true ;
		
		if(operuser==null)
			return false;//
		
		if(operuser.bPrjOper)
			return true ; //
		
		for(String r:rs)
		{
			if(operuser.hasRole(r))
				return true;
		}
		return false;
	}


	public boolean OPER_onOperEvent(WSServer.SessionItem<UAHmi.OperUser> si,String diid, String eventn, String val,StringBuilder failedr)
	{
		UAHmi.OperUser oper_user = si.getLoginSession();
		if(oper_user==null)
		{
			String k = OPER_getHmiKey(this.getBelongTo().getBelongToPrj(),this) ;
			oper_user = (UAHmi.OperUser)si.getHttpSession().getAttribute(k) ;
		}
		
		if(!this.OPER_checkWriteUserRight(oper_user))
		{
			failedr.append("no hmi write right") ;
			return false;
		}
		
		BindDI bdi = this.getBind(diid);
		if (bdi == null)
		{
			failedr.append("no bind di found") ;
			return false;
		}
		EventBindItem ebi = bdi.getEventBindItem(eventn);
		boolean ret = ebi.RT_runEventJS(this.getBelongTo(), val,failedr);
		if(!ret)
			return false;
		UAPrj prj = this.getBelongTo().getBelongToPrj() ;
		if(prj==null)
			return ret ;
		
		UAHmi.ClientEvent ce = new UAHmi.ClientEvent(this,oper_user,diid, eventn,ebi.getRunName(), val);
		if(log.isTraceEnabled())
			log.trace(ce.toString());
		
		prj.RT_onHmiEvent(ce);
		return ret ;
	}
	
	/**
	 * hmi write operation (send manual cmds) use another way,except user psw checking
	 * it's run on ROOT/hmi_ajax.jsp,so session obj is limited in this webapp
	 * 
	 * session keep obj is not same with normal page
	 * 
	 * @author jason.zhu
	 */

	private static final String OPER_AUTH = "_OPER_AUTH_" ;
	
	public static OperUser OPER_checkSessionAuthOk(EndpointConfig config,UAPrj prj,UAHmi hmi)
	{
		HttpSession hs = WebSocketConfig.getHttpSession(config) ;
		return OPER_checkSessionAuthOk(hs,prj,hmi) ;
	}
	
	public static OperUser OPER_getSessionUser(HttpSession hs,UAPrj prj,UAHmi hmi)
	{
		String k = OPER_getHmiKey(prj,hmi) ;
		return (OperUser)hs.getAttribute(k) ;
	}
	
	public static OperUser OPER_checkSessionAuthOk(HttpSession hs,UAPrj prj,UAHmi hmi)
	{
		String k = OPER_getHmiKey(prj,hmi) ;
		OperUser ou = (OperUser)hs.getAttribute(k) ;
		if(ou==null)
			return null ;
		if(System.currentTimeMillis() - ou.authDT < prj.getOperPermDurSec()*1000)
			return ou;
		return null ;
	}
	
	public static OperUser OPER_loginSessionAuth(HttpSession hs,UAPrj prj,UAHmi hmi,String user,String psw) throws Exception
	{
		if(prj.checkOperator(user, psw))
		{
			OperUser ou = new OperUser(user) ;
			hs.setAttribute(OPER_getHmiKey(prj,hmi), ou);
			return ou ;
		}
		
		UserAuthItem u =LoginUtil.checkUserPsw(user, psw);
		if(u!=null)
		{
			OperUser ou = new OperUser(u) ;
			hs.setAttribute(OPER_getHmiKey(prj,hmi), ou);
			return ou ;
		}
		return null ;
	}
	
	private static String OPER_getHmiKey(UAPrj prj,UAHmi hmi)
	{
		return OPER_AUTH+prj.getId()+"_"+hmi.getId() ;
		}
}
