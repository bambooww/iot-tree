package org.iottree.core.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class PlugMNNode extends MNNode
{
	private Object plugObj = null ;
	
	private String tp ;
	
	private String tpt ;
	
	private boolean bStartNode = false;
	private boolean bEndNode = false;
	
	private String icon ;
	
	private String color ;
	
	private Method mGetOutNum = null;
	
	private Method mGetOutColor = null ;
	
	private Method mIsParamReady= null;
	
	private Method mGetParamJO= null;
	
	private Method mSetParamJO= null;
	
	private Method mRT_processMsgIn = null ;
	
	private Method mRT_getMsgOutPayload = null ;
	
	private Method mRT_getMsgOutHeads = null ;
	
	private Method mRT_getMsgOutTopic = null ;
	
	public PlugMNNode()
	{
		
	}
	
	@Override
	protected MNNode createNewIns(MNNet net) throws Exception
	{
		PlugMNNode nn = (PlugMNNode)super.createNewIns(net) ;
		Object plugob = plugObj.getClass().getConstructor().newInstance() ;
		nn.plugObj = plugob ;
		nn.tp = this.tp ;
		nn.tpt = this.tpt;
		nn.bStartNode = this.bStartNode ;
		nn.bEndNode = this.bEndNode ;
		nn.icon = this.icon ;
		nn.color = this.color ;
		return nn ;
	}
	
	private Method getMethod(Class<?> c,String name,Class<?>... pmtps)
	{
		try
		{
			return c.getDeclaredMethod(name,pmtps);
		}
		catch(NoSuchMethodException nsme) {
			return null ;
		}
	}
	
	boolean initNode(Object plugobj,JSONObject jo)
	{
		assert(plugobj!=null) ;
		
		this.tp = jo.getString("tp") ;
		this.tpt = jo.optString("tpt", this.tp);
		this.bStartNode = jo.optBoolean("is_start_node",false) ;
		this.bEndNode = jo.optBoolean("is_end_node",false) ;
		this.icon = jo.optString("icon", "\\uf1e6") ;
		this.color =  jo.optString("color", "#0078b9") ;
		
		plugObj = plugobj ;
		Class<?> c = plugObj.getClass() ;
		
		mGetOutNum = getMethod(c,"MN_getOutNum",Integer.class);
		mGetOutColor = getMethod(c,"MN_getOutColor",Integer.class);
		mIsParamReady = getMethod(c,"MN_isParamReady",StringBuilder.class);
		mGetParamJO = getMethod(c,"MN_getParamJO");
		mSetParamJO = getMethod(c,"MN_setParamJO",JSONObject.class);
		mRT_processMsgIn = getMethod(c,"MN_RT_processMsgIn",String.class,Map.class,Object.class);
		mRT_getMsgOutPayload= getMethod(c,"MN_RT_getMsgOutPayload",Integer.class);
		mRT_getMsgOutHeads = getMethod(c,"MN_RT_getMsgOutHeads",Integer.class);
		mRT_getMsgOutTopic = getMethod(c,"MN_RT_getMsgOutTopic",Integer.class);
		return true ;
	}

	@Override
	public boolean supportInConn()
	{
		return !this.bStartNode;
	}
	
	@Override
	public boolean isEnd()
	{
		return this.bEndNode;
	}
	
	private Object invoke(Method m,Object...pms)
	{
		try
		{
		return m.invoke(plugObj, pms);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e) ;
		}
	}

	@Override
	public String getTP()
	{
		return this.tp;
	}

	@Override
	public String getTPTitle()
	{
		return this.tpt;
	}

	@Override
	public String getColor()
	{
		return this.color;
	}

	@Override
	public String getIcon()
	{
		return this.icon;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return (Boolean)this.invoke(mIsParamReady, failedr);
	}

	@Override
	public JSONObject getParamJO()
	{
		return (JSONObject)this.invoke(this.mGetParamJO);
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.invoke(this.mSetParamJO, jo) ;
	}
	

	@Override
	public int getOutNum()
	{
		return (Integer)invoke(mGetOutNum) ;
	}
	
	@Override
	public String getOutColor(int idx)
	{
		if(mGetOutColor!=null)
			return (String)invoke(mGetOutColor,idx) ;
		return null ;
	}
	

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		RT_processMsgIn(in_conn,msg) ;
		int n = getOutNum() ;
		if(n<=0)
			return null ;
		
		RTOut ret = RTOut.createOutIdx() ;
		for(int i = 0 ; i < n ; i ++)
		{
			Object pld = RT_getMsgOutPayload(i) ;
			if(pld==null)
				continue ;
			Map<String,Object> heads = RT_getMsgOutHeads(i) ;
			String topic = RT_getMsgOutTopic(i) ;
			MNMsg out = new MNMsg().asPayload(pld) ;
			if(heads!=null)
				out.asHeads(heads) ;
			if(Convert.isNotNullEmpty(topic))
				out.asTopic(topic) ;
			ret.asIdxMsg(i, out) ;
		}
		return ret ;
	}

	private void RT_processMsgIn(MNConn in_conn,MNMsg msg)
	{
		this.invoke(mRT_processMsgIn, msg.getTopic(),msg.getHeadsMap(),msg.getPayload()) ;
	}
	
	
	private Object RT_getMsgOutPayload(int idx)
	{
		if(mRT_getMsgOutPayload==null)
			return null ;
		return this.invoke(mRT_getMsgOutPayload) ;
	}
	
	private Map<String,Object> RT_getMsgOutHeads(int idx)
	{
		if(mRT_getMsgOutHeads==null)
			return null ;
		return (Map<String,Object>)this.invoke(mRT_getMsgOutHeads,idx) ;
	}
	
	private String RT_getMsgOutTopic(int idx)
	{
		if(mRT_getMsgOutTopic==null)
			return null ;
		return (String)this.invoke(mRT_getMsgOutTopic,idx) ;
	}
	
	@Override
	public String RT_getOutColor(int idx)
	{
		return null;
	}

	@Override
	public String RT_getOutTitle(int idx)
	{
		return null ;
	}
}
