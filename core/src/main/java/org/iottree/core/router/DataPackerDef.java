package org.iottree.core.router;

import org.iottree.core.UAPrj;
import org.iottree.core.util.ILang;
import org.json.JSONObject;

public class DataPackerDef extends DataPacker implements ILang
{
	public static final String TP = "_def" ;
	
	public DataPackerDef(RouterManager rm)
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
		return g("def_t");
	}

	@Override
	public String getDesc()
	{
		return g("def_t","desc");
	}

	@Override
	public String getTp()
	{
		return TP;
	}
	
	public String getPackData() throws Exception
	{
		return this.belongTo.belongTo.JS_get_def_json() ;
	}

}
