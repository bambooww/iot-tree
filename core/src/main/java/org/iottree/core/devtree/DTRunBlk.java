package org.iottree.core.devtree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.Lan;
import org.json.JSONObject;

/**
 * run block
 * 1,prop (can bind tag with rt/his data,manual config,output resule)
 * 2,init func, end func, loop func
 * 3,event (error,warn with description)
 * 4,sub node requirement: all sub node bind tags
 * 
 * @author jason.zhu
 */
public abstract class DTRunBlk implements ILang
{
	static Lan lan = Lan.getLangInPk(DTRunBlk.class) ;
	
	
	
		
	String name = null ;
	
	String titleEn = null ;
	
	String descEn = null ;
	
	String titleCn = null ;
	
	String descCn = null ;
	
	DTRunBlkCat cat ;
	
	public DTRunBlk(DTRunBlkCat cat,String name)
	{
		this.cat = cat ;
		this.name = name ;
	}
	
	public DTRunBlkCat getCat()
	{
		return this.cat;
	}
	
//	public DTRunBlk(String name)
//	{
//		//this.owner = owner ;
//		this.name = name ;
//	}

	public String getName()
	{
		return this.name;
	}
	
	public String getUID()
	{
		return this.cat.getName()+"."+this.name ;
	}
	
	public String getTitle()
	{
		if("cn".equals(Lan.getUsingLang()))
			return this.titleCn ;
		else
			return this.titleEn ;
	}
	
	public String getTitleFull()
	{
		if("cn".equals(Lan.getUsingLang()))
			return this.cat.titleCn+"."+this.titleCn ;
		else
			return this.cat.titleEn+"."+this.titleEn ;
	}
	
	public String getDesc()
	{
		if("cn".equals(Lan.getUsingLang()))
			return this.descCn ;
		else
			return this.descEn ;
	}
	
	public abstract LinkedHashMap<String,DTRunProp> getProps() ;
	
	public abstract LinkedHashMap<String,DTRunEvent> getEvents() ;
	
	public DTRunProp getProp(String pn)
	{
		LinkedHashMap<String,DTRunProp> ps = getProps() ;
		if(ps==null)
			return null ;
		return ps.get(pn) ;
	}
	
	public DTRunEvent getEvent(String pn)
	{
		LinkedHashMap<String,DTRunEvent> ps = getEvents() ;
		if(ps==null)
			return null ;
		return ps.get(pn) ;
	}
	
	public boolean fromJO(JSONObject jo,StringBuilder failedr)
	{
		this.titleCn = jo.optString("t_cn") ;
		this.titleEn = jo.optString("t_en") ;
		this.descCn = jo.optString("d_cn") ;
		this.descEn = jo.optString("d_en") ;
		//this.mode = Mode.valueOf(jo.optString("mode")) ;
		//this.minRunIntv = jo.optLong("min_run_intv",-1) ;
		return true ;
	}
	
	public JSONObject toListJO()
	{
		return new JSONObject().put("n", this.name)
				.putOpt("t_cn",this.titleCn).putOpt("t_en", this.titleEn)
				.putOpt("t", this.getTitle()).putOpt("tt", this.getTitleFull())
				.put("d", this.getDesc()).put("uid", this.getUID()) ;
	}

	public abstract boolean RT_run(DTNode nd) ;
	
	
}
