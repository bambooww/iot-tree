package org.iottree.core.station;

import java.util.Arrays;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;

public class PSCmdPrjSynPM extends PSCmd
{
	public final static String CMD = "prj_syn_pm" ;
	
	@Override
	public String getCmd()
	{
		return CMD;
	}

	public PSCmdPrjSynPM asPrjPM(String prjname,boolean b_auto_start,boolean datasyn_en,long datasyn_intv, boolean failed_keep,long keep_max_len)
	{
		this.asParams(Arrays.asList(prjname,""+b_auto_start,""+datasyn_en,""+datasyn_intv,""+failed_keep,""+keep_max_len)) ;
		return this ;
	}
	
	@Override
	public void RT_onRecvedInStationLocal(StationLocal sl) throws Exception
	{
		String prjname = this.getParamByIdx(0) ;
		if(Convert.isNullOrEmpty(prjname))
			return ;
		
		UAPrj prj = UAManager.getInstance().getPrjByName(prjname) ;
		if(prj==null)
			return ;
		
		String pmautostart = this.getParamByIdx(1) ;
		boolean bauto_start = "true".equals(pmautostart) ;
		if(prj.isAutoStart()!=bauto_start)
			prj.setAutoStart(bauto_start);
		
		boolean datasyn_en = "true".equals(this.getParamByIdx(2)) ;
		long datasyn_intv = Convert.parseToInt64(this.getParamByIdx(3), 10000) ;
		boolean failed_keep = "true".equals(this.getParamByIdx(4)) ;
		long keep_max_len = Convert.parseToInt64(this.getParamByIdx(5), 3153600) ;
		sl.setPrjSynPM(prjname, datasyn_en, datasyn_intv, failed_keep,keep_max_len);
	}
}
