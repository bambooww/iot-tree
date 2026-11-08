package org.iottree.core.ai;

import org.iottree.core.UAPrj;

public class AIRoleHA extends AIRole
{
	public static final String TP = "ai_r_ha" ;
	
	public AIRoleHA(UAPrj prj,String uid)
	{
		super(prj,uid);
	}

	@Override
	public String getRoleTP()
	{
		return TP;
	}

}
