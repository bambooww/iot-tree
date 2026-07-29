package org.iottree.driver.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.iottree.core.ConnDev;
import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.DevDriverMsgOnly;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.ConnPt.DataTp;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.conn.*;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 * 
 * @author jason.zhu
 *
 */
public class MsgJSDrv extends DevDriverMsgOnly
{
	String onInitJS = null ;
	
	String onMsgInJS = null ;
	boolean hasMsgInJS = false ;
	boolean msgInStr = true;
	
	String onTagWriteJS = null ;
	boolean hasTagWriteJS = false ;
	
	String onLoopJS = null ;
	boolean hasLoopJS = false ;
	
	@Override
	public String getName()
	{
		return "msg_js";
	}

	@Override
	public String getTitle()
	{
		return "Msg JS Handler";
	}

	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return true;
	}

	@Override
	public DevDriver copyMe()
	{
		return new MsgJSDrv();
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtMsg.class;
	}

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
	public List<PropGroup> getPropGroupsForCh(UACh ch)
	{
//		ArrayList<PropGroup> rets = new ArrayList<>() ;
//		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
//		
//		PropGroup pg = new PropGroup("msg_js",lan);//"Ping");
//		
//		pg.addPropItem(new PropItem("on_msg_in",lan,PValTP.vt_str,false,null,null,3000)); //"Check Timeout","Check Timeout(ms),too small may check error"
//		pg.addPropItem(new PropItem("script", lan, PValTP.vt_str, false, null, null,
//				"").withTxtMultiLine(true)); 
//		rets.add(pg);
//		return rets;
		return null ;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		return null;
	}

	@Override
	public DevAddr getSupportAddr()
	{
		return null;
	}
	
	@Override
	public boolean hasDriverConfigPage()
	{//有驱动特殊配置页面
		return true;
	}
	
	protected boolean initDriver(StringBuilder failedr) throws Exception
	{
		if(!super.initDriver(failedr))
			return false;
		
		UACh ch = this.getBelongToCh() ;
		if(ch==null)
		{
			failedr.append("no belong to ch found") ;
			return false;
		}
		String txt = ch.getDrvSpcConfigTxt() ;
		if(Convert.isNullOrEmpty(txt))
		{
			failedr.append("ch is not set special configuration") ;
			return false;
		}
		JSONObject jo = new JSONObject(txt) ;
		onInitJS = jo.optString("on_init_js","") ;
		onMsgInJS = jo.optString("on_msgin_js") ;
		msgInStr = jo.optBoolean("msg_in_str", true) ;
		onTagWriteJS = jo.optString("on_tagw_js") ;
		onLoopJS = jo.optString("on_loop_js") ;
		if(Convert.isNullOrEmpty(onMsgInJS) && Convert.isNullOrEmpty(onTagWriteJS))
		{
			failedr.append("ch is not set special configuration") ;
			return false;
		}
		
		String js_uid = ch.getId() ;
		
		UAContext cxt = ch.RT_getContext() ;
		String tmps = onInitJS +"\r\n" ;
		
		if(Convert.isNotNullTrimEmpty(onMsgInJS))
		{
			tmps += "function on_msgin_"+js_uid+"($ch,$connpt,$msg){\r\n";
			tmps += onMsgInJS ;
			tmps += "\r\n}" ;
			
			hasMsgInJS = true ;
		}
		
		if(Convert.isNotNullTrimEmpty(onTagWriteJS))
		{
			tmps += "function on_tagw_"+js_uid+"($ch,$connpt,$tag,$input){\r\n";
			tmps += onTagWriteJS ;
			tmps += "\r\n}" ;
			
			hasTagWriteJS = true ;
		}
		
		if(Convert.isNotNullTrimEmpty(onLoopJS))
		{
			tmps += "function on_loop_"+js_uid+"($ch,$connpt){\r\n";
			tmps += onLoopJS ;
			tmps += "\r\n}" ;
			
			hasLoopJS = true ;
		}
			
		cxt.scriptEval(tmps);
			
		return true;
	}
	
	@Override
	public void RT_onConnMsgIn(byte[] msgbs)
	{
		//System.out.println("on msg in="+new String(msgbs)) ;
		if(!hasMsgInJS)
			return ;
		Object msg = msgbs ;
		if(msgInStr)
		{
			try
			{
				msg = new String(msgbs,"UTF-8") ;
			}
			catch(Exception ee)
			{
				throw new RuntimeException(ee) ;
			}
		}
		
		UACh ch = this.getBelongToCh() ;
		String js_uid = ch.getId() ;
		ConnPtMsg cpm = (ConnPtMsg)this.getBindedConnPt() ;
		//cpm.sen
		UAContext cxt = ch.RT_getContext() ;
		try
		{
			cxt.scriptInvoke("on_msgin_"+js_uid, ch,cpm,msg) ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}

	
	@Override
	public boolean RT_writeVal(UACh ch, UADev dev, UATag tag, DevAddr da, Object v)
	{
		if(!hasTagWriteJS)
			return false;
		
		String js_uid = ch.getId() ;
		ConnPtMsg cpm = (ConnPtMsg)this.getBindedConnPt() ;
		//cpm.sen
		UAContext cxt = ch.RT_getContext() ;
		try
		{
			cxt.scriptInvoke("on_tagw_"+js_uid, ch,cpm,tag,v) ;
			return true;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return false;
		}
	}


	@Override
	protected void RT_onConnReady(ConnPt cp, UACh ch, UADev dev) throws Exception
	{
		
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp, UACh ch, UADev dev) throws Exception
	{
		
	}

	@Override
	protected boolean RT_runInLoop(UACh ch, UADev dev, StringBuilder failedr) throws Exception
	{
		if(!hasLoopJS)
			return true;
		
		String js_uid = ch.getId() ;
		ConnPtMsg cpm = (ConnPtMsg)this.getBindedConnPt() ;
		//cpm.sen
		UAContext cxt = ch.RT_getContext() ;
		try
		{
			cxt.scriptInvoke("on_loop_"+js_uid, ch,cpm) ;
			return true;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return false;
		}
	}

	

	@Override
	public boolean RT_writeVals(UACh ch, UADev dev, UATag[] tags, DevAddr[] da, Object[] v)
	{
		return false;
	}

}
