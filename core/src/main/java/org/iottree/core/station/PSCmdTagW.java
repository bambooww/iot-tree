package org.iottree.core.station;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;

public class PSCmdTagW extends PSCmd
{
	static ILogger log = LoggerManager.getLogger(PSCmdTagW.class);
	
	public final static String CMD = "tag_w" ;

	@Override
	public String getCmd()
	{
		return CMD;
	}
	
	public PSCmdTagW asPrjWriteTag(String prjname,String tagpath,String strv)
	{
		JSONObject jo = new JSONObject() ;
		jo.put("prjn", prjname) ;
		jo.put("tagp", tagpath) ;
		jo.put("strv", strv) ;
		this.asCmdDataJO(jo) ;
		return this ;
	}
	
	@Override
	public void RT_onRecvedInStationLocal(StationLocal sl) throws Exception
	{
		JSONObject jo = this.getCmdDataJO() ;
		if(jo==null)
			return ;
		String prjn = jo.optString("prjn") ;
		String tagp = jo.optString("tagp") ;
		String strv = jo.optString("strv") ;
		if(Convert.isNullOrEmpty(prjn) || Convert.isNullOrEmpty(tagp) || Convert.isNullOrEmpty(strv))
			return ;
		boolean bw = false;
		String err = null ;
		try
		{
			UAPrj prj = UAManager.getInstance().getPrjByName(prjn);
			if(prj==null)
			{
				err = "no prj found in station with name ="+prjn;
				return ;
			}
			UATag tag = prj.getTagByPath(tagp) ;
			if(tag==null)
			{
				err = "no tag found in prj ["+tagp+"] with path ="+tagp;
				return ;
			}
			StringBuilder failedr = new StringBuilder() ;
			bw = tag.RT_writeValStr(strv,failedr) ;
			err = failedr.toString() ;
		}
		finally
		{
			sendAck(sl, prjn, tagp,strv,bw,err) ;
		}
	}
	

	private void sendAck(StationLocal sl, String prjn, String tagp,String strv,boolean bok,String err)
	{
		PSCmdTagWAck ack = new PSCmdTagWAck();
		ack.asAckWriteTag(prjn, tagp, strv,bok,err) ;
		StringBuilder failedr = new StringBuilder();
		if (!sl.RT_sendCmd(ack, failedr))
		{
			if (log.isDebugEnabled())
			{
				log.debug(failedr.toString());
			}
		}
	}
	
}
