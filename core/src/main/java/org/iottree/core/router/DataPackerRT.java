package org.iottree.core.router;

import org.iottree.core.UAPrj;
import org.iottree.core.util.ILang;

public class DataPackerRT extends DataPacker implements ILang
{
	public static final String TP = "_rt";
	
	public DataPackerRT(RouterManager rm)
	{
		super(rm);
	}

	@Override
	public String getName()
	{
		return TP;
	}

	@Override
	public String getTitle()
	{
		return g("rt_t");
	}

	@Override
	public String getDesc()
	{
		return g("rt_t","desc");
	}

	@Override
	public String getTp()
	{
		return TP;
	}

	@Override
	public String getPackData() throws Exception
	{
		return this.belongTo.belongTo.JS_get_rt_json_flat();
	}

}
