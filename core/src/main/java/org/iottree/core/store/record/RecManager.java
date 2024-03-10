package org.iottree.core.store.record;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.store.StoreHandler;
import org.iottree.core.store.tssdb.TSSAdapter;
import org.iottree.core.store.tssdb.TSSSavePK;
import org.iottree.core.store.tssdb.TSSTagParam;
import org.iottree.core.store.tssdb.TSSTagSegs;
import org.iottree.core.store.tssdb.TSSValSeg;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 支持标签基于内部存储tssdb进行相关的配置、数据管理和展示
 * 
 * @author jason.zhu
 */
public class RecManager implements ILang
{
	private static final HashMap<String, RecManager> name2recm = new HashMap<>();

	public static RecManager getInstance(UAPrj prj)
	{
		String name = prj.getName();
		RecManager recm = name2recm.get(name);
		if (recm != null)
			return recm;

		synchronized (RecManager.class)
		{
			recm = name2recm.get(name);
			if (recm != null)
				return recm;

			recm = new RecManager(name);
			name2recm.put(name, recm);
			return recm;
		}
	}

	public static RecManager getInstance(String prjid)
	{
		UAPrj prj = UAManager.getInstance().getPrjById(prjid);
		if (prj == null)
			return null;
		return getInstance(prj);
	}

	String prjName = null;

	UAPrj prj = null;

	File prjDir = null;

	private HashMap<String, RecTagParam> tagPath2Params = null;

	private HashMap<String, RecSaver> name2savers = null;

	private LinkedHashMap<String, RecPro> id2pros = null;

	private TSSAdapterPrj tssAdpPrj = null;

	private transient HashSet<String> recTagSet = null;

	private RecManager(String prjname)
	{
		this.prjName = prjname;

		this.prj = UAManager.getInstance().getPrjByName(prjName);
		if (this.prj == null)
			throw new IllegalArgumentException("not prj found");

		prjDir = this.prj.getPrjSubDir();

		tssAdpPrj = new TSSAdapterPrj(this.prj);
		// tssAdpPrj.asSavedListener(tssSavedLis) ;
		tssAdpPrj.asPkSavedListener(tssPkSavedLis);
	}

	public TSSAdapterPrj getTSSAdapterPrj()
	{
		return tssAdpPrj;
	}

	private File getRecTagsFile()
	{
		return new File(prjDir, "rec_tags.json");
	}

	private HashMap<String, RecTagParam> loadRecTags() throws IOException
	{
		HashMap<String, RecTagParam> rets = new HashMap<>();

		File f = getRecTagsFile();
		if (!f.exists())
			return rets;

		String txt = Convert.readFileTxt(f, "UTF-8");
		JSONArray jarr = new JSONArray(txt);
		int n = jarr.length();

		for (int i = 0; i < n; i++)
		{
			JSONObject jo = jarr.getJSONObject(i);
			RecTagParam rtp = RecTagParam.fromJO(this, jo);
			if (rtp == null)
				continue;
			rets.put(rtp.tagPath, rtp);
		}
		return rets;
	}

	private void saveRecTags() throws IOException
	{
		HashMap<String, RecTagParam> tag2p = getRecTagParams();
		if (tag2p == null)
			return;

		File f = getRecTagsFile();
		JSONArray jarr = new JSONArray();

		for (RecTagParam tp : tag2p.values())
		{
			jarr.put(tp.toJO());
		}
		String txt = jarr.toString();
		Convert.writeFileTxt(f, txt, "UTF-8");
	}

	public HashMap<String, RecTagParam> getRecTagParams()
	{
		if (tagPath2Params != null)
			return tagPath2Params;

		synchronized (this)
		{
			if (tagPath2Params != null)
				return tagPath2Params;

			try
			{
				tagPath2Params = loadRecTags();
				return tagPath2Params;
			}
			catch ( Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}

	public List<RecTagParam> listRecTagParams()
	{
		HashMap<String, RecTagParam> pms = getRecTagParams();
		ArrayList<RecTagParam> rets = new ArrayList<>(pms.size());
		rets.addAll(pms.values());
		Collections.sort(rets);
		return rets;
	}

	public void setRecTagParam(String tag, RecTagParam rtp) throws IOException
	{
		HashMap<String, RecTagParam> tag2p = getRecTagParams();
		
		RecTagParam oldrtp = tag2p.get(tag) ;
		boolean bdirty = false;
		if (rtp == null)
		{
			bdirty = tag2p.remove(tag) != null;
		}
		else
		{
			tag2p.put(tag, rtp);
			bdirty = true;
		}
		

		if (bdirty)
			saveRecTags();
		
		HashSet<String> proids = new HashSet<>() ;
		if(oldrtp!=null)
		{
			List<String> ss= oldrtp.getUsingProIds() ;
			if(ss!=null)
				proids.addAll(ss) ;
		}
		if(rtp!=null)
		{
			List<String> ss= rtp.getUsingProIds() ;
			if(ss!=null)
				proids.addAll(ss) ;
		}
		for(String proid:proids)
		{
			RecPro rp = this.getRecProById(proid) ;
			if(rp==null)
				continue ;
			rp.clearCache();
		}
	}
	

	public void setRecProL1SelTagIds(String id, List<String> tagids) throws Exception
	{
		RecPro ao = this.getRecProById(id);
		if (ao == null || !(ao instanceof RecProL1))
			throw new Exception("no RecPro or not RecProL1 with id=" + id);

		RecProL1 rpl1 = (RecProL1) ao;

		boolean bdirty = false;
		for (RecTagParam rtp : this.getRecTagParams().values())
		{
			String tagid = rtp.tagId;
			if (tagids != null && tagids.contains(tagid))
			{
				if (rtp.setUsingProId(id))
					bdirty = true;
			}
			else
			{
				if (rtp.unsetUsingProId(id))
					bdirty = true;
			}
		}
		// this.saveRecPros();

		if (bdirty)
		{
			this.saveRecTags();
			rpl1.clearCache();
		}
	}
	
	List<UATag> listRecProUsingTags(String id)
	{
		RecPro ao = this.getRecProById(id);
		if (ao == null || !(ao instanceof RecProL1))
			return null;//throw new Exception("no RecPro or not RecProL1 with id=" + id);

		RecProL1 rpl1 = (RecProL1) ao;

		ArrayList<UATag> rets = new ArrayList<>() ;
		for (RecTagParam rtp : this.getRecTagParams().values())
		{
			List<String> proids = rtp.getUsingProIds() ;
			if(proids==null)
				continue ;
			if(proids.contains(id))
			{
				String tagid = rtp.tagId;
				
				UANode n = this.prj.findNodeById(tagid);
				if(n==null || !(n instanceof UATag))
					continue ;
				rets.add((UATag)n) ;
			}
		}
		return rets ;
	}
	//

	public boolean checkTagCanRecord(UATag tag)
	{
		ValTP vtp = tag.getValTp();
		if (vtp == null)
			return false;
		return vtp.isNumberVT() || vtp == ValTP.vt_bool;
	}

	public RecTagParam getRecTagParam(UATag tag)
	{
		return this.getRecTagParams().get(tag.getNodeCxtPathInPrj());
	}

	public List<RecProL1> listFitRecProsByValStyle(RecValStyle rvs)
	{
		ArrayList<RecProL1> rets = new ArrayList<>() ;
		for(RecPro rp:this.getId2RecPro().values())
		{
			if(!(rp instanceof RecProL1))
				continue ;
			RecProL1 rpl1 = (RecProL1)rp ;
			List<RecValStyle> vss = rpl1.getSupportedValStyle() ;
			if(vss==null)
				continue ;
			if(vss.contains(rvs))
				rets.add(rpl1) ;
		}
		return rets ;
	}
	// saver mgr

	private File getRecSaversFile()
	{
		return new File(prjDir, "rec_savers.json");
	}

	public HashMap<String, RecSaver> getSavers()
	{
		if (name2savers != null)
			return name2savers;

		synchronized (this)
		{
			if (name2savers != null)
				return name2savers;

			try
			{
				return name2savers = loadSavers();
			}
			catch ( Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}

	private HashMap<String, RecSaver> loadSavers() throws Exception
	{
		HashMap<String, RecSaver> rets = new HashMap<>();

		File f = getRecSaversFile();
		if (!f.exists())
			return rets;

		String txt = Convert.readFileTxt(f, "UTF-8");
		JSONArray jarr = new JSONArray(txt);
		int n = jarr.length();

		for (int i = 0; i < n; i++)
		{
			JSONObject jo = jarr.getJSONObject(i);
			RecSaver rtp = RecSaver.fromJO(jo);
			if (rtp == null)
				continue;
			rets.put(rtp.name, rtp);
		}
		return rets;
	}

	private void saveSavers() throws IOException
	{
		HashMap<String, RecSaver> n2s = getSavers();
		if (n2s == null)
			return;

		File f = getRecSaversFile();
		JSONArray jarr = new JSONArray();

		for (RecSaver tp : n2s.values())
		{
			jarr.put(tp.toJO());
		}
		String txt = jarr.toString();
		Convert.writeFileTxt(f, txt, "UTF-8");
	}

	public void setSaver(String name, RecSaver rs) throws IOException
	{
		HashMap<String, RecSaver> n2s = getSavers();
		boolean bdirty = false;
		if (rs == null)
		{
			bdirty = n2s.remove(name) != null;
		}
		else
		{
			n2s.put(name, rs);
			bdirty = true;
		}

		if (bdirty)
			saveSavers();
	}

	// end of saver mgr

	// recorder

	private File getRecProsFile()
	{
		return new File(prjDir, "rec_pros.json");
	}

	public LinkedHashMap<String, RecPro> getId2RecPro()
	{
		if (id2pros != null)
			return id2pros;

		synchronized (this)
		{
			if (id2pros != null)
				return id2pros;

			try
			{
				return id2pros = loadRecPros();
			}
			catch ( Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}

	public RecPro getRecProById(String id)
	{
		return getId2RecPro().get(id);
	}

	public RecPro getRecProByName(String name)
	{
		for (RecPro rp : this.getId2RecPro().values())
		{
			if (name.equals(rp.name))
				return rp;
		}
		return null;
	}

	private LinkedHashMap<String, RecPro> loadRecPros() throws Exception
	{
		LinkedHashMap<String, RecPro> rets = new LinkedHashMap<>();

		File f = getRecProsFile();
		if (!f.exists())
			return rets;

		String txt = Convert.readFileTxt(f, "UTF-8");
		JSONArray jarr = new JSONArray(txt);
		int n = jarr.length();

		StringBuilder failedr = new StringBuilder();
		for (int i = 0; i < n; i++)
		{
			JSONObject jo = jarr.getJSONObject(i);
			RecPro rtp = RecPro.fromJO(this, jo, failedr);
			if (rtp == null)
			{
				System.out.println(" Warn: loadRecPros error - " + failedr);
				continue;
			}
			rets.put(rtp.id, rtp);
		}
		return rets;
	}

	private void saveRecPros() throws IOException
	{
		LinkedHashMap<String, RecPro> id2p = getId2RecPro();
		if (id2p == null)
			return;

		File f = getRecProsFile();
		JSONArray jarr = new JSONArray();

		for (RecPro tp : id2p.values())
		{
			jarr.put(tp.toJO());
		}
		String txt = jarr.toString();
		Convert.writeFileTxt(f, txt, "UTF-8");
	}

	private void setRecPro(RecPro rs) throws IOException
	{
		String id = rs.getId();
		if (Convert.isNullOrEmpty(id))
			rs.id = id = IdCreator.newSeqId();
		HashMap<String, RecPro> id2p = getId2RecPro();
		// boolean bdirty = false;

		id2p.put(id, rs);

		saveRecPros();
	}

	public boolean setRecProByJSON(JSONObject jo, StringBuilder failedr) throws IOException
	{
		RecPro rp = RecPro.fromJO(this, jo, failedr);
		if (rp == null)
			return false;
		String id = rp.id;
		String n = rp.getName();
		if (!Convert.checkVarName(n, true, failedr))
			return false;
		boolean bnew = Convert.isNullOrEmpty(id);
		if (bnew)
		{
			RecPro oldrp = this.getRecProByName(n);
			if (oldrp != null)
			{
				failedr.append(g("name_existed") + " - " + n);
				return false;
			}
		}
		else
		{
			RecPro oldrp = this.getRecProById(id);
			RecPro oldrp1 = this.getRecProByName(n);
			if (oldrp1 != null && oldrp1 != oldrp)
			{
				failedr.append(g("name_existed") + " - " + n);
				return false;
			}
		}

		setRecPro(rp);
		return true;
	}

	public boolean delRecProById(String id) throws Exception
	{
		RecPro ao = this.getRecProById(id);
		if (ao == null)
			return false;
		this.getId2RecPro().remove(id);
		this.saveRecPros();
		return true;
	}


	// runtime

	private boolean RT_init(StringBuilder failedr)
	{
		ArrayList<TSSTagParam> pms = new ArrayList<>();
		HashSet<String> tagset = new HashSet<>();
		for (RecTagParam rtp : this.getRecTagParams().values())
		{
			String tagp = rtp.getTagPath();
			UATag tag = rtp.getUATag();// this.prj.getTagByPath(tagp) ;
			if (tag == null)
				continue;
			TSSTagParam ttp = new TSSTagParam(tagp, tag.getValTp(), rtp.getUsingGatherIntv(), rtp.minRecordIntv);
			pms.add(ttp);
			tagset.add(tagp);

			//rtp.asUATag(tag);
		}

		recTagSet = tagset;
		tssAdpPrj.asTagParams(pms);
		if (!tssAdpPrj.RT_init(true, failedr)) // make sure to re init
			return false;

		for (RecTagParam rtp : this.getRecTagParams().values())
		{
			String tagp = rtp.getTagPath();
			TSSTagSegs<?> ts = tssAdpPrj.getTagSegs(tagp);
			rtp.asTSSTagSegs(ts);
		}

		for (RecPro rp : this.getId2RecPro().values())
		{
			if (!rp.isEnable())
				continue;
			StringBuilder tmpsb = new StringBuilder();
			if (!rp.RT_init(tmpsb))
			{
				System.out.println(" Warn: RecManager RT_init pro=" + rp.getName() + " failed - " + tmpsb.toString());
			}
		}
		return true;
	}

	public synchronized void RT_start()
	{
		if (tssAdpPrj.RT_isRunning())
			return;

		StringBuilder failedr = new StringBuilder();
		if (!RT_init(failedr))
			throw new RuntimeException(failedr.toString());

		tssAdpPrj.RT_start();
	}

	public synchronized void RT_stop()
	{
		tssAdpPrj.RT_stop();
	}

	/**
	 * call by UATag in running
	 * 
	 * @param tag
	 */
	public void RT_fireUATagChanged(UATag tag)
	{

		if (!tssAdpPrj.RT_isRunning())
			return;
		String tagp = tag.getNodeCxtPathInPrj();
		if (recTagSet == null || !recTagSet.contains(tagp))
			return;
		UAVal uav = tag.RT_getVal();
		long dt = uav.getValDT();
		tssAdpPrj.addTagValue(tagp, dt, uav.isValid(), uav.getObjVal());
	}

	private TSSAdapter.IPkSavedListener tssPkSavedLis = new TSSAdapter.IPkSavedListener() {

		@Override
		public void onTagSegsSaved(List<TSSSavePK> segss)
		{
			if (segss == null)
				return;
			Map<String, RecTagParam> tag2pm = getRecTagParams();
			for (TSSSavePK ts : segss)
			{
				String tag = ts.getTag();
				RecTagParam rtp = tag2pm.get(tag);
				if (rtp == null)
					continue;
				List<RecProL1> pros = rtp.listPros();
				if (pros == null)
					continue;
				for (RecProL1 pro : pros)
				{
					try
					{
						pro.RT_onTagSegsSaved(ts);
					}
					catch ( Exception ee)
					{
						ee.printStackTrace();
					}
				}
			}
		}
	};

	public JSONObject RT_getInf()
	{
		JSONObject jo = new JSONObject();

		JSONArray jarr = new JSONArray();
		for (RecPro rp : this.getId2RecPro().values())
		{
			JSONObject tmpjo = rp.RT_getInf();
			jarr.put(tmpjo);
		}
		jo.put("pros", jarr);

		JSONObject tssjo = getTSSAdapterPrj().RT_getInfo().toJO();
		jo.put("tss_adp", tssjo);

		return jo;
	}

	// /**
	// * TSS Saved Listener,it will trigger RecPro to work
	// */
	// private TSSAdapter.ISavedListener tssSavedLis = new
	// TSSAdapter.ISavedListener() {
	//
	// @Override
	// public void onTagSegSaved(boolean b_insert, TSSTagSegs<?> tagsegs,
	// Integer tagidx,TSSValSeg<?> valseg, long saved_enddt)
	// {
	// String tag = tagsegs.getTag() ;
	// if(!recTagSet.contains(tag))
	// return ;
	// RecTagParam rtp = getRecTagParams().get(tag) ;
	// if(rtp==null)
	// return ;
	// List<RecProL1> pros = rtp.listPros() ;
	// if(pros==null)
	// return ;
	// for(RecProL1 pro:pros)
	// {
	// //pro.RT_doPro() ;
	// }
	// }
	// };

}
