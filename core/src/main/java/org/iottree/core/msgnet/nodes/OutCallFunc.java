package org.iottree.core.msgnet.nodes;

import java.util.List;

import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * 外部调用函数
 * 
 * 配合OutCallRet形成外界程序可以通过此节点作为起始，触发调用后续的消息流
 * 
 * 如果没有对应的OutCallRet，则代表没有返回值。如果在后续流程中，有对应的OutCallRet节点被触发消息，则外部调用认为此消息是返回内容
 * 
 * @author jason.zhu
 *
 */
public class OutCallFunc extends MNNodeStart
{
	String funcName = null ;
	
	public String getFuncName()
	{
		return this.funcName ;
	}
	
	@Override
	public String getTitle()
	{
		if(Convert.isNotNullEmpty(this.funcName))
			return this.funcName+"()";
		else
			return super.getTitle() ;
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "_oc_func";
	}

	@Override
	public String getTPTitle()
	{
		return "Call Func";
	}

	@Override
	public String getColor()
	{
		return "#dddddd";
	}

	@Override
	public String getIcon()
	{
		return "PK_call_in";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return Convert.isNotNullEmpty(this.funcName);
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("func", this.funcName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.funcName = jo.optString("func") ;
	}
	
	private static OutCallRet findRetNode(String func,MNNode curn)
	{
		List<MNNode> ns = curn.getOutConnNodes() ;
		if(ns==null)
			return null ;
		for(MNNode n:ns)
		{
			if(n instanceof OutCallRet)
			{
				OutCallRet ocr = (OutCallRet)n ;
				if(func.equals(ocr.funcName))
					return ocr ;
				continue ;
			}
			
			OutCallRet r = findRetNode(func,n) ;
			if(r!=null)
				return r ;
		}
		return null ;
	}
	
	// rt
	
	public MNMsg RT_callByOutter(MNMsg input_msg)
	{
		if(Convert.isNullOrEmpty(this.funcName))
			throw new RuntimeException("no func name set in node") ;
		
		OutCallRet ocr = findRetNode(this.funcName,this) ;
		this.RT_sendMsgOut(RTOut.createOutAll(input_msg));
		if(ocr!=null)
			return ocr.getReturnMsg() ;
		return null ;
	}
	
}
