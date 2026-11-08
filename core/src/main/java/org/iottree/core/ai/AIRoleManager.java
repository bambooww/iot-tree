package org.iottree.core.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class AIRoleManager
{
	private static HashMap<String, AIRoleManager> prj2instance = new HashMap<>();

	public static ArrayList<AIRole> ALL_ROLES = new ArrayList<>();
	static
	{
		ALL_ROLES.add(new AIRoleNor(null, ""));
		ALL_ROLES.add(new AIRoleHA(null, ""));
	}

	public static AIRoleManager getInstance(UAPrj prj)
	{
		AIRoleManager r = prj2instance.get(prj.getId());
		if (r != null)
			return r;

		synchronized (AIRoleManager.class)
		{
			r = prj2instance.get(prj.getId());
			if (r != null)
				return r;

			r = new AIRoleManager(prj);
			prj2instance.put(prj.getId(), r);
			return r;
		}
	}

	UAPrj owner;

	HashMap<String, AIRole> uid2role = new HashMap<>();

	private AIRoleManager(UAPrj prj)
	{
		this.owner = prj;
	}

	public List<UADev> listAIDevAll()
	{
		ArrayList<UADev> rets = new ArrayList<>();
		for (UACh ch : this.owner.getChs())
		{
			for (UADev d : ch.getDevs())
			{
				AIDevDef dd = d.getAIDevDef();
				if (dd == null)
					continue;
				String aian = dd.getAIAName() ;
				d.setUseTitle(aian);
				rets.add(d);
			}
		}
		return rets;
	}
	
	public List<NM_AIRole> listAIRoleAll(boolean enabled)
	{
		List<MNNet> nets = this.owner.listMNNetsAll() ;
		ArrayList<NM_AIRole> rets = new ArrayList<>() ;
		for(MNNet net:nets)
		{
			List<NM_AIRole> airs = net.findItemByTpMark(NM_AIRole.class, null) ;
			if(airs==null||airs.size()<=0)
				continue ;
			
			rets.addAll(airs) ;
		}
		return rets ;
	}

	public AIRole createAIRole(String tp, String uid, JSONObject jo)
	{
		if (Convert.isNullOrEmpty(tp) || jo==null)
			return null;

		AIRole r = null;
		switch (tp)
		{
		case AIRoleHA.TP:
			r = new AIRoleHA(this.owner, uid);
			break;
		case AIRoleNor.TP:
			r = new AIRoleNor(this.owner, uid);
			break;
		default:
			return null;
		}
		r.fromJO(jo);
		return r;
	}

}
