package org.iottree.core.station;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.iottree.core.station.PSCmdDirSyn.DirDiff;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class PSCmdDirSynAck extends PSCmd
{
	public final static String CMD = "dir_syn_ack" ;
	
	
	@Override
	public String getCmd()
	{
		return CMD;
	}

	public PSCmdDirSynAck asAckDiff(DirDiff dd)
	{
		this.asParams(Arrays.asList("diff")) ;
		this.asCmdDataJO(dd.toJO());
		return this;
	}
	
	public PSCmdDirSynAck asAckSyn(String op,String subf)
	{
		this.asParams(Arrays.asList("syn",op,subf)) ;
		//this.asCmdDataJO(dd.toJO());
		return this ;
	}
	
	@Override
	public void RT_onRecvedInPlatform(PlatformWSServer.SessionItem si,PStation ps) throws Exception
	{
		String tp = this.getParamByIdx(0) ;
		if(tp==null)
			return ;
		switch(tp)
		{
		case "diff":
			RT_onRecvDiff(ps);
			break ;
		case "syn":
			RT_onRecvSyn(ps) ;
			break ;
		default:
		}
	}
	
	private void RT_onRecvDiff(PStation ps) throws Exception
	{
		JSONObject jo = this.getCmdDataJO() ;
		if(jo==null)
			return ;
		DirDiff dd = new DirDiff() ;
		dd.fromJO(jo);
		
		ps.RT_fireRecvObj(dd) ;
	}
	
	private void RT_onRecvSyn(PStation ps) throws Exception
	{
		String op = this.getParamByIdx(1) ;
		String subf = this.getParamByIdx(2) ;
		if(Convert.isNullOrEmpty(op) || Convert.isNullOrEmpty(subf))
			return ;
		
		ps.RT_fireRecvedSyn(op,subf) ;
	}
}
