package org.iottree.core.devtree;

import java.util.LinkedHashMap;

import org.iottree.core.util.Lan;
import org.json.JSONArray;
import org.json.JSONObject;

public class DTRunBlkCat
{
	String name ;
	
	String titleEn ;
	
	String titleCn ;
	
	LinkedHashMap<String,DTRunBlk> name2runblks = new LinkedHashMap<>() ;
	
	DTRunBlkCat(String name,String title_en,String title_cn)
	{
		this.name = name ;
		this.titleCn = title_cn ;
		this.titleEn = title_en ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		if("cn".contentEquals(Lan.getUsingLang()))
			return this.titleCn ;
		else
			return this.titleEn ;
	}
	
	public LinkedHashMap<String,DTRunBlk> getRunBlksMap()
	{
		return this.name2runblks ;
	}
	
	public DTRunBlk getRunBlk(String name)
	{
		return this.name2runblks.get(name) ;
	}
	
	void setRunBlk(DTRunBlk rb)
	{
		this.name2runblks.put(rb.getName(), rb) ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().put("n",this.name).putOpt("t_cn", this.titleCn).putOpt("t_en", this.titleEn).putOpt("t", this.getTitle()) ;
	}
	
	public JSONArray toListRunBlksJArr()
	{
		JSONArray jarr = new JSONArray() ;
		for(DTRunBlk rb:this.getRunBlksMap().values())
		{
			jarr.put(rb.toListJO()) ;
		}
		return jarr ;
	}
}
