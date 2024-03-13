package org.iottree.core.store.record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.iottree.core.UATag;
import org.iottree.core.store.tssdb.TSSSavePK;
import org.iottree.core.store.tssdb.TSSTagSegs;
import org.iottree.core.ui.IUIProvider;
import org.iottree.core.ui.IUITemp;
import org.iottree.core.ui.UITemp;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class RecProL1 extends RecPro implements IUIProvider
{

	// HashSet<String> selTagIds = new HashSet<>() ;

	private transient List<UATag> fitTags = null;
	private transient List<UATag> selTags = null;

	public abstract List<RecValStyle> getSupportedValStyle();

	/**
	 * fired by tssdb
	 * 
	 * @param tagsegs
	 * @return
	 * @throws Exception
	 */
	protected abstract boolean RT_onTagSegsSaved(TSSSavePK savepk) throws Exception;

	// public HashSet<String> getSelectTagIds()
	// {
	// return this.selTagIds ;
	// }

	public List<UATag> listFitTags()
	{
		if (this.fitTags != null)
			return this.fitTags;

		List<RecValStyle> su_vs = this.getSupportedValStyle();
		if (su_vs == null || su_vs.size() <= 0)
			return null;

		ArrayList<UATag> rets = new ArrayList<>();
		for (RecTagParam pm : this.belongTo.getRecTagParams().values())
		{
			RecValStyle rvs = pm.getValStyle();
			if (rvs == null)
				continue;
			if (su_vs.contains(rvs))
				rets.add(pm.getUATag());
		}

		this.fitTags = rets;
		return rets;
	}

	public boolean checkTagFitOrNot(UATag tag)
	{
		List<RecValStyle> su_vs = this.getSupportedValStyle();
		if (su_vs == null || su_vs.size() <= 0)
			return false;
		RecTagParam pm = this.belongTo.getRecTagParam(tag);
		if (pm == null)
			return false;
		RecValStyle rvs = pm.getValStyle();
		if (rvs == null)
			return false;
		;
		return su_vs.contains(rvs);
	}

	public synchronized List<UATag> listSelectedTags()
	{
		if (this.selTags != null)
			return this.selTags;

		this.selTags = this.belongTo.listRecProUsingTags(this.id);
		return this.selTags;
	}

	synchronized void clearCache()
	{
		fitTags = null;
		selTags = null;
	}

	public JSONObject toJO()
	{
		JSONObject jo = super.toJO();

		// for list using
		JSONArray jarr = new JSONArray();// (this.selTagIds) ;
		for (UATag tag : listSelectedTags())
			jarr.put(tag.getId());
		jo.put("sel_tags", jarr);

		return jo;
	}

	protected boolean fromJO(JSONObject jo, StringBuilder failed)
	{
		if (!super.fromJO(jo, failed))
			return false;

		// JSONArray jarr = jo.optJSONArray("sel_tags") ;
		// HashSet<String> tags = new HashSet<>() ;
		// if(jarr!=null)
		// {
		// int n = jarr.length() ;
		// for(int i = 0 ; i < n ; i ++)
		// {
		// tags.add(jarr.getString(i)) ;
		// }
		// }
		// this.selTagIds = tags ;
		return true;
	}

	// --- UI
	
	protected String UI_getTempIcon() 
	{
		return "/_iottree/res/ui_"+this.getTp()+".png" ;
	}
	
	protected String UI_getTempTitle()
	{
		return this.getTitle() ;
	}

	private UITemp ui_temp = new UITemp() {

		@Override
		public String getName()
		{
			return RecProL1.this.getName();
		}

		@Override
		public String getTitle()
		{
			return UI_getTempTitle();//RecProL1.this.getTitle();
		}

		@Override
		public boolean checkTagsFitOrNot(List<UATag> tags)
		{
			if(tags==null||tags.size()>1)
				return false;
			
			return RecProL1.this.checkTagFitOrNot(tags.get(0));
		}

		@Override
		public String calUrl(List<String> tagpaths)
		{
			return "/prj_tag_recp_" + RecProL1.this.getTp() + ".jsp?prjid=" + belongTo.prj.getId() + "&tagid="
					+ tagpaths.get(0) + "&proid=" + getId();
		}
		
		public String getIconUrl()
		{
			return UI_getTempIcon() ;
		}
	};

	private List<IUITemp> ui_temps = Arrays.asList(ui_temp);

	@Override
	public List<IUITemp> UI_getTemps()
	{
		return ui_temps;
	}
}
