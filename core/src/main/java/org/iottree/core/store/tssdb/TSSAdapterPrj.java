package org.iottree.core.store.tssdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.StoreManager;
import org.iottree.core.store.gdb.connpool.DBConnPool;

public class TSSAdapterPrj extends TSSAdapter
{
	private static final HashMap<String,TSSAdapterPrj> name2recm = new HashMap<>() ;
	
	public static TSSAdapter getInstance(UAPrj prj)
	{
		String name = prj.getName();
		TSSAdapterPrj recm = name2recm.get(name) ;
		if(recm!=null)
			return recm ;
		
		synchronized(TSSAdapter.class)
		{
			recm = name2recm.get(name) ;
			if(recm!=null)
				return recm ;
			
			recm = new TSSAdapterPrj(name) ;
			name2recm.put(name,recm) ;
			return recm ;
		}
	}
	
	String prjName = null ;
	
	UAPrj prj = null ;
	
	TSSIO recIO = null ;
	
	protected TSSAdapterPrj(String prjname)
	{
		this.prjName = prjname ;
		
		this.prj = UAManager.getInstance().getPrjByName(prjName) ;
		
		this.asTagParams(createTagParams()) ;
	}
	
	private List<TSSTagParam> createTagParams()
	{
		ArrayList<TSSTagParam> pms = new ArrayList<>() ;
		
		return pms ;
	}
	
	@Override
	protected long getSaveIntervalMS() 
	{
		return 100 ;
	}

	@Override
	protected boolean RT_init(StringBuilder failedr)
	{
		
		getIO() ;
		
		if(!super.RT_init(failedr))
			return false;
		
		return this.prj!=null;
	}
	
	@Override
	protected TSSIO getIO()
	{
		if(recIO!=null)
			return recIO ;
		
		synchronized(this)
		{
			if(recIO!=null)
				return recIO ;
			
			SourceJDBC innersor = StoreManager.getInnerSource(prjName+".ttsr") ;
			DBConnPool cp= innersor.getConnPool() ;
			
			recIO = new TSSIOSQLite() ;
			StringBuilder failedr = new StringBuilder() ;
			if(!recIO.initIO(cp,failedr))
				throw new RuntimeException(failedr.toString()) ;
			
			return recIO;
		}
	}
}
