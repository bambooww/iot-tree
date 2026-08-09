package org.iottree.core.msgnet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UAPrj;
import org.iottree.core.UAServer;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsDef;
import org.iottree.core.cxt.JsEnv;
import org.iottree.core.cxt.JsMethod;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.cxt.JsSub;
import org.iottree.core.cxt.JsSubOb;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.msgnet.annotion.outer_api;
import org.iottree.core.plugin.PlugJsApi;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.Lan;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * a base may be db ,mqtt client which has one or more node related
 * @author jason.zhu
 *
 */
public abstract class MNBase extends MNCxtPk implements ILang
{
	private static Lan lan = Lan.getLangInPk(MNBase.class) ;
	
	public static class OuterApi implements Comparable<OuterApi>
	{
		public static final String MN_OUTER_API_PRE = "/_mn_api";
		
		public static final String MN_OUTER_API = "_mn_api";
		
		final MNBase owner ;
		
		String name ;
		
		String title_en ;
		
		String title_cn ;
		
		String desc_en ;
		String desc_cn ;
		
		Method method ;
		
		outer_api oa ;
		
//		OuterApi(String n,String t,String d,Method m)
//		{
//			this.name = n ;
//			this.title = t ;
//			this.desc = d ;
//			this.method = m ;
//		}
		
		OuterApi(MNBase owner,outer_api oa,Method m)
		{
			this.owner = owner ;
			this.oa = oa ;
			this.name = oa.name() ;
			this.title_cn = oa.title_cn() ;
			this.title_en = oa.title_en() ;
			this.desc_en = oa.desc_en() ;
			this.desc_cn = oa.desc_cn() ;
			
//			if("cn".equals(Lan.getUsingLang()))
//			{
//				
////				if(Convert.isNullOrEmpty(this.title))
////					this.title = oa.title_en() ;
////				if(Convert.isNullOrEmpty(this.desc))
////					this.desc = oa.desc_en() ;
//			}
//			else
//			{
//				this.title = oa.title_en() ;
//				this.desc = oa.desc_en() ;
//			}
			
			if(Convert.isNullOrEmpty(this.name))
				this.name = m.getName() ;
			
//			if(Convert.isNullOrEmpty(this.title))
//				this.title = m.getName() ;
			
			this.method = m ;
			
			//owner.get
		}
		
		public MNBase getOwner()
		{
			return this.owner ;
		}
		
		public MNNet getBelongToNet()
		{
			return this.owner.getBelongTo() ;
		}
		
		public UAPrj getBelongToPrj()
		{
			return this.owner.getPrj() ;
		}
		
		public String getApiUID()
		{
			return this.getBelongToPrj().getName()+"."+this.getBelongToNet().getName()
			+"."+this.owner.getName() +"."+this.name;
		}
		
		public String getAccessPath()
		{
			return MN_OUTER_API_PRE+"/"+this.getBelongToPrj().getName()+"/"+this.getBelongToNet().getName()
					+"/"+this.owner.getName() +"/"+this.name;
		}
		
		public String getSubPathIn(String prj_n,String net_n,String node_n)
		{
			if(Convert.isNullOrEmpty(prj_n))
				return "/"+this.getBelongToPrj().getName()+"/"+this.getBelongToNet().getName()
						+"/"+this.owner.getName() +"/"+this.name;
			if(Convert.isNullOrEmpty(net_n))
				return "/"+this.getBelongToNet().getName()
						+"/"+this.owner.getName() +"/"+this.name;
			if(Convert.isNullOrEmpty(node_n))
				return "/"+this.owner.getName() +"/"+this.name;
			return "/"+this.name;
		}
		
		public JSONObject[] getSubInOutSampleIn(String prj_n,String net_n,String node_n)
		{
			JSONObject in_jo = new JSONObject() ;
			JSONObject out_jo = new JSONObject() ;
			if(Convert.isNullOrEmpty(prj_n))
			{
				String n = this.getBelongToPrj().getName();
				in_jo.put("prj_n", n) ;
				out_jo.put("prj_n", n) ;
			}
			if(Convert.isNullOrEmpty(net_n))
			{
				String n = this.getBelongToNet().getName();
				in_jo.put("net_n", n) ;
				out_jo.put("net_n", n) ;
			}
			if(Convert.isNullOrEmpty(node_n))
			{
				String n = this.owner.getName();
				in_jo.put("node_n", n) ;
				out_jo.put("node_n", n) ;
			}
			in_jo.put("api_n", this.name);
			out_jo.put("api_n", this.name) ;
			
			Object[] inout = this.owner.getOuterApiIOSample(this.name) ;
			if(inout!=null)
			{
				if(inout.length>0)
					in_jo.putOpt("api_in", inout[0]) ;
				if(inout.length>1)
					out_jo.putOpt("api_out", inout[1]) ;
			}
			return new JSONObject[] {in_jo,out_jo};
		}
		
		private Object[] getApiSampleLoc()
		{
			return new Object[] {oa.in_sample(),oa.out_sample()} ;
		}
		
		public Object[] getApiSample()
		{
			return this.owner.getOuterApiIOSample(this.name) ;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public String getTitle()
		{
			if("cn".equals(Lan.getUsingLang()))
			{
				if(Convert.isNotNullEmpty(this.title_cn))
					return this.title_cn ;
			}
			
			if(Convert.isNotNullEmpty(this.title_en))
				return this.title_en ;
			return this.name ;
		}
		
		public String getDesc()
		{
			if("cn".equals(Lan.getUsingLang()))
			{
				if(Convert.isNotNullEmpty(this.desc_cn))
					return this.desc_cn ;
			}
			
			if(Convert.isNotNullEmpty(this.desc_en))
				return this.desc_en ;
			return null;
		}
		
		public Method getMethod()
		{
			return this.method ;
		}

		@Override
		public int compareTo(OuterApi o)
		{
			return this.name.compareTo(o.name);
		}
		
		public JSONObject toListJO()
		{
			return new JSONObject().put("api_n", this.name).put("api_t", this.getTitle()).putOpt("api_desc", this.getDesc()).put("path", this.getAccessPath())
					.put("node_n", this.owner.getName()).put("net_n", this.getBelongToNet().getName())
					.put("prj_n",this.getBelongToPrj().getName()).put("uid", this.getApiUID());
		}
		
		public JSONObject toDetailJO()
		{
			JSONObject ret = this.toListJO() ;
			Object[] sp_io = this.getApiSample() ;
			if(sp_io!=null)
			{
				ret.putOpt("sample_in",sp_io[0]) ;
				ret.putOpt("sample_out",sp_io[1]) ;
			}
			return ret ;
		}
		
		
		
		private OuterApiLog lastCallLog = null ;
		
		public Object RT_call(JSONObject inputjo,StringBuilder failedr)
		//	throws Exception
		{
			try
			{
				long calldt = System.currentTimeMillis() ;
				Object retob = this.method.invoke(this.owner, inputjo,failedr) ;
				long retdt = System.currentTimeMillis() ;
				lastCallLog = new OuterApiLog(calldt,inputjo,retdt,retob) ;
				return retob ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				failedr.append(ee.getMessage()) ;
				return null ;
			}
		}
		
		public OuterApiLog RT_getLastCallLog()
		{
			return this.lastCallLog ;
		}
		
		public static JSONArray RT_callInServer(JSONArray req_jarr,StringBuilder failedr)
		{
			JSONArray ret_jarr = new JSONArray() ;
			int n = req_jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = req_jarr.getJSONObject(i) ;
				String prj_n = tmpjo.optString("prj_n") ;
				String net_n = tmpjo.optString("net_n") ;
				String node_n = tmpjo.optString("node_n") ;
				String api_n = tmpjo.optString("api_n") ;
				if(Convert.isNullOrEmpty(prj_n)||Convert.isNullOrEmpty(net_n)
						||Convert.isNullOrEmpty(node_n)||Convert.isNullOrEmpty(api_n))
					continue ;
				UAPrj prj = UAManager.getInstance().getPrjByName(prj_n) ;
				if(prj==null)
					continue ;
				MNNet net = prj.getMNManager().getNetByName(net_n) ;
				if(net==null)
					continue ;
				MNBase node = net.getItemByName(node_n) ;
				if(node==null)
					continue ;
				OuterApi oa = node.getUsingOuterApi(api_n) ;
				if(oa==null)
				{
					ret_jarr.put(new JSONObject().put("api_n",api_n).put("api_out_err","no using api found with name="+api_n)) ;
					continue ;
				}
				
				JSONObject in_jo = tmpjo.optJSONObject("api_in") ;
				doOuterApi(oa,in_jo,ret_jarr) ;
			}
			
			return ret_jarr ;
		}
		
		public static JSONArray RT_callInPrj(UAPrj prj,JSONArray req_jarr,StringBuilder failedr)
		{
			JSONArray ret_jarr = new JSONArray() ;
			int n = req_jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = req_jarr.getJSONObject(i) ;
				String net_n = tmpjo.optString("net_n") ;
				String node_n = tmpjo.optString("node_n") ;
				String api_n = tmpjo.optString("api_n") ;
				if(Convert.isNullOrEmpty(net_n)||Convert.isNullOrEmpty(node_n)||Convert.isNullOrEmpty(api_n))
					continue ;
				MNNet net = prj.getMNManager().getNetByName(net_n) ;
				if(net==null)
					continue ;
				MNBase node = net.getItemByName(node_n) ;
				if(node==null)
					continue ;
				OuterApi oa = node.getUsingOuterApi(api_n) ;
				if(oa==null)
				{
					ret_jarr.put(new JSONObject().put("api_n",api_n).put("api_out_err","no using api found with name="+api_n)) ;
					continue ;
				}
				
				JSONObject in_jo = tmpjo.optJSONObject("api_in") ;
				doOuterApi(oa,in_jo,ret_jarr) ;
			}
			
			return ret_jarr ;
		}
		
		public static JSONArray RT_callInNet(MNNet net,JSONArray req_jarr,StringBuilder failedr)
		{
			JSONArray ret_jarr = new JSONArray() ;
			int n = req_jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = req_jarr.getJSONObject(i) ;
				String node_n = tmpjo.optString("node_n") ;
				String api_n = tmpjo.optString("api_n") ;
				if(Convert.isNullOrEmpty(node_n)||Convert.isNullOrEmpty(api_n))
					continue ;
				MNBase node = net.getItemByName(node_n) ;
				if(node==null)
					continue ;
				OuterApi oa = node.getUsingOuterApi(api_n) ;
				if(oa==null)
				{
					ret_jarr.put(new JSONObject().put("api_n",api_n).put("api_out_err","no using api found with name="+api_n)) ;
					continue ;
				}
				
				JSONObject in_jo = tmpjo.optJSONObject("api_in") ;
				doOuterApi(oa,in_jo,ret_jarr) ;
			}
			
			return ret_jarr ;
		}
		

		public static JSONArray RT_callInNode(MNBase node,JSONArray req_jarr,StringBuilder failedr)
		{
			JSONArray ret_jarr = new JSONArray() ;
			int n = req_jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = req_jarr.getJSONObject(i) ;
				String api_n = tmpjo.optString("api_n") ;
				if(Convert.isNullOrEmpty(api_n))
					continue ;
				OuterApi oa = node.getUsingOuterApi(api_n) ;
				if(oa==null)
				{
					ret_jarr.put(new JSONObject().put("api_n",api_n).put("api_out_err","no using api found with name="+api_n)) ;
					continue ;
				}
				
				JSONObject in_jo = tmpjo.optJSONObject("api_in") ;
				doOuterApi(oa,in_jo,ret_jarr) ;
			}
			
			return ret_jarr ;
		}
		
		private static void doOuterApi(OuterApi oa,JSONObject in_jo,JSONArray ret_jarr)
		{
			StringBuilder ffr = new StringBuilder() ;
			JSONObject retjo = new JSONObject().put("api_n",oa.getName());
			MNBase node = oa.getOwner() ;
			MNNet net = node.getBelongTo() ;
			UAPrj prj = node.getPrj() ;
			retjo.put("prj_n", prj.getName()).put("net_n", net.getName()).put("node_n", node.getName()) ;
			
			Object retob = oa.RT_call(in_jo, ffr) ;
			if(retob==null)
			{
				ret_jarr.put(retjo.put("api_out_err",ffr.toString())) ;
				return ;
			}
			ret_jarr.put(retjo.put("api_out",retob)) ;//succ out
		}
		
		public static JSONArray RT_callInPath(String uri_path,JSONArray req_jarr,StringBuilder failedr)
		{
			List<String> ss = Convert.splitStrWith(uri_path, "./");
			int plen = 0;
			if(ss!=null)
				plen = ss.size() ;
			UAPrj prj = null;
			MNNet net = null ;
			MNBase node = null ;
			if(plen>0)
			{
				prj = UAManager.getInstance().getPrjByName(ss.get(0)) ;
				if(prj==null)
				{
					failedr.append("no prj found") ;
					return null ;
				}
			}
			if(plen>1)
			{
				net = prj.getMNManager().getNetByName(ss.get(1));
				if(net==null)
				{
					failedr.append("no net found") ;
					return null ;
				}
			}
			if(plen>2)
			{
				node = net.getItemByName(ss.get(2));
				if(node==null)
				{
					failedr.append("no node found") ;
					return null ;
				}
			}
			
			switch(plen)
			{
			case 0:
				return RT_callInServer(req_jarr,failedr) ;
			case 1:
				return RT_callInPrj(prj, req_jarr, failedr);
			case 2:
				return RT_callInNet(net,req_jarr,failedr);
			case 3:
				return RT_callInNode(node,req_jarr,failedr) ;
			default:
				failedr.append("only support in server prj net node") ;
				return null ;
			}
		}
		//
		
		public static OuterApi getOuterApiByUID(String uid)
		{
			List<String> ss = Convert.splitStrWith(uid, ".") ;
			if(ss.size()!=4)
				return null ;
			UAPrj prj = UAManager.getInstance().getPrjByName(ss.get(0)) ;
			if(prj==null)
				return null ;
			MNNet net = prj.getMNManager().getNetByName(ss.get(1)) ;
			if(net==null)
				return null ;
			MNBase node  = net.getItemByName(ss.get(2)) ;
			if(node==null)
				return null ;
			return node.getOuterApi(ss.get(3)) ;
		}
		
		public static LinkedHashMap<String,OuterApi> listSubApisAll()
		{
			return listSubApis(null,null,null) ;
		}
		
		public static LinkedHashMap<String,OuterApi> listSubApis(String prj_n,String net_n,String node_n)
		{
			LinkedHashMap<String,OuterApi> rets = new LinkedHashMap<>() ;
			for(UAPrj prj:UAManager.getInstance().listPrjs())
			{
				String pn = prj.getName() ;
				if(Convert.isNotNullEmpty(prj_n))
				{
					 if(!prj_n.equals(pn))
						 continue ;
				}
				
				for(MNNet net:prj.listMNNetsAll())
				{
					if(Convert.isNotNullEmpty(net_n))
					{
						if(!net_n.equals(net.getName()))
							continue ;
					}
					
					for(MNBase node:net.getNamedItemsAll().values())
					{
						if(Convert.isNotNullEmpty(node_n))
						{
							if(!node_n.equals(node.getName()))
								continue ;
						}
						
						Collection<OuterApi> oas = node.getUsingOuterApis().values() ;
						for(OuterApi oa:oas)
						{
							rets.put(oa.getApiUID(),oa) ;
						}
					}
				}
			}
			return rets ;
		}
		
		public static JSONArray listSubApiJArr(String prj_n,String net_n,String node_n)
		{
			JSONArray ret = new JSONArray() ;
			for(OuterApi api:listSubApis(prj_n,net_n,node_n).values())
			{
				JSONObject tmpjo = api.toListJO() ;
				String subp = api.getSubPathIn(prj_n,net_n,node_n);
				tmpjo.put("sub_path", subp) ;
				MNBase node = api.owner ;
				tmpjo.put("node_tpt",node.getTPTitle()) ;
				ret.put(tmpjo) ;
			}
			return ret ;
		}
		
		public static Object[] getApiInOutSample(String api_uid)
		{
			OuterApi oa = getOuterApiByUID(api_uid) ;
			if(oa==null)
				return null ;
			return oa.owner.getOuterApiIOSample(oa.name) ;
		}
		
		public static JSONArray[] listSubApiInOutSample(String prj_n,String net_n,String node_n,
				List<String> sub_uids)
		{
			JSONArray in = new JSONArray() ;
			JSONArray out = new JSONArray() ;
			if(sub_uids==null||sub_uids.size()<=0)
				return new JSONArray[] {in,out};
			LinkedHashMap<String,OuterApi> uid2oa = listSubApis(prj_n,net_n,node_n);
			if(uid2oa==null||uid2oa.size()<=0)
				return new JSONArray[] {in,out};
			
			for(OuterApi oa:uid2oa.values())
			{
				String uid = oa.getApiUID() ;
				if(!sub_uids.contains(uid))
					continue ;
				JSONObject[] inout = oa.getSubInOutSampleIn(prj_n,net_n,node_n);
				in.put(inout[0]);
				out.put(inout[1]);
			}
			
			return new JSONArray[] {in,out};
		}
	}
	
	public static class OuterApiLog
	{
		public long callDT = -1 ;
		
		public long retDT = -1 ;
		
		public JSONObject inputJO ;
		
		public Object retOb ;
		
		OuterApiLog(long calldt,JSONObject inputjo,long retdt,Object retob)
		{
			this.callDT = calldt;
			this.inputJO = inputjo ;
			this.retDT = retdt ;
			this.retOb = retob;
		}
		
		public long getCostMS()
		{
			return this.retDT - this.callDT ;
		}
	}
	
	public static class Widget implements Comparable<Widget>
	{
		String name ;
		
		String title ;
		
		String desc ;
		
		Widget(outer_api oa)
		{
			this.name = oa.name() ;
			if("cn".equals(Lan.getUsingLang()))
			{
				this.title = oa.title_cn() ;
				this.desc = oa.desc_cn() ;
				if(Convert.isNullOrEmpty(this.title))
					this.title = oa.title_en() ;
				if(Convert.isNullOrEmpty(this.desc))
					this.desc = oa.desc_en() ;
			}
			else
			{
				this.title = oa.title_en() ;
				this.desc = oa.desc_en() ;
			}
			
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public String getTitle()
		{
			return this.title ;
		}
		
		public String getDesc()
		{
			return desc ;
		}
		
		@Override
		public int compareTo(Widget o)
		{
			return this.name.compareTo(o.name);
		}
		
	}
	
	String id = IdCreator.newSeqId() ;
	
	String title = "" ;
	
	String name = null ; //unique var name in net
	
	String desc = "";
	
	/**
	 * Set some markers to support node positioning through marks
	 * In this way, the top-level business system can utilize nodes in msgnet as more precise configuration information
	 * In addition to filtering and searching through node types, you can also use marks for more accurate filtering
	 */
	List<String> marks = null ;
	
	MNNet belongTo = null ;
	
	MNCat cat = null ;
	
	float x = 0 ;
	float y = 0 ;
	
	boolean bEnable = true ;
	
	boolean bShowRT = false;
	
	/**
	 * implements IMNNodeRes,this var can be used
	 */
	String resName = null ;
	
	/**
	 * show output title or not
	 */
	boolean bShowOutTitle = getShowOutTitleDefault();
	
	
	HashSet<String> usingOANames = null ;
//	private String nodeTp = null ;
//	
//	private String nodeTpT = null ;
	
	private transient LinkedHashMap<String,OuterApi> usingOAs = null ;


	public MNBase()
	{
	}
	
	/**
	 * when driver is loaded,it will check env to decide it will be used
	 * @return
	 */
	protected boolean ENV_check()
	{
		return true ;
	}
	
	void setCat(MNCat cat)
	{
		this.cat = cat ;
	}
	
	public MNCat getCat()
	{
		return this.cat ;
	}
	
	public String TP_getParamUrl()
	{
		return this.cat.getParamUrl(this) ;
	}
	
	public String TP_getDocUrl()
	{
		return this.cat.getDocUrl(this) ;
	}

	@JsDef
	public String getId()
	{
		return this.id ;
	}
	
	@Override
	public String CXT_getUID()
	{
		MNNet net = this.getBelongTo() ;
		IMNContainer cont = net.getContainer() ;
		return cont.getMsgNetContainerId()+"-"+ net.getId()+"-"+this.id ;
	}
	
	public UAPrj getPrj()
	{
		return this.belongTo.belongTo.getBelongToPrj() ;
	}
	
	@JsDef
	public String getTitle()
	{
		return title ;
	}
	
	public void setTitle(String t)
	{
		this.title = t ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getDesc()
	{
		return desc ;
	}
	
	public List<String> getMarks()
	{
		return this.marks ;
	}
	
	public boolean hasMark(String mark)
	{
		if(this.marks==null)
			return false;
		return this.marks.contains(mark) ;
	}
	
	public MNNet getBelongTo()
	{
		return this.belongTo ;
	}
	
	
	public float getX()
	{
		return x ;
	}
	public float getY()
	{
		return  y;
	}
	
	@JsDef
	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public boolean isShowOutTitle()
	{
		return this.bShowOutTitle;
	}
	
	public boolean getShowOutTitleDefault()
	{
		return false;
	}
	
	public boolean isShowRT()
	{
		return this.bShowRT ;
	}
	
	public boolean isRunner()
	{
		return this instanceof IMNRunner;
	}
	
	public String getTPFull()
	{
		String ownn = this.getOwnerTP() ;
		if(Convert.isNullOrEmpty(ownn))
			return this.cat.getName()+"."+this.getTP() ;
		else
			return this.cat.getName()+"."+ownn+"."+this.getTP() ;
	}
	
	public String getCatName()
	{
		return this.cat.getName() ;
	}
	
	public String getTpFullInCat()
	{
		String ownn = this.getOwnerTP() ;
		if(Convert.isNullOrEmpty(ownn))
			return this.getTP() ;
		else
			return ownn+"."+this.getTP() ;
	}
	
	protected abstract String getOwnerTP() ;

	@JsDef
	public abstract String getTP() ;
	
	
	@JsDef
	public abstract String getTPTitle();
	
	public String getTPDesc()
	{
		return g(getTP(),"desc","") ;
	}
	
	public boolean isFitForPrj(UAPrj prj)
	{
		return true ;
	}
	
	abstract MNBase createNewIns(MNNet net) throws Exception ;
//	{
//		if(Convert.isNotNullEmpty(this.nodeTpT))
//			return this.nodeTpT ;
//		
//		return g_def(this.nodeTp,this.nodeTp) ;
//	}
	
//	void setNodeTP(String tp,String tpt)
//	{
//		this.nodeTp = tp ;
//		this.nodeTpT = tpt ;
//	}


	
	public abstract String getColor() ;
	
	public abstract String getIcon() ;
	
	public String getTitleColor()
	{
		return null ;
	}
	
	protected boolean supportCxtVars()
	{
		return false ;
	}
	
	public String getPmTitle()
	{
		return null ;
	}
	/**
	 * 判断节点参数是否完备，只有完备之后的节点才可以运行
	 * @return
	 */
	public abstract boolean isParamReady(StringBuilder failedr);
	
	//to be override
	public abstract JSONObject getParamJO();
	
//	//to be override
//	final void setParamJO(JSONObject jo)
//	{
//		setParamJO(jo,System.currentTimeMillis()) ;
//	}
	
	protected abstract void setParamJO(JSONObject jo);
	
	/**
	 * Node impl IMNNodeRes ,it will be used
	 * @return
	 */
	public String getMNResName()
	{
		return this.resName ;
	}
	
	private transient LinkedHashMap<String,OuterApi> outerApiAll = null ;
	
	public LinkedHashMap<String,OuterApi> listOuterApiAll()
	{
		if(this.outerApiAll!=null)
			return outerApiAll;
		LinkedHashMap<String,OuterApi> rets = new LinkedHashMap<>() ;
		ArrayList<OuterApi> ss = new ArrayList<>() ;
		for(Method m:this.getClass().getMethods())
		{
			outer_api oa = m.getAnnotation(outer_api.class) ;
			if(oa==null)
				continue ;
			ss.add(new OuterApi(this,oa,m)) ;
		}
		Collections.sort(ss);
		for(OuterApi oa:ss)
			rets.put(oa.getName(),oa) ;
		return outerApiAll = rets;
	}
	
	public OuterApi getOuterApi(String apin)
	{
		return listOuterApiAll().get(apin) ;
	}
	
	public final Object[] getOuterApiIOSample(String apin)
	{
		Object[] iosp = extOuterApiIOSample(apin) ;
		if(iosp!=null)
			return iosp ;
		
		OuterApi oa = this.listOuterApiAll().get(apin) ;
		if(oa==null)
			return null ;
		return oa.getApiSampleLoc() ;
	}
	
	protected Object[] extOuterApiIOSample(String apin)
	{
		return null ;
	}
	
	public HashSet<String> getUsingOuterApiNames()
	{
		return this.usingOANames ;
	}
	
	public synchronized LinkedHashMap<String,OuterApi> getUsingOuterApis()
	{
		if(this.usingOAs!=null)
			return this.usingOAs ;
		LinkedHashMap<String,OuterApi> ret = new LinkedHashMap<>() ;
		if(this.usingOANames==null||this.usingOANames.size()<=0)
			return this.usingOAs = ret ;
		LinkedHashMap<String,OuterApi> n2oa = listOuterApiAll() ;
		for(String n:this.usingOANames)
		{
			OuterApi oa = n2oa.get(n) ;
			if(oa==null)
				continue ;
			ret.put(oa.getName(),oa) ;
		}
		return this.usingOAs = ret ;
	}
	
	public OuterApi getUsingOuterApi(String name)
	{
		return getUsingOuterApis().get(name) ;
	}
	
	final void setDetailJO(JSONObject jo) throws MNException
	{
		String nn = jo.optString("name") ;
		if(Convert.isNotNullEmpty(nn))
		{
			StringBuilder failedr = new StringBuilder() ;
			if(!Convert.checkVarName(nn, true, failedr))
				throw new MNException("name="+nn+" invalid "+failedr) ;
			MNBase oldb = this.belongTo.getItemByName(nn) ;
			if(oldb!=null&&oldb!=this)
				throw new MNException("name="+nn+" is already existed in net") ;
		}
		this.name = nn ;
		
		JSONObject pm_jo = jo.getJSONObject("pm_jo");
		setParamJO(pm_jo);
		
		this.bEnable = jo.optBoolean("enable",true) ;
		this.bShowOutTitle = jo.optBoolean("show_out_tt",false);
		this.title = jo.optString("title","") ;
		this.marks = Convert.splitStrWith(jo.optString("marks"), ",|") ;
		this.desc = jo.optString("desc","") ;
		this.resName = jo.optString("res_name") ;
		
		JSONArray outapis_jarr = jo.optJSONArray("outer_apis") ;
		int n ;
		if(outapis_jarr!=null && (n=outapis_jarr.length())>0)
		{
			this.usingOANames = new HashSet<>() ;
			for(int i = 0 ; i < n ; i ++)
			{
				String tmpn = outapis_jarr.getString(i) ;
				this.usingOANames.add(tmpn) ;
			}
		}
		else
		{
			this.usingOANames = null;
		}
		
//		if(this instanceof MNNodeRes)
//		{
//			MNNodeRes nres = (MNNodeRes)this ;
//			nres.callerUID = jo.optString("caller_uid") ;
//		}
		//other may be icon color etc
		clearCache();
		this.belongTo.clearCache();
	}
	
	protected synchronized void clearCache()
	{
		this.usingOAs=null ;
	}
	
	public void renderOut(Writer w)
	{
		JSONObject jo = toJO() ;
		jo.write(w) ; 
	}
	
	public JSONObject toListJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", this.id) ;
		String tt = this.getTitle() ;
		if(Convert.isNullOrEmpty(tt))
			tt = this.getTPTitle() ;
		jo.putOpt("title", tt) ;
		jo.putOpt("name", this.name) ;
		jo.putOpt("marks", Convert.combineStrWith(this.marks, ',')) ;
		jo.putOpt("desc", desc);
		jo.put("_tp", getTPFull()) ;
		jo.put("tpt", getTPTitle()) ;
		if(this.cat!=null)
		{
			jo.put("cat", this.cat.getName()) ;
			jo.putOpt("catt", this.cat.getTitle()) ;
		}
//		jo.put("uid", this.getUID());
		jo.put("x", this.x) ;
		jo.put("y", this.y) ;
		jo.put("enable", this.bEnable) ;
		jo.put("show_rt", this.bShowRT) ;
		jo.put("show_out_tt",this.bShowOutTitle);
		jo.put("color", this.getColor()) ;
		jo.putOpt("tcolor", this.getTitleColor()) ;
		jo.put("icon", this.getIcon()) ;
		
		jo.put("outer_api_total", listOuterApiAll().size()) ;
		jo.put("outer_api_num", 0) ;
		if(this.usingOANames!=null&&this.usingOANames.size()>0)
		{
			jo.put("outer_apis", this.usingOANames) ;
			jo.put("outer_api_num", this.usingOANames.size()) ;
		}

		if(this instanceof IMNRunner)
		{
			IMNRunner rr = (IMNRunner)this;
			jo.put("runner", isRunner()) ;
			jo.put("runner_en", rr.RT_runnerEnabled()) ;
			jo.put("runner_in", rr.RT_runnerStartInner()) ;
		}
		if(this instanceof MNNodeState)
		{
			jo.put("_state",true) ;
		}
		return jo ;
	}

	public JSONObject toJO()
	{
		JSONObject jo = this.toListJO() ;
		
		JSONObject cxtdefjo = this.CXT_getDefJO();
		jo.putOpt("cxt_def", cxtdefjo) ;
		
		JSONObject pmjo = this.getParamJO() ;
		jo.putOpt("pm_jo", pmjo) ;
		//jo.put("pm_need", this.needParam()) ;
		StringBuilder sb = new StringBuilder() ;
		boolean br = this.isParamReady(sb);
		jo.put("pm_ready", br) ;
		jo.putOpt("pm_title", this.getPmTitle()) ;
		if(!br)
			jo.put("pm_err", sb.toString()) ;
		else
			jo.put("pm_err", "") ;
		
		jo.putOpt("res_name", resName) ;
		return jo;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.id = jo.getString("id") ;
		this.title = jo.optString("title") ;
		this.name = jo.optString("name") ;
		this.desc = jo.optString("desc") ;
		this.marks = Convert.splitStrWith(jo.optString("marks"), ",|") ;
		this.x = jo.optFloat("x",0) ;
		this.y = jo.optFloat("y",0) ;
		this.bEnable = jo.optBoolean("enable",true) ;
		this.bShowRT = jo.optBoolean("show_rt",false) ;
		this.bShowOutTitle = jo.optBoolean("show_out_tt",false) ;
		
		this.resName = jo.optString("res_name") ;
		
		JSONArray outapis_jarr = jo.optJSONArray("outer_apis") ;
		int n ;
		if(outapis_jarr!=null && (n=outapis_jarr.length())>0)
		{
			this.usingOANames = new HashSet<>() ;
			for(int i = 0 ; i < n ; i ++)
			{
				String tmpn = outapis_jarr.getString(i) ;
				this.usingOANames.add(tmpn) ;
			}
		}
		
		JSONObject pmjo = jo.optJSONObject("pm_jo") ;
		if(pmjo!=null)
		{
			//long updt = this.belongTo.updateDT ;
			this.setParamJO(pmjo);
		}
		
		JSONObject cxt_def = jo.optJSONObject("cxt_def") ;
		if(cxt_def!=null)
		{
			this.CXT_setDefJO(cxt_def) ;
		}
		
		return true;
	}
	
	protected boolean fromJOBasic(JSONObject jo,StringBuilder failedr)
	{
		this.x = jo.optFloat("x", this.x) ;
		this.y = jo.optFloat("y", this.y) ;
		this.bShowRT = jo.optBoolean("show_rt",false) ;
		//this.title = jo.optString("title") ;
		//this.desc = jo.optString("desc") ;
		//this.bStart = jo.optBoolean("b_start",false) ;
		return true ;
	}
	
	protected void onAfterLoaded()
	{}
	
	// -- RT
	
	protected void RT_onBeforeNetRun()
	{}
	
	protected void RT_onAfterNetRun()
	{}
	
	protected void RT_onAfterNetStop()
	{}
	
	//override to do something by net tick 1 second interval
	protected void RT_onNetTick1S()
	{}
	
	public void RT_clean()
	{
		this.RT_CXT_clean();
	}
	
	protected RTDebug RT_DEBUG_INF = new RTDebug(this,"inf","rgba(0,0,255,0.3)") ;
	protected RTDebug RT_DEBUG_WARN = new RTDebug(this,"warn","rgba(255,255,0,0.3)") ;
	protected RTDebug RT_DEBUG_ERR = new RTDebug(this,"err","rgba(255,0,0,0.3)") ;
	
//	HashMap<String,RTDebugPrompt> RT_tp2pptInf = new HashMap<>() ;
//	HashMap<String,RTDebugPrompt> RT_tp2pptWarn = new HashMap<>() ;
//	HashMap<String,RTDebugPrompt> RT_tp2pptErr = new HashMap<>() ;
//	
//	public final boolean RT_hasPromptWarn()
//	{
//		return RT_tp2pptWarn.size()>0 ;
//	}
//	
//	public final boolean RT_hasPromptErr()
//	{
//		return RT_tp2pptErr.size()>0 ;
//	}
	
	public RTDebug RT_DEBUG_getByLvl(String lvl)
	{
		switch(lvl)
		{
		case "err":
			return RT_DEBUG_ERR;
		case "warn":
			return RT_DEBUG_WARN ;
		case "inf":
			return RT_DEBUG_INF ;
		default:
			return null ;
		}
	}
	
	public static class DivBlk
	{
		String blk ;
		
		String div ;
		
		public DivBlk(String blk,String div)
		{
			this.blk = blk ;
			this.div = div ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("blk",this.blk) ;
			jo.put("div", div) ;
			return jo ;
		}
	}
	
	/**
	 * has panel = true will make node detail has iframe xxx.xxx.rt.jsp.
	 * it can support special runtime display or control send output msg etc. 
	 * @return height% in div blk
	 */
	public int RT_hasPanel()
	{
		return 0;
	}
	
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		//check rt
		int panel_h = this.RT_hasPanel() ;
		if(panel_h>0)
		{
			String rt_panel_url = this.getCat().getRTPanelUrl(this) ;
			if(Convert.isNotNullEmpty(rt_panel_url))
			{
				int k = rt_panel_url.lastIndexOf('/') ;
				String url_base = null ;
				if(k>0)
					url_base = rt_panel_url.substring(0,k) ;
				
				k = rt_panel_url.lastIndexOf('?') ;
				String itemid = this.getId() ;
				String netid = this.getBelongTo().getId() ;
				String cid = this.getBelongTo().getContainer().getMsgNetContainerId() ;
				if(k<=0)
					rt_panel_url+="?container_id="+cid+"&netid="+netid+"&itemid="+itemid ;
				else
					rt_panel_url+= "&container_id="+cid+"&netid="+netid+"&itemid="+itemid ;
				
				StringBuilder divsb = new StringBuilder() ;
				divsb.append("<div class=\"rt_blk\" style='position:relative;height:"+panel_h+"%;'><iframe id='rt_panel_"+this.getId()+"' style='width:100%;height:100%;border:0px;' src='"+rt_panel_url+"'></iframe>") ;
				divsb.append("</div>") ;
				divblks.add(new DivBlk("rt_panel",divsb.toString())) ;
			}
		}
				
		if(isRunner())
		{
			IMNRunner rnr = (IMNRunner)this ;
			if(rnr.RT_isRunning())
			{
				StringBuilder ssb = new StringBuilder() ;
				if(rnr.RT_isSuspendedInRun(ssb))
				{
					StringBuilder divsb = new StringBuilder() ;
					divsb.append("<div tp='run' class=\"rt_blk\"><span style=\"color:#dd7924\">Suspended:"+ssb.toString()+"</span>");
					if(!rnr.RT_runnerStartInner())
						divsb.append("<button onclick=\"rt_item_runner_start_stop('"+this.getId()+"',false)\">stop</button>");
					divsb.append("</div>") ;
					
					divblks.add(new DivBlk("rt_run",divsb.toString())) ;
				}
				else
				{
					StringBuilder divsb = new StringBuilder() ;
					divsb.append("<div tp='run' class=\"rt_blk\"><span style=\"color:green\">Running</span>");
					if(!rnr.RT_runnerStartInner())
						divsb.append("<button onclick=\"rt_item_runner_start_stop('"+this.getId()+"',false)\">stop</button>");
					divsb.append("</div>") ;
					
					divblks.add(new DivBlk("rt_run",divsb.toString())) ;
				}
			}
			else
			{
				StringBuilder divsb = new StringBuilder() ;
				divsb.append("<div tp='run' class=\"rt_blk\"><span style=\"color:green\">Stopped</span>");
				if(!rnr.RT_runnerStartInner())
					divsb.append("<button onclick=\"rt_item_runner_start_stop('"+this.getId()+"',true)\">start</button>");
				divsb.append("</div>") ;
				
				divblks.add(new DivBlk("rt_run",divsb.toString())) ;
			}
		}
		
		RT_DEBUG_ERR.renderDiv(divblks);
		
		RT_DEBUG_WARN.renderDiv(divblks);
		
		RT_DEBUG_INF.renderDiv(divblks);

		if(supportCxtVars())
			CXT_renderVarsDiv(divblks) ;
		
		LinkedHashMap<String,OuterApi> using_oa = this.getUsingOuterApis() ;
		if(using_oa!=null&&using_oa.size()>0)
		{
			StringBuilder divsb = new StringBuilder() ;
			divsb.append("<div tp='run' class=\"rt_blk\">Outer Apis")
				.append("<button onclick=\"mn_open_node_outer_api('"+this.getId()+"')\">View Detail</button>") ;
			for(OuterApi oa:using_oa.values())
			{
				divsb.append("<div style='margin-left:30px;'>").append(oa.getTitle());
				OuterApiLog lastcl = oa.RT_getLastCallLog() ;
				if(lastcl!=null)
					divsb.append(" ").append(Convert.calcDateGapToNow(lastcl.callDT)).append(" cost ").append(lastcl.getCostMS()).append("ms") ;
				divsb.append("</div>") ;
			}
			divsb.append("</div>") ;
			
			divblks.add(new DivBlk("outer_api",divsb.toString())) ;
		}
	}


	/**
	 * override to impl div fired event
	 * @param evtn
	 */
	public void RT_onRenderDivEvent(String evtn,JSONObject evt_pm,StringBuilder retmsg)
	{
		switch(evtn)
		{
		case "view_outerapi":
			String apin = evt_pm.getString("apin") ;
			OuterApi oa = this.getUsingOuterApi(apin) ;
			if(oa==null)
			{
				retmsg.append("<span style='color:red'>no out api found</span>");
				return;
			}
			return ;
		}
	}
	
	public JSONObject RT_toJO(boolean out_rt_div)
	{
		JSONObject jo = new JSONObject() ;
		if(isRunner())
		{
			IMNRunner rnr = (IMNRunner)this ;
			jo.put("runner", true) ;
			jo.put("b_running",rnr.RT_isRunning()) ;
			StringBuilder rsb = new StringBuilder() ;
			boolean bsusp = rnr.RT_isSuspendedInRun(rsb) ;
			jo.put("suspended", bsusp) ;
			if(bsusp)
				jo.put("suspend_reson", rsb.toString()) ;
		}
		if(out_rt_div)
		{
			ArrayList<DivBlk> divblks = new ArrayList<>() ;
			RT_renderDiv(divblks);
			JSONArray tmpjar = new JSONArray() ;
			for(DivBlk db : divblks)
				tmpjar.put(db.toJO()) ;
			jo.put("divs", tmpjar) ;
		}
		
		if(this instanceof MNNodeState)
		{
			jo.put("state", true) ;
			jo.put("state_active", ((MNNodeState)this).RT_isStateActive()) ;
			jo.put("state_running", ((MNNodeState)this).RT_isStateRunning()) ;
		}
		List<String> ss = this.RT_DEBUG_WARN.getPromptTitles() ;
		//jo.put("has_warn", warns.size()>0) ;
		jo.put("warns", new JSONArray(ss)) ;
		ss = this.RT_DEBUG_ERR.getPromptTitles() ;
		jo.put("errs",  new JSONArray(ss)) ;
		return jo ;
	}

	
	// JS
	
	public static List<JsProp> JS_getSysPropsCxt()
	{
		ArrayList<JsProp> jps = new ArrayList<>() ;
		jps.add(new JsProp("$sys",UAContext.sys,null,true,"system","System support func")) ;
		jps.add(new JsProp("$util",UAContext.util,null,true,"util","System util func")) ;
		jps.add(new JsProp("$debug",UAContext.debug,null,true,"system","System debug func")) ;
		HashMap<String,PlugJsApi> gvar2obj = PlugManager.getInstance().getJsApiAll();
		if(gvar2obj!=null)
		{
			for(Map.Entry<String, PlugJsApi> n2o:gvar2obj.entrySet())
			{
				String k = n2o.getKey();
				PlugJsApi jsapi= n2o.getValue() ;
				jps.add(new JsProp("$$"+k,jsapi,null,true,"plugin_"+k,jsapi.getDesc())) ;
			}
		}
		return jps;
	}
	
	private List<JsProp> globalPropsCxt = null ;
	
	protected List<JsProp> JS_getGlobalPropsCxt()
	{
		if(globalPropsCxt!=null)
			return globalPropsCxt ;
		
		ArrayList<JsProp> jps = new ArrayList<>() ;
		jps.addAll(JS_getSysPropsCxt()) ;
		
		MNNet net = this.getBelongTo() ;
		
		jps.add(new JsProp("flow",net,null,true,"Flow/Net","Flow obj in context").asTpTitle("Flow")) ;
		jps.add(new JsProp("node",this,null,true,this.getTitle(),this.getDesc()).asTpTitle("Node")) ;
		//jps.add(new JsProp("topic",null,String.class,false,"In Msg Topic","")) ;
		//jps.add(new JsProp("heads",null,Map.class,true,"In Msg Heads","")) ;
		//jps.add(new JsProp("payload",null,Object.class,true,"In Msg Payload","")) ;
		globalPropsCxt = jps;
		return jps ;
	}
	
	private transient List<JsSub> js_cxt_root_subs = null ;
	
	public final List<JsSub> JS_CXT_get_root_subs()
	{
		if(js_cxt_root_subs==null)
		{
			List<JsSub> rets = new ArrayList<>() ;
			List<JsProp> jps = JS_getGlobalPropsCxt();
			rets.addAll(jps) ;
			
			List<JsSub> subs = this.JS_get_subs() ;
			if(subs!=null)
			{
				for(JsSub sub:subs)
				{
					if(sub instanceof JsMethod || !sub.hasSub())
						continue ;// root has no method
					if(sub instanceof JsProp && ((JsProp)sub).isSysTag())
						continue ;
					rets.add(sub);
				}
			}
			js_cxt_root_subs = rets ;
		}
		
		JsEnv env = JsEnv.getInThLoc() ;
		if(env==null)
			return js_cxt_root_subs ;
		
		ArrayList<JsSub> rets = new ArrayList<>(js_cxt_root_subs);
		List<JsProp> jps = env.JS_get_props() ;
		if(jps!=null)
			rets.addAll(jps) ;
		return rets ;
	}
	
	public final JsSub JS_CXT_get_root_sub(String name)
	{
		for(JsSub jp: JS_CXT_get_root_subs())
		{
			if(jp.getName().equals(name))
				return jp ;
		}
		return null ;
	}
	
	public final JsSubOb JS_CXT_get_sub_by_id(String sub_id)
	{
		if(Convert.isNullOrEmpty(sub_id))
			return null ;
		
		List<String> ss = Convert.splitStrWith(sub_id, ".") ;
		String rootn = ss.get(0) ;
		int n = ss.size() ;
		JsSub jp1 = JS_CXT_get_root_sub(rootn) ;
		if(jp1==null)
			return null ;
		Object pv = this.JS_get(rootn) ;
		if(n==1)
			return new JsSubOb(jp1,pv) ;

		JsSub jss = null ;
		for(int i = 1 ; i < n ; i ++)
		{
			if(pv==null || !(pv instanceof JSObMap))
				return null ;
			
			JSObMap pv_ob = (JSObMap)pv ; 
			
			String name = ss.get(i) ;
			jss = pv_ob.JS_get_sub(name) ;
			if(jss==null)
				return null ;
			pv = pv_ob.JS_get(name) ;
		}
		return new JsSubOb(jss,pv) ;
	}
	
	public Object JS_get(String key)
	{
		Object r = super.JS_get(key);
		if (r != null)
			return r;
		
		if(key.startsWith("$$"))
		{//JsApi
			String plug_n = key.substring(2) ;
			HashMap<String,PlugJsApi> gvar2obj = PlugManager.getInstance().getJsApiAll();
			if(gvar2obj==null)
				return null ;
			return gvar2obj.get(plug_n) ;
		}
		
		if(key.startsWith("$"))
		{
			switch(key)
			{
//			case "flow":
//				return this.getBelongTo() ;
//			case "node":
//				return this ;
			case "$sys":
				return UAContext.sys;
			case "$debug":
				return UAContext.debug;
			case "$util":
				return UAContext.util;
			}
			
			//env
			JsEnv env = JsEnv.getInThLoc();
			if(env!=null)
			{
				return env.JS_get(key) ;
			}
		}
		
		switch(key)
		{
		case "flow":
			return this.getBelongTo() ;
		case "node":
			return this ;
		}
		
		return null ;
	}
	
	
	protected void UTIL_sleep(long t)
	{
		try
		{
			Thread.sleep(t);
		}catch(Exception e) {}
	}
	
}
