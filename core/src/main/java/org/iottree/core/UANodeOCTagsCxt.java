package org.iottree.core;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.JSObMap;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.cxt.UARtSystem;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;

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
	private static final long serialVersionUID = 7775465863975907067L;

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
	protected void copyTreeWithNewSelf(UANode new_self, String ownerid, boolean copy_id, boolean root_subnode_id)
	{
		super.copyTreeWithNewSelf(new_self, ownerid, copy_id, root_subnode_id);
		UANodeOCTagsCxt self = (UANodeOCTagsCxt) new_self;
		self.hmis.clear();
		for (UAHmi hmi : hmis)
		{
			UAHmi nt = new UAHmi();
			if (root_subnode_id)
				nt.id = this.getNextIdByRoot();
			hmi.copyTreeWithNewSelf(nt, ownerid, copy_id, root_subnode_id);
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

		UAHmi ch = getHmiByName(name);
		if (ch != null && ch != hmi)
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

	public void CXT_renderJson(Writer w) throws IOException
	{
		CXT_renderJson(w, -1);
	}

	public void CXT_renderJson(Writer w, HashMap<String, Object> extpms) throws IOException
	{
		CXT_renderJson(w, -1, extpms);
	}

	public boolean CXT_renderJson(Writer w, long lastdt) throws IOException
	{
		return CXT_renderJson(w, lastdt, null);
	}

	public boolean CXT_renderJson(Writer w, long lastdt, HashMap<String, Object> extpms) throws IOException
	{
		boolean bchged = false;
		w.write("{\"id\":\"" + this.id + "\",\"n\":\"" + this.getName() + "\"");
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
		w.write(",\"tags\":[");
		boolean bfirst = true;
		List<UATag> tags = this.listTags();
		for (UATag tg : tags)
		{
			UAVal val = tg.RT_getVal();
			// if(val==null)
			// continue ;

			boolean bvalid = false;
			Object v = null;
			String strv = "";
			long dt = -1;
			long dt_chg = -1;
			String str_err = "";

			if (val != null)
			{
				bvalid = val.isValid();
				v = val.getObjVal();
				strv = val.getStrVal(tg.getDecDigits());
				dt = val.getValDT();// Convert.toFullYMDHMS(new
									// Date(val.getValDT())) ;
				dt_chg = val.getValChgDT();// Convert.toFullYMDHMS(new
											// Date(val.getValChgDT())) ;
				str_err = val.getErr();
				if (str_err == null)
					str_err = "";
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
			// w.write("\""+tg.getName()+"\":");
			w.write("{\"n\":\"");
			w.write(tg.getName());
			ValTP vtp = tg.getValTp();
			if (bvalid)
			{
				if (vtp.isNumberVT() || vtp == ValTP.vt_bool)
					w.write("\",\"valid\":" + bvalid + ",\"v\":" + strv + ",\"strv\":\"" + strv + "\",\"dt\":" + dt
							+ ",\"chgdt\":" + dt_chg + "}");
				else
					w.write("\",\"valid\":" + bvalid + ",\"v\":\"" + strv + "\",\"strv\":\"" + strv + "\",\"dt\":" + dt
							+ ",\"chgdt\":" + dt_chg + "}");
			}
			else
			{
				w.write("\",\"valid\":" + bvalid + ",\"v\":null,\"dt\":" + dt + ",\"chgdt\":" + dt_chg + ",\"err\":\""
						+ str_err + "\"}");
			}

			bchged = true;
		}
		w.write("],\"subs\":[");

		bfirst = true;
		for (UANodeOCTagsCxt subtg : this.getSubNodesCxt())
		{
			if (!bfirst)
				w.write(",");
			else
				bfirst = false;
			// w.write("\""+subtg.getName()+"\":");

			if (subtg.CXT_renderJson(w, lastdt, null))
				bchged = true;
		}
		w.write("]}");

		return bchged;
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
				String cxtpath = tg.getNodeCxtPathIn(this);
				boolean bloc = tg.getParentNode() == this;

				UAVal val = tg.RT_getVal();

				boolean bvalid = false;
				// Object v=null ;
				String strv = "";
				long dt = -1;
				long dt_chg = -1;
				String str_err = "";

				if (val != null)
				{
					bvalid = val.isValid();
					// v = val.getObjVal() ;
					strv = val.getStrVal(tg.getDecDigits());
					dt = val.getValDT();// Convert.toFullYMDHMS(new
										// Date(val.getValDT())) ;
					dt_chg = val.getValChgDT();// Convert.toFullYMDHMS(new
												// Date(val.getValChgDT())) ;
					str_err = val.getErr();
					if (str_err == null)
						str_err = "";
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

				w.write("{\"p\":\"" + cxtpath + "\",\"t\":\"" + tg.getTitle() + "\",\"vt\":\"" + tg.getValTp() + "\"");

				ValTP vtp = tg.getValTp();
				if (bvalid)
				{
					if (vtp.isNumberVT() || vtp == ValTP.vt_bool)
						w.write(",\"valid\":" + bvalid + ",\"v\":" + strv + ",\"strv\":\"" + strv + "\",\"dt\":" + dt
								+ ",\"chgdt\":" + dt_chg + "}");
					else
						w.write(",\"valid\":" + bvalid + ",\"v\":\"" + strv + "\",\"strv\":\"" + strv + "\",\"dt\":"
								+ dt + ",\"chgdt\":" + dt_chg + "}");
				}
				else
				{
					w.write(",\"valid\":" + bvalid + ",\"v\":null,\"dt\":" + dt + ",\"chgdt\":" + dt_chg + ",\"err\":\""
							+ str_err + "\"}");
				}

			}
		}
		w.write("]");
		return true;
	}
}
