package org.iottree.core;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.iottree.core.util.js.Debug;
import org.iottree.core.util.js.GSys;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.util.xmldata.data_val;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.cxt.JSProxyOb;
import org.iottree.core.cxt.JSProxyObGetter;
import org.iottree.core.cxt.UARtSystem;
import org.iottree.core.res.IResCxt;
import org.iottree.core.res.IResCxtRelated;
import org.iottree.core.res.ResCxt;
import org.iottree.core.res.ResCxtManager;
import org.json.JSONObject;

/**
 * UA Repository
 * 
 * @author zzj
 */
@data_class
public class UAPrj extends UANodeOCTagsCxt implements IRoot,IOCUnit, IOCDyn,IResCxt,IResCxtRelated,ISaver
{
	@data_obj(obj_c = UACh.class)
	List<UACh> chs = new ArrayList<>();
	
	@data_val(param_name = "max_id")
	int maxIdVal = 0 ;

	//@data_obj(obj_c = UAConn.class)
	//List<UAConn> conns = new ArrayList<>();



	/**
	 * javascript run with interval
	 */
	@data_val(param_name = "script")
	String script = null ;
	
	/**
	 * js run interval with ms
	 */
	@data_val(param_name = "script_int")
	long scriptInt = 10000 ;
	
	
	/**
	 * last script date time
	 */
	transient long scriptRunDT = System.currentTimeMillis() ;
	
	/**
	 * check js script ok or not
	 */
	private transient boolean jsSetOk = false;
	
	/**
	 * script set error when setup js script
	 */
	private transient String jsSetError  = null ;
	
	/**
	 * script run error 
	 */
	private transient String jsRunError  = null ;

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

	public String getRootIdPrefix()
	{
		return "r" ;
	}
	
	public int getRootNextIdVal()
	{
		maxIdVal ++ ;
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
	protected void copyTreeWithNewSelf(UANode new_self,String ownerid,boolean copy_id)
	{
		super.copyTreeWithNewSelf(new_self,ownerid,copy_id);
		UAPrj self = (UAPrj)new_self ;
		self.script = this.script ;
		self.scriptInt = this.scriptInt ;
		self.chs.clear();
		for(UACh ch:this.chs)
		{
			UACh nt = new UACh() ;
			ch.copyTreeWithNewSelf(nt,ownerid,copy_id);
			self.chs.add(nt) ;
		}
	}
	
	
	void onLoaded()
	{
		this.getResCxt() ;
		ResCxtManager.getInstance().setResCxtRelated(this);
	}

	public List<UANode> getSubNodes()
	{
		List<UANode> rets = super.getSubNodes();
		rets.addAll(chs);
		//rets.addAll(conns);
		//rets.addAll(stores);
		return rets;
	}
	
//	public List<UAMember> getMembers()
//	{
//		List<UAMember> rets = super.getMembers();
//		rets.addAll(conns) ;
//		return rets ;
//	}

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

	public UACh addCh(String drvname, String name, String title, String desc, HashMap<String, Object> uiprops)
			throws Exception
	{
		UAUtil.assertUAName(name);

		UACh ch = getChByName(name);
		if (ch != null)
		{
			throw new IllegalArgumentException("ch with name=" + name + " existed");
		}
		ch = new UACh(name, title, desc, drvname);
		if (uiprops != null)
		{
			for (Map.Entry<String, Object> n2v : uiprops.entrySet())
			{
				ch.OCUnit_setProp(n2v.getKey(), n2v.getValue());
			}
		}
		ch.id = this.getNextIdByRoot() ;
		// ch.belongTo = this;
		chs.add(ch);
		this.constructNodeTree();

		save();
		return ch;
	}
	

	public UACh updateCh(UACh ch,String drvname,String name,String title,String desc) throws Exception
	{
		UAUtil.assertUAName(name);
		
		UACh tmpch = this.getChByName(name) ;
		if(tmpch!=null&&tmpch!=ch)
			throw new IllegalArgumentException("ch with name="+name+" existed") ;
		ch.setNameTitle(name, title, desc);
		
		if(drvname.equals(ch.getDriverName()))
		{
			save();
			return ch;
		}
		ch.setDriverName(drvname) ;
		return ch ;
	}
	

	void delCh(UACh ch) throws Exception
	{
		chs.remove(ch);
		save();
	}


	public boolean chkValid()
	{
		return true;
	}

	private List<PropGroup> repPGS = null ;
	
	@Override
	public List<PropGroup> listPropGroups()
	{
		if(repPGS!=null)
			return repPGS;
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		List<PropGroup> lpgs = super.listPropGroups() ;
		if(lpgs!=null)
			pgs.addAll(lpgs) ;
		pgs.add(this.getRepPropGroup()) ;
		//
		
		repPGS = pgs;
		return pgs;
	}
	
	
	private PropGroup getRepPropGroup()
	{
		PropGroup r = new PropGroup("rep","Respository");
		
		r.addPropItem(new PropItem("script","JavaScript","JavaScript run interval by Channel",PValTP.vt_str,false,null,null,"")
				.withTxtMultiLine(true));
		
		r.addPropItem(new PropItem("script_int","JavaScript Interval","JavaScript run interval(ms)",PValTP.vt_int,false,null,null,"10000"));
		
		return r ;
	}
	
	public Object getPropValue(String groupn,String itemn)
	{
		if("rep".contentEquals(groupn))
		{
			switch(itemn)
			{
			case "script":
				return this.script ;
			case "script_int":
				return this.scriptInt ;
			}
		}
		return super.getPropValue(groupn, itemn);
	}
	
	public boolean setPropValue(String groupn,String itemn,String strv)
	{
		if("rep".contentEquals(groupn))
		{
			switch(itemn)
			{
			case "script":
				this.script = strv ;
				return true ;
			case "script_int":
				this.scriptInt = Long.parseLong(strv) ;
				return true ;
			}
			
		}
		return super.setPropValue(groupn, itemn,strv);
	}

	public void save() throws Exception
	{
		UAManager.getInstance().saveRep(this);
	}

	/**
	 * get rep sub dir which can save content related this rep e.g hmi
	 * 
	 * @return
	 */
	public File getRepSubDir()
	{
		return UAManager.getPrjFileSubDir(this.getId());
	}
	
	public File getSaverDir()
	{
		return getRepSubDir() ;
	}
	
	File getRepFile()
	{
		return UAManager.getPrjFile(this.id) ;
	}
	
	
	public long getSavedDT()
	{
		File f = getRepFile() ;
		if(!f.exists())
			return -1 ;
		return f.lastModified();
	}
	
	
	public List<ConnProvider> getConnProviders() throws Exception
	{
		return ConnManager.getInstance().getConnProviders(this.getId()) ;
	}
	
	public UAHmi findHmiById(String id)
	{
		UANode uan = this.findNodeById(id) ;
		if(uan==null||!(uan instanceof UAHmi))
		{
			return null ;
		}
		return (UAHmi)uan;
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
		return IOCBox.dynOCUnitToJSON(this,lastdt);
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
	
	protected void listTagsAll(List<UATag> tgs,boolean bmid)
	{
		if(bmid)
		{
			for(UATag tg:listTags())
			{
				if(tg.isMidExpress())
					tgs.add(tg) ;
			}
		}
		else
			tgs.addAll(this.listTags());
		 for(UACh d:chs)
		{
			d.listTagsAll(tgs,bmid);
		}
	 }

	/**
	 * 
	 * @return
	 */
	public JSONObject OC_getDynJSON(long lastdt)
	{
		JSONObject r = new JSONObject() ;
		r.put("brun", this.RT_isRunning()) ;
		return r;
	}

	@Override
	protected void onPropNodeValueChged()
	{
		setupJsScript() ;
	}

	@Override
	public boolean CXT_containsKey(String jsk)
	{
		if(jsk.startsWith("_"))
		{//system 
			switch(jsk)
			{
			case "_system":
				return true ;
			default:
				return false ;
			}
		}
		
		UACh ch = getChByName(jsk);
		return ch != null;
	}

	@Override
	public Object CXT_getByKey(String jsk)
	{
		if(jsk.startsWith("_"))
		{//system 
			switch(jsk)
			{
			case "_system":
				return new UARtSystem() ;
			default:
				return null ;
			}
		}
		UACh ch = getChByName(jsk);
		if (ch != null)
			return ch;
		return null;
	}
	
	
	private transient ResCxt resCxt = null ;

	/**
	 * name of editor which will use res
	 * @return
	 */
	public String getEditorName()
	{
		return "rep" ;
	}
	
	public String getEditorId()
	{
		return this.id ;
	}
	/**
	 * 
	 * @return
	 */
	@Override
	public ResCxt getResCxt()
	{
		if(resCxt!=null)
			return resCxt ;
		File fsb = UAManager.getPrjFileSubDir(this.getId()) ;
		File dir = new File(fsb,"_res/") ;
		if(!dir.exists())
			dir.mkdirs();
		resCxt=new ResCxt("rep",this.getId(),this.getTitle(),dir);
		return resCxt;
	}

	@Override
	public List<ResCxt> getResCxts()
	{
		ArrayList<ResCxt> rcs = new ArrayList<>(1) ;
		rcs.add(getResCxt()) ;
		return rcs;
	}

	
	// public Object JS_get(Object key)
	// {
	// String pn = (String)key ;
	// UACh ch = this.getChByName(pn) ;
	// if(ch!=null)
	// return ch ;
	// return null ;
	// }
	
	private Thread rtTh = null ;
	
	private boolean rtRun = false;
	
	synchronized public boolean RT_start()
	{
		if(rtTh!=null)
			return true;
		rtTh = new Thread(runner);
		rtRun = true;
		rtTh.start();
		return true;
	}

	public void RT_stop()
	{
		if(rtTh==null)
			return ;
		
		rtRun = false;
	}

	public boolean RT_isRunning()
	{
		return rtTh!=null;
	}
	
	private Runnable runner = new Runnable() {

		@Override
		public void run()
		{
			try
			{
				//StringBuilder failedr = new StringBuilder() ;
				
				while(rtRun)
				{
					try
					{
						Thread.sleep(5);
					}catch(Exception e) {}
					
					runScriptInterval() ;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				rtRun=false;
				rtTh = null ;
			}
		}
		
	};
	
	


	
	
	private ScriptEngine engine = null ;
	
	private ScriptEngine getJSEngine()
	{
		if(engine!=null)
			return engine ;
		
		ScriptEngineManager manager = new ScriptEngineManager();
		engine = manager.getEngineByName(JS_NAME);
		engine.put("polyglot.js.allowHostAccess", true);
		engine.put("polyglot.js.allowAllAccess",true);
		engine.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);
		
		return engine ;
	}
	
	
	boolean isJsSetOk()
	{
		return this.jsSetOk ;
	}
	
	/**
	 * 被外界调度的脚本运行过程
	 */
	
	private boolean setupJsScript()
	{
		if(script==null)
		{
			jsSetOk = false;
			return false;
		}
		if(script.equals("")||script.trim().equals(""))
		{
			jsSetOk = false;
			return false;
		}
		
		ScriptEngine engine = getJSEngine() ;
		try
		{
			engine.eval("function _rt_rep_script_run(){\r\n"
						 +script
						+"}\r\n");
			this.jsSetError = null ;
			//js set ok 
			
			//Bindings bds = engine.getBindings(ScriptContext.ENGINE_SCOPE);
			
			
			engine.put("$this", new JSOb());
			engine.put("$sys", new GSys());
			engine.put("$debug", new Debug());
			jsSetOk = true;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			this.jsSetError = ee.getMessage() ;
			jsSetOk = false;
		}
		return jsSetOk ;
	}
	
	/**
	 * get js setup error info
	 * @return
	 */
	public String getJsSetError()
	{
		return this.jsSetError ;
	}
	
	/**
	 * get js run error
	 * @return
	 */
	public String getJsRunError()
	{
		return this.jsRunError ;
	}

	void runScriptInterval()
	{
		if(!jsSetOk)
			return ;
		
		if(System.currentTimeMillis() - this.scriptRunDT<this.scriptInt)
			return ;//no run
		
		try
		{
			Invocable jsInvoke = (Invocable) getJSEngine();
			jsInvoke.invokeFunction("_rt_rep_script_run");
		}
		catch(ScriptException se)
		{
			jsRunError = se.getMessage() ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			this.scriptRunDT = System.currentTimeMillis() ;
		}
	}
	

	public class JSOb
	{
		public boolean set_tag_val(String ch_name,String dev_name,String tagn,Object v)
		{
			UACh ch = UAPrj.this.getChByName(ch_name) ;
			if(ch==null)
				return false;
			UADev dev = ch.getDevByName(dev_name);
			if(dev==null)
				return false;
			UATag t = null;//dev.getTagByName(tagn) ;
			if(t==null)
				return false;
			t.RT_setVal(v) ;
			UAPrj.this.CXT_calMidTagsValLocal();
			return true;
		}
	}

}
