package org.iottree.core.devtree;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;

/**
 * DevPart library
 * 
 * @author jason.zhu
 *
 */
public class DTDevPartLib implements Comparable<DTDevPartLib>
{
	static ILogger log = LoggerManager.getLogger(DTDevPartLib.class) ;
	
	String libId = null ;
	
	String title ;
	
	String desc ;
	
	private LinkedHashMap<String,DTDevPartTP> parttp_id2tp = null ;
	
	DTDevPartLib()
	{
		
	}
	
	DTDevPartLib(String t,String d)
	{
		this.libId = CompressUUID.createNewId() ;
		this.title = t ;
		this.desc = d ;
	}
	
	public String getLibId()
	{
		return this.libId ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public String getDesc()
	{
		return this.desc ;
	}
	
	public DTDevPartLib asBasic(String title,String desc)
	{
		this.title = title ;
		this.desc = desc ;
		return this ;
	}
	
	private File calcPartTPFile(DTDevPartTP ptp)
	{
		File libdir = DTDevPartManager.calLibDir(this.libId) ;
		return new File(libdir,"tp_"+ptp.getPartTpId()+".json") ;
	}
	
	public synchronized LinkedHashMap<String,DTDevPartTP> getPartTPsAll()
	{
		if(parttp_id2tp!=null)
			return parttp_id2tp;
		LinkedHashMap<String,DTDevPartTP> ret = new LinkedHashMap<>();
		File libdir = DTDevPartManager.calLibDir(this.libId) ;
		if(libdir.exists())
		{
			File[] ffs = libdir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File f)
				{
					if(!f.isFile())
						return false ;
					String fn = f.getName() ;
					return fn.startsWith("tp_") && fn.endsWith(".json");
				}});
			
			for(File ff:ffs)
			{
				try
				{
					JSONObject tmpjo = Convert.readFileJO(ff) ;
					DTDevPartTP tp = new DTDevPartTP(this) ;
					if(tp.fromJO(tmpjo))
					{
						tp.modifyDT = ff.lastModified();
						ret.put(tp.getPartTpId(),tp) ;
					}
				}
				catch(Exception ee)
				{
					if(log.isWarnEnabled())
						log.warn("load PartTP "+ff.getName()+" error "+ee.getMessage());
					if(log.isDebugEnabled())
						log.debug(ee);
				}
			}
		}
		return parttp_id2tp = ret ;
	}
	
	
	void savePartTP(DTDevPartTP ptp) throws IOException
	{
		File f = calcPartTPFile(ptp) ;
		//if(!f.getParentFile().exists())
		Convert.writeFileJO(f, ptp.toJO());
		ptp.modifyDT = System.currentTimeMillis() ;
	}
	
	
	public List<DTDevPartTP> listPartTPs()
	{
		ArrayList<DTDevPartTP> rets = new ArrayList<>() ;
		rets.addAll(getPartTPsAll().values()) ;
		return rets ;
	}
	
	public DTDevPartTP getPartTP(String parttpid)
	{
		return getPartTPsAll().get(parttpid) ;
	}
	
	public DTDevPartTP setPartTP(String parttpid,String title,String desc,StringBuilder failedr) throws IOException
	{
		if(Convert.isNotNullEmpty(parttpid))
		{
			DTDevPartTP tp = getPartTP(parttpid) ;
			if(tp==null)
			{
				failedr.append("no DevPartTP found with id="+parttpid) ;
				return null ;
			}
			
			tp.title = title ;
			savePartTP(tp) ; 
			tp.modifyDT = System.currentTimeMillis() ;
			return tp ;
		}
		
		DTDevPartTP tp = new DTDevPartTP(this,title,desc) ;
		savePartTP(tp) ; 
		getPartTPsAll().put(tp.getPartTpId(),tp) ;
		return tp ;
	}
	
	public DTDevPartTP addPartTPByTreeNode(String treeid,String tree_nid,String newtitle,StringBuilder failedr) throws IOException
	{
		DTNode dn = DTTreeManager.getInstance().getTreeNode(treeid, tree_nid) ;
		if(dn==null)
		{
			failedr.append("no tree node found") ;
			return null ;
		}
		DTDevPartTP tp = DTDevPartTP.createByCopy(this, dn) ;
		tp.name = null;
		if(Convert.isNotNullEmpty(newtitle))
			tp.title = newtitle ;
		savePartTP(tp) ; 
		getPartTPsAll().put(tp.getPartTpId(),tp) ;
		return tp ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().putOpt("t",this.title).putOpt("d", this.desc) ;
	}
	
	public boolean fromJO(String libid,JSONObject jo)
	{
		if(Convert.isNullOrEmpty(libid))
			return false;
		//DTDevPartLib ret = new DTDevPartLib() ;
		this.libId = libid ;
		this.title = jo.optString("t") ;
		this.desc = jo.optString("d") ;
		return true;
	}
	
	public void save() throws IOException
	{
		DTDevPartManager.getInstance().saveLib(this);
	}

	@Override
	public int compareTo(DTDevPartLib o)
	{
		return this.libId.compareTo(o.libId);
	}
}
