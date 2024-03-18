package org.iottree.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.bind.BindDI;
import org.iottree.core.bind.EventBindItem;
import org.iottree.core.bind.PropBindItem;
import org.iottree.core.cxt.JsDef;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
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
		if (!subdir.exists())
			subdir.mkdirs();
		return new File(subdir, "hmi_" + this.getId() + ".txt");
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
					boolean bexp = bdob.optBoolean("exp");
					PropBindItem pbi = new PropBindItem(k, bexp, bdtxt);
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
					EventBindItem ebi = new EventBindItem(k, serverjs);
					ebis.add(ebi);
				}
			}

			if (pbis.size() > 0 || ebis.size() > 0)
			{
				pbs.add(new BindDI(itemid, pbis, ebis));
			}
		}

		binds = pbs;
		return pbs;
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
}
