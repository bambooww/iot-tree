package org.iottree.core.conn.html;

import java.util.List;

public abstract class XPFunc
{
	public abstract boolean setupParams(List<String> params) ;
	
	public abstract String runFunc(String input) ;
}


class XPFuncSubstr extends XPFunc
{
	int p_num = 0 ;
	
	int p1 ;
	int p2;
	
	public boolean setupParams(List<String> params)
	{
		if(params==null||params.size()<=0)
			return false;
		
		p_num = params.size() ;
		if(p_num>2)
			return false;
		p1 = Integer.parseInt(params.get(0)) ;
		if(p_num>1)
			p2 = Integer.parseInt(params.get(1)) ;
		return true;
	}
	
	@Override
	public String runFunc(String input)
	{
		if(p_num==1)
			return input.substring(p1) ;
		else if(p_num==2)
			return input.substring(p1,p2) ;
		return null;
	}
}
