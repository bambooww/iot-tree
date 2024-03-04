package org.iottree.core.store.ttsr;

import java.util.HashMap;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.StoreManager;
import org.iottree.core.store.gdb.connpool.DBConnPool;

public class RecAdapterPrj extends RecAdapter
{
	private static final HashMap<String,RecAdapterPrj> name2recm = new HashMap<>() ;
	
	public static RecAdapter getInstance(UAPrj prj)
	{
		String name = prj.getName();
		RecAdapterPrj recm = name2recm.get(name) ;
		if(recm!=null)
			return recm ;
		
		synchronized(RecAdapter.class)
		{
			recm = name2recm.get(name) ;
			if(recm!=null)
				return recm ;
			
			recm = new RecAdapterPrj(name) ;
			name2recm.put(name,recm) ;
			return recm ;
		}
	}
	
	String prjName = null ;
	
	UAPrj prj = null ;
	
	RecIO recIO = null ;
	
	protected RecAdapterPrj(String prjname)
	{
		this.prjName = prjname ;
	}

	@Override
	protected boolean RT_init(StringBuilder failedr)
	{
		this.prj = UAManager.getInstance().getPrjByName(prjName) ;
		getIO() ;
		return this.prj!=null;
	}
	
	@Override
	protected RecIO getIO()
	{
		if(recIO!=null)
			return recIO ;
		
		synchronized(this)
		{
			if(recIO!=null)
				return recIO ;
			
			SourceJDBC innersor = StoreManager.getInnerSource(prjName+".ttsr") ;
			DBConnPool cp= innersor.getConnPool() ;
			
			recIO = new RecIOSQLite() ;
			StringBuilder failedr = new StringBuilder() ;
			if(!recIO.initIO(cp,failedr))
				throw new RuntimeException(failedr.toString()) ;
			
			return recIO;
		}
	}
}
