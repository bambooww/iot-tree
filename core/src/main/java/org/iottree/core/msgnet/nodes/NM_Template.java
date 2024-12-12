package org.iottree.core.msgnet.nodes;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class NM_Template extends MNNodeMid
{
	public static enum OutFmt
	{
		txt,json
	}
	
	String tempTxt = null ;
	
	OutFmt outFmt = OutFmt.txt;//"txt" ; // txt or json
	
	@Override
	public String getColor()
	{
		return "#ffb263";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf570";
	}

	@Override
	public JSONTemp getInJT()
	{
		return null;
	}

	@Override
	public JSONTemp getOutJT()
	{
		return null;
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}

//	@Override
	public String getTP()
	{
		return "template";
	}

	@Override
	public String getTPTitle()
	{
		return g("template");
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(this.tempTxt==null)
		{
			failedr.append("no template txt set") ;
			return false ;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("temp", this.tempTxt) ;
		jo.putOpt("out_fmt", outFmt.name()) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.tempTxt = jo.optString("temp") ;
		this.outFmt = OutFmt.valueOf(jo.optString("out_fmt","txt")) ;
		if(this.outFmt==null)
			this.outFmt = OutFmt.txt ;
		clear();
	}
	
	private synchronized void clear()
	{
		mache = null ;
	}
	// --------------
	
	private Mustache mache = null ;
	
	private synchronized Mustache getMustache()
	{
		if(mache!=null)
			return mache;
		
		MustacheFactory mf = new DefaultMustacheFactory();
		mache = mf.compile(new StringReader(this.tempTxt), "temp");
	    return mache ;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		if(this.tempTxt==null)
			return null ;
		HashMap<String, Object> scopes = new HashMap<>();
		if(Convert.isNotNullEmpty(msg.getTopic()))
			scopes.put("topic", msg.getTopic());
		Map<String,Object> hd = msg.getHeadsMap() ;
		if(hd!=null)
			scopes.put("heads", hd);
		scopes.put("payload",msg.CXT_PK_getPayload());
		scopes.put("msg_dt",msg.getMsgDT());
		scopes.put("msg_id",msg.getMsgId());
	    scopes.put("node", this.CXT_PK_toMap());
	    scopes.put("flow", this.getBelongTo().CXT_PK_toMap());

	    StringWriter sw = new StringWriter() ;
	    getMustache().execute(sw, scopes);
	    String res = sw.toString() ;
	    
	    if(outFmt==OutFmt.json)
	    {
	    	int n = res.length() ;
	    	char firstc=0 ;
	    	for(int i = 0 ; i < n ; i ++)
	    	{
	    		firstc = res.charAt(i) ;
	    		if(Character.isWhitespace(firstc))
	    			continue ;
	    		else
	    			break ;
	    	}
	    	
	    	if(firstc=='{')
	    	{
	    		JSONObject tmpjo = new JSONObject(res) ;
	    		return RTOut.createOutAll(new MNMsg().asPayload(tmpjo)) ;
	    	}
	    	else if(firstc=='[')
	    	{
	    		JSONArray jarr = new JSONArray(res) ;
	    		return RTOut.createOutAll(new MNMsg().asPayload(jarr)) ;
	    	}
	    	else
	    	{
	    		this.RT_DEBUG_ERR.fire("temp","Template result is no JSON format") ;
	    		return null ;
	    	}
	    }
		
	    return RTOut.createOutAll(new MNMsg().asPayload(res)) ;
	}
}
