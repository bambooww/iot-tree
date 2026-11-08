package org.iottree.core.ai;

import org.iottree.core.UAPrj;

public class AIRoleNor extends AIRole
{
	public static final String TP = "ai_r_nor" ;
	
	public AIRoleNor(UAPrj prj,String uid)
	{
		super(prj,uid);
	}

	@Override
	public String getRoleTP()
	{
		return TP;
	}
}
