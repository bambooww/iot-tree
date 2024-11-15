package org.iottree.core.station;

import java.util.Arrays;
import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;

/**
 * platform 触发 station上载本地项目到platform的指令
 * plaform - > station
 * @author jason.zhu
 *
 */
public class PSCmdPrjUpTrigger extends PSCmd
{
	public final static String CMD = "up_prj_trigger" ;

	@Override
	public String getCmd()
	{
		return CMD;
	}
	
	public PSCmdPrjUpTrigger asUpPrjname(String prjname)
	{
		this.asParams(Arrays.asList(prjname)) ;
		return this ;
	}
	
	@Override
	public void RT_onRecvedInStationLocal(StationLocal sl) throws Exception
	{
		String prjname = this.getParamByIdx(0) ;
		if(Convert.isNullOrEmpty(prjname))
			return ;
		UAPrj locprj = UAManager.getInstance().getPrjByName(prjname) ;
		if(locprj==null)
			return ;
		
		PSCmdPrjUpdate cmd = new PSCmdPrjUpdate() ;
		cmd.asToBePackPrj(locprj) ;
		StringBuilder failedr = new StringBuilder();
		if (!sl.RT_sendCmd(cmd, failedr))
		{
			if (StationLocal.log.isDebugEnabled())
			{
				StationLocal.log.debug(failedr.toString());
			}
		}
	}
	
}
