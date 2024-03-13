package org.iottree.core.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.store.record.RecManager;
import org.iottree.core.store.record.RecPro;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 统一管理以窗口/或对话框形式的UI界面，每个界面可以有id,name,title等内容，
 * 后续内容根据 使用的内部资源 进行配置
 * 
 * 如，基于recorder提供图表界面支持。或者一些通用的基于多个标签的组合展示功能等。
 * 
 * 这些图表基础支持，加上此包中的统一配置管理，可以为前端UI提供丰富和强大的配置和定义——无需任何代码即可
 * 完成复杂UI界面的定义。
 * 
 * 后面又可以成为通用资源对外服务。
 * 
 * @author jason.zhu
 */
public class UIManager implements ILang
{
	private static final HashMap<String, UIManager> name2uim = new HashMap<>();

	public static UIManager getInstance(UAPrj prj)
	{
		String name = prj.getName();
		UIManager recm = name2uim.get(name);
		if (recm != null)
			return recm;

		synchronized (UIManager.class)
		{
			recm = name2uim.get(name);
			if (recm != null)
				return recm;

			recm = new UIManager(name);
			name2uim.put(name, recm);
			return recm;
		}
	}

	public static UIManager getInstance(String prjid)
	{
		UAPrj prj = UAManager.getInstance().getPrjById(prjid);
		if (prj == null)
			return null;
		return getInstance(prj);
	}
	
	String prjName = null;

	UAPrj prj = null;
	
	RecManager recmgr = null ;

	File prjDir = null;

	private UIManager(String prjname)
	{
		this.prjName = prjname;

		this.prj = UAManager.getInstance().getPrjByName(prjName);
		if (this.prj == null)
			throw new IllegalArgumentException("not prj found");

		recmgr = RecManager.getInstance(this.prj) ;
		prjDir = this.prj.getPrjSubDir();
	}
	
	public RecManager getRecMgr()
	{
		return this.recmgr ;
	}
	
	public List<IUITemp> listTempsAll()
	{
		return recmgr.UI_getTemps() ;
	}
	

//	private HashMap<String,IUITemp> name2temp = null;
//	
//	public HashMap<String,IUITemp> getName2Temp()
//	{
//		if(name2temp!=null)
//			return name2temp ;
//		
//		synchronized(UIManager.class)
//		{
//			if(name2temp!=null)
//				return name2temp ;
//			
//			name2temp = new HashMap<>() ;
//			
//			return name2temp ;
//		}
//	}
	
	public IUITemp getTempByName(String name)
	{
		for(IUITemp uit:listTempsAll())
		{
			if(name.equals(uit.getName()))
				return uit ;
		}
		return null ;
	}

	
	public List<IUITemp> listFitTemps(List<UATag> tags)
	{
		ArrayList<IUITemp> rets = new ArrayList<>() ;
		for(IUITemp uit : this.listTempsAll())
		{
			if(uit.checkTagsFitOrNot(tags))
				rets.add(uit) ;
		}
		return rets ;
	}
	
	public List<IUITemp> listFitTempsByTagId(String tagid)
	{
		UATag tag = prj.findTagById(tagid) ;
		if(tag==null)
			return null ;
		return this.listFitTemps(Arrays.asList(tag)) ;
	}
	
	public List<IUITemp> listFitTempsByTagIds(List<String> tagids)
	{
		if(tagids==null||tagids.size()<=0)
			return null ;
		ArrayList<UATag> tags = new ArrayList<>(tagids.size()) ;
		for(String tagid:tagids)
		{
			UATag tag = prj.findTagById(tagid) ;
			if(tag==null)
				return null ;
		}
		return this.listFitTemps(tags) ;
	}
	
	// ----------------------  ui item defs
	LinkedHashMap<String, UIItem> id2items = null;
	
	private File getItemsFile()
	{
		return new File(prjDir, "ui_items.json");
	}
	
	public LinkedHashMap<String, UIItem> getId2Items()
	{
		if(id2items!=null)
			return id2items ;
		synchronized(this)
		{
			if(id2items!=null)
				return id2items ;
			
			try
			{
				id2items = loadItems() ;
				return id2items ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null ;
			}
		}
	}
	
	public UIItem getItemById(String id)
	{
		return this.getId2Items().get(id) ;
	}
	
	public UIItem getItemByName(String name)
	{
		for(UIItem uii:this.getId2Items().values())
		{
			if(name.equals(uii.name))
				return uii ;
		}
		return null ;
	}
	
	private LinkedHashMap<String, UIItem> loadItems() throws Exception
	{
		LinkedHashMap<String, UIItem> rets = new LinkedHashMap<>();

		File f = getItemsFile();
		if (!f.exists())
			return rets;

		String txt = Convert.readFileTxt(f, "UTF-8");
		JSONArray jarr = new JSONArray(txt);
		int n = jarr.length();

		StringBuilder failedr = new StringBuilder();
		for (int i = 0; i < n; i++)
		{
			JSONObject jo = jarr.getJSONObject(i);
			UIItem rtp = UIItem.fromJO(this, jo);
			if (rtp == null)
			{
				//System.out.println(" Warn: loadRecPros error - " + failedr);
				continue;
			}
			rets.put(rtp.id, rtp);
		}
		return rets;
	}
	
	private void saveItems() throws IOException
	{
		LinkedHashMap<String, UIItem> id2p = getId2Items();
		if (id2p == null)
			return;

		File f = getItemsFile();
		JSONArray jarr = new JSONArray();

		for (UIItem tp : id2p.values())
		{
			jarr.put(tp.toJO());
		}
		String txt = jarr.toString();
		Convert.writeFileTxt(f, txt, "UTF-8");
	}
	
	private void setItem(UIItem rs) throws IOException
	{
		String id = rs.getId();
		if (Convert.isNullOrEmpty(id))
			rs.id = id = IdCreator.newSeqId();
		HashMap<String, UIItem> id2p = getId2Items();
		// boolean bdirty = false;

		id2p.put(id, rs);

		saveItems();
	}
	
	public boolean setItemByJSON(JSONObject jo, StringBuilder failedr) throws IOException
	{
		UIItem rp = UIItem.fromJO(this, jo);
		if (rp == null)
			return false;
		String id = rp.id;
		String n = rp.getName();
		if (!Convert.checkVarName(n, true, failedr))
			return false;
		boolean bnew = Convert.isNullOrEmpty(id);
		if (bnew)
		{
			UIItem oldrp = this.getItemByName(n);
			if (oldrp != null)
			{
				failedr.append(g("name_existed") + " - " + n);
				return false;
			}
		}
		else
		{
			UIItem oldrp = this.getItemById(id);
			UIItem oldrp1 = this.getItemByName(n);
			if (oldrp1 != null && oldrp1 != oldrp)
			{
				failedr.append(g("name_existed") + " - " + n);
				return false;
			}
		}

		setItem(rp);
		return true;
	}

	public boolean delItemById(String id) throws Exception
	{
		UIItem ao = this.getItemById(id);
		if (ao == null)
			return false;
		this.getId2Items().remove(id);
		this.saveItems();
		return true;
	}
}
