package org.iottree.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.bind.BindDI;
import org.iottree.core.bind.EventBindItem;
import org.iottree.core.bind.PropBindItem;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * hmi can be defined in node cxt.
 * and it can be edited online in brw,with cxt tags to be binded.
 * 
 * @author jason.zhu
 */
@data_class
public class UAHmi extends UANodeOC implements IOCUnit,IRelatedFile
{
	@data_val(param_name = "tp")
	String hmiTp = "" ;
	
	
	public UAHmi()
	{}
	
	public UAHmi(String name,String title,String desc,String tp)
	{
		super(name,title,desc);
		//this.connTp = conntp ;
		hmiTp = tp ;
	}
	
	public UANodeOCTagsCxt getBelongTo()
	{
		return (UANodeOCTagsCxt)this.getParentNode() ;
	}
	
	protected void copyTreeWithNewSelf(IRoot root,UANode new_self,String ownerid,
			boolean copy_id,boolean root_subnode_id,
			HashMap<IRelatedFile,IRelatedFile> rf2new)
	{
		super.copyTreeWithNewSelf(root,new_self,ownerid,copy_id,root_subnode_id,rf2new);
		UAHmi self = (UAHmi)new_self ;
		self.hmiTp = this.hmiTp ;
		if(rf2new!=null)
			rf2new.put(this, self);
	}
	
	public String getHmiTp()
	{
		return hmiTp ;
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

	@Override
	protected void onPropNodeValueChged()
	{
		
	}

	File getHmiUIFile()
	{
		UAHmi rbhmi = (UAHmi)this.getRefBranchNode();
		if(rbhmi!=null)
		{
			return rbhmi.getHmiUIFile() ;
		}
		
		ISaver saver = (ISaver)this.getTopNode() ;
		
		File subdir = saver.getSaverDir();
		if(!subdir.exists())
			subdir.mkdirs() ;
		return new File(subdir,"hmi_"+this.getId()+".txt") ;
	}
	
	public File getRelatedFile()
	{
		ISaver rep = (ISaver)this.getTopNode() ;
		
		File subdir = rep.getSaverDir();
		if(!subdir.exists())
			subdir.mkdirs() ;
		return new File(subdir,"hmi_"+this.getId()+".txt") ;
	}
	
	private transient List<BindDI> binds = null ;
	
	public String loadHmiUITxt() throws IOException
	{
		File savef = getHmiUIFile();
		if(!savef.exists())
			return "" ;
		return Convert.readFileTxt(savef, "UTF-8") ;
	}
	
	public void saveHmiUITxt(String txt) throws FileNotFoundException, IOException
	{
		UAHmi rbhmi = (UAHmi)this.getRefBranchNode();
		if(rbhmi!=null)
			throw new IOException("hmi has refer branch node");
		
		File savef = getHmiUIFile();
		try(FileOutputStream fos = new FileOutputStream(savef))
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
		List<BindDI> bdis = getBinds() ;
		if(bdis==null)
			return null ;
		
		for(BindDI bdi:bdis)
		{
			if(diid.equals(bdi.getId()))
				return bdi ;
		}
		return null ;
	}
	
	
	public List<BindDI> getBinds()
	{
		if(binds!=null)
			return binds ;
		
		ArrayList<BindDI> pbs = new ArrayList<>() ;
		String txt = null;
		try
		{
			txt = loadHmiUITxt() ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(txt==null||(txt=txt.trim()).equals(""))
		{
			binds = pbs ;
			return pbs ;
		}
		
		JSONObject jobj = new JSONObject(txt) ;
		JSONArray jarr = jobj.optJSONArray("dis") ;
		if(jarr==null)
		{
			binds = pbs ;
			return pbs ;
		}
		
		int len = jarr.length();
		for(int i = 0 ; i< len ;i++)
		{
			JSONObject dijo = jarr.getJSONObject(i) ;
			String itemid = dijo.optString("id") ;
			
			ArrayList<PropBindItem> pbis = new ArrayList<>() ;
			ArrayList<EventBindItem> ebis = new ArrayList<>() ;
			
			JSONObject jo = dijo.optJSONObject("_prop_binder") ;
			if(jo!=null)
			{
				for(String k : jo.keySet())
				{
					JSONObject bdob = jo.optJSONObject(k);
					if(bdob==null)
						continue ;
					String bdtxt= bdob.optString("txt") ;
					if(bdtxt==null||bdtxt.equals(""))
						continue ;
					boolean bexp = bdob.optBoolean("exp") ;
					PropBindItem pbi = new PropBindItem(k, bexp, bdtxt) ;
					pbis.add(pbi) ;
				}
			}
			
			 jo = dijo.optJSONObject("_event_binder") ;
			if(jo!=null)
			{
				for(String k : jo.keySet())
				{
					JSONObject bdob = jo.optJSONObject(k);
					if(bdob==null)
						continue ;
					String serverjs= bdob.optString("serverjs") ;
					if(serverjs==null||serverjs.equals(""))
						continue ;
					EventBindItem ebi = new EventBindItem(k, serverjs) ;
					ebis.add(ebi) ;
				}
			}
			
			if(pbis.size()>0||ebis.size()>0)
			{
				pbs.add(new BindDI(itemid, pbis,ebis));
			}
		}
		
		binds = pbs ;
		return pbs ;
	}
	
//	
//	public void RT_getBindVal()
//	{
//		List<PropBindItem> items = getPropBindItems();
//		if(items==null||items.size()<=0)
//			return ;
//		UANodeOCTagsCxt ntags = this.getBelongTo() ;
//		for(PropBindItem pbi:items)
//		{
//			UAVal v = pbi.RT_getVal(ntags) ;
//		}
//		
//	}
	
	public boolean isMainInPrj()
	{
		UANode uan = this.getTopNode() ;
		if(!(uan instanceof UAPrj))
			return false;
		return this.getId().equals(((UAPrj)uan).getHmiMainId()) ;
	}
	
	public boolean setMainInPrj() throws Exception
	{
		UANode uan = this.getTopNode() ;
		if(!(uan instanceof UAPrj))
			return false;
		((UAPrj)uan).setHmiMainId(this.getId());
		return true;
	}
}
