package org.iottree.core.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;

/**
 * value indicator
 * 
 * like temperature 
 * @author jason.zhu
 *
 */
public enum ValIndicator
{
	temp,pres,flow,flowa,rot_rate,rh,ah,ph,i,v,r,c,l,p,e,f,q,cond,condm,
	turb,DO,tmdur 
	;
	
	private static Lan lan  = Lan.getLangInPk(ValUnit.class) ;
	
	public String getTitle()
	{
		return lan.g("vi_"+this.name()) ;
	}
	
	private List<ValUnit> units =null ;
	
	public List<ValUnit> getUnits()
	{
		if(units!=null)
			return units ;
		String ss = lan.gn("vi_"+this.name()).getAttr("units") ;
		if(Convert.isNullOrEmpty(ss))
		{
			units = Arrays.asList() ;
			return units ;
		}
		List<String> sss = Convert.splitStrWith(ss, ",|") ;
		ArrayList<ValUnit> rets = new ArrayList<>() ;
		for(String s:sss)
		{
			ValUnit vu = ValUnit.valueOf(s) ;
			rets.add(vu) ;
		}
		return units=rets ;
	}
}

/*


*/