package org.iottree.core.store.record;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.store.StoreHandler;
import org.iottree.core.store.tssdb.TSSTagParam;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 支持标签基于内部存储tssdb进行相关的配置、数据管理和展示
 * 
 * @author jason.zhu
 */
public class RecManager
{
	private static final HashMap<String,RecManager> name2recm = new HashMap<>() ;
	
	public static RecManager getInstance(UAPrj prj)
	{
		String name = prj.getName();
		RecManager recm = name2recm.get(name) ;
		if(recm!=null)
			return recm ;
		
		synchronized(RecManager.class)
		{
			recm = name2recm.get(name) ;
			if(recm!=null)
				return recm ;
			
			recm = new RecManager(name) ;
			name2recm.put(name,recm) ;
			return recm ;
		}
	}
	
	String prjName = null ;
	
	UAPrj prj = null ;
	
	File prjDir = null ;
	
	private HashMap<String ,RecTagParam> tag2Params = null ;
	
	private TSSAdapterPrj tssAdpPrj = null ;
	
	private RecManager(String prjname)
	{
		this.prjName = prjname ;
		
		this.prj = UAManager.getInstance().getPrjByName(prjName) ;
		if(this.prj==null)
			throw new IllegalArgumentException("not prj found");
		
		prjDir = this.prj.getPrjSubDir() ;
		
		tssAdpPrj = new TSSAdapterPrj(this.prj) ;
	}
	
	public TSSAdapterPrj getTSSAdapterPrj()
	{
		return tssAdpPrj ;
	}
	
	private File getRecTagsFile()
	{
		return new File(prjDir,"rec_tags.json") ;
	}
	
	private HashMap<String,RecTagParam> loadRecTags() throws IOException	
	{
		HashMap<String,RecTagParam> rets = new HashMap<>();
		
		File f = getRecTagsFile() ;
		if(!f.exists())
			return rets ;
		
		String txt = Convert.readFileTxt(f, "UTF-8") ;
		JSONArray jarr = new JSONArray(txt) ;
		int n = jarr.length() ;
		
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			RecTagParam rtp = RecTagParam.fromJO(jo) ;
			if(rtp==null)
				continue ;
			rets.put(rtp.tag,rtp) ;
		}
		return rets ;
	}
	
	private void saveRecTags() throws IOException
	{
		HashMap<String,RecTagParam> tag2p = getRecTagParams() ;
		if(tag2p==null)
			return ;
		
		File f = getRecTagsFile() ;
		JSONArray jarr = new JSONArray() ;

		for(RecTagParam tp:tag2p.values())
		{
			jarr.put(tp.toJO()) ;
		}
		String txt = jarr.toString() ;
		Convert.writeFileTxt(f, txt, "UTF-8");
	}
	
	public HashMap<String,RecTagParam> getRecTagParams()
	{
		if(tag2Params!=null)
			return tag2Params ;
		
		synchronized(this)
		{
			if(tag2Params!=null)
				return tag2Params ;
			
			try
			{
				tag2Params = loadRecTags() ;
				return tag2Params;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null ;
			}
		}
	}
	
	public void setRecTagParam(String tag,RecTagParam rtp) throws IOException
	{
		HashMap<String,RecTagParam> tag2p = getRecTagParams() ;
		boolean bdirty = false;
		if(rtp==null)
		{
			bdirty = tag2p.remove(tag) !=null ;
		}
		else
		{
			tag2p.put(tag,rtp) ;
			bdirty = true ;
		}
		
		if(bdirty)
			saveRecTags();
	}
	
	public boolean checkTagCanRecord(UATag tag)
	{
		ValTP vtp = tag.getValTp() ;
		if(vtp==null)
			return false;
		return vtp.isNumberVT() || vtp==ValTP.vt_bool ;
	}
	
	public RecTagParam getRecTagParam(UATag tag)
	{
		return this.getRecTagParams().get(tag.getNodeCxtPathInPrj()) ;
	}
	
	private transient HashSet<String> recTagSet = null ;
	
	private List<TSSTagParam> createTagParams()
	{
		ArrayList<TSSTagParam> pms = new ArrayList<>() ;
		HashSet<String> tagset = new HashSet<>() ;
		for(RecTagParam rtp:this.getRecTagParams().values())
		{
			String tagp = rtp.getTag() ;
			UATag tag = this.prj.getTagByPath(tagp) ;
			if(tag==null)
				continue ;
			TSSTagParam ttp = new TSSTagParam(tagp,tag.getValTp(),rtp.gatherIntv) ;
			pms.add(ttp) ;
			tagset.add(tagp) ;
		}
		recTagSet= tagset ;
		return pms ;
	}
	
	public synchronized void RT_start()
	{
		if(tssAdpPrj.RT_isRunning())
			return ;

		tssAdpPrj.asTagParams(createTagParams()) ;
		tssAdpPrj.RT_start() ;
	}
	
	public synchronized void RT_stop()
	{
		tssAdpPrj.RT_stop();
	}
	
	public void fireUATagChanged(UATag tag)
	{
		
		if(!tssAdpPrj.RT_isRunning())
			return ;
		String tagp = tag.getNodeCxtPathInPrj() ;
		if(recTagSet==null||!recTagSet.contains(tagp))
			return ;
		UAVal uav = tag.RT_getVal() ;
		long dt = uav.getValDT() ;
		tssAdpPrj.addTagValue(tagp, dt, uav.isValid(), uav.getObjVal());
	}
	
	public void savePros() throws Exception
	{
		JSONObject jo = new JSONObject() ;
		JSONArray jarr = new JSONArray();
		jo.put("pros",jarr) ;
//		for (StoreHandler st : getId2Handler().values())
//		{
//			JSONObject tmpjo = st.toJO() ;
//			jarr.put(tmpjo);
//		}
		File f = new File(prjDir, "store_handlers.json");
		
		try(FileOutputStream fos= new FileOutputStream(f);OutputStreamWriter osw = new OutputStreamWriter(fos,"utf-8") ;)
		{
			jo.write(osw) ;
		}
	}
}
