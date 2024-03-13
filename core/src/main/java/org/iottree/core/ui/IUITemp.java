package org.iottree.core.ui;

import java.util.List;

import org.iottree.core.UATag;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * UI模板，可以通过配置参数，生成具体的UIItem实例
 * @author jason.zhu
 *
 */
public interface IUITemp
{
	public String getName();
	
	public String getTitle();
	
	public String getDesc() ;
	
	public int supportInputTagMaxNum() ;
	
	public int supportInputMinNum() ;
	
	public boolean checkTagsFitOrNot(List<UATag> tags);
	
	
	public String calUrl(List<String> tagpaths) ;
	
	public String getIconUrl() ;
	
	public int getWidth() ;
	
	public int getHeight() ;
	
	default public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("n", this.getName()) ;
		jo.putOpt("t", this.getTitle()) ;
		jo.putOpt("d", this.getDesc()) ;
		jo.put("max_tag_n", this.supportInputTagMaxNum()) ;
		jo.put("min_tag_n", this.supportInputMinNum()) ;
		jo.put("w", this.getWidth()) ;
		jo.put("h", this.getHeight()) ;
		jo.putOpt("icon", this.getIconUrl()) ;
		return jo ;
	}
}
