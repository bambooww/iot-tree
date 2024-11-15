package org.iottree.core.station;

import java.util.Arrays;
import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;

public class PSCmdPrjStartStop extends PSCmd
{
	public final static String CMD = "prj_start_stop" ;
	
	@Override
	public String getCmd()
	{
		return CMD;
	}

	public PSCmdPrjStartStop asPrjStartStop(String prjname,boolean bstart_stop) //,Boolean b_auto_start)
	{
		//if(b_auto_start!=null)
		//	this.asParams(Arrays.asList(prjname,""+bstart_stop,""+b_auto_start)) ;
		//else
			this.asParams(Arrays.asList(prjname,""+bstart_stop)) ;
		return this ;
	}
	
	@Override
	public void RT_onRecvedInStationLocal(StationLocal sl) throws Exception
	{
		String prjname = this.getParamByIdx(0) ;
		if(Convert.isNullOrEmpty(prjname))
			return ;
		
		boolean bstart = "true".equals(this.getParamByIdx(1)) ;
		UAPrj prj = UAManager.getInstance().getPrjByName(prjname) ;
		if(prj==null)
			return ;
		
//		String pmautostart = this.getParamByIdx(2) ;
//		if(Convert.isNotNullEmpty(pmautostart))
//		{
//			boolean bauto_start = "true".equals(pmautostart) ;
//			if(prj.isAutoStart()!=bauto_start)
//				prj.setAutoStart(bauto_start);
//		}
		
		if(bstart)
			prj.RT_start() ;
		else
			prj.RT_stop();
	}
}
