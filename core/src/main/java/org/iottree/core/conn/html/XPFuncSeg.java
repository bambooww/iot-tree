package org.iottree.core.conn.html;

import java.util.ArrayList;
import java.util.List;

/**
 * extract numbers in string
 * it may has one or more
 * @author jason.zhu
 *
 */
public class XPFuncSeg extends XPFunc
{
	int idx ;
	
	public boolean setupParams(List<String> params)
	{
		if(params==null||params.size()!=1)
			return false;
		
		idx = Integer.parseInt(params.get(0))-1 ;
		
		return true;
	}
	
	@Override
	public String runFunc(String input)
	{
		List<String> ss = splitSegs(input,true) ;
		if(ss==null||ss.size()<=idx)
			return null ;
		return ss.get(idx);
	}
	
	public static List<String> splitSegs(String str,boolean ret_num_only)
	{
		if(str==null)
			return null ;
		int n = str.length() ;
		ArrayList<String> rets = new ArrayList<>() ;
		String curs = "" ;
		int st = 0 ;
		for(int i = 0 ; i < n ; i ++)
		{
			char c = str.charAt(i) ;

			switch(st)
			{
			case 0://normal 
				if(c>='0' && c<='9')
				{
					if(!ret_num_only && curs.length()>0)
						rets.add(curs) ;
					curs = ""+c ;
					st = 1 ;
				}
				else
				{
					curs += c ;
				}
				break ;
			case 1://num
				if(c>=0 && c<='9' || c=='.')
					curs += c ;
				else
				{
					if(curs.length()>0)
						rets.add(curs) ;
					curs = ""+c ;
					st = 0 ;
				}
				break ;
			}
			
		}
		
		if(curs.length()>0)
		{
			if(st==1 || !ret_num_only)
				rets.add(curs) ;
		}
		return rets ;
	}
}
