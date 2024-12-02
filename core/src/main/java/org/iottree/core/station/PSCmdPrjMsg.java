package org.iottree.core.station;

import java.io.IOException;
import java.util.Arrays;

import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * msg has topic and payload
 * 
 * this is used for StationMsgSend_NM - PlatMsgRecv_NM
 * @author jason.zhu
 *
 */
public class PSCmdPrjMsg extends PSCmd
{
	public final static String CMD = "prj_msg";

	private String keyid = null ;
	
	private String topic = null ;
	
	String payload = null ;
	
	private UAPrj prj = null ;
	
	@Override
	public String getCmd()
	{
		return CMD;
	}

	public PSCmdPrjMsg asStationLocalPayloadPrj(String keyid,String topic,byte[] payload,boolean b_his,UAPrj prj) // throws IOException
	{
		this.keyid = keyid ;
		this.topic = topic ;
		this.prj = prj ;
		
		this.asParams(Arrays.asList(prj.getName(),keyid,topic,b_his?"1":"0"));
		this.asCmdData(payload) ;

		return this;
	}
	
	public String getKeyId()
	{
		return this.keyid ;
	}
	
	public String getTopic()
	{
		return this.topic ;
	}
	
	@Override
	public void RT_onRecvedInPlatform(PlatInsWSServer.SessionItem si, PStation ps) throws Exception
	{
		String prjname = this.getParamByIdx(0);
		String keyid = this.getParamByIdx(1) ;
		String topic = this.getParamByIdx(2) ;
		boolean b_his = "1".equals(this.getParamByIdx(3)) ;
		if (Convert.isNullOrEmpty(prjname) || Convert.isNullOrEmpty(topic)||Convert.isNullOrEmpty(keyid))
			return;
		
		UAPrj platform_prj = ps.getRelatedPrjByRStationPrjN(prjname); //UAManager.getInstance().getPrjByName(p_prjname);
		if (platform_prj == null)
			return;

		byte[] payload = this.getCmdData();
		
		PlatInsManager.getInstance().onRecvedRTMsg(ps,platform_prj,keyid,topic,payload,b_his) ;
	}
}
