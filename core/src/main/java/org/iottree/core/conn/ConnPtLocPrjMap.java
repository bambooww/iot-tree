package org.iottree.core.conn;

import java.io.Writer;
import java.util.List;

import org.iottree.core.ConnPt;

public class ConnPtLocPrjMap  extends ConnPtBinder
{
	public static class MapItem
	{
		String prjId ;
		
		String tagInPrj ;
		
		String tagInCh ;
	}
	
	public static class MapDir
	{
		 
	}
	
	String prjId = null ;
	
	
	@Override
	public String getConnType()
	{
		return "loc_prj_map";
	}

	@Override
	public String getStaticTxt()
	{
		return null;
	}

	@Override
	public void RT_checkConn()
	{
		
	}

	@Override
	public boolean isConnReady()
	{
		return true;
	}

	@Override
	public String getConnErrInfo()
	{
		return null;
	}

	@Override
	public void RT_writeValByBind(String tagpath, String strv)
	{
	}

	@Override
	public void clearBindBeSelectedCache()
	{
	}

	@Override
	public List<BindItem> getBindBeSelectedItems() throws Exception
	{
		return null;
	}

	@Override
	public void writeBindBeSelectedTreeJson(Writer w, boolean list_tags_only, boolean force_refresh) throws Exception
	{
	}
}
