package org.iottree.core.router;

import java.util.Arrays;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.router.RouterInnCollator.OutStyle;
import org.iottree.core.router.RouterInnCollator.TagVal;
import org.iottree.core.util.ILang;

public class RICRunTime extends RouterInnCollator implements ILang
{
	public static final String TP = "rt";
	
	public RICRunTime(RouterManager rm)
	{
		super(rm);
		//this.id = "rt" ;
	}

	@Override
	public String getTp()
	{
		return TP;
	}
	
	protected RouterInnCollator newInstance(RouterManager rm)
	{
		return new RICRunTime(rm) ;
	}
	
	public OutStyle getOutStyle()
	{
		return OutStyle.interval ;
	}

	private List<JoinOut> jouts = Arrays.asList(
			new JoinOut(this,"rt_tree"),//,false,true),
			new JoinOut(this,"rt_flat") //,false,true)
			) ;

	@Override
	public List<JoinIn> getJoinInList()
	{
		return null ;
	}
	
	@Override
	public List<JoinOut> getJoinOutList()
	{
		return jouts ;
	}
	
	/**
	 * override by sub
	 */
	@Override
	protected void runInIntvLoop()
	{
		
	}
	
	
	public String pullOut(String join_out_name) throws Exception
	{
		switch(join_out_name)
		{
		case "rt_tree":
			return this.belongTo.belongTo.JS_get_rt_json() ;
		case "rt_flat":
			return this.belongTo.belongTo.JS_get_rt_json_flat();
		default:
			return null ;
		}
	}
	
	@Override
	protected void RT_onRecvedFromJoinIn(JoinIn ji,String recved_txt)
	{
		
	}
}
