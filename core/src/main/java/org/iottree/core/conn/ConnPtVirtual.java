package org.iottree.core.conn;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;
import org.iottree.core.UACh;
import org.iottree.core.UATag;
import org.iottree.core.cxt.UACodeItem;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

public class ConnPtVirtual extends ConnPt
{
	
	public static final int RUN_ST_NOTRUN = 0 ;
	
	public static final int RUN_ST_OK = 1;
	
	public static final int RUN_ST_ERROR = 2 ;
	/**
	 * script runner under 
	 */
	String script = null ;
	
	long runIntervalMS = 500 ;
	
	/**
	 * when tag write some value to driver,
	 * then driver will handle this value ,and if driver has connection,it will send write cmd to connection(to real device or other)
	 */
	String onWriteScript = null ;
	
	
	private int runST = RUN_ST_NOTRUN ;
	
	private String runErr = null ;
	
	@Override
	public String getConnType()
	{ 
		return "virtual";
	}

	@Override
	public String getStaticTxt()
	{
		return "";
	}

	@Override
	public boolean isConnReady()
	{
		ConnProvider cp = this.getConnProvider();
		if(cp==null)
			return false;
		
		return cp.isRunning();
		//return false;
	}
	
	public String getIntervalScript()
	{
		if(this.script==null)
			return "" ;
		return this.script;
	}
	
	public void setIntervalScript(String txt)
	{
		this.script = txt ;
		clearCache();
	}
	
	public long getRunIntervalMS()
	{
		return this.runIntervalMS ;
	}
	
	public void setRunIntervalMS(long ms)
	{
		this.runIntervalMS = ms ;
	}
	
	public String getOnWriteScript()
	{
		if(this.onWriteScript==null)
			return "" ;
		return this.onWriteScript;
	}
	
	public void setOnWriteScript(String txt)
	{
		this.onWriteScript = txt ;
		clearCache();
	}

	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();
		if(this.script!=null)
			xd.setParamValue("script", this.script);
		if(this.onWriteScript!=null)
			xd.setParamValue("on_w_script", this.onWriteScript);
		xd.setParamValue("run_int", this.runIntervalMS);
		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);
		this.script = xd.getParamValueStr("script","");
		this.onWriteScript = xd.getParamValueStr("on_w_script","") ;
		this.runIntervalMS = xd.getParamValueInt64("run_int", 500) ;
		
		clearCache();
		return r;
	}
	
	protected void injectByJson(JSONObject jo) throws Exception
	{
		super.injectByJson(jo);
		
		
		this.script = jo.getString("script") ;
		this.onWriteScript = jo.getString("on_w_script") ;
		this.runIntervalMS = jo.getLong("run_int") ;

		clearCache();
	}
	
	transient private UACodeItem codeItem = null ;
	transient private UACodeItem onWriteCI = null ;
	
	transient private long lastRunMS = -1 ;
	
	private void clearCache()
	{
		this.codeItem = null ;
		onWriteCI = null ;
	}
	
	private UACodeItem getCodeItem(UAContext cxt)
	{
		if(codeItem!=null)
			return codeItem ;
		if(Convert.isNullOrTrimEmpty(this.script))
			return null ;
		UACodeItem ci = new UACodeItem("cpt_virtual_"+this.getName(), this.script) ;
		ci.initItem(cxt);
		this.codeItem = ci ;
		return codeItem ;
	}
	
	private UACodeItem getOnWriteCI(UAContext cxt)
	{
		if(onWriteCI!=null)
			return onWriteCI ;
		if(Convert.isNullOrTrimEmpty(this.onWriteScript))
			return null ;
		UACodeItem ci = new UACodeItem("cpt_virtual_o_w_"+this.getName(), "{"+this.onWriteScript+"}") ;
		ci.initItem(cxt,"$tag","$value");
		this.onWriteCI = ci ;
		return onWriteCI ;
	}
	
	synchronized void runVirtualInLoop() throws Exception
	{
		if(System.currentTimeMillis()-this.lastRunMS<this.runIntervalMS)
			return ;
		
		UACh ch = this.getJoinedCh();
		if(ch==null)
		{
			return ;
		}
		
		this.lastRunMS = System.currentTimeMillis() ;
		
		try
		{
			UAContext cxt = ch.RT_getContext() ;
			UACodeItem ci = getCodeItem(cxt);
			ci.runCode();
			this.runST = RUN_ST_OK ;
		}
		catch(Exception e)
		{
			this.runST = RUN_ST_ERROR ;
			this.runErr=e.getMessage();
			e.printStackTrace();
		}
	}
	
	public void runOnWrite(UATag tag,Object val) throws Exception
	{
		UACh ch = this.getJoinedCh();
		if(ch==null)
		{
			return ;
		}
		
		UAContext cxt = ch.RT_getContext() ;
		UACodeItem ci = getOnWriteCI(cxt);
		ci.runCodeFunc(tag,val) ;
	}
	
	
	public int getRunST()
	{
		return this.runST ;
	}
	
	public String getRunErr()
	{
		return this.runErr ;
	}
}
