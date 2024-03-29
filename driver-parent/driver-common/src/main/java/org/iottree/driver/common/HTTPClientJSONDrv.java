package org.iottree.driver.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.util.Lan;

/**
 * 
 * @author zzj
 */
public class HTTPClientJSONDrv extends DevDriver
{

	@Override
	public String getName()
	{
		return "http_json";
	}

	@Override
	public String getTitle()
	{
		return "Http Json";
	}

	
	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return false;
	}

	@Override
	public DevDriver copyMe()
	{
		return new HTTPClientJSONDrv();
	}

	@Override
	public List<PropGroup> getPropGroupsForCh(UACh ch)
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		
		PropGroup gp = null;
		
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		
		gp = new PropGroup("conns",lan);//"Connections");
		gp.addPropItem(new PropItem("title",lan,PValTP.vt_str,false,null,null,"")); //"Title",""
		gp.addPropItem(new PropItem("url",lan,PValTP.vt_str,false,null,null,"")); //"URL",""
		gp.addPropItem(new PropItem("js",lan,PValTP.vt_str,false,null,null,"").withTxtMultiLine(true)); //"JavaScript",""
		pgs.add(gp) ;
		
		return pgs;
	}

	public List<PropGroup> getPropGroupsForDev()
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		
		//PropGroup gp = null;
		
		
		return pgs;
	}

	private static HTTPClientJSONAddr msAddr = new HTTPClientJSONAddr() ;
	
	@Override
	public DevAddr getSupportAddr()
	{
		return msAddr;
	}


//	protected boolean RT_initDriver(StringBuilder failedr) throws Exception
//	{
//		String jsstr = this.getPropValStr("conns", "js", null) ;
//		if(jsstr==null)
//			jsstr = "" ;
//		try
//		{
//			
//			getJSEngine().eval("function _do_http_json_model(){\r\n"
//						 +jsstr
//						+"}\r\n");
//			return true ;
//		}
//		catch(Exception ee)
//		{
//			ee.printStackTrace();
//			return false;
//		}
//	}
//	
	private static String readStringFromUrl(String url) throws IOException
	{
		try(InputStream is = new URL(url).openStream();)
		{
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = rd.read()) != -1)
			{
				sb.append((char) cp);
			}
			return sb.toString();
		}
	}
	
	long lastRunDT = -1 ;


	

	static final String JS_NAME="nashorn";
	
	private ScriptEngine engine = null ;

	private ScriptEngine getJSEngine()
	{
		if(engine!=null)
			return engine ;
		
		ScriptEngineManager manager = new ScriptEngineManager();
		engine = manager.getEngineByName(JS_NAME);
		return engine ;
	}
	
	
	public boolean setRtVal(String dev_name,String tag_name,Object v)
	{
		
		UADev dev = this.getBelongToCh().getDevByName(dev_name) ;
		if(dev==null)
			return false;
		UATag tag = dev.getTagByName(tag_name) ;
		if(tag==null)
			return false;
		//tag.cx
		
		return true ;
	}
	
	private void runScriptToGetVal(String jsonstr) throws ScriptException, NoSuchMethodException
	{
		String script_txt = "var $in="+jsonstr+";" ;

		engine.eval(script_txt) ;
		engine.put("$out", this);
		
		Invocable jsInvoke = (Invocable) getJSEngine();
		jsInvoke.invokeFunction("_do_http_json_model");
	}

//	@Override
//	public String[] supportConnTps()
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public boolean supportDevFinder()
	{
		return false;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		return null;
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		return null;
	}

	@Override
	protected void RT_onConnReady(ConnPt cp,UACh ch,UADev dev)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp,UACh ch,UADev dev)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean RT_runInLoop(UACh ch,UADev dev,StringBuilder failedr) throws Exception
	{
		String url = this.getBelongToCh().getOrDefaultPropValueStr("conns", "url", null) ;
		if(url==null||url.contentEquals(""))
			return false;

		try
		{
			String txt = readStringFromUrl(url) ;
			if(txt==null||txt.contentEquals(""))
				return false;
			runScriptToGetVal(txt) ;
			return true ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean RT_writeVal(UACh ch,UADev dev,UATag tag,DevAddr da,Object v)
	{
		return false;
	}
	
	@Override
	public boolean RT_writeVals(UACh ch,UADev dev,UATag[] tags,DevAddr[] da,Object[] v)
	{
		return false;
	}
}
