package org.iottree.core.msgnet.nodes;

import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class NM_RESTfulApi extends MNNodeMid //implements
{
	public static final String TP = "restful_api" ;
	
	String apiName = null ;
	
	private String okRespTxt =  null ;
	
	private String errRespTxt =  null ;
	
	private transient Object outputObj = null ;
	
	
	
	public String getApiName()
	{
		return this.apiName ;
	}
	
	public String getAccessPath()
	{
		MNNet net = this.getBelongTo() ;
		UAPrj prj = net.getBelongTo().getBelongToPrj() ;
		return "/"+prj.getName()+"/_mn_"+TP+"/"+net.getName()+"/"+this.apiName ;
	}
	
	public String getOkRespTxt()
	{
		return this.okRespTxt ;
	}
	
	public String getErrRespTxt()
	{
		return this.errRespTxt ;
	}
	
	public Object getOutputObj()
	{
		return this.outputObj ;
	}
	
	/**
	 * when url received post data,it will output
	 */
	@Override
	public int getOutNum()
	{
		return 1;
	}
	
	@Override
	public String getTP()
	{
		return TP;
	}

	@Override
	public String getTPTitle()
	{
		return "RESTful Api";
	}

	@Override
	public String getColor()
	{
		return "#1488f5";
	}

	@Override
	public String getIcon()
	{
		return "\\uf090";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.apiName))
		{
			failedr.append("no api name set") ;
			return false;
		}
		return true ;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("api_n",this.apiName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.apiName = jo.optString("api_n") ;
	}
	
	@Override
	public String RT_getInTitle()
	{
		return "Input Payload will be response to out request";//this.apiName;
	}
	
	/**
	 * call in Web Filter
	 * @param jo
	 */
	public final void RT_onApiPosted(Object jo)
	{
		MNMsg m = new MNMsg().asPayload(jo) ;
		RTOut rto = RTOut.createOutIdx().asIdxMsg(0, m) ;
		this.RT_sendMsgOut(rto);
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		outputObj = msg.getPayload() ;
		return null;
	}
}
