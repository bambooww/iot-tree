 package org.iottree.core.store;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.basic.ce.ExchgModule;
import org.iottree.core.basic.ce.ExchgObj;
import org.iottree.core.basic.ce.ExchgModuleAdp;

public class StoreExchgModuleAdp extends ExchgModuleAdp
{

	@Override
	public String getExchgModuleName()
	{
		return "store";
	}

	@Override
	public String getExchgModuleTitle()
	{
		return "Store";
	}

	@Override
	public List<ExchgObj> SOR_provideExchgObjs()
	{
		List<Source> sors = StoreManager.listSources() ;
		ArrayList<ExchgObj> rets = new ArrayList<>() ;
		rets.addAll(sors) ;
		return rets;
	}
	
	

	@Override
	public ExchgObj createExchgObjByTP(String tp)
	{
		switch(tp)
		{
		case SourceJDBC.EXCHG_TP:
			return new SourceJDBC() ;
		case SourceInfluxDB.EXCHG_TP:
			return new SourceInfluxDB() ;
		default:
			break ;
		}
		return null;
	}

}
