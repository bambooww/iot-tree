package org.iottree.core;

import java.io.*;
import java.util.*;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.SQLiteSaver;
import org.iottree.core.util.web.PrjNavTree;
import org.iottree.core.util.web.PrjRestful;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.util.xmldata.data_val;
import org.graalvm.polyglot.HostAccess;
import org.iottree.core.Config.InnerComp;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.alert.AlertManager;
import org.iottree.core.basic.NameTitle;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.basic.PropItem.ValOpt;
import org.iottree.core.conn.ConnProHTTPSer;
import org.iottree.core.conn.ConnPtHTTPSer;
import org.iottree.core.cxt.JsDef;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.cxt.UARtSystem;
import org.iottree.core.filter.UANodeFilter;
import org.iottree.core.msgnet.IMNContTagListMapper;
import org.iottree.core.msgnet.IMNContainer;
import org.iottree.core.msgnet.MNManager;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.nodes.NS_TagChgTrigger;
import org.iottree.core.node.PrjShareManager;
import org.iottree.core.node.PrjSharer;
import org.iottree.core.res.IResCxt;
import org.iottree.core.res.IResNode;
import org.iottree.core.router.RouterManager;
import org.iottree.core.station.PStation;
import org.iottree.core.station.PlatInsManager;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.StoreManager;
import org.iottree.core.store.record.RecManager;
import org.iottree.core.task.Task;
import org.iottree.core.task.TaskManager;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * UA Project
 * 
 * @author zzj
 */
@data_class
@JsDef(name="prj",title="Prj",desc="Project Node",icon="icon_prj")
public class UAPrj extends UANodeOCTagsCxt implements IRoot, IOCUnit, IOCDyn, ISaver, IResCxt ,IMNContainer,IMNContTagListMapper//IJSOb
{
	public static final String NODE_TP = "prj" ;
	
	@data_obj(obj_c = UACh.class)
	List<UACh> chs = new ArrayList<>();

	@data_val(param_name = "max_id")
	int maxIdVal = 0;

	// @data_obj(obj_c = UAConn.class)
	// List<UAConn> conns = new ArrayList<>();
	@data_val(param_name = "auto_start")
	boolean bAutoStart = false;

	/**
	 * javascript run with interval
	 */
	@data_val(param_name = "script")
	String script = null;

	/**
	 * js run interval with ms
	 */
	@data_val(param_name = "script_int")
	long scriptInt = 10000;
	

	@data_val(param_name = "midtag_int")
	long midTagScriptInt = 100;

	@data_val(param_name = "hmi_main_id")
	String hmiMainId = null;

	@data_val
	String operators = null ;
	
	@data_val(param_name="perm_dur")
	int permDur = 300 ;
	
	@data_val(param_name="client_hmis")
	String clientHmis = null ;
	/**
	 * last script date time
	 */
	transient long scriptRunDT = System.currentTimeMillis();

	transient long midTagScriptRunDT = System.currentTimeMillis();

	/**
	 * check js script ok or not
	 */
	private transient boolean jsSetOk = false;

	/**
	 * script set error when setup js script
	 */
	private transient String jsSetError = null;

	/**
	 * script run error
	 */
	private transient String jsRunError = null;
	
	private transient UAContext context = null ;
	
	private transient SQLiteSaver sqliteSaver = null ;
	
	private transient long stationUpDT = System.currentTimeMillis() ;
	
	private transient UATag tagStationUpDT = null ;
	private transient UATag tagStationUpGAP = null ;

	public UAPrj()
	{
		super();
	}

	/**
	 * for creation
	 */
	public UAPrj(String name, String title, String desc)
	{
		super(name, title, desc);
	}
	
	public String getNodeTp()
	{
		return NODE_TP;
	}

	public String getRootIdPrefix()
	{
		return "r";
	}

	public int getRootNextIdVal()
	{
		maxIdVal++;
		return maxIdVal;
	}
	// void constructTree()
	// {
	// for(UACh ch:chs)
	// {
	// ch.belongTo = this ;
	// ch.constructTree();
	// }
	// }

	@Override
	protected void copyTreeWithNewSelf(IRoot root,UANode new_self, String ownerid, 
			boolean copy_id, boolean root_subnode_id,HashMap<IRelatedFile,IRelatedFile> rf2new)
	{
		super.copyTreeWithNewSelf(root,new_self, ownerid, copy_id, root_subnode_id,rf2new);
		UAPrj self = (UAPrj) new_self;
		self.script = this.script;
		self.scriptInt = this.scriptInt;
		self.chs.clear();
		for (UACh ch : this.chs)
		{
			UACh nt = new UACh();
			if (root_subnode_id)
			{
				if(root!=null)
					nt.id = root.getRootNextId() ;
				else
					nt.id = this.getNextIdByRoot();
			}
			ch.copyTreeWithNewSelf(root,nt, ownerid, copy_id, root_subnode_id,rf2new);
			self.chs.add(nt);
		}
	}

	void onLoaded()
	{
		//this.getResDir();

		this.RT_init(true, true);
		
		
	}

	public List<UANode> getSubNodes()
	{
		List<UANode> rets = super.getSubNodes();
		rets.addAll(chs);
		// rets.addAll(conns);
		// rets.addAll(stores);
		return rets;
	}

	// public List<UAMember> getMembers()
	// {
	// List<UAMember> rets = super.getMembers();
	// rets.addAll(conns) ;
	// return rets ;
	// }

	public boolean isAutoStart()
	{
		return this.bAutoStart;
	}

	public void setAutoStart(boolean b) throws Exception
	{
		if (this.bAutoStart == b)
			return;
		this.bAutoStart = b;
		this.save();
	}

	
	public List<UACh> getChs()
	{
		return chs;
	}

	public UACh getChById(String id)
	{
		for (UACh ch : chs)
		{
			if (id.contentEquals(ch.getId()))
				return ch;
		}
		return null;
	}

	public UACh getChByName(String n)
	{
		for (UACh ch : chs)
		{
			if (n.contentEquals(ch.getName()))
				return ch;
		}
		return null;
	}

	public UACh addCh(String drvname,boolean drv_en_at_ps, String name, String title, String desc, HashMap<String, Object> uiprops)
			throws Exception
	{
		UAUtil.assertUAName(name);

		UACh ch = getChByName(name);
		if (ch != null)
		{
			throw new IllegalArgumentException("ch with name=" + name + " existed");
		}
		ch = new UACh(name, title, desc, drvname);
		ch.bDrvEnAtPStation = drv_en_at_ps;
		if (uiprops != null)
		{
			for (Map.Entry<String, Object> n2v : uiprops.entrySet())
			{
				ch.OCUnit_setProp(n2v.getKey(), n2v.getValue());
			}
		}
		ch.id = this.getNextIdByRoot();
		// ch.belongTo = this;
		chs.add(ch);
		this.constructNodeTree();

		save();
		return ch;
	}

	public UACh updateCh(UACh ch, String drvname, boolean drv_en_at_ps,String name, String title, String desc) throws Exception
	{
		UAUtil.assertUAName(name);

		UACh tmpch = this.getChByName(name);
		if (tmpch != null && tmpch != ch)
			throw new IllegalArgumentException("ch with name=" + name + " existed");
		ch.setNameTitle(name, title, desc);
		ch.bDrvEnAtPStation = drv_en_at_ps ;

		if (drvname != null && drvname.equals(ch.getDriverName()))
		{
			save();
			return ch;
		}
		ch.setDriverName(drvname);
		return ch;
	}

	void delCh(UACh ch) throws Exception
	{
		chs.remove(ch);
		save();
	}

	public UACh deepPasteCh(UACh ch) throws Exception
	{
		String newn = ch.getName();
		newn = this.calNextSubNameAuto(newn);
		return deepPasteCh(ch, newn, ch.getTitle());
	}

	public UACh deepPasteCh(UACh ch, String newname, String newtitle) throws Exception
	{
		UANode oldn = this.getSubNodeByName(newname);
		if (oldn != null)
		{
			throw new Exception("ch name [" + newname + "] already existed");
		}

		UACh newch = new UACh();

		HashMap<IRelatedFile,IRelatedFile> rf2new = new HashMap<>();
		ch.copyTreeWithNewSelf(null,newch, null, false, true,rf2new);
		newch.id = this.getNextIdByRoot();
		// newch.name = newname;
		newch.setNameTitle(newname, null, null);
		// UACh newch = new UACh ch.deepCopyMe();
		// newch.id=this.getNextIdByRoot();
		this.chs.add(newch);
		this.constructNodeTree();
		//
//		for (UADev tmpd : newch.devs)
//		{
//			tmpd.updateByDevDef(rf2new);
//		}
//		this.constructNodeTree();
		this.save();
		Convert.copyRelatedFile(rf2new);
		return newch;
	}

	public void setHmiMainId(String hmiid) throws Exception
	{
		this.hmiMainId = hmiid;
		save();
	}

	public String getHmiMainId()
	{
		return this.hmiMainId;
	}

	public UAHmi getHmiMain()
	{
		if (Convert.isNullOrEmpty(this.hmiMainId))
		{
			List<UAHmi> hs = this.getHmis();
			if (hs == null || hs.size() <= 0)
				return null;
			return hs.get(0);
		}
		return this.getHmiById(hmiMainId);
	}
	
	public List<UAHmi> listHmiNodesAll()
	{
		ArrayList<UAHmi> rets = new ArrayList<>() ;
		listHmiNodesAll(this,rets) ;
		return rets ;
	}

	private void listHmiNodesAll(UANodeOCTagsCxt cxt,List<UAHmi> hmis)
	{
		List<UAHmi> chmis = cxt.getHmis() ;
		if(chmis!=null)
		{
			for(UAHmi hmi:chmis)
			{
				hmis.add(hmi) ;
			}
		}
		
		List<UANodeOCTagsCxt> cxts = cxt.getSubNodesCxt();
		if(cxts==null)
			return ;
		for(UANodeOCTagsCxt tmpcxt:cxts)
			listHmiNodesAll(tmpcxt,hmis) ;
	}

	
	public UATag getTagByPath(String tagpath)
	{
		UANode n = this.getDescendantNodeByPath(tagpath) ;
		if(n==null)
			return null ;
		if(n instanceof UATag)
			return (UATag)n ;
		else
			return null ;
	}

	public boolean chkValid()
	{
		return true;
	}

	private List<PropGroup> repPGS = null;

	@Override
	public List<PropGroup> listPropGroups()
	{
		if (repPGS != null)
			return repPGS;
		ArrayList<PropGroup> pgs = new ArrayList<>();
		List<PropGroup> lpgs = super.listPropGroups();
		if (lpgs != null)
			pgs.addAll(lpgs);
		pgs.add(this.getPrjPropGroup());
		pgs.add(this.getPrjRestfulApiGroup());
		pgs.add(this.getPrjHmiNavGroup());
		pgs.add(this.getPrjPStationGroup());
		//

		repPGS = pgs;
		return pgs;
	}
	
	static final String PI_SAVE_SNAPSHOT = "save_snapshot" ;
	static final String PI_SAVE_SNAPSHOT_INTV = "save_snapshot_intv" ;

	private PropGroup getPrjPropGroup()
	{
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		
		PropGroup r = new PropGroup("prj", lan);//"Project");

		r.addPropItem(new PropItem("script", lan, PValTP.vt_str, false, null, null,
				"").withTxtMultiLine(true)); //"JavaScript","JavaScript run interval by Project,you can do controller logic here"

		r.addPropItem(new PropItem("script_int", lan, PValTP.vt_int,
				false, null, null, "10000")); //"JavaScript Interval", "JavaScript run interval(ms)"
		
		r.addPropItem(new PropItem("operators",lan, PValTP.vt_str, false, null, null,
				"").withTxtMultiLine(true)); // "Operators","Operators may has name and password,it's may need to input when do some operation command."
		
		r.addPropItem(new PropItem("perm_dur", lan, PValTP.vt_int, false, null, null,
				"300")); // "Permission duration In seconds","Duration of authority after operator authentication."
		
		r.addPropItem(new PropItem("client_hmis", lan, PValTP.vt_str, false, null, null,"").withPop(PropItem.POP_N_CLIENT_HMIS)); 
		
		r.addPropItem(new PropItem(PI_SAVE_SNAPSHOT, lan, PValTP.vt_bool, false, null, null,false)); 
		r.addPropItem(new PropItem(PI_SAVE_SNAPSHOT_INTV, lan, PValTP.vt_int, false, null, null,5000));
		
		return r;
	}
	
	private transient PrjRestful restful = null ;
	private transient boolean bRestfulGit = false;

	private PropGroup getPrjRestfulApiGroup()
	{
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		
		PropGroup r = new PropGroup("prj_restful", lan,"/doc/advanced/adv_restful_out.md");//"Project");

		r.addPropItem(new PropItem("token_en", lan, PValTP.vt_bool, false, null, null,false));
		r.addPropItem(new PropItem("token_users",lan, PValTP.vt_str, false, null, null,"").withTxtMultiLine(true));
		r.addPropItem(new PropItem("wtag_cutoff", lan, PValTP.vt_bool, false, null, null,false));
		
		return r;
	}
	
	public PrjRestful getEnabledRestfulToken()
	{
		if(bRestfulGit)
			return this.restful ;
		
		try
		{
			boolean b = this.getOrDefaultPropValueBool("prj_restful", "token_en", false) ;
			if(!b)
			{
				restful = null;
				return null;
			}
			
			restful = new PrjRestful(this) ;
			return restful ;
		}
		finally
		{
			bRestfulGit = true ;
		}
	}
	
	private PropGroup getPrjHmiNavGroup()
	{
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		
		PropGroup r = new PropGroup("prj_hmi_nav", lan,"/doc/hmi/hmi_nav.md");//"Project");

		r.addPropItem(new PropItem("hmi_nav1_en", lan, PValTP.vt_bool, false, null, null,false));
		r.addPropItem(new PropItem("hmi_nav1_jo",lan, PValTP.vt_str, false, null, null,"").withTxtMultiLine(true));
		
		r.addPropItem(new PropItem("hmi_nav2_en", lan, PValTP.vt_bool, false, null, null,false));
		r.addPropItem(new PropItem("hmi_nav2_jo",lan, PValTP.vt_str, false, null, null,"").withTxtMultiLine(true));
		
		return r;
	}
	
	private transient HashMap<String,PrjNavTree> nav2tree = null ;
	
	public synchronized PrjNavTree getPrjNavTree(String nav)
	{
		if(nav2tree!=null)
		{
			return nav2tree.get(nav) ;
		}
		
		try
		{
			HashMap<String,PrjNavTree> rr = new HashMap<>() ;
			PrjNavTree pnt = loadPrjNavTree("nav1") ;
			if(pnt!=null)
				rr.put("nav1",pnt) ;
			pnt = loadPrjNavTree("nav2") ;
			if(pnt!=null)
				rr.put("nav2",pnt) ;
			nav2tree = rr ;
			return nav2tree.get(nav) ;
		}
		finally
		{
			
		}
	}
	
	private PrjNavTree loadPrjNavTree(String nav)
	{
		boolean b = this.getOrDefaultPropValueBool("prj_hmi_nav", "hmi_"+nav+"_en", false) ;
		if(!b) return null;
		String jostr = this.getOrDefaultPropValueStr("prj_hmi_nav", "hmi_"+nav+"_jo", "") ;
		if(Convert.isNullOrEmpty(jostr))
			return null ;
		try
		{
			JSONObject jo = new JSONObject(jostr) ;
			PrjNavTree pnt = new PrjNavTree() ;
			if(pnt.fromJO(jo))
				return pnt ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		return null ;
	}
	
	// PStation for remote station supported
	
	PStation pstationInsDef = null ;
	
	private PropGroup getPrjPStationGroup()
	{
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		
		PropGroup r = new PropGroup("pstation_ins", lan);//"Project");

		r.addPropItem(new PropItem("pstation_ins_en", lan, PValTP.vt_bool, false, null, null,false));
		r.addPropItem(new PropItem("station_id",lan, PValTP.vt_str, false, new String[] {}, new Object[] {},"").withDynValOpts(new PropItem.IValOpts() {
			@Override
			public PropItem.ValOpts getValOpts()
			{
				List<PStation> pss = PlatInsManager.getInstance().listStations() ;
				ArrayList<ValOpt> vopt = new ArrayList<>(pss.size()) ;
				vopt.add(new ValOpt(" --- ","")) ;
				for(PStation ps:pss)
				{
					vopt.add(new ValOpt("["+ps.getId()+"] "+ps.getTitle(),ps.getId())) ;
				}
				return new PropItem.ValOpts(vopt);
			}
		}));
		
		r.addPropItem(new PropItem("syn_data_saver_sor",lan, PValTP.vt_str, false, new String[] {}, new Object[] {},"").withDynValOpts(new PropItem.IValOpts() {
			
			@Override
			public PropItem.ValOpts getValOpts()
			{
				List<SourceJDBC> sors = StoreManager.listSourcesJDBC() ;
				
				ArrayList<ValOpt> vopt = new ArrayList<>(sors.size()) ;
				vopt.add(new ValOpt(" --- ","")) ;
				for(SourceJDBC sor:sors)
				{
					vopt.add(new ValOpt(sor.getDBInf(),sor.getName())) ;
				}
				return new PropItem.ValOpts(vopt);
			}
		}));
		
		r.addPropItem(new PropItem("station_prjn",lan, PValTP.vt_str, false, null, null,""));
		
		
//		r.addPropItem(new PropItem("pstation_ins_run_chs",lan, PValTP.vt_str, false, null, null,""));
//		
//		r.addPropItem(new PropItem("pstation_ins_run_conns",lan, PValTP.vt_str, false, null, null,""));
		
		return r;
	}
		
	public synchronized PStation getPrjPStationInsDef()
	{
		if(pstationInsDef!=null)
			return pstationInsDef ;
		
		boolean ben = this.getOrDefaultPropValueBool("pstation_ins", "pstation_ins_en", false) ;
		if(!ben)
		{
			return null ;
		}
		
		String id = this.getOrDefaultPropValueStr("pstation_ins", "station_id", "") ;
		if(Convert.isNullOrEmpty(id))
			return null ;
		pstationInsDef = PlatInsManager.getInstance().getStationById(id) ;
		return pstationInsDef ;
	}
	
	public String getPrjPStationSaverSor()
	{
		return this.getOrDefaultPropValueStr("pstation_ins", "syn_data_saver_sor", "") ;
	}
	
	/**
	 * remote station prj name
	 * @return
	 */
	public String getPrjRStationPrjName()
	{
		return this.getOrDefaultPropValueStr("pstation_ins", "station_prjn", "") ;
	}
	
	/**
	 * prj is station instance for remote station
	 * @return
	 */
	public boolean isPrjPStationIns()
	{
		return this.getOrDefaultPropValueBool("pstation_ins", "pstation_ins_en", false) ;
//		PStation def = getPrjPStationInsDef() ;
//		return def!=null ;
	}
	
//	public List<String> getPrjPStationInsEnChs()
//	{
//		String str = this.getOrDefaultPropValueStr("pstation_ins", "pstation_ins_run_chs", "").trim();
//		return Convert.splitStrWith(str, ",|");
//	}
//	
//	public List<String> getPrjPStationInsEnConns()
//	{
//		String str = this.getOrDefaultPropValueStr("pstation_ins", "pstation_ins_run_conns", "").trim();
//		return Convert.splitStrWith(str, ",|");
//	}
	
	public Object getPropValue(String groupn, String itemn)
	{
		if ("prj".contentEquals(groupn))
		{
			switch (itemn)
			{
			case "script":
				return this.script;
			case "script_int":
				return this.scriptInt;
			case "operators":
				return this.operators;
			case "perm_dur":
				return this.permDur ;
			case "client_hmis":
				return this.clientHmis ;
			}
		}
		return super.getPropValue(groupn, itemn);
	}

	public boolean setPropValue(String groupn, String itemn, String strv)
	{
		if ("prj".contentEquals(groupn))
		{
			switch (itemn)
			{
			case "script":
				this.script = strv;
				return true;
			case "script_int":
				this.scriptInt = Long.parseLong(strv);
				return true;
			case "operators":
				this.operators=strv; 
				return true ;
			case "perm_dur":
				this.permDur = Integer.parseInt(strv) ;
				return true;
			case "client_hmis":
				this.clientHmis = strv;
				return true ;
			}

		}
		return super.setPropValue(groupn, itemn, strv);
	}
	

	@Override
	protected void onPropNodeValueChged()
	{
		synchronized(this)
		{
			bRestfulGit = false;
			nav2tree = null ;
			this.pstationInsDef = null ;
		}
		try
		{
			setupJsScript();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	public void save() throws Exception
	{
		save(false);
	}

	public void save(boolean reinit) throws Exception
	{
		if (reinit)
		{
			this.RT_init(true, true);
			this.constructNodeTree();
		}
		UAManager.getInstance().savePrj(this);
	}
	
	public void setPrjName(String prjn)
	{
		Convert.checkVarName(prjn, true) ;
		//	throw new IllegalArgumentException("invalid prj name="+prjn) ;
		this.setNameTitle(prjn, null, null) ;
	}

	public boolean isMainPrj() throws IOException
	{
		return UAManager.getInstance().getPrjDefault() == this;
	}

	/**
	 * get rep sub dir which can save content related this rep e.g hmi
	 * 
	 * @return
	 */
	public File getPrjSubDir()
	{
		return UAManager.getPrjFileSubDir(this.getId());
	}
	
	public String getMsgNetContainerId()
	{
		return this.getId() ;
	}

	@Override
	public File getMsgNetDir()
	{
		return getPrjSubDir();
	}
	
	

	@Override
	public List<NameTitle> getMNContTagListCatTitles()
	{
		ArrayList<NameTitle> rets = new ArrayList<>() ;
		String prjnp = this.getNodePath() ;
		rets.add(new NameTitle(prjnp,prjnp)) ;
		for(UANodeOCTagsCxt subn:this.getSubNodesCxt())
		{
			String np = subn.getNodePath() ;
			rets.add(new NameTitle(np,np)) ;
		}
		return rets;
	}

	@Override
	public List<NameTitle> getMNContTagList(String cat)
	{
		UANodeOCTagsCxt subn = (UANodeOCTagsCxt)UAUtil.findNodeByPath(cat) ;
		List<UANodeOCTags>  tns = subn.listSelfAndSubTagsNode() ;
		ArrayList<NameTitle> rets = new ArrayList<>() ;
		for(UANodeOCTags tn:tns)
		{
			List<UATag> tags = tn.getNorTags() ;
			for(UATag tag:tags)
			{
				String np = tag.getNodeCxtPathIn(subn,".") ;
				String npt = tag.getNodeCxtPathTitleIn(subn) ;
				rets.add(new NameTitle(np,npt)) ;
			}
		}
		return rets ;
	}
	

	public File getSaverDir()
	{
		return getPrjSubDir();
	}

	File getPrjFile()
	{
		return UAManager.getPrjFile(this.id);
	}

	public long getSavedDT()
	{
		File f = getPrjFile();
		if (!f.exists())
			return -1;
		return f.lastModified();
	}

	public List<ConnProvider> getConnProviders() //throws Exception
	{
		return ConnManager.getInstance().getConnProviders(this.getId());
	}
	
	
	public ConnPtHTTPSer getConnPtHTTPSerByName(String conn_ptn)
	{
		ConnProvider cp = ConnManager.getInstance().getConnProviderSingle(this.getId(), ConnProHTTPSer.TP) ;
		if(cp==null)
			return null ;
		return (ConnPtHTTPSer)cp.getConnByName(conn_ptn) ;
	}

	public UAHmi findHmiById(String id)
	{
		UANode uan = this.findNodeById(id);
		if (uan == null || !(uan instanceof UAHmi))
		{
			return null;
		}
		return (UAHmi) uan;
	}
	
	
	public JSONObject toOCUnitJSON()
	{
		return IOCBox.transOCUnitToJSON(this);
	}

	public void fromOCUnitJSON(JSONObject jobj)
	{
		this.RT_stop();

		IOCBox.transJSONToOCUnit(this, jobj);
	}

	public JSONObject toOCDynJSON(long lastdt)
	{
		return IOCBox.dynOCUnitToJSON(this, lastdt);
	}

	// @Override
	// public void OC_setBaseVal(String name, String title)
	// {
	//
	// }

	@Override
	public String OCUnit_getUnitTemp()
	{
		return "rep";
	}

	/**
	 * true node may has sub unit
	 * 
	 * @return
	 */
	@Override
	public boolean OC_supportSub()
	{
		return true;
	}

	@Override
	public List<IOCBox> OC_getSubs()
	{
		List<IOCBox> rets = super.OC_getSubs();
		if (chs != null)
			rets.addAll(chs);
		return rets;
	}

	// protected void listTagsAll(List<UATag> tgs,boolean bmid)
	// {
	// if(bmid)
	// {
	// for(UATag tg:listTags())
	// {
	// if(tg.isMidExpress())
	// tgs.add(tg) ;
	// }
	// }
	// else
	// tgs.addAll(this.listTags());
	// for(UACh d:chs)
	// {
	// d.listTagsAll(tgs,bmid);
	// }
	// }

	/**
	 * 
	 * @return
	 */
	public JSONObject OC_getDynJSON(long lastdt)
	{
		JSONObject r = new JSONObject();
		r.put("brun", this.RT_isRunning());
		return r;
	}

	@Override
	public boolean CXT_containsKey(String jsk)
	{
		if (jsk.startsWith("_"))
		{// system
			switch (jsk)
			{
			case "_system":
				return true;
			default:
				return false;
			}
		}

		UACh ch = getChByName(jsk);
		return ch != null;
	}

	@Override
	public Object CXT_getByKey(String jsk)
	{
		if (jsk.startsWith("_"))
		{// system
			switch (jsk)
			{
			case "_system":
				return new UARtSystem();
			default:
				return null;
			}
		}
		UACh ch = getChByName(jsk);
		if (ch != null)
			return ch;
		return null;
	}

	/**
	 * name of editor which will use res
	 * 
	 * @return
	 */
	public String getEditorName()
	{
		return "rep";
	}

	public String getEditorId()
	{
		return this.id;
	}
	
	/**
	 * used for local tags value and tags values snapshot
	 * @return
	 * @throws Exception 
	 */
	public SQLiteSaver getSQLiteSaver() throws Exception
	{
		if(sqliteSaver!=null)
			return sqliteSaver;
		
		synchronized(this)
		{
			if(sqliteSaver!=null)
				return sqliteSaver;
			
			File dbf = new File(this.getSaverDir(),"_prj_store.db");
			sqliteSaver = new SQLiteSaver(dbf) ;
			return sqliteSaver;
		}
	}

//	private File getLocalTagsValFile()
//	{
//		return new File(this.getSaverDir(),"local_tags_val.xml") ;
//	}
	
	static final String LOCAL_TAG_VALS = "local_tag_vals" ;

	private transient XmlData localTagVals = null ;
	
	private transient long localTagsLastRun = -1 ;
	
	private List<UATag> listTagsLocalAutoSaveAll()
	{
		List<UATag> rets = listTagsLocalAll() ;
		for(int i = 0 ; i < rets.size() ; )
		{
			UATag tag = rets.get(i) ;
			if(tag.isLocalAutoSave())
			{
				i ++ ;
				continue ;
			}
			
			rets.remove(i) ;
		}
		return rets ;
	}
	
	private void loadLocalTagsValue() throws Exception
	{
		List<UATag> localtags = listTagsLocalAutoSaveAll() ;
		if(localtags==null||localtags.size()<=0)
			return ;
		
		SQLiteSaver sqls = getSQLiteSaver() ;
		String ss = sqls.getValByKey(LOCAL_TAG_VALS) ;
		if(Convert.isNotNullEmpty(ss))
			localTagVals = XmlData.parseFromReader(new StringReader(ss)) ;
		
		for(UATag tag:localtags)
		{
			if(localTagVals!=null)
			{
				String pn = tag.getNodePathCxt();
				Object objv = localTagVals.getParamValue(pn) ;
				if(objv!=null)
				{
					tag.RT_setValStr(""+objv);
					continue;
				}
			}
			
			String strv = tag.getLocalDefaultVal() ;
			if(strv!=null)
				tag.RT_setValStr(strv);
		}
	}
	
	
	private void saveLocalTagsValue() throws Exception
	{
		//File f = getLocalTagsValFile() ;
		
		List<UATag> localtags = listTagsLocalAutoSaveAll() ;
		if(localtags==null || localtags.size()<=0)
			return ;
		XmlData xd = new XmlData() ;
		boolean bchged = false;
		
		for(UATag tag:localtags)
		{
			UAVal uav = tag.RT_getVal() ;
			if(uav==null||!uav.isValid())
				continue ;
			
			Object v = uav.getObjVal() ;
			if(v==null)
				continue ;
			//String defv = tag.getLocalDefaultVal() ;
			//if(defv!=null&&defv.equals(v.toString()))
			//	continue ;
			String pn = tag.getNodePathCxt() ;
			if(!bchged)
			{
				if(localTagVals!=null)
				{
					Object oldv = localTagVals.getParamValue(pn) ;
					if(!v.equals(oldv))
					{
						bchged = true;
					}
				}
				else
				{
					bchged=true;
				}
			}
			xd.setParamValue(pn, v);
		}
		
		if(bchged)
		{//chged will save
//			Convert.writeFileSafe(f, (tmpf)->{
//				XmlData.writeToFile(xd, tmpf);
//			});
			SQLiteSaver sqls = getSQLiteSaver() ;
			sqls.setKeyVal(LOCAL_TAG_VALS,xd.toXmlString()) ;
			localTagVals = xd ;
		}
	}
	
	private void runLocalTagsSave()
	{
		if(System.currentTimeMillis()-localTagsLastRun<5000)
			return ;
		
		try
		{
			saveLocalTagsValue();
		}
		catch(Exception e)
		{
			if(log.isDebugEnabled())
				log.debug("save local tags value err", e);
		}
		finally
		{
			localTagsLastRun = System.currentTimeMillis();
		}
	}
	
	//private transient boolean enableSnapshotSave = false;
	private transient int snapshotSaveIntv = -1 ;
	private transient long lastTagsSnapshotSave = System.currentTimeMillis() ;
	
	private static final String TAGS_SNAPSHOT = "tags_snapshot" ;
	
	private void loadlTagsSnapshot() throws Exception
	{
		if(snapshotSaveIntv<=0)
			return ;
		
		SQLiteSaver sqls = getSQLiteSaver() ;
		String ss = sqls.getValByKey(TAGS_SNAPSHOT) ;
		if(Convert.isNullOrEmpty(ss))
			return ;
		
		JSONObject jo = new JSONObject(ss) ;
		this.RT_injectSnapCurData(jo) ;
	}
	
	private void saveTagsSnapshot() throws Exception
	{
		if(snapshotSaveIntv<=0)
			return ;
		
		JSONObject jo = RT_snapCurData(true,true);
		SQLiteSaver sqls = getSQLiteSaver() ;
		sqls.setKeyVal(TAGS_SNAPSHOT,jo.toString()) ;
	}
	
	private void runTagsValSnapshot()
	{
		if(snapshotSaveIntv<=0)
			return ;
		
		if(System.currentTimeMillis()-lastTagsSnapshotSave<snapshotSaveIntv)
			return ;
		
		try
		{
			saveTagsSnapshot();
		}
		catch(Exception e)
		{
			if(log.isDebugEnabled())
				log.debug("save local tags value snapshot err", e);
		}
		finally
		{
			lastTagsSnapshotSave = System.currentTimeMillis();
		}
	}
	// @Override
	// public List<ResDir> getResCxts()
	// {
	// ArrayList<ResDir> rcs = new ArrayList<>(1) ;
	// rcs.add(getResCxt()) ;
	// return rcs;
	// }

	// public Object JS_get(Object key)
	// {
	// String pn = (String)key ;
	// UACh ch = this.getChByName(pn) ;
	// if(ch!=null)
	// return ch ;
	// return null ;
	// }

	@Override
	void RT_init(boolean breset, boolean b_sub)
	{
		super.RT_init(breset, b_sub);

		this.setSysTag("_name", "", "", ValTP.vt_str);
		this.setSysTag("_title", "", "", ValTP.vt_str);
		// this.setSysTag("_tick_ms", "Milliseconds from 1970-1-1", "",
		// ValTP.vt_int64);
		this.setSysTag("_date", "yyyy-MM-dd hh:mm:ss", "", ValTP.vt_str);
		this.setSysTag("_date_year", "current year int16 value", "", ValTP.vt_int16);
		this.setSysTag("_date_month", "current month int16 value", "", ValTP.vt_int16);
		this.setSysTag("_date_day", "current day int16 value", "", ValTP.vt_int16);
		this.setSysTag("_hour", "current hour0-23 int16 value", "", ValTP.vt_int16);
		this.setSysTag("_minute", "current minute 0-59 int16 value", "", ValTP.vt_int16);
		this.setSysTag("_second", "current second 0-59 int16 value", "", ValTP.vt_int16);
		
		

		this.RT_setSysTagVal("_name", this.getName(), true);
		this.RT_setSysTagVal("_title", this.getTitle(), true);
		
		List<Task> jsts = TaskManager.getInstance().getTasks(this.getId());
		if (jsts != null&&jsts.size()>0)
		{
			for (Task jst : jsts)
			{
				String n = jst.getName() ;
				if(Convert.isNullOrEmpty(n))
					continue ;
				if(!Convert.checkVarName(n, false, null))
					continue;
				this.setSysTag("_task_"+n, "task is running or not","",ValTP.vt_bool) ;
			}
		}
		
		try
		{
			//load local tags
			loadLocalTagsValue();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		boolean pv = this.getOrDefaultPropValueBool("prj", PI_SAVE_SNAPSHOT,false);
		int intv = this.getOrDefaultPropValueInt("prj", PI_SAVE_SNAPSHOT_INTV, -1) ;
		if(pv && intv>0)
		{
			snapshotSaveIntv = intv ;
			try
			{
				loadlTagsSnapshot();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	void RT_onMonInit()
	{
		if(this.isPrjPStationIns())
		{
			try
			{
				tagStationUpDT = this.getOrAddTag("station_up_dt","pstation update dt from remote station (ms)","",ValTP.vt_int64, true) ;
				tagStationUpGAP = this.getOrAddTag("station_up_gap", "The interval between the pstation update time and now (second)","",ValTP.vt_int64, true) ;
				//this.setSysTag("_station_up_dt", "pstation update dt from remote station (ms)","",ValTP.vt_int64) ;
				//this.setSysTag("_station_up_gap", "The interval between the pstation update time and now (second)","",ValTP.vt_int64) ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void RT_flush()
	{
		super.RT_flush();

		Calendar d = Calendar.getInstance();
		// this.RT_setSysTagVal("_tick_ms", d.getTimeInMillis(),true) ;

		short y = (short) d.get(Calendar.YEAR);
		short m = (short) (d.get(Calendar.MONTH) + 1);
		short day = (short) d.get(Calendar.DAY_OF_MONTH);
		short hour = (short) d.get(Calendar.HOUR_OF_DAY);
		short min = (short) d.get(Calendar.MINUTE);
		short sec = (short) d.get(Calendar.SECOND);

		this.RT_setSysTagVal("_date", Convert.toFullYMDHMS(d.getTime()), true);
		this.RT_setSysTagVal("_date_year", y, true);
		this.RT_setSysTagVal("_date_month", m, true);
		this.RT_setSysTagVal("_date_day", day, true);
		this.RT_setSysTagVal("_hour", hour, true);
		this.RT_setSysTagVal("_minute", min, true);
		this.RT_setSysTagVal("_second", sec, true);
		
		
		//else
		//	this.RT_setSysTagVal("_station_up_gap", -1, true);
		
		List<Task> jsts = TaskManager.getInstance().getTasks(this.getId());
		if (jsts != null)
		{
			for (Task jst : jsts)
			{
				String n = jst.getName() ;
				if(Convert.isNullOrEmpty(n))
					continue ;
				this.RT_setSysTagVal("_task_"+n, jst.RT_isRunning()) ;
			}
		}
	}

	private Thread rtTh = null;

	private volatile boolean rtRun = false;

	@JsDef
	synchronized public boolean RT_start()
	{
		//if(this.isPrjPStationIns()) //PlatInsManager.isInPlatform())
		//	return false;
		
		if (rtTh != null)
			return true;
		// rtTh = new Thread(this::prjRun,"iottree-prj-"+this.getName());
		rtTh = new Thread(prjRunner, "iottree-prj-" + this.getName());
		rtRun = true;
		rtTh.start();
		return true;
	}

	@JsDef
	public void RT_stop()
	{
		rtRun = false;
		
//		try
//		{
//			Thread.sleep(PRJ_RUN_INTERVAL);
//		}
//		catch(Exception e) {}
		
		stopPrj();
		
		if (rtTh == null)
			return;
	}
	
	@JsDef
	public boolean RT_isRunning()
	{
		return rtTh != null;
	}

	private void startStopConn(boolean b) // throws Exception
	{
		try
		{
			List<ConnProvider> cps = ConnManager.getInstance().getConnProviders(this.getId());
			if (cps == null)
				return;
			for (ConnProvider cp : cps)
			{
				if (!cp.isEnable())
					continue;

				try
				{
					if (b)
						cp.start();
					else
						cp.stop();
				}
				catch ( Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
	}

	private void startStopCh(boolean b)
	{
		//boolean b_station_ins = this.isPrjPStationIns() ;
		//List<String> pstation_en_chs = this.getPrjPStationInsEnChs();
		
		for (UACh ch : this.getChs())
		{
			if(!ch.canRun())
				continue ;
//			String chn = ch.getName() ;
//			if(b_station_ins && (pstation_en_chs==null || !pstation_en_chs.contains(chn)))
//				continue;
			
			try
			{
				StringBuilder sb = new StringBuilder();
				if (b)
					ch.RT_startDriver(sb);
				else
					ch.RT_stopDriver(true);
			}
			catch ( Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private static void RT_initContext(UANodeOCTagsCxt cxt)
	{
		//old context with js env will rebuild
		for(UANodeOCTagsCxt subcxt :cxt.getSubNodesCxt())
		{
			RT_initContext(subcxt) ;
		}
		cxt.RT_reContext();
	}
	
	private static long PRJ_RUN_INTERVAL = 5 ;
	
	private static long __lastGap = -1 ;

	Runnable prjRunner = new Runnable() {
		public void run()
		{
			InnerComp ic = Config.getInnerComp("rec") ;
			boolean b_rec = ic==null||ic.bEnable ;
			ic = Config.getInnerComp("store") ;
			boolean b_store = ic==null||ic.bEnable ;
			
			boolean b_station_ins = UAPrj.this.isPrjPStationIns() ;
			try
			{
				RT_init(true, true) ;
				
				RT_initContext(UAPrj.this) ;
				
				RT_initByNet();
				
				// StringBuilder failedr = new StringBuilder() ;
				
				if(!b_station_ins)
				{
					if(b_rec)
						RecManager.getInstance(UAPrj.this).RT_start() ;
					
					AlertManager.getInstance(UAPrj.this.getId()).RT_start();
					
					if(b_store)
						StoreManager.getInstance(UAPrj.this.getId()).RT_start();
					
					RouterManager.getInstance(UAPrj.this).RT_start();
					// start channel drivers
					startStopCh(true);
					
					// start connprovider
					startStopConn(true);
	
					startStopTask(true) ;
				}
				else
				{// station ins- agent  ,it can config conn or ch to be run. so in agent ,you can disable conn or driver
					// and keep agent special conn and ch to run as normal. that's make agent can has some data or device add
					//if(b_)
					startStopCh(true); //you can set run at pstation
					
					startStopConn(true); //you can disabled conn at pstation
				}
				
				MNManager.getInstance(UAPrj.this).RT_start();
				
				while (rtRun)
				{
					try
					{
						Thread.sleep(PRJ_RUN_INTERVAL);
					}
					catch ( Exception e)
					{
					}

					if(!rtRun)
						break ;
					
					if(!b_station_ins)
					{
						runFlush();
						runMidTagsScript();
						runScriptInterval();
						runShareInterval();
					}
					else
					{
						runFlush();
						runMidTagsScript();
						
						if(stationUpDT>0 && tagStationUpGAP!=null)
						{
							long tmpt = System.currentTimeMillis() ;
							if(tmpt-__lastGap>1000)
							{
								//RT_setSysTagVal("_station_up_gap", (tmpt-stationUpDT)/1000, true);
								tagStationUpGAP.RT_setVal( (tmpt-stationUpDT)/1000);
								__lastGap = tmpt ;
							}
						}
					}
//					et = System.currentTimeMillis() ;
//					System.out.println("run flush cost="+(et-st));
					
					runLocalTagsSave();
					
					runTagsValSnapshot();
				}
			}
			catch ( Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{// stop normal save snapshot
					saveTagsSnapshot() ;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				MNManager.getInstance(UAPrj.this).RT_stop();
				
				stopPrj();

				runFlush();
				
				AlertManager.getInstance(UAPrj.this.getId()).RT_stop();
				
				if(b_store)
					StoreManager.getInstance(UAPrj.this.getId()).RT_stop();
				
				if(b_rec)
					RecManager.getInstance(UAPrj.this).RT_stop() ;
				
				RouterManager.getInstance(UAPrj.this).RT_stop();
			}
		}
	};
	
	private void stopPrj()
	{
		
		
		startStopTask(false);
		startStopConn(false);
		startStopCh(false);

		PrjSharer ps = getSharer();
		if (ps != null)
			ps.runStop();

		rtRun = false;
		rtTh = null;
	}

	// private Runnable runner = new Runnable() {
	//
	// @Override
	// public void run()
	// {
	//
	// }
	//
	// };

	public PrjSharer getSharer()
	{
		return PrjShareManager.getInstance().getSharer(this.getId());
	}
	
	private transient long lastRunFlush = -1 ;

	private final void runFlush()
	{
		if(System.currentTimeMillis()-lastRunFlush<500)
			return ;
		
		try
		{
			boolean b_pstation = this.isPrjPStationIns() ;
			if(b_pstation)
			{
				//run only channel enable at pstation and prj root mid tags
				for(UACh ch:this.chs)
				{
					if(!ch.canRun())
						continue ;
					ch.RT_runFlush(); 
				}
			}
			else
			{
				this.RT_runFlush();
			}
			
//			for (UANode subn : this.getSubNodes())
//			{
//				if (subn instanceof UANodeOCTags)
//				{
//					((UANodeOCTags) subn).RT_runFlush();
//				}
//			}
		}
		finally
		{
			lastRunFlush = System.currentTimeMillis() ;
		}
	}


	private void runShareInterval()
	{
		PrjSharer ps = getSharer();
		if (ps == null)
			return;
		if (ps.isEnable())
			ps.runInLoop();
		else
			ps.runStop();
	}

	/**
	 * judge share or not
	 * 
	 * @return
	 */
	public boolean isShare()
	{
		PrjSharer ps = getSharer();
		if (ps == null)
			return false;
		return ps.isEnable();
	}

	public boolean isShareRunning()
	{
		PrjSharer ps = getSharer();
		if (ps == null)
			return false;
		return ps.isRunning();
	}

//	private ScriptEngine engine = null;
//
//	private ScriptEngine getJSEngine0()
//	{
//		if (engine != null)
//			return engine;
//
//		engine = UAManager.createJSEngine(this);
//
//		engine.put("$this", jsOb);
//		engine.put("$prj", jsOb);
//		return engine;
//	}
	
	private ScriptEngine getJSEngine() throws ScriptException
	{
		if(this.context!=null)
			return this.context.getScriptEngine() ;
		
		synchronized(this)
		{
			if(this.context!=null)
				return this.context.getScriptEngine() ;
			
			this.context = new UAContext(this) ;
			return this.context.getScriptEngine() ;
		}
	}

	boolean isJsSetOk()
	{
		return this.jsSetOk;
	}

	private static String FN_PRJ_SCRIPT = "_rt_prj_script_run";

	/**
	 * 被外界调度的脚本运行过程
	 * @throws ScriptException 
	 */
	private boolean setupJsScript() throws ScriptException
	{
		if (script == null)
		{
			jsSetOk = false;
			return false;
		}
		if (script.equals("") || script.trim().equals(""))
		{
			jsSetOk = false;
			return false;
		}

		ScriptEngine engine = getJSEngine();
		try
		{
			engine.eval("function " + FN_PRJ_SCRIPT + "(){\r\n" + script + "\r\n}");
			this.jsSetError = null;
			// js set ok

			// Bindings bds = engine.getBindings(ScriptContext.ENGINE_SCOPE);

			jsSetOk = true;
		}
		catch ( Exception ee)
		{
			ee.printStackTrace();
			this.jsSetError = ee.getMessage();
			jsSetOk = false;
		}
		return jsSetOk;
	}

	/**
	 * get js setup error info
	 * 
	 * @return
	 */
	public String getJsSetError()
	{
		return this.jsSetError;
	}

	/**
	 * get js run error
	 * 
	 * @return
	 */
	public String getJsRunError()
	{
		return this.jsRunError;
	}

	void runScriptInterval()
	{
		if (!jsSetOk)
			return;

		if (System.currentTimeMillis() - this.scriptRunDT < this.scriptInt)
			return;// no run

		try
		{
			Invocable jsInvoke = (Invocable) getJSEngine();
			jsInvoke.invokeFunction(FN_PRJ_SCRIPT);
		}
		catch ( ScriptException se)
		{
			jsRunError = se.getMessage();
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			this.scriptRunDT = System.currentTimeMillis();
		}
	}

	void runMidTagsScript()
	{
		if (System.currentTimeMillis() - this.midTagScriptRunDT < this.midTagScriptInt)
			return;// no run

		boolean b_pstation = this.isPrjPStationIns() ;
		try
		{
			if(b_pstation)
			{
				//run only channel enable at pstation and prj root mid tags
				for(UACh ch:this.chs)
				{
					if(!ch.canRun())
						continue ;
					ch.CXT_calMidTagsVal();
				}
				//run prj local mid tags
				this.CXT_calMidTagsValLocal();
			}
			else
			{
				CXT_calMidTagsVal();
			}
		}
		finally
		{
			this.midTagScriptRunDT = System.currentTimeMillis();
		}

	}
	
	
	public boolean checkOperator(String user,String psw)
	{
		if(Convert.isNullOrEmpty(this.operators))
			return false;
		HashMap<String,String> pms = Convert.transPropStrToMap(this.operators) ;
		String v = pms.get(user) ;
		if(Convert.isNullOrEmpty(v))
			return false;
		return v.equals(psw) ;
	}
	
	/**
	 * ger operation permission seconds
	 * @return
	 */
	public long getOperPermDurSec()
	{
		return this.permDur;
	}
	
	public static class HmiNavItem
	{
		public String path ;
		
		public String icon ;
		
		public String title ;
		
		public String color ;
		
		private HmiNavItem(String path,String icon,String title,String color)
		{
			this.path = path ;
			this.icon = icon ;
			this.title = title ;
			this.color = color ;
		}
	}
	
	public LinkedHashMap<String,HmiNavItem> getClientHMIPath2NavList()
	{
		if(Convert.isNullOrEmpty(this.clientHmis))
			return null ;
		
		LinkedHashMap<String,HmiNavItem> rets = new LinkedHashMap<>() ;
		JSONArray jarr = new JSONArray(this.clientHmis) ;
		int len = jarr.length() ;
		for(int i = 0 ; i < len ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			String path = jo.getString("path") ;
			String icon = jo.optString("icon") ;
			String tt = jo.getString("title") ;
			String color = jo.optString("color") ;
			HmiNavItem ni = new HmiNavItem(path,icon,tt,color) ;
			rets.put(path, ni) ;
		}
		return rets ;
	}

	private void startStopTask(boolean b)
	{
		List<Task> jsts = TaskManager.getInstance().getTasks(this.getId());
		if (jsts == null || jsts.size() <= 0)
			return;

		for (Task jst : jsts)
		{
			if(b)
				jst.RT_start();
			else
				jst.RT_stop();
		}
	}
	
	public int getTaskRunningNum()
	{
		List<Task> jsts = TaskManager.getInstance().getTasks(this.getId());
		if (jsts == null || jsts.size() <= 0)
			return 0;

		int r = 0 ;
		for (Task jst : jsts)
		{
			if(jst.RT_isRunning())
				r ++ ;
		}
		return r;
	}

//	public class JSOb extends JSObMap //this will limit by JSObMap
//	{
//		
//
//		public Object JS_get(String key)
//		{
//			return UAPrj.this.JS_get(key) ;
//		}
//
//		public List<Object> JS_names()
//		{
//			return UAPrj.this.JS_names() ;
//		}
//		
//		public String toString()
//		{
//			return UAPrj.this.toString() ;
//		}
//	}
//
//	private transient JSOb jsOb = new JSOb();
//
//	public JSOb getJSOb()
//	{
//		return jsOb;
//	}

	@Override
	public String getResCxtId()
	{
		return this.getId();
	}

//	@Override
//	public String getResCxtName()
//	{
//		return "prj";
//	}

	@Override
	public String getResPrefix()
	{
		return IResCxt.PRE_PRJ;
	}

	
	private File resRootD = null ;
	private File refRootD = null ;
	
	@Override
	public File getResRootDir()
	{
		if(resRootD!=null)
			return resRootD;
		resRootD =  new File(this.getPrjSubDir(),"_res/");
		return resRootD;
	}
	
	@Override
	public File getRefRootDir()
	{
		if(refRootD!=null)
			return refRootD;
		refRootD =  new File(this.getPrjSubDir(),"_ref/");
		return refRootD;
	}
	
	static private List<String> refferNames = Arrays.asList(IResCxt.PRE_DEVDEF,IResCxt.PRE_COMP);

	@Override
	public List<String> getResRefferNames()
	{
		return refferNames;
	}
	
	@Override
	public IResNode getResNodeById(String res_id)
	{//get UADev
		for(UACh ch:this.getChs())
		{
			UADev d = ch.getDevById(res_id);
			if(d!=null)
				return d ;
		}
		return null ;
	}

	@Override
	public String getResNodeId()
	{
		return this.getId();
	}

	@Override
	public String getResNodeTitle()
	{
		return this.getTitle();
	}

	@Override
	public File getResNodeDir()
	{
		return this.getPrjSubDir() ;
	}

//	private transient ResDir resDir = null;
//
//	@Override
//	public String getResNodeId()
//	{
//		return this.getId();
//	}
//
//	@Override
//	public String getResNodeTitle()
//	{
//		return this.getTitle();
//	}
//
//	@Override
//	public IResNode getResNodeParent()
//	{
//		return UAManager.getInstance();
//	}
//
//	/**
//	 * 
//	 * @return
//	 */
//	@Override
//	public ResDir getResDir()
//	{
//		if (resDir != null)
//			return resDir;
//		File fsb = UAManager.getPrjFileSubDir(this.getId());
//		File dir = new File(fsb, "_res/");
//		if (!dir.exists())
//			dir.mkdirs();
//		resDir = new ResDir(this, this.getId(), this.getTitle(), dir);
//		return resDir;
//	}
//
//	@Override
//	public IResNode getResNodeSub(String subid)
//	{
//		return null;
//	}

	

	@HostAccess.Export
	public boolean JS_set_tag_val(String ch_name, String dev_name, String tagn, Object v)
	{
		UACh ch = UAPrj.this.getChByName(ch_name);
		if (ch == null)
			return false;
		UADev dev = ch.getDevByName(dev_name);
		if (dev == null)
			return false;
		UATag t = dev.getTagByName(tagn);
		if (t == null)
			return false;
		t.RT_writeVal(v);
		UAPrj.this.CXT_calMidTagsValLocal();
		return true;
	}

	@HostAccess.Export
	public String JS_get_rt_json_lastdt(Long lastdt) throws IOException
	{
		return JS_get_rt_json_lastdt(lastdt,false);
	}
	
	public String JS_get_rt_json_lastdt(Long lastdt,boolean ignore_sys_tag) throws IOException
	{
		StringWriter sw = new StringWriter();
		this.CXT_renderJson(sw, null,lastdt,null,ignore_sys_tag) ;
		return sw.toString();
	}
	
	@HostAccess.Export
	@JsDef(name="get_rt_json",title="get rt data",desc="get runtime data with json format")
	public String JS_get_rt_json() throws IOException
	{
		return JS_get_rt_json_lastdt(-1L) ;
	}
	
	public String JS_get_rt_json(boolean ignore_sys_tag) throws IOException
	{
		return JS_get_rt_json_lastdt(-1L, ignore_sys_tag) ;
	}
	
	@HostAccess.Export
	public String JS_filter_rt_by_extname(String mid_ext,String tag_ext) throws IOException
	{
		StringWriter sw = new StringWriter();
		UANodeFilter.JSON_renderMidNodesWithTagsByExtName(sw, UAPrj.this, mid_ext,tag_ext);
		return sw.toString();
	}
	
	@HostAccess.Export
	public String JS_get_def_json() throws IOException
	{
		StringWriter sw = new StringWriter();
		this.DEF_renderJson(sw,false,null) ;
		return sw.toString();
	}
	
	@HostAccess.Export
	public String JS_get_def_json_flat() throws IOException
	{
		StringWriter sw = new StringWriter();
		this.CXT_renderDefJsonFlat(sw,false) ;
		return sw.toString();
	}
	
	@HostAccess.Export
	public String JS_get_rt_json_flat() throws IOException
	{
		JSONArray jarr = new JSONArray() ;
		for(UATag tag : this.listTagsNorAll())
		{
			JSONObject rt = tag.RT_toFlatJson() ;
			jarr.put(rt) ;
		}
		return jarr.toString() ;
	}
	
	void RT_onTagValSet(UATag tag)
	{
		RecManager.getInstance(this).RT_fireUATagChanged(tag);
	}
	
	private void RT_initByNet()
	{
		List<UATag> alltags = this.listTagsAll();
		for(UATag tag:alltags)
		{
			tag.bNetMon=false;
		}
		
		for(MNNet net :MNManager.getInstance(this).listNets())
		{
			if(!net.isEnable())
				continue ;
			for(MNNode node:net.getNodeMapAll().values())
			{
				if(node instanceof NS_TagChgTrigger && node.isEnable())
				{
					NS_TagChgTrigger nnn = (NS_TagChgTrigger)node;
					List<String> ps = nnn.getTagPaths() ;
					if(ps==null||ps.size()<=0)
						continue ;
					
					for(String p:ps)
					{
						UATag tag = this.getTagByPath(p);
						if(tag==null)
							continue ;
						tag.bNetMon = true ;
					}
				}
			}
		}
	}
	
	void RT_onTagValChgedNetMon(UATag tag,boolean cur_valid,Object curv)
	{//RT_TAG_updt_valnotchg
		MNManager.getInstance(this).RT_TAG_valchged(tag,cur_valid, curv);
	}
	
	void RT_onTagValNotChgUpdateNetMon(UATag tag)
	{//RT_TAG_updt_valnotchg
		MNManager.getInstance(this).RT_TAG_updt_valnotchg(tag);
	}

	public void RT_onStationRecved(PStation pstation,String key,byte[] zipdata,JSONObject rt_jo,boolean b_his)
	{
		stationUpDT = System.currentTimeMillis() ;
		//this.RT_setSysTagVal("_station_up_dt", stationUpDT, true);
		if(tagStationUpDT!=null)
			tagStationUpDT.RT_setVal(stationUpDT);
	}
	
	
}
