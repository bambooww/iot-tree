package org.iottree.core.msgnet.store;

import java.util.ArrayList;
import java.util.List;

public class TagsVals
{
	public static class Val
	{
	}
	
	public static class Row
	{
		long ts ;
		
		List<Object> vals ;
		
		public long getTimestamp()
		{
			return ts ;
		}
		
		public Object getValIdx(int colidx)
		{
			if(vals==null)
				return null ;
			
			if(colidx<vals.size())
				return vals.get(colidx) ;
			
			return null ;
		}
	}
	
	List<String> tagPaths ;
	
	ArrayList<Row> rows = new ArrayList<>();
	
	
	public TagsVals(List<String> tagps)
	{
		this.tagPaths = tagps ;
	}

	public void setRow(long st,List<Val> vs)
	{
		
	}
}
